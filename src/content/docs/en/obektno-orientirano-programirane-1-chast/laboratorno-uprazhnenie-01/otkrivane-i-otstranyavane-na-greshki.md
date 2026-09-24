---
title: Debugging
sidebar:
  order: 13
---

# Debugging

Debugging is a process: reproduce the problem, compare expected and actual results, trace execution, fix the cause, and repeat the check. A debugger is a tool for observing this process.

## Types of Errors

A program error is a condition in which code does not compile, stops while running, or produces a result different from the expected one. Finding errors requires observing the program's execution, variable values, and the paths it takes through conditionals, loops, and methods.

A syntax error occurs when code does not follow Java's rules. The compiler detects it before the program starts.

```java
int number = 10
System.out.println(number);
```

The first statement is missing a semicolon, so the program cannot be compiled.

A runtime error occurs after successful compilation, when the program reaches an operation that cannot be performed.

```java
int result = 10 / 0;
```

Dividing by zero stops the program at runtime.

A logic error occurs when the program compiles and runs but produces an incorrect result.

```java
int width = 5;
int height = 10;
int perimeter = width * height;
```

The code runs, but calculates area instead of perimeter. Detect this kind of error by checking values and comparing them with the expected result.

## Debug Mode

Debug mode lets you run a program under controlled conditions. Instead of running the entire program at once, you can pause it on selected lines and inspect variable values and the execution order.

You can start a program in debug mode from the **Run** menu, the icon next to `main`, the context menu, or the IntelliJ IDEA toolbar.

<img width="448" height="255" alt="Starting Debug mode from the Run menu in IntelliJ IDEA" src="https://github.com/user-attachments/assets/137eed19-8820-4a65-8448-f1ffbe20b432" />

<img width="434" height="169" alt="Starting Debug mode from the icon next to the main method" src="https://github.com/user-attachments/assets/f2b3e502-f88a-4858-a48a-1316d1134fea" />

<img width="418" height="380" alt="Starting Debug mode from the context menu" src="https://github.com/user-attachments/assets/5d82fcb1-33d5-4e17-8d0e-cdb7150fa634" />

<img width="331" height="87" alt="Starting Debug mode from the toolbar" src="https://github.com/user-attachments/assets/66a44873-c1b2-44f8-a93b-8a9950e48809" />

A breakpoint is placed on a line of the program. When execution reaches that line, the program pauses temporarily so that you can inspect its current state.

```java
int first = 10;
int second = 20;
int sum = first + second;

System.out.println(sum);
```

If you place a breakpoint on the line with `sum`, you can inspect the values of `first` and `second` before the calculation.

## Breakpoints

A breakpoint is a marker on a specific line. When execution reaches that line in debug mode, the program pauses before running the instruction.

Click in the gutter beside the line numbers to set a breakpoint. IntelliJ IDEA displays a marker beside the line where execution should stop.

<img width="600" height="240" alt="Breakpoint set in IntelliJ IDEA" src="https://github.com/user-attachments/assets/b1262d5c-056c-429d-ba37-ebbe29e4b74e" />

Use a breakpoint to check:

- whether a line is reached;
- the current values of variables;
- which branch of a conditional runs;
- how many times a loop runs.

```java
int total = 0;

for (int i = 1; i <= 5; i++) {
    total = total + i;
}
```

A breakpoint inside the loop lets you observe how `i` and `total` change during each iteration.

You can add a condition to a breakpoint. Execution then pauses only when that condition is `true`.

<img width="532" height="307" alt="Breakpoint condition in IntelliJ IDEA" src="https://github.com/user-attachments/assets/b9c1e909-3fef-49ee-aba2-abdd668230e3" />

## Stepping Through a Program

Stepping lets you move through a program one line at a time.

`Step Over` runs the current line and moves to the next one. If the line calls a method, the method runs without entering its body.

`Step Into` enters a method call. Use it to trace how a method works internally.

`Step Out` finishes the current method and returns execution to the place where the method was called.

## Pausing, Resuming, and Stopping the Debugger

You can control a debug session with pause, resume, and stop commands.

`Pause` temporarily suspends the running program. Use it if the program is taking a long time or appears to be stuck in a loop and you need to inspect the current execution point.

`Resume Program` continues execution after a breakpoint. The program runs until it reaches another breakpoint or finishes.

`Stop` ends the debug session. The program is no longer controlled by the debugger.

## The Debugger Window

The debugger window shows information about the program's current state while execution is paused. It displays the current method, the call stack, variable values, and program output.

<img width="1048" height="242" alt="Debugger window in IntelliJ IDEA" src="https://github.com/user-attachments/assets/315adf03-e376-40af-9220-a90305ef1adc" />

| Panel | Purpose |
| ----- | ------- |
| `Debugger` | Shows debug-session controls and the current execution point. |
| `Console` | Shows standard output and program messages. |
| `Frames` | Shows the sequence of method calls up to the current point. |
| `Variables` | Shows local variables, parameters, and fields of accessible objects. |
| `Watches` | Lets you monitor selected expressions while debugging. |

The `Frames` call stack is useful when one method calls another. It shows how execution reached the current line.

## The Variables Window

When the program is paused, the debugger shows the current values of local variables, parameters, and objects. If a variable refers to an object, you can inspect that object's fields as well.

```java
class Student {

    private String name;
    private int facultyNumber;

    public Student(String name, int facultyNumber) {
        this.name = name;
        this.facultyNumber = facultyNumber;
    }
}
```

After creating a `Student` object, pause the program to check whether its `name` and `facultyNumber` fields were initialized correctly.

## Evaluating Expressions

Expression evaluation calculates a Java expression using the current values of variables while the program is paused.

```java
int price = 50;
int quantity = 3;
int discount = 10;
```

During a debug session, evaluate this expression:

```java
price * quantity - discount
```

The result is `140`. This lets you check whether a formula, condition, or method produces the expected value.

## Quick Evaluate Expression

Use `Quick Evaluate Expression` to quickly inspect a selected expression while debugging. This option is available when the program is paused at a breakpoint.

```java
int first = 10;
int second = 20;
int result = first + second;
```

If execution is paused before or after `result` is calculated, select `first + second` to check its value. In IntelliJ IDEA, use `Run -> Debugging Actions -> Quick Evaluate Expression`, the `Ctrl+Alt+F8` shortcut, or `Alt`-click the selected expression.

## Evaluate Expression

`Evaluate Expression` lets you evaluate an expression or a short code fragment in the current execution context. It is more flexible than quick evaluation because you can enter an expression that is not selected in the editor.

<img width="581" height="333" alt="Evaluate Expression in IntelliJ IDEA" src="https://github.com/user-attachments/assets/e9427a70-f74c-4a97-97d5-7abb3839f090" />

```java
int price = 50;
int quantity = 3;
int discount = 10;
```

While the program is paused, evaluate:

```java
price * quantity - discount
```

You can also evaluate a condition:

```java
price * quantity > 100
```

In IntelliJ IDEA, open this action through `Run -> Debugging Actions -> Evaluate Expression` or with `Alt+F8`. Evaluation uses the current stack frame, so only variables and objects accessible where the program paused can be used.

## Evaluating a Condition

Expression evaluation is useful with conditional statements.

```java
int age = 17;

if (age >= 18) {
    System.out.println("Adult");
} else {
    System.out.println("Minor");
}
```

If execution pauses before the `if`, evaluate `age >= 18` to see which branch will run.

## Arguments to `main`

The `main` method can receive arguments through the `String[] args` array.

```java
public class Application {

    public static void main(String[] args) {
        System.out.println(args[0]);
    }
}
```

If the program starts without arguments, accessing `args[0]` causes a runtime error. Use debug mode to inspect the array length with `args.length` and find the cause.

Set arguments to `main` in the run configuration's **Program arguments** field.

<img width="524" height="543" alt="Setting arguments for main in IntelliJ IDEA" src="https://github.com/user-attachments/assets/cbd8c6fd-d795-44ce-b3a1-4c17866fad11" />
