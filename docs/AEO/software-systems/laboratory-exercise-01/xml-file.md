---
layout: default
title: The XML File
parent: Laboratory Exercise 1
grand_parent: Software Systems
nav_order: 2
#permalink: /docs/AEO/software-systems/laboratory-exercise-01/xml-file
---

# Introduction to XML

XML (eXtensible Markup Language) is a text-based format for structured data description. It uses tags to organize information into a clearly defined hierarchy. XML is not a programming language but a markup language that allows data to be readable both by humans and computer systems.

XML documents are widely used for configuration files, data exchange, and structure description. One of the most common examples in Java projects is the **pom.xml** file.

## Structure of an XML Document

Every XML document starts with a declaration that specifies the XML version and the encoding used:

```xml
<?xml version="1.0" encoding="UTF-8"?>
```

This declaration informs the system that the file is an XML document created according to version 1.0 of the standard and uses UTF-8 encoding.

After the declaration comes the root element, which encompasses the entire content of the document. In **pom.xml**, this is the `<project>` element:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0">
    ...
</project>
```

The root element is mandatory for every XML document and contains all other elements.

## XML Tags

XML uses tags, which consist of:

* an opening tag;
* content;
* a closing tag.

Example from **pom.xml**:

```xml
<groupId>bg.tu_varna.sit.ps</groupId>
```

Here:

* `<groupId>` is the opening tag;
* `bg.tu_varna.sit.ps` is the element value;
* `</groupId>` is the closing tag.

Every opening tag must have a corresponding closing tag, and their names must match exactly.

## Hierarchical Structure

XML documents have a tree-like (hierarchical) structure, where elements can contain other elements. This allows logical grouping of related information.

Example from **pom.xml**:

```xml
<dependencies>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>21.0.6</version>
    </dependency>
</dependencies>
```

In this example:

* `<dependencies>` is the parent element;
* `<dependency>` is a nested element;
* `<groupId>`, `<artifactId>`, and `<version>` are subordinate to `<dependency>`.

This structure shows that a dependency is described using several related properties.

## XML Attributes

In addition to content between tags, XML elements can have attributes, which are set in the opening tag.

Example from the root element:

```xml
<project
    xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
```

Here:

* `xmlns` and `xmlns:xsi` are attributes that define the XML namespaces used;
* `xsi:schemaLocation` indicates the location of the XML schema against which the document is validated.

Attributes provide additional information about elements without creating new nested tags.

## Repetition and Logical Grouping

XML allows repeating the same structures, which is especially useful for configurations. In **pom.xml**, this can be seen, for example, when defining plugins:

```xml
<plugins>
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.13.0</version>
    </plugin>
</plugins>
```

A parent element (`<plugins>`) can contain many nested elements, each following the same structure (in this case `<plugin>`), which simplifies reading, processing, and maintaining the configuration.
