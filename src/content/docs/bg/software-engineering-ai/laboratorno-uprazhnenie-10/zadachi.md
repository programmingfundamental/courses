---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

## 9. Водена практическа задача

Командите за Python/pytest са от `ai-platform` при активирана среда. Процесът е **проблем → теория → анализ на лошо решение → практическа задача → самостоятелна задача → тестове → инженерна дискусия**.

### Стъпка 1

Прочетете starter/review.json; това е inert fixture, не реален compromised service. Класифицирайте поне6 problems като security/privacy/dependency/model/data debt.

### Стъпка 2

Проследете app trust boundaries: API key, schema, model repository, filesystem permissions, CI secrets. Обяснете защо joblib не трябва да приема uploads от users и защо SHA проверката е само integrity срещу случайна промяна при trusted metadata.

### Стъпка 3

Създайте findings с Risk, Impact, Technical Debt, Mitigation, Priority и Owner. За всеки добавете acceptance evidence, due date и residual risk. Не измисляйте CVE за фиктивния deprecated adapter.

### Стъпка 4

Реализирайте поне два controls/tests: например fail-closed missing key, sensitive input не се echo-ва/log-ва, stale model metadata gate. Сравнете с baseline и покажете red→green за собствената поправка.

### Стъпка 5

Напишете кратък model/data card: intended use, prohibited use, dataset origin, evaluation protocol, slice support, limitations, retention и retirement owner. Превърнете поне едно ethical constraint в автоматична проверка и обяснете какво остава за human review.

Време: сценарий/теория15, анализ10, guided работа30, checkpoint5, самостоятелна работа25, tests15, дискусия10 минути — общо110. При 90 минути преподавателят подготвя environment и baseline evidence предварително.

## 10. Checkpoint

Има приоритизиран risk/debt register и доказани controls; model/data limitations и maintenance ownership са explicit.

Покажете working increment и кратък before/after diff. Ако има failure, класифицирайте го като environment, contract, quality или implementation проблем. Не преминавайте нататък само заради един green happy-path test.

## 11. Самостоятелна задача

**Problem statement:** планирайте поддръжката и retirement на версия, която вече не покрива product requirements.

**Requirements:** минимум 5 проблема с Risk/Impact/Technical Debt/Mitigation/Priority/Owner; добавете deprecation deadline, migration path, artifact/data retention и rollback boundary.

**Constraints:** не използвайте реални PII/secrets; не твърдете fairness само от synthetic accuracy; не изтривайте незаменими audit/reproducibility evidence без policy.

**Acceptance criteria:** поне2 automated acceptance checks, всеки проблем има owner и измерим completion criterion; retirement test показва, че retired version не се deploy-ва, докато approved version работи. Документирайте human-review decisions.

Предайте собствена реализация/спецификация, rationale и evidence. Пълно решение не е включено тук; готовият общ проект е reference baseline за сравнение на contracts, а starter TODO задачите изискват ваш diff и допълнителни проверки.

## 12. Automated tests

Начални runnable проверки:

```bash
pytest tests/test_api.py tests/test_repository.py -q
python -m pip check
```

Новите tests трябва да проверяват observable contract, negative/edge behavior и разрешения нормален flow. За документните задачи автоматизирайте структурните invariants, а смисъла проверете с peer review. За statistical/performance проверки запишете dataset/workload/seed/version/sample count; не твърдете универсална гаранция от малка synthetic извадка.

Добавете test/evidence traceability: **requirement ID → test name → command → actual result → limitation**. Поне една собствена проверка трябва да открива deliberate bad fixture или regression. След restore повторете suite; не променяйте assertions, за да прикриете failure.

## 13. Edge cases

- Secret е махнат от последния commit, но остава в Git history.
- Artifact е стар, но дата сама по себе си не доказва непригодност.
- Slice има твърде малко positive labels за надежден recall.
- Rollback връща версия, която вече е забранена по policy.

Изберете поне един за нов автоматизиран test; за останалите опишете expected behavior и owner.

## 14. Въпроси за анализ

1. Защо checksum не обезопасява pickle?
2. Какво следва след изтекъл real secret?
3. Как ethical requirement става проверим?
4. Защо възрастта не е единствен retirement критерий?
5. Как измерваме debt?
6. Кога rollback е забранен?

## 15. Очакван резултат

Завършен engineering increment по **Сигурност, етика, технически дълг и поддръжка**, checkpoint evidence, самостоятелната задача според acceptance criteria и нови automated checks. Предайте decision/trade-off analysis, а не само screenshot или model score. Данните, моделът и test environment трябва да са идентифицируеми.

## 16. Checklist

- [ ] Анализирах проблемния starter и записах failure scenario.
- [ ] Избрах архитектурно/процесно решение с trade-offs.
- [ ] Реализирах guided increment и checkpoint.
- [ ] Самостоятелната задача е различна и покрива acceptance criteria.
- [ ] Tests покриват negative/edge и positive behavior.
- [ ] Evidence включва data/model/code/environment identity.
- [ ] Не включих реални secrets/PII или платени external dependencies.
- [ ] Описах limitations, technical debt и следваща стъпка.
