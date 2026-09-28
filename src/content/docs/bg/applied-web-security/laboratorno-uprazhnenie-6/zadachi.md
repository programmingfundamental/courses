---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

## Самостоятелни задачи

### Задача 1

**Условие:** разширете profile с optional homepage link; unsafe starter в resources поставя стойността директно в href.

**Изисквания:** plain display name да остава безопасен, homepage да допуска само http/https URL и да се рендерира в quoted attribute.

**Ограничения:** не приемайте javascript/data schemes; не използвайте HTML text escaping като единствен URL control; без външни заявки по време на тестовете.

**Критерии за приемане:** автоматизирани tests за quoted attribute breakout, опасна scheme, relative/empty value според описана policy и нормален https URL; browser проверка без marker execution. Не е достатъчно да поправите само comments.

Предайте собствен code diff, test report и кратка аргументация. Не включвайте реални secrets или сурови session/token стойности в evidence.

### Задача 2 — Гранични случаи

- Double encoding при вече съдържащ `&lt;` input.
- Attribute value със затваряща кавичка.
- Опасна URL scheme при правилно escaped attribute.
- CSP блокира exploit, но raw vulnerable sink остава.

Изберете поне един за нов regression test и обяснете кой security invariant защитава.
