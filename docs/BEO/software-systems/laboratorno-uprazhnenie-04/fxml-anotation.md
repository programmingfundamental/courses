---
layout: default
title: Анотиране на полета с @FXML анотация
parent: Лабораторно упражнение 4
grand_parent: Програмни системи
nav_order: 2
---

#### Анотиране на полета с @FXML анотация

fx:id атрибута е уникален идентификатор на UI елемент във FXML, който позволява достъп до компонента от контролера. Името на променливата в контролера трябва да съвпада с fx:id. @FXML анотираните полета, автоматично ще бъдат инизиялизирани.
Пример: 

<table>
<tr>
<td><b>FXML</b></td>
<td><b>Java</b></td>
</tr>

<tr>
<td>

<pre><code class="language-xml">
&lt;TextField fx:id="usernameField"/&gt;
</code></pre>

</td>
<td>

<pre><code class="language-java">
@FXML
private TextField usernameField;
</code></pre>

</td>
</tr>
</table>

#### Анотиране на методи с @FXML анотация

Метод за действие - Event handlers 
- атрибутите onAction, onMouseClicked, и др. свързват FXML събитие с метод в Controller. Метода трябва да съществува в контролера, да е маркиран с @FXML, да е void

<table>
<tr>
<td><b>FXML</b></td>
<td><b>Java</b></td>
</tr>

<tr>
<td>

<pre><code class="language-xml">
&lt;Button text="Вход" onAction="#onLogin"/&gt;
</code></pre>

</td>

<td>

<pre><code class="language-java">
@FXML
private void onLogin() {
    System.out.println("Бутонът е натиснат");
}
</code></pre>

</td>

</tr>
</table>