#!/system/bin/sh
#
# Updated: Out 10, 2019
# by diplomatic and JunioJsv
#
# WARNING: DO NOT UPDATE MAGISK THROUGH MAGISK MANAGER OR YOU WILL BRICK YOUR
#          DEVICE ON A LOCKED BOOTLOADER
#

HOMEDIR=$1

SU_MINISCRIPT='
# Magisk function to find boot partition and prevent the installer from finding
# it again
toupper() {
  echo "$@" | tr "[:lower:]" "[:upper:]"
}

grep_prop() {
  local REGEX="s/^$1=//p"
  shift
  local FILES=$@
  sed -n "$REGEX" $FILES 2>/dev/null | head -n 1
}

find_block() {
  for BLOCK in "$@"; do
    DEVICES=`find /dev/block -type l -iname $BLOCK` 2>/dev/null
    for DEVICE in $DEVICES; do
      if [ -h "$DEVICE" ]; then
	    cd ${DEVICE%/*}
	    BASENAME="${DEVICE##*/}"
	    mv "$BASENAME" ".$BASENAME"
	    cd -
	  fi
	done
  done
  # Fallback by parsing sysfs uevents
  for uevent in /sys/dev/block/*/uevent; do
    local PARTNAME=`grep_prop PARTNAME $uevent`
    for BLOCK in "$@"; do
      if [ "`toupper $BLOCK`" = "`toupper $PARTNAME`" ]; then
        #echo /dev/block/$DEVNAME
        #return 0
        chmod 0 $uevent
      fi
    done
  done
  return 1
}

# Root only at this point; hoping selinux is permissive
if [ $(id -u) != 0 ] || [ "$(getenforce)" != "Permissive" ]; then
	echo "Root user only" >&2
	exit 1
fi

cd $HOMEDIR

# Patch selinux policy -- error messages here are normal
./magiskpolicy --live --magisk "allow magisk * * *"
if [ ! -f /sbin/.init-stamp ]; then
	# Create tmpfs /xbin overlay
	./magisk --startup

	if [ ! -f /sbin/magiskinit ] || [ ! -f /sbin/magisk.bin ]; then
		echo "Bad /sbin mount?" >&2
		setenforce 1
		exit 1
	fi

	# Copy binaries
	cp magiskinit /sbin/

	export PATH=/sbin:$PATH
	magiskinit -x magisk /sbin/magisk.bin

	# Finish startup calls
	magisk --post-fs-data
	magisk --service
	magisk --boot-complete

	touch /sbin/.init-stamp
fi

# Disaster prevention
SLOT=$(getprop ro.boot.slot_suffix)
find_block boot$SLOT

setenforce 1
'

mkdir -p $HOMEDIR
cd $HOMEDIR || exit 1

./magiskinit -x magisk ./magisk || exit 1
chmod 755 magisk
ln -fs magiskinit magiskpolicy

# start SU daemon
export HOMEDIR
echo "$SU_MINISCRIPT" | ./mtk-su -v
