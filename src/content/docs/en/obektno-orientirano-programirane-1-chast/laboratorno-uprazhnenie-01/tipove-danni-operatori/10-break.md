---
title: Flow Control Statements
sidebar:
  order: 11
---

# Flow Control Statements

The `break` and `continue` statements control loop execution. `break` exits a loop or a `switch` statement, while `continue` skips the current loop iteration.

## The `break` Statement

The `break` statement exits the innermost loop (`for`, `while`, or `do-while`) or `switch` statement that contains it. Execution then continues with the first statement after it.

Syntax:

```java
break;
```

Example with a `for` loop:

```java
for (int i = 1; i <= 10; i++) {

    if (i == 5) {
        break;
    }

    System.out.println(i);
}
```

Output:

```text
1
2
3
4
```

The `break` statement immediately exits the innermost loop or `switch` statement that contains it. Control then moves to the first statement after that construct.

In this example, the loop starts printing values from 1 to 10. When it reaches 5, the condition is true and `break` runs, ending the loop early. The console displays 1, 2, 3, and 4.

Example with `switch`:

```java
int day = 2;

switch (day) {

    case 1:
        System.out.println("Monday");
        break;
    case 2:
        System.out.println("Tuesday");
        break;
    default:
        System.out.println("Unknown");
}
```

The `switch` statement selects a block based on the value of an expression. After the instructions in the matching case run, `break` exits the `switch` and control moves to the next statement.

Here, `day` is 2, so the statements in `case 2` run. After “Tuesday” is printed, `break` exits the construct and the other cases do not run.

## The `continue` Statement

The `continue` statement ends the current loop iteration and moves to the next one.

Syntax:

```java
continue;
```

Example:

```java
for (int i = 1; i <= 5; i++) {

    if (i == 3) {
        continue;
    }

    System.out.println(i);
}
```

Output:

```text
1
2
4
5
```

The `continue` statement skips the rest of the current iteration and moves directly to the next one. Statements after `continue` in the loop body are not executed during that iteration.

Here, the loop visits values from 1 to 5. When `i` is 3, `continue` skips printing it. The console displays 1, 2, 4, and 5.

## Comparison

| Statement | Purpose |
| --------- | ------- |
| `break` | Exits a loop or `switch` statement. |
| `continue` | Skips the current loop iteration and moves to the next one. |
