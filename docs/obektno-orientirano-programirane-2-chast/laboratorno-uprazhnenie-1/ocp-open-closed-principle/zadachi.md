# Задачи

### Задача 1

Преглеждайки правилото за Open-Closed и представения пример. Виждате ли възможни пробойни на това правило в примера в упражнението. Обосновете се какви са те и какво е решението за тяхното преодоляване.

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
public double get_total_volume(Cuboid[] c_geo_objects,
							Sphere[] s_geo_objects)
{
	// Variable used to store total volume
	double vol_sum = 0;

	// Iteratively calculating the volume of each Cuboid
	// and adding it to the total volume

	// Iterating using for each loop to
	// calculate the volume of a cuboid
	for (Cuboid geo_obj : c_geo_objects) {

		vol_sum += geo_obj.length * geo_obj.breadth
				* geo_obj.height;
	}

	// Iterating using for each loop to
	// calculate the volume of a cuboid
	for (Sphere geo_obj : s_geo_objects) {

		// Iteratively calculating the volume of each
		// Sphere and adding it to the total volume
		vol_sum += (4 / 3) * Math.PI * geo_obj.radius
				* geo_obj.radius * geo_obj.radius;
	}

	// Returning the to total volume
	return vol_sum;
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
	Cuboid[] c_arr = new Cuboid[3];
	c_arr[0] = cb1;
	c_arr[1] = cb2;
	c_arr[2] = cb3;

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
	Sphere[] s_arr = new Sphere[3];
	s_arr[0] = sp1;
	s_arr[1] = sp2;
	s_arr[2] = sp3;

	// Initializing Application class
	Application app = new Application();

	// Getting the total volume
	// using get_total_volume
	double vol = app.get_total_volume(c_arr, s_arr);

	// Print and display the total volume
	System.out.println("The total volume is " + vol);
}
}
```

Така създадения клас отговаряли на OCP?

Какво бихте направили, за да подобрите този клас?

Реализирайте решението.

### Задача 3

Как ще реализирате програма за кафе машини, който имат базови функционалностти и премиум такива.&#x20;

От какви класове ще имате нужда?&#x20;

Каква ще е тяхната отговорност?

Кои ще са тези класове?

Реализирайте решението.
