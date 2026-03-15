---
layout: default
title: FormApplication / Launcher
parent: Лабораторно упражнение 5
grand_parent: Програмни системи
nav_order: 1
---

### FormApplication.java

```java
public class FormApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(
                FormApplication.class.getResource("form-view.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load(), 1000, 650);
        stage.setTitle("Система за студентски заявления");
        stage.setScene(scene);
        stage.show();
    }
}
```

### Launcher.java

```java
public class Launcher {
    public static void main(String[] args) {
        Application.launch(FormApplication.class, args);
    }
}
```