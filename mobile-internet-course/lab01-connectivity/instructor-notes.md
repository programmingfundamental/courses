# Упражнение 1 — Бележки за преподавателя
Само за преподавателя. Този файл не се публикува в студентските страници.

## 1. Концептуална цел
Connectivity е наблюдавано, променливо състояние с identity и uncertainty, не универсален boolean.

## 2. Какво НЕ е основна цел
Не настройвайте routing tables или VPN server в часа; не преподаваме Compose widgets.

## 3. Предварителна подготовка
Проверете emulator и по желание real Wi-Fi/cellular/VPN. Подгответе fake event sequence A/B с event clock и failing captive-portal case. Не предоставяйте reducer.

Изтеглете dependencies/images и проверете starter build преди часа. Подгответе работещ checkpoint от предходното занятие или преподавателско копие само на вече преподаваната функционалност. Самостоятелното решение за текущия час остава извън student starter.

## 4. Разпределение на 90-те минути
| Дейност | Минути |
|---|---:|
| Инженерен проблем и контекст | 10 |
| Теория и мини експеримент | 15 |
| Водена задача и checkpoint | 35 |
| Самостоятелна задача | 20 |
| Failure checks, измервания и анализ | 10 |
| **Общо** | **90** |

На 60-тата минута започва самостоятелната част. При изоставане използвайте подготвения guided checkpoint; не отнемайте нейното време за setup или UI оформление.

## 5. Начален въпрос / сценарий
Телефонът има Wi-Fi icon. Кое още трябва да знаем преди голям sync?

## 6. Основни концепции
Capability срещу transport; validation срещу API reachability; identity на callback; lifecycle ownership; цена на metered upload.

## 7. Чести грешки
- Online се задава в onAvailable.
- Transport е един mutually exclusive enum.
- onLost(A) изтрива B.
- Регистрация се прави при всяка recomposition.
- Няма unregister при navigation.
- Metered се приравнява на offline.

## 8. Насочващи въпроси
Кое network ID е актуално? Дали информацията е достатъчна за Online? Кой owner отменя callback? Какво пази историята при еднакви capabilities?

## 9. Очаквана архитектура
```text
[Android NetworkSource → ViewModel → Compose]
          |
ConnectivityManager default network
Backend още не е необходим
```
ViewModel не пази Activity. Adapter използва application service, а reducer получава immutable events; фиксиран buffer и lifecycle policy.

## 10. Ключови части от примерно решение
CallbackFlow регистрира един NetworkCallback, onCapabilitiesChanged прави snapshot, awaitClose го unregister-ва. Presentation използва lifecycle-aware StateFlow collection.

**НЕ ПОКАЗВАЙ НА СТУДЕНТИТЕ ПРЕДИ САМОСТОЯТЕЛНАТА ЗАДАЧА**

Допустима policy: no default→Offline; available без capabilities→Connecting; Internet+Validated→Online; иначе Limited. Lost се записва като transition за текущата identity и след кратък explicit interval се стабилизира в Offline. Стар network ID се игнорира. Не налагайте точно тази Lost duration.

## 11. Как да се предизвикат failure scenarios
Изключете Wi-Fi, включете airplane mode, възстановете. Replay-нете late Lost(A) след validated B. За VPN използвайте test snapshot, ако няма настроен real profile. Повторете Home/Resume и проверете registrations.

## 12. Очаквани наблюдения
Може да има повече callbacks от видими промени. Validation идва по-късно; устройството може да смени default route без пълно изчезване на Internet.

## 13. Проверка на самостоятелната задача
- [ ] Има transition table.
- [ ] Lost и initial Offline се различават.
- [ ] Stale ID не поврежда state.
- [ ] History <=20.
- [ ] Lifecycle cleanup е доказан.

## 14. Оценяване
| Област | Точки |
|---|---:|
| Водена практическа задача | 30 |
| Самостоятелна задача | 30 |
| Архитектура и code quality | 15 |
| Failure handling и tests | 10 |
| Анализ и измервания | 10 |
| Устна проверка | 5 |
| **Общо** | **100** |

Работещ happy path без failure semantics не получава пълните точки. Оценявайте аргументацията и доказателствата, а не конкретен хардуер или абсолютна latency.

## 15. Въпроси за устна защита
1. Какво доказва VALIDATED? — OS проверка на Internet, не business API health.
2. Защо пазим identity? — За корелация на callbacks и защита от стари events.
3. Какво е metered? — Сигнал за цена/ограничение, независим от reachability.
4. Кой спира producer? — Owner cancellation, не само спирането на UI collection.
5. Как тестваме без cellular? — Deterministic snapshots със същия contract.

## 16. Връзка със следващото упражнение
Lab 2 изпраща реална заявка и показва, че network observation и request outcome могат да се различават.
