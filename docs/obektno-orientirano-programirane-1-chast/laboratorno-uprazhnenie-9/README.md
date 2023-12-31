---
layout: default
title: Лабораторно упражнение 9
parent: Обектно-ориентирано програмиране - 1 част
has_children: true
nav_order: 9
permalink: /docs/obektno-orientirano-programirane-1-chast/laboratorno-uprazhnenie-9
---
# Лабораторно упражнение 9

### Generics в Java

**Generics** означава **параметризирани типове**. Идеята е да се позволи типът (Integer, String, … и т.н., както и потребителски дефинирани типове) да бъде параметър за методи, класове и интерфейси. С помощта на генериците е възможно да се създават класове, които работят с различни типове данни. Клас, интерфейс или метод, който работят с параметризирани типове, своята същност са генерици.

**Object** е суперкласът на всички останали класове. В тази връзка референция към **Object** може да се бъде валидна за който и да е обект**.** Това довежда до липса на типова безопасност.

Нека да разгледаме класа Box, който работи с обекти от всякакъв тип. Той съдържа два метода: set , който присвоява обект към полето, и get, който го извлича:

```
public class Box {
    private Object object;

    public void set(Object object) { this.object = object; }
    public Object get() { return object; }
}

```

Тъй като неговите методи приемат или връщат Object, вие сте свободни да предавате обекти от всякакъв тип, при условие че не е един от примитивните типове. Няма начин да се провери по време на компилиране как се използва класът. Една част от кода може да постави Integer в полето и да очаква да получи Integer от него, докато друга част от кода може погрешно да предаде String , което да доведе до грешка по време на изпълнение. Генериците добавят възможност за избягване на подобен вид несъответствия.
