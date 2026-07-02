---
layout: default
title: Command
parent: Лабораторно упражнение 11
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 2
---

# Command

### Проблем

В практиката често се налага дадено действие да бъде подадено, съхранено или изпълнено по-късно, без обектът, който го извиква, да знае как точно ще бъде реализирано то.

Например дистанционно управление на музикален плеър може да има бутони за стартиране, пауза и спиране. Самото дистанционно не трябва да знае как работи плеърът. То трябва единствено да изпълни зададената команда.

### Решение

Шаблонът Command капсулира заявка като самостоятелен обект. Всеки команден обект съдържа необходимата информация за извикване на конкретно действие върху получателя.

По този начин обектът, който извиква командата, не зависи директно от класа, който реално извършва действието.

### Дефиниция

Command е поведенчески шаблон за проектиране, който превръща заявка в обект, позволявайки нейното подаване, съхранение, отлагане или изпълнение без пряка зависимост между извикващия обект и обекта, който реално изпълнява действието.

Основни понятия:
* Command - Интерфейсът, който дефинира изпълнението на команда.
* Concrete Command - Конкретна команда, която извиква определен метод на получателя.
* Receiver - Обектът, който реално извършва действието.
* Invoker - Обектът, който стартира командата, без да знае детайлите на изпълнението.
* Client - Създава командите и ги свързва с получателя.

### UML диаграма

<img width="928" height="547" alt="Command" src="https://github.com/user-attachments/assets/0e8aa98a-918d-4c2a-80c3-8d995f2e14dc" />


### Примерна реализация

```java
public interface Command {

    String execute();
}
```
```java
public class MusicPlayer {

    public String play() {
        return "Music player started.";
    }

    public String pause() {
        return "Music player paused.";
    }

    public String stop() {
        return "Music player stopped.";
    }
}
```
```java
public class PlayCommand implements Command {

    private MusicPlayer player;

    public PlayCommand(MusicPlayer player) {
        this.player = player;
    }

    @Override
    public String execute() {
        return player.play();
    }
}
```
```java
public class PauseCommand implements Command {

    private MusicPlayer player;

    public PauseCommand(MusicPlayer player) {
        this.player = player;
    }

    @Override
    public String execute() {
        return player.pause();
    }
}
```
```java
public class StopCommand implements Command {

    private MusicPlayer player;

    public StopCommand(MusicPlayer player) {
        this.player = player;
    }

    @Override
    public String execute() {
        return player.stop();
    }
}
```
```java
public class RemoteControl {

    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public String pressButton() {
        return command.execute();
    }
}
```
Използване
```java
public class Application {

    public static void main(String[] args) {

        MusicPlayer player = new MusicPlayer();

        Command playCommand = new PlayCommand(player);
        Command pauseCommand = new PauseCommand(player);
        Command stopCommand = new StopCommand(player);

        RemoteControl remoteControl = new RemoteControl();

        remoteControl.setCommand(playCommand);
        System.out.println(remoteControl.pressButton());

        remoteControl.setCommand(pauseCommand);
        System.out.println(remoteControl.pressButton());

        remoteControl.setCommand(stopCommand);
        System.out.println(remoteControl.pressButton());
    }
}
```
Класът MusicPlayer е получателят (Receiver) и съдържа реалната логика за изпълнение на действията.

Всеки конкретен команден клас (PlayCommand, PauseCommand, StopCommand) съдържа референция към MusicPlayer и извиква конкретен негов метод.

Класът RemoteControl е извикващият обект (Invoker). Той не знае какво действие ще бъде изпълнено, а работи единствено с интерфейса Command.

По този начин командите могат да бъдат сменяни динамично, без да се променя класът RemoteControl.

> [!IMPORTANT]
> Command не изпълнява действието самостоятелно. Той капсулира заявката и я насочва към получателя,
> който реално извършва действието.

### Предимства

* намалява зависимостта между извикващия обект и получателя;
* позволява действията да бъдат представени като обекти;
* улеснява добавянето на нови команди;
* позволява съхранение, отлагане или групиране на команди;
* подпомага спазването на принципа Open/Closed.

### Недостатъци

* увеличава броя на класовете;
* при голям брой действия могат да се създадат много конкретни команди;
* структурата може да изглежда излишно сложна при малки приложения.

### Приложение

Command е подходящ когато:
* действията трябва да бъдат капсулирани като обекти;
* извикващият обект не трябва да зависи от конкретния изпълнител;
* е необходимо динамично сменяне на действия;
* се реализират бутони, менюта, макроси, undo/redo механизми или опашки от задачи.
