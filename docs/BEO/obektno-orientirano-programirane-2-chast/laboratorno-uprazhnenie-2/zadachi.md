---
layout: default
title: Задачи
parent: Лабораторно упражнение 2
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 4
---

# Задачи

### Задача 1

Дефинирайте следния интерфейс:

```java
public interface Vehicle {    
    public void drive();    
    public void stop();
    public void refuel();
    public void openDoors();
}
```

Имплементирайте го в класа Bike. Приложете принципа ISP и имплементирайте класовете Car и Truck


### Задача 2

Създайте програма, която да записва информацията за книга с помоща на интерфейса PrintInfo с метод printText. Направете две имплементации на интерфейса PrintInfoТоConsole и PrintInfoТоFile. Отговаря ли на всички SOLID-принципи подобна реализация?

Създайте интерфейс PrintInfoТоMedia с две различни имплементации, като едната имплементация е в конзола а другата във файл. Изпълнете програмата един път с имплементацията за извеждане в конзолата и един път с извеждането във файл. Направете така, че двете имплементации да се сменят само с промяна на пакета.

Каква е разликата между решенията с двата интерфейса (PrintInfo и PrintInfoТоMedia)?


### Задача 3

Разгледайте посочения по-долу пример, който нарушава SOLID-принципите. Модифицирайте го по начин, по който те са спазени.

```java
import java.util.ArrayList;
import java.util.List;

public class Student {
    private String name;
    private String facultyNumber;
    private String email;
    private double gpa;
    private List<String> courses = new ArrayList<>();
    private int courseYear;
    private double tuitionBalance;
    private boolean hasScholarship;

    public Student(String name, String facultyNumber, String email, int courseYear) {
        this.name = name;
        this.facultyNumber = facultyNumber;
        this.email = email;
        this.courseYear = courseYear;
    }

    public String enrollInCourse(String courseCode) {
        courses.add(courseCode);
        return (name + " enrolled in " + courseCode);
    }

    public double calculateGPA(List<Double> grades) {
        // Complex GPA calculation logic
        double sum = 0;
        for (double grade : grades) {
            sum += grade;
        }
        return this.gpa = sum / grades.size();
    }

    // Financial responsibilities
    public String payTuition(double amount) {
        this.tuitionBalance -= amount;
        if (this.tuitionBalance < 0) {
            throw new IllegalArgumentException("You have a credit of " + Math.abs(tuitionBalance));
        }
        return sendPaymentConfirmation();
    }

    private String sendPaymentConfirmation() {
        return "Sending payment confirmation to " + email;

    }

    public double calculateGraduationRequirements() {
        if (courseYear == 1) {
            return 120 - 30; // 120 total credits
        } else if (courseYear == 2) {
            return 120 - 60;
        } else if (courseYear == 3) {
            return 120 - 90;
        } else if (courseYear == 4) {
            return 120 - 120; // Should be graduating!
        }
        return 0;
    }

    public String attendClass() {
        if (this instanceof OnlineStudent) {
            return "Joining Teams meeting...";
        } else {
            return "Walking to classroom 201...";
        }
    }

    public String submitAssignment(String assignment) {
        if (courseYear == 1) {
            return "First course student assignment submitted - will get extra feedback";
        } else if (courseYear == 4) {
            return "Last year student assignment submitted - this is for graduation!";
        }
        return "Student assignment submitted";
    }

    public String useCafeteriaMealPlan() {
        if (courseYear != 1) {
            throw new UnsupportedOperationException("Only freshmen have meal plans!");
        }
        if (tuitionBalance > 0) {
            throw new IllegalStateException("Can't use meal plan with unpaid tuition!");
        }
        return "Using meal card in cafeteria...";
    }

    public String applyForScholarship() {
        if (gpa < 3.5) {
            throw new UnsupportedOperationException("GPA too low for scholarship!");
        }
        if (hasScholarship) {
            throw new IllegalStateException("Already has a scholarship!");
        }
        return "Processing scholarship application for " + name;
    }

    public String attendGraduationCeremony() {
        if (courseYear != 4) {
            throw new UnsupportedOperationException("Only seniors can attend graduation!");
        }
        return "Putting on cap and gown...";
    }


    private MySQLDatabase database = new MySQLDatabase();
    private EmailService emailService = new EmailService();

    public void saveToDatabase() {
        database.connect("localhost:3306", "root", "password123");
        database.saveStudent(this);
        emailService.sendEmail(email, "Your data has been saved!");
    }

    private class MySQLDatabase {
        public void connect(String url, String user, String pass) {
            System.out.println("Connecting to MySQL...");
            // MySQL specific connection code
        }
        public void saveStudent(Student s) {
            System.out.println("INSERT INTO students VALUES...");
            // MySQL specific SQL
        }
    }

    private class EmailService {
        public void sendEmail(String to, String message) {
            System.out.println("Using SMTP server to send email to " + to);
            // SMTP specific code
        }
    }

    public void processEndOfSemester(List<Double> finalGrades) {
        calculateGPA(finalGrades);
        System.out.println("GPA calculated: " + gpa);

        saveToDatabase();

        if (gpa < 2.0) {
            System.out.println("Academic probation!");
            emailService.sendEmail(email, "You're on academic probation!");
        }

        if (courseYear == 4 && gpa >= 3.5) {
            applyForScholarship();
        }
    }
}

public class OnlineStudent extends Student {
    public OnlineStudent(String name, String studentId, String email, int courseYear) {
        super(name, studentId, email, courseYear);
    }

    @Override
    public String attendClass() {
        return "Joining Teams...";
    }

    @Override
    public String useCafeteriaMealPlan() {
        throw new UnsupportedOperationException("Online students don't have meal plans!");
    }
}

public class UniversitySystem {
    public static void main(String[] args) {
        Student firstStudent = new Student("Alice", "S123", "alice@uni.edu", 2);
        Student secondStudent = new Student("Bob", "S456", "bob@uni.edu", 4);
        OnlineStudent thirdStudent = new OnlineStudent("Charlie", "S789", "charlie@uni.edu", 1);

        firstStudent.enrollInCourse("CS101");
        firstStudent.useCafeteriaMealPlan();

        secondStudent.useCafeteriaMealPlan();

        thirdStudent.attendClass();
        thirdStudent.useCafeteriaMealPlan();
    }
}

```



Кой вариант ще доведе до по-лесна реализация на проектите?
