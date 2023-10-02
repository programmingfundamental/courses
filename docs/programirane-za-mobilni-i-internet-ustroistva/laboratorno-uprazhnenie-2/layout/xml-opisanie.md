---
layout: default
title: Layout
parent: Лабораторно упражнение 2
grand_parent: Програмиране за мобилни и Интернет устройства
has_children: true
nav_order: 2
---

# XML описание

Използвайки XML речника на Android, може бързо да се проектират оформления на потребителския интерфейс и елементите на екрана, които те съдържат, по същия начин, по който се създава уеб страници в HTML - с поредица вложени елементи.

Всеки файл с оформление трябва да съдържа точно един коренен елемент, който трябва да бъде обект View или ViewGroup. След като се дефинира основния елемент, може да се добавят допълнителни обекти като дъщерни елементи, за да се изгради постепенно йерархия на View, която определя изгледа. Например,  XML оформление, което използва вертикален LinearLayout за подреждане на TextView и Button:

```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
              android:layout_width="match_parent"
              android:layout_height="match_parent"
              android:orientation="vertical" >
    <TextView android:id="@+id/text"
              android:layout_width="wrap_content"
              android:layout_height="wrap_content"
              android:text="Hello, I am a TextView" />
    <Button android:id="@+id/button"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Hello, I am a Button" />
</LinearLayout>
```

Всички XML оформления на потребителския интерфейс се съхраняват като .xml файл в директорията "res/layout/" на Android проекта.
