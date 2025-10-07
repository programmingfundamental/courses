---
layout: default
title: Example
parent: Lab 1
grand_parent: Mobile and Internet Programming Kotlin
nav_order: 1
---

# Kotlin code example

Kotlin code could be short since the language eliminates code like getters & setters.

### Aquarium class - Java

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

### Aquarium class - Kotlin

```kotlin
data class Aquarium (var temperature: Int = 0)
```
