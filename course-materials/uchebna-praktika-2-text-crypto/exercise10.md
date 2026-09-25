# Просто транспозиционно шифриране

Избираме броя колони и записваме текста по редове. След това четем клетките по колони. За MEETME с 3 колони получаваме редове MEE и TME; четенето по колони дава MTEMEE. Ако последният ред е непълен, примерното решение допълва клетките с X. При декриптиране знаем оригиналната дължина, за да отрежем запълването. Методът е само учебен.

**Важно за учебната криптография:** Цезаровият шифър, шифърът на Виженер, простото XOR преобразуване и използваните транспозиционни методи са само за обучение. Не ги използвайте за защита на чувствителна информация. Целта е да се упражнят алгоритми, преобразуване на текст, ключове, обратими операции, криптиране, декриптиране и елементарен криптоанализ; не се разглеждат съвременни криптографски системи.

## Алгоритъм
1. Изчисли броя редове с закръгляне нагоре.
2. Попълни матрицата по редове; липсващите клетки запълни с X.
3. Прочети колоните отляво надясно и добави символите към резултата.
4. За възстановяване попълни матрицата по колони, после прочети я по редове.
5. Върни само оригиналния брой символи.

Псевдокод:

```text
rows = закръгли нагоре(length(text) / columns)
попълни grid[row][column] по редове; липсващото = 'X'
за column: за row: добави grid[row][column]

декриптиране:
попълни grid по колони от шифротекста
прочети grid по редове до originalLength
```

## Примерен вход и изход
```text
Вход:
Текст: MEETME
Колони: 3

Матрица:
M E E
T M E

Очакван резултат:
Транспониран текст: MTEMEE
Възстановен текст: MEETME
```

## Практическа задача
Напишете encrypt(text, columns) и decrypt(cipher, columns, originalLength). Проверете, че при положителен брой колони възстановеният текст е равен на входа. Обработете и последен непълен ред.

## Насоки за реализация
- Броят редове може да се изчисли като (length + columns - 1) / columns.
- При шифриране запълнете матрицата по редове, после обходете по колони.
- При декриптиране попълването започва по колони; не забравяйте оригиналната дължина.

## Пример
Допълнете TODO частите. Кодът е умишлено непълен и не представлява готово решение.

```java
public static String encrypt(String text, int columns) {
    // TODO: създайте и попълнете двумерна матрица
    return "";
}

public static String decrypt(String encrypted, int columns, int originalLength) {
    // TODO: попълнете матрицата по колони и прочетете по редове
    return "";
}
```

## Самостоятелна задача
Изпробвайте текст с дължина, която не се дели на броя колони. Покажете какви X символи са добавени към шифротекста и че не остават във възстановения резултат.

## Допълнителна задача
**По желание:** добавете визуално отпечатване на матрицата преди и след транспонирането.

## Въпроси за проверка
1. Какво се променя при транспозиция?
2. Как се попълва матрицата при шифриране?
3. В какъв ред я четем за шифротекста?
4. Защо е нужна оригиналната дължина?
5. Какво означават запълващите X?

## Очакван резултат
MEETME с 3 колони се преобразува в MTEMEE, а декриптирането с дължина 6 връща MEETME. Непълен последен ред се обработва чрез запълване.

## Пълно примерно решение
Работещият пълен пример е даден по-долу и се намира отделно във файла `10-transposition/src/Main.java`.


```java
import java.util.Scanner;

public class Main {
    public static String encrypt(String text, int columns) {
        if (columns <= 0 || text.length() == 0) return "";
        int rows = (text.length() + columns - 1) / columns;
        char[][] grid = new char[rows][columns];
        int index = 0;

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (index < text.length()) {
                    grid[row][column] = text.charAt(index);
                    index++;
                } else {
                    grid[row][column] = 'X';
                }
            }
        }

        StringBuilder result = new StringBuilder();
        for (int column = 0; column < columns; column++) {
            for (int row = 0; row < rows; row++) {
                result.append(grid[row][column]);
            }
        }
        return result.toString();
    }

    public static String decrypt(String encrypted, int columns, int originalLength) {
        if (columns <= 0 || originalLength <= 0) return "";
        int rows = (originalLength + columns - 1) / columns;
        char[][] grid = new char[rows][columns];
        int index = 0;

        for (int column = 0; column < columns; column++) {
            for (int row = 0; row < rows; row++) {
                if (index < encrypted.length()) {
                    grid[row][column] = encrypted.charAt(index);
                    index++;
                }
            }
        }

        StringBuilder result = new StringBuilder();
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (result.length() < originalLength) {
                    result.append(grid[row][column]);
                }
            }
        }
        return result.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Въведете текст: ");
        String text = scanner.nextLine();
        System.out.print("Брой колони: ");
        int columns = scanner.nextInt();

        String encrypted = encrypt(text, columns);
        System.out.println("Транспониран текст: " + encrypted);
        System.out.println("Възстановен текст: "
                + decrypt(encrypted, columns, text.length()));
    }
}
```
