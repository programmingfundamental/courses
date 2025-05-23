---
layout: default
title: Филтри и FilterChain
parent: Лабораторно упражнение 11
grand_parent: Интернет технологии
nav_order: 3
---

# Филтри и FilterChain

В типично уеб приложение на Java клиентът изисква от сървъра достъп до ресурс чрез HTTP или HTTPS протокол. Клиентската заявка в сървъра се обработва от сървлет. Сървлетът обработва HTTP заявката и предоставя HTTP отговор, който се изпраща обратно на клиента. Основен компонент на спецификацията на Servlet, който играе основна роля в обработката на заявка-отговор, е филтърът. Филтърът се намира преди сървлета и прихваща заявката и отговора. Той може да прави промени в обектите request и response. Един или повече филтри могат да бъдат конфигурирани чрез FilterChain и всички филтри, които са част от веригата, могат да прихващат и модифицират обектите заявка-отговор. Много от функциите на Spring Security са базирани именно на филтри.

<figure><img src="../../../assets/image (159).png" alt=""><figcaption></figcaption></figure>

Подобно на начина, по който специален сървлет, наречен DispatcherServlet, обработва всички входящи заявки в Spring Web приложение, специален филтър, наречен DelegatingFilterProxy, се използва за активиране на Spring Security. Този филтър е регистриран в контейнера на сървлета и започва да прихваща входящите заявки. В приложение Spring Boot тази регистрация е направено от автоконфигурацията на Spring Security на Spring Boot. Нека сега да разгледаме интерфейса на филтъра, както е показано в следния списък.

```java
public interface Filter {

    public default void init(FilterConfig filterConfig) throws
    ServletException {}
    
    public void doFilter(ServletRequest request, ServletResponse response,
    FilterChain chain) throws IOException, ServletException;
    
    public default void destroy() {}
}
```

Имплементацията на Filter трябва да приложи три метода (init(), doFilter() и destroy(..)), както е показано на фигурата по-долу.

<figure><img src="../../../assets/image (164).png" alt=""><figcaption></figcaption></figure>

Трите филтърни метода са разгледани по-долу:

·        Init(..) се извиква от уеб контейнера, за да укаже на филтъра, че е пуснат в експлоатация.

·        doFilter(..) е основният метод, при който се извършва действителното функциониране на филтъра. Той има достъп до заявката, отговора и обектите FilterChain. FilterChain позволява на текущия филтър да извика следващия филтър във веригата, след като обработката му приключи.

·        Destroy() се извиква, когато контейнерът извади филтъра от работа.

FilterChain е друг компонент, осигурен от контейнера на сървлета, който предоставя изглед във веригата за извикване на филтрирана заявка. Фигурата по-долу показва примерна филтърна верига. Филтрите използват FilterChain, за да извикат следващия филтър във веригата или действителния ресурс (напр. сървлета), ако филтърът е последният във веригата. FilterChain има само един метод, наречен doFilter(). Методът doFilter() в интерфейса на филтъра има достъп до FilterChain заедно с обектите ServletRequest и ServletResponse. По този начин филтърът може да изпълни възложената му задача и да получи достъп до FilterChain, за да извика следващия филтър във веригата. 

<figure><img src="../../../assets/image (155).png" alt=""><figcaption></figcaption></figure>

```java
public interface FilterChain {
    public void doFilter(ServletRequest request, ServletResponse response)
        throws IOException, ServletException;
}
```

Spring Security използва интензивно филтрите за прилагане на различни функции за сигурност. Основата на Spring Security се базира на тези филтри. Например, ако Spring Security трябва да извърши удостоверяване, базирано на потребителско име и парола, той делегира заявката към филтър, наречен UsernamePasswordAuthenticationFilter, който отговаря за удостоверяването на потребителя въз основа на предоставените идентификационни данни. По същия начин, за основно HTTP удостоверяване Spring Security използва BasicAuthenticationFilter.
