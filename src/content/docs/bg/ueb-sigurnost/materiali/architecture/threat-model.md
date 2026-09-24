---
title: "Начален threat model — работен документ"
sidebar:
  order: 100
---

# Начален threat model — работен документ

Само локалната лаборатория. Threat означава потенциално нежелано събитие; vulnerability е слабост, която го допуска; exploit е конкретното използване на слабостта; risk съчетава вероятност и въздействие; mitigation е control, който намалява риска. Asset е нещо с ценност за защитаване, а attack surface — всички достъпни входове и взаимодействия.

## Scope и assumptions

Защитаваме synthetic profiles/documents, password hashes, sessions, signing/AES keys, availability и audit integrity. Threat actors: anonymous local client, нормален потребител със собствена сесия, потребител с възможност да подава comments, оператор с неправилна конфигурация. Host administrator е извън приложния boundary и може да чете памет/дискове. SQL role не компенсира authorization bug в service.

Data flows и TB1–TB4 са в [system-overview](/courses/bg/ueb-sigurnost/materiali/architecture/system-overview/). Разширете схемата с attacker capabilities и мястото на control-а; не маркирайте всяка Java функция като отделен trust boundary.

## STRIDE и начален register

STRIDE: Spoofing (представяне за друг), Tampering (неразрешена промяна), Repudiation (оспорване на действие), Information disclosure (разкриване), Denial of service (отказ на услуга), Elevation of privilege (повишаване на права).

| ID | STRIDE / flow | Threat и нарушено assumption | Control | Доказателство |
|---|---|---|---|---|
| T01 | Spoofing / login | позната/позната по hash парола води до identity takeover | BCrypt и ограничение на опитите | lab02/lab04 tests |
| T02 | Disclosure / document | ID се приема за разрешение | server ownership policy | owner/non-owner матрица |
| T03 | Tampering / search | input се интерпретира като SQL | bind parameters | malicious query → 0 results |
| T04 | Elevation / comments | text става script в чужда session | context encoding и CSP | raw/encoded response + browser |
| T05 | Tampering / profile | cookie доказва намерение за действие | CSRF token | липсващ token → 403 и без update |
| T06 | Disclosure / DB | read-only dump разкрива identifier | authenticated encryption, отделен key | DB ciphertext и tamper rejection |
| T07 | Spoofing / JWT | payload се приема без signature | pinned algorithm/key + claims | modified token → 401 |
| T08 | DoS / login | lockout позволява спиране на чужд account | временен лимит, layered throttling | expiration и residual-risk анализ |
| T09 | Repudiation / logs | липсва връзка между request и отказ | correlation/event policy | audit regression |

Таблицата е начален пример, не решение на самостоятелния модул registration. За всеки нов finding оценете likelihood 1–3 и impact 1–3, risk = произведението; 1–2 нисък, 3–4 среден, 6–9 висок. Това е учебна скала, не CVSS. Опишете capability/precondition; локалният достъп намалява reachability, но не поправя root cause. След mitigation оценете остатъчния риск с аргумент.

OWASP Top 10 служи като карта за класове проблеми, не като пълна test plan. При цитиране посочвайте изданието; курсът не изисква запомняне на номерата на категориите.
