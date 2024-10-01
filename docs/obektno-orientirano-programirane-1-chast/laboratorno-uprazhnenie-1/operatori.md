---
layout: default
title: Оператори
parent: Лабораторно упражнение 1
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 6
---

# Оператори

Операторът е знак, който **представлява действие**, например + е аритметичен оператор за добавяне.

### 1) Основни аритметични оператори

Основни аритметични оператори са: \
**+** е за събиране.\
**–** е за изваждане.\
**\*** е за умножение.\
**/** е за разделяне.\
**%** е за модуло деление.\
Забележка: Операторът % връща остатъка, например 10 % 5 би върнал 0

#### Пример за Аритметични оператори

```
public class ArithmeticOperatorDemo {
   public static void main(String[] args) {
      int num1 = 100;
      int num2 = 20;

      System.out.println("num1 + num2: " + (num1 + num2) );
      System.out.println("num1 - num2: " + (num1 - num2) );
      System.out.println("num1 * num2: " + (num1 * num2) ); 
      System.out.println("num1 / num2: " + (num1 / num2) );
      System.out.println("num1 % num2: " + (num1 % num2) );
   }
}
```

**Изход:**

```
num1 + num2: 120
num1 - num2: 80
num1 * num2: 2000
num1 / num2: 5
num1 % num2: 0
```

### 2) Оператори за присвояване

Оператори за присвояване =, +=, -=, \*=, /=, %=\
**num2 = num1** ще присвои стойността на num1 на num2

**num2+=num1** съответства на num2 = num2+num1

**num2-=num1** съответства на num2 = num2-num1

**num2\*=num1** съответства на num2 = num2\*num1

**num2/=num1** съответства на num2 = num2/num1

**num2%=num1** съответства на num2 = num2%num1

#### Пример:

```
public class AssignmentOperatorDemo {
   public static void main(String[] args) {
      int num1 = 10;
      int num2 = 20;

      num2 = num1;
      System.out.println("= Output: "+num2);

      num2 += num1;
      System.out.println("+= Output: "+num2);
	      
      num2 -= num1;
      System.out.println("-= Output: "+num2);
	      
      num2 *= num1;
      System.out.println("*= Output: "+num2);
	      
      num2 /= num1;
      System.out.println("/= Output: "+num2);
	      
      num2 %= num1;
      System.out.println("%= Output: "+num2);
   }
}
```

**Output:**

```
= Output: 10
+= Output: 20
-= Output: 10
*= Output: 100
/= Output: 10
%= Output: 0
```

### 3) Автоматично увеличаване или намаляване

**num++** съответства на `num=num+1;`

**num–-** съответства на `num=num-1;`

#### Пример:

```
public class AutoOperatorDemo {
   public static void main(String[] args){
      int num1=100;
      int num2=200;
      num1++;
      num2--;
      System.out.println("num1++ is: "+num1);
      System.out.println("num2-- is: "+num2);
   }
}
```

**Изход:**

```
num1++ is: 101
num2-- is: 199
```

### 4) Логически оператори

Логическите оператори се използват с двоични променливи. Те се използват главно в условни изрази и цикли за оценка на състояние.

Логическите оператори в java са: &&, ||,!

Да кажем, че имаме две булеви променливи b1 и b2.

b1 && b2 ще върне true, ако b1 и b2 са със стойност "истина", иначе ще върне false.

b1 || b2 ще върне false, ако b1 и b2 са със стойност "лъжа", иначе ще върне истина.

!b1 ще върне обратното на b1, това означава, че би било вярно, ако b1 е невярно и би върнало невярно, ако b1 е вярно.

Пример за логически оператори

```
public class LogicalOperatorDemo {
   public static void main(String[] args) {
      boolean b1 = true;
      boolean b2 = false;

      System.out.println("b1 && b2: " + (b1&&b2));
      System.out.println("b1 || b2: " + (b1||b2));
      System.out.println("!(b1 && b2): " + !(b1&&b2));
   }
}
```

**Изход:**

```
b1 && b2: false
b1 || b2: true
!(b1 && b2): true
```

### 5) Оператори за сравнение

Имаме шест релационни оператора в Java: ==,! =,>, <,> =, <=

\== връща true, ако лявата и дясната страна са равни

! = връща true, ако лявата страна не е равна на дясната страна на оператора.

\>връща true, ако лявата страна е по-голяма от дясната.

< връща true, ако лявата страна е по-малка от дясната страна.

\= връща true, ако лявата страна е по-голяма или равна на дясната страна.

<= връща true, ако лявата страна е по-малка или равна на дясната страна. 

Пример за релационни оператори

```
public class RelationalOperatorDemo {
   public static void main(String[] args) {
      int num1 = 10;
      int num2 = 50;
      if (num1==num2) {
	 System.out.println("num1 and num2 are equal");
      }
      else{
	 System.out.println("num1 and num2 are not equal");
      }

      if( num1 != num2 ){
	 System.out.println("num1 and num2 are not equal");
      }
      else{
	 System.out.println("num1 and num2 are equal");
      }

      if( num1 > num2 ){
	 System.out.println("num1 is greater than num2");
      }
      else{
	 System.out.println("num1 is not greater than num2");
      }

      if( num1 >= num2 ){
	 System.out.println("num1 is greater than or equal to num2");
      }
      else{
	 System.out.println("num1 is less than num2");
      }

      if( num1 < num2 ){
	 System.out.println("num1 is less than num2");
      }
      else{
	 System.out.println("num1 is not less than num2");
      }

      if( num1 <= num2){
	 System.out.println("num1 is less than or equal to num2");
      }
      else{
	 System.out.println("num1 is greater than num2");
      }
   }
}
```

**Изход:**

```
num1 and num2 are not equal
num1 and num2 are not equal
num1 is not greater than num2
num1 is less than num2
num1 is less than num2
num1 is less than or equal to num2
```

### 6) Битови оператори

Има шест битови оператори: &, |, ^, \~, <<, >>

Да дефинираме две числа

int num1 = 11; /\* равен на 00001011\*/\
\
int num2 = 22; /\* равна на 00010110 \*/

Битовите оператор извършват битова обработка.\
**num1 & num2** сравнява съответните битове на num1 и num2 и генерира 1, ако битовете на една и съща позиция са равни, иначе връща 0. В нашия случай би се върнало: 2 което е 00000010, защото в двоичната форма на num1 и num2 има съвпадение единствено в бит на позиция 1.

**num1 | num2** сравнява съответните битове на num1 и num2 и генерира 1, ако поне един от битовете на една и съща позиция има стойност 1, иначе връща 0. В нашия случай би върнало 31, което е 00011111.

**num1 ^ num2** сравнява съответните битове num1 и num2 и генерира 1, ако битовете на една и съща позиция са с различни стойности, иначе връща 0. В нашия пример би се получило 29, което е еквивалентно на 00011101

**\~num1** е оператор на преобразуване, който просто променя бита от 0 на 1 и 1 на 0. В нашия пример би се върнало -12, която е  11110100

**num1 << 2** е оператор за побитово изместване наляво, който изхвърля най-левия бит и присвоява на най-десния бит стойност 0. В нашия случай продукцията е 44, което е еквивалентно на 00101100

**num1 >> 2** е оператор за побитово изместване надясно, който изхвърля най-десния бит и присвоява на най-левия бит стойност 0. В нашия случай продукцията е 2, която е еквивалентна на 00000010

#### Пример за оператори на Bitwise

```
public class BitwiseOperatorDemo {
  public static void main(String[] args) {

     int num1 = 11;  /* 11 = 00001011 */
     int num2 = 22;  /* 22 = 00010110 */
     int result = 0;

     result = num1 & num2;   
     System.out.println("num1 & num2: "+result);

     result = num1 | num2;   
     System.out.println("num1 | num2: "+result);
    
     result = num1 ^ num2;   
     System.out.println("num1 ^ num2: "+result);
    
     result = ~num1;   
     System.out.println("~num1: "+result);
    
     result = num1 << 2;   
     System.out.println("num1 << 2: "+result); result = num1 >> 2;   
     System.out.println("num1 >> 2: "+result);
  }
}
```

**Изход:**

```
num1 & num2: 2
num1 | num2: 31
num1 ^ num2: 29
~num1: -12
num1 << 2: 44 num1 >> 2: 2
```

### 7) Троен оператор

Този оператор оценява булев израз и присвоява стойността от резултата.\
**Синтаксис:**

```
<тип данни> <идентификатор> = (израз) ? стойност при истина : стойност при лъжа
```

#### Пример:

```
public class TernaryOperatorDemo {

   public static void main(String[] args) {
        int num1, num2;
        num1 = 25;
        /* num1 is not equal to 10 that's why
	 * the second value after colon is assigned
	 * to the variable num2
	 */
	num2 = (num1 == 10) ? 100: 200;
	System.out.println( "num2: "+num2);

	/* num1 is equal to 25 that's why
	 * the first value is assigned
	 * to the variable num2
	 */
	num2 = (num1 == 25) ? 100: 200;
	System.out.println( "num2: "+num2);
   }
}
```

**Изход:**

```
num2: 200
num2: 100
```
