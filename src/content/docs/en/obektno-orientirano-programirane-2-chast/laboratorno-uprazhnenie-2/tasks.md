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

## Tasks LSP

### Task 1

Create a program for shop shelfs. The shelf stores list of products and each product has storage temperature. The shop has list of shelfs and possibility to add products.

Later the shop introduce a shelf for cold products, which stores only products with storage temperature below 15 degrees.

## Задачи ISP

### Task 1

Define following interface:

```java
public interface Vehicle {    
    public void drive();    
    public void stop();
    public void refuel();
    public void openDoors();
}
```

The interface should be implemented by classes Bike, Car and Truck, so ISP should be implemented.

### Задача 2

Define following interface:

```
public interface Payments {
    
    public bool payMoney(double amount);
    
    public ScratchCard getScratchCard();
    
    public double getCashBackAsCreditBalance();
}
```

Implement class GooglePay with possibilities for:

* bill payment, balance check and balance decrease;
* gives scratch card with a prize:
  * if balance is over 100, there is 10% chance of winning
  * if balance is over 1000, there is 20% chance of winning card
  * if balance is over 3000, there is 30% winning chance
  * if balance is over 5000, there is 50% winning chance
  * if balance is over 10000, there is 100% chance of winning
* return of 10% payment when there is 50% winning chance and increase balance.

Implement class Paytm with following behaviour:
* bill payment, balance check and balance decreasing in a five days;.
* return of 5% payment when there is 70% winning chance and increase balance.

## Tasks DI

### Task 1

Create two implementations of the PrintInfoToMedia interface, where one implementation prints to the console and the other to a file. Execute the program once with the implementation for console output and once with the file output. Make it so that the two implementations can be switched by just changing the package.

## Bonus task
Apply the SOLID principles when writing a program that creates new text files. When an error occur, they are handled and written to a file named 'LocalErrors.txt'.
There are two types of files - those with textual content and those with metadata (the metadata includes the author).
Text files are stored in the 'base' directory, while files with metadata are stored in the 'meta' directory.
If the text of the file starts with 'Author', a metadata file is created.
Each file should be able to be opened for reading.
