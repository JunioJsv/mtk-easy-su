# Mtk Easy Su
- This app set up bootless root access with [Magisk](https://github.com/topjohnwu/Magisk) and Mtk-su (by [Diplomatic](https://forum.xda-developers.com/member.php?u=8132642)@Xda) on MediaTek Android devices.
- Download the latest version [here](https://github.com/JunioJsv/mediatek-easy-root/releases/latest)

### Notes :memo:
- (:warning:__Required__) To manage root access for each application, you must download [Magisk manager](https://github.com/topjohnwu/Magisk/releases/tag/manager-v7.1.1) (__v7.1.1__)
- (:warning:__Required__) Before installing the app make sure that **google play protect is disabled**, in recent weeks the play store has marked the app as harmful for no reason.
- To check if you have been granted root access, check the value returned by the log, when it is 0 is because it worked and you have root access, the log below is of a mediatek that this app was successful.
```sh
param1: 0x1000, param2: 0x8040, type: 4
Building symbol table
kallsyms_addresses pa 0x40e81240
kallsyms_num_syms 81314, addr_count 81314
kallsyms_names pa 0x40ed08e0, size 1013690
kallsyms_markers pa 0x40fc80a0
kallsyms_token_table pa 0x40fc85a0
kallsyms_token_index pa 0x40fc8950
Patching credentials
Parsing current_is_single_threaded
c0478518: MOVW R0, #0x8d60
c047851c: MOVT R0, #0xc162
init_task VA: 0xc1628d60
Potential list_head tasks at offset 0x2b0
comm swapper/0 at offset 0x430
Found own task_struct at node 1
cred VA: 0xcdf01480
Parsing avc_denied
c0cdea6c: MOVW R12, #0x77e4
c0cdea70: MOVT R12, #0xc190
selinux_enforcing VA: 0xc19077e4
Setting selinux_enforcing
Switched selinux to permissivearmv7l machine
starting /system/bin/sh
UID: 0  cap: 3fffffffff  selinux: permissive  
returned 0
```
> _If for some reason the script fails on the first attempt, try again. The second attempt is sometimes successful._

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

### Acknowledgments :handshake:
- This app was based on [this](https://forum.xda-developers.com/showpost.php?p=79626434&postcount=135) tutorial.
- Thanks to [Diplomatic](https://forum.xda-developers.com/member.php?u=8132642) for writing the script that makes this app possible.
- Thanks to everyone on the LG K10 XDA Forum.
- Thanks to John Wu for Magisk Manager. [@topjohnwu](https://twitter.com/topjohnwu)

### Warning :warning:
    WARNING: DO NOT UPDATE MAGISK THROUGH MAGISK MANAGER ON A LOCKED BOOTLOADER OR YOU WILL BRICK YOUR DEVICE.
<p align=center>I'm not responsible for anything '_'</p>
