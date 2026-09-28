---
title: Задачи
taskPage: true
sidebar:
  label: Задачи
  order: 100
---

## Самостоятелни задачи

### Задача 1

**Условие:** оценете модула registration преди бъдещо публично използване.

**Изисквания:** DFD, assets, entry points, trust boundaries, минимум 5 конкретни threats, likelihood/impact и mitigation/test за всяка.

**Ограничения:** анализирайте само предоставения код; не добавяйте реален mail provider и не изпращайте заявки навън.

**Критерии за приемане:** всяка threat има actor/precondition и връзка към code/config; поне една е abuse/availability, една е privilege escalation и една е information disclosure. Добавете един автоматизиран security test по избраната threat; не предавайте готовия register от общата архитектура.

Предайте собствен code diff, test report и кратка аргументация. Не включвайте реални secrets или сурови session/token стойности в evidence.

### Задача 2 — Гранични случаи

- Публикуване на IPv6 wildcard вместо IPv4 loopback.
- Nginx е спрян, но директният app port остава публикуван.
- User създава profile с role параметър, който server не трябва да приема.

Изберете поне един за нов regression test и обяснете кой security invariant защитава.
