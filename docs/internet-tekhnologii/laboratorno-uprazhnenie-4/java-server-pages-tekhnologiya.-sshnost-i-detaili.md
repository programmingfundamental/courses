# Java Server Pages технология. Същност и детайли

Java Server Pages (JSP) е технология за разработка на уеб страници, които поддържат динамично съдържание. Технологията представлява възможност за разработчиците да вмъкнат Java код в HTML страници чрез използване на специални тагове.

JavaServer Pages е предназначен да изпълнява ролята на потребителски интерфейс за уеб приложения. Уеб разработчиците пишат JSP като текстови файлове, в които се комбинират HTML или XHTML код, XML елементи и вградени действия и команди на Java.

Използването на JSP, позволява събиране на информация от потребителите чрез уеб форми, представяне на записи от база данни или друг източник, създаване на динамични уеб страници.

JSP тагове могат да бъдат използвани за различни цели, като например извличане на информация от база данни или регистриране на предпочитанията на потребителите, достъп до JavaBeans компоненти, предаване на контрол между страници и споделяне на информация между заявки, страници и т.н.

**Предимства на JSP пред подобни технологии**

* в сравнение с Active Server Pages (ASP) - предимствата на JSP са в две посоки. Първо, динамичната част е написана на Java, не Visual Basic или друг MS конкретен език, така че е по-мощен и лесен за използване. Второ, тя е преносим към други операционни системи и уеб сървъри, които не са продукти на Microsoft;
* в сравнение с Servlets - по-удобно е да се пише (и модифицира) обикновен HTML, отколкото да се използват множество println() методи, които генерират HTML;
* в сравнение с Server-Side Includes (SSI) - SSI е предназначен само за прости включвания, а не за "истински" приложения, които използват данни от формуляри, осъществяват връзки с бази от данни и други;
* в сравнение с JavaScript - JavaScript може да генерира HTML динамично на клиента, но не може да си взаимодейства с уеб сървър, за да изпълнява сложни задачи като достъп до бази от данни и обработка на изображения и т.н.
* в сравнение с обикновен HTML – обикновения HTML не може да генерира динамично съдържание.

### JSP процес

* Както и при нормална страница, браузърът изпраща заявка за HTTP към уеб сървър;
* Уеб сървърът идентифицира, че желания ресурс е JSP страница, и препраща заявката към JSP енджина.
* JSP енджинът зарежда JSP файла от диска и го конвертира в съдържание на сървлет. Това представлява преобразуване на всички текстове от структурата на файла в println (), а всички JSP елементи се превръщат в Java код. JSP енджинът компилира сървлета в изпълнима клас и изпраща първоначалната клиентска заявка към сървлет енджина;
* Сървлет енджинът зарежда получения Servlet клас файл и го изпълнява. По време на изпълнение сървлетът генерира съдържание в HTML формат, което сървлет енджинът предава към уеб сървъра като HTTP отговор.
* Уеб сървърът изпраща на браузъра отговора под формата на статично HTML съдържание;
* Накрая уеб браузърът обработва динамично генерираната HTML страница.

[![image](https://user-images.githubusercontent.com/10382663/77285646-294ffe00-6cda-11ea-8c0a-c2fcfcbc2227.png)](https://user-images.githubusercontent.com/10382663/77285646-294ffe00-6cda-11ea-8c0a-c2fcfcbc2227.png)

Обикновено JSP енджинът проверява дали вече съществува сървлет, съответстващ на текущото JSP, и дали датата на изменението на JSP страницата е по-стара от сървлета. Ако тя е по-стара от своя генериран, контейнерът приема, че JSP страницата не е променяна и че генерираният за нея сървлет все още отговаря на съдържанието ѝ. Това прави процеса по-ефективен, отколкото при други скриптови езици (като PHP) и следователно по-бърз. В известен смисъл, JSP страницата е просто още един начин да се напише сървлет, без да се налага на човек да бъде способен да програмира на Java. С изключение на етапа на транслация от JSP страница към сървлет, през останалата част от съществуването си тя се обработва точно като обикновен сървлет.

### Жизнен цикъл на JSP страницата

JSP жизненият цикъл може да се дефинира като целият процес от създаването на страницата до унищожаването ѝ, процес който е подобен на жизнения цикъл на сървлета с допълнителна стъпка - трансформацията от JSP в сървлет. Етапите, през които минава JSP страницата, са следните:

* Транслиране;
* Компилация;
* Инициализация;
* Изпълнение;
* Почистване (изтриване).

Четирите основни фази на JSP жизнен цикъл са много сходни с този на сървлета и са както следва:

[![image](https://user-images.githubusercontent.com/10382663/77285800-8c419500-6cda-11ea-999d-a262945640e0.png)](https://user-images.githubusercontent.com/10382663/77285800-8c419500-6cda-11ea-999d-a262945640e0.png)

### JSP синтаксис, тагове и елементи

#### Скриплети

Скриплетът може да съдържа произволен брой JAVA декларации, променливи, методи или изрази, които са валидни в скриптовите езици:

`<% code fragment %>`

Всеки текст, HTML таг или JSP елемент трябва да са извън scriptlet таговете:

```
<html>
    <head>
        <title>Hello Java</title>
    </head>
    <body>
    Hello Java!<br/>
    <%
        out.println("Your IP address is " + request.getRemoteAddr());   
    %>
    </body>
</html>
```

####

#### JSP декларации

Посредством тях се декларират един или повече променливи или методи, които да са достъпни за използване по-късно във файла JSP. Синтаксисът на декларацията е следният:

`<%! declaration; [ declaration; ]+ ... %>`

```
<%! int i = 0; %>
<%! int a, b, c; %>
<%! Circle a = new Circle(2.0); %>
```

####

#### JSP изрази

Елементът за JSP израз съдържа код, характерен за скриптовите езици, които се обработва и изходът му се конвертира в стринг. Тъй като стойността на израза се превръща в низ, може да се използва израз в рамките на един ред от текст, независимо дали е или не е маркиран с HTML таг, във JSP файл. Елементът за израз може да съдържа всеки текст, който е валиден според езиковата спецификация на Java, но не може да се използва точка и запетая, за да се сложи край на израза. Синтаксисът е следният:

`<%= expression %>`

```
<html>
    <head>
        <title>JSP Expression</title>
    </head>
<body>
    <p>
        Today is date: <%= (new java.util.Date()).toLocaleString()%>
    </p>
    </body>
</html>
```

####

#### JSP директиви

JSP директивата засяга цялостната структура на класа на сървлета. Тя обикновено има следния вид:

`<%@ directive attribute="value" %>`

Има три типа директиви:

* `<%@ page ... %>` - дефинира инструкции към контейнера, които се отнасят за текущата JSP
* `<%@ include ... %>` - включване на файл в процеса на трансформация от JSP в сървлет;
* `<%@ taglib ... %>` - за декларация на библиотека с дефинирани тагове от потребителя, използвани на конкретната страница.

####

#### JSP имплицитни обекти

JSP поддържа девет обекта, които автоматично се дефинират и се наричат имплицитни. Това са:

* request - HttpServletRequest обект, свързан със заявката на клиента;
* response - HttpServletResponse обект, свързан с отговора към клиента
* out – PrintWriter обект, свързан с изпращането на отговора към клиента
* session – HttpSession обект, свързан със заявката на клиента;
* application – ServletContext обект, свързан с контекста на приложението;
* config – ServletConfig обект, свързан със страницата;
* pageContext – капсулира използване на специфични сървърни функции, като например изпълнението на по-сложни JspWriters;
* page – синоним, който се използва, за да се извикват методите, дефинирани в новополучения сървлет клас;
* Exception – обект, който позволява данните за изключения да бъдат достъпни чрез определен JSP.

####

#### JSP действия

JSP действията използват конструкции в XML синтаксис, за да контролират поведението на сървлет енджина. Позволяват събития като например динамично вмъкване на файл, повторно използване на JavaBeans компоненти, препращане на потребителя към друга страница, или генериране на HTML за Java плъгина. Синтаксисът на елемента за действие е следният:

`<jsp:action_name attribute ="value" />`

Елементите за действие са основно предварително дефинирани функции и има следните JSP действия:

* `jsp:include` - включва файл в момента, в който страницата бъде заявена:
* `jsp:useBean` – намира съществуващ или подава заявка за създаване на JavaBean компонент;
* `jsp:setProperty` – задава характеристика на JavaBean компонент;
* `jsp:getProperty` – вмъква характеристика на JavaBean компонент в генерирания отговор;
* `jsp:forward` – пренасочва клиента към друга страница;
* `jsp:plugin` – генерира браузър-специфичен код, който създава OBJECT или EMBED тага за Java плъгина;
* `jsp:element` – динамично създава XML елементи;
* `jsp:attribute` – определя атрибут на динамично създаден XML елемент;
* `jsp:body` – дефинира тяло (body) на динамично създаден XML елемент;
* `jsp:text` – използва се за шаблонни записи на текст в JSP страници или документи.

JSP осигурява пълните възможности на Java да бъдат вградени в уеб приложение. Може да се използват всички API-и конструкции на Java в JSP програмирането, включително проверки на условия, цикли и други.

####

#### Операторът “If..”:

Започва като обикновен скриплет, но всеки ред, на който е затворен с HTML текст, включен между таговете.

```
<%! int day = 3; %>
<html>
    <head>
        <title>IF...ELSE Example</title>
    </head>
<body>
    <% if (day == 1 | day == 7) { %>
        <p> Today is weekend</p>
        <% } else { %>
        <p> Today is not weekend</p>
        <% } 
    %>
    </body>
</html>
```

####

#### Jsp дава възможност за използването на всички основни цикли от Java

Пример за for цикъл:

```
<%! int fontSize; %>
<html>
    <head>
        <title>FOR LOOP Example</title>
    </head>
    <body>
        <%for ( fontSize = 1; fontSize <= 3; fontSize++){ %>
        <font color="green" size="<%= fontSize %>">
            JSP Tutorial
        </font>
        <br />
    <%}%>
    </body>
</html>
```

####

#### Пример за while цикъл

```
<%! int fontSize; %>
<html>
    <head>
        <title>WHILE LOOP Example</title>
    </head>
    <body>
        <%while ( fontSize <= 3){ %>
        <font color="green" size="<%= fontSize %>">
            JSP Tutorial
        </font><br />
        <%fontSize++;%>
        <%}%>
    </body>
</html>
```