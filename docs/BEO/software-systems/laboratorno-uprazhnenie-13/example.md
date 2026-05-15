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
<groupId>bg.tu_varna.sit.ps</groupId>
<artifactId>PS-Projects</artifactId>
<version>1.0-SNAPSHOT</version>
```

Това определя:

* организацията - groupId;
* името на проекта - artifactId;
* версията - version. SNAPSHOT в номера на версията означава, че това все още е версия в разрботка.

Maven ще създаде:

```text
PS-Projects-1.0-SNAPSHOT.jar
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
./mvnw clean javafx:run
```

Резултата трябва да е:

`
Error: JAVA_HOME not found in your environment. 
Please set the JAVA_HOME variable in your environment to match the 
location of your Java installation. 
`

Това означава, че в операционната система не е инстанцирана виртуална машина за изпълнение на Java приложения.

## Инициялизация през Java SDK в IntelliJ

В IntelliJ Отворете Меню -> File ->  Project Structure

В отворилия се прозорец изберете Platform Settings -> SDKs, това е списък с версиите на Java SDK инсталирани в IntelliJ

Избирате версията с ковто е създаден проекта `openjdk-21`

В JDK home path се зе покаже пътя до Java SDK

`C:\Users\OOP\.jdks\openjdk-21` - копирайте го.

## Създаване на системна променлива JAVA_HOME

В windows search въведете: `edit environment variables for your account` 

В отворения прозорец:

<img width="600" height="568" alt="image" src="https://github.com/user-attachments/assets/9e44f0ee-b3a6-4774-a9ee-ce23873de1f0" />

Трябва да се попълни `User variables for ...` с бутона `New`

Variable name: JAVA_HOME
Variable value: C:\Users\OOP\.jdks\openjdk-21

Когато е създадена променливата на средата JAVA_HOME.

Трябва да се дефинира пътя до изпълнимия файл на JRE:

Променливата Path се отваря в Edit

<img width="516" height="487" alt="image" src="https://github.com/user-attachments/assets/79caa51c-53e6-4ba3-93b3-43b582fa0c87" />

Създава се нов пър с бутона `New`:

Пътя трябва да е `%JAVA_HOME%\bin`

Записвате промените в променливата Path и в Enviroment Variables.

Следващата стъпка е рестартиране на IntelliJ за да може терминала в IntelliJ да прочете новите пеоменливи.

## Стартиране отново

```bash
./mvnw clean javafx:run
```

Резултата трябва да е стартирано приложение, това кое приложение ще се изпълнизависи от pom.xaml конфигуразията `<mainClass>`

---

# Main Class

```xml
<mainClass>
    bg.tu_varna.sit.ps.lab9.task1.Launcher
</mainClass>
```

Това е началният клас на приложението. Сменете го с класа, който стартира приложението DeveloperApplication, на всички места където се ползва.

---

# Maven Dependency Plugin

Създаването на изпълним файл изисква добавянето на няколко плъгина:

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
./mvnw clean package
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
<mainJar>PS-Projects-1.0.jar</mainJar> 
```

Основният `.jar` файл. Файла тряба да отговаря на дефинираното име и версия в 

``
    <artifactId>PS-Projects</artifactId>
    <version>1.0</version>
```

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
./mvnw clean javafx:run
```

---

# 9. Създаване на изпълним файл

## Команда

```bash
./mvnw clean package
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

                    <mainJar>PS-Projects-1.0.jar</mainJar>

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
