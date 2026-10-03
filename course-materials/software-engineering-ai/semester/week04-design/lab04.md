# Упражнение 4 — Реализация на AI компонент — модулност и Design Patterns

## Теория

### Работа в Scrum: реализация на първия AI прираст

Работите по AI-01c и AI-01d от началния backlog: предложение по правила и отделно потвърждение. AI инженерът реализира детерминистичния baseline и договора; с backend разработчика свързва CategorySuggester, без да поема съхранението на задачите в модела. За Review демонстрира предложение, непроменена задача и отделно потвърждение. При Retrospective проверява дали споделени fixtures са предотвратили несъответствие. Само работеща стратегия без интеграция още не завършва потребителската история.

**Baseline** е проста отправна точка за сравнение. Правилата показват какъв резултат получаваме без обучение и дават работещ договор на екипа. AI инженерът пази корпуса с гранични примери и правилата за етикетиране, за да сравни по-късно baseline и обучен модел върху едни и същи данни.

### 1. Модулност и слоеве

1. **Свързаност и сцепление.** Един модул обединява близки отговорности и зависи от малко ясни договори. Клас, който валидира HTTP, записва SQL и обучава модел, има твърде много причини за промяна.
2. **Разделяне на отговорностите.** Контролерът преобразува HTTP входа; услугата изпълнява сценария; домейнът проверява правилата; repository съхранява състоянието.
   - **Пример:** забранен преход DONE → IN_PROGRESS се отхвърля от бизнес логиката, независимо кой клиент го е поискал.
3. **DTO и домейн.** DTO е договор с клиента, а entity — модел за съхранение. Автоматичното копиране на всички полета от входа създава риск да се промени поле, което клиентът не управлява.

### 2. Принципи за качествен код

1. **Single Responsibility.** Валидирането на преход и извикването на AI услуга са различни отговорности.
2. **Open/Closed и Dependency Inversion.** Нов категоризатор се добавя чрез реализация на интерфейс, без контролерът да се обвързва с конкретна библиотека.
3. **Liskov Substitution и Interface Segregation.** Всички реализации спазват един договор; интерфейсът за предложение не задължава клиента да знае как се обучава модел.
   - **Пример:** `suggest` никога не записва confirmedCategory, независимо дали използва правила или модел.

### 3. Strategy, Adapter и рефакториране

**Strategy** позволява избор на алгоритъм зад общ интерфейс. **Adapter** превежда външен договор към вътрешен. Dependency injection свързва реализациите; не е нужно да се създава фабрика за всеки клас.

```java
public record Suggestion(String category, String source, String modelVersion) {}

public interface CategorySuggester {
    Suggestion suggest(String summary, String description);
}
```

`RuleBasedCategorySuggester` е учебна стратегия, а бъдещият `HttpCategorySuggester` — адаптер към обучен модел. Примерните правила връщат BUG при „error“, FEATURE при „add“, DOCUMENTATION при „readme“, иначе OTHER. Записваме приоритет на съвпаденията и source=RULES, modelVersion=rules-v1. Това не е машинно обучение и не доказва ML-01.

Рефакторирането променя структурата при запазено договорено поведение. Добавянето на статус е функционално разширение; отделянето на вече работещата проверка за преход в домейн метод е рефакториране. Отчитаме двете отделно.


### Прилагане в Task Manager


Категоризацията трябва да е заменяема, а задачата да има статус и потвърдена категория. Началният Task съдържа id, summary, description, deadline и reports; новите полета и пътища трябва да се реализират.

#### Стъпка 1. Подготовка на прираста

Продължете [архитектурата от тема 3](../week03-architecture/lab03.md). Съпоставете Class и Package моделите със съществуващите пакети. Създайте enum TaskStatus с OPEN, IN_PROGRESS, DONE и отделен тип за категориите. Добавете status с начална стойност OPEN и nullable confirmedCategory.

#### Стъпка 2. Данни и преходи

Подгответе версия на миграция: новите полета, попълване OPEN за съществуващите редове и едва след това NOT NULL за status. Запишете избрания начин за изпълнение на SQL; не разчитайте, че автоматично обновяване на схемата винаги попълва стари данни. Проверявайте преходите по State Machine от упражнение 3. Повторното задаване на същия статус е идемпотентно; останалите недоговорени преходи връщат 409.

#### Стъпка 3. Порт и стратегия

Добавете CategorySuggester и реализацията по правила. Инжектирайте я в услугата за предложения. `POST /tasks/{id}/category-suggestion` чете текста на задачата и връща резултат, без `repository.save`. Използвайте отделен DTO за ръчно потвърждение и отделен за статус.

#### Стъпка 4. API и съвместимост

Реализирайте `PATCH /tasks/{id}/category` и `PATCH /tasks/{id}/status` по SRS. Добавете status и confirmedCategory в TaskResponseDto. Запазете старите полета и пътища. Общ обработчик преобразува липсваща задача в 404, невалидна категория в 400 и забранен преход в 409; не разкрива stack trace. Проверете всички нови пътища с потребителска сесия.

#### Стъпка 5. Проверка и актуализиране на модела

Създайте задача, поискайте предложение, прочетете я и докажете, че confirmedCategory не е променена. Потвърдете категорията и проверете повторно. Изпълнете OPEN → IN_PROGRESS → DONE, после опитайте DONE → IN_PROGRESS и запишете 409 и непроменено състояние. Запазете ръчните сценарии за автоматизиране в упражнение 6. Актуализирайте Class и Sequence моделите според кода.


## Примерен проблем

AI-01c изисква заменяем baseline. При „error“ и „readme“ едновременно екипът получава различни категории. Ще реализираме еднозначен приоритет на правилата, отделен резултат и проверка, че предложението не променя задачата.

### Решение

Запишете `RulesDemo.java`:

```java
import java.util.Locale;

public class RulesDemo {
    record Task(String summary, String description, String confirmedCategory) {}
    record Suggestion(String category, String source, String modelVersion) {}
    interface CategorySuggester {
        Suggestion suggest(String summary, String description);
    }
    static class RuleBasedCategorySuggester implements CategorySuggester {
        public Suggestion suggest(String summary, String description) {
            if (summary == null || description == null) {
                throw new IllegalArgumentException("Missing task text");
            }
            String text = (summary + " " + description).toLowerCase(Locale.ROOT);
            String category = text.contains("error") ? "BUG"
                : text.contains("add") ? "FEATURE"
                : text.contains("readme") ? "DOCUMENTATION" : "OTHER";
            return new Suggestion(category, "RULES", "rules-v1");
        }
    }
    static void check(boolean condition) {
        if (!condition) throw new AssertionError("Contract failed");
    }
    public static void main(String[] args) {
        CategorySuggester rules = new RuleBasedCategorySuggester();
        Task task = new Task("Fix README error", "Correct the broken example", null);
        Suggestion result = rules.suggest(task.summary(), task.description());
        check(result.category().equals("BUG"));
        check(result.source().equals("RULES"));
        check(result.modelVersion().equals("rules-v1"));
        check(task.confirmedCategory() == null);
        check(rules.suggest("Add calendar", "New task view").category().equals("FEATURE"));
        check(rules.suggest("README guide", "Explain usage").category().equals("DOCUMENTATION"));
        check(rules.suggest("Review plans", "Discuss scope").category().equals("OTHER"));
        CategorySuggester replacement = (s, d) -> new Suggestion("DOCUMENTATION", "RULES", "fixture-v1");
        check(replacement.suggest(task.summary(), task.description()).category().equals("DOCUMENTATION"));
        System.out.println("PASS: baseline, priority, replacement, no mutation");
    }
}
```

Решението използва Strategy през интерфейса. Record Task е неизменяем, а стратегията получава само текст; в интеграцията подайте данните от услугата, без repository в категоризатора. Търсенето е по подниз и може да даде грешки като „address“ → FEATURE; това е ограничение на baseline, което се измерва по-късно. Примерът решава категоризирането, а добавянето на полета и API в Task Manager е отделната интеграционна работа по-долу.

### Проверка на резултата

С JDK 17+ изпълнете `javac --release 17 RulesDemo.java`, после `java RulesDemo`. Очаквайте `PASS: baseline, priority, replacement, no mutation`. При промяна на приоритета readme преди error проверката се проваля. Не са нужни Spring или база за този пример.


## Самостоятелни задачи

### Задача 1. Baseline и договор

Като AI инженер реализирайте CategorySuggester и baseline по правила, който покрива трите категории и въздържане OTHER. Запишете приоритетите и версията на правилата. Подгответе поне 12 собствени случая, включително смесени ключови думи, регистър и липсващи данни. Предайте код, очаквани/действителни резултати и ограничения; не представяйте правилата като обучен модел.

### Задача 2. Интегриран прираст

Свържете категоризатора с Task Manager съвместно с backend частта: предложение без запис и отделно потвърждение. Ако работите самостоятелно, изпълнете и минималните промени по API и данните от раздела за интеграция. Докажете, че заменяем категоризатор връща същия договор без промяна на контролера. Предайте собствен diff, актуални Class/Sequence `.mmd` и SVG и сценарий създаване → предложение → прочитане → потвърждение → прочитане.

### Задача 3. Review и гранични случаи

Проверете непозната категория, липсващ ID, предложение при вече потвърдена категория и забранен преход на статус. За всяка проверка покажете непроменените несвързани данни. Актуализирайте Sprint Backlog и DoD според действителното завършване. Предайте кратък протокол за Review с обратна връзка и едно действие за Retrospective, което подобрява сътрудничеството AI/backend.
