---
layout: default
title: Java Architecture for XML Binding
parent: Лабораторно упражнение 3
grand_parent: Интернет технологии
nav_order: 2
---

# Java Architecture for XML Binding

JAXB осигурява бърз и удобен начин за обвързване на XML схеми и Java представителства, като улеснява разработчиците на Java да включват XML данни и функции за обработка в Java приложения. Като част от този процес, JAXB предоставя методи за демаркиране (четене) на документи от XML екземпляри в дървета със съдържание на Java и след това преобразуване (писане) на дървета със съдържание на Java обратно в документи на XML инстанции. JAXB предоставя също начин за генериране на XML схема от Java обекти.

| Анотация                                      | Описание                                                                                                                           |
| ----------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------- |
| @XmlRootElement(namespace = "namespace")        | Дефинира коренния елемент за XML дърво                                                                                                |
| @XmlType(propOrder = { "field2", "field1",.. }) | Позволява да се определи редът, в който полетата се записват във XML файла                                                            |
| @XmlElement(name = "newName")                   | Определя XML елемента, който ще бъде използван. Използва се когато XML името е различно от Java полетo |
|@XmlAccessorType(XmlAccessType.TYPE)| Определя по какъв начин JAXB достъпва данните в класа — дали чрез полета, getter/setter методи, публични членове или само изрично анотирани елементи
@XmlElementWrapper(name = "wrapperName")| Създава обвиващ родителски XML елемент около списък от елементи
@XmlTransient| Изключва поле или метод от XML сериализацията

<br>

| Стойност na AccessType       | Описание                                                      |
| --------------- | ------------------------------------------------------------- |
| `FIELD`         | JAXB използва директно всички полета на класа                 |
| `PROPERTY`      | JAXB използва getter/setter методите                          |
| `PUBLIC_MEMBER` | JAXB използва публични полета и публични getter/setter методи |
| `NONE`          | Само елементи с изрично зададени JAXB анотации се обработват  |

<br>

###

### JAXB библиотека

[https://mvnrepository.com/artifact/org.glassfish.jaxb/jaxb-runtime](https://mvnrepository.com/artifact/org.glassfish.jaxb/jaxb-runtime)

### Дефиниране на клас, моделиращ данните в XML файла

```java
@XmlRootElement(name = "student")
@XmlAccessorType(XmlAccessType.FIELD)
// Не е задължително но ако искате, можете да определите реда, в който да се запишат полетата
@XmlType(propOrder = {"fakNum", "name", "specialty"})
public class Student {

    private String name;
    //Ако името на променливата не е подходящо за записване в XML файл, можете лесно да се промените
    @XmlElement(name = "fakNum")
    private String number;
    private String specialty;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
}
```

### Създаване на клас за съдържанието на XML файла

```java
//Тази анотация означава, че клас "Group.java" е основният елемент на файла
@XmlRootElement(name = "group")
@XmlAccessorType(XmlAccessType.FIELD)
public class Group {

    // XmLElementWrapper генерира обвиващ елемент около XML елементите, родителския елемент
    @XmlElementWrapper(name = "studentList")
    // XmlElement задава името на вътрешните елементи
    @XmlElement(name = "student")
    private ArrayList<Student> students;
    private int number;
    private String department;

    public void setStudents(ArrayList<Student> students) {
        this.students = students;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
```

### Трансформиране в XML

Добавяне на XSD схема за валидация:

```java
String xsdFile = this.getClass().getClassLoader().getResource("xml/person.xsd").getPath();
```

```java
public void writeToXML(Writer writer, Group group) {

    // Създаване на JAXB контекст
    JAXBContext context = JAXBContext.newInstance(Group.class);
    // Създаване на marshaller инстанция
    Marshaller m = context.createMarshaller();
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

    // Записване в поток
    m.marshal(group, writer);

}
```

###

### Трансформиране от XML

```java
public Group readerFromXML(String xml) {

    // Създаване на JAXB контекст
    JAXBContext context = JAXBContext.newInstance(Group.class);
    // Създаване на unmarshaller инстанция
    Unmarshaller um = context.createUnmarshaller();

    //Валидиране с xsd
    SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = sf.newSchema(new File(xsdFile));
    um.setSchema(schema);

    Group group = (Group) um.unmarshal(new StringReader(xml));
    return group;
}
```
