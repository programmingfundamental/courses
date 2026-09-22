---
title: "Упражнение 6 — Bluetooth Low Energy"
sidebar:
  order: 6
  label: Упражнение 6
---

# Упражнение 6 — Bluetooth Low Energy

## 1. Инженерен проблем

Nearby measurement device се появява многократно в scan list. Свързването връща object веднага, но services още не са достъпни. Понякога read работи, а notifications никога не идват; след disconnect стар callback променя UI на новата connection. BLE е asynchronous workflow, а не постоянен надежден кабел.

**Използваме:** permission/capability state от Lab 5, repository/Flow и bounded history. **Предаваме към Lab 7:** измерими scan/connection lifetimes, serialized operations и controllable fake BLE transport.

## 2. Учебни цели

- Разграничава advertising, scanning и GATT connection.
- Реализира scan→select→connect→discover→read→subscribe workflow.
- Валидира services, characteristics, properties и payload format.
- Сериализира asynchronous GATT operations и callbacks.
- Проектира state machine с timeout и bounded reconnect.
- Обработва permissions, missing hardware и unexpected disconnect.
- Проверява lifecycle cleanup и persistent measurement history с fake transport.

## 3. Предварителни знания

Lab 4–5, coroutines/Flow, state transitions, UUID и binary decoding. Peripheral firmware не се пише в тези 135 минути.

## 4. Необходими инструменти

Android Studio, Logcat и предварително подготвен FakeBleTransport. **За реален BLE опит са нужни API 37 Android central и peripheral**: ESP32 с готов firmware, второ Android устройство с проверена advertising/GATT server поддръжка или BLE simulator. При API 33–36 използвайте fake adapter; emulator не се приема за доказателство за radio communication.

Реалният adapter използва API 37 connection settings/executor overload, за да няма deprecated connectGatt примери. В common app код поставете version/capability guard, без да зареждате API 37-specific classes на старо устройство. [BluetoothDevice](https://developer.android.com/reference/android/bluetooth/BluetoothDevice) описва актуалния overload.

## 5. Теоретична подготовка

Advertising оповестява limited metadata, scanning го наблюдава, а GATT connection предоставя services/characteristics. UUID определя protocol role, не удостоверява device identity. Characteristic може да поддържа read/write/notify/indicate; проверете properties, не предполагайте ги от името.

```text
Compose -> ViewModel -> BleRepository -> serialized GATT adapter
                            ^                 |
                            +---- callbacks --+
                     FakeBleTransport заменя само adapter-а
```

За central workflow се искат runtime BLUETOOTH_SCAN и BLUETOOTH_CONNECT. BLUETOOTH_ADVERTISE е нужен само на Android peripheral, не на monitor central. Не enable-вайте Bluetooth насила; предложете системния enable workflow и обработете отказ. Manifest:

```xml
<uses-permission android:name="android.permission.BLUETOOTH_SCAN"
    android:usesPermissionFlags="neverForLocation" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-feature android:name="android.hardware.bluetooth_le" android:required="false" />
```

`neverForLocation` тук е валидна декларация само защото scan results **не се използват за извеждане на физическо местоположение**. Location permission от Lab 5 остава отделно и не се премахва. Този flag може да филтрира част от beacons; лабораторният peripheral се проверява предварително. Вижте [Bluetooth permissions](https://developer.android.com/develop/connectivity/bluetooth/bt-permissions).

### Лабораторен GATT договор

| Роля | UUID / формат |
|---|---|
| Service | `12345678-1234-5678-1234-56789abcdef0` |
| Measurement, READ + NOTIFY | `12345678-1234-5678-1234-56789abcdef1` |
| Control, WRITE with response | `12345678-1234-5678-1234-56789abcdef2` |
| CCCD | `00002902-0000-1000-8000-00805f9b34fb` |
| Measurement bytes | VERSION u8=1 + SEQ u32 little-endian + VALUE float32 little-endian; точно 9 bytes |
| Control bytes | intervalMs u16 little-endian, 100..5000; точно 2 bytes |

VALUE е finite число в `[-1000,1000]`, в условни demo units. Receiver timestamp е локално monotonic време на получаване, не peripheral sample time. Sequence позволява duplicate/gap наблюдение; history identity включва local connection session ID. В упражнението run е до 5 min и sequence не wrap-ва; peripheral започва нова session при reset.

Примерен measurement: `01 01 00 00 00 00 00 20 41` означава version=1, seq=1, value=10.0. Control `F4 01` задава 500 ms. Payload е достатъчно малък за базов ATT размер; MTU negotiation не е необходима за тази задача.

Само една request/response GATT операция е in flight: discover, read, write или descriptor write. Успешното начало на API call не е завършване; проверявате и callback status. Notifications са отделни asynchronous events. Memory-safe callback overloads приемат `value: ByteArray`; writes подават отделни bytes, вместо mutable `characteristic.value`. Вижте [BluetoothGatt](https://developer.android.com/reference/android/bluetooth/BluetoothGatt) и [BluetoothGattCallback](https://developer.android.com/reference/android/bluetooth/BluetoothGattCallback).
