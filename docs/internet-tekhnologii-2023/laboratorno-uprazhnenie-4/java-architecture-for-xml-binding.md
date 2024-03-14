---
layout: default
title: Java Architecture for XML Binding
parent: Лабораторно упражнение 4
grand_parent: Интернет технологии
nav_order: 2
---

# Java Architecture for XML Binding

JAXB осигурява бърз и удобен начин за обвързване на XML схеми и Java представителства, като улеснява разработчиците на Java да включват XML данни и функции за обработка в Java приложения. Като част от този процес, JAXB предоставя методи за демаркиране (четене) на документи от XML екземпляри в дървета със съдържание на Java и след това преобразуване (писане) на дървета със съдържание на Java обратно в документи на XML инстанции. JAXB предоставя също начин за генериране на XML схема от Java обекти.

| Annotation                                      | Description                                                                                                                           |
| ----------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------- |
| @XmlRootElement(namespace = "namespace")        | Дефинира коренния елемент за XML дърво                                                                                                |
| @XmlType(propOrder = { "field2", "field1",.. }) | Позволява да се определи редът, в който полетата се записват във XML файла                                                            |
| @XmlElement(name = "neuName")                   | Определя XML елемента, който ще бъде използван. Трябва да се използва само ако името на XML елемента е различно от името на JavaBeans |

###

### JAXB библиотека

https://mvnrepository.com/artifact/org.glassfish.jaxb/jaxb-runtime

### Дефиниране на клас, моделиращ данните в XML файла

```
@XmlRootElement(name = "student")
@XmlAccessorType(XmlAccessType.FIELD)
// Не е задължително но ако искате, можете да определите реда, в който да се запишат полетата
@XmlType(propOrder = { "fakNum", "name", "specialty")
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

```
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
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

### Трансформиране от XML

```
String xsdFile = this.getClass().getClassLoader().getResource("xml/person.xsd").getPath();
```

```
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

### Трансформиране в XML

```
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
