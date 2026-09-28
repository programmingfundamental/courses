---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

## Самостоятелни задачи

### Задача 1

**Условие:** защитете втория mutation flow — създаване на comment през собствена HTML форма.

**Изисквания:** формата да използва server-provided CSRF token; след login да работи с актуалната session; server отказът да оставя comments count непроменен.

**Ограничения:** без глобално изключване на CSRF/CORS wildcard; token не се поставя в URL; не използвайте cookies като Bearer fallback.

**Критерии за приемане:** legitimate comment → 201; missing, forged и token от друга session → 403; поне един test извлича реален `/csrf` response вместо само `.with(csrf())`; проверка за липса на DB insert при отказ.

Предайте собствен code diff, test report и кратка аргументация. Не включвайте реални secrets или сурови session/token стойности в evidence.

### Задача 2 — Гранични случаи

- Token от предишна session след login/logout.
- Same-site cross-origin request от различен localhost port.
- Secure cookie върху plain HTTP и browser-specific localhost exceptions.
- POST без CSRF връща 403 преди authentication, затова anonymous тестовете за identity използват GET.

Изберете поне един за нов regression test и обяснете кой security invariant защитава.
