# Композиция

Композицията в java е техниката на проектиране за внедряване на отношение в класове. Можем да използваме наследяване на java или композиция на Object в java за повторно използване на код.

Композицията на Java се постига чрез използване на променливи на екземпляри, които препращат към други обекти. Например, човек има работа. Нека видим това с примерен код за съставяне на Java.

{% code title="Job.java" lineNumbers="true" %}
```java
public class Job {
    private String role;
    private long salary;
    private int id;
        
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public long getSalary() {
        return salary;
    }
    public void setSalary(long salary) {
        this.salary = salary;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }java
```
{% endcode %}

{% code title="Person.java" lineNumbers="true" %}
```java
public class Person {

    //composition has-a relationship
    private Job job;
   
    public Person(){
        this.job=new Job();
        job.setSalary(1000L);
    }
    public long getSalary() {
        return job.getSalary();
    }

}
```
{% endcode %}
