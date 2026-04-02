## 2. Popups

### Tooltip

```java
Button button = new Button("Hover");
Tooltip tooltip = new Tooltip("Help");
button.setTooltip(tooltip);
```

### Popup

```java
Popup popup = new Popup();
Label label = new Label("Message");
popup.getContent().add(label);
popup.show(primaryStage);
```

---
