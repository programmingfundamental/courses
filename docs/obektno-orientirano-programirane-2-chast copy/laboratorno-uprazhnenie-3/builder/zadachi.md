# Задачи

### Задача 1

Създайте програма с примера "_Използване на Builder_" от упражнението.

* Допълнете класа Book с нужните полета и конструктори
* Допълнете метода build, за да се създават обекти от Book.
* Сложете нужните проверки, за да са валидни стойностите на полетата в класа Book (да се провери, че всяко поле от класа Builder е сетнато и може да се създаде обект Book, ако не може да се продуцира грешка)

### Задача 2

Съставете програма която създава автомобили, с клас директора който дефинира реда за създаване на автомобилите. За целта ще имате нужда от:

* Индерфейс Мotion с метод void go(double mileage)
* Класове компонени на автомобилите:
  * Engine - обем, пробег, флаг за стартиран или спрян
    * Имплементира Motion като увеличава пробега на автомобила ако колата е в движение ако не е продуцира грешка
  * GPSNaviator - дестинация
  * Енумерация Transmission - SINGLE_SPEED, MANUAL, AUTOMATIC, SEMI_AUTATIC
  * TripComputer - връща информация за горивото в колата, състоянието на двигателя дали е стартиран или спрян и изминатия пробег.
* Класове автомобил
  * Енумерация CarType с изброени типове автомобили CITY\_CAR, SPORTS\_CAR, SUV
  * Абстрактен клас Vehicle
    * полета
      * carType
      * seats
      * engine
      * transmission
      * tripComputer - кога се добавя инстанцията наавтомобила?
      * gpsNavigator
  * Клас Car с:
    * наследява Vehicle
    * Методи - за извличане стойностите на полетата
  * Клас Manual
    * наследява Vehicle
    * Метод toString връща информацията за автомобила и наличието или липсата на функция за "Trip Computer" и "GPS Navigator"
* Класове за създаване на автомобили:
  * Интерфейс Builder с методи за сетване компонентите на автомобила и темплейтен метод build
  * Клас CarBuilder имплементира Builder
  * Клас CarManualBuilder имплементира Builder
* Клас Direktor
  * Конструира градски автомобил
  * Конструира спортен автомобил
  * Конструира СУВ автомобил
* В main се билдват трите вида автомобили

### Задача 3

Преработете задачата за книжарница от упражнение 1 с използване на шаблона Builder

