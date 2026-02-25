# Hubitat Device List App

## Overview
This repository contains a Hubitat App (`HubitatDeviceListApp.groovy`) that lists devices and supports filtering by:
- Device Type
- Room
- Protocol (Any, Zigbee, or Z-Wave)

The app output is limited to:
- Device ID
- Device Name
- Type
- Room

## Installation
1. In Hubitat, go to **Apps Code**.
2. Create a new app and paste in the contents of `HubitatDeviceListApp.groovy`.
3. Save, then go to **Apps** and add **Hubitat Device List**.
4. Choose filter values and view the generated device list.

## Changelog
- 0.1.3: Added explicit device authorization input so filters and results populate reliably in Hubitat app permissions model.
- 0.1.2: Fix empty results by calling `getAllDevices()` directly with safe fallback.
- 0.1.1: Fix install/runtime error by switching device enumeration to `getAllDevices()`.
- 0.1.0: Initial release with dynamic filter UI and device list output.
