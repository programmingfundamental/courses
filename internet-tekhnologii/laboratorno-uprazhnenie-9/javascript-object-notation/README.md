# JavaScript Object Notation

JSON е текстово базиран отворен стандарт създаден за човешки четим обмен на данни. JSON представя прости структури от данни и обекти като асоциативни масиви. JSON е езиково независима спецификация, с анализатори, които могат да преобразуват много други езици в JSON.

Форматът на JSON

Форматът на JSON често е използван за сериализация и предаване на структурирани данни през Интернет връзка. Използва се главно, за да предаде данни между сървър и клиент.

* Mедия тип за JSON е application/json.
* Разширението на файловете написани на JSON е .json.
* Типове данни
  * Number (число с плаваща запетая, double precision floating-point format в JavaScript)
  * String (низ от символи с Unicode кодиране, затворени в двойни кавички, като „специалните“ символи се представят с т.нар. escaping – символни последователности, започващи със символа „\“)
  * Boolean (true или false)
  * Array (наредена поредица от стойности, разделени със запетая и затворени в квадратни скоби; стойностите не е задължително да бъдат от един и същ тип)
  * Object (неподредена колекция от двойки ключ:стойност, символът „:“ разделя ключът и стойността, разделени със запетая и затворени в къдрави скоби; ключовете трябва да са string-ове и да са различни един от друг)
  * null (empty)

**Пример**

```
{
    "firstName": "Иван",
    "lastName": "Иванов",
    "jobTitle": "Студент",
    "address": {
        "street": "21 2nd Street",
        "name": "New York",
    },
    "phone": "212 555-1234"
    "jobsSkills": [
        {
            "name": "java",
            "value": 10
        },
        {
            "name": "HTML",
            "value": 5
        }
    ]
}
```

### GSON

#### Добавяне на библиотека

Добавяме в pom.xml dependency за работа с библиотеката.

```
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.8.2</version>
    </dependency>
```

**Пример**

```
//Прочита текстовото съдържанието на заявката
StringBuilder sb = new StringBuilder();
String s;
while ((s = request.getReader().readLine()) != null) {
    sb.append(s);
}

UserBean newUser = (UserBean) gson.fromJson(sb.toString(), UserBean.class);
```

```
//Връщане на резултата като json
response.setContentType("application/json;charset=UTF-8");
PrintWriter out = response.getWriter();
out.println(gson.toJson(result)); //result обекта който щесе върне като резултат
out.flush();
```
