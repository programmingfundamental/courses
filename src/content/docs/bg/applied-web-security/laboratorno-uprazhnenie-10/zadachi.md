---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

## Самостоятелни задачи

### Задача 1

**Условие:** направете mini security assessment на registration + profile модула и разширете audit events за него.

**Изисквания:** предайте Finding, Risk, Evidence, Root Cause, Mitigation, Regression Test за минимум 3 обосновани observations; разграничете потвърден finding от limitation/hypothesis. Добавете безопасен event type и автоматизиран no-secret-in-logs test.

**Ограничения:** само локални synthetic данни; без general request/body logging; без изтриване на existing tests или смяна на vulnerable mode като fix.

**Критерии за приемане:** поне един нов regression test извън готовата suite; positive functionality test; correlation между event и request без raw session/token; остатъчен риск и приоритет за всяко observation.

Предайте собствен code diff, test report и кратка аргументация. Не включвайте реални secrets или сурови session/token стойности в evidence.

### Задача 2 — Гранични случаи

- Green тест срещу грешния profile/base URL.
- Случайно skip-нат Docker integration test.
- Debug logging разкрива request parameters.
- Control работи директно към app, но proxy configuration го променя.

Изберете поне един за нов regression test и обяснете кой security invariant защитава.
