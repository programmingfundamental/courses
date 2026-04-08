---
layout: default
title: Конфигуриране на H2 DB
parent: Лабораторно упражнение 9
grand_parent: Интернет технологии
nav_order: 1
---

# Добавяне на библиотеки и конфигурации на проекта

За активиране на JPA в Spring Boot приложението се добавя зависимостта `spring-boot-starter-data-jpa`. Необходим е и JDBC драйвер за H2. Spring Boot конфигурира Hibernate да използва JPA по подразбиране.

Във файла `pom.xml` се добавят следните зависимости:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```
---

## 2. Въведение в типовете бази данни и режими на H2

H2 е релационна база данни, написана на Java, предназначена за локална разработка, обучение и тестване. Тя предлага различни **типове съхранение на данни** и **режими на достъп**, които се избират според нуждите на приложението.

- **Типове съхранение на данни** определят дали данните се губят при спиране на приложението или се запазват на диск.  
- **Режими на достъп** определят как приложението или външни клиенти се свързват с базата: директно в приложението или чрез отделен процес (TCP).

Всеки тип и режим има специфичен **JDBC URL**, който се използва за конфигуриране на връзката. В Spring Boot URL-а се дефинира в `application.properties`, а при работа с H2 Console или IntelliJ Data Source се въвежда във формата за връзка.

---

## 3. Видове съхранение на данни в H2

### In-Memory база данни

- Данните се съхраняват изцяло в оперативната памет.  
- Изчезват при спиране на приложението.  
- Много бърза, подходяща за тестове и демонстрации.  
- Не създава физически файл.  

**Примерен JDBC URL:**  
`jdbc:h2:mem:testdb`

Компоненти на URL:

- `jdbc` – използване на JDBC връзка  
- `h2` – тип база данни H2  
- `mem` – in-memory съхранение  
- `testdb` – име на базата  

> **Дефиниция:** Този URL се поставя в `spring.datasource.url` в `application.properties` или във формата на H2 Console / Data Source при тестване.

---

### File-Based база данни

- Данните се записват във физически файл на локалната система.  
- Запазват се между отделните стартирания на приложението.  
- Подходяща за локална разработка и лабораторни упражнения.  

**Примерен JDBC URL:**  
`jdbc:h2:file:./data/tasksdb`

Компоненти на URL:

- `jdbc` – използване на JDBC връзка  
- `h2` – тип база данни H2  
- `file` – данните се записват във файл  
- `./data/tasksdb` – местоположение на файла спрямо проекта    
<br>

> **Дефиниция:** Този URL се дефинира в `application.properties` под `spring.datasource.url` за Spring Boot, а при H2 Console или Data Source в IntelliJ се въвежда във формата за връзка.

---

## 4. Режими на достъп до H2

### Embedded режим

- Базата работи **вътре в приложението**.  
- Не е необходим отделен сървър.  
- Приложението управлява базата директно чрез JDBC.  
- Може да използва както in-memory, така и file-based съхранение.    
<br>

<p align="center">
  <img src="../../../../assets/connection-mode-embedded-2.png" alt="alt text" width="180"/>
</p>


### Server режим

- H2 стартира като **отделен процес**, който слуша на TCP порт.  
- Позволява връзка от няколко клиента едновременно.  
- Данните могат да се съхраняват във файл или in-memory.  

**Примерен JDBC URL:**  
`jdbc:h2:tcp://localhost/~/tasksdb`

Компоненти:

- `jdbc` – използване на JDBC връзка  
- `h2` – тип база данни H2  
- `tcp` – мрежова връзка  
- `localhost` – адрес на сървъра  
- `~/tasksdb` – местоположение на базата     
<br>


<p align="center">
  <img src="../../../../assets/connection-mode-remote-2.png" alt="alt text" width="230"/>
</p>

### Mixed режим

- Комбинира **Embedded и Server** достъп.  
- Приложението има директен локален достъп, а външни клиенти могат да се свързват през TCP.   
<br> 

<p align="center">
  <img src="../../../../assets/connection-mode-mixed-2.png" alt="alt text" width="230"/>
</p>

> Mixed режимът позволява едновременно локален достъп и споделяне между приложения, подходящ за демонстрации и лабораторни упражнения.

---

## 5. Конфигурация на Spring Boot за file-based + embedded

В `src/main/resources/application.properties` се добавят следните настройки:

```properties
spring.datasource.url=jdbc:h2:file:./data/tasksdb 
spring.datasource.driverClassName=org.h2.Driver 
spring.datasource.username=sa
spring.datasource.password= 

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update  

spring.h2.console.enabled=true 
spring.h2.console.path=/h2-console
```


- **`spring.datasource.url=jdbc:h2:file:./data/tasksdb`**  
  Определя местоположението на базата данни, типа (H2) и режима на съхранение (file-based). Файлът се създава в папката `./data` спрямо проекта. Този URL се използва и от Spring Boot при създаване на връзка, и от H2 Console / Data Source в IntelliJ за визуална работа с базата.

- **`spring.datasource.driverClassName=org.h2.Driver`**  
  Указва JDBC драйвера за H2, който Spring Boot използва, за да се свърже с базата.

- **`spring.datasource.username=sa`**  
  Стандартно потребителско име за H2. Позволява достъп без предварителна регистрация на потребител.

- **`spring.datasource.password=`**  
  Позволява работа без парола в локална среда. Може да се добави парола при нужда за защита.

- **`spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect`**  
  Указва SQL диалекта за Hibernate, специфичен за H2. Това позволява правилно генериране на SQL команди.

- **`spring.jpa.hibernate.ddl-auto=update`**  
  Определя как Hibernate управлява схемата на базата: проверява, създава или обновява таблиците според entity класовете. Стойността `update` добавя промени без да изтрива данни.

- **`spring.h2.console.enabled=true`**  
  Активира уеб конзолата на H2 за визуална работа и изпълнение на SQL заявки.

- **`spring.h2.console.path=/h2-console`**  
  Определя URL адреса, на който е достъпна H2 Console. По подразбиране това е `http://localhost:8080/h2-console`.   
  <br>

> URL за JDBC се дефинира тук, за да може Spring Boot да създаде връзката при стартиране. H2 Console и IntelliJ Data Source използват същия URL за визуална работа с базата.

---

## 6. Управление на схемата на базата данни

Свойството `spring.jpa.hibernate.ddl-auto` определя как Hibernate управлява схемата на базата данни.  

| Стойност       | Действие                                                                                 | Кога се използва                           |
|----------------|------------------------------------------------------------------------------------------|-------------------------------------------|
| `validate`     | Проверява дали съществуващата схема съответства на entity класовете                        | Ако базата се управлява външно           |
| `create`       | Създава схема при стартиране, премахва предходните данни                                   | Тестове или първоначална разработка      |
| `create-drop`  | Създава схема при стартиране, премахва я при спиране                                       | Временни тестове                          |
| `update`       | Добавя нови промени към съществуващата схема, без да изтрива данни                         | Локална разработка с постоянни данни     |

---

## 7. Стартиране на H2 Console и използване на Data Source в IntelliJ

Spring Boot приложенията, които използват H2 база данни, предлагат **две основни възможности за визуална работа с базата**:

1. **H2 Console** – уеб интерфейс, стартиран от приложението.  
2. **Data Source в IntelliJ Ultimate** – инструмент за управление на базата директно в IDE.  

И двете дават достъп до таблици, съдържание и възможност за изпълнение на SQL заявки, но има различия в начина на достъп и възможностите за конфигурация.

### 7.1. H2 Console

За да бъде достъпна H2 Console в Spring Boot, е необходимо първо да бъде добавена зависимостта за H2 Console в `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-h2console</artifactId>
</dependency>
```

- Стартира се приложението (Embedded Tomcat слуша на порт 8080).  
- Отваря се браузър: `http://localhost:8080/h2-console`  
- Попълва се формата:  
  - JDBC URL: `jdbc:h2:file:./data/tasksdb`  
  - User Name: `sa`  
  - Password: празно    
  <br>


  <p align="center">
  <img src="../../../../assets/h2-form.png" alt="alt text" width="350"/>
</p>

- Натиска се **Connect**  
- Могат да се видят всички таблици, съдържание и да бъдат изпълнени SQL заявки:    
<br>

<p align="center">
  <img src="../../../../assets/h2-connected.png" alt="alt text" width="400"/>
</p>


### 7.2. Data Source в IntelliJ Ultimate

- Отваря се **Database** tool window → **+ → Data Source → H2**  

<p align="center">
  <img src="../../../../assets/data-source-create.png" alt="alt text" width="400"/>
</p>

- Въвеждат се:  
  - URL: `jdbc:h2:file:./data/tasksdb`  
  - User: `sa`  
  - Password: празно  
  <br>

<p align="center">
  <img src="../../../../assets/datasource-conf.png" alt="alt text" width="400"/>
</p>

- При необходимост се изтеглят необходимите драйвери.Натиска се **Test Connection → OK** за проверка на връзката. След това се натиска **OK** за свързване.
- Таблиците и SQL панела са достъпни за преглед.

<p align="center">
  <img src="../../../../assets/datasource-view.png" alt="alt text" width="300"/>
</p>


