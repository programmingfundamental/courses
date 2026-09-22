---
title: Tasks
taskPage: true
sidebar:
  label: Tasks
  order: 100
---
## OCP tasks

### Task 1

Consider the calculator example that follows OCP from the lesson. What else could be done to optimize the code?

### Task 2

Look at the following code:

```
public class Cuboid {
// Member variables of this class
public double length;
public double breadth;
public double height;
}
```

```
public class Sphere {
// Storing radius of a sphere
public double radius;
}
```

```
public class Application {
// Returning the total volume of the geometric objects
public double getTotalVolume(Cuboid[] cGeoObjects, Sphere[] sGeoObjects)
{
	// Variable used to store total volume
	double volSum = 0;

	// Iteratively calculating the volume of each Cuboid
	// and adding it to the total volume

	// Iterating using for each loop to
	// calculate the volume of a cuboid
	for (Cuboid geoObj : cGeoObjects) {

		volSum += geoObj.length * geoObj.breadth
				* geoObj.height;
	}

	// Iterating using for each loop to
	// calculate the volume of a cuboid
	for (Sphere geoObj : sGeoObjects) {

		// Iteratively calculating the volume of each
		// Sphere and adding it to the total volume
		volSum += (4 / 3) * Math.PI * geoObj.radius
				* geoObj.radius * geoObj.radius;
	}

	// Returning the to total volume
	return volSum;
}
}
```

```
public class GFG {
public static void main(String args[])
{
	// Initializing a cuboid one as well as declaring
	// its dimensions.
	Cuboid cb1 = new Cuboid();
	cb1.length = 5;
	cb1.breadth = 10;
	cb1.height = 15;

	// Initializing a cuboid two as well as declaring
	// its dimensions.
	Cuboid cb2 = new Cuboid();
	cb2.length = 2;
	cb2.breadth = 4;
	cb2.height = 6;

	////Initializing a cuboid three as well as declaring
	/// its dimensions.
	Cuboid cb3 = new Cuboid();
	cb3.length = 3;
	cb3.breadth = 12;
	cb3.height = 15;

	// Initializing and declaring an array of cuboids
	Cuboid[] cArr = new Cuboid[3];
	cArr[0] = cb1;
	cArr[1] = cb2;
	cArr[2] = cb3;

	// Initializing a sphere one as well as declaring
	// its dimension.
	Sphere sp1 = new Sphere();
	sp1.radius = 5;

	// Initializing a sphere two as well as declaring
	// its dimension.
	Sphere sp2 = new Sphere();
	sp2.radius = 2;

	// Initializing a sphere three as well as declaring
	// its dimension.
	Sphere sp3 = new Sphere();
	sp3.radius = 3;

	// Initializing and declaring an array of spheres
	Sphere[] sArr = new Sphere[3];
	sArr[0] = sp1;
	sArr[1] = sp2;
	sArr[2] = sp3;

	// Initializing Application class
	Application app = new Application();

	// Getting the total volume
	// using get_total_volume
	double vol = app.getTotalVolume(cArr, sArr);

	// Print and display the total volume
	System.out.println("The total volume is " + vol);
}
}
```

Does it follow the OCP?

What should be done?

Implement the solution.

### Task 3

Create a program for coffee machines with basic and premium functions. The code have to follow the OCP.

## SRP tasks

### Task 1

Look at the following code:

```
public class TextManipulator { 
    private String text;
    
    public TextManipulator(String text) {
        this.text = text;
    }
    
    public String getText() {
        return text;
    }
    
    public void appendText(String newText) {
        text = text.concat(newText);
    }
    
    public String findWordAndReplace(String word, String replacementWord) {
        if (text.contains(word)) {
            text = text.replace(word, replacementWord);
        }
        return text;
    }
    
    public String findWordAndDelete(String word) {
        if (text.contains(word)) {
            text = text.replace(word, "");
        }
        return text;
    }
    
    public void printText() {
        System.out.println(text);
    }

    public void printOutEachWordOfText() {
        System.out.println(Arrays.toString(text.split(" ")));
    }

    public void printRangeOfCharacters(int startingIndex, int endIndex) {
        System.out.println(text.substring(startingIndex, endIndex));
    }
}
```

Does it follow the SRP?

What should be done?

Implement the solution.

### Task 2

Create application for food delivery that accepts orders, calculates the bill and deliver the order. Follow SRP while implementing the code.

## Tasks 

### Task 1

Look at lab's examples. Modify the code as follows:

- convert multidirectional composition to aggregation

- convert unidirectional aggregation to composition.


### Task 2

Look at the code below. What type of relation is implemented? Refactor the code to implement the other type and create main function to test the logic.

```java
public class Student {

    private String fNumber;
    private String fullName;
    private int course;
    private String specialty;

    public Student(String fNumber, String fullName, int course, String specialty) {
        this.fNumber = fNumber;
        this.fullName = fullName;
        this.course = course;
        this.specialty = specialty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return course == student.course && Objects.equals(fNumber, student.fNumber) && Objects.equals(fullName, student.fullName) && Objects.equals(specialty, student.specialty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fNumber, fullName, course, specialty);
    }

    @Override
    public String toString() {
        return "Student{" +
                "fNumber='" + fNumber + '\'' +
                ", fullName='" + fullName + '\'' +
                ", course=" + course +
                ", specialty='" + specialty + '\'' +
                '}';
    }
}

public class University {

    private Set<Student> students = new HashSet<>();

    public void addStudent(Student student) {
        students.add(student);
    }

    public String getStudentInfo() {
        StringBuilder info = new StringBuilder();
        for (Student s : students) {
            info.append(s.toString()).append("\n");
        }
        return info.toString();
    }
}
```

### Task 3

Write a program for an online platform (OnlinePlatform). The platform has users (User), each of whom has a user profile (Profile) and can create posts (Post). Users can also comment on existing posts (Comment). What type of relationships between the User-Profile, User-Post and Post-Comment objects will be used and why?
