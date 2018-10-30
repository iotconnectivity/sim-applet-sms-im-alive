# sim-applet-sms-im-alive
JavaCard SIM card applet to deliver an SMS very the first time the SIM registers in the GSM/GPRS network


## Table of Contents

- [Description](#description)
- [To Do](#todo)
- [Installation and usage](#installation)
- [Contributing and license](#contributing)
- [Standards](#standards)

## Description

This version sends a sms with location information (MCC MNC) to an example destination address. You should trigger the applet by selecting the SKT menu. You should code the desired destination address following the instructions in the source code.


## ToDo

- Use the event "Download location status" send the sms only the first time the sim registers in a network.
- Add more information to the sms. For example, IMEI of the device.
- Asign an AID

## Installation

To install the applet you need:
- sysmoUSIM-SJS1 SIM + USIM Card with ADM1 key (find it here: http://shop.sysmocom.de/products/sysmousim-sjs1).
- a card reader.
- The scripts for installation are in the osmocom repository (http://git.osmocom.org/sim/sim-tools/) and you have a guide (https://osmocom.org/projects/sim-toolkit/wiki)


## Contributing

Please contribute using [Github Flow](https://guides.github.com/introduction/flow/). Create a branch, add commits, and [open a pull request](https://github.com/fraction/readme-boilerplate/compare/).

Please note this source code has been released under the GPLv3 terms and all contributions will be considered. Have a look at the LICENSE file distributed with this code.


## Standards

[3GPP TS 23.038 Alphabets and language-specific information](https://portal.3gpp.org/desktopmodules/Specifications/SpecificationDetails.aspx?specificationId=745)

[ETSI TS 102 223 Card Application Toolkit (CAT) ](https://www.etsi.org/deliver/etsi_ts/102200_102299/102223/14.00.00_60/ts_102223v140000p.pdf)


