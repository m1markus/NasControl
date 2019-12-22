#!/bin/sh

JAR_FILE=nascontrol.jar
PROJECT=NASControl
TARGET_JAR_DIR=./${PROJECT}.app/Contents/MacOS
OUTPUT_DMG_FILE=${PROJECT}.dmg

cp ../target/$JAR_FILE $TARGET_JAR_DIR

# delete the old dmg file
if [ -f $OUTPUT_DMG_FILE ]; then
  rm $OUTPUT_DMG_FILE
fi

# make it read/write: -format UDRW
hdiutil create -format UDRW  -size 20m -srcfolder ${PROJECT}.app $OUTPUT_DMG_FILE
#hdiutil internet-enable -yes $OUTPUT_DMG_FILE

# create the /Application link in the dmg file
#
open $OUTPUT_DMG_FILE
sleep 3
pushd `pwd` >/dev/null 2>&1
cd /Volumes/${PROJECT}
ln -s /Applications Applications
cd /
popd >/dev/null 2>&1
umount /Volumes/${PROJECT}

if [ -f $TARGET_JAR_DIR/$JAR_FILE ]; then
  echo "delete... file: $TARGET_JAR_DIR/$JAR_FILE"
  rm $TARGET_JAR_DIR/$JAR_FILE
fi
