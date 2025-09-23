---
layout: default
title: Пример за Kotlin код
parent: Лабораторно упражнение 1
grand_parent: Програмиране за мобилни и Интернет устройства Kotlin
nav_order: 1
---

# Пример за Kotlin код

Кодът, написан на Kotlin, може да бъде много кратък и езикът е проектиран да елиминира шаблонен код като гетери и сетери.

### Клас за аквариум в Java

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

### Калс за аквариум в Kotlin

```kotlin
data class Aquarium (var temperature: Int = 0)
```