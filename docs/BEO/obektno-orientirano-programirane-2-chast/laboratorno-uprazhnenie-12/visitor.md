---
layout: default
title: Visitor
parent: Лабораторно упражнение 12
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 1
---

# Visitor

### Проблем

Да се разработи система за работа с документ.

Документът може да съдържа различни елементи: параграфи, изображения и таблици. За тези елементи трябва да могат да се изпълняват различни операции, например експортиране в HTML и извличане на статистика.


### Решение без използване на шаблона

Един възможен подход е всяка операция да бъде добавена директно в класовете на елементите:
```java
public class Paragraph {

    private String text;

    public String exportAsHtml() {
        return "<p>" + text + "</p>";
    }

    public String getStatistics() {
        return "Paragraph length: " + text.length();
    }
}
```
Аналогично подобни методи трябва да бъдат добавени и в ImageElement, и в TableElement.


### Недостатъци на решението

При този подход всеки клас на елемент започва да съдържа логика за различни операции. Ако се добави нова операция, например експортиране в PDF или проверка за правопис, трябва да бъдат променени всички класове на елементите.

Така класовете постепенно започват да събират логика, която не е част от основната им отговорност.

Следователно е необходимо решение, което позволява добавяне на нови операции върху елементите, без да се променят техните класове.


### Шаблонът като решение

Шаблонът **Visitor** позволява нови операции да бъдат добавяни в отделни класове, без да се променят класовете на елементите.

Всеки елемент приема посетител чрез метод accept(), а посетителят изпълнява съответната операция според конкретния тип на елемента.


### Дефиниция

Visitor е поведенчески шаблон за проектиране, който позволява дефиниране на нови операции върху обекти от дадена структура, без да се променят класовете на тези обекти.

Основни участници:
* Element - Интерфейс или абстрактен клас за обектите, които могат да бъдат посетени.
* Concrete Element - Конкретен елемент от структурата.
* Visitor - Интерфейс, който дефинира операции за посещение на различните типове елементи.
* Concrete Visitor - Конкретна реализация на операцията, която се изпълнява върху елементите.

### UML диаграма

<img width="1015" height="596" alt="Visitor" src="https://github.com/user-attachments/assets/53d21a75-afc6-4a04-807d-3a7c036c55ff" />


### Примерна реализация

Element
```java
public interface DocumentElement {

    String accept(DocumentVisitor visitor);
}
```
Concrete Elements
```java
public class Paragraph implements DocumentElement {

    private String text;

    public Paragraph(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String accept(DocumentVisitor visitor) {
        return visitor.visit(this);
    }
}
```
```java
public class ImageElement implements DocumentElement {

    private String fileName;

    public ImageElement(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String accept(DocumentVisitor visitor) {
        return visitor.visit(this);
    }
}
```
```java
public class TableElement implements DocumentElement {

    private int rows;
    private int columns;

    public TableElement(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    @Override
    public String accept(DocumentVisitor visitor) {
        return visitor.visit(this);
    }
}
```
Visitor
```java
public interface DocumentVisitor {

    String visit(Paragraph paragraph);

    String visit(ImageElement image);

    String visit(TableElement table);
}
```
```java
public class HtmlExportVisitor implements DocumentVisitor {

    @Override
    public String visit(Paragraph paragraph) {
        return "<p>" + paragraph.getText() + "</p>";
    }

    @Override
    public String visit(ImageElement image) {
        return "<img src=\"" + image.getFileName() + "\">";
    }

    @Override
    public String visit(TableElement table) {
        return "<table>"
                + table.getRows()
                + " rows, "
                + table.getColumns()
                + " columns</table>";
    }
}
```
```java
public class StatisticsVisitor implements DocumentVisitor {

    @Override
    public String visit(Paragraph paragraph) {
        return "Paragraph length: " + paragraph.getText().length();
    }

    @Override
    public String visit(ImageElement image) {
        return "Image file: " + image.getFileName();
    }

    @Override
    public String visit(TableElement table) {
        return "Table cells: " + table.getRows() * table.getColumns();
    }
}
```
Използване
```java
import java.util.ArrayList;
import java.util.List;

public class Application {

    public static void main(String[] args) {

        List<DocumentElement> document = new ArrayList<>();

        document.add(new Paragraph("Visitor design pattern"));
        document.add(new ImageElement("diagram.png"));
        document.add(new TableElement(3, 4));

        DocumentVisitor htmlVisitor = new HtmlExportVisitor();
        DocumentVisitor statisticsVisitor = new StatisticsVisitor();

        for (DocumentElement element : document) {
            System.out.println(element.accept(htmlVisitor));
        }

        for (DocumentElement element : document) {
            System.out.println(element.accept(statisticsVisitor));
        }
    }
}
```

Интерфейсът DocumentElement дефинира метода accept(), чрез който всеки елемент от документа приема посетител.

Класовете Paragraph, ImageElement и TableElement са конкретни елементи. Всеки от тях извиква подходящия метод visit() на подадения посетител, като подава себе си като аргумент.

Интерфейсът DocumentVisitor дефинира отделен метод visit() за всеки тип елемент. Така конкретният посетител знае как да обработи всеки от елементите.

Класът HtmlExportVisitor реализира операция за експортиране в HTML формат, а StatisticsVisitor реализира операция за извличане на статистика. Добавянето на нова операция изисква създаване на нов посетител, без да се променят класовете Paragraph, ImageElement и TableElement.

> [!IMPORTANT]
> Visitor е подходящ когато структурата от елементи е сравнително стабилна, но често се
> добавят нови операции върху нея. Ако често се добавят нови типове елементи, шаблонът става
> по-труден за поддръжка, защото интерфейсът Visitor трябва да бъде променян.

### Предимства

* позволява добавяне на нови операции без промяна в класовете на елементите;
* отделя операциите от структурата от обекти;
* събира свързаната логика в отделен visitor клас;
* улеснява работа със стабилни йерархии от обекти.

### Недостатъци

* добавянето на нов тип елемент изисква промяна в интерфейса Visitor;
* всички конкретни посетители трябва да бъдат актуализирани при добавяне на нов елемент;
* може да наруши капсулацията, ако visitor класовете се нуждаят от много вътрешни данни на елементите;
* структурата е по-сложна в сравнение с директно добавяне на методи в класовете.

### Приложение

Visitor е подходящ когато:
* има стабилна структура от различни типове обекти;
* често се добавят нови операции върху тези обекти;
* не е желателно класовете на елементите да се променят при всяка нова операция;
* се работи с документи, синтактични дървета, графични елементи или други йерархични структури.
