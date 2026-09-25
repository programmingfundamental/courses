# Шифър на Виженер

При Виженер всяка буква от ключа задава изместването за съответната буква от текста. Ако текстът е HELLOWORLD и ключът KEY, използваната последователност е KEYKEYKEYK. Буквите превръщаме в числа A=0..Z=25; при криптиране събираме стойностите, а при декриптиране изваждаме. Това е учебен пример и не е подходящ за защита на информация.

**Важно за учебната криптография:** Цезаровият шифър, шифърът на Виженер, простото XOR преобразуване и използваните транспозиционни методи са само за обучение. Не ги използвайте за защита на чувствителна информация. Целта е да се упражнят алгоритми, преобразуване на текст, ключове, обратими операции, криптиране, декриптиране и елементарен криптоанализ; не се разглеждат съвременни криптографски системи.

## Алгоритъм
1. Премахни всички символи, които не са A-Z, и превърни буквите в главни.
2. За позиция i избери key[i % key.length()].
3. При криптиране събери стойностите по модул 26.
4. При декриптиране извади стойността на ключа и добави 26 преди модула.
5. Преобразувай числото обратно в буква.

Псевдокод:

```text
за i от 0 до дължина(text)-1:
    textValue = text[i] - 'A'
    keyValue = key[i mod дължина(key)] - 'A'
    ако криптиране: result = (textValue + keyValue) mod 26
    иначе: result = (textValue - keyValue + 26) mod 26
    добави 'A' + result
```

## Примерен вход и изход
```text
Вход:
TEXT: HELLOWORLD
KEY: KEY

Повтарящ се ключ: KEYKEYKEYK
Очакван шифротекст: RIJVSUYVJN
Декриптиране: HELLOWORLD
```

## Практическа задача
Създайте encrypt(text, key) и decrypt(text, key), като почистите текста и ключа до A-Z. Проверете с HELLOWORLD и KEY, че декриптирането възстановява първоначалните букви.

## Насоки за реализация
- Позицията в повтарящия се ключ е i % cleanKey.length().
- Преди деление проверете дали ключът съдържа поне една буква.
- При изваждане използвайте +26, за да остане стойността неотрицателна.

## Пример
Допълнете TODO частите. Кодът е умишлено непълен и не представлява готово решение.

```java
public static String transform(String text, String key, boolean decrypt) {
    String cleanText = lettersOnly(text);
    String cleanKey = lettersOnly(key);
    if (cleanKey.length() == 0) return "";
    StringBuilder result = new StringBuilder();
    // TODO: използвайте ключа циклично
    return result.toString();
}
```

## Самостоятелна задача
Покажете под всеки символ от HELLOWORLD съответния символ от повтарящия се ключ.

## Допълнителна задача
**По желание:** позволете на потребителя да избере режим encrypt/decrypt чрез конзолно меню.

## Въпроси за проверка
1. Защо ключът се повтаря?
2. Как се избира текущата позиция в ключа?
3. Какво означават A=0 и Z=25?
4. Защо декриптирането включва +26?
5. Какво правим, ако ключът няма букви A-Z?

## Очакван резултат
При текст HELLOWORLD и ключ KEY шифротекстът е RIJVSUYVJN. Декриптиране с KEY връща HELLOWORLD.

## Пълно примерно решение
Работещият пълен пример е даден по-долу и се намира отделно във файла `08-vigenere/src/Main.java`.


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

    public static String transform(String text, String key, boolean decrypt) {
        String cleanText = lettersOnly(text);
        String cleanKey = lettersOnly(key);
        if (cleanKey.length() == 0) return "";

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < cleanText.length(); i++) {
            int textValue = cleanText.charAt(i) - 'A';
            int keyValue = cleanKey.charAt(i % cleanKey.length()) - 'A';
            int resultValue;
            if (decrypt) {
                resultValue = (textValue - keyValue + 26) % 26;
            } else {
                resultValue = (textValue + keyValue) % 26;
            }
            result.append((char) ('A' + resultValue));
        }
        return result.toString();
    }

    public static String encrypt(String text, String key) {
        return transform(text, key, false);
    }

    public static String decrypt(String text, String key) {
        return transform(text, key, true);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Текст (английски букви): ");
        String text = scanner.nextLine();
        System.out.print("Ключ (английски букви): ");
        String key = scanner.nextLine();

        String encrypted = encrypt(text, key);
        System.out.println("Криптиран текст: " + encrypted);
        if (lettersOnly(key).length() == 0) {
            System.out.println("Ключът трябва да съдържа поне една буква A-Z.");
        } else {
            System.out.println("Декриптиран текст: " + decrypt(encrypted, key));
        }
    }
}
```
