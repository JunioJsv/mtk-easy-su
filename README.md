# Mtk Easy Su
[![](https://img.shields.io/github/downloads/JunioJsv/mtk-easy-su/total.svg)](https://github.com/JunioJsv/mtk-easy-su/releases/) [![](https://img.shields.io/badge/maintained-yes-yellow.svg)](https://github.com/JunioJsv/mtk-easy-su)

- This app set up bootless super user access, with [Magisk](https://github.com/topjohnwu/Magisk) and Mtk-su, on MediaTek Android devices. To facilitate those wishing to use the security breach Mtk-su by [Diplomatic](https://forum.xda-developers.com/member.php?u=8132642).
- Download the latest version [here](https://github.com/JunioJsv/mediatek-easy-root/releases), at your own risk.

### Notes :memo:
- (:warning:__REQUIRED__) To manage root access for each application, you must download [Magisk manager](https://github.com/topjohnwu/Magisk/releases/tag/manager-v7.1.1) (__v7.1.1__).
- (:warning:__REQUIRED__) Before installing the app make sure that **google play protect is disabled**, in recent weeks the play store has marked the app as harmful for no reason.
- (:warning:__READ THIS__) XDA - [Critical MediaTek rootkit affecting millions of Android devices has been out in the open for months](https://www.xda-developers.com/mediatek-su-rootkit-exploit/).
- (:warning:__WARNING__) Any firmware update released after March, 2020 is bound to block the method used by mtk-easy-su. Think twice before updating your device if you would like to keep using mtk-easy-su or mtk-su.
- To check if you have been granted super user access, check the exit value returned by the log, when it is 0 is because it worked and you have root access, the log below is of a mediatek device (LG K10 2017) that this app was successful.
```sh
...
/data/data/juniojsv.mtk.easy.su/files
UID: 0  cap: 3fffffffff  selinux: permissive  
Load policy from: /sys/fs/selinux/policy
20.4:MAGISK (20400)
client: launching new main daemon process
Exit value 0
```

### Tested devices :heavy_check_mark:
|      Device     |  Model  |      Chipset     |  Result |
|-----------------|:-------:|:----------------:|:-------:|
| Lg K10          |  M250DS |  MediaTek MT6750 | Success |
| Lg K10 Power    |  M320TV |  MediaTek MT6750 | Success |
| Lg K10 TV       | K430DSF |  MediaTek MT6753 | Success |
| Lg K8           |   K350  |  Mediatek MT6735 |   Fail  |
| Lg K4           |  X230DS | MediaTek MT6737M | Success |
| Motorola Moto C |  XT1756 | Mediatek MT6737M | Success |
| Motorola Moto E4|  XT1773 |  Mediatek MT6737 | Success |
| Alcatel A3 LX   |  9008X  | Mediatek MT8735B | Success |
| Alcatel 1       |  5033T  |  Mediatek MT6739 | Success |
| Alcatel U5 3G   |  4047A  | Mediatek MT6580M |   Fail  |
| Blu Studio X8 HD|   S532  |  Mediatek MT6580 |   Fail  |
| Nook Tablet 10.1|BNTV650|Mediatek A35-MT8167A| Success |
|ZTE Blade A7 Prime|  OEM  |Mediatek A22-MT6761| Success |
| Oppo F7         | CPH1821 |  Mediatek MT6771 |   Fail  |
| Oppo F3         | CPH1609 | Mediatek MT6750T |   Fail  |
|Lenovo Vibe K5 Note|A7020a48|Mediatek MT6755  |   Fail  |
| Vernee Mix 2    |  Mix 2  | Mediatek MT6757CD| Success |
| Nokia 1         | TA-1130 | Mediatek MT6739WW|   Fail  |

### Acknowledgments :handshake:
- This app was based on [this](https://forum.xda-developers.com/android/development/amazing-temp-root-mediatek-armv8-t3922213/post82081703#post82081703) tutorial.
- Thanks to [Diplomatic](https://forum.xda-developers.com/member.php?u=8132642) for writing the mtk-su that makes this app possible.
- Thanks to everyone on the LG K10 XDA Forum.
- Thanks to John Wu for Magisk. [@topjohnwu](https://twitter.com/topjohnwu)

### Warning :warning:
    WARNING: DO NOT UPDATE MAGISK THROUGH MAGISK MANAGER ON A LOCKED BOOTLOADER OR YOU WILL BRICK YOUR DEVICE.
<p align=center>I'm not responsible for anything '_'</p>
