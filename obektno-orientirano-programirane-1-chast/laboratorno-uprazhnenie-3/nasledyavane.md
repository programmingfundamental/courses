# Наследяване

**Наследяването** е един от основните принципи на ООП. То представлява механизъм, чрез който един клас може да наследи състоянието (полетата) и поведението (методите) на друг (родителски) клас.&#x20;

Чрез наследяване могат да се преизползват атрибутите и методите от родителския клас, когато създаваме нашия клас – наследник ( ако са с модификатор за достъп различен от private).

В Java един клас може да има само един родителски клас.

Родителският клас НЕ разполага с променливите и методите на дъщерния клас, независимо от модификаторите им за достъп.

Super/parent class– родителски клас, неговите атрибути и методи могат да се преизпозлват.

Sub/child class– клас наследник. Той приема всички характеристики на родителския клас и може да добави собствени атрибути. Също така приема поведението на родителския клас и може да добави ново поведение.

{% code title="Anima.java" lineNumbers="true" %}
```java
class Animal { 
    void eat(){
        System.out.println("eating...");
    } 
} 
```
{% endcode %}

{% code title="Dog.java" lineNumbers="true" %}
```java
class Dog extends Animal jav{ 
    void bark(){
    System.out.println("barking...");
    } 
}
```
{% endcode %}

<pre class="language-java" data-title="BabyDog.java" data-line-numbers><code class="lang-java">class BabyDog extends Dog { 
    void weep(){
<strong>        System.out.println("weeping...");
</strong>        } 
}
</code></pre>

{% code title="Main.java" lineNumbers="true" %}
```java
class Main { 
    public static void main(String args[]){ 
        BabyDog d=new BabyDog(); 
        d.weep(); 
        d.bark(); 
        d.eat(); 
    }
} 
```
{% endcode %}
