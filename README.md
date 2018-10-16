# sim-applet-sms-im-alive
JavaCard SIM card applet to deliver an SMS very the first time the SIM registers in the GSM/GPRS network


## Table of Contents

- [Description](#description)
- [To Do](#todo)
- [Installation and usage](#installation)
- [Contributing and license](#contributing)
- [Standards](#standards)

## Description

GSM SIM cards can realize about the status of network regitration (see ["Location Status" ETSII TS 102.223](https://www.etsi.org/deliver/etsi_ts/102200_102299/102223/12.01.00_60/ts_102223v120100p.pdf). What a SIM cannot realize is whether the data connection has been correctly acquired or not). This projects aim to deliver a "hello-im-alive" heart-beat-kind message to a remote server with the content of a Location Status message.


## ToDo

- to code the information in 7-bit GSM to be able to read it in the receiving mobile equipment.
- Use the OPEN CHANNEL proactive command instead of sms
- ...

## Installation

To install the applet you need:
- sysmoUSIM-SJS1 SIM + USIM Card with ADM1 key (find it here: http://shop.sysmocom.de/products/sysmousim-sjs1).
- a card reader.
- The scripts for installation are in the osmocom repository (http://git.osmocom.org/sim/sim-tools/) and you have a guide (https://osmocom.org/projects/sim-toolkit/wiki)


## Contributing

Please contribute using [Github Flow](https://guides.github.com/introduction/flow/). Create a branch, add commits, and [open a pull request](https://github.com/fraction/readme-boilerplate/compare/).

Please note this source code has been released under the GPLv3 terms and all contributions will be considered. Have a look at the LICENSE file distributed with this code.






