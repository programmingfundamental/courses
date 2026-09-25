# Източници и версия на контекста

Проверени при подготовката на22.09.2026. Примерите са авторски; pinned dependency API е определено от requirements.lock, а online docs може да показват по-нова версия.

- [scikit-learn: common pitfalls](https://scikit-learn.org/stable/common_pitfalls.html) — data leakage, разделяне на fit/test и Pipeline.
- [scikit-learn: model persistence](https://scikit-learn.org/stable/model_persistence.html) — trusted artifacts и runtime compatibility ограничения.
- [FastAPI: request bodies](https://fastapi.tiangolo.com/tutorial/body/) — Pydantic contracts и request validation.
- [MLflow: backend stores](https://mlflow.org/docs/latest/tracking/backend-stores/) — разграничение metadata/artifact storage; local journal в курса е ограничен functional equivalent.
- [MLflow: self-hosting overview](https://mlflow.org/docs/latest/self-hosting/) — кога централен tracking backend е оправдан.

Термини: lifecycle — жизнен цикъл; requirements — проверими изисквания; inference — използване на обучен модел; lineage — проследим произход; model registry — управление на версии/selection state; observability — извеждане на състоянието от сигнали; technical debt — бъдеща инженерна цена; rollback — възстановяване на предишна допустима версия.
