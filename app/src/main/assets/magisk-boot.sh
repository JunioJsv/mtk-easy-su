#!/system/bin/sh
HOMEDIR="$(dirname $0)/$1"
INTLROOT=".magisk"

SU_MINISCRIPT='
find_block() {
  for BLOCK in "$@"; do
    DEVICES=$(find /dev/block -type l -iname "$BLOCK" 2>/dev/null)
    for DEVICE in $DEVICES; do
      DIRNAME=$(dirname "$DEVICE")
      BASENAME=$(basename "$DEVICE")
      cd "$DIRNAME"
      mv "$BASENAME" ".$BASENAME"
      cd -
    done
  done
  FILELIST=$(grep -s PARTNAME= /sys/dev/block/*/uevent 2>/dev/null)
  for uevent in $FILELIST; do
    PARTNAME=$(echo "$uevent" | sed "s/.*PARTNAME=//" | tr "[:upper:]" "[:lower:]")
    for BLOCK in "$@"; do
      LBLOCK=$(echo "$BLOCK" | tr "[:upper:]" "[:lower:]")
      if [ "$LBLOCK" = "$PARTNAME" ]; then
        FNAME=$(echo "$uevent" | sed "s/:.*//")
        chmod 0 "$FNAME"
      fi
    done
  done
  return 0
}

if [ "$(id -u)" != "0" ] || [ "$(getenforce)" != "Permissive" ]; then
  echo "Root user only" >&2
  exit 1
fi

SLOT=$(getprop ro.boot.slot_suffix)
find_block "boot$SLOT"

cd "$HOMEDIR" || { setenforce 1; exit 1; }

TMPROOT=/sbin
if ! mount | grep -q " $TMPROOT "; then
  mount -t tmpfs -o mode=755 magisk "$TMPROOT" 2>/dev/null
fi
mkdir -p "$TMPROOT/$INTLROOT"
chmod 700 "$TMPROOT/$INTLROOT"
echo "RECOVERYMODE=false" > "$TMPROOT/$INTLROOT/config"

cp -f magiskinit "$TMPROOT/magisk"
cp -f magiskpolicy "$TMPROOT/magiskpolicy"
chmod 755 "$TMPROOT/magisk" "$TMPROOT/magiskpolicy"
chcon u:object_r:magisk_file:s0 "$TMPROOT/magisk" "$TMPROOT/magiskpolicy" 2>/dev/null

ln -sf magisk "$TMPROOT/su"
chcon u:object_r:magisk_file:s0 "$TMPROOT/su" 2>/dev/null

"$TMPROOT/magiskpolicy" --live --magisk "allow magisk * * *"

export MAGISKTMP="$TMPROOT"
export PATH="$TMPROOT:$PATH"

nohup "$TMPROOT/magisk" --daemon >/dev/null 2>&1 &
sleep 1

"$TMPROOT/magisk" --post-fs-data
sleep 1
"$TMPROOT/magisk" --service
"$TMPROOT/magisk" --boot-complete

setenforce 1
'

cd "$HOMEDIR" || exit 1

ctx=$(cat /proc/$$/attr/current)
newctx=$(echo "$ctx" | sed "s/:s0:.*$/:s0/")

export HOMEDIR
echo "$SU_MINISCRIPT" | "./mtk-su" -v -Z $newctx
