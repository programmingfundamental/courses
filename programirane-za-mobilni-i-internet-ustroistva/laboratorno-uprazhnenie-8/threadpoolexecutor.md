# ThreadPoolExecutor

ThreadPoolExecutor е чудесен начин за изпълнение на паралелизирани задачи в множество различни нишки в рамките на ограничен набор от нишки. Ако искате да изпълнявате работа едновременно и да запазите контрола върху това как се изпълнява работата, това е инструментът за работата.

Използването на ThreadPoolExecutor започва с конструиране на нова инстанция с конфигуриране пула от нишки:

```
// Определете броя на ядрата на устройството 
int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
// Създайте пул от нишки, преминавайки в опции за конфигуриране 
// int minPoolSize, int maxPoolSize, long keepAliveTime, TimeUnit unit,
// BlockingQueue<Runnable> workQueue
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    NUMBER_OF_CORES*2,
    NUMBER_OF_CORES*2,
    60L,
    TimeUnit.SECONDS,
    new LinkedBlockingQueue<Runnable>()
);
```

### Използване на Runnables в ThreadPoolExecutor

Добавяне на изпилниза задача към пула с нишки

```
// Изпълнява задача върху нишка в пула от нишки 
executor.execute(new Runnable() {
    public void run() {
        // Кода на задач1ата която ще се изпълни на нишка от пула с нишки
    }
});
```

Нишките се използват едновременно и приемат съобщения, докато всички нишки не са заети. Ако всички нишки в момента са заети, Executor обекта ще постави на опашка новата задача, докато някоя нишка стане налична.

Спиране на ThreadPoolExecutor

Пулът от нишки може да бъде изключен по всяко време с командата за изключване:

```
executor.shutdown();
```

Това ще спе всички задачи безопасно, след като всички са обработени. За да се изключи незабавно изпълнението, вместо това се използва executor.shutdownNow ().
