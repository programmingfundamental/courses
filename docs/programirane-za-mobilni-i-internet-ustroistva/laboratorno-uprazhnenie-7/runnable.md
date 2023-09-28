# Runnable

Runnable е интерфасе, който трябва да се наследи от всеки клас чиито инстанции ще се изпълняват от нишка. Калса трябва да имплементира метода run().

Този интерфейс е предназначен да предоставя общ протокол за обекти, които желаят да изпълняват код, докато са активни. Да бъдеш активен просто означава, че е стартирана нишка и все още не е спряна.

Когато бъде стартирана нишка с обект наследяващ Runnable се изпълнява метода run.

**Пример**

```
public class TaskToRun implements Runnable {
    @Override
    public void run() {
        // Кодът, който ще се изпълни в нишка
    }
};
```

### Thread

Thread е единица за изпълнение, която изпълнява код, дефиниран в Runnable. Дефинираното по-горе taskToRun може да се изпълни с помощта на нишка:

**Пример**

```
TaskToRun taskToRun = TaskToRun();

Thread thread = new Thread(taskToRun);
thread.start();
```

### Executor

Обект, който изпълнява подадените задачи. Този интерфейс предоставя начин за отделяне на задачата от механиката на това как ще се изпълнява всяка задача, включително подробности за използването на нишките, планиране и др. Обикновено се използва вместо изрично създаване на нишки.

**Пример**

```
class DirectExecutor implements Executor {
   public void execute(Runnable r) {
     r.run();
   }
 }
```