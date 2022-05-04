### Nexus BLE peripheral sample app for Android

This application emulates BLE peripheral that can be used with Nexus BLE service. It should make it easier to understand how to implement Nexus BLE TLV protocol for wearables. The application has included PKCS#12 container with certificate and private key. When paired with Windows PC on which Nexus BLE service is running the certificate should appear in Windows Certificate Store.

## How to test
1. Install Nexus BLE service on Windows machine. You must use version v4.2.0 or higher.
2. Open the project in Android Studio and run the app. Make sure the phone supports BLE peripheral mode.
3. Nexus BLE service should automatically discover and connect to the phone.
4. BLE traffic is logged and can be seen in Logcat tab in Android Studio:
```
2022-05-04 15:01:56.932 9883-9962/com.nexusgroup.ble.peripheral I/ble-device-session: received envelope tag PROFILE_IDS
2022-05-04 15:01:56.962 9883-9962/com.nexusgroup.ble.peripheral D/gatt-service: sending back 26 bytes, value: (0x) A1-18-C2-10-01-02-03-04-05-06-07-08-01-02-03-04-05-06-07-08-C7-01-00-D0-01-00
2022-05-04 15:01:56.971 9883-9929/com.nexusgroup.ble.peripheral I/gatt-service: [Server] Notification sent
2022-05-04 15:01:57.082 9883-9929/com.nexusgroup.ble.peripheral I/gatt-service: [Server] WRITE REQUEST for characteristic 80323644-3537-4f0b-a53b-cf494eceaab3 received, value: (0x) A3-15-C2-10-01-02-03-04-05-06-07-08-01-02-03-04-05-06-07-08-C7-01-01
2022-05-04 15:01:57.096 9883-9962/com.nexusgroup.ble.peripheral I/ble-device-session: received envelope tag PROFILE_INFO
2022-05-04 15:01:57.101 9883-9962/com.nexusgroup.ble.peripheral D/gatt-service: sending back 38 bytes, value: (0x) A3-24-C3-1C-6E-65-78-75-73-2D-62-6C-65-2D-70-65-72-69-70-68-65-72-61-6C-2D-61-6E-64-72-6F-69-64-C7-01-01-D0-01-00
2022-05-04 15:01:57.113 9883-9929/com.nexusgroup.ble.peripheral I/gatt-service: [Server] Notification sent
2022-05-04 15:01:57.203 9883-9929/com.nexusgroup.ble.peripheral I/gatt-service: [Server] WRITE REQUEST for characteristic 80323644-3537-4f0b-a53b-cf494eceaab3 received, value: (0x) A2-15-C2-10-01-02-03-04-05-06-07-08-01-02-03-04-05-06-07-08-C7-01-02
2022-05-04 15:01:57.216 9883-9962/com.nexusgroup.ble.peripheral I/ble-device-session: received envelope tag PROFILE_CERTS
2022-05-04 15:01:57.251 9883-9962/com.nexusgroup.ble.peripheral D/gatt-service: sending back 42 bytes, value: (0x) A2-28-C4-20-94-86-20-48-92-35-0B-97-9E-F0-DF-1A-8A-EE-C0-4A-C1-19-F7-9C-0C-F6-E5-11-E6-8C-E8-5C-AA-90-6A-CC-C7-01-02-D0-01-00
```
5. In Windows, make sure certificate is copied to the Certificate Store:
6. Open https://server.cryptomix.com/secure and select the certificate to authenticate with:
7. After successfull signature you should see this:
