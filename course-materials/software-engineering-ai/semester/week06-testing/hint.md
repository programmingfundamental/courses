# Преподавателски ключ — упражнение 6

## Тест за Microsoft Forms

Всичките 10 въпроса се решават по [упражнение 6](lab06.md), включително примерния Python код и задачите. Числата в казусите са зададени условия. По-специално 0,78 е праг само за въпрос 8, а не число, което студентът трябва да знае от друга тема.

### Ключ и обосновка

| № | Ключ | Основание и разграничение от останалите отговори |
| --- | --- | --- |
| 1 | C | Оценъчните текстове са повлияли на признаците още преди обучението на класификатора. A запазва замърсените тегла, B отново обучава преобразуване върху evaluation, а D проверява само етикетите и поведението при предсказване. Общият pipeline намалява риска от различна обработка при сервиране, но не отменя вече настъпилото изтичане. |
| 2 | B | И двете проверки пресичат граница между компоненти. Зададеният отговор не премахва реалния HTTP обмен (C), но не дава измерване за модела (A). Отделни успешни проверки на две граници не изпълняват потребителския сценарий (D). |
| 3 | D | Минимумите са едновременно за клас и за извадка. За DOCUMENTATION липсват 15 − 14 = 1 и 5 − 4 = 1; другите класове достигат изискването. A разчита на общ сбор и присъствие, B оставя train с 14, а C оставя evaluation с 4 примера DOCUMENTATION. |
| 4 | A | В A се променя само поведението под тест и точно наблюдението на състоянието след заявката разкрива дефекта. В B провалът е по друга причина. В C се променя очакването, а не кодът. В D правилен отговор и неправилен запис могат да съществуват едновременно; тестът остава зелен и при дефекта. |
| 5 | B, E | Готовност за предсказване изисква годен артефакт; A погрешно обучава в заявката. При Y празната версия нарушава договора независимо от HTTP статуса и категорията (C). Стара версия не удостоверява текущия отговор (D); адаптерът обработва отказа без промяна на задачата. |
| 6 | A, D | Допустимата среда за специфичния тест е отделна PostgreSQL база. Повторяемият резултат от H2 не сменя проверявания механизъм (B); тестовият secret не прави работната база тестова (C). Чистото домейн правило не изисква цял контекст. Копирането на работния .env (E) е изрично изключено. |
| 7 | C, E | C връща всички случаи от първоначалната оценъчна извадка. A би избрало подмножеството според резултата на единия алгоритъм, а D би променило еталонните етикети. E запазва договорените класове с равна тежест при macro-F1. В B трудният клас остава изкуствено извън метриката. |
| 8 | A, B | В казуса 0,76 < 0,78. По-добър резултат от baseline не изпълнява абсолютния праг (C); работещият сценарий не доказва качеството на класификацията (D). Предупреждение при успешен тест не спира неприемлив модел. E би внесло оценъчна информация в обучението. |
| 9 | 60 | За BUG precision = 10/10 = 1, recall = 10/15 = 2/3 и F1 = 2 × 1 × (2/3) / (1 + 2/3) = 0,8. За FEATURE F1 = 1; за DOCUMENTATION F1 = 0. OTHER не е четвърти договорен клас. Средното е (0,8 + 1 + 0) / 3 = 0,6, т.е. 60%. Делението на 15 верни предсказания на 30 задачи би дало 50%, но това не е macro-F1. |
| 10 | Train/evaluation overlap | Първото изпълнение спира при празния след strip текст. След изтриването му проверките за класове и за празен текст минават. Нормализирането свежда втория нов текст до „fix login error“. Сечението с обучаващите текстове вече се проверява и поражда точно Train/evaluation overlap. Търси се съобщението от второто изпълнение, без префикса от print. |

### Къде в упражнението се намира отговорът

След всеки указател са дадени дословни откъси от учебния файл. При числовите и последователните казуси таблицата по-горе показва как правилата се прилагат към зададените условия.

#### Въпрос 1

Теория → Проверки на AI компоненти, т. 2 · ред 24:

> Моделът и преобразуването на текста се обучават само върху обучаващите данни.

Теория → Работа в Scrum · ред 9:

> **Data leakage** е използване на информация от оценката при обучение или избор на признаци. **Training-serving skew** е разлика между обработката при обучение и предсказване. Един сериализиран pipeline за текста и модела намалява втория риск.

#### Въпрос 2

Теория → Нива и цели на тестовете, т. 2 · ред 16:

> записване и прочитане на confirmedCategory през JPA; HTTP адаптерът разчита реален JSON от локален тестов сървър.

Същият раздел, т. 3 · ред 17:

> Проверява завършен сценарий през публичния вход.

Теория → Проверки на AI компоненти, т. 1 · ред 23:

> Валиден JSON доказва формата, но не правилността на категорията. Тестов заместител е подходящ за отказ на HTTP услуга, но не може да замени реална оценка на модела.

#### Въпрос 3

Теория → Първи обучен модел · ред 31:

> Използвайте поне 15 обучаващи и 5 оценъчни примера за всеки от BUG, FEATURE и DOCUMENTATION.

Примерен проблем → Решение → validate · ред 129:

> if {label for _, label in rows} != set(LABELS):

#### Въпрос 4

Теория → Нива и цели на тестовете, т. 4 · ред 19:

> Fixture задава началното състояние; изолацията предотвратява зависимост между тестове; регресионният тест пази вече договорено поведение.

Прилагане в Task Manager → Стъпка 4 · ред 88:

> Java тест създава задача, извиква предложението и проверява, че confirmedCategory остава null.

Прилагане в Task Manager → Стъпка 5 · ред 92:

> В работен клон временно добавете записване на предложената категория. Съответният тест трябва да се провали. Възстановете правилното поведение, изпълнете пак и запазете red → green резултатите.

#### Въпрос 5

Прилагане в Task Manager → Стъпка 3 · ред 82:

> Във функцията за HTTP обработка няма `fit`. При липсващ/повреден артефакт readiness е неуспешна и предсказване не се допуска.

Същата стъпка, HTTP адаптер · ред 84:

> Реализирайте HttpCategorySuggester, проверявайте категорията и непразната версия и преобразувайте транспортен/договорен отказ в UNAVAILABLE.

Самостоятелни задачи → Задача 3 · ред 183:

> Проверете, че не се извиква fit по време на request и че отказът оставя потвърдената категория непроменена.

#### Въпрос 6

Прилагане в Task Manager → Стъпка 1 · ред 72:

> За PostgreSQL специфично поведение използвайте отделна тестова PostgreSQL база, никога работната.

Същата стъпка, изпълнение на тестовете · ред 74:

> Unit тестовете не стартират целия Spring контекст. При integration тест задайте тестов JWT secret с необходимата за приложението дължина; не копирайте работния `.env`.

Теория → Нива и цели на тестовете, т. 1 · ред 14:

> DONE → IN_PROGRESS е забранен преход; стратегията по правила дава еднакъв резултат за един и същ текст.

#### Въпрос 7

Примерен проблем → Решение, пояснение след кода · ред 164:

> OTHER от baseline е въздържане и се брои като пропуск за действителния клас.

Теория → Проверки на AI компоненти, т. 3 · ред 25:

> Macro-F1 дава равна тежест на класовете, а support показва броя примери.

Същата точка, пример · ред 26:

> В отчета включваме всички договорени класове.

Самостоятелни задачи → Задача 2 · ред 179:

> Обучете TF-IDF + LogisticRegression или обоснована алтернатива, оценете по ML-01 и сравнете с baseline върху същата извадка.

#### Въпрос 8

Теория → Работа в Scrum · ред 7:

> ML-01 е условие за приемане на обучената версия; ако не е изпълнено, запазвате предишния допустим прираст и записвате нужната работа.

Самостоятелни задачи → Задача 2 · ред 179:

> Един тест трябва действително да отказва при неизпълнен праг.

Прилагане в Task Manager → Стъпка 2 · ред 78:

> Ако ML-01 не е изпълнено, отчетете провала; не подменяйте метриката и не обучавайте върху оценъчната извадка.

Самостоятелни задачи → Задача 3 · ред 183:

> Предайте работещ сценарий за Review, ограниченията на извадката и следващ backlog запис, основан на действителна грешка на модела.

#### Въпрос 9

Теория → Проверки на AI компоненти, т. 3 · ред 25:

> Precision отчита верните положителни сред предсказаните; recall — сред действителните. F1 е хармоничното им средно. Macro-F1 дава равна тежест на класовете, а support показва броя примери.

Примерен проблем → Решение, код · ред 112:

> LABELS = ["BUG", "FEATURE", "DOCUMENTATION"]

Примерен проблем → Решение, пояснение след кода · ред 164:

> OTHER от baseline е въздържане и се брои като пропуск за действителния клас.

#### Въпрос 10

Примерен проблем → Решение → validate, първа проверка · ред 129:

> if {label for _, label in rows} != set(LABELS):

Същата функция, проверка на текста · ред 131:

> if any(not text.strip() for text, _ in rows):

Същата проверка · ред 132:

> raise ValueError("Empty text")

Същата функция, нормализиране · ред 133:

> normalize = lambda text: " ".join(text.lower().split())

Същата функция, проверка на сечението · ред 134:

> if {normalize(t) for t, _ in training} & {normalize(t) for t, _ in held_out}:

Същият клон, съобщение · ред 135:

> raise ValueError("Train/evaluation overlap")

### Импорт

Word файлът е преподавателско копие с ред ANSWER след всеки въпрос. След импорт като Quiz сверете отговорите с ключа: 1–4 са с единичен избор, 5–8 с Multiple answers, 9–10 са кратък Text. Ако ANSWER е видим като част от въпрос или опция, пренесете верния отговор в ключа на Forms и премахнете реда от видимия текст. Не включвайте преподавателското копие или този ключ в сайта или студентски архиви.

## Решение на задача 4. Unit тестове за Task Manager

Първият метод използва фиксирани дати. Вторият проверява двете страни на границата с `@CsvSource`. В последните два метода Mockito подава задача и отчети на реалната услуга. Проверката е върху `Duration`, така че загуба на цяло денонощие би довела до неуспешен тест.

Запишете класа като `src/test/java/bg/tu_varna/sit/task_manager/TaskManagerUnitExamplesTest.java`. Изпълнете командата от условието. Очакваният резултат е `Tests run: 5, Failures: 0, Errors: 0, Skipped: 0`.

```java
package bg.tu_varna.sit.task_manager;

import bg.tu_varna.sit.task_manager.exception.ResourceNotFoundException;
import bg.tu_varna.sit.task_manager.model.dto.request.FilterReportDto;
import bg.tu_varna.sit.task_manager.model.dto.response.TaskResponseDto;
import bg.tu_varna.sit.task_manager.model.entity.Report;
import bg.tu_varna.sit.task_manager.model.entity.Task;
import bg.tu_varna.sit.task_manager.model.validator.AfterMidnightValidator;
import bg.tu_varna.sit.task_manager.model.validator.DateRangeValidator;
import bg.tu_varna.sit.task_manager.repository.ReportRepository;
import bg.tu_varna.sit.task_manager.repository.TaskRepository;
import bg.tu_varna.sit.task_manager.service.ReportServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.modelmapper.ModelMapper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TaskManagerUnitExamplesTest {

    @Test
    void rejectsEndBeforeStart() {
        var start = LocalDateTime.of(2030, 5, 10, 9, 0);
        var filter = new FilterReportDto();
        filter.setFrom(start);
        filter.setTo(start.minusMinutes(1));

        assertFalse(new DateRangeValidator().isValid(filter, null));
    }

    @ParameterizedTest
    @CsvSource({"00:00:00, false", "00:00:01, true"})
    void checksLowerTimeBoundary(LocalTime time, boolean expected) {
        assertEquals(expected, new AfterMidnightValidator().isValid(time, null));
    }

    @Nested
    class WorkedTime {
        private final long taskId = 42L;
        private final ReportRepository reports = mock(ReportRepository.class);
        private final TaskRepository tasks = mock(TaskRepository.class);
        private final ModelMapper mapper = mock(ModelMapper.class);
        private final ReportServiceImp service = new ReportServiceImp(reports, tasks, mapper);

        @BeforeEach
        void givenExistingTask() {
            var task = new Task();
            when(tasks.findById(taskId)).thenReturn(Optional.of(task));
            when(mapper.map(task, TaskResponseDto.class)).thenReturn(new TaskResponseDto());
        }

        @Test
        void addsWorkedTimeBeyond24Hours() throws ResourceNotFoundException {
            when(reports.findByTaskId(taskId)).thenReturn(List.of(
                    reportWithTime(16, 45), reportWithTime(9, 30)));

            var summary = service.getTaskWithTotalWorkedTime(taskId);

            assertEquals(Duration.ofHours(26).plusMinutes(15), summary.getTotalWorkedTime());
        }

        @Test
        void returnsZeroForTaskWithoutReports() throws ResourceNotFoundException {
            when(reports.findByTaskId(taskId)).thenReturn(List.of());

            var summary = service.getTaskWithTotalWorkedTime(taskId);

            assertEquals(Duration.ZERO, summary.getTotalWorkedTime());
        }

        private Report reportWithTime(int hours, int minutes) {
            var report = new Report();
            report.setWorkedTime(LocalTime.of(hours, minutes));
            return report;
        }
    }
}
```

