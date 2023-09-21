# Задачи

### Задача 1

Преглеждайки правилотро за еденична отговорност и представения пример. Виждате ли възможни пробойни на това правило в примера в упражнението. Обосновете се какви са те и какво е решението за тяхното преодоляване.

### Задача 2

Съставете следния клас:

```
public class TextManipulator { 
    private String text;
    
    public TextManipulator(String text) {
        this.text = text;
    }
    
    public String getText() {
        return text;
    }
    
    public void appendText(String newText) {
        text = text.concat(newText);
    }
    
    public String findWordAndReplace(String word, String replacementWord) {
        if (text.contains(word)) {
            text = text.replace(word, replacementWord);
        }
        return text;
    }
    
    public String findWordAndDelete(String word) {
        if (text.contains(word)) {
            text = text.replace(word, "");
        }
        return text;
    }
    
    public void printText() {
        System.out.println(text);
    }

    public void printOutEachWordOfText() {
        System.out.println(Arrays.toString(text.split(" ")));
    }

    public void printRangeOfCharacters(int startingIndex, int endIndex) {
        System.out.println(text.substring(startingIndex, endIndex));
    }
}
```

Така създадения клас отговаряли на SRP?

Какво бихте направили, за да подобрите този клас?

Реализирайте решението.

### Задача 3

Как се разделите функционалностите в приложение за доставка на храна, което приема поръчки на храна, изчислява сметката и я доставя на клиентите.&#x20;

От колко класа ще имате нужда?&#x20;

Каква ще е тяхната отговорност?

Кои ще са тези класове?

Реализирайте решението.
