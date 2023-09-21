# OCP - Open-Closed Principle

**Както подсказва името, този принцип гласи, че софтуерните обекти трябва да бъдат отворени за разширение, но затворени за модификация.** В резултат на това, когато бизнес изискванията се променят (условието на задачата се надгради) тогава обектът може да бъде разширен, но не и модифициран.

Нека да разгледаме следния пример:

```
public class Guitar {
    private String make;
    private String model;
    private int volume;

    //Constructors, getters & setters
}
```

Започваме с класическа итара, но след някоплко месеца решаваме, че една китара брандирана с пламаци ще изглежда по-рокендрол

В този момент може да е съблазнително просто да отворим класа _"Китара_" и да добавим поле за пламъка — но кой знае какви грешки може да доведе това в нашия клас.

Вместо това нека **се придържаме към принципа на отворените за разширение класове и просто да разширим класа си **_**Китара**_:

```
public class SuperCoolGuitarWithFlames extends Guitar {

    private String flameColor;
 
    //constructor, getters + setters
}
```

Разширявайки класа Китара, сме сигурни, досегашното ни приложениое няма да бъде засегнато, а това за което трябва да мислим е само новата функционалност.

**Ще разгледаме как интерфейсите са един от начините да следвате на този принцип**

### **1.** Несъответствие

**Нека разгледаме изграждането на приложение за калкулатор, което може да има няколко операции, като например добавяне и изваждане.**

На първо място, ще определим интерфейс от най-високо ниво – _CalculatorOperation_:

```
public interface CalculatorOperation {}
```

**Нека създадем клас **_**"Добавяне**_**", който би събрал две числа и би разшерил **_**CalculatorOperation**_**:**

```java
public class Addition implements CalculatorOperation {
    private double left;
    private double right;
    private double result = 0.0;

    public Addition(double left, double right) {
        this.left = left;
        this.right = right;
    }

    // getters and setters

}
```

От сега имаме само един клас _Добавяне,_ така че трябва да определим друг клас на име _Изваждане_:

```java
public class Subtraction implements CalculatorOperation {
    private double left;
    private double right;
    private double result = 0.0;

    public Subtraction(double left, double right) {
        this.left = left;
        this.right = right;
    }

    // getters and setters
}
```

**Нека сега определим нашия основен клас, който ще изпълни нашите калкулаторни операции:**

```java
public class Calculator {

    public void calculate(CalculatorOperation operation) {
        if (operation == null) {
            throw new InvalidParameterException("Can not perform operation");
        }

        if (operation instanceof Addition) {
            Addition addition = (Addition) operation;
            addition.setResult(addition.getLeft() + addition.getRight());
        } else if (operation instanceof Subtraction) {
            Subtraction subtraction = (Subtraction) operation;
            subtraction.setResult(subtraction.getLeft() - subtraction.getRight());
        }
    }
}
```

**Въпреки че това може да изглежда добре, това не е добър пример за OCP.** Когато влезе ново изискване за добавяне на умножение или функционалност за разделяне, нямаме начин освен да променим метода на _изчисляване_ на клас _Калкулатор_.

**Оттук можем да кажем, че този код не е съвместим с OCP.**

### 2. Съвместими с OCP

Както видяхме нашето приложение за калкулатор все още не е съвместимо с OCP. Кодът в метода _на изчисляване_ ще се промени с всяко входящо ново искане за поддръжка на операция. Така че, трябва да извлечем този код и да го сложим в абстракционен слой.

Едно решение е всяка операция да бъде делегирана в съответния им клас:

```java
public interface CalculatorOperation {
    void perform();
}
```

**В резултат на това класът **_**"Добавяне"**_** би могъл да внедри логиката на добавяне на два номера:**

```java
public class Addition implements CalculatorOperation {
    private double left;
    private double right;
    private double result;

    // constructor, getters and setters

    @Override
    public void perform() {
        result = left + right;
    }
}
```

По същия начин актуализиран клас _на Изваждане_ би имал подобна логика. И подобно на _Добавяне_ и _Изваждане_, като настъпи нова задача за промяна, бихме могли да внедрим логиката на _разделението_:

```java
public class Division implements CalculatorOperation {
    private double left;
    private double right;
    private double result;

    // constructor, getters and setters
    @Override
    public void perform() {
        if (right != 0) {
            result = left / right;
        }
    }
}
```

**И накрая, нашият клас **_**Калкулатор**_** няма нужда да прилага нова логика, когато въвеждаме нови оператори:**

```java
public class Calculator {

    public void calculate(CalculatorOperation operation) {
        if (operation == null) {
            throw new InvalidParameterException("Cannot perform operation");
        }
        operation.perform();
    }
}
```

Така класът е _затворен_ за модификация, но _отворен_ за разширение.

### 3. Заключение

Принципа на отворени за разширение и затворени за модификация обекти, подобрава работата върху проектите, като намалява грешките, който могат да възникнат при модифициране на класове и улеснява тестването като капсолира вече съществуващата логика и предоставя възможност за концентрация върху новата логика на приложениет.
