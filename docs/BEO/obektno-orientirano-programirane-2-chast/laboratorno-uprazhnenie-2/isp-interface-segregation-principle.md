---
layout: default
title: ISP - Interface Segregation Principle
parent: Лабораторно упражнение 2
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 4
---

# ISP - Interface Segregation Principle

Интерфейсна сегрегация означава, че **по-големите интерфейси трябва да бъдат разделени на по-малки. Като правим това, можем да гарантираме, че класовете по изпълнение трябва само да бъдат загрижени за методите, които представляват интерес за тях.**

За този пример ще работим заграждение за мечки в зоопарк.

Нека започнем с интерфейс, който дефинира поведението на пазителите на мечки:

```java
public interface BearKeeper {
    void washTheBear();
    void feedTheBear();
    void petTheBear();
}
```

Тук в примера методите за хранене на мечка, чистене и милване на домашна мечка са на едно и също място, но милването на възрастна мечка може да не е толкова риятно. И по този начин интерфейсът ни представя много отговорности, това означава че интерфейсът е твърде голям.

Нека **оправим това, като разделим големия си интерфейс на три отделни**:

```java
public interface BearCleaner {
    void washTheBear();
}

public interface BearFeeder {
    void feedTheBear();
}

public interface BearPetter {
    void petTheBear();
}
```

Сега, благодарение на интерфейсната сегрегация, ние сме свободни да внедрим само методите, които са важни за нас:

```java
public class BearCarer implements BearCleaner, BearFeeder {

    public void washTheBear() {
        //I think we missed a spot...
    }

    public void feedTheBear() {
        //Tuna Tuesdays...
    }
}
```

И накрая, можем да оставим опасните неща на безразсъдните хора:

```java
public class CrazyPerson implements BearPetter {

    public void petTheBear() {
        //Good luck with that!
    }
}
```

