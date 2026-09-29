---
title: Tasks
sidebar:
  order: 100
  label: Tasks
taskPage: true
---
## Task 1

![Diagram of Animal and its subclasses Duck, Fish, and Zebra](/courses/docs/BEO/programirane-za-mobilni-i-internet-ustroistva-kotlin/laboratorno-uprazhnenie-2/image-1.png)

## Task 2

![Diagram of BankAccount and its subclasses CheckingAccount and SavingsAccount](/courses/docs/BEO/programirane-za-mobilni-i-internet-ustroistva-kotlin/laboratorno-uprazhnenie-2/image-2.png)

## Task 3

![Diagram of the Person interface and its implementations Professor and Student](/courses/docs/BEO/programirane-za-mobilni-i-internet-ustroistva-kotlin/laboratorno-uprazhnenie-2/image.png)

## Independent tasks

Create a console-based device model. Use mock data; no hardware or network access is required.

### Task 1. Encapsulating state

Create a `Device` class with an immutable identifier, a name, and a Boolean `isOn` state that can be read externally but changed only through `turnOn()` and `turnOff()`. The device should be off when created. Repeatedly turning it on or off should preserve the same state.

Create two objects and show that changing one does not change the other.

### Task 2. Inheritance and an interface

Extend the model with `SmartLamp` and `TemperatureSensor`. Define a `StatusProvider` interface with `status(): String` and implement it in both classes. The lamp should report whether it is lit, while the sensor should report a preset sample temperature when on and "Off" when off.

Iterate over a shared `List<StatusProvider>` and call `status()` for each element without checking its specific class.

### Task 3. A data snapshot

Create a `data class DeviceSnapshot` with `id`, `name`, and `isOn` properties. Create a snapshot of a device, change the device, and show that the existing snapshot retains the old values.

Use `copy()` to create a snapshot with a different name. Use `==` and `===` to compare the original, an unchanged copy, and a copy with a different name. Explain the results.

### Verification and submission

Submit the classes and a demonstration in `main()`. Check the initial state, repeated operations, object independence, and the results of the three comparisons. Describe which behavior is provided by inheritance and which by the interface.
