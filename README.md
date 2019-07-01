# MediaTek Easy Root
- Get bootless root access with one click
- Download the latest version [here](https://github.com/JunioJsv/mediatek-easy-root/releases/latest)
- [Buy a snack for me :hamburger:](https://www.mercadopago.com/mlb/checkout/start?pref_id=365594257-359f7b8e-cc7c-4ff2-8fd1-4fc73eb6de50)

### Notes :memo:
- If for some reason the script fails on the first attempt, try again. The second attempt is sometimes successful.
- To check if you have been granted root access, try these commands in a terminal emulator:
```sh
    $ su
    # Tries to switch to root user
    root@[device]:/*folder location*
    # If you see something like this, you have root access.
````
- (Required) To manage root access for each application, you must download Magisk manager [here](https://magiskmanager.com/)

### Tested devices :heavy_check_mark:
|      Device     |  Model  |      Chipset     |  Result |
|-----------------|:-------:|:----------------:|:-------:|
| Lg K10          |  M250DS |  MediaTek MT6750 | Success |
| Lg K10 Power    |  M320TV |  MediaTek MT6750 | Success |
| Lg K10 TV       | K430DSF |  MediaTek MT6753 | Success |
| Lg K4           |  X230DS | MediaTek MT6737M | Success |
| Motorola Moto C |  XT1756 | Mediatek MT6737M | Success |
| Motorola Moto E4|  XT1773 |  Mediatek MT6737 | Success |
| Alcatel A3 LX   |  9008X  | Mediatek MT8735B | Success |
| Lg K8           |   K350  |  Mediatek MT6735 |   Fail  |

### Acknowledgments :handshake:
- This app was based on [this](https://forum.xda-developers.com/showpost.php?p=79626434&postcount=135) tutorial.
- Thanks to [Diplomatic](https://forum.xda-developers.com/member.php?u=8132642) for writing the script that makes this app possible.
- Thanks to everyone on the LG K10 XDA Forum.
- Thanks to John Wu for Magisk Manager. [@topjohnwu](https://twitter.com/topjohnwu)

### Warning :warning:
    WARNING: DO NOT UPDATE MAGISK THROUGH MAGISK MANAGER ON A LOCKED BOOTLOADER OR YOU WILL BRICK YOUR DEVICE.
