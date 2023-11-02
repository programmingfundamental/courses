---
layout: default
title: Класа String
parent: Лабораторно упражнение 7
grand_parent: Обектно-ориентирано програмиране - 1 част
nav_order: 2
---
# String

#### ASCII И UNICODE таблица ASCII е предшественика на UNICODE таблицата.&#x20;

В ASCII таблицата се записват 128 или 256 символа (7 или 8 битова) - цифри, малки и главни букви и специлани символи. Когато е необходимо да се работи със символи ралични от латинските, нама как да се изпoлзва ASCII таблицата.&#x20;

Java използва 16-битовата таблица за символи UNICODE в нея се съхраняват 216=65536 символа, а при комбиниране на 2 символа за получаване на специфичен нов знак можем да образуваме до 100 000 знака.&#x20;

#### String&#x20;

Класът java.lang.String позволява обработка на символни низове в Java.&#x20;

Вътрешното предствяне на String класа представлява масив от символи(char).&#x20;

Класът String спазва принципите на обектно-ориентираното програмиране: стойностите се записват в динамичната памет, а променливите пазят препратка към паметта (референция към обект в динамичната памет). Той има важна особеност – последователностите от символи, записани в променлива от класа, са неизменими (immutable). Веднъж записано, съдържанието на променливата не се променя директно - ако опитаме да променим стойността, тя ще бъде записана на ново място в динамичната памет, а променливата ще започне да сочи към него. Пр.: Ако няколко променливи сочат към една и съща област в паметта с дадена стойност, тази стойност не може да бъде директно променена. Промяната ще се отрази само на променливата, чрез която е редактирана стойността, тъй като това ще създаде нова стойност в динамичната памет и ще насочи въпросната променлива към нея, докато останалите променливи ще сочат на старото място.&#x20;

#### Деклариране на символен низ&#x20;

Можем да декларираме променливи от тип символен низ чрез класа java.lang.String: String str; Декларацията на символен низ представлява декларация на обект от класа String. Това не е еквивалентно на създаването на променлива и заделянето на памет за нея! С декларацията уведомяваме компилатора, че ще използваме променлива str и очакваният тип за нея е String. Ние не създаваме променливата в паметта и тя все още не е достъпна за обработки (има стойност null, което означава липса на стойност) и опитът за манипулация на такъв стринг ще генерира грешка (изключение за достъп до липсваща стойност NullPointerException)!&#x20;

#### Деклариране, създаване и инициализиране на символен низ&#x20;

За да може да обработваме декларираната променлива, трябва да я създадем и инициализираме.&#x20;

Създаването на променлива на клас (познато още като инстанциране) е процес, свързан със заделянето на област в динамичната памет. Инициализиране променливи:

* чрез символна константа String helloWorld = "Hello, world!";
* чрез присвояване стойността на друг символен низ Присвояването на стойността е еквивалентно на насочване на String променлива към друга променлива от същия тип. Пример за това е следният фрагмент:&#x20;
* String source = "Some source";&#x20;
* String assigned = source;&#x20;

![](<../../../assets/image (1).png>)

* Променливата assigned приема стойността на source. Тъй като класът String е референтен тип, "Some source" е записано в динамичната памет (heap, хийп), сочено от първата променлива. Така двата обекта имат една и съща стойност:
* чрез предаване стойността на операция, връщаща символен низ Това може да бъде резултат от метод, който валидира данни; събиране на стойностите на няколко константи и променливи, преобразуване на съществуваща променлива и др. Пример за израз, връщащ символен низ след конкатенация: String email = "some@email.bg"; String info = "My mail is: " + email + "."; // My mail is: some@email.bg.&#x20;

#### Отпечатване на символни низове&#x20;

Извеждането на данни се извършва чрез изходния поток System.out: System.out.println("Your name is: " + name); Използвайки метода println(…) извеждаме съобщението: Your name is:, придружено със стойността на променливата name. След края на съобщението се добавя символ за нов ред, като следващото съобщение ще бъде изведено на следващия ред на конзолата. Ако искаме да избегнем символа за нов ред и съобщенията да се извеждат на един и същ, тогава прибягваме към метода print(…). В случай, че ни трябва по-прецизен форматиран изход, на помощ идва методът printf(…): System.out.printf("Hello, %s, have a nice reading!", name);&#x20;

#### Escaping при символните низове&#x20;

Ако искаме да използваме кавички в съдържанието, тогава трябва да поставим наклонена черта преди тях за указание на компилатора. String quote = "Book’s title is "Intro to Java""; // Book's title is "Intro to Java" Кавичките този път са част от текста. В променливата те са добавени чрез поставянето им след екраниращия знак (escaping character) обратна наклонена черта (). По този начин компилаторът разбира, че кавичките не служат за начало или край на символен низ, а са част от данните. Наклонената черта се използва за символи, които играят специална роля в текста (в случая кавичките) или за дефиниране на действие, което не може да се изрази със символ. Пример за втория случай са обозначаването на символ за нов ред (\n), табулация (\t), избор на символ по неговия Unicode (\uXXXX, където с X се обозначава кодът) и др.

* \’ - Единична кавичка
* \”- Двойна кавичка
* \ - Наклонена черта&#x20;

#### Сравнение за еднаквост

* equals() – прави разлика меджу малки и главни букви
* eqalsIgnoreCase() – пренебрегва регистъра, сравнява само съдържанието на стринга. String word1 = "Java"; String word2 = "JAVA"; System.out.println(word1.equals(word2)); // false System.out.println(word1.equalsIgnoreCase(word2)); // true&#x20;

#### Сравнение на низове по азбучен ред

* compareTo(…) – сравнява като рпави разлика между малки главни букви.
* compareToIgnoreCase(…) – сравнява като пренебрегва регистъра. compareTo(…) връща положително число, отрицателно число или 0 в зависимост от лексикографската подредба на символните низове, които сравняваме. Изчислението се извършва като взимаме уникалния код от Unicode таблицата за всеки символ и изчилсяваме разликата. String str1= "abc"; String str2 = "aBcd"; System.out.println(str1.compareTo(str2)); //32 System.out.println(str2.compareTo(str1)); //-32 System.out.println(str1.compareToIgnoreCase(str2)); //-1 System.out.println(str2.compareToIgnoreCase(str1)); //1&#x20;

#### Долепване на низове (конкатенация)&#x20;

* Долепването на символни низове и получаването на нов низ се нарича конкатенация. То може да бъде извършено по 2 начина: чрез метода concat(…): String greet = "Hello, "; String name = "reader!"; String result = greet.concat(name); // Hello, reader! Извиквайки метода, ще долепим променливата name, която е подадена като аргумент, към променливата greet. Резултатният низ ще има стойност "Hello, reader!". Вторият вариант за конкатенация е чрез операторите + и +=. Горния пример може да реализираме без проблем и по двата начина, например: String greet = "Hello, "; String name = "reader!"; String result = greet + name; // Hello, reader! Всички долепвания до низове не променят съществуващите променливи, а връщат нова променлива като резултат.&#x20;

#### Търсене в символен низ&#x20;

*   String str = "First java class";&#x20;

    int index = str.indexOf("java");&#x20;

    System.out.println(index); // index = 6 (starts from 0)&#x20;

#### Извличане на част от низ&#x20;

String str = "First java class"; String subStr = str.substring(11, 16); // subStr = " class " Извикването на метода substring(index1, index2) извлича подниз на дадена променлива, който се намира между index1и (index2– 1) включително. Символът на посочената позиция – index2– не се взима предвид! Aко посочим substring(11, 16), ще бъдат извлечени символите между индекс 10 и 15 включително, а не между 10 и 16!&#x20;

#### Разцепване на низ по разделител&#x20;

String listOfAnimals = "dog, cat, lion, pork"; String\[] animalsArr = listOfAnimals.split("\[ ,]"); for(String animal : animalsArr) { if(!animal.equals("")) { System.out.println(animal); } }&#x20;

#### Замяна на подниз с друг&#x20;

String helloWorld = "Hello, java."; String fixedGreeting = helloWorld.replace("java", " world"); System.out.println(fixedGreeting);&#x20;

#### Преминаване към главни и малки букви&#x20;

String helloWorld = "Hello, java."; System.out.println(helloWorld.toLowerCase()); System.out.println(helloWorld.toUpperCase());&#x20;

#### Премахване на празно пространство в края на низ&#x20;

String helloWorld = "Hello, java. ";; String withoutWhiteSpace = helloWorld.trim(); System.out.println(helloWorld.length()); System.out.println(withoutWhiteSpace.length());&#x20;

#### StringBuilder&#x20;

StringBuilder е клас, който служи за построяване и промяна на символни низове. Той преодолява проблемите с бързодействието, които възникват при конкатениране на низове от тип String. Класът е изграден под формата на масив от символи и това. Информацията в него не е неизменима – промените, които се налагат в променливите от тип StringBuilder, се извършват в една и съща област от паметта (буфер), което спестява време и ресурси. За промяната на съдържанието не се създава нов обект, а просто се променя текущият. StringBuilder sb = new StringBuilder(15); sb.append("Hello,World! "); sb.append("It’s a great day!");

* capacity() – връща размера на целия буфер (общия брой заети и свободни символи)
* length() – връща дължината на записания низ в променливата
* charAt(int index) – връща символа на указаната позиция
* append(…) – слепва низ, число или друга стойност след последния записан символ в буфера
* delete(int start, int end) – премахва низ по зададена начална и крайна позиция
* insert(int offset, String str) – вмъква даден стринг на дадена позиция
* replace(int start, int end, String str) – замества записания низ между началната и крайната позиция със стойността на променливата str
* toString() – връща записаната информация в обекта на StringBuilder като резултат от тип String, който можем да запишем в променлива на String.&#x20;