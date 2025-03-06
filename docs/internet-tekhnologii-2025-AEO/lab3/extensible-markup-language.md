---
layout: default
title: Extensible Markup Language
parent: Laboratory excercise 3
grand_parent: Internet Technologies
nav_order: 4
---

# Extensible Markup Language

An XML document consists of elements, each element having a start tag, content, and an end tag. An XML document must have exactly one root element (i.e., a tag that encloses all other tags). XML is case-sensitive.

An XML file must be well-formed. This means that it must meet the following conditions:

* An XML document always begins with a prolog that describes the XML file.

An XML document always begins with a prolog that describes the XML file. This prolog can be minimal, for example:

```
<?xml version="1.0"?>
```

But it can also contain other information, such as the encoding

```
<?xml version="1.0"?><?xml version="1.0" encoding="UTF-8" standalone="yes" ?>
```

* Every opening tag has a closing tag.

An element that does not enclose any content is known as an "empty tag", for example `<flag/>`

* All tags are fully nested.

It is relatively easy to process an XML document compared to a binary or unstructured format. This is due to the following characteristics:

* XML represents data without specifying how the data should be displayed;
* XML can be transformed into other formats using XSL;
* XML can be easily processed using standard parsers;
* XML files are hierarchical.

The XML format is relatively verbose, i.e. if the data is represented as XML, the size of that data is relatively large compared to other formats. On the Internet, JSON or binary formats are often used to replace XML if the data is important.

1.3. XML elements

| Java Class                              | XML Data Type    |
| --------------------------------------- | ---------------- |
| java.lang.String                        | xs:string        |
| java.math.BigInteger                    | xs:integer       |
| java.math.BigDecimal                    | xs:decimal       |
| java.util.Calendar                      | xs:dateTime      |
| java.util.Date                          | xs:dateTime      |
| javax.xml.namespace.QName               | xs:QName         |
| java.net.URI                            | xs:string        |
| javax.xml.datatype.XMLGregorianCalendar | xs:anySimpleType |
| javax.xml.datatype.Duration             | xs:duration      |
| java.lang.Object                        | xs:anyType       |
| java.awt.Image                          | xs:base64Binary  |
| javax.activation.DataHandler            | xs:base64Binary  |
| javax.xml.transform.Source              | xs:base64Binary  |
| java.util.UUID                          | xs:string        |

2. XML elements are validated with an XSD schema.

The schema contains a description of the syntax of the XML file. The names of the elements, their child elements, and the data types that can be the values ​​of the elements.

Example schema for validating an XML file with a person object

```
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified">
    <xs:element name="person">
        <xs:complexType>
            <xs:sequence>
                <xs:element ref="name"/>
                <xs:element ref="age"/>
            </xs:sequence>
        </xs:complexType>
    </xs:element>
    <xs:element name="name" type="xs:string"/>
    <xs:element name="age" type="xs:integer"/>
</xs:schema>
```

2.1. Generating a schema in resources

- Create an XML input file
- Select Tools -> XML Actions -> Generate XSD Schema from XML file
- Place the resulting file in the resources directory
