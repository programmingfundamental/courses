---
layout: default
title: Списък на стилове (CSS) в JavaFX
parent: Лабораторно упражнение 6
grand_parent: Програмни системи
nav_order: 1
permalink: /docs/BEO/software-systems/laboratorno-uprazhnenie-6/css-stilove
---

### 1. Фон и запълване (Backgrounds & Fills)

Тези свойства определят как изглежда „тялото“ на графичния елемент.

- `-fx-background-color`: Задава цвета на фона. Може да приема имена на цветове (`red`), Hex кодове (`#FFFFFF`), RGB (`rgb(255,0,0)`) или градиенти (`linear-gradient`).

  ```css
  .my-button {
    -fx-background-color: #ff0000;
  }
  ```

  ```css
  .my-pane {
    -fx-background-color: linear-gradient(to bottom, #ff0000, #0000ff);
  }
  ```

  ```css
  .my-label {
    -fx-background-color: derive(green, 20%);
  }
  ```

- `-fx-background-radius`: Определя заоблянето на ъглите на фона. Приема 1 стойност (за всички ъгли) или 4 стойности (Горе-ляво, Горе-дясно, Долу-дясно, Долу-ляво).

  ```css
  .my-button {
    -fx-background-radius: 10;
  }
  ```

  ```css
  .my-pane {
    -fx-background-radius: 10 0 0 10;
  }
  ```

- `-fx-background-insets`: Определя отстоянието на фона от границите на елемента. Често се използва за създаване на „вложени“ цветове или ефект на рамка.

  ```css
  .my-button {
    -fx-background-color: red, white;
    -fx-background-insets: 0, 2;
    -fx-background-radius: 10, 8;
  }
  ```

- `-fx-fill`: Използва се специално за фигури (Shapes) като `Circle`, `Rectangle` или за цвета на обекта `Text`.

  ```css
  .my-circle {
    -fx-fill: blue;
  }
  ```

### 2. Граници и рамки (Borders)

Контролират външната линия около контейнера или контролата.

- `-fx-border-color`: Задава цвета на рамката.

  ```css
  .my-pane {
    -fx-border-color: black;
  }
  ```

- `-fx-border-width`: Задава дебелината на рамката. Може да се задава индивидуално: `-fx-border-width: 1 0 1 1;` (Горе, Дясно, Долу, Ляво).

  ```css
  .my-pane {
    -fx-border-width: 2;
  }
  ```

  ```css
  .my-pane {
    -fx-border-width: 2 0 2 2;
  }
  ```

- `-fx-border-radius`: Определя заоблянето на самата рамка. Обикновено трябва да съвпада с `-fx-background-radius`.

  ```css
  .my-button {
    -fx-border-radius: 10;
  }
  ```

  ```css
  .my-pane {
    -fx-border-radius: 10 0 0 10;
  }
  ```

- `-fx-border-style`: Определя стила на линията – `solid` (плътна), `dashed` (пунктирана), `dotted` (на точки).

  ```css
  .my-pane {
    -fx-border-style: dashed;
  }
  ```

- `-fx-stroke`: Използва се вместо `-fx-border-color` при фигури (Shapes) като `Circle` или `Line`.

  ```css
  .my-circle {
    -fx-stroke: red;
  }
  ```

- `-fx-stroke-width`: Дебелина на линията при фигури.

  ```css
  .my-circle {
    -fx-stroke-width: 3;
  }
  ```

### 3. Текст и шрифтове (Text & Fonts)

Прилагат се към контроли, които съдържат текст (`Label`, `Button`, `TextField`).

- `-fx-text-fill`: Задава цвета на текста в контролите (еквивалент на `color` в уеб).

  ```css
  .my-label {
    -fx-text-fill: white;
  }
  ```

- `-fx-font-family`: Избор на шрифт (напр. `"Arial"`, `"System"`, `"Verdana"`).

  ```css
  .my-label {
    -fx-font-family: 'Arial';
  }
  ```

- `-fx-font-size`: Размер на шрифта (напр. `14px`, `1.2em`).

  ```css
  .my-label {
    -fx-font-size: 16px;
  }
  ```

- `-fx-font-weight`: Дебелина на шрифта (`bold`, `normal`, `100` до `900`).

  ```css
  .my-label {
    -fx-font-weight: bold;
  }
  ```

- `-fx-font-style`: Стил на шрифта (`italic`, `normal`).

  ```css
  .my-label {
    -fx-font-style: italic;
  }
  ```

- `-fx-alignment`: Подравняване на съдържанието вътре в елемента (`center`, `center-left`, `top-right`).

  ```css
  .my-button {
    -fx-alignment: center;
  }
  ```

### 4. Размери и отстояния (Sizing & Spacing)

Управляват разположението на елемента и пространството в него.

- `-fx-padding`: Вътрешно отстояние между съдържанието и границата на елемента.

  ```css
  .my-button {
    -fx-padding: 10;
  }
  ```

  ```css
  .my-button {
    -fx-padding: 10 20; /* Горен/долен, Ляв/десен */
  }
  ```

  ```css
  .my-button {
    -fx-padding: 10 20 5 15; /* Горен, Десен, Долен, Ляв */
  }
  ```

- `-fx-pref-width` / `-fx-pref-height`: Предпочитана ширина и височина.

  ```css
  .my-button {
    -fx-pref-width: 100;
    -fx-pref-height: 40;
  }
  ```

- `-fx-min-width` / `-fx-max-width`: Ограничения за минимална и максимална ширина.

  ```css
  .my-button {
    -fx-min-width: 80;
    -fx-max-width: 150;
  }
  ```

- `-fx-opacity`: Прозрачност на целия елемент (от `0.0` – напълно прозрачен, до `1.0` – плътен).

  ```css
  .my-pane {
    -fx-opacity: 0.5;
  }
  ```

### 5. Ефекти и интерактивност (Effects & Interactivity)

Визуални подобрения и поведение.

- `-fx-effect`: Прилагане на графични ефекти. Най-чести:
  - `dropshadow(тип, цвят, радиус, разсейване, x-от отместване, y-от отместване)` – външна сянка.

    ```css
    .my-button {
      -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 10, 0, 5, 5);
    }
    ```

  - `inner-shadow(...)` – вътрешна сянка.

    ```css
    .my-button {
      -fx-effect: inner-shadow(gaussian, rgba(0, 0, 0, 0.5), 10, 0, 5, 5);
    }
    ```

- `-fx-cursor`: Променя вида на курсора при посочване (`hand` - ръчичка, `default` - стрелка, `wait` - зареждане).

  ```css
  .my-button {
    -fx-cursor: hand;
  }
  ```

- `-fx-visibility`: Определя дали елементът е видим (`visible`, `hidden`).

  ```css
  .my-pane {
    -fx-visibility: hidden;
  }
  ```

### 6. Псевдо-класове (Състояния)

Използват се за динамична промяна на стила при действие от потребителя.

- `:hover`: Стил, когато мишката е върху елемента.

  ```css
  .my-button:hover {
    -fx-background-color: #00ff00;
  }
  ```

- `:pressed`: Стил, докато елементът е натиснат.

  ```css
  .my-button:pressed {
    -fx-background-color: #0000ff;
  }
  ```

- `:focused`: Стил, когато елементът е на фокус (напр. избрано текстово поле).

  ```css
  .my-textfield:focused {
    -fx-border-color: blue;
  }
  ```

- `:disabled`: Стил, когато контролата е неактивна (`setDisable(true)`).

  ```css
  .my-button:disabled {
    -fx-background-color: #cccccc;
    -fx-text-fill: #666666;
  }
  ```

### 7. Цветове

JavaFX поддържа различни начини за задаване на цветове:

- Имена на цветове: `red`, `blue`, `green`, `black`, `white` и др.
- Hex кодове: `#RRGGBB` (напр. `#ff0000` за червено).
- RGBA: `rgba(255, 0, 0, 0.5)` за полупрозрачно червено.
- HSL: `hsl(120, 100%, 50%)` за ярко зелено.
- `derive(color, percentage)`: Позволява да се създаде по-светъл или по-тъмен нюанс на даден цвят. (напр. `derive(red, 20%)` за по-светло червено)
