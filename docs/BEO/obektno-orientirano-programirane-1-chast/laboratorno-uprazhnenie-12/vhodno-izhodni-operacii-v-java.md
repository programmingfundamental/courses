---
layout: default
title: Входно-изходни операции в Java
parent: Лабораторно упражнение 12
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 1
---

# Входно-изходни операции в Java

Входно-изходните операции в Java са базирани на концепцията на потоци (streams), като под „поток” се има предвид последователност от данни.

Дадено Java-приложение използва входен поток за прочитане на данни от източник и изходен поток за запис на тези данни в приемник. Източникът и приемникът могат да бъдат файл, конзола, друго приложение или някакво периферно устройство, както е показано на фигурата по-долу.

![](<../../assets/image (118).png>)

Потоците поддържат различни типове данни (байтове, данни от прост тип, символи, обекти), като някои потоци просто предават данните, докато други обработват тези данни и ги преобразуват в подходящ за конкретния случай вид.

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

Към класовете, които наследяват InputStream, спадат:

\-          BufferedInputStream – за прочитане на байтове от буфер;

\-          ByteArrayInputStream – за прочитане на байтове от масив;

\-          DataInputStream – за прочитане на прости типове данни;

\-          FileInputStream – за четене на байтове от файл;

\-          ObjectInputStream – за прочитане на обекти;

\-          др.

Част от методите на класа InputStream:

·         public abstract int read() – чете поредния байт данни от входния поток; при достигане на край на входния поток връща -1;

·         public int available() – връща колко байта от текущия входен поток могат да бъдат прочетени;

·         public void close() – затваря текущия входен поток.

Аналогично, към класовете, наследяващи OutputStream, спадат:

\-          BufferedOutputStream – за запис на байтове в буфер;

\-          ByteArrayOutputStream – за запис на байтове в масив;

\-          DataOutputStream – за запис на прости типове данни;

\-          FileOutputStream – за запис на байтове във файл;

\-          ObjectOutputStream – за запис на обекти;

\-          др.

Част от методите на класа OutputStream:

·         public void write(int i) – записва подавания байт в текущия изходен поток;

·         public void write(byte\[]) – записва масива байтове в текущия изходен поток;

·         public void flush() – изчиства текущия изходен поток;

·         public void close() – затваря текущия изходен поток.

Важно е да се запомни, че всеки един входно-изходен поток трябва да бъде затворен.

Байтовите потоци се използват за четене/запис на примитивни типове данни. Съществуват други типове потоци, за по-сложни данни – но всички те се базират на байтовите потоци.

_Пример за използване на байтов поток:_

```java
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ByteStreamExample {

    public static void main(String[] args) {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] content = "Example of I/O operations using byte streams".getBytes();

            // Файлът трябва да е предварително създаден и като аргумент се подава пътят към него
            File file = new File("C:\\io\\FirstExample");
            FileOutputStream outputStream = new FileOutputStream(file);

            outputStream.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

## Символни потоци. CharacterStream класове

Класовете за работа с байтови потоци могат да оперират само с един байт и не са подходящи за директна работа с Unicode символи, които са 16-битови. За работа с потоци от такива символи се използват съответно символни потоци.

По подобие на класовете за работа с байтови потоци, класовете за работа със символни потоци също са два основни типа: наследници на абстрактен клас Reader или на абстрактен клас Writer.

Класове, които наследяват клас Reader:

\-          BufferedReader;

\-          FileReader;

\-          InputStreamReader – този клас предоставя методи за преобразуване на байтове в символи;

\-          StringReader;

\-          др.

Класът Reader и всички негови наследници имат сходни методи с тези на клас InputStream като _**int read()**_ и _**void close()**_.

Класове, които наследяват клас Writer:

\-          BufferedWriter;

\-          FileWriter;

\-          OutputStreamWriter;

\-          StringWriter;

\-          др.

Класът Writer и всички негови наследници имат подобни на OutputStream методи: _**void write()**_, _**void write(int i)**_ за запис на един символ, _**void flush()**_ и _**void close()**_.

Често символните потоци се явяват „обгръщащи” (wrappers) за байтовите потоци. Така например, FileReader използва FileInputStream, а FileWriter – FileOutputStream.

Символните потоци поддържат всички символи за край на ред – “\r”, “\n” и “\r\n”. Това позволява работа с текстови файлове.

_Примери за използване на символни потоци:_

Показаните два примера извършват едно и също: четат от един файл и записват прочетеното съдържание в друг. В първият пример са използвани само класове FileReader и FileWriter, докато вторият реализира същото с помощта на BufferedReader и PrintWriter.

```java
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileStreamsExample {

    public static void main(String[] args) {
        FileReader fileReader;
        FileWriter fileWriter;
        int ch;

        try {
            fileReader = new FileReader("C:\\io\\input");
            fileWriter = new FileWriter("C:\\io\\output");

            while ((ch = fileReader.read()) != -1) {
                fileWriter.write(ch);
            }

            fileReader.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

```java
import java.io.*;

public class BufferedStreamsExample {

    public static void main(String[] args) {
        BufferedReader bufferedReader;
        PrintWriter printWriter;
        String line;

        try {
            bufferedReader = new BufferedReader(new FileReader("C:\\io\\input"));
            printWriter = new PrintWriter(new FileWriter("C:\\io\\output"));

            while ((line = bufferedReader.readLine()) != null) {
                printWriter.writeln(line);
            }

            bufferedReader.close();
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

## Клас Scanner

Този клас дава възможност за работа с текст като предоставя методи за неговото преобразуване в различни примитивни типове данни. За разлика от входно-изходните потоци, класът се намира в пакета java.util.

Scanner разбива входа на токени чрез използване на разделител (по подразбиране това е интервала). Получените в резултат на това разделение токени могат да бъдат превърнати в стойности от различен тип, благодарение на различните next-методи.

Често използвани методи от клас Scanner:

·         boolean hasNext() – връща истина ако има следващ токен;

·         boolean hasNextInt() – връща истина ако следващия токен може да се интерпретира като цяло число;

·         boolean hasNextDouble() – връща истина ако следващия токен може да се интерпретира като дробно число;

·         boolean hasNextLong() – връща истина ако следващия токен може да се интерпретира като число от тип long;

·         boolean hasNextLine() – връща истина ако входът има следващ/и ред/ове;

·         String next() – намира и връща следващия токен;

·         boolean nextBoolean() – намира и връща следващия токен, който може да се интерпретира като булева стойност;

·         int nextInt() - намира и връща следващия токен, който може да се интерпретира като целочислена стойност;

·         double nextDouble() - намира и връща следващия токен, който може да се интерпретира като дробно число;

·         long nextLong() - намира и връща следващия токен, който може да се интерпретира като число от тип long;

·         Scanner useDelimeter(String pattern) – задава какво да се използва за разделител;

·         void close().

_Примери за използване на клас Scanner_:

Примерът по-долу прочита входен символен низ и след това извежда всяка една дума на отделен ред.

```java
import java.util.Scanner;

public class StringScannerExample {

    public static void main(String[] args) {
        String input = "This is an example of using Scanner";
        Scanner scanner = new Scanner(input);

        while (scanner.hasNext()) {
            System.out.println(scanner.next());
        }

        scanner.close();
    }
}
```

Методът next() прочита следващата дума от входния поток, като използва празните пространства (интервали, табулации и нови редове) като разделители по подразбиране. Методът hasNext() проверява дали има следваща дума за прочитане.

Следващият пример чете от файл и сумира дробните числа, записани в него (пропуска данните от всички други типове, които се съдържат в този файл).

```java
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MixedDataScannerExample {

    public static void main(String[] args) {
        Scanner scanner;
        double sum = 0;

        try {
            scanner = new Scanner(new File("C:\\io\\input"));

            while (scanner.hasNext()) {
                if (scanner.hasNextDouble()) {
                    sum += scanner.nextDouble();
                } else {
                    scanner.next();
                }
            }

            scanner.close();
            System.out.println(sum);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
```

## Стандартни потоци. Клас Console

Java поддържа три стандартни потока:

\-          System.in – системен вход (от клавиатура);

\-          System.out – системен изход (на екран/конзола);

\-          System.err – стандартен поко за грешки.

Никой от тези потоци не трябва да бъде деклариран, те се създават автоматично.

System.out и System.err са дефинирани като PrintWriter-обекти, т.е. символни потоци. От своя страна, System.in е дефиниран като байтов поток.

Алтернатива на стандартните потоци е класът Console. Той предоставя функционалностите на стандартните потоци, както и допълнителни такива. Обектът Console предоставя входни и изходни символни потоци чрез методите си reader() и writer().

_Пример за използване на стандартни потоци:_

```java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StandardStreamExample {

    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Hello, please write your name: ");

        String name = bufferedReader.readLine();

        System.out.println("You have entered: " + name);
    }
}
```

## Даннови потоци. Интерфейси DataInput и DataOutput

Данновите потоци поддържат входно-изходни операции върху всички прости типове данни, както и върху символни низове. Всички даннови потоци имплементират или интерфейс DataInput, или интерфейс DataOutput.

Методи на интерфейс DataInput:

·         boolean readBoolean();

·         byte readByte();

·         char readChar();

·         double readDouble();

·          int reading();

·         String readLine();

·         и др.

Всички тези методи имат своите съответстващи write() методи от интерфейса DataOutput: void writeBoolean(boolean value), void writeByte(byte value) и т.н.

Класове, които имплементират DataInput: DataInputStream, FileImageInputStream, ObjectInputStream, RandomAccessFile.

Класове, които имплементират DataOutput: DataOutputStream, FileImageOutputStream, ObjectOutputStream, RandomAccessFile.

_Пример за използване на даннови потоци:_

Примерът прочита файл чрез RandomAccessFile и запълва колекция от обекти. Файлът съдържа по един обект на ред.

```java
public class Cat {

    private String name;
    private double weight;
    private int age;

    public Cat() {
    }

    public Cat(String name, double weight, int age) {
        this.name = name;
        this.weight = weight;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Cat{" +
                "name='" + name + '\'' +
                ", weight=" + weight +
                ", age=" + age +
                '}';
    }
}
```

```java
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class CatCollection {

    private List<Cat> cats = new ArrayList<>();

    public CatCollection(String fileName) {

        try {
            RandomAccessFile myFile = new RandomAccessFile(fileName, "r"); // "r" - отваря файла за четене

            String line;
            String[] result;

            while ((line = myFile.readLine()) != null && line.length() > 0) {
                result = line.split(" ");

                String catName = result[0];
                double catWeight = Double.parseDouble(result[1]);
                int catAge = Integer.parseInt(result[2]);

                cats.add(new Cat(catName, catWeight, catAge));
            }
            myFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```
