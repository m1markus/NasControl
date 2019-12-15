
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
hdiutil create -format UDRW  -size 5m -srcfolder ${PROJECT}.app $OUTPUT_DMG_FILE
#hdiutil internet-enable -yes $OUTPUT_DMG_FILE

# create the /Application link in the dmg file
#
open $OUTPUT_DMG_FILE
sleep 3
cd /Volumes/${PROJECT}
ln -s /Applications Applications
cd /
umount /Volumes/${PROJECT}
