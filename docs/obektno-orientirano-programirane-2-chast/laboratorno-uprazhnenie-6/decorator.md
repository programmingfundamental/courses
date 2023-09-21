# Decorator

Декораторът е представител на структурните шаблони. Неговото предназначение е динамично да добавя характеристики и функционалности към вече съществуващи обекти, без това да променя тяхната структура. Декораторът се явява като обгръщащ (wrapper) клас за вече съществуващия и имплементира същия интерфейс.

Като илюстрация на шаблона декоратор, нека разгледме следния пример:

Декларираме интерфейс Book:

```
public interface Book {
    String decorateBook();
}
```

Необходим е също така клас BookImpl:

```
public class BookImpl implements Book{
    @Override
    public String decorateBook() {
        return "Book has: ";
    }
}
```

Следващата стъпка е създаването на абстрактен клас BookDecorator, в който като частно поле декларираме обект от интерфейса:

```
public abstract class BookDecorator implements Book {
    private Book book;
    public BookDecorator(Book book) {
        this.book = book;
    }
    @Override
    public String decorateBook() {
        return book.decorateBook();
    }
}
```

Този клас ще бъде базовия декоратор, като всички останали ще го наследяват.

```
public class HardCoverDecorator extends BookDecorator {
    public HardCoverDecorator(Book book) {
        super(book);
    }
    @Override
    public String decorateBook() {
        return super.decorateBook() + addHardCover();
    }
    private String addHardCover() {
        return "\n hard cover";
    }
}
```

```
public class CoverIllustrationsDecorator extends BookDecorator {
    public CoverIllustrationsDecorator(Book book){
        super(book);
    }
    @Override
    public String decorateBook() {
        return super.decorateBook() + addCoverIllustrations();
    }
    private String addCoverIllustrations() {
        return "\ncover illustrations";
    }
}
```

```
public class BookIllustrationsDecorator extends BookDecorator {
    public BookIllustrationsDecorator(Book book) {
        super(book);
    }
    @Override
    public String decorateBook() {
        return super.decorateBook() + addBookIllustrations();
    }
    private String addBookIllustrations() {
        return "\n book illustrations";
    }
}
```

```
public class VolumeDecorator extends BookDecorator {
    public VolumeDecorator(Book book) {
        super(book);
    }
    @Override
    public String decorateBook() {
        return super.decorateBook() + addVolume();
    }
    private String addVolume() {
        return "\n more than one volume";
    }
}
```

Създаването на обекти и използването на различните декоратори е илюстрирано със следващия програмен фрагмент:

```
public class Main {
    public static void main(String[] args) {
        Book book = new BookImpl();
        Book firstBook = new HardCoverDecorator(book);
        System.out.println("First " + firstBook.decorateBook() + "\n");
        Book secondBook = new HardCoverDecorator(new CoverIllustrationsDecorator(book));
        System.out.println("Second " + secondBook.decorateBook() + "\n");
        Book thirdBook = new VolumeDecorator(new HardCoverDecorator(new BookIllustrationsDecorator(book)));
        System.out.println("Third " + thirdBook.decorateBook() + "\n");
        Book fourthBook = new VolumeDecorator(new VolumeDecorator(book));
        System.out.println("Fourth " + fourthBook.decorateBook());
    }
}
```

Вижда се, че създаването на книги може да става с различни комбинации от декларираните декоратори, както и повече от едно извикване на един и същи декоратор.

Резултатът е следния:

Кога се използва декоратор:

* Когато желаем динамично да добавяме отговорности към обекти без това да оказва влияние върху други такива;
* Когато е необходимо да добавим функционалности към обект, които може да променим в бъдеще;
* Когато добавянето на наследници/подкласове вече не е практично (станат прекалено много).

Предимства от използването на шаблон Декоратор:

* Предоставя по-голяма гъвкавост в сравнение със статичното наследяване;\\
* Разширява и променя поведението на обекта без използване на подкласове/наследници;
* Дава възможност за комбиниране на различни функционалности чрез обгръщане на дадения клас с различни декоратори (последните три обекта в горния пример).

Като недостатък на декоратора може да се посочи факта, че е трудно неговото имплементиране по начин, по който неговото поведение да не зависи от поредността на извикванията.
