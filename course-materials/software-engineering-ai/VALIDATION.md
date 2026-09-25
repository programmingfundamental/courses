# Consistency review и изпълнени проверки

Дата: 22.09.2026. Обхват: новият software-engineering-ai курс и неговият отделен GitHub Actions workflow. Предишният курс applied-web-security и съществуващият сайт не са променяни.

След проверките временният labelled validation контейнер е премахнат. Изходните файлове, локалните model bundles и Docker images са запазени; други услуги не са спирани.

## Изпълнени технически проверки

| Проверка | Резултат |
|---|---|
| Изолирана Python 3.12 среда и pinned dependencies | PASS |
| pytest baseline | 41 passed |
| Coverage на core modules | 98% statement coverage; CLI е изрично извън този scope |
| Ruff lint/format | PASS |
| Notebook code cells, clean sequential execution | PASS; Jupyter UI не е автоматизирано проверяван |
| Dataset validation и SHA manifest | PASS; 400 synthetic rows |
| Training/evaluation/lineage | PASS; deterministic split/metrics и близки predictions |
| Held-out quality за demo-v1 | F1 0.98969, recall 0.97959, accuracy 0.99; само synthetic acceptance set |
| Immutable save / corrupted artifact / runtime mismatch | PASS в pytest |
| Promotion/rollback на registry pointer | PASS в pytest и CLI promotion |
| Docker build | PASS върху Python 3.12.14-slim/Linux |
| Container deployment и readiness | PASS на loopback port 64403 |
| Реални prediction requests | PASS: 10 warmup +100 measured requests |
| Умишлен unready release | Очакван отказ; финалният image/model demo-v2 е възстановен и ready |
| Runtime structured logging | PASS: 21 JSON request events след readiness и bounded prediction smoke |
| Starter programs | PASS: notebook, SRS checker, monolith, coupled component, factory fixture, happy-path test и реален comparison на две версии |
| Структура и локални Markdown links | PASS: verify_course.py |

Примерното локално измерване през Docker даде p95≈3.97ms при concurrency 1,batch 1,100 measured requests след 10 warmup. Това не е гарантиран SLO/throughput за друга машина, concurrent workload или production deployment. Host: Windows с Docker Linux containers; client Python 3.12.14. Порт18090 беше зает и не е спирана чуждата услуга.

## Съдържателен review

- Точно 10 теми следват progression от notebook/lifecycle до security/maintenance.
- Всеки student файл има 16 раздела, 6 цели, guided checkpoint, отделна independent задача, tests, минимум 3 edge cases и 6 въпроса.
- Всеки instructor файл има 15 раздела,100-точкова rubric, минимум 5 чести грешки и отговори за устна защита.
- Starter code използва малкия общ dataset и има TODO; JSON/YAML review fixtures са изрично означени като non-executable, а не представени за production code.
- Същият AI Prediction Platform contract се развива през курса. Задачите са engineering задачи, а не hyperparameter/model competition.
- Tracking е ограничен local functional equivalent на experiment tracking/registry; data versioning е Git+manifest. Не се твърди, че са инсталирани MLflow/DVC services.
- Документирани са test scope, immutable deployment, promotion/deployment разликата, rollback, privacy, synthetic fairness ограничения и debt/retirement decisions.

## Известни граници

GitHub Actions workflow е добавен, но remote run не е стартиран чрез push/PR. Локалните еквивалентни стъпки са проверени отделно. Няма public deployment, GPU, paid cloud или external model API.

Statement coverage не измерва архитектурна правилност или пълно test качество. Финалният run е 41 passed и 98.28% statement coverage на описания scope. В текущата pinned среда Starlette TestClient дава deprecation warning за AnyIO BlockingPortal alias; tests минават, но това е конкретен dependency-debt item за планирана съвместима актуализация, не скрита грешка.

При review са коригирани runtime INFO logger конфигурацията, dataset version derivation за нов filename, Docker error diagnostics и проверката за readiness след rollback. Pending registry directories са изключени от Docker build context. Windows sandbox/host test users имаха различни filesystem права; финалните pytest проверки използват собствен workspace temp path, а build user получи read-only достъп само до генерирания demo-v2 bundle. Това не променя runtime secret policy.

Работните source файлове не са commit-нати от асистента. При недостъпен/dirty Git metadata показва това явно; source hash е допълнителна следа. Преподавателят трябва да създаде clean reviewed release commit преди използване на git SHA като доказателство за release lineage.

Joblib е допустим само при trusted local artifact provenance. Local registry и metrics са single-writer/process решения. HTTP/API key, JSON metrics и кратки benchmarks не са production security/availability/fairness certification. Самостоятелните student extensions се оценяват след реализацията им, а не се представят за вече завършени.
