---
layout: default
title: Flyweight
parent: Лабораторно упражнение 8
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 2
---

# Flyweight

Шаблонът за проектиране Flyweight е представител на структурните шаблони. Използва се с цел намаляване броя на създаваните обекти, което от своя страна води до подобряване структурата на разработваното приложение и намалява времето за създаване на обекти. Прилагането на Flyweight е удачно в случаите, в които се налага създаването на множество подобни обекти. Самият flyweight-обект е споделен обект, който може да се използва в различни контексти едновременно.

За имплементиране на шаблона Flyweight са необходими:

- интерфейс;
- една или повече конкретни имплементации на декларирания интерфейс;
- клас с factory-метод, който отговаря за създаването и кеширането на обекти.

Нека разгледаме следния пример:

```
public interface StudentInformation {

    void display();
}
```

Създаваме две конкретни имплементации на горния интерфейс - BachelorStudent и MasterStudent: 

```
public class BachelorStudent implements StudentInformation{

    private String facultyNumber;
    private String firstName;
    private String lastName;

    public BachelorStudent(String facultyNumber, String firstName, String lastName) {
        this.facultyNumber = facultyNumber;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BachelorStudent that = (BachelorStudent) o;
        return Objects.equals(facultyNumber, that.facultyNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(facultyNumber);
    }

    @Override
    public String toString() {
        return "BachelorStudent{" +
                "facultyNumber='" + facultyNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

    @Override
    public void display() {
        System.out.println(this);
    }
}
```

```
public class MasterStudent implements StudentInformation{

    private String facultyNumber;
    private String firstName;
    private String lastName;

    public MasterStudent(String facultyNumber, String firstName, String lastName) {
        this.facultyNumber = facultyNumber;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BachelorStudent that = (BachelorStudent) o;
        return Objects.equals(facultyNumber, that.facultyNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(facultyNumber);
    }

    @Override
    public String toString() {
        return "MasterStudent{" +
                "facultyNumber='" + facultyNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

    @Override
    public void display() {
        System.out.println(this);
    }
}
```

След това за имплементацията на шаблона Flyweight е необходимо създаването на фабричен клас:

```
public class StudentFactory {

    private static Map<String, StudentInformation> students = new HashMap<>();

    public static StudentInformation getInfo(String type, String facultyNumber, String firstName,
                                             String lastName) {
        StudentInformation student = students.get(facultyNumber);

        if (student == null) {
            student = type.equalsIgnoreCase("bachelor")
                    ? new BachelorStudent(facultyNumber, firstName, lastName)
                    : new MasterStudent(facultyNumber, firstName, lastName);
            students.put(facultyNumber, student);
        }

        return student;
    }

    public static long getStudentsNumber() {
        return students.size();
    }
}
```

Колекцията, която е декларирана като атрибут, има за ключ факултетния номер на студента, който еднозначно го идентифицира. Важно е да се отбележи, че след като веднъж обектът е създаден и сложен в колекцията, той не се променя, т.е. обекта е immutable.

Използването на дефинираните класове е следното:

```
public class Application {

    public static void main(String[] args) {
        StudentInformation firstStudent = StudentFactory.getInfo("bachelor", "123456", "John", "Doe");
        StudentInformation secondStudent = StudentFactory.getInfo("master", "112233", "Jane", "Doe");
        StudentInformation thirdStudent = StudentFactory.getInfo("master", "123456", "Jake", "Doe");

        firstStudent.display();
        secondStudent.display();
        thirdStudent.display();

        System.out.println(StudentFactory.getStudentsNumber());
    }
}
```

Въпреки създаването на три обекта с помощта на фабричния метод, в колекцията са записани само 2. Причината за това е, че два от обектите имат еднакъв ключ (еднакъв факултетен номер).


Внедряването на шаблона Flyweight може да направи приложението по-сложно поради разделянето на вътрешните и външните състояния. Въпреки това, тази сложност може да бъде оправдана, ако системата трябва да бъде мащабируема, обработвайки ефективно голям брой обекти.

Докато използването на Flyweight оптимизира използването на паметта, използването на този шаблон за проектиране може да доведе до излишни разходи по отношение на производителността. Постоянните търсения и управление на споделените състояния могат да забавят операциите, особено в сценарии, при които производителността е критична.




