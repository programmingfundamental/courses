---
title: "Finding — попълва се от студента"
sidebar:
  order: 100
---

# Finding — попълва се от студента

Само контролирана локална среда; synthetic данни.

- **ID / заглавие:**
- **Finding:** наблюдавано нарушение на security invariant, засегнат endpoint/module.
- **Risk:** actor, preconditions, asset, likelihood 1–3 × impact 1–3, обосновка.
- **Evidence:** mode/commit, локален request, expected/actual content/state; без cookies/tokens.
- **Root Cause:** source file/method и нарушено trust assumption.
- **Mitigation:** избраният control и защо адресира причината.
- **Regression Test:** class/method, red evidence преди fix, green след fix, positive case.
- **Residual risk:** ограничения, непроверени assumptions, бъдеща работа.
- **Disposition:** fixed / accepted limitation с аргумент / unresolved.

Не маркирайте hypothesis като потвърдена vulnerability. Scanner output без reproduction/reachability анализ не е достатъчен.
