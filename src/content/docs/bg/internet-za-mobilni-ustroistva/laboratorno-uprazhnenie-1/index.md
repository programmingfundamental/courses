---
title: "Упражнение 1 — Mobile Connectivity и Network State"
sidebar:
  order: 1
  label: Упражнение 1
---

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
