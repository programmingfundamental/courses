# JavaBean

JavaBeans са класове, които капсулират много обекти в един обект (the bean). Това е java клас, който трябва да следва следните конвенции:

* Трябва да осигурява конструктор по подразбиране, без аргументи.
* Трябва да бъде сериализируем, да имплементира интерфейса на serializable.
* Всички променливи в класа трябва да са защитени (private).
* Трябва да осигурява публични "getter" и "setter" за променливите.

```
public class StudentsBean implements java.io.Serializable {
   private String firstName = null;
   private String lastName = null;
   private int age = 0;

   public StudentsBean() {
   }
   public String getFirstName(){
      return firstName;
   }
   public String getLastName(){
      return lastName;
   }
   public int getAge(){
      return age;
   }
   public void setFirstName(String firstName){
      this.firstName = firstName;
   }
   public void setLastName(String lastName){
      this.lastName = lastName;
   }
   public void setAge(Integer age){
      this.age = age;
   }
}
```

### Достъп до JavaBeans

Деклариране на променлива

`<jsp:useBean id = "bean's name" scope = "bean класа" typeSpec/>`

След като бъде деклариран, bean става скриптова променлива, която може да бъде достъпна както чрез елементи на скриптове, така и от други потребителски тагове, използвани в JSP.

```
<html>
   <head>
      <title>useBean Example</title>
   </head>
   
   <body>
      <jsp:useBean id = "date" class = "java.util.Date" /> 
      <p>The date/time is <%= date %>
   </body>
</html>
```

### Достъп до свойства на JavaBeans

Заедно с `<jsp:useBean...>` се използва:

* `<jsp:getProperty/>` за достъп до методите за извличане
* `<jsp:setProperty/>` за достъп до методите за поставяне на стойности

```
<jsp:useBean id="id" class="bean класа" scope="пространство за достъп на bean класа">
   <jsp:setProperty name="id на декларирания bean класа" property="името на променливата" value="value"/>
   <jsp:getProperty name="id на декларирания bean класа" property="името на променливата"/>
   ...........
</jsp:useBean>
```

**Пример**

```
<html>
   <head>
      <title>get and set properties Example</title>
   </head>
   <body>
      <jsp:useBean id = "students" class = "com.tutorialspoint.StudentsBean"> 
         <jsp:setProperty name = "students" property = "firstName" value = "Koko"/>
         <jsp:setProperty name = "students" property = "lastName" value = "Kiki"/>
         <jsp:setProperty name = "students" property = "age" value = "10"/>
      </jsp:useBean>
      <p>Първото име на ученика: 
         <jsp:getProperty name = "students" property = "firstName"/>
      </p>
      <p>Фамилията на ученика: 
         <jsp:getProperty name = "students" property = "lastName"/>
      </p>
      <p>Годините на ученика: 
         <jsp:getProperty name = "students" property = "age"/>
      </p>
   </body>
</html>
```
