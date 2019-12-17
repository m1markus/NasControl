# NASControl
Send a WoL (wake on lan) packet to your NAS (network attached storage) device from your mac or pc

It integrates into the SystemTray with a tiny icon. You have to build from source yourself.
Its written in Java and Maven.

## prerequisite
Maven 3.6.2 or higher
JDK 1.8

###build
mvn clean package
###run
java -jar ./target/nascontrol.jar

###create mac .dmg image
(cd pkg;./package.sh)

##installation:
Create a config file in your home directory.

file: /Users/yourUserId/nascontrol.conf

network.broadcast_address = 192.168.1.255
nas.mac_address = 70:85:C2:DD:18:6F
nas.adminui_url = http://freenas.local
