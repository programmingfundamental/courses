# Преобразуване и нормализиране на текст

Нормализирането тук означава да запазим само буквите A-Z и да ги приведем до главни. Например Java, Security! става JAVASECURITY. Обхождаме входа; малка буква се преобразува чрез разликата между 'a' и 'A', а после се добавя само ако е в диапазона 'A'–'Z'. StringBuilder е удобен за сглобяване на резултат.

## Алгоритъм
1. Създай празен StringBuilder.
2. За всеки символ превърни a-z в A-Z.
3. Ако резултатът е буква A-Z, добави го; в противен случай го пропусни.
4. Върни получения низ.

Псевдокод:

```text
result = празен StringBuilder
за всеки символ c в text:
    ако 'a' <= c <= 'z': c = c - 'a' + 'A'
    ако 'A' <= c <= 'Z': добави c към result
върни result като String
```

## Примерен вход и изход
```text
Вход:
Java, Security!

Очакван резултат:
JAVASECURITY
```

## Практическа задача
Напишете normalizeText(text), която премахва интервали и препинателни знаци и връща само главни английски букви A-Z. Покажете резултата за вход от потребителя.

## Насоки за реализация
- Обработвайте по един char.
- Първо преобразувайте малките букви, после проверете диапазона A-Z.
- Добавяйте със StringBuilder.append(c).

## Пример
Допълнете TODO частите. Кодът е умишлено непълен и не представлява готово решение.

```java
public static String normalizeText(String text) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < text.length(); i++) {
        char c = text.charAt(i);
        // TODO: преобразувайте малките букви
        // TODO: добавете само A-Z
    }
    return result.toString();
}
```

## Самостоятелна задача
Покажете колко символа са премахнати: дължината на входа минус дължината на резултата.

## Допълнителна задача
**По желание:** поддържайте и кирилските букви, като ги преобразувате отделно и изрично обясните новите диапазони.

## Въпроси за проверка
1. Какво означава нормализиране в тази задача?
2. Кои символи се запазват?
3. Как се преобразува малка ASCII буква в главна?
4. Защо използваме StringBuilder?
5. Защо пунктуацията не се добавя към резултата?

## Очакван резултат
Входът Java, Security! дава JAVASECURITY. Разстоянията, запетаята и удивителният знак не присъстват в резултата.

## Пълно примерно решение
Работещият пълен пример е даден по-долу и се намира отделно във файла `04-text-normalization/src/Main.java`.


```java
import java.util.Scanner;

public class Main {
    public static String normalizeText(String text) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= 'a' && c <= 'z') {
                c = (char) (c - 'a' + 'A');
            }
            if (c >= 'A' && c <= 'Z') {
                result.append(c);
            }
        }

        return result.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Въведете текст: ");
        String text = scanner.nextLine();

        System.out.println("Нормализиран текст: " + normalizeText(text));
    }
}
```
