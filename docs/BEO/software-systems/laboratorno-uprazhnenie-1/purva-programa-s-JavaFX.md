---
layout: default
title: Първа програма с JavaFx
parent: Лабораторно упражнение 1
grand-parent: Програмни системи
nav_order: 2
permalink: /docs/BEO/software-systems/laboratorno-uprazhnenie-1/purva-programa-s-JavaFX
---

# Създаване на първа JavaFX програма – Калкулатор

  След конфигурирането на проекта и създаването на основните класове в предния раздел, следва реализирането на първото JavaFX приложение. За пример ще бъде създаден прост калкулатор, който извършва събиране на две числа и визуализира резултата в графичен интерфейс.    


## 1. Наследяване на класа Application

  Класът HelloApplication трябва да наследява класа Application от JavaFX. Това е задължително, защото Application управлява жизнения цикъл на JavaFX приложението и позволява създаването на графичен прозорец. Без това наследяване приложението не може да бъде стартирано като JavaFX приложение.      


```java
package bg.tu_varna.sit.ps.lab1;

import javafx.application.Application;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {
        // Тук ще се изгради графичният интерфейс
    }
}

```

## 2. Създаване на графичните компоненти

  В метода start(Stage stage) се създават компонентите на потребителския интерфейс. Използват се текстови полета за въвеждане на числа, бутон за стартиране на изчислението и етикет за показване на резултата. Тези компоненти осигуряват основното взаимодействие между потребителя и приложението.    

 
```java
TextField number1Field = new TextField();
number1Field.setPromptText("Първо число");

TextField number2Field = new TextField();
number2Field.setPromptText("Второ число");

Button calculateButton = new Button("Изчисли");

Label resultLabel = new Label("Резултат:");
```

*При импортирането да се използват контролите от javafx.scene.control пакетите.*

## 3. Обработка на събития

  Към бутона се добавя обработка на събитие, която се изпълнява при неговото натискане. В този код се прочитат стойностите от текстовите полета, преобразуват се в числа, извършва се аритметичното изчисление и резултатът се извежда на екрана. Това показва как се задава поведение на приложението при взаимодействие с потребителския интерфейс.    


```java
calculateButton.setOnAction(event -> {
    double num1 = Double.parseDouble(number1Field.getText());
    double num2 = Double.parseDouble(number2Field.getText());
    double result = num1 + num2;

    resultLabel.setText("Резултат: " + result);
});
```

## 4. Подреждане на компонентите (Layout)

За подреждане на компонентите се използва layout контейнер от тип VBox. Layout контейнерите са необходими, тъй като JavaFX не позволява директно позициониране на елементите без контейнер. VBox подрежда компонентите вертикално, което е удобно за прост интерфейс като калкулатора.    

 
```java
VBox layout = new VBox(10);
layout.getChildren().addAll(
        number1Field,
        number2Field,
        calculateButton,
        resultLabel
);
```

## 5. Създаване на сцена и показване на прозореца

След като интерфейсът е изграден, той се поставя в обект от тип Scene. Сцената се асоциира с основния прозорец (Stage), задава се заглавие и чрез извикване на метода show() приложението се визуализира на екрана.     

```java
Scene scene = new Scene(layout, 300, 200);

stage.setTitle("Прост калкулатор");
stage.setScene(scene);
stage.show();
```


## 6. Стартиране на приложението чрез класа Launcher

За безопасно стартиране на JavaFX приложението се използва отделен клас Launcher, който съдържа метода main(). В него се извиква статичният метод launch(), който стартира JavaFX средата и извиква метода start() на класа HelloApplication.

```java
package bg.tu_varna.sit.ps.lab1;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        Application.launch(HelloApplication.class, args);
    }
}
```



