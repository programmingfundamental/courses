---
title: Tasks
taskPage: true
sidebar:
  label: Tasks
  order: 100
---
### Task 1

Create program with the Book example (for Builder).

* Expand class Book with the necessary fields and constructors
* Complete method build in order to create Book object
* Validate fileds' values.

### Task 2

Create program for cars creation:

* Interface Мotion with method void go(double mileage)
* Classes for car components:
  * Engine - volume, mileage, started/stopped
    * implements Motion and increases mileage if the car is in movement, elsewhere an exception is thrown;
  * GPSNaviator - destination
  * Enumeration Transmission - SINGLE_SPEED, MANUAL, AUTOMATIC, SEMI_AUTATIC
  * TripComputer - returns information for the fuel, engine condition (started/stopped) and mileage.
* Car classes
  * Enum CarType - CITY\_CAR, SPORTS\_CAR, SUV
  * Abstract class Vehicle
    * fields
      * carType
      * seats
      * engine
      * transmission
      * tripComputer
      * gpsNavigator
  * Class Car:
    * extends Vehicle
    * methods - getters
  * Class Manual
    * extends Vehicle
    * method toString returns car information and information for "Trip Computer" and "GPS Navigator"
* Creational classes:
  * Interface Builder with component setters and method build
  * Class CarBuilder
  * Class CarManualBuilder
* Class Director
  * creates city car
  * creates sport car
  * creates SUV
* Main function where three types cars are build.



BONUS TASK

Implement Singleton and Builder to create a real estate agents program.

Each agent has their own name, contact phone, and uses a shared list of real estate properties. They can add and remove properties from this list.

Each property has characteristics such as type (house, apartment, or office), area, price, number of rooms, furnished or not, availability of garage/parking space, and garden.
