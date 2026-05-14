---
layout: default
title: Примерна задача
parent: Лабораторно упражнение 13
grand_parent: Програмни системи
nav_order: 1
---

# 1. Примерна задача

Да се създаде възможност JavaFX приложението **DeveloperApp** да бъде пакетирано като изпълним файл.


Крайният резултат трябва да бъде папка:

```text
destination/DeveloperApp/
```

В нея трябва да има стартиращ файл:

```text
DeveloperApp.exe
```

или съответния изпълним файл за операционната система.

---

# 6. Настройки в pom.xml

# Основна информация за проекта

```xml
<groupId>bg.tu_varna.sit</groupId>
<artifactId>javaFX-project</artifactId>
<version>1.0</version>
```

Това определя:

* организацията;
* името на проекта;
* версията.

Maven ще създаде:

```text
javaFX-project-1.0.jar
```

---


# 7. Build Plugins

# Maven Compiler Plugin

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
</plugin>
```

## Предназначение

Компилира Java кода.

---

## Java Version

```xml
<source>21</source>
<target>21</target>
```

Проектът използва Java 21.

---

# JavaFX Maven Plugin

```xml
<plugin>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-maven-plugin</artifactId>
</plugin>
```

## Предназначение

Позволява приложението да се стартира чрез Maven.

---

## Стартиране

```bash
mvn clean javafx:run
```

---

# Main Class

```xml
<mainClass>
    bg.tu_varna.sit.ps.lab9.task1.Launcher
</mainClass>
```

Това е началният клас на приложението.

---

# jlink Configuration

```xml
<launcher>app</launcher>
<jlinkZipName>app</jlinkZipName>
<jlinkImageName>app</jlinkImageName>
```

## Предназначение

Създава custom runtime image.

---

## Оптимизация

```xml
<noManPages>true</noManPages>
<stripDebug>true</stripDebug>
<noHeaderFiles>true</noHeaderFiles>
```

Намалява размера на приложението.

---

# Maven Dependency Plugin

```xml
<plugin>
    <artifactId>maven-dependency-plugin</artifactId>
</plugin>
```

## Предназначение

Копира runtime зависимостите в папката:

```text
target/
```

---

## Важна конфигурация

```xml
<phase>package</phase>
```

Plugin-ът се изпълнява при:

```bash
mvn clean package
```

---

# jpackage Maven Plugin

```xml
<plugin>
    <artifactId>jpackage-maven-plugin</artifactId>
</plugin>
```

## Предназначение

Създава изпълнимо приложение.

---

# Име на приложението

```xml
<name>DeveloperApp</name>
```

---

# Входна папка

```xml
<input>${project.build.directory}</input>
```

Използва:

```text
target/
```

---

# Main JAR

```xml
<mainJar>javaFX-project-1.0.jar</mainJar>
```

Основният `.jar` файл.

---

# Тип на приложението

```xml
<type>APP_IMAGE</type>
```

Създава папка с готово приложение.

---

# Destination

```xml
<destination>${project.basedir}/destination</destination>
```

Крайният резултат се записва в:

```text
destination/
```

---

# winConsole

```xml
<winConsole>false</winConsole>
```

Скрива конзолния прозорец при Windows.

---

# Java Options

```xml
<javaOptions>
```

## Module Path

```xml
<option>--module-path</option>
<option>$APPDIR</option>
```

Казва къде се намират JavaFX модулите.

---

## Add Modules

```xml
<option>--add-modules</option>

<option>
    javafx.controls,javafx.fxml
</option>
```

Зарежда JavaFX модулите.

---

# 8. Стартиране на приложението

## Стартиране от Maven

```bash
mvn clean javafx:run
```

---

# 9. Създаване на изпълним файл

## Команда

```bash
mvn clean package
```

---

# 10. Очакван резултат

След изпълнение на командата трябва да се появи:

```text
destination/DeveloperApp/
```

Вътре трябва да има:

```text
DeveloperApp.exe
```

или съответния изпълним файл за операционната система.

---

# 11. Краен резултат на pom.xml

## Пълен pom.xml файл

```xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <groupId>bg.tu_varna.sit</groupId>
    <artifactId>javaFX-project</artifactId>
    <version>1.0</version>

    <name>javaFX-project</name>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <junit.version>5.12.1</junit.version>
    </properties>

    <dependencies>

        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>2.4.240</version>
        </dependency>

        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-controls</artifactId>
            <version>21.0.6</version>
        </dependency>

        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-fxml</artifactId>
            <version>21.0.6</version>
        </dependency>

        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>

    </dependencies>

    <build>

        <plugins>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>

                <configuration>
                    <source>21</source>
                    <target>21</target>
                </configuration>
            </plugin>

            <plugin>
                <groupId>org.openjfx</groupId>
                <artifactId>javafx-maven-plugin</artifactId>
                <version>0.0.8</version>

                <executions>
                    <execution>

                        <id>default-cli</id>

                        <configuration>

                            <mainClass>
                                bg.tu_varna.sit.ps.lab9.task1.Launcher
                            </mainClass>

                            <launcher>app</launcher>

                            <jlinkZipName>app</jlinkZipName>

                            <jlinkImageName>app</jlinkImageName>

                            <noManPages>true</noManPages>

                            <stripDebug>true</stripDebug>

                            <noHeaderFiles>true</noHeaderFiles>

                        </configuration>

                    </execution>
                </executions>

            </plugin>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <version>3.8.1</version>

                <executions>
                    <execution>

                        <id>copy-dependencies</id>

                        <phase>package</phase>

                        <goals>
                            <goal>copy-dependencies</goal>
                        </goals>

                        <configuration>

                            <outputDirectory>
                                ${project.build.directory}
                            </outputDirectory>

                            <includeScope>runtime</includeScope>

                        </configuration>

                    </execution>
                </executions>

            </plugin>

            <plugin>

                <groupId>org.panteleyev</groupId>

                <artifactId>jpackage-maven-plugin</artifactId>

                <version>1.6.6</version>

                <configuration>

                    <name>DeveloperApp</name>

                    <input>${project.build.directory}</input>

                    <mainJar>javaFX-project-1.0.jar</mainJar>

                    <mainClass>
                        bg.tu_varna.sit.ps.lab9.task1.Launcher
                    </mainClass>

                    <type>APP_IMAGE</type>

                    <destination>
                        ${project.basedir}/destination
                    </destination>

                    <winConsole>false</winConsole>

                    <javaOptions>

                        <option>--module-path</option>

                        <option>$APPDIR</option>

                        <option>--add-modules</option>

                        <option>
                            javafx.controls,javafx.fxml
                        </option>

                    </javaOptions>

                </configuration>

                <executions>

                    <execution>

                        <phase>package</phase>

                        <goals>
                            <goal>jpackage</goal>
                        </goals>

                    </execution>

                </executions>

            </plugin>

        </plugins>

    </build>

</project>
```
