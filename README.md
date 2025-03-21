# Mtk Easy Su

[![](https://img.shields.io/github/downloads/JunioJsv/mtk-easy-su/total.svg)](https://github.com/JunioJsv/mtk-easy-su/releases/)
[![](https://img.shields.io/badge/maintained-maybe-yellow.svg)](https://github.com/JunioJsv/mtk-easy-su)

Welcome to Mtk Easy Su, an application designed to set up bootless super user access using [Magisk](https://github.com/topjohnwu/Magisk) and Mtk-su on MediaTek Android devices. This tool is particularly useful for those interested in leveraging the security breach Mtk-su by [Diplomatic](https://forum.xda-developers.com/member.php?u=8132642).

## Features

- Easily establish bootless super user access on your MediaTek Android device.
- Seamlessly integrates Magisk and Mtk-su for enhanced functionality.
- Empowers advanced users with greater control over the rooting process through terminal-based [mtk-su](https://forum.xda-developers.com/t/amazing-temp-root-for-mediatek-armv8-2020-08-24.3922213/).

## Getting Started

To get started with Mtk Easy Su, follow these simple steps:

1. Download the latest version [here](https://github.com/JunioJsv/mediatek-easy-root/releases), but please be aware that you are doing so at your own risk.

2. Ensure you have [Magisk Manager](https://github.com/topjohnwu/Magisk/releases/tag/manager-v8.0.4) installed to manage root access for each application.

3. Before installing the app, disable **Google Play Protect** as recent updates have flagged the app as harmful without valid reasons.

## Important Notes

- Stay informed about the critical MediaTek rootkit affecting millions of Android devices. Check out the XDA article [here](https://www.xda-developers.com/mediatek-su-rootkit-exploit/).

- Be cautious with firmware updates released after March 2020, as they may block the methods utilized by Mtk Easy Su or mtk-su.

- To confirm if you've been granted super user access, check the exit value returned by the log. A value of 0 indicates success and root access.

```sh
/data/data/juniojsv.mtk.easy.su/files
UID: 0  cap: 3fffffffff  selinux: permissive  
Load policy from: /sys/fs/selinux/policy
20.4:MAGISK (20400)
client: launching new main daemon process
Exit value 0
```

## Tested devices
| Device                  |     Model      |       Chipset        | Result  |
|-------------------------|:--------------:|:--------------------:|:-------:|
| Lg X Power2             |     M320N      |   MediaTek MT6750    | Success |
| Lg K10                  |     M250DS     |   MediaTek MT6750    | Success |
| Lg K10 Power            |     M320TV     |   MediaTek MT6750    | Success |
| Lg K10 TV               |    K430DSF     |   MediaTek MT6753    | Success |
| Lg K8                   |      K350      |   Mediatek MT6735    |  Fail   |
| Lg K4                   |     X230DS     |   MediaTek MT6737M   | Success |
| Motorola Moto C         |     XT1756     |   Mediatek MT6737M   | Success |
| Motorola Moto E4        |     XT1773     |   Mediatek MT6737    | Success |
| Alcatel A3 LX           |     9008X      |   Mediatek MT8735B   | Success |
| Alcatel 1               |     5033T      |   Mediatek MT6739    | Success |
| Alcatel U5 3G           |     4047A      |   Mediatek MT6580M   |  Fail   |
| Blu Studio X8 HD        |      S532      |   Mediatek MT6580    |  Fail   |
| Nook Tablet 10.1        |    BNTV650     | Mediatek A35-MT8167A | Success |
| ZTE Blade A7 Prime      |      OEM       | Mediatek A22-MT6761  | Success |
| Oppo F7                 |    CPH1821     |   Mediatek MT6771    |  Fail   |
| Oppo F3                 |    CPH1609     |   Mediatek MT6750T   |  Fail   |
| Lenovo Vibe K5 Note     |    A7020a48    |   Mediatek MT6755    |  Fail   |
| Lenovo Tab E8           |   TB-8304F1    |   Mediatek MT8163    | Success |
| Vernee Mix 2            |     Mix 2      |  Mediatek MT6757CD   | Success |
| Nokia 1                 |    TA-1130     |  Mediatek MT6739WW   |  Fail   |
| Sony Xperia C5 Ultra    |     E5553      |   Mediatek MT6752    | Success |
| Redmi 6                 |     Cerus      |   Mediatek MT6762    | Success |
| Ark Elf S8              |     Elf S8     |   MediaTek MT6580    | Success |
| Sony Xperia L3          |     I4312      |   MediaTek MT6762    | Success |
| GIONEE S10              | GIONEE_SW17G04 |   MediaTek MT6767    | Success |
| GIONEE M7               | GIONEE_SW17G07 |   MediaTek MT6758    | Success |
| Southern Telecom PB1009 |     PB1009     |   MediaTek MT8167B   | Success |
| Meizu MX6               |     MX6        |   MediaTek MT6797    | Success |

## Acknowledgments

- Special thanks to [Diplomatic](https://forum.xda-developers.com/member.php?u=8132642) for developing the mtk-su tool that powers this application.

- Gratitude to all contributors on the LG K10 XDA Forum for their valuable insights and support.

- Shoutout to John Wu for his invaluable contribution to the rooting community with Magisk. [@topjohnwu](https://twitter.com/topjohnwu)

## Warning
DO NOT UPDATE MAGISK THROUGH MAGISK MANAGER ON A LOCKED BOOTLOADER OR YOU WILL BRICK YOUR DEVICE.<br>
Remember, while this tool aims to simplify the rooting process, I'm not responsible for any mishaps that may occur during or after its usage. Proceed with caution! 😊
