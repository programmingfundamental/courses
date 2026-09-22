---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

## 6. Мини експеримент / демонстрация — 10 минути

Fake transport връща един device пет пъти, забавя service discovery с 1 s и отказва read преди discovery. Нека наивният UI опита read веднага след connect request. После локално enable-нете notifications, но пропуснете CCCD write: fake peripheral не изпраща updates.

Запишете timeline с request started/callback complete и разликата между Connected transport и Ready application session. С real peripheral повторете само successful workflow след проверка на UUID/permissions.

## 7. Водена практическа задача — 35 минути

### Стъпка 1 — capabilities и scan

Използвайте BluetoothManager.adapter и проверете BLE feature, Bluetooth enabled и grants. Scan е user-triggered, филтриран по service UUID и ограничен до 10 s; stopScan се извиква със същия callback при timeout, selection или lifecycle stop. Map за discovery е до 50 entries с update на lastSeen/RSSI; duplicate advertisements не са нови devices. Избран адрес е session-local identifier, не постоянна identity гаранция. Вижте [Find BLE devices](https://developer.android.com/develop/connectivity/bluetooth/ble/find-ble-devices).

### Стъпка 2 — repository contract

```kotlin
data class BleDeviceSummary(val key: String, val name: String?, val rssi: Int)
data class BleReading(val sessionId: String, val sequence: Long,
                      val value: Float, val receivedElapsedNanos: Long)
interface BleRepository {
    val devices: StateFlow<List<BleDeviceSummary>>
    val readings: Flow<BleReading>
    suspend fun scan()
    suspend fun connect(deviceKey: String)
    suspend fun setInterval(intervalMs: Int)
    suspend fun disconnect()
}
```

Platform objects остават в adapter; ViewModel получава immutable state. Fake transport поддържа същите stages и explicit failure hooks. Callback events се сериализират към един owner context; pending operations queue има cap=16. Не споделяйте mutable GATT state между произволни callback threads.

### Стъпка 3 — connect/discover/read

За API 37 създайте BluetoothGattConnectionSettings.Builder с autoConnect=false и TRANSPORT_LE, после използвайте connectGatt(settings, executor, callback). Call-ът може да върне null; обработете failure. Settings и executor са описани в [connection settings builder](https://developer.android.com/reference/android/bluetooth/BluetoothGattConnectionSettings.Builder).

Изчакайте successful STATE_CONNECTED callback, тогава discoverServices; изчакайте successful services callback и проверете точните UUID/properties. След него прочетете Measurement и валидирайте payload. Не пренасяйте characteristic reference от стара GATT session към нова.

### Стъпка 4 — write и notifications

Напишете Control=500 ms с `writeCharacteristic(characteristic, bytes, WRITE_TYPE_DEFAULT)` и проверете immediate return code плюс onCharacteristicWrite status. За subscription извикайте setCharacteristicNotification(measurement, true), после `writeDescriptor(cccd, ENABLE_NOTIFICATION_VALUE)` и изчакайте successful onDescriptorWrite. Едва тогава UI показва Ready/Subscribed.

Текущият measurement callback ползва overload с отделен value bytes; копирайте преди asynchronous handoff. Read и notification със същия sequence не се записват два пъти. UI показва последна value, notification count и последен error; не показва Connected като доказателство за поток от данни.

## 8. Checkpoint — 5 минути

- Scan завършва до 10 s и list не дублира advertisements.
- Successful workflow е видим като ordered timeline.
- Control write и Measurement read са валидни по wire vectors.
- Notifications започват след CCCD acknowledgement.
- Stop/navigation затваря GATT и scan; fake mode е функционален без hardware.

## 9. Самостоятелна задача — 35 минути, в часа

Добавете **connection state machine**, timeout/reconnect policy и persistent history. Минимум states: Idle, Scanning, Connecting, Connected, Disconnected, Error; можете да отделите Discovering/Subscribing/Ready за по-точни transitions.

**Изисквания:** stage deadlines (например connect 10 s, discover 5 s, read/write 3 s), stale-notification detection и максимум 3 reconnect attempts с backoff 1/2/4 s. Retry е само докато screen е видим, user intent е active, permissions са налични и Bluetooth е enabled. Manual Disconnect отменя pending retry. Missing characteristic/denied permission не трябва да водят до безкраен reconnect.

**History:** schema version=5, BleMeasurement с sessionId, sequence, value и reception timestamps; последни 1000 records в Room, до 100 в UI. Индекс/uniqueness предотвратява двойно записване на read+notification от една session. New connection получава new session ID. Invalid bytes никога не се persist-ват като валидно measurement.

**Техническо решение:** изберете как state reducer сериализира callback/timer/user events и как generation token отхвърля късен callback от стар BluetoothGatt. Не е дадена готова transition table. Опишете кои failures са retryable, как stale timer се reset-ва и кой close-ва GATT при timeout.

**Приемане:** fake unexpected disconnect води до bounded recovery; old callback не променя новата session; manual stop предотвратява reconnect; denied/disabled/missing characteristic имат различими errors; history survives restart, но connection не се възстановява без explicit action и capability recheck. Предайте собствена transition table и tests за поне четири competing events.

## 10. Edge cases

| Случай | Очакване |
|---|---|
| Bluetooth disabled / permission denied | normal capability state, без crash |
| Duplicate scan result | update на съществуващ entry |
| Device disappears | stage timeout, ограничена recovery |
| Connection callback с failure status | explicit error, cleanup |
| Characteristic/CCCD липсва | incompatible device, без retry storm |
| Notifications спират | stale indicator и policy, не фиктивна свежа value |
| Unexpected disconnect | generation invalidation и bounded reconnect |
| Old callback след нов connect | игнориране според owner/session |
| Payload length/version/NaN е невалиден | reject counter, без history insert |

## 11. Тестване

Pure tests за little-endian vectors и reducer transitions. Fake transport трябва да може да: дублира advertisement, пропусне callback, върне non-success status, скрие characteristic, спре notifications и достави late callback. Lifecycle/permission tests cancel-ват scan и retries при Home/revoke. Resource test повтаря 20 sessions и очаква activeGatt=0/activeScan=0 след cleanup. Real BLE test, когато има hardware, проверява interoperability; passing fake test не доказва RF performance.

## 12. Наблюдение и измерване — 10 минути

Съберете scan duration, unique devices/advertisements, connect/discovery/subscription times, requested/observed notification interval, malformed/duplicate/gap counts и retry count. Logs включват local session/generation, без публични MAC адреси. След Stop пребройте активните scan callbacks, GATT objects и retry jobs. Докладвайте real/fake mode и radio условията, когато са приложими.

## 13. Въпроси за анализ

1. Защо successful connect request не означава Ready session?
2. Защо local notification enable не е достатъчен без CCCD?
3. Защо GATT operations се сериализират?
4. Как old callback може да повреди нов connection state?
5. Защо reconnect има budget и зависи от user intent?
6. Как UUID/device address се различават от authenticated identity?
7. Какво доказва fake transport и какво изисква реален peripheral?

## 14. Очакван резултат

BLE workflow с read/write/notifications, самостоятелна recovery state machine и persistent bounded history. Всички основни scenarios могат да се проверят и без peripheral, а реалните observations са отделени от симулацията.

## 15. Критерии за приемане

- [ ] Permission/capability guard включва runtime API support.
- [ ] Scan е bounded, user-triggered и се прекратява коректно.
- [ ] UUID/properties/payload се валидират.
- [ ] GATT operations чакат callback completion и са serialized.
- [ ] CCCD acknowledgement предхожда Ready/Subscribed.
- [ ] Самостоятелната state machine има deadlines, bounded reconnect и generation protection.
- [ ] Room history е bounded, unique и се възстановява след restart.
- [ ] Stop/revoke освобождават GATT, scan и timers.
- [ ] Fake failure tests и mode-specific наблюденията са предадени.
