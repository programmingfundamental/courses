---
title: Програмиране в мрежова среда
sidebar:
  order: 13
---

Практически курс **„Мрежово програмиране с Java“** за студенти в магистърска степен: 7 лабораторни упражнения по 3 учебни часа (135 минути).

Развиваме общ проект **LabNet** — мрежова услуга със собствен binary protocol. Всяко упражнение започва с инженерен проблем и наблюдаем експеримент, продължава с реализация и тестове при отказ и завършва с измервания и анализ.

## Предварителни знания и инструменти

Необходими са добро владеене на Java и ООП, базови знания за операционни системи, TCP/IP и concurrency. Използвайте JDK 25 или обща JDK 21 среда, IDE, terminal, Git и Wireshark. Основните реализации използват standard Java networking APIs.

## Програма

| Упражнение | Инженерен проблем |
|---|---|
| [1. TCP sockets и application protocol](/courses/bg/programirane-v-mrezhova-sreda/laboratorno-uprazhnenie-1/) | Как възстановяваме съобщения от byte stream? |
| [2. Concurrent TCP Server](/courses/bg/programirane-v-mrezhova-sreda/laboratorno-uprazhnenie-2/) | Как обслужваме едновременни клиенти с коректно shared state? |
| [3. UDP и reliability](/courses/bg/programirane-v-mrezhova-sreda/laboratorno-uprazhnenie-3/) | Как обработваме загуба, повторение и разместване? |
| [4. Java NIO и event-driven networking](/courses/bg/programirane-v-mrezhova-sreda/laboratorno-uprazhnenie-4/) | Как управляваме много connections чрез event loop? |
| [5. Async processing и backpressure](/courses/bg/programirane-v-mrezhova-sreda/laboratorno-uprazhnenie-5/) | Как ограничаваме претоварването и опашките? |
| [6. TLS и defensive programming](/courses/bg/programirane-v-mrezhova-sreda/laboratorno-uprazhnenie-6/) | Как защитаваме transport и application resources? |
| [7. Performance engineering](/courses/bg/programirane-v-mrezhova-sreda/laboratorno-uprazhnenie-7/) | Как сравняваме архитектури чрез възпроизводими измервания? |

## Начин на работа

Запазвайте работеща Git версия след всяко упражнение. Следващите лаборатории използват общия codec, protocol tests и сървърни варианти. UDP е отделен adapter, а TLS се добавя върху blocking варианта. Финалното сравнение използва еднаква ECHO функционалност.

Предавайте source code, команди за възпроизвеждане, failure tests, измервания и кратък анализ. Самостоятелното надграждане и допълнителните задачи се изпълняват след основните 135 минути. Финалният резултат е технически доклад **Comparative Analysis of Java Network I/O Models** с таблици, графики и ограничения на експеримента.
