---
layout: default
title: Composite
parent: Лабораторно упражнение 8
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 1
---

# Composite

### Проблем

В практиката често се срещат ситуации, при които отделни обекти и групи от обекти трябва да бъдат обработвани по един и същ начин.

Например една файлова система съдържа файлове и папки. Папките могат да съдържат както файлове, така и други папки, образувайки йерархична структура. Независимо дали работим с отделен файл или с цяла папка, клиентският код трябва да може да използва един и същ интерфейс.

<img width="259" height="195" alt="image" src="https://github.com/user-attachments/assets/72b8ef57-7ba2-48c4-87e6-e779bc65df3f" />

Всички елементи в дървото са от тип FileSystemItem.

### Решение

Composite организира обектите в дървовидна структура, при която както отделните елементи, така и техните групи реализират общ интерфейс.

По този начин клиентският код работи еднакво както с единичен обект, така и с цяла йерархия от обекти.

### Дефиниция

Composite е структурен шаблон за проектиране, който организира обекти в дървовидна структура и позволява отделните обекти и техните композиции да бъдат третирани по еднакъв начин.

В шаблона Composite основните участници са:
* Компонент (Component) – общ интерфейс или абстрактен клас, реализиран от всички елементи в дървото.
* Лист (Leaf) – елемент, който няма наследници и реализира конкретното поведение.
* Композит (Composite) – елемент, който съдържа други компоненти и делегира операциите към тях.

### UML диаграма

<img width="511" height="316" alt="Composite" src="https://github.com/user-attachments/assets/8ca3e732-71c5-488d-ae2a-300e8293deee" />


### Примерна реализация

Компонент
```java
public interface FileSystemItem {

    String getName();

    int getSize();

}
```

Лист
```java
public class File implements FileSystemItem {

    private String name;
    private int size;

    public File(String name, int size) {
        this.name = name;
        this.size = size;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getSize() {
        return size;
    }

}
```

Композит
```java
import java.util.ArrayList;
import java.util.List;

public class Folder implements FileSystemItem {

    private String name;

    private List<FileSystemItem> items = new ArrayList<>();

    public Folder(String name) {
        this.name = name;
    }

    public void add(FileSystemItem item) {
        items.add(item);
    }

    public void remove(FileSystemItem item) {
        items.remove(item);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getSize() {

        int totalSize = 0;

        for (FileSystemItem item : items) {
            totalSize += item.getSize();
        }

        return totalSize;
    }

}
```

Използване
```java
public class Application {

    public static void main(String[] args) {

        FileSystemItem notes =
                new File("notes.txt", 10);

        FileSystemItem report =
                new File("report.pdf", 120);

        Folder documents =
                new Folder("Documents");

        documents.add(notes);
        documents.add(report);

        Folder root =
                new Folder("Root");

        root.add(documents);
        root.add(new File("readme.md", 5));

        System.out.println(root.getName());
        System.out.println(root.getSize());

    }

}
```

Интерфейсът FileSystemItem представлява общия компонент в дървовидната структура.

Класът File е листо (Leaf). Той не съдържа други елементи и реализира операциите директно.

Класът Folder е композит (Composite). Освен собствените си данни той съдържа колекция от обекти, реализиращи интерфейса FileSystemItem. Това позволява една папка да съдържа както файлове, така и други папки.

При извикване на метода getSize() папката последователно извиква същия метод за всички свои елементи и сумира получения резултат. По този начин клиентският код работи еднакво както с отделен файл, така и с цяла йерархия от папки.

> [!IMPORTANT]
> За разлика от Adapter, Decorator и Bridge, при които референцията към друг обект служи за
> делегиране на поведение, Composite използва йерархична структура от обекти. Всеки композит
> съдържа колекция от компоненти, което позволява изграждането на дървовидни структури с
> произволна дълбочина.

### Предимства

* позволява еднаква работа с отделни обекти и техните композиции;
* естествено моделира дървовидни структури;
* улеснява добавянето на нови типове елементи;
* намалява необходимостта от проверки за конкретния тип на обекта;
* подпомага спазването на принципа Open/Closed.

### Недостатъци

* затруднява налагането на ограничения върху допустимите елементи в дървото;
* при големи йерархии обработката може да стане по-сложна;
* не е подходящ за структури, които не са йерархични.

### Приложение

Composite е подходящ когато:
* се моделират дървовидни структури;
* отделните обекти и групите от обекти трябва да бъдат обработвани по еднакъв начин;
* клиентският код не трябва да прави разлика между единичен обект и композиция от обекти;
* се изграждат файлови системи, графични сцени, менюта, организационни структури и други йерархични модели.
