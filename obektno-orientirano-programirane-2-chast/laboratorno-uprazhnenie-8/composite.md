# Composite

Композитният модел е един от моделите на структурния дизайн. Композитният модел на проектиране се използва, когато трябва да представим йерархия на част от цялото.

Когато трябва да създадем структура по начин, по който обектите в структурата трябва да бъдат третирани по същия начин, можем да приложим композитен модел на дизайн. Нека го разберем с пример от реалния живот - Диаграмата е структура, която се състои от обекти като кръг, линии, триъгълник и т.н. Когато напълним чертежа с цвят (да речем червен), същият цвят се прилага и към обектите в чертежа. Тук чертежът се състои от различни части и всички те имат едни и същи операции. Композитният модел се състои от следните обекти.

1. Базов компонент - Базов компонент е интерфейсът за всички обекти в композицията, клиентската програма използва **базов компонент** за работа с обектите в композицията. Тя може да бъде интерфейс или **абстрактен клас** с някои методи, общи за всички обекти.
2. Лист - Определя поведението на елементите в състава. Това е градивният елемент за композицията и изпълнява базовия компонент. Той няма препратки към други компоненти.
3. **Композитен** - Състои се от листни елементи и изпълнява операциите в базов компонент.

Тук прилагам композитен модел на проектиране за сценария за рисуване.

#### Композитен модел Базов компонент

Композитният основен компонент на модела определя общите методи за листа и композити. Можем да създадем клас с метод за рисуване на формата с даден цвят.&#x20;

```


public interface Shape {
	
	public void draw(String fillColor);
}
```

#### Композитен дизайн модел листни обекти

Композитен дизайн модел листа изпълнява основен компонент и това са градивния елемент за композита. Можем да създадем множество листни обекти като триъгълник, кръг и т.н.&#x20;

```


public class Triangle implements Shape {

	@Override
	public void draw(String fillColor) {
		System.out.println("Drawing Triangle with color "+fillColor);
	}

}
```



```


public class Circle implements Shape {

	@Override
	public void draw(String fillColor) {
		System.out.println("Drawing Circle with color "+fillColor);
	}

}
```

#### Композитен обект

Композитният обект съдържа група листни обекти и трябва да предоставим някои помощни методи за добавяне или изтриване на листа от групата. Можем също така да предоставим метод за премахване на всички елементи от групата.&#x20;

```


import java.util.ArrayList;
import java.util.List;

public class Drawing implements Shape{

	
	private List<Shape> shapes = new ArrayList<Shape>();
	
	@Override
	public void draw(String fillColor) {
		for(Shape sh : shapes)
		{
			sh.draw(fillColor);
		}
	}
	
	
	public void add(Shape s){
		this.shapes.add(s);
	}
	
	
	public void remove(Shape s){
		shapes.remove(s);
	}
	
	
	public void clear(){
		System.out.println("Clearing all the shapes from drawing");
		this.shapes.clear();
	}
}
```

```
public class Application {

	public static void main(String[] args) {
		Shape tri = new Triangle();
		Shape tri1 = new Triangle();
		Shape cir = new Circle();
		
		Drawing drawing = new Drawing();
		drawing.add(tri1);
		drawing.add(tri1);
		drawing.add(cir);
		
		drawing.draw("Red");
		
		drawing.clear();
		
		drawing.add(tri);
		drawing.add(cir);
		drawing.draw("Green");
	}

}
```

Изходът от горната композитна клиентска програма е:

```
Drawing Triangle with color Red
Drawing Triangle with color Red
Drawing Circle with color Red
Clearing all the shapes from drawing
Drawing Triangle with color Green
Drawing Circle with color Green
```

#### Композитен модел важни точки

* Композитният модел трябва да се прилага само когато групата обекти трябва да се държи като единичен обект.
* Композитният дизайн може да се използва за създаване на дървовидна структура.
