# Сравняване на

Когато присвояваме целочислена стойност на целочислен обект, стойността се автоматично boxing в целочислен обект. Например твърдението "Integer x = 10" създава обект "x" със стойност 10.

Но какъв ще е изхода от следния пример:

{% tabs %}
{% tab title="Пример" %}
```
public class Application {
    public static void main(String args[]) {
         Integer x = 400, y = 400;
         if (x == y)
            System.out.println("Едни и същи");
         else
            System.out.println("Не са едни и същи");
    }
}
```
{% endtab %}

{% tab title="Second Tab" %}
```
Не са едни и същи
```

{% hint style="info" %}
Тъй като x и y се отнасят до различни обекти, получаваме изхода като "Не са едни и същи"
{% endhint %}
{% endtab %}
{% endtabs %}

Но какъв може да се обясни изхода от следния пример:

{% tabs %}
{% tab title="Пример" %}
```
public class Application {
    public static void main(String args[]) {
         Integer x = 40, y = 40;
         if (x == y)
            System.out.println("Едни и същи");
         else
            System.out.println("Не са едни и същи");
    }
}
```
{% endtab %}

{% tab title="Резултат" %}
```
Едни и същи
```

{% hint style="info" %}
В Java стойностите от -128 до 127 се кешират, така че същите обекти се връщат. Използването на valueOf() използва кеширани обекти, ако стойността е между -128 до 127.
{% endhint %}
{% endtab %}
{% endtabs %}

Ако изрично създадем нови обекти с помощта на оператор new, получаваме изхода като "Не са едни и същи". Вижте следната Java програма където не се използва valueOf().

{% tabs %}
{% tab title="Пример" %}
```
public class Application {
    public static void main (String args[]) {
          Integer x = new Integer(40), y = new Integer(40);
         if (x == y)
            System.out.println("Едни и същи");
         else
            System.out.println("Не са едни и същи");
    }
}
```
{% endtab %}

{% tab title="Резултат" %}
```
Не са едни и същи
```
{% endtab %}
{% endtabs %}

С получената информация до тук, какъв ще е изхода от следния пример, защо?

{% tabs %}
{% tab title="Пример" %}
```
public class Application {
    public static void main(String[] args) {
          Integer X = new Integer(10);
          Integer Y = 10;
          
          System.out.println(X == Y);
    }
}
```
{% endtab %}

{% tab title="Second Tab" %}
```
false
```

{% hint style="info" %}
Тук ще бъдат създадени два обекта. Първият обект, който е насочен от X поради използването оператор new и втори обект, ще бъде създаден заради autoboxing.
{% endhint %}
{% endtab %}
{% endtabs %}

\
