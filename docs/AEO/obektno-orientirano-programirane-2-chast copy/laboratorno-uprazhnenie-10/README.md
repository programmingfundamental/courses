---
layout: default
title: Laboratory lesson 10
parent: Object-oriented Programming - 2 part AEO
has_children: true
nav_order: 10
permalink: /docs/obektno-orientirano-programirane-2-chast-aeo/laboratorno-uprazhnenie-10
---

# Strategy


Strategy belongs to behavioural patterns group. It is used when a problem could be resolved in more than one way and the choise of decision method is made during execution.

In order to implement Strategy there have to be:
- inreface defining strategy method;
- concrete implementations for each strategy;
- context object with behavioour dependent on the chosen strategy.

Such organization allows seamless strategy change or addition of another since there is no need to modify context object and client code. It is important to point out that all strategies are mutually replaceble.

As disadvantage of Strategy is the need client to be familiar with all strategies in order to use them properly. Such approach is not very effective if there are only a few strategies available.

The example below shows area calculation for different shapes.

```
public interface ShapeStrategy {

    double calculateArea();
}
```

Some concrete implementations are nedeed:

```
public class RectangleShape implements ShapeStrategy{

    private int length;
    private int width;

    public RectangleShape(int length, int width) {
        this.length = length;
        this.width = width;
    }

    @Override
    public double calculateArea() {
        return length * width;
    }
}
```
```
public class TriangleStrategy implements ShapeStrategy{

    private int a;
    private int b;
    private int c;

    public TriangleStrategy(int a, int b, int c) {
        if ((a + b > c) && (a + c > b) && (b + c > a)) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }

    @Override
    public double calculateArea() {
        int p = (a + b + c)/2;
        return Math.sqrt(p * (p - a) * (p - b) * (p - c));
    }
}
```
```
public class CircleStrategy implements ShapeStrategy{

    private int radius;

    public CircleStrategy(int radius) {
        this.radius = radius;
    }

    @Override
    public double calculateArea() {
        return Math.PI * radius * radius;
    }
}
```

The last step is to implement the context object:

```
public class Shape {

    private ShapeStrategy shapeStrategy;

    public Shape(ShapeStrategy shapeStrategy) {
        this.shapeStrategy = shapeStrategy;
    }

    public double getArea() {
        return shapeStrategy.calculateArea();
    }
}
```

Client code:

```
public class Application {

    public static void main(String[] args) {
        Shape circle = new Shape(new CircleStrategy(2));
        System.out.println("Circle's area is: " + String.format("%.2f", circle.getArea()));

        Shape triangle = new Shape(new TriangleStrategy(5, 4, 6));
        System.out.println("Triangle's area is: " + String.format("%.2f", triangle.getArea()));

        Shape rectangle = new Shape(new RectangleShape(4, 7));
        System.out.println("Rectangle's area is: " + rectangle.getArea());
    }
}
```

# Command

The Command Pattern is one of the behavioral design patterns. The Command Design Pattern is used to implement loose coupling in a request-response model.

In the Command Pattern, a request is sent to the Invoker, and the Invoker passes it to the encapsulated Command object. The Command object passes the request to the appropriate Receiver method to perform the specific action. The client program creates the Receiver object and then attaches it to the command. It then creates the Invoker object and attaches the Command object to perform the action. When the client program executes the action, it is processed based on the command and the Receiver object.

The Command Pattern could be implemented in a file system utility with methods to open, write, and close a file. This file system utility must support multiple operating systems such as Windows and Unix. To create a file system utility, first there have to be Receiver classes created that will actually perform the functionality. Interface FileSystemReceiver and its implementation classes will be used for different operating systems such as Windows, Unix, Solaris, etc.

```
public interface FileSystemReceiver {

	String openFile();
	String writeFile();
	String closeFile();
}
```

FileSystemReceiver interface defines the contract for concrete classes; there will be two receiver implementations - for Unix and for Windows systems.

```
public class UnixFileSystemReceiver implements FileSystemReceiver {

	@Override
	public String openFile() {
		return "Opening file in unix OS";
	}

	@Override
	public String writeFile() {
		return "Writing file in unix OS";
	}

	@Override
	public String closeFile() {
		return "Closing file in unix OS";
	}

}
```

```
public class WindowsFileSystemReceiver implements FileSystemReceiver {

	@Override
	public String openFile() {
		return "Opening file in Windows OS";
		
	}

	@Override
	public String writeFile() {
		return "Writing file in Windows OS";
	}

	@Override
	public String closeFile() {
		return "Closing file in Windows OS";
	}
}
```

Basic command could be created via abstract class or interface depending on the task that is solved. The example below is with interface:

```
public interface Command {

	String execute();
}
```

There must be implementations for every receiver action. Current example expects three Command implementations with each implementation forwarding the command to the corresponding receiver method:

```
public class OpenFileCommand implements Command {

	private FileSystemReceiver fileSystem;
	
	public OpenFileCommand(FileSystemReceiver fs){
		this.fileSystem = fs;
	}
	@Override
	public String execute() {
		//open command is forwarding request to openFile method
		return this.fileSystem.openFile();
	}

}
```

```
public class CloseFileCommand implements Command {

	private FileSystemReceiver fileSystem;
	
	public CloseFileCommand(FileSystemReceiver fs){
		this.fileSystem = fs;
	}

	@Override
	public String execute() {
		return this.fileSystem.closeFile();
	}

}
```

```
public class WriteFileCommand implements Command {

	private FileSystemReceiver fileSystem;
	
	public WriteFileCommand(FileSystemReceiver fs){
		this.fileSystem = fs;
	}

	@Override
	public String execute() {
		return this.fileSystem.writeFile();
	}

}
```

After creating all necessary implementations for receiver and commands there have to be invoker implementation.

Invoker is usually simple class with encapsulated command that transfers the command to the corresponding object for execution.

```
public class FileInvoker {

	public Command command;
	
	public FileInvoker(Command c){
		this.command = c;
	}
	
	public String execute(){
		return this.command.execute();
	}
}
```

The implementation of the file system utility is ready so there have to be client object developed. 

This example uses the following object:


```
public class FileSystemReceiverUtil {
	
	public static FileSystemReceiver getUnderlyingFileSystem(){
		 String osName = System.getProperty("os.name");
		 System.out.println("Underlying OS is:" + osName);
		 if (osName.contains("Windows")) {
			 return new WindowsFileSystemReceiver();
		 } else {
			 return new UnixFileSystemReceiver();
		 }
	}
	
}
```

After all development, testing its logic is shown below:

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
		System.out.println(file.execute());
		
		WriteFileCommand writeFileCommand = new WriteFileCommand(fs);
		file = new FileInvoker(writeFileCommand);
		System.out.println(file.execute());
		
		CloseFileCommand closeFileCommand = new CloseFileCommand(fs);
		file = new FileInvoker(closeFileCommand);
		System.out.println(file.execute());
	}

}
```

The client is responsible for creating the appropriate type of command object. For example, if a file is to be written, a CloseFileCommand object should not be created. The client program is also responsible for attaching the receiver to the command and then the command to the invoker class.

Conclusion:

The command is the core of the command design pattern, which defines the execution contract.

The receiver implementation is separate from the command implementation.

The command implementation classes choose the method to invoke on the receiver object, for each method in the receiver there will be a command implementation. It acts as a bridge between the receiver and the action methods.

The Invoker class simply forwards the request from the client to the command object. The client is responsible for creating the appropriate command and receiver implementation and then associating them together.

The client is also responsible for instantiating the invoker object and associating the command object with it and executing the action method.

The command design pattern is easily extensible, we can add new action methods to receivers and create new command implementations without changing the client code.

The disadvantage of the Command design pattern is that the code becomes huge and confusing with a large number of action methods and because of so many associations.

# State

The state design pattern allows the behavior of an object to change when its internal state changes. Typically, different states are separated into separate classes and the behavior is delegated to the current state.

State eliminates conditional logic through polymorphism. Its use saves writing multiple conditional constructs, highly branched logic, and violating OCP.

To implement the State pattern, a context (an object whose behavior is changing),
a state (described by an interface defining the behavior of the context), and specific states (representing implementations of a given state) are required.

Main features of State pattern:

- object's behaviour varies depending on its state

- the control over state changes is done either by states themselves or be the context (there are different possible implementations)

- uses composition and polymorphism

- decreses the conditional logic.

Below is considered example of a ticket that could be booked, sold or cancelled. The implementation is **without** using State.

```

public class Ticket {

    private String ticketState;
    private int ticketId;

    public Ticket(int ticketId) {
        this.ticketState = "available";
        this.ticketId = ticketId;
    }

    public String book() {
        if (ticketState.equalsIgnoreCase("available")) {
            ticketState = "booked";
            return "Ticket with id = " + this.ticketId + " has been booked";
        } else if (ticketState.equalsIgnoreCase("booked")) {
            return "Ticket with id = " + this.ticketId + " has been already booked";
        } else if (ticketState.equalsIgnoreCase("sold")) {
            return "Ticket with id = " + this.ticketId + " has been already sold";
        }
        return "Unrecognized state";
    }

    public String buy() {
        if (ticketState.equalsIgnoreCase("available") ||
				ticketState.equalsIgnoreCase("booked")) {
            ticketState = "sold";
            return "Ticket with id = " + this.ticketId + " has been sold";
        } else if (ticketState.equalsIgnoreCase("sold")) {
            return "Ticket with id = " + this.ticketId + " has been already sold";
        }
        return "Unrecognized state";
    }

    public String cancel() {
        if (ticketState.equalsIgnoreCase("available")) {
            return "Available ticket cannot be cancelled";
        } else if (ticketState.equalsIgnoreCase("booked")) {
            ticketState = "available";
            return "Ticket with id = " + this.ticketId + " has been cancelled";
        } else if (ticketState.equalsIgnoreCase("sold")) {
            return "Sold ticket cannot be cancelled";
        }
        return "Unrecognized state";
    }

}

public class Application {

    public static void main(String[] args) {
        Ticket ticket = new Ticket(123);
        System.out.println(ticket.book());
        System.out.println(ticket.cancel());
        System.out.println(ticket.buy());
        System.out.println(ticket.cancel());

    }
}

```

The obtained results are:

```
Ticket with id = 123 has been booked
Ticket with id = 123 has been cancelled
Ticket with id = 123 has been sold
Sold ticket cannot be cancelled

```


Same example is presented below **with** pattern State implemented:

```
public interface TicketState {

    String bookTicket(Ticket ticket);

    String buyTicket(Ticket ticket);

    String cancelTicket(Ticket ticket);
}
```
The interface defines all possible context states (Ticket is the context in this case).

Each state is represented by dedicated class.

```
public class AvailableTicket implements TicketState {

    @Override
    public String bookTicket(Ticket ticket) {
        ticket.setTicketState(new BookedTicket());
        return "Ticket with id = " + ticket.getTicketId() + " has been booked";
    }

    @Override
    public String buyTicket(Ticket ticket) {
        ticket.setTicketState(new SoldTicket());
        return "Ticket with id = " + ticket.getTicketId() + " has been sold";
    }

    @Override
    public String cancelTicket(Ticket ticket) {
        return "No ticket to be cancelled";
    }
}
```

```
public class BookedTicket implements TicketState {

    @Override
    public String bookTicket(Ticket ticket) {
        return "Ticket with id = " + ticket.getTicketId() + " is already booked";
    }

    @Override
    public String buyTicket(Ticket ticket) {
        ticket.setTicketState(new SoldTicket());
        return "Ticket with id = " + ticket.getTicketId() + " has been sold";
    }

    @Override
    public String cancelTicket(Ticket ticket) {
        ticket.setTicketState(new AvailableTicket());
        return "Ticket with id = " + ticket.getTicketId() + " has been cancelled";
    }
}
```

```
public class SoldTicket implements TicketState {

    @Override
    public String bookTicket(Ticket ticket) {
        return "Sold ticket cannot be booked";
    }

    @Override
    public String buyTicket(Ticket ticket) {
        return "Sold ticket cannot be sold";
    }

    @Override
    public String cancelTicket(Ticket ticket) {
        return "Sold ticket cannot be cancelled";
    }
}
```

The context object Ticket will be:

```
public class Ticket {

    private TicketState ticketState = new AvailableTicket();
    private int ticketId;

    public Ticket(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketState(TicketState ticketState) {
        this.ticketState = ticketState;
    }

    public String book() {
        return ticketState.bookTicket(this);
    }

    public String buy() {
        return ticketState.buyTicket(this);
    }

    public String cancel() {
        return ticketState.cancelTicket(this);
    }
}

```

The testing and the result of the implemented code look completely identical.

The example implemented with State presents a clear separation of roles and in it the state does not store internal data.

It is important to note that State is not a universal solution for eliminating all conditional logic.



In conclusion, using the State pattern is appropriate when solving tasks where:

- there are clearly defined states

- the behavior of the object changes

- transitions between different states are observed

- we do not want conditional logic in the context.

  

**PRACTICE**

Task 1. Create a program that processes payments using different payment methods (e.g., credit card, PayPal, Bitcoin) using the Strategy Pattern.

Task 2. Enhance the written program from task 1 using Builder pattern.
