# SRP - Single Responsibility Principle

### **Единна отговорност**

Този принцип гласи, че **един елемент трябва да има само една отговорност. Освен това тя следва да има само една причина да се промени, тази причина е промяна в изискването, какво се изисква от този елемент да изпълнява.**

**Този принцип помага да и се раздели по-добър софтуер, като ни позволява по лесно да**:

1. **Тестваме** – Клас с една отговорност ще има много по-малко тестови случаи.
2. **По-малко връзки** – По-малко функционалност в един клас ще има по-малко зависимости.
3. **Организация** – По-малките, добре организирани класове са по-лесни за разбиране от монолитните.

Да започнем от следния пример, най-вероятно сте чували принципа: **Една функция трябва да прави едно и само едно нещо.** Ние използваме този принцип, когато рефакторираме (преработка на код, с цел да се подобри неговото качество, без да се промени крайния резултат) големи функции в по-малки функции; използва се на най-ниските нива.&#x20;

Въпреки това, вярвам че се е случвало да пишете подобен код:\


```java
public static void calculateSum() {
     System.out.println("Enter the Two numbers for addtion: ");  
     Scanner readInput = new Scanner(System.in);  
     
     int a = readInput.nextInt();  
     int b = readInput.nextInt();
     int sum = a + b;
     
     System.out.println("The sum of numbers is: "+sum);     
}
```

Така написана функцията, дали може да получава числа от, файл, база данни, фруга функция, сензоро?

Според вас тази функция изпълнява ли повече от едно нещо?

SOLID принципа за единна отговорност (SRP), много прилича на принципа по горе но е и различен. Докато принципа "**Една функция, една отговорност**" е приложим на ниско ниво, по колкото по нагоре се отива толкова по трудно става разпознаването на отговорностите.

Всеки клас може да съдържа повече от един метод или поле, в тази ситуация не може да се приложи директно подхода както при функциите, а трябва да се мисли за средата в която това обединение от методи и променливи ще намери приложение.

Може да се разбере този принцип, като се разгледат симптомите/начините по които се нарушава.&#x20;

СИМПТОМ 1: СЛУЧАЙНО ДУБЛИРАНЕ&#x20;

Като пример може да разгледаме приложение за заплати в което присъства клас Employee,  тои има три метода: calculatePay(), reportHours() и save().

```java
public class Employee {
     public void calculatePay() {
          //код на метода
     }
     public void reportHours() {
          //код на метода
     }
     public void save() {
          //код на метода
     }
}
```

Метода calculatePay() изчислява заплатата необходима за счетоводния отдел

Метода reportHours() е специфичен и се отнася към отдела за човешки ресурси

Метода save() се отнася до администраторите на Базата даннищ

Да предположим, че функцията calculatePay() и reportHours() споделят общ алгоритъм за изчисляване на часове без извънреден труд.&#x20;

Как бихте организирали когда си?

Логично е да поставим този алгоритъм във функция, предимството ще е че няма да се дублира код, с името regularHours()

Обединяването на тези 3 метода в един клас може да доведе до следното:

Свързване на счетоводния отдел, човешки ресурси, администраторите помежду им. Това обединение може да може накара действията на счетоводния отдел да повлияят на нещо, от който отдел човешки ресурси зависи.

Например, нека разгледаме един клас, за да представим една проста книга:

```java
public class Book {

    private String name;
    private String author;
    private String text;

    //constructor, getters and setters
}
```

В този код съхраняваме името, автора и текста, свързани с екземпляр на _Книга_.

Нека сега добавим няколко метода за обработка на текста:

```java
public class Book {

    private String name;
    private String author;
    private String text;

    //constructor, getters and setters

    // methods that directly relate to the book properties
    public String replaceWordInText(String word){
        return text.replaceAll(word, text);
    }

    public boolean isWordInText(String word){
        return text.contains(word);
    }
}
```

Сега нашият клас _Book_ работи добре и можем да съхраняваме толкова книги, колкото ни харесва в нашето приложение.

Но каква полза е съхраняването на информацията, ако не можем да изведем текста в нашата конзола и да я прочетем?

Нека оставим предпазливоста на страна и да добавим метод за печат:

```java
public class Book {
    //...

    void printTextToConsole(){
        // our code for formatting and printing the text
    }
}
```

Този метод ще има достъп до системните ресурси за извеждане, което ще добави нова зависимост в него. Освен да съхранява информацията за книгата, класа знае и къде да я презентира.

Това какво виждате нарушава принципа на единната отговорност, който очертахме по-рано.

За да оправим бъркотията си, трябва да внедрим отделен клас, който се занимава само с отпечатване на нашите текстове:

```java
public class BookPrinter {

    // methods for outputting text
    void printTextToConsole(String text){
        //our code for formatting and printing the text
    }

    void printTextToAnotherMedium(String text){
        // code for writing to any other location..
    }
}
```

Не само сме разработили клас, който обслужва _Book_ на нейните задължения за печат, но и можем да използваме нашия клас _BookPrinter_, за да изпратим текста си на други медии.

Независимо дали става дума за имейл, сеч, или нещо друго, имаме отделен клас, посветен на тази грижа.

**Трикът за внедряване на SRP в нашия софтуер е познаването на отговорността** на всеки клас.

Следвайки принципа SRP, нашите класове ще се придържат към една функционалност. Техните методи и данни ще бъдат загрижени с една ясна цел. Това означава **високо кохерентност,** както и **устойчивост на промени, които заедно намаляват грешките**.

Въпреки че името на принципа е саморазяснително, можем да видим колко лесно е да се приложи неправилно. Уверете се, че ще разграничите отговорността на всеки клас при разработването на проект и обърнете допълнително внимание на кохерентността.

