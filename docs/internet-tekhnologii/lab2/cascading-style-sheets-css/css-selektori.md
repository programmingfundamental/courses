# CSS селектори

С помощта на CSS селекторите се задава по отношение на кои HTML елементи да бъде приложено дадено стилизиране.

### Базисни селектори

* Селектиране на елементи от даден таг

```
/* сeлектира всички елементи с таг <p> */
p { 
    color: #222;
    font-size: 16px;
}

/* селектира едновременно всички заглавия от ниво 3 и всички параграфи */
h3, p { 
    color: #222;
    font-size: 16px;
}
```

* Селектиране по клас

```
/* сeлектира всички елементи, по отношение на които е зададен атрибут 
class="some-style" */

.some-style {
    color: darkblue;
    font-size: 18px;
}

/* сeлектира всички div елементи, по отношение на които е зададен атрибут 
class="another-style" */

div.another-style {
    margin: 20px;
    padding: 10px;
}
```

* Селектиране по идентификатор (id)

```
/* сeлектира елемента с id="first" */

#first {
    text-transform: uppercase;
}
 
 <h1 id="first">Some text</h1> 
```

* Универсален селектор

```
/* селектира всички елементи от страницата */

* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

```

### Комбинирани селектори

* a b - селектира всички елементи b, вложени в a

```
/* селектира всички параграфи, вложени в <div> */

div p {
    color: red;
}

<body>
    <div>
        <p>Този елемент ще бъде селектиран</p>
        <p>Този елемент ще бъде селектиран</p>
        <section>
            <p>Този елемент ще бъде селектиран</p>
        </section>
    </div>

    <p>Този елемент НЯМА да бъде селектиран</p>
</body>
```

* a > b - селектира всички елементи b, вложени на първо ниво в a

```
/* селектира всички параграфи, вложени на първо ниво в <div> */

div > p {
    color: red;
}

<body>
    <div>
        <p>Този елемент ще бъде селектиран</p>
        <p>Този елемент ще бъде селектиран</p>
        <section>
            <p>Този елемент НЯМА да бъде селектиран</p>
        </section>
    </div>

    <p>Този елемент НЯМА да бъде селектиран</p>
</body>
```

* a + b - селектира всички елементи b, които се намират непосредствено след a

```
/* селектира всички параграфи, разположени непосредствено след <div> */

div + p {
    color: red;
}

<body>
    <div>
        <p>Този елемент НЯМА да бъде селектиран</p>
    </div>
    <p>Този елемент ще бъде селектиран</p>
    <p>Този елемент НЯМА да бъде селектиран</p>
</body>
```

* a \~ b - селектира всички елементи b, предшествани от a, когато a и b имат един и същи родителски елемент

```
/* селектира всички параграфи, разположени след <div>, 
когато се намират в един и същи родителски елемент */

div ~ p {
    color: red;
}

<body>
    <section>
        <div>
            <p>Този елемент НЯМА да бъде селектиран</p>
        </div>
        <p>Този елемент ще бъде селектиран</p>
        <p>Този елемент ще бъде селектиран</p>
        <p>Този елемент ще бъде селектиран</p>
    </section>
    <p>Този елемент НЯМА да бъде селектиран</p>
</body>
```

### Псевдоселектори

* За състояние - стилът се прилага към елемента въз основа на неговото състояние;
* За псевдоелементи, дефиниращи част от елемент

```
/* Селектира непосетена хипервръзка */
a:link {
    color: red;
}

/* Селектира посетена хипрвръзка */
visited {
    color: green;
}

/* Селектира хипервръзка с поставен курсор върху нея */
a:hover {
    color: grey;
}

/* Селектира хипервръзка, върху която е натиснат ляв бутон на мишката */
a:active {
    color: blue;
}
```

### Селектори по атрибут

Селектира елементи със зададени специфични атрибути или стойности

```
/* Селектира контроли от тип text и password */

input[type=text], input[type=password] {
    width: 90%;
}
```

### Приоритети при прилагане на стилизирането

1. !important&#x20;
2. Инлайн стилове&#x20;
3. Стилове, добавени с помощта на селекции по id&#x20;
4. Стилове, добавени с помощта на селекции по клас&#x20;
5. Стилове, добавени с помощта на селекции по таг&#x20;
6. Стилове по подразбиране на браузъра

Повече селектори: [https://www.w3schools.com/cssref/css\_selectors.asp](https://www.w3schools.com/cssref/css\_selectors.asp)