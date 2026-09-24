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

От ai-platform изпълнете `python scripts/check_notebook.py`. Отворете starter/experiment.ipynb в Jupyter при желание. Notebook-ът работи върху 400 synthetic rows, но няма отделна validation оценка; не го оптимизирайте като ML алгоритъм.

### Стъпка 2

Създайте inventory на code/config/data/runtime assumptions. За всеки проблем посочете конкретна клетка, failure scenario и засегнат stakeholder. Разграничете engineering defect от липса на научен резултат.

### Стъпка 3

Превърнете минимум 8 наблюдения в backlog items с acceptance test и dependency. Приоритизирайте reproducible run, contract и artifact identity преди графичен интерфейс.

### Стъпка 4

Планирайте три increments: reproducible offline pipeline, tested inference API, operated release. За всеки задайте вход, проверим output, owner и риск. Направете diagram requirements → validation → release feedback.

### Стъпка 5

Добавете малък machine-readable DoD checklist и test, който открива липсващ dataset/version/test-report item. Сравнете успешното изпълнение на notebook с доказателство за качествен продукт.

Време: сценарий/теория15, анализ10, guided работа30, checkpoint5, самостоятелна работа25, tests15, дискусия10 минути — общо110. При 90 минути преподавателят подготвя environment и baseline evidence предварително.

## 10. Checkpoint

Notebook cells работят от чист контекст; backlog съдържа поне 8 проследими items и три milestones с measurable exit criteria.

Покажете working increment и кратък before/after diff. Ако има failure, класифицирайте го като environment, contract, quality или implementation проблем. Не преминавайте нататък само заради един green happy-path test.

## 11. Самостоятелна задача

**Problem statement:** продуктът трябва да се предаде на втори екип след шест седмици.

**Requirements:** създайте project roadmap, DoD и engineering workflow с branch/review/test/release/rollback стъпки; дефинирайте поне един feedback loop от наблюдение към ново requirement.

**Constraints:** максимум 2 разработчици, CPU лаптоп, без облачен бюджет; не решавайте задачата с добавяне на microservices.

**Acceptance criteria:** всяка седмица има проверим increment; рисковете имат owner; roadmap съдържа maintenance/retirement; един автоматизиран test валидира задължителните DoD fields.

Предайте собствена реализация/спецификация, rationale и evidence. Пълно решение не е включено тук; готовият общ проект е reference baseline за сравнение на contracts, а starter TODO задачите изискват ваш diff и допълнителни проверки.

## 12. Automated tests

Начални runnable проверки:

```bash
python scripts/check_notebook.py
pytest tests/test_data.py -q
```

Новите tests трябва да проверяват observable contract, negative/edge behavior и разрешения нормален flow. За документните задачи автоматизирайте структурните invariants, а смисъла проверете с peer review. За statistical/performance проверки запишете dataset/workload/seed/version/sample count; не твърдете универсална гаранция от малка synthetic извадка.

Добавете test/evidence traceability: **requirement ID → test name → command → actual result → limitation**. Поне една собствена проверка трябва да открива deliberate bad fixture или regression. След restore повторете suite; не променяйте assertions, за да прикриете failure.

## 13. Edge cases

- Notebook клетки се изпълняват в различен ред.
- Dataset е сменен без промяна на filename.
- Seed е фиксиран, но dependency version е различна.

Изберете поне един за нов автоматизиран test; за останалите опишете expected behavior и owner.

## 14. Въпроси за анализ

1. Защо training score не е DoD?
2. Как lifecycle се различава от pipeline?
3. Кога technical debt е приемлив?
4. Кои identities са нужни за reproducibility?
5. Защо deployment не е краят?
6. Какво е vertical slice?

## 15. Очакван резултат

Завършен engineering increment по **Софтуерен жизнен цикъл и инженерни процеси**, checkpoint evidence, самостоятелната задача според acceptance criteria и нови automated checks. Предайте decision/trade-off analysis, а не само screenshot или model score. Данните, моделът и test environment трябва да са идентифицируеми.

## 16. Checklist

- [ ] Анализирах проблемния starter и записах failure scenario.
- [ ] Избрах архитектурно/процесно решение с trade-offs.
- [ ] Реализирах guided increment и checkpoint.
- [ ] Самостоятелната задача е различна и покрива acceptance criteria.
- [ ] Tests покриват negative/edge и positive behavior.
- [ ] Evidence включва data/model/code/environment identity.
- [ ] Не включих реални secrets/PII или платени external dependencies.
- [ ] Описах limitations, technical debt и следваща стъпка.
