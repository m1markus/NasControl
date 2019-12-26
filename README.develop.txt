next:
- about dialog with version and icon ref
- pref dialog
- plugin for different NAS types
- manage mount points?


Icons
=====

icons: https://icons8.com/icons/set/cloud
convert: https://www.zamzar.com/convert/png-to-gif/


https://www.flaticon.com/free-icon/cloud-computing_1252924
credit:

<div>Icons made by
	<a href="https://www.flaticon.com/authors/itim2101" title="itim2101">itim2101</a> from
	<a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a>
</div>

output:

Icons made by itim2101 from www.flaticon.com

https://codebeautify.org/htmlviewer/


https://anyconv.com/icns-converter/


FreeNAS
=======

open http://freenas.local

http://freenas.local/api/v1.0
auth: basic auth

http://freenas.local:80/api/v1.0/system/shutdown/
json
no body

/api/v1.0/system/reboot/
json
no body

/api/v1.0/system/reboot/
json
no body

GET http://freenas.local/api/v1.0/storage/volume/
status

[
   {
      "avail":253383413760,
      "children":[  ],
      "id":1,
      "is_decrypted":true,
      "is_upgraded":true,
      "mountpoint":"/mnt/PoolData01",
      "name":"PoolData01",
      "status":"HEALTHY",

.../stroage/volume

JSON example response:
[{"avail": 253383413760, "children": [{"avail": 245465460736, "children": [{"avail": 245465460736, "children": [{"avail": 245465460736, "id": 102, "mountpoint": "/mnt/PoolData01/iocage/download", "name": "download", "path": "PoolData01/iocage/download", "status": "-", "type": "dataset", "used": 90112, "used_pct": 0}, {"avail": 245465460736, "id": 103, "mountpoint": "/mnt/PoolData01/iocage/images", "name": "images", "path": "PoolData01/iocage/images", "status": "-", "type": "dataset", "used": 90112, "used_pct": 0}, {"avail": 245465460736, "id": 104, "mountpoint": "/mnt/PoolData01/iocage/jails", "name": "jails", "path": "PoolData01/iocage/jails", "status": "-", "type": "dataset", "used": 90112, "used_pct": 0}, {"avail": 245465460736, "id": 105, "mountpoint": "/mnt/PoolData01/iocage/log", "name": "log", "path": "PoolData01/iocage/log", "status": "-", "type": "dataset", "used": 90112, "used_pct": 0}, {"avail": 245465460736, "id": 106, "mountpoint": "/mnt/PoolData01/iocage/releases", "name": "releases", "path": "PoolData01/iocage/releases", "status": "-", "type": "dataset", "used": 90112, "used_pct": 0}, {"avail": 245465460736, "id": 107, "mountpoint": "/mnt/PoolData01/iocage/templates", "name": "templates", "path": "PoolData01/iocage/templates", "status": "-", "type": "dataset", "used": 90112, "used_pct": 0}], "id": 101, "mountpoint": "/mnt/PoolData01/iocage", "name": "iocage", "path": "PoolData01/iocage", "status": "-", "type": "dataset", "used": 4595712, "used_pct": 0}, {"avail": 245465460736, "id": 108, "mountpoint": "/mnt/PoolData01/mue", "name": "mue", "path": "PoolData01/mue", "status": "-", "type": "dataset", "used": 94208, "used_pct": 0}, {"avail": 245465460736, "id": 109, "mountpoint": "/mnt/PoolData01/sam", "name": "sam", "path": "PoolData01/sam", "status": "-", "type": "dataset", "used": 90112, "used_pct": 0}, {"avail": 245465460736, "id": 110, "mountpoint": "/mnt/PoolData01/transfer", "name": "transfer", "path": "PoolData01/transfer", "status": "-", "type": "dataset", "used": 94208, "used_pct": 0}], "id": 100, "mountpoint": "/mnt/PoolData01", "name": "PoolData01", "path": "PoolData01", "status": "-", "type": "dataset", "used": 18604032, "used_pct": 0}], "id": 1, "is_decrypted": true, "is_upgraded": true, "mountpoint": "/mnt/PoolData01", "name": "PoolData01", "status": "HEALTHY", "used": 19656704, "used_pct": "0%", "vol_encrypt": 0, "vol_encryptkey": "", "vol_guid": "15152716564634517844", "vol_name": "PoolData01"}, {"avail": 1992863875072, "children": [{"avail": 1930586849280, "id": 111, "mountpoint": "/mnt/PoolData02", "name": "PoolData02", "path": "PoolData02", "status": "-", "type": "dataset", "used": 839680, "used_pct": 0}], "id": 2, "is_decrypted": true, "is_upgraded": true, "mountpoint": "/mnt/PoolData02", "name": "PoolData02", "status": "HEALTHY", "used": 950272, "used_pct": "0%", "vol_encrypt": 0, "vol_encryptkey": "", "vol_guid": "18044241570246150092", "vol_name": "PoolData02"}]


Mac packaging
=============
https://www.eclipse.org/swt/macosx/
http://www.theinstructional.com/guides/disk-management-from-the-command-line-part-3
https://lerks.blog/packaging-a-jar-as-app-for-macos/
https://developer.apple.com/library/archive/documentation/Java/Conceptual/Java14Development/03-JavaDeployment/JavaDeployment.html
https://github.com/libgdx/packr
https://github.com/Jorl17/jar2app
https://centerkey.com/mac/java/


network address:
https://stackoverflow.com/questions/1221517/how-to-get-subnet-mask-of-local-system-using-java

cli:
https://commons.apache.org/proper/commons-cli/usage.html

icon in dock:
https://stackoverflow.com/questions/6006173/how-do-you-change-the-dock-icon-of-a-java-program

do not show in dock mac os:
https://www.cnet.com/news/prevent-an-applications-dock-icons-from-showing-in-os-x/


# console output is hidden !!!
open -a /Applications/NASControl.app --args -v

# with output on the console
/Applications/NASControl.app/Contents/MacOS/nascontrol -v

