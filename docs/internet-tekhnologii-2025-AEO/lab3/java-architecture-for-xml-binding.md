---
layout: default
title: Java Architecture for XML Binding
parent: Laboratory excercise 3
grand_parent: Internet Technologies
nav_order: 5
---


# Java Architecture for XML Binding

JAXB provides a fast and convenient way to bind XML schemas and Java representations, making it easier for Java developers to include XML data and processing functions in Java applications. As part of this process, JAXB provides methods for demarcating (reading) documents from XML instances into Java content trees and then converting (writing) Java content trees back into XML instance documents. JAXB also provides a way to generate an XML schema from Java objects.

| Annotation                                      | Description                                                                                                                           |
| ----------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------- |
| @XmlRootElement(namespace = "namespace")        | Defines the root element for an XML tree                                                                                                |
| @XmlType(propOrder = { "field2", "field1",.. }) | Allows you to specify the order in which fields are written in the XML file                                                            |
| @XmlElement(name = "neuName")                   | Specifies the XML element to be used. Should only be used if the XML element name is different from the JavaBeans name |

###

### JAXB library

https://mvnrepository.com/artifact/org.glassfish.jaxb/jaxb-runtime

### Defining a class that models the data in the XML file

```
@XmlRootElement(name = "student")
@XmlAccessorType(XmlAccessType.FIELD)
// It is not mandatory, but if you want, you can specify the order in which the fields are written.
@XmlType(propOrder = { "fakNum", "name", "specialty")
public class Student {

    private String name;
    //If the variable name is not suitable for writing to an XML file, you can easily change
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

### Creating a class for the content of the XML file

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

### Transform to XML

```
String xsdFile = this.getClass().getClassLoader().getResource("xml/person.xsd").getPath();
```

```
public void writeToXML(Writer writer, Group group) {

    // Creating a JAXB context
    JAXBContext context = JAXBContext.newInstance(Group.class);
    // Creating a marshaller instance
    Marshaller m = context.createMarshaller();
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

    // Recording to a stream
    m.marshal(group, writer);

}
```

###

### Transform from XML

```
public Group readerFromXML(String xml) {

    // Creating a JAXB context
    JAXBContext context = JAXBContext.newInstance(Group.class);
    // Creating an unmarshaller instance
    Unmarshaller um = context.createUnmarshaller();

    //Validation with xsd
    SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = sf.newSchema(new File(xsdFile));
    um.setSchema(schema);

    Group group = (Group) um.unmarshal(new StringReader(xml));
    return group;
}
```
