---
layout: default
title: Жизнен цикъл на сървлета
parent: Лабораторно упражнение 2
grand_parent: Интернет технологии
nav_order: 7
---

# Жизнен цикъл на сървлета

Жизненият цикъл на сървлета може да се дефинира като времето от неговото създаване до премахването му от паметта. Етапите, през които преминава сървлета, са следните:

* инициализация на сървлета чрез повикване на init() метода му;
* сървлетът повиква метода си service() за да обслужва клиентските заявки;
* сървлетът е деактивиран чрез повикване на метода му destroy() от контейнера;
* сървлетът е премахнат от паметта от почистващата услуга на Java виртуалната машина (JVM).

## Методът `init()`

Методът е проектиран да бъде извикан само веднъж. Извиква се при създаването на сървлета и повече не се извиква. Използва се за първоначална инициализация. Сървлетът обикновено се създава, когато клиентът за първи път достъпи URL адреса, на който той съответства, но може да бъде създаден и при стартирането на сървъра. Когато клиент направи достъп до сървлета, се създава единична негова инстанция, а обслужването на всяка клиентска заявка се обработва в отделна нишка. Методът init() също така създава или зарежда различни помощни данни, необходими за работата на сървлета. Дефиницията на метода е следната:

```
public void init() throws ServletException{
    // инициализиращ код...
}
```

## Методът `service()`

Това е основният метод на сървлета, който извършва същинската работа. Сървлет контейнерът извиква този метод, за да бъдат обслужени клиентските заявки и да бъде генериран форматиран отговор за клиента. Всеки път, когато сървърът получи заявка за сървлет, той създава нова нишка и извиква service(). Методът проверява типа на заявката (GET, POST, PUT, DELETE, etc.) и извиква съответния метод, реализиран в сървлета.

```
public void service(ServletRequest request, ServletResponse response)
throws ServletException, IOException {
    …
}
```

Методите doGet() и doPost() са най-често използваните методи във всяка клиентска заявка.

### Методът doGet()

Методът обработва постъпила GET заявка от клиента, която е резултат от нормално извикване URL адреса на сървлета или от HTML форма, в която не е указан `method`. Заявката от тип GET предава данни към сървъра посредством параметри на URL адреса `Query string`.

Query string параметрите се изписват след URL адреса като започват с `?`. Те се състоят от ключ и стойност, като при изброяване на множество параметри се разделят със символа `&`.

`http://example.com?key1=value1&key2=value2`

```
public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    // код на сървлета
}
```

### Методът doPost()

Методът обработва постъпила POST заявка от клиента, която е резултат от HTML форма, в която конкретно е зададен `method="POST"`. Заявката от тип POST предава данни към сървъра посредством тялото на HTTP пакета, което не изменя URL адреса и позволява предаване на данни с по-голям обем.

Параметрите на POST заявката се състоят от ключ и стойност, които се групират в тялото на HTTP пакета.

```
public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // код на сървлета
}
```

## Методът destroy()

Методът се извиква само веднъж по време на жизнения цикъл на сървлета. Този метод дава възможност на сървлета да извърши някои финализиращи операции като затваряне на връзка към база от данни, записване на бисквитка или някаква почистваща операция. След неговото повикване сървлета е маркиран за почистване.

```
public void destroy(){
    // финализиращ код...
}
```
