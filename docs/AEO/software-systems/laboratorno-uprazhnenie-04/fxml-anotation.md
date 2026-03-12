---
layout: default
title:  FXML anotation
parent: Laboratory Exercise 4
grand_parent: Software Systems
nav_order: 2
---

## Annotating fields with the @FXML annotation.

The fx:id attribute is a unique identifier of a UI element in FXML that allows the component to be accessed from the controller. The variable name in the controller must match the fx:id. Fields annotated with @FXML will be automatically initialized.

Example: 

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

#### Annotating methods with the @FXML annotation.

Action method – Event handlers

The attributes onAction, onMouseClicked, etc., connect an FXML event to a method in the Controller. The method must exist in the controller, be annotated with @FXML, and have a void return type.

<table>
<tr>
<td><b>FXML</b></td>
<td><b>Java</b></td>
</tr>

<tr>
<td>

<pre><code class="language-xml">
&lt;Button text="Login" onAction="#onLogin"/&gt;
</code></pre>

</td>

<td>

<pre><code class="language-java">
@FXML
private void onLogin() {
    System.out.println("The button is pressed.");
}
</code></pre>

</td>

</tr>
</table>