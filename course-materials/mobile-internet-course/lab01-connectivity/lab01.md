# Упражнение 1 — Mobile Connectivity и Network State

## 1. Инженерен сценарий
Пътник вижда Wi-Fi icon, но captive portal блокира Internet. По-късно VPN сменя default route; приложението показва Online по стар callback и изпраща голям upload през metered cellular. Трябва да наблюдаваме capabilities, без да ги превръщаме в гаранция за успешна business заявка.

## 2. Учебни цели
- Анализира default network и transport/capability разликата.
- Реализира lifecycle-owned callback observation.
- Разграничава Internet capability от validation.
- Измерва loss/recovery и network transitions.
- Проектира state reducer с ограничена история.
- Аргументира metered/VPN behavior и stale callback protection.

## 3. Предварителни знания
Kotlin, Flow, ViewModel, Android lifecycle. Готовият Compose shell се отваря и стартира преди часа.

## 4. Необходими инструменти
Android Studio, SDK 36, emulator, Logcat; физически телефон с Wi-Fi/cellular/VPN е по избор. Deterministic NetworkSnapshot replay замества недостъпните hardware scenarios.

## 5. Архитектурен контекст
```text
[Android NetworkSource → ViewModel → Compose]
          |
ConnectivityManager default network
Backend още не е необходим
```
Работим върху network observation в mobile клиента. Същият state по-късно управлява подсказки за sync, но не заменя HTTP result.

## 6. Кратка теория
Default network е маршрутът, който OS избира за приложението. NetworkCapabilities може да съдържа няколко transports; VPN не е взаимно изключващ enum с Wi-Fi. NET_CAPABILITY_INTERNET означава заявена Internet възможност, а VALIDATED — че OS я е проверила. Captive portal, DNS или outage на конкретния API остават възможни.

NOT_METERED се интерпретира независимо от Online; няма transport→цена предположение. Използвайте registerDefaultNetworkCallback и onCapabilitiesChanged. onAvailable не съдържа достатъчно данни за Online. Съхранявайте Network identity; късен onLost за стар network не трябва да изтрие новия. Не извиквайте synchronous capability query вътре в callbacks за заместител на доставеното състояние. [Android network state](https://developer.android.com/develop/connectivity/network-ops/reading-network-state) описва callback semantics.

ACCESS_NETWORK_STATE е normal permission; INTERNET е необходима за заявки. UI collection и producer lifetime са отделни. Регистрацията има един owner; cancellation достига unregisterNetworkCallback със същия instance. Raw network ID е само observation identity, не постоянен user identifier.

## 7. Мини експеримент
Покажете Wi-Fi connected без validated Internet чрез captive portal или fake capabilities. Replay: Available(A)→Capabilities(A,unvalidated)→Capabilities(A,validated)→Available(B)→Capabilities(B,validated)→Lost(A). Наивният reducer завършва Offline. Студентите записват защо това е грешно.

## 8. Водена практическа задача
### Стъпка 1
В NetworkSource.kt използвайте готовия immutable NetworkSnapshot. Реализирайте Android adapter с callbackFlow; копирайте networkId, transports, INTERNET, VALIDATED, NOT_METERED и VPN. Heavy work не се изпълнява в callback thread.

### Стъпка 2
Осигурете initial Unknown/Connecting snapshot, следван от callbacks. Bounded channel до 32 и conflation са допустими за latest capabilities; diagnostic event history не се представя като lossless capture. В awaitClose отменете callback регистрацията.

### Стъпка 3
Свържете source към ViewModel/StateFlow с един collector и STARTED lifecycle policy. Compose показва Transport, Internet capability, Validated, Metered, VPN и Network availability като отделни полета. Не правете ConnectivityManager calls от UI.

### Стъпка 4
Пуснете реална промяна Wi-Fi off/on или replay. Добавете activeCallbackCount, timestamp на последната промяна и source mode. Log е само при промяна, без IP/MAC/user identifiers.

Работете в общия platform проект. От директорията starter изпълнете `node ../../platform/tools/lab.mjs 01 build`. Build проверява scaffold-а и вашите промени; наличието на 501 LAB_TODO означава незавършена учебна функционалност, а не успешен checkpoint.

## 9. Checkpoint
- Всички шест network полета се виждат; unvalidated не се означава като Online.
- След Home/Stop няма останала callback registration.
- Rotation не създава два collectors.

## 10. Самостоятелна задача
**Problem statement:** Проектирайте reducer с Offline, Connecting, Online, Limited/Unvalidated и Lost плюс последните 20 transitions.

**Functional requirements:** Дефинирайте кои входни събития водят до всеки state; пазете предходно/ново състояние и monotonic timestamp. Lost трябва да е наблюдаем преход, без вечен подвеждащ status.

**Technical constraints:** Един сериализиран owner; без Boolean isConnected като цял модел. Решете stale-network guard и кога initial Offline се различава от Lost. Не е дадена готова transition table.

**Acceptance criteria:** Посоченият A→B→Lost(A) replay запазва правилния текущ network. Duplicate capabilities не пълнят history; 100 events оставят <=20 entries. Denied Internet validation, recovery и VPN имат обясним state.

Решението и кратката аргументация се проверяват в края на същото занятие. Това не е домашна работа.

## 11. Failure scenarios / Edge cases
| Сценарий | Очакване / наблюдение |
|---|---|
| Wi-Fi off / airplane mode | Offline/Lost според собствената policy; няма crash. |
| Wi-Fi→cellular | Нова identity и metered state, без stale rollback. |
| VPN enable/disable | Transport set и VPN status се обновяват. |
| Interface без Internet validation | Limited; application request може да се провери отделно. |

## 12. Тестване
Pure reducer tests с последователностите A/B, duplicates и initial state; happy path Available→Validated; failure path loss без recovery. Instrumented lifecycle test повтаря 10 navigation/rotation цикъла и очаква registrations=0 след Stop. Fake replay е задължителен за случая validated=false дори emulator да няма captive portal.

## 13. Наблюдение и измерване
Брой registrations, transitions и dropped diagnostic events; elapsed loss→recovery; request attempt outcomes се добавят в Lab 2. Не измервайте callback time като Internet latency и не приемайте metered за ниска скорост.

Запишете кратка таблица в `results/lab01/observations.md`: configuration, workload, expected/actual, sample count и ограничения. За latency използвайте p50/p95/max, когато имате достатъчно измервания, а не само средна стойност.

## 14. Въпроси за анализ
1. Защо Wi-Fi interface не доказва използваем Internet?
2. Защо VALIDATED не гарантира нашия backend?
3. Кой network притежава onLost събитието?
4. Как VPN променя transport модела?
5. Кога conflation е подходяща и какво губи history?
6. Защо network state не трябва да блокира всяка заявка?

## 15. Очакван резултат
Network Inspector с отделни capabilities и student-authored transition reducer; bounded history и доказано cleanup.

## 16. Критерии за приемане
- [ ] Има шестте наблюдавани network полета.
- [ ] Default network callbacks се освобождават.
- [ ] Transition logic е самостоятелно описана.
- [ ] History е ограничена до 20.
- [ ] Stale callback/recovery/VPN са тествани.
- [ ] Резултатите различават real и fake source.
