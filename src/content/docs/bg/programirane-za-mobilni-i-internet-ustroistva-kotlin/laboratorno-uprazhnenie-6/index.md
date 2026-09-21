---
title: Лабораторно упражнение 6
sidebar:
  order: 6
---

# Лабораторно упражнение 6

## Жизнен цикъл на `Activity`

`Activity` е Android компонент, който предоставя прозорец за потребителския интерфейс. Например приложение може да използва `LoginActivity`, `SettingsActivity` или `MainActivity`. Една `Activity` може да представя и повече от един екран чрез Compose.

Системата управлява жизнения цикъл на всеки екземпляр и извиква callback методи при създаване, показване, спиране и унищожаване.

### Основни callback методи

| Метод | Кога се извиква | Типична роля |
| --- | --- | --- |
| `onCreate()` | При създаване на екземпляра, включително след пресъздаване | Инициализация и задаване на съдържанието чрез `setContent`. |
| `onStart()` | При преминаване към видимо състояние | Подготовка на работа, необходима докато `Activity` е видима. |
| `onResume()` | При преминаване към активно състояние за взаимодействие | Възобновяване на работа, свързана с активния интерфейс. |
| `onPause()` | При напускане на активното състояние; `Activity` може още да е видима | Кратки операции за пауза на работа, която изисква активен интерфейс. |
| `onStop()` | Когато `Activity` вече не е видима | Прекратяване на работа, необходима само при видим интерфейс. |
| `onRestart()` | След спиране, когато същият екземпляр се показва отново | След него следва `onStart()`. |
| `onDestroy()` | При унищожаване на екземпляра, например след `finish()` или при определени промени на конфигурацията | Завършване на работа, свързана с този екземпляр, когато callback методът бъде извикан. |

`onDestroy()` не е гарантиран при прекратяване на процеса и не е надеждна точка за запазване на критични данни. [Жизнен цикъл на Activity](https://developer.android.com/guide/components/activities/activity-lifecycle).

### Типични последователности

```text
Създаване:                  onCreate() → onStart() → onResume()
Временно прекъсване:         onPause() → onResume()
Изпращане във фонов режим:   onPause() → onStop()
Връщане на същия екземпляр:  onRestart() → onStart() → onResume()
Приключване чрез finish():  onPause() → onStop() → onDestroy()
```

При пресъздаване старият екземпляр се унищожава, а новият преминава през `onCreate()`, `onStart()` и `onResume()`. Последователностите са за обичайните сценарии; прекратяване на процеса може да прекъсне извикванията.

### Жизнен цикъл на композицията

Жизненият цикъл на `Activity` и този на композицията са различни. Композируема функция влиза в композицията, може да участва в рекомпозиции и по-късно да напусне композицията. Рекомпозицията не означава ново извикване на `Activity.onCreate()`.

`remember` запазва стойност на съответното място в композицията. `LaunchedEffect` е API за странични ефекти (side effects), свързано с композицията: стартира корутина при влизане, рестартира я при промяна на ключовете и я отменя при напускане. То не е общ lifecycle callback на `Activity` и не се отменя автоматично само защото тя е във фонов режим.

## Реактивен подход в Jetpack Compose

При декларативния подход интерфейсът се описва според текущите данни. Compose проследява прочетеното наблюдавано състояние и при промяна планира рекомпозиция на засегнатите части. Рекомпозицията е изпълнение на функции за актуализиране на композицията; тя не е синоним на всяка операция по рисуване на екрана.

### Императивен пример

При View компонент свойствата се променят изрично:

```kotlin
import android.view.View
import android.widget.TextView

fun showGreeting(textView: TextView) {
    textView.text = "Hello"
    textView.visibility = View.VISIBLE
}
```

### Декларативен пример

```kotlin
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun NameGreeting() {
    var name by remember { mutableStateOf("Иван") }
    Text(text = "Здравей, $name!")
}
```

Ако `name` бъде променена от обработваща функция, Compose планира рекомпозиция на кода, който прочита тази стойност. Не се извиква ръчно метод за смяна на текста на View.

### Състояние, `mutableStateOf()` и `remember`

Състоянието (`state`) представя данни, които могат да се променят, например брояч или въведен текст. Обикновена локална променлива не е автоматично Compose състояние.

- `mutableStateOf()` създава наблюдавано състояние. Промяна на неговата `value` може да предизвика рекомпозиция.
- `remember` запазва създадената стойност между рекомпозиции, докато мястото ѝ е в композицията.
- `remember` не осигурява запазване при пресъздаване на `Activity`. За подходящи UI стойности, например `String` и `Int`, може да се използва `rememberSaveable`, което участва в механизма за запазване и възстановяване на UI състоянието.
- Синтаксисът `by` изисква импорти на `getValue` и `setValue` от `androidx.compose.runtime`.

### Реактивен брояч

```kotlin
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CounterScreen() {
    var count by remember { mutableStateOf(0) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Брояч: $count", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { count++ }) {
            Text("Увеличаване")
        }
    }
}
```

Броячът започва от 0. Натискането на бутона увеличава `count`, а прочетената стойност се използва при следващата рекомпозиция. Не се предполага, че всяка промяна веднага рисува отделен кадър.

### Сравнение на подходите

| Характеристика | Императивен подход с Views | Декларативен подход с Compose |
| --- | --- | --- |
| Промяна на UI | Изрична промяна на свойствата на View. | Промяна на наблюдавано състояние и актуализиране на композицията. |
| Описание | Последователност от действия върху съществуващи обекти. | Описание на интерфейса за дадени входни данни. |
| Актуализиране | Зависи от използваните View компоненти и извиканите методи. | Compose планира необходимата рекомпозиция и последващите фази. |

Спирането на `Activity` не означава автоматично премахване на композицията или спиране на всяка операция, която използва състоянието. Работата, зависеща от lifecycle, се управлява изрично според съответния API.

### Състояние като свойство на `Activity`

Състоянието може да се пази и като свойство на екземпляра. Следният фрагмент се поставя в класа `Activity`, с импорти на `mutableStateOf`, `getValue` и `setValue`:

```kotlin
private var lifecycleState by mutableStateOf("")
```

Тук `remember` не е необходим, защото свойството принадлежи на `Activity`, а не е локално в композируема функция. Когато callback метод промени `lifecycleState`, интерфейсът, който я прочита, може да се актуализира. Стойността не се запазва автоматично в нов екземпляр на `Activity`.

## `Toast`

`Toast` е кратко съобщение, което изчезва автоматично и не изисква действие от потребителя.

```kotlin
import android.content.Context
import android.widget.Toast

fun showGreetingToast(context: Context) {
    Toast.makeText(context, "Hello!", Toast.LENGTH_SHORT).show()
}
```

Първият аргумент е контекстът, вторият — текстът, а `Toast.LENGTH_SHORT` или `Toast.LENGTH_LONG` определя продължителността. `show()` показва съобщението.

В callback на `Activity` може да се използва `this`. При наличен `import android.widget.Toast` примерният фрагмент е:

```kotlin
Toast.makeText(this, "Състояние: onStart", Toast.LENGTH_SHORT).show()
```

Бързо следващи събития и ограниченията за работа във фонов режим могат да попречат всички съобщения да се видят. Точната последователност се проследява чрез Logcat.

## Logcat

Logcat показва диагностични съобщения от Android и приложенията: системни събития, съобщения от разработчика, предупреждения и изключения. В Android Studio приложението се стартира чрез **Run**, отваря се **Logcat** и се избира съответното устройство и процес.

### Нива на съобщенията

| Метод | Ниво | Предназначение |
| --- | --- | --- |
| `Log.v()` | VERBOSE | Подробна диагностична информация. |
| `Log.d()` | DEBUG | Съобщения за отстраняване на грешки. |
| `Log.i()` | INFO | Информация за нормалното изпълнение. |
| `Log.w()` | WARN | Предупреждение за възможен проблем. |
| `Log.e()` | ERROR | Информация за грешка. |
| `Log.wtf()` | ASSERT | Сериозно нарушение на очаквано условие; поведението зависи от системната конфигурация. |

Примерните извиквания се поставят в метод на приложението; необходим е `import android.util.Log`:

```kotlin
val tag = "LifecycleDemo"
Log.v(tag, "Подробна информация")
Log.d(tag, "Debug съобщение")
Log.i(tag, "Нормално изпълнение")
Log.w(tag, "Предупреждение")
Log.e(tag, "Грешка")
Log.wtf(tag, "Нарушено критично условие")
```

### Филтриране

Съобщенията се филтрират по пакет, ниво и таг. Например `tag:LifecycleDemo` ограничава резултатите до съобщения с този таг. Така събитията от упражнението се разграничават от останалите системни съобщения.

## Наблюдение на жизнения цикъл

`LifecycleOwner` предоставя обект `Lifecycle`; `ComponentActivity` реализира този интерфейс. `LifecycleOwner` не е заместител на базов клас. `LifecycleObserver` е интерфейс за наблюдатели, а `DefaultLifecycleObserver` предоставя callback методи като `onStart(owner)` и `onStop(owner)`. Наблюдател се регистрира чрез `lifecycle.addObserver(...)`.

`DefaultLifecycleObserver` няма `onRestart()`. Ако това конкретно извикване трябва да бъде записано, то остава callback метод в `Activity`.

Практическите задания и задачите за самостоятелна работа са на страницата [„Задачи“](/courses/bg/programirane-za-mobilni-i-internet-ustroistva-kotlin/laboratorno-uprazhnenie-6/zadacha/).
