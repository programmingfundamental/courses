---
layout: default
title: First Project with JavaFX
parent: Laboratory Exercise 1
grand_parent: Software Systems
nav_order: 3
permalink: /docs/BEO/software-systems/laboratorno-uprazhnenie-1/purvi-proekt-s-JavaFX
---

# Създаване на първи JavaFX проект в Intellij IDE.

След като стартирате изпълнимия файл, ще се отвори Intellij Welcome screen, кликвате върху New Project. Отваря се следния екран за конфигурация на проекта:

<br>
<img width="784" height="697" alt="Screenshot 2026-01-23 115515" src="https://github.com/user-attachments/assets/741d788e-bad1-4003-b69c-0bbfe8524e42" /><br>    
<br>


Избира се JavaFx в опциите вляво. При създаване на проект чрез JavaFX генератора IntelliJ автоматично подготвя начална структура за JavaFX приложение, включително основен клас и конфигурация за стартиране. Така JavaFX библиотеките се добавят автоматично от средата за разработка и не е необходимо ръчно конфигуриране на зависимости на този етап.

 - За нуждите на проекта се използва папката по подразбиране на избраното от вас работно пространство.
 - В процеса на конфигуриране се избира Project SDK, като се препоръчва използването на инсталирания локално JDK. За език се избира Java, а за система за изграждане - Maven.
 - Идентификатор на групата, от която е част проекта (Group). Group е един от ключовите идентификатори на проекта и обикновено се основава като пълното квалифицирано име на домейн на организация. Например bg.tu.varna.sit.ps.
 - Идентификатор на проекта/артефакт (Artifact) - уникалното базово име на основния артефакт, генериран от този проект. Основният артефакт за проект обикновено е JAR файл. Останалите артефакти в проекта използват името на основния артефакт, за да образуват своите.

След като са избрани нужните опции, се натиска бутона Next като не се променя нищо в следващия прозорец и се натиска бутона Create. Изчаква се проектът да зареди. 

## Конфигурация 
 
Тъй като проектите, създадени чрез JavaFX генератора, по подразбиране използват модулна структура, в проекта се създава файл module-info.java, съдържащ описанието на използваните JavaFX модули. В следващите няколко стъпки ще се промени структурата от модулна към монолитна с цел улесняване на процеса на обучение и фокусиране върху основните концепции на езика и технологията.


 - Изтриване на файла module-info.java - Десен бутон върху файла -> Delete    

 - Промяна на конфигурацията в pom.xml

След като е изтрит файла module-info.java, проектът вече няма да използва модулна система. За да се стартира JavaFX приложението правилно в монолитна структура, е необходимо да се направят няколко промени в `pom.xml`.

[Въведение в XML](https://programmingfundamental.github.io/courses/docs/BEO/software-systems/laboratorno-uprazhnenie-1/failut-xml)

### **1. Отваряне на pom.xml - Намира се в основната директория на проекта.**

<img width="728" height="527" alt="Screenshot 2026-01-25 235807" src="https://github.com/user-attachments/assets/0acfc5ff-3a11-47be-8bb8-00d0e42a3312" />

 - Намират се таговете `<build>` → `<plugins>` → `<plugin>` за javafx-maven-plugin.

Текуща конфигурация: 

```xml
<plugin>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-maven-plugin</artifactId>
    <version>0.0.8</version>
    <executions>
        <execution>
            <id>default-cli</id>
            <configuration>
                <mainClass>bg.tu_varna.sit.ps.demo1/bg.tu_varna.sit.ps.demo1.HelloApplication</mainClass>
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
```


### **2. Редакция на таговете:**

 Текущата структура има двойно посочване на пакети (bg.tu_varna.sit.ps.demo1/bg.tu_varna.sit.ps.demo1.HelloApplication), което е специфично за модулни проекти.

**2.1. Променя се `<mainClass>` като се посочва пълното име на класа с пакета:**      

 `<mainClass>bg.tu_varna.sit.ps.myfirstjavafxproject.HelloApplication</mainClass>`


**2.2. Коментират се или се изтриват ненужните тагове:**

Таговете `<launcher>`, `<jlinkZipName>`, `<jlinkImageName>`, `<noManPages>`, `<stripDebug>`, `<noHeaderFiles>` са специфични за модулен проект и jlink.

За монолитен проект не са нужни и могат да бъдат закоментирани (обгражда се съдържанието с <!-- и -->): 

```xml
<!--
<launcher>app</launcher>
<jlinkZipName>app</jlinkZipName>
<jlinkImageName>app</jlinkImageName>
<noManPages>true</noManPages>
<stripDebug>true</stripDebug>
<noHeaderFiles>true</noHeaderFiles>
-->
```

**2.3. Добавяне на таг `<options>`**

Целта е да се добавят JVM аргументи, с които ще се даде достъп на проекта до пакети на Java, които иначе са блокирани от модулната система.      

```xml
<options>
    <option>--add-opens</option>
    <option>java.base/java.lang=ALL-UNNAMED</option>
</options>
```

Това казва на JVM да „отвори“ пакета `java.lang` за всички класове в монолитния проект, което гарантира, че приложението ще стартира правилно без `module-info.java`.


### 3. Създаване на пакет и основни класове

След като проектът вече е монолитен и pom.xml е конфигуриран, следва да се създаде структурата за Java код и основните класове за стартиране на JavaFX приложението.

**3.1. Създаване на пакет**
 - В панела Project, навигирайте до src/main/java.
 - Десен бутон → New → Package.
 - Въвежда се името на пакета:

 `bg.tu_varna.sit.ps.lab1`

 **3.2. Създаване на основен клас HelloApplication**
 - Десен бутон върху пакета lab1 → New → Java Class.
 - Име на класа: HelloApplication.

 Идеята на този клас е да съдържа кода за JavaFX интерфейса. Методът start(Stage stage) в него да създава прозореца и контролите.

**3.3. По същия начин се създава и клас Launcher**

Този клас ще служи за безопасно стартиране на JavaFX приложението. Тук се поставя main(), който ще стартира HelloApplication.

### 4. Създаване на първа програма

[Отвори раздел за създаване на първа програма с JavaFx.](https://programmingfundamental.github.io/courses/docs/BEO/software-systems/laboratorno-uprazhnenie-1/purva-programa-s-JavaFX)


 
