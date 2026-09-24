---
title: "AI Prediction Platform — architectural contract"
sidebar:
  order: 100
---

# AI Prediction Platform — architectural contract

## Responsibilities и dependency direction

```text
data/dataset.py ------> validation + version/hash checks
features/transform.py -> stable feature names/order, no target/cohort
training/strategies.py -> estimator factory + preprocessing pipeline
training/pipeline.py --> offline split, fit, metrics, lineage
inference/repository.py -> ModelRepository port + trusted FileRepository
inference/service.py --> Predictor using repository contract
api/app.py -----------> HTTP schema/auth/errors + serving lifecycle
config/ -------------> validated environment settings
observability.py ----> bounded counts/latency + drift smoke
```

Training се изпълнява чрез CLI. Inference не импортира training implementation. Artifact е boundary: model.joblib плюс metadata.json, version, hash, features, sklearn runtime, parameters, dataset identity, test IDs, source/dependency identities и cohort support. Predictor получава repository отвън и зарежда веднъж. API връща само стабилен prediction contract.

## Data и evaluation contract

400 synthetic requests. Features: token_count[1,1000], keyword_score[0,1], previous_requests[0,50]. Request ID е уникален; labels са0/1; cohort A/B е audit marker, не feature. CSV bytes имат committed SHA-256 manifest. Missing values, duplicates, nonnumeric/нефинитни стойности, invalid labels/bounds се отказват.

Train/test split е stratified75/25, seed42. StandardScaler се fit-ва само върху training rows чрез sklearn Pipeline. Held-out F1/recall gates са0.85 в reference tests. Повтаряне на същия run трябва да дава същите split IDs/metrics и близки predictions при същия runtime; serialized bytes и timestamps не са обещани да са идентични. За реален model selection е нужен отделен final holdout и confidence analysis.

## Registry и release state

```text
dataset + config + source + dependencies
                  ↓
      version folder (write once)
                  ↓
         validation/review gate
                  ↓
       champion.json (mutable pointer)
                  ↓ explicit release build
      image ID + embedded model version
                  ↓ explicit deployment
     readiness + version + contract checks
```

Folder publication и pointer replace са atomic local operations; concurrent writers не се поддържат. Pending directories не са released versions. Rollback на pointer и rollback на deployment са различни операции. Deployment embed-ва bundle и работи като non-root user. Old image/bundle трябва да се пази според retention/retirement policy.

## Failure и observability contract

Missing/corrupted/incompatible model оставя live200, ready503 и predict503. Missing key е fail-closed. Invalid body връща generic422 без raw input. Failed inference не се маскира като label0. Model version е в predictions, readiness, metrics и structured events.

Metrics са process-local: request_count брои всички requests след завършване; error_count включва4xx/5xx; prediction latency е server duration за /predict, включително отказите, върху последни1000 samples. `/metrics` response не включва самия незавършен metrics request. Това не е long-term Prometheus histogram и restart reset-ва state. Benchmark измерва client duration sequentially с warmup; двете измервания не са идентични.

## Trust boundaries и trade-offs

Client JSON/key е недоверен input; HTTP schema/auth отделя access от computation. Registry filesystem е доверен local operator boundary; SHA не е authenticity control. Няма uploads, външен model issuer, paid APIs или реални sensitive data. Source/lock/data се пазят в Git; runtime key е environment secret, не source.

Минималният embedded-model API е избран за малък CPU workload и един екип. Separate inference service, MLflow/DVC backend, shared metrics и distributed tracing са разумни разширения при по-голям обхват. Разходите им трябва да са обосновани чрез requirements, не чрез списък от модерни инструменти.
