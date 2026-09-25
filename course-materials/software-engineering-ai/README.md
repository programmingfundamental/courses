# Софтуерно инженерство за AI системи

Практически курс от **10 лабораторни упражнения по 110 минути** за IV курс, бакалавър „Изкуствен интелект“. Целта е да превърнем notebook експеримент в поддържана, тестваема, версионирана, автоматизирана и наблюдаема система. Това е software engineering курс; ML компонентите са даденост, а не предмет на алгоритмично състезание.

## Аудитория, prerequisites и learning outcomes

Предполага се Python, основи на ML/Jupyter, Git, REST, Docker, scikit-learn/pandas/numpy, Linux и бази данни. След курса студентът може да формулира measurable requirements, да разделя training/inference, да refactor-ва coupling, да избира design patterns, да изгражда test strategy и CI/CD, да управлява data/model lineage и rollback и да аргументира observability/security/ethics/maintenance решения.

```text
Notebook → Engineering Process → Requirements → Architecture → Modularity
→ Design → Testing → CI/CD → MLOps → Observability → Security + Maintenance
```

Педагогическият модел във всеки lab е **проблем → теория → анализ на лошо решение → практическа задача → самостоятелна задача → тестове → инженерна дискусия**. Student files дават стъпки и acceptance criteria, без пълно решение; instructor-notes са отделни. Общият `ai-platform` е runnable reference baseline и база за последователни student increments. Starter TODO задачите изискват собствен diff и нови tests, а не просто стартиране на reference suite.

## Общ проект: AI Prediction Platform

Системата класифицира **синтетични заявки** по три числови features: token_count, keyword_score, previous_requests. Label 1 означава учебна ескалация. Dataset има 400 rows, няма лични данни и не изисква download/GPU. Cohort A/B е synthetic slice marker, изключен от model features; не позволява реални fairness заключения.

```text
CSV + version/hash manifest
        ↓
Data validation → shared feature selection
        ↓
Offline training + held-out evaluation
        ↓
Immutable model bundle + experiment metadata
        ↓
Local registry / champion pointer
        ↓
Pinned-version inference service → FastAPI → Client
        ↓
Structured logs / bounded metrics → CI/CD / operational feedback
```

Подробности: [архитектура](architecture/system-overview.md), [източници](architecture/references.md), [проверки и ограничения](VALIDATION.md).

## Технологии и граници

Python 3.12, scikit-learn1.7.2, pandas2.3.3, numpy2.3.4, FastAPI0.119.1, Pydantic2.12.3, pytest, Ruff, GitHub Actions и Docker. Точните преки и транзитивни версии са в [requirements.lock](ai-platform/requirements.lock); requirements.in е входният списък, не reproducible install command. Версиите са фиксиран учебен baseline, не твърдение за липса на известни vulnerabilities.

За малкия local проект използваме **еквивалентен experiment tracking/model registry**: immutable version directories с parameters, metrics, dataset hash, git commit, source hash, dependency lock hash, split IDs и model artifact. Git+CSV+SHA manifest осигурява data versioning. Тези решения реализират нужните учебни функции без MLflow/DVC service. Сравнението с MLflow/DVC и критерият за миграция са в lab08. JSON metrics демонстрират observability; не са Prometheus/OpenTelemetry exporter.

## Setup

Нужни са Python 3.12, Git и Docker с Linux containers. От `software-engineering-ai`:

```powershell
python -m venv .venv
.\.venv\Scripts\Activate.ps1
cd ai-platform
python -m pip install -r requirements.lock
python -m pip install --no-deps --no-build-isolation -e .
python -m pip check
python -m ai_platform.cli validate
pytest -q
```

В Bash активирайте с `source .venv/bin/activate`, после същите commands. Ако PowerShell policy не позволява activation, използвайте пълния path до `.venv/Scripts/python.exe`; от ai-platform той е `../.venv/Scripts/python.exe`. Не е нужно да променяте глобалната policy.

Committed dataset е готов. `python -m ai_platform.cli generate` възпроизвежда synthetic fixture с seed42, но за нови данни създавайте нов version/path вместо overwrite на v1. Notebook се проверява с `python scripts/check_notebook.py`. За UI по желание инсталирайте JupyterLab в отделна notebook среда/същата среда с осъзнат dependency update и отворете `../lab01-lifecycle-processes/starter/experiment.ipynb`. Kernel cwd трябва да е starter папката; hardcoded path е умишленият lab01 smell.

### Обучение, metadata и promotion

```powershell
python -m ai_platform.cli train --version demo-v1
python -m ai_platform.cli inspect --version demo-v1
python -m ai_platform.cli promote --version demo-v1
```

Artifacts са в `ai-platform/artifacts/demo-v1/`: model.joblib и metadata.json. Версията **не може да се презапише**; при повторна работа използвайте demo-v2 или нов root с `--root`. Preprocessing се fit-ва само върху training split. Учебните quality gates са held-out F1≥0.85 и recall≥0.85 в tests; promotion baseline проверява F1≥0.85, а допълнителните gates се добавят в labs07/08. Не използвайте същия holdout за неограничено model selection в реален проект.

За champion/challenger сравнение:

```powershell
python -m ai_platform.cli train --version compare-v1 --strategy linear
python -m ai_platform.cli train --version compare-v2 --strategy tree
python ../lab08-mlops/starter/compare.py compare-v1 compare-v2
python -m ai_platform.cli promote --version compare-v1
# Само след review и преминали gates:
python -m ai_platform.cli promote --version compare-v2
python -m ai_platform.cli rollback
```

Ако candidate не покрие gate, отказът е правилен резултат. `rollback` връща предишния champion pointer, но не сменя автоматично model в running API. Има single-writer assumption; за конкурентни operators е нужен transactional registry.

### Локален API

```powershell
$env:AI_API_KEY = python -c "import secrets; print(secrets.token_hex(32))"
$env:AI_MODEL_VERSION = 'demo-v1'
python -m uvicorn ai_platform.api.app:app --host 127.0.0.1 --port 8000 --no-access-log
```

Environment key остава локален; не го commit-вайте. Във втори terminal задайте същия key локално или използвайте UI на `/docs` с header. `.env.example` е template; Settings не зарежда `.env` автоматично — задайте environment явно. App без key отказва protected endpoints и остава unready.

| Endpoint | Contract |
|---|---|
| GET /health/live | 200 ако process работи |
| GET /health/ready | 200 с model_version; 503 при missing model/key |
| POST /predict | X-API-Key; batch от features; label/probability/model_version |
| GET /metrics | X-API-Key; counts, latency p95/samples, model_version |

Request example:

```json
{"instances":[{"token_count":140,"keyword_score":0.8,"previous_requests":2}]}
```

Missing/invalid/extra feature →422, липсващ/невалиден key→401, batch над configured32→413 (absolute schema limit128), unavailable model→503. Inputs не се връщат в validation error. API contract не разкрива estimator class. Model се load-ва веднъж при lifecycle startup; import/request не обучават.

### Docker release и локален CD

След train/quality validation, от ai-platform:

```powershell
docker build --build-arg MODEL_VERSION=demo-v1 -t ai-course:demo-v1 .
python scripts/deploy_local.py ai-course:demo-v1 --port 18090
python scripts/benchmark.py --port 18090 --count 100
```

Key трябва да е зададен в environment. Docker image съдържа конкретния bundle; key се подава при run, не при build. Deploy script resolve-ва tag до image ID и докосва само container с име/label `ai-platform-course`. При update старият container се запазва като `ai-platform-course-previous`; readiness failure връща стария. Successful update запазва stopped previous container за explicit review/cleanup. Това е single-host teaching CD с кратък downtime, не zero-downtime rollout. След successful deployment проверете model_version и prediction contract; gate не замества operational observation.

За cleanup проверете label с `docker inspect` и премахнете само създадените учебни containers. Не използвайте global prune. За reset на experiments изберете нов artifact root/version; не изтривайте чужди datasets или registry directories. Source fixes се връщат чрез собствени Git checkpoints, а не чрез reset на runtime files.

## Упражнения

| № | Тема | Engineering increment |
|---|---|---|
| 01 | [Lifecycle и процеси](lab01-lifecycle-processes/lab01.md) | backlog, roadmap, DoD |
| 02 | [Requirements](lab02-requirements/lab02.md) | measurable SRS и traceability |
| 03 | [Architecture](lab03-architecture/lab03.md) | training/serving boundary и ADR |
| 04 | [Modularity](lab04-modularity/lab04.md) | modules, ports, configuration |
| 05 | [Design patterns](lab05-design-patterns/lab05.md) | stable orchestration/extension |
| 06 | [Testing](lab06-testing/lab06.md) | data/model/API test strategy |
| 07 | [CI/CD](lab07-cicd/lab07.md) | automated gates и immutable release |
| 08 | [MLOps](lab08-mlops/lab08.md) | lineage, promotion, rollback |
| 09 | [Observability](lab09-observability/lab09.md) | measurements и incident runbook |
| 10 | [Security, ethics, maintenance](lab10-security-ethics-maintenance/lab10.md) | risk/debt register и retirement |

## Git workflow

Работете в feature branch, например `codex/ai-lab04-repository`. Един commit добавя characterization/negative test, следващият — refactoring/fix. PR описва problem, behavior, alternatives, validation и debt. Не commit-вайте `.venv`, artifacts, logs, API keys или PII. Dataset/manifest и source/lock са versioned; large artifacts се пазят отделно с immutable identity.

Преди release запишете clean source commit. При dirty tree metadata е обозначена с `-dirty`; при недостъпен Git е `unavailable`. Source SHA е допълнителна identity, не оправдание да се твърди clean release. При този repository учебните файлове първоначално са uncommitted; преподавателят прави review/commit преди раздаване на pinned release.

## Tests и CI

```powershell
ruff check src tests scripts
ruff format --check src tests scripts
pytest -m 'not integration' -q
pytest --cov=ai_platform --cov-fail-under=85 --cov-report=term-missing -q
python scripts/check_notebook.py
python scripts/verify_course.py
```

Coverage обхваща основните ai_platform modules; CLI е изрично изключен и се проверява чрез отделни smoke commands. Unit selection все още може да използва малък trained fixture; markers обозначават основния contract, а не гаранция за пълна IO изолация. Performance benchmark е bounded sequential measurement и не е fragile unit timing gate.

Реалният [GitHub Actions workflow](../../.github/workflows/software-engineering-ai.yml) се изпълнява при промени в курса и workflow_dispatch. Той инсталира pinned dependencies, lint/format, unit/full tests с85% gate, data/notebook/course checks, training+promotion, Docker build и local deployment smoke. Няма public deployment или secrets в repository. Remote CI run изисква push/PR от потребителя; локалната проверка не се представя като изпълнен GitHub run. При копиране като standalone repo адаптирайте `paths` и `working-directory`.

## Security, ethics и ограничения

Използвайте само synthetic data и localhost. Joblib artifacts са executable serialized Python objects: приемат се само от доверения собствен pipeline/registry; hash защитава от corruption при trusted metadata, не от злонамерен publisher. Не добавяйте upload/remote-download endpoint за модели. API key е minimal access control; production identity, TLS, request-body limits, rate limiting и shared metrics изискват отделно решение.

Този комплект демонстрира engineering пътя към production readiness, но не твърди production certification. Model/data cards и debt register трябва да документират intended use, prohibited use, evaluation support, privacy, retention, owner и retirement. Synthetic cohort tests и кратките benchmarks не доказват fairness/availability на реална система.
