# Управление на конфигурацията с application.properties

Spring Initializr генерира празен файл application.properties в папката src/main/resources. Този файл със свойства ви позволява да зададете различни конфигурации на приложението (напр. подробности за сървъра или подробности за базата данни). Въпреки че има множество начини за задаване на properties на Spring Boot приложение, това е най-често използваният подход. Този файл със свойства ви позволява да укажете конфигурациите във формат на двойка ключ-стойност, където ключът е отделен от свързаната стойност със знак =. Следващата фигура показва примерна конфигурация в application.properties файл за конфигуриране на адреса на сървъра и порта на Spring Boot приложение.

<figure><img src="../../../.gitbook/assets/image (17).png" alt=""><figcaption></figcaption></figure>

За да видите файла application.properties на практика, можете да промените стойността на server.port в текущото приложение до различна стойност на HTTP порт (напр. до 9090). Ако стартирате приложението след тази модификация, можете да видите, че стартира на актуализирания HTTP порт.

Ако не харесвате този файлов формат, можете алтернативно да използвате файловия формат YAML (https://yaml.org/spec/1.2.2/), за да конфигурирате свойствата на приложението. YAML ви позволява да дефинирате йерархично свойствата. Ако искате да използвате файловия формат YAML, можете да преименувате съществуващия файл application.properties на application.yml и да зададете свойствата във YAML формат. &#x20;

![](<../../../.gitbook/assets/image (38).png>)

Можете да откриете  списък на поддържаните свойства от application.properties  на уебсайта на Spring Boot ([https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html)).