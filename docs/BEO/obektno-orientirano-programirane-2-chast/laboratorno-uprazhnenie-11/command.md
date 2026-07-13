---
layout: default
title: Command
parent: Лабораторно упражнение 11
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 2
---

# Command

### Проблем

Да се разработи дистанционно управление за музикален плеър.

Дистанционното управление трябва да позволява стартиране, пауза и спиране на възпроизвеждането.


### Решение без използване на шаблона

Един възможен подход е дистанционното управление директно да работи с класа MusicPlayer и да извиква неговите методи:

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
public class RemoteControl {

    private MusicPlayer player;

    public RemoteControl(MusicPlayer player) {
        this.player = player;
    }

    public String pressPlayButton() {
        return player.play();
    }

    public String pressPauseButton() {
        return player.pause();
    }

    public String pressStopButton() {
        return player.stop();
    }

    public String pressNextButton() {
        return player.nextSong();
    }
}
```
Използване:
```java
public class Application {

    public static void main(String[] args) {

        MusicPlayer player = new MusicPlayer();

        RemoteControl remote = new RemoteControl(player);

        System.out.println(remote.pressPlayButton());
        System.out.println(remote.pressPauseButton());
        System.out.println(remote.pressStopButton());
        System.out.println(remote.pressNextButton());
    }
}
```

### Недостатъци на решението

При този подход класът RemoteControl познава директно всички действия, които могат да бъдат изпълнявани от музикалния плеър.

Ако се добави нова функционалност, например превключване към следваща песен или регулиране на звука, ще бъде необходимо да се променят както класът MusicPlayer, така и класът RemoteControl (nextSong(), previousSong(), increaseVolume(), decreaseVolume() и др.).

Освен това дистанционното управление е тясно свързано с конкретния клас MusicPlayer и не може лесно да бъде използвано с друг тип устройство.

Следователно е необходимо решение, при което отделните действия да бъдат представени като самостоятелни обекти и дистанционното управление да работи с тях по унифициран начин.


### Шаблонът като решение

Шаблонът **Command** капсулира заявка като самостоятелен обект. Всеки команден обект съдържа необходимата информация за извикване на конкретно действие върху получателя.


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
