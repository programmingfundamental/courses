---
layout: default
title: Singleton
parent: Лабораторно упражнение 3
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 2
---

# Singleton

Singleton е шаблон за проектиране, който спада към групата на шаблоните за създаване.

Шаблонът решава два проблема (въпреки че нарушава SRP):

\-          Гарантира, че съществува само една инстанция на обекта – което е от значение при използване на споделени ресурси, например файл или база от данни;

\-          Осигурява глобален достъп до тази инстанция – позволява достъп до този обект от всяка една част на кода и предпазва от презаписването му. По този начин, решението на даден проблем е „заключено” в един-единствен клас, а не се среща на различни места, което се оказва много полезно в случай, че останалият код зависи от него.

Всички имплементации на Singleton трябва да следват две изисквания: да имат частен конструктор по подразбиране и да имат статичен метод, който да играе ролята на конструктор. Всъщност този метод извиква частния конструктор, създава обекта и го запазва в статично поле. По този начин всички обръщения към този метод връщат кеширания обект.

Примерна имплементация би изглеждала по следния начин:

```
public class SingletonClass {
    // статично поле за обекта
    private static SingletonClass singletonInstance;
    // частен конструтор по подразбиране
    private SingletonClass() {}
    // публичен статичен метод
    public static SingletonClass getInstance() {
        if (singletonInstance == null) {
            singletonInstance = new SingletonClass();
        }
        return singletonInstance;
    }
    public String printClass() {
        return "Instance of SingletonClass";
    }
}

public class Main {
    public static void main(String[] args) {
        SingletonClass singletonClass = SingletonClass.getInstance();
        System.out.println(singletonClass.printClass());
    }
}
```

Ако желаем да се убедим, че всеки път се извиква един и същи обект, бихме могли да добавим извеждане на съобщение в метода getInstance() (само с илюстративна цел, System.out.println() няма място в логиката на кода):

```
public static SingletonClass getInstance() {
    if (singletonInstance == null) {
        singletonInstance = new SingletonClass();
        System.out.println("Creating instance"); 
    }
    return singletonInstance;
}

public class Main {
    public static void main(String[] args) {
        SingletonClass singletonClass = SingletonClass.getInstance();
        System.out.println(singletonClass.printClass());
        SingletonClass anotherSingletonClass = SingletonClass.getInstance();
        System.out.println(anotherSingletonClass.printClass());
        SingletonClass oneMoreSingletonClass = SingletonClass.getInstance();
        System.out.println(oneMoreSingletonClass.printClass());
    }
}
```

Шаблонът се използва ако задачата/проблема изисква единствена инстанция, която да е достъпна за всички клиенти (както беше посочено по-горе, единствен обект за дадена база от данни, който се използва от различни програмни фрагменти).

Често този шаблон се използва в комбинация с шаблони като AbstractFactory, Factory, Builder, Facade и други.

Освен това Singleton се използва за логване, драйверни обекти и при т.нар пул от нишки.
