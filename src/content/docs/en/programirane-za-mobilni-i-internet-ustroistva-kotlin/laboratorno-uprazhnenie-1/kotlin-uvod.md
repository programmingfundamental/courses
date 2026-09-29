---
title: Kotlin code example
sidebar:
  order: 1
---

# Kotlin code example

Kotlin properties reduce the need to write getters and setters manually. For a `data class`, the compiler also generates functions such as `toString()`, `equals()`, and `hashCode()` based on the properties in the primary constructor. Both examples store a temperature, but they are not fully equivalent in their generated behavior.

## Aquarium class in Java

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

## Aquarium class in Kotlin

```kotlin
data class Aquarium (var temperature: Int = 0)
```
