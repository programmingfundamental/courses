---
title: Пример за Kotlin код
sidebar:
  order: 1
---

# Пример за Kotlin код

Свойствата в Kotlin намаляват необходимостта от ръчно писане на getter и setter. При клас за данни (`data class`) компилаторът генерира и функции като `toString()`, `equals()` и `hashCode()` въз основа на свойствата в първичния конструктор. Двата примера показват съхраняване на температура, но не са напълно равностойни по генерирано поведение.

## Клас за аквариум в Java

```java
public class Aquarium {

   private int temperature;

   public Aquarium() { }

   public int getTemperature() {
       return temperature;
   }

   public void setTemperature(int temperature) {
       this.temperature = temperature;
   }

   @Override
   public String toString() {
       return "Aquarium{" +
               "temperature=" + temperature +
               '}';
   }
}
```

## Клас за аквариум в Kotlin

```kotlin
data class Aquarium (var temperature: Int = 0)
```
