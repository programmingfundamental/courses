---
layout: default
title: Lombok
parent: Лабораторно упражнение 7
grand_parent: Интернет технологии
nav_order: 3
---

# Lombok

Проектът Lombok е базирана на анотации Java библиотека, която ви позволява да намалите шаблонния код. Lombok предлага различни анотации, насочени към замяна на Java код, който е шаблонен, повтарящ се или досаден за писане. Като използвате Lombok можете да избегнете писането на конструктори без аргументи, `toString()`, `equals()` и `hashCode()` методи като добавите няколко анотации. По време на компилиране  библиотеката инжектира байт кода, представляващ желания шаблонен код в _.class_ файловете. 

Добавяне на библиотеката:

```
compileOnly 'org.projectlombok:lombok'
annotationProcessor 'org.projectlombok:lombok'
```

### Най-често използвани анотации

#### @Getter, @Setter

Когато дадено поле е анотирано с `@Getter` и/или `@Setter`, Lombok автоматично генерира съответно гетър и/или сетър по подразбиране. Генерираният метод по подразбиране ще бъде public, освен ако не е зададен `AccessLevel`. Възможните опции за последния са `PUBLIC, PROTECTED, PACKAGE и PRIVATE`.

```java
public class Author {
    private int id;

// Библиотеката ще създаде методи за достъп и модификация на полето name
    @Getter @Setter	
    private String name;

// Библиотеката ще създаде setter метод с модификатор за достъп protected
    @Setter(AccessLevel.PROTECTED)
    private String surname;
}
```

С анотациите `@Getter` и `@Setter` можете да коментирате и целия клас. В този случай логиката ще бъде приложена към всяко поле на класа. Можете ръчно да деактивирате генерирането на getter/setter за дадено поле, като използвате опцията `AccessLevel.NONE`.

```java
@Getter
@Setter
public class Author {
    private int id;
    private String name;
    private String surname;
} 
```

#### @NoArgsConstructor, @RequiredArgsConstructor,@AllArgsConstructor

Когато даден клас е анотиран с `@NoArgsConstructor`, Lombok автоматично генерира конструктор без параметри. По същия начин, когато се анотира с `@AllArgsConstructor`, ще се генерира конструктор с параметри за всяко поле на класа.

`@RequiredArgsConstructor` води до конструктор с параметър за всяко поле, което изисква специална обработка - неинициализирани final полета, както и всички полета, маркирани като `@NonNull`,но неинициализирани на мястото на тяхната декларация.

Статичните полета ще бъдат игнорирани от тези анотации.

```java
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Author {
    private int id;
    private String name;
    private String surname;
    private final String birthPlace;
}
```

#### @ToString

Ако даден клас е анотиран с `@ToString`, Lombok ще се погрижи за генерирането на метода. По подразбиране ще бъде върнат низ, съдържащ името на класа, последвано от стойностите на всяко поле, разделени със запетая. Ако зададете стойност на параметъра `includeFieldNames` true, името на всяко поле ще бъде поставено преди неговата стойност. По подразбиране всички нестатични полета ще бъдат взети предвид при генерирането на метода. Анотирайте поле с `@ToString.Exclude`, ако искате Lombok да го игнорира.

```java
@ToString(includeFieldNames=true)
public class Author {
    private int id;
    private String name;
    private String surname;
}
```

#### @EqualsAndHashCode

Анотирайте клас с `@EqualsAndHashCode` и Lombok автоматично ще създаде методите. По подразбиране всички нестатични, нетранзитивни полета ще бъдат взети под внимание. Можете да промените кои полета да се използват, като ги анотирате с `@EqualsAndHashCode.Include` или `@EqualsAndHashCode.Exclude`. Като алтернатива можете да анотирате своя клас с `@EqualsAndHashCode(onlyExplicitlyIncluded=true)` и след това да посочите точно кои полета или методи искате да се използват, като ги анотирате с `@EqualsAndHashCode.Include`.

```java
@Getter
@Setter
@EqualsAndHashCode
public class Author {
    private int id;
    private String name;
    private String surname;
}
```

#### @NonNull

Можете да анотирате със `@NonNull` поле, параметър на метод или цял конструктор. По този начин Lombok ще генерира null проверки за съответния компонент.

```java
public class Author {
    private int id;
    private String name;
    private String surname;

    public Author(
      @NonNull int id,
      @NonNull String name,
      String surname
    ) {
      this.id = id;
      this.name = name;
      this.surname = surname; 
  }
}
```

Без библиотеката Lombok кодът би имал следния вид:

```java
public class Author {
    private int id;
    private String name;
    private String surname;

    public Author(
      int id,
      String name,
      String surname
    ) {
        if (id == null) {
          throw new NullPointerException("id cannot be null");
        }
        this.id = id;
        if (name == null) {
          throw new NullPointerException("name cannot be null");
        }
        this.name = name;
        this.surname = surname; 
  }
}
```

#### @Data

`@Data` е анотация, която събира в едно цяло анотациите [`@ToString`](https://translate.google.com/website?sl=en\&tl=bg\&hl=en\&client=webapp\&u=https://projectlombok.org/features/ToString), [`@Getter`](https://translate.google.com/website?sl=en\&tl=bg\&hl=en\&client=webapp\&u=https://projectlombok.org/features/GetterSetter), [`@Setter`](https://translate.google.com/website?sl=en\&tl=bg\&hl=en\&client=webapp\&u=https://projectlombok.org/features/GetterSetter), [`@EqualsAndHashCode`](https://translate.google.com/website?sl=en\&tl=bg\&hl=en\&client=webapp\&u=https://projectlombok.org/features/EqualsAndHashCode) и [`@RequiredArgsConstructor`](https://translate.google.com/website?sl=en\&tl=bg\&hl=en\&client=webapp\&u=https://projectlombok.org/features/constructor). По този начин генерира всички шаблони, необходими за POJO клас.  А именно: гетъри за всички полета, сетъри за всички non-final полета, имплементации на `toString`, `equals` и `hashCode`, включващи всички полета от класа и конструктор за всички полета, изискващи специална обработка.

```java
@Data
public class Author {
    private final int id;
    private String name;
    private String surname;
}
```
