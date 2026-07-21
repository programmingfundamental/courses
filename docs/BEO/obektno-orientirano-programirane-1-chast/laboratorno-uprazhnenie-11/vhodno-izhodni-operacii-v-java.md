---
layout: default
title: Входно-изходни операции в Java
parent: Лабораторно упражнение 11
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 1
---

# Входно-изходни операции в Java

Входно-изходните операции в Java са базирани на концепцията на потоци (streams), като под „поток” се има предвид последователност от данни.

Дадено Java-приложение използва входен поток за прочитане на данни от източник и изходен поток за запис на тези данни в приемник. Източникът и приемникът могат да бъдат файл, конзола, друго приложение или някакво периферно устройство, както е показано на фигурата по-долу.

```mermaid
flowchart LR
    A["Източник"] --> B["Входен поток"]
    B --> C["Java приложение"]
    C --> D["Изходен поток"]
    D --> E["Приемник"]
```

В следващите раздели потоците се разглеждат според вида на данните, с които работят: байтове, текстови данни, стойности от примитивни типове и обекти. Някои потоци четат и записват данните директно, а други извършват преобразуване между външното представяне и стойностите в програмата.

## Използване на `import` при входно-изходни операции

Класовете за входно-изходни операции се намират в различни пакети. За да бъдат използвани с кратките си имена, в
началото на Java файла се добавят декларации `import`.

```java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
```

Без `import` трябва да се използва пълното име на класа.

```java
java.util.Scanner scanner = new java.util.Scanner(System.in);
```

Използването на `import` не добавя нова функционалност към класа. То само позволява по-кратък и по-четим запис на
имената на използваните класове.

## Байтови потоци. ByteStream класове

Както подсказва името им, тези класове четат и записват данни в 8-битов формат, т.е. представляват байтови потоци. Намират се в пакета java.io.

Всеки един от класовете наследява или абстрактния клас InputStream, или абстрактния клас OutputStream.

`InputStream` е базовият клас за байтов вход. Конкретните наследници определят откъде се четат байтовете или как се обработват.

Към класовете, които наследяват `InputStream`, спадат:

- `BufferedInputStream` - чете байтове чрез буфер;
- `ByteArrayInputStream` - чете байтове от масив;
- `DataInputStream` - чете стойности от примитивни типове данни;
- `FileInputStream` - чете байтове от файл;
- `ObjectInputStream` - чете обекти.

Част от методите на класа `InputStream`:

- `public abstract int read()` - чете следващия байт от входния поток; при достигане на край на потока връща `-1`;
- `public int available()` - връща броя байтове, които могат да бъдат прочетени без блокиране;
- `public void close()` - затваря входния поток.

`OutputStream` е базовият клас за байтов изход. Конкретните наследници определят къде се записват байтовете или как се обработват преди запис.

Към класовете, които наследяват `OutputStream`, спадат:

- `BufferedOutputStream` - записва байтове чрез буфер;
- `ByteArrayOutputStream` - записва байтове в масив;
- `DataOutputStream` - записва стойности от примитивни типове данни;
- `FileOutputStream` - записва байтове във файл;
- `ObjectOutputStream` - записва обекти.

Част от методите на класа `OutputStream`:

- `public void write(int value)` - записва един байт в изходния поток;
- `public void write(byte[] data)` - записва масив от байтове в изходния поток;
- `public void flush()` - принуждава записването на натрупаните в буфер данни;
- `public void close()` - затваря изходния поток.

Потоците, които работят с външни ресурси като файлове, трябва да бъдат затваряни след приключване на работата с тях. Стандартните потоци `System.in`, `System.out` и `System.err` обикновено не се затварят от приложния код. За файлови потоци се използва конструкцията `try-with-resources`.

Байтовите потоци се използват за четене и запис на байтове. Те са подходящи за двоични данни. Когато се работи с текст, стойности от примитивни типове или обекти, се използват по-специализирани класове, които предоставят по-подходящ начин за представяне на данните.

Следва пример за запис на байтове във файл чрез `FileOutputStream`:

```java
import java.io.FileOutputStream;
import java.io.IOException;

public class ByteStreamExample {

    public static void main(String[] args) {
        byte[] content = "Example of I/O operations using byte streams".getBytes();

        try (FileOutputStream outputStream = new FileOutputStream("C:\\io\\FirstExample")) {
            outputStream.write(content);
        } catch (IOException exception) {
            System.out.println("Cannot write to file.");
        }
    }
}
```

В примера `FileOutputStream` отваря изходен поток към файл. Блокът `try-with-resources` затваря потока автоматично след приключване на записа.

## Символни потоци. CharacterStream класове

Байтовите потоци четат и записват отделни байтове. Това е подходящо за двоични файлове, но не е достатъчно за текст, защото един видим символ може да бъде представен чрез повече от един байт. Символните потоци се използват за текстови данни и извършват преобразуване между байтове и символи.

По подобие на класовете за работа с байтови потоци, класовете за работа със символни потоци също са два основни типа: наследници на абстрактен клас Reader или на абстрактен клас Writer.

`Reader` е базовият клас за символен вход. Неговите наследници определят откъде се четат символите или как се преобразуват входните данни.

- `BufferedReader` - чете текст чрез буфер;
- `FileReader` - чете текст от файл;
- `InputStreamReader` - преобразува байтов входен поток в символен входен поток;
- `StringReader` - чете текст от низ.

Класът `Reader` и неговите наследници използват методи като `read()` и `close()`.

`Writer` е базовият клас за символен изход. Неговите наследници определят къде се записват символите или как се преобразуват изходните данни.

- `BufferedWriter` - записва текст чрез буфер;
- `FileWriter` - записва текст във файл;
- `OutputStreamWriter` - преобразува символен изходен поток към байтов изходен поток;
- `StringWriter` - записва текст в низ.

Класът `Writer` и неговите наследници използват методи като `write()`, `flush()` и `close()`.

Символните потоци могат да бъдат свързани с байтови потоци чрез класовете `InputStreamReader` и `OutputStreamWriter`. Те извършват преобразуване между байтове и символи според използваното текстово кодиране.

Символните потоци поддържат всички символи за край на ред – “\r”, “\n” и “\r\n”. Това позволява работа с текстови файлове.

Следва пример за четене на текст от един файл и записване на прочетените редове в друг файл чрез `BufferedReader` и `PrintWriter`:

```java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class BufferedStreamsExample {

    public static void main(String[] args) {
        try (
                BufferedReader reader = new BufferedReader(new FileReader("C:\\io\\input.txt"));
                PrintWriter writer = new PrintWriter(new FileWriter("C:\\io\\output.txt"))
        ) {
            String line;

            while ((line = reader.readLine()) != null) {
                writer.println(line);
            }
        } catch (IOException exception) {
            System.out.println("Cannot copy text file.");
        }
    }
}
```

В примера `BufferedReader` чете файла ред по ред, а `PrintWriter` записва всеки прочетен ред в изходния файл.

## Клас Scanner

Класът `Scanner` се използва за четене и разделяне на текстов вход на отделни части, наречени токени. Източникът на данни може да бъде низ, файл, стандартен вход или друг входен поток. Класът се намира в пакета `java.util`.

`Scanner` използва разделител, чрез който входът се разделя на токени. По подразбиране разделителят е празно пространство: интервал, табулация или нов ред.

Основни методи на класа `Scanner`:

- `boolean hasNext()` - проверява дали има следващ токен;
- `boolean hasNextInt()` - проверява дали следващият токен може да се прочете като `int`;
- `boolean hasNextDouble()` - проверява дали следващият токен може да се прочете като `double`;
- `boolean hasNextLong()` - проверява дали следващият токен може да се прочете като `long`;
- `boolean hasNextLine()` - проверява дали входът съдържа следващ ред;
- `String next()` - прочита и връща следващия токен;
- `boolean nextBoolean()` - прочита следващия токен като булева стойност;
- `int nextInt()` - прочита следващия токен като стойност от тип `int`;
- `double nextDouble()` - прочита следващия токен като стойност от тип `double`;
- `long nextLong()` - прочита следващия токен като стойност от тип `long`;
- `Scanner useDelimiter(String pattern)` - задава разделител чрез шаблон;
- `void close()` - затваря скенера.

Следват примери за използване на клас `Scanner`:

Примерът по-долу прочита входен символен низ и след това извежда всяка една дума на отделен ред.

```java
import java.util.Scanner;

public class StringScannerExample {

    public static void main(String[] args) {
        String input = "This is an example of using Scanner";

        try (Scanner scanner = new Scanner(input)) {
            while (scanner.hasNext()) {
                System.out.println(scanner.next());
            }
        }
    }
}
```

Методът next() прочита следващата дума от входния поток, като използва празните пространства (интервали, табулации и нови редове) като разделители по подразбиране. Методът hasNext() проверява дали има следваща дума за прочитане.

Следва пример за четене на файл и сумиране на числата от тип `double`, които могат да бъдат прочетени от него:

```java
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MixedDataScannerExample {

    public static void main(String[] args) {
        double sum = 0;

        try (Scanner scanner = new Scanner(new File("C:\\io\\input.txt"))) {
            while (scanner.hasNext()) {
                if (scanner.hasNextDouble()) {
                    sum += scanner.nextDouble();
                } else {
                    scanner.next();
                }
            }

            System.out.println(sum);
        } catch (FileNotFoundException exception) {
            System.out.println("Input file not found.");
        }
    }
}
```

## Стандартни потоци

Java поддържа три стандартни потока:

- `System.in` - стандартен вход, от който може да се чете информация;
- `System.out` - стандартен изход, към който се извежда основният резултат от програмата;
- `System.err` - стандартен поток за съобщения за грешки.

Тези потоци се предоставят от класа `System`. Те не се създават с `new` от приложния код.

`System.in` е байтов входен поток от тип `InputStream`. `System.out` и `System.err` са изходни потоци от тип `PrintStream`.

Следва пример за четене от стандартния вход и извеждане към стандартния изход:

```java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StandardStreamExample {

    public static void main(String[] args) {
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.print("Въведете име: ");

            String name = reader.readLine();

            System.out.println("Въведено име: " + name);
        } catch (IOException exception) {
            System.err.println("Cannot read from standard input.");
        }
    }
}
```

## Потоци за структурирани данни. Интерфейси DataInput и DataOutput

Потоците за структурирани данни позволяват запис и четене на стойности от примитивни типове и символни низове в машинно представяне. Тези потоци са подходящи, когато данните трябва да бъдат прочетени обратно в същия ред и със същите типове. Всички потоци за структурирани данни имплементират или интерфейс DataInput, или интерфейс DataOutput.

Методите на `DataInput` четат стойности в същия ред, в който са записани чрез `DataOutput`.

- `boolean readBoolean()`;
- `byte readByte()`;
- `char readChar()`;
- `double readDouble()`;
- `int readInt()`;
- `String readUTF()`.

При запис се използват съответстващи методи от `DataOutput`, например `writeBoolean(boolean value)`, `writeByte(int value)`, `writeInt(int value)`, `writeDouble(double value)` и `writeUTF(String value)`.

Основните класове за работа с тези интерфейси са `DataInputStream` и `DataOutputStream`. Други класове, например `RandomAccessFile`, също реализират тези интерфейси, но се използват в по-специализирани случаи.

Следва пример за запис и четене на структурирани данни чрез `DataOutputStream` и `DataInputStream`:

```java
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class DataStreamExample {

    public static void main(String[] args) {
        String path = "C:\\io\\cat.dat";

        try (DataOutputStream output = new DataOutputStream(new FileOutputStream(path))) {
            output.writeUTF("Tom");
            output.writeDouble(4.5);
            output.writeInt(3);
        } catch (IOException exception) {
            System.out.println("Cannot write structured data.");
        }

        try (DataInputStream input = new DataInputStream(new FileInputStream(path))) {
            String name = input.readUTF();
            double weight = input.readDouble();
            int age = input.readInt();

            System.out.println(name + " " + weight + " " + age);
        } catch (IOException exception) {
            System.out.println("Cannot read structured data.");
        }
    }
}
```

В примера данните се четат в същия ред, в който са записани: първо текст, след това дробно число и накрая цяло число.
