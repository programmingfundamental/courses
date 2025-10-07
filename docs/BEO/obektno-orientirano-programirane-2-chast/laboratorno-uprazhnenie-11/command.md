---
layout: default
title: Command
parent: Лабораторно упражнение 11
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 2
---

# Command

Командният модел е един от поведенческите моделни модели. Шаблонът за проектиране на команди се използва за прилагане на хлабаво свързване в модел заявка-отговор.

В командния модел заявката се изпраща до инвокатора и инвокаторът я предава към капсулирания команден обект. Командният обект предава заявката към съответния метод на Receiver за извършване на конкретното действие. Клиентската програма създава обекта приемник и след това го прикачва към командата. След това създава обекта invoker и прикачва командния обект за извършване на действие. Когато клиентската програма изпълни действието, то се обработва въз основа на командата и обекта на приемника.

Може да се внедри командния модел в помощна програма за файлова система с методи за отваряне, запис и затваряне на файл. Тази помощна програма за файлова система трябва да поддържа множество операционни системи като Windows и Unix. За да се създаде помощна програма за файлова система, първо трябва да се създаде класове приемници, които всъщност ще изпълняват функционалностите. Може да ползваме интерфейс FileSystemReceiver и неговите класове за внедряване за различни разновидности на операционни системи като Windows, Unix, Solaris и т.н.

```
public interface FileSystemReceiver {

	void openFile();
	void writeFile();
	void closeFile();
}
```

Интерфейсът FileSystemReceiver дефинира договора за класовете за изпълнение. Следва да се създадат два варианта на класовете приемници за работа с Unix и Windows системи.

```
public class UnixFileSystemReceiver implements FileSystemReceiver {

	@Override
	public void openFile() {
		System.out.println("Opening file in unix OS");
	}

	@Override
	public void writeFile() {
		System.out.println("Writing file in unix OS");
	}

	@Override
	public void closeFile() {
		System.out.println("Closing file in unix OS");
	}

}
```

```
public class WindowsFileSystemReceiver implements FileSystemReceiver {

	@Override
	public void openFile() {
		System.out.println("Opening file in Windows OS");
		
	}

	@Override
	public void writeFile() {
		System.out.println("Writing file in Windows OS");
	}

	@Override
	public void closeFile() {
		System.out.println("Closing file in Windows OS");
	}
}
```

Можем да се използва интерфейс или абстрактен клас, за да се създаде основната команда, това е дизайнерско решение и зависи от изискванията. Ще използваме интерфейс, защото нямаме внедрения по подразбиране.

```
public interface Command {

	void execute();
}
```

Следва да се създадат реализации за всички различни типове действия, извършвани от приемника. За трите действия който имаме, ще са необходими три реализации на Command. Всяка реализация на команда ще препрати заявката към подходящия метод на получателя.

```
public class OpenFileCommand implements Command {

	private FileSystemReceiver fileSystem;
	
	public OpenFileCommand(FileSystemReceiver fs){
		this.fileSystem=fs;
	}
	@Override
	public void execute() {
		//open command is forwarding request to openFile method
		this.fileSystem.openFile();
	}

}
```

```
public class CloseFileCommand implements Command {

	private FileSystemReceiver fileSystem;
	
	public CloseFileCommand(FileSystemReceiver fs){
		this.fileSystem=fs;
	}
	@Override
	public void execute() {
		this.fileSystem.closeFile();
	}

}
```

```
public class WriteFileCommand implements Command {

	private FileSystemReceiver fileSystem;
	
	public WriteFileCommand(FileSystemReceiver fs){
		this.fileSystem=fs;
	}
	@Override
	public void execute() {
		this.fileSystem.writeFile();
	}

}
```

След като са готови реализациите на приемника и командите, следващата стъпка е внедряване на класа invoker.

Invoker е прост клас, който капсулира командата и предава заявката към командния обект, за да я обработи.

```
public class FileInvoker {

	public Command command;
	
	public FileInvoker(Command c){
		this.command=c;
	}
	
	public void execute(){
		this.command.execute();
	}
}
```

Реализацията на помощната програма за файлова система е готова и може да се премине към написването на клиентска програма за шаблон на стандартна команда. Първо е необходимо да се предостави помощен метод за създаване на подходящия обект FileSystemReceiver. Можем да се използва системния клас, за получаване на информация за операционната система, може да се използва той или в противен случай можем да се използва фабричен шаблон, за връщане  на подходящ тип въз основа на входа.

```
public class FileSystemReceiverUtil {
	
	public static FileSystemReceiver getUnderlyingFileSystem(){
		 String osName = System.getProperty("os.name");
		 System.out.println("Underlying OS is:"+osName);
		 if(osName.contains("Windows")){
			 return new WindowsFileSystemReceiver();
		 }else{
			 return new UnixFileSystemReceiver();
		 }
	}
	
}
```

Вече може да се премине към създаването на клиентска програма за модел на команди, която ще използва помощна програма за файлова система.

```
public class FileSystemClient {

	public static void main(String[] args) {
		//Creating the receiver object
		FileSystemReceiver fs = FileSystemReceiverUtil.getUnderlyingFileSystem();
		
		//creating command and associating with receiver
		OpenFileCommand openFileCommand = new OpenFileCommand(fs);
		
		//Creating invoker and associating with Command
		FileInvoker file = new FileInvoker(openFileCommand);
		
		//perform action on invoker object
		file.execute();
		
		WriteFileCommand writeFileCommand = new WriteFileCommand(fs);
		file = new FileInvoker(writeFileCommand);
		file.execute();
		
		CloseFileCommand closeFileCommand = new CloseFileCommand(fs);
		file = new FileInvoker(closeFileCommand);
		file.execute();
	}

}
```

Клиентът е отговорен за създаването на подходящия тип команден обект. Например, ако се иска написвеане на файл, не трябва да се създава обект CloseFileCommand. Клиентската програма също е отговорна за прикачването на приемника към командата и след това на командата към класа invoker.

Заключение:

Командата е ядрото на шаблона за проектиране на команда, който определя договора за изпълнение.

Реализацията на приемника е отделна от реализацията на командата.

Класовете за внедряване на команди избират метода за извикване на обекта приемник, за всеки метод в приемника ще има изпълнение на команда. Той работи като мост между приемника и методите на действие.

Класът Invoker просто препраща заявката от клиента към командния обект. Клиентът е отговорен да създаде подходяща команда и реализация на приемник и след това да ги асоциира заедно.

Клиентът също е отговорен за инстанцирането на обекта invoker и свързването на командния обект с него и изпълнението на метода на действие.

Шаблонът за проектиране на команди е лесно разширим, можем да се добавят нови методи за действие в приемници и да се създават нови реализации на команди, без да се променя клиентския код.

Недостатъкът на модела на дизайн на Command е, че кодът става огромен и объркващ с голям брой методи за действие и поради толкова много асоциации.
