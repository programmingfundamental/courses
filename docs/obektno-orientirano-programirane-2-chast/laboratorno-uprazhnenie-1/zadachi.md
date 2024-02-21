---
layout: default
title: Задачи
parent: Лабораторно упражнение 1
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 3
---

# Задачи OCP

### Задача 1

Разгледайте примера от упражнението от гледна точка на Open-Closed принципа. Виждате ли несъответствия с това правило в примера в упражнението? Обосновете се какви са те и какво е решението за тяхното преодоляване.

### Задача 2

Разгледайте следния пример:

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
	double vol = app.getTotalVolume(cAarr, sArr);

	// Print and display the total volume
	System.out.println("The total volume is " + vol);
}
}
```

Така създадения клас отговаря ли на OCP?

Какво бихте направили, за да подобрите този клас?

Реализирайте решението.

### Задача 3

Как ще реализирате програма за кафе машини, които имат базови функционалности и премиум такива.

От какви класове ще имате нужда?

Каква ще е тяхната отговорност?

Кои ще са тези класове?

Реализирайте решението.

# Задачи SRP

### Задача 1

Преглеждайки правилотро за единична отговорност и представения пример. Виждате ли възможни пробойни на това правило в примера в упражнението? Обосновете се какви са те и какво е решението за тяхното преодоляване.

### Задача 2

Съставете следния клас:

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

Така създадения клас отговаряли на SRP?

Какво бихте направили, за да подобрите този клас?

Реализирайте решението.

### Задача 3

Как ще разделите функционалностите в приложение за доставка на храна, което приема поръчки на храна, изчислява сметката и я доставя на клиентите.

От колко класа ще имате нужда?

Каква ще е тяхната отговорност?

Кои ще са тези класове?

Реализирайте решението.
