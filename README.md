# dfu_java
stm32 java DFU implementation (DfuSe)

This java code can upload a .hex or a .dfu file into stm32f47.

On Windows that means using a libusb-compatible driver like the one included in windows_driver folder.


ST AN3156 Application note
USB DFU protocol used in the STM32 bootloader

# Windows drama

https://github.com/libusb/libusb/wiki/Windows#How_to_use_libusb_on_Windows

Zadig is a Windows application that installs generic USB drivers, such as WinUSB - https://zadig.akeo.ie/

Uses precompiled https://github.com/j123b567/java-intelhex-parser

This implementation would not happen without http://dfu-util.sourceforge.net/dfuse.html and https://github.com/kairyu/flop

https://github.com/UmbrelaSmart/android-stm32-dfu-programmer is also nice