# Проверка на комплекта

Изпълнена на 20.09.2026 г. върху Windows, JDK 21, Android SDK 36 и Docker с Linux containers.

## Реално изпълнени проверки

| Област | Проверка | Резултат |
|---|---|---|
| Backend | `mvn -B -f backend/pom.xml verify` от `platform/` | Четирите Quarkus приложения се компилират; 7 tests, 0 failures/errors. |
| Persistence | H2 transaction tests | Успешен insert/read и rollback без оставен запис. |
| HTTP fixture | Quarkus tests | 200 JSON, 422, 503 с Retry-After и malformed JSON се различават. |
| Android | `gradlew.bat :app:assembleDebug :app:testDebugUnitTest` | APK и двата unit tests се изпълняват успешно. |
| Android runtime | Инсталиране и cold launch в временен emulator | MainActivity стартира; UI е прегледан визуално; няма AndroidRuntime crash. |
| Infrastructure | Изолиран Compose проект с events и observability | Всичките 8 containers стартират; Gateway health и BFF fixture връщат 200. |
| Events | Един synthetic JSON event в Redpanda | Notification consumer изпраща event към BFF internal relay; отговор 202. |
| Tracing | Gateway fixture request → BFF | Jaeger съдържа общ trace с parent/child spans; structured BFF log има requestId и traceId. |
| Token tool | Пет варианта на credential fixture | RSA signature, member/viewer, expired, wrong audience и other user claims са проверени без запис на token output. |
| Lost response | Локален mock upstream и response-drop proxy | 503 се препраща; първият успешен POST response се губи след upstream commit; следващият се доставя; Idempotency-Key се запазва. |
| Site | Astro check/build, link/migration checks | 0 diagnostics; 816 pages built; 52 771 internal links/assets/anchors са валидни. |
| Student pages | Преглед на генерирания HTML | 10 labs, по един H1 и 16 основни раздела; 10 overview links; правилен раздел „Магистри“. |
| Публикуване на съдържание | Allowlist в sync script и проверка на dist | Instructor notes, private solution markers и key files не присъстват в сайта. |

Тестовете проверяват предоставената инфраструктура и starter contracts. Функционалността с `TODO Lxx-G/Lxx-I` се реализира от студентите и се приема чрез checkpoint и checklist на съответното занятие. Не е отчетен като изпълнен целият бъдещ business flow, JWT authorization policy или offline/event reducer.

## Consistency review

- Всички 10 студентски файла имат 16 основни раздела след заглавието, 5–8 измерими цели, 5–8 въпроса за анализ, самостоятелна задача и поне 3 failure scenarios.
- Всички 10 преподавателски файла имат 16 раздела, поне 5 чести грешки, отговори за устна защита, план за 90 минути и оценяване от 100 точки.
- Самостоятелната работа е ограничена до едно инженерно решение за 20 минути. Setup/downloads са предварителна подготовка. Времевата реалистичност е оценена по обхвата и наличния scaffold; не е провеждан пилотен час със студенти.
- Connectivity, resilient API client, backend contracts, service boundaries, Gateway, BFF, resilience, offline sync, events и observability развиват един проект последователно.
- Всяка backend тема е свързана с mobile round trips, payload, променлива връзка или времеви budget. Mobile упражнение не се свежда до UI оформление.
- Gateway routing/cross-cutting concerns и BFF aggregation са разделени. Android използва един entry point и не познава Docker topology.
- Idempotency, uncertain POST outcome, duplicate/stale events, bounded retry, cancellation и lifecycle са последователни между student labs, notes и архитектурата.
- Instructor snippets за самостоятелните решения са отделени и маркирани. Starters съдържат infrastructure и TODO, без готовите reducer, dedup и authorization решения.
- Използват се NetworkCapabilities/NetworkCallback, lifecycle-aware Flow, Room/WorkManager, Quarkus REST и актуалният `quarkus-junit` artifact. Не се използва deprecated NetworkInfo.
- Всички starter manifests сочат съществуващи файлове в общите Android/Quarkus проекти; локалните Markdown links са проверени.
- README, ports, Compose overlays, реалните директории и архитектурният договор съвпадат. Lab 10 затваря учебните public fixture/WebSocket routes, докато не бъдат защитени.

За повторение на проверките на сайта от repository root: `npm run check`, `npm run build`, `npm run check:links`, `npm run check:migration`. За starter builds използвайте командите от README. Нов clone изисква локален SDK и изтегляне на dependencies/images.
