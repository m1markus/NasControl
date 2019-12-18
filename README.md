# NASControl
Send a WoL (wake on lan) packet from your mac or pc to your NAS (network attached storage) device.

It integrates into the SystemTray with a tiny icon. You have to build it from source yourself. It's written in Java.

## Prerequisite
- Maven 3.6.2 or higher
- JDK 1.8 or higher

## Build

```
mvn clean package
```

## Run (for testing with log on the console)
```
java -jar ./target/nascontrol.jar
```

## Create .dmg image for MacOS
```
(cd pkg;./package.sh)
```

## Installation:
Create a config file in your home directory.
```
file: /Users/yourUserId/nascontrol.conf
```
```
network.broadcast_address = 192.168.1.255
nas.mac_address = FF:A9:C9:D1:38:1F
nas.adminui_url = http://myfreenas.local
```
