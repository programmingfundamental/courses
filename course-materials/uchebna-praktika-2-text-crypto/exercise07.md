# Шифър на Цезар

Шифърът на Цезар измества всяка буква с еднакъв ключ. За ключ 3, H има стойност 7 и става K със стойност 10. След Z се продължава от A чрез остатък при деление на 26. Примерът работи само с A-Z: програмата нормализира входа и пропуска интервали и пунктуация. За декриптиране правим обратно изместване.

**Важно за учебната криптография:** Цезаровият шифър, шифърът на Виженер, простото XOR преобразуване и използваните транспозиционни методи са само за обучение. Не ги използвайте за защита на чувствителна информация. Целта е да се упражнят алгоритми, преобразуване на текст, ключове, обратими операции, криптиране, декриптиране и елементарен криптоанализ; не се разглеждат съвременни криптографски системи.

## Алгоритъм
1. Почисти текста до главни букви A-Z.
2. Превърни буквата в число чрез c - 'A'.
3. Добави нормализирания ключ и вземи остатък при деление на 26.
4. Преобразувай числото обратно в буква.
5. При декриптиране използвай отрицателно изместване.

Псевдокод:

```text
за всяка буква c:
    value = c - 'A'
    shifted = (value + key) mod 26
    добави 'A' + shifted
за декриптиране използвай ключ -key
```

## Примерен вход и изход
```text
Вход:
Текст: HELLO
Ключ: 3

Очакван резултат:
Криптиран текст: KHOOR
Декриптиран текст: HELLO
```

## Практическа задача
Реализирайте lettersOnly(text), encrypt(text, key) и decrypt(text, key). Покажете шифротекста и след това декриптирайте същия текст. Програмата приема английски букви A-Z и връща само букви.

## Насоки за реализация
- Преобразувайте текста към A-Z преди шифриране.
- Нормализирайте ключа, ако е отрицателен или по-голям от 26.
- Връщането назад може да се реализира като encrypt(text, -key).

## Пример
Допълнете TODO частите. Кодът е умишлено непълен и не представлява готово решение.

```java
public static String encrypt(String text, int key) {
    StringBuilder result = new StringBuilder();
    // TODO: за всяка буква A-Z изчислете циклично изместване
    return result.toString();
}

public static String decrypt(String text, int key) {
    // TODO: обърнете изместването
    return "";
}
```

## Самостоятелна задача
Проверете ключове 0, 26 и 29 и обяснете защо 0 и 26 дават едно и също преобразуване.

## Допълнителна задача
**По желание:** запазвайте малките букви и пунктуацията на оригиналните им позиции.

## Въпроси за проверка
1. На коя числова стойност съответства A?
2. Защо използваме остатък при деление на 26?
3. Каква е разликата между encrypt и decrypt?
4. Какво става с интервалите в дадената реализация?
5. Защо нормализираме ключа?

## Очакван резултат
HELLO с ключ 3 дава KHOOR, а декриптирането с ключ 3 връща HELLO. Входът се обработва като английски букви A-Z.

## Пълно примерно решение
Работещият пълен пример е даден по-долу и се намира отделно във файла `07-caesar/src/Main.java`.


```java
import java.util.Scanner;

public class Main {
    public static String lettersOnly(String text) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= 'a' && c <= 'z') c = (char) (c - 'a' + 'A');
            if (c >= 'A' && c <= 'Z') result.append(c);
        }
        return result.toString();
    }

    public static String encrypt(String text, int key) {
        String clean = lettersOnly(text);
        StringBuilder result = new StringBuilder();
        int shift = ((key % 26) + 26) % 26;
        for (int i = 0; i < clean.length(); i++) {
            char c = clean.charAt(i);
            result.append((char) ('A' + (c - 'A' + shift) % 26));
        }
        return result.toString();
    }

    public static String decrypt(String text, int key) {
        return encrypt(text, -key);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Текст (английски букви): ");
        String text = scanner.nextLine();
        System.out.print("Ключ (цяло число): ");
        int key = scanner.nextInt();

        String encrypted = encrypt(text, key);
        System.out.println("Криптиран текст: " + encrypted);
        System.out.println("Декриптиран текст: " + decrypt(encrypted, key));
    }
}
```
