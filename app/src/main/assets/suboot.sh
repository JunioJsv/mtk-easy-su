#!/system/bin/sh
#
# Updated: Jun 04, 2019
# by diplomatic and JunioJsv
#
# This script sets up bootless root with Magisk on MediaTek Android devices.
# It uses mtk-su, the temporary root tool for MediaTek ARMv8 chips. Currently
# this only supports Magisk up to 18.1. Must be run from the app 
# 'init.d scripts support' by RYO Software. Put this file into
# /storage/emulated/0/init.d along with mtk-su and magiskinit into .../init.d/bin
# Point the app to run sh scripts from /storage/emulated/0/init.d at boot time.
#
# WARNING: DO NOT UPDATE MAGISK THROUGH MAGISK MANAGER OR YOU WILL BRICK YOUR
#          DEVICE ON A LOCKED BOOTLOADER
#

HOMEDIR=/data/data/juniojsv.mediatekeasyroot/files

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
  [ -z "$FILES" ] && FILES="/system/build.prop"
  sed -n "$REGEX" $FILES 2>/dev/null | head -n 1
}

find_block() {
  for BLOCK in "$@"; do
    DEVICE=`find /dev/block -type l -iname $BLOCK | head -n 1` 2>/dev/null
    if [ ! -z $DEVICE ]; then
      #readlink -f $DEVICE
      #return 0
      cd $(dirname $DEVICE)
      BASENAME="$(basename $DEVICE)"
      mv "$BASENAME" ".$BASENAME"
      cd -
    fi
  done
  # Fallback by parsing sysfs uevents
  for uevent in /sys/dev/block/*/uevent; do
    local DEVNAME=`grep_prop DEVNAME $uevent`
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
# Create tmpfs /xbin tree
if [ ! -f /sbin/.init-stamp ]; then
	./magisk --startup

	if [ ! -f /sbin/magiskinit ] || [ ! -f /sbin/magisk.bin ]; then
		echo "Bad /sbin mount?" >&2
		exit 1
	fi

	touch /sbin/.init-stamp
fi

# Copy binaries
cp magiskinit /sbin/

export PATH=/sbin:$PATH
magiskinit -x magisk /sbin/magisk.bin
# Finish startup calls
magisk --post-fs-data
magisk --service
magisk --boot-complete

# Disaster prevention
find_block boot boot_a kernel

setenforce 1
'

mkdir -p ${HOMEDIR}
cd ${HOMEDIR} || exit 1

# chmod 700 magiskinit
./magiskinit -x magisk ./magisk || exit 1

# chmod 700 magisk
ln -fs magiskinit magiskpolicy

# chmod 700 mtk-su

# start SU daemon
export HOMEDIR
echo "$SU_MINISCRIPT" | ./mtk-su
