---
title: Задача
sidebar:
  order: 2
---

# Задача

Да се създаде приложение, което през зададен от потребителя интервал избира произволен зар и визуализира съответното изображение.

## Ресурси и модел

1. Да се използват изображенията на зарове от следния ресурс: [изображения за задачата](https://tuvarnabg.sharepoint.com/:u:/s/msteams_230e9b/EXtfPyFQ_3tAnBwEYE7-4XgB3w6hd6boqpZEw_RJEj-sgg?e=HW2dfN).
2. Изображенията да се добавят в `app/src/main/res/drawable`.
3. В `app/src/main/res/values/strings.xml` да се добави текстов ресурс за стойността на всеки зар.
4. Да се създаде клас за данни `Dice` с `@StringRes val stringResourceId: Int` и `@DrawableRes val drawableResourceId: Int`. Да се подготви непразен списък `List<Dice>`, който свързва действителните ресурси. Имената на файловете не се предполагат от адреса на ресурса.

## Интервал и управление

5. Да се добави поле за интервал в **цели положителни секунди**. Да се проверяват празна стойност, нечислова стойност и стойност, по-малка или равна на нула. Да се отхвърлят и стойности, които не могат безопасно да се преобразуват в милисекунди.
6. Да се добавят бутони „Старт“ и „Стоп“. „Старт“ да включва повтарящия се процес само след успешна проверка. При работещ процес полето и „Старт“ да са неактивни. „Стоп“ да отменя работата.
7. Да се използва един `LaunchedEffect`, управляван от състоянието `running`. След всяко `delay()` да се избира случаен обект от списъка и да се показват неговите изображение и текст. Повторно натискане на „Старт“ да не създава допълнителни цикли.

## Пример за управлението

Примерът се добавя в Kotlin файл на Compose проекта. `DiceScreen(dice)` се извиква в `setContent` в темата на приложението с подготвения списък от ресурси. Използва се Material 3. Кодът не предполага конкретни имена на изображенията.

```kotlin
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

data class Dice(
    @StringRes val stringResourceId: Int,
    @DrawableRes val drawableResourceId: Int
)

fun intervalMillisOrNull(input: String): Long? {
    val seconds = input.trim().toLongOrNull() ?: return null
    if (seconds <= 0 || seconds > Long.MAX_VALUE / 1000L) return null
    return seconds * 1000L
}

@Composable
fun DiceScreen(dice: List<Dice>, modifier: Modifier = Modifier) {
    var intervalText by rememberSaveable { mutableStateOf("1") }
    var intervalMillis by remember { mutableStateOf(1000L) }
    var running by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var currentDice by remember(dice) { mutableStateOf(dice.firstOrNull()) }

    LaunchedEffect(running, intervalMillis, dice) {
        if (running && dice.isNotEmpty()) {
            while (isActive) {
                delay(intervalMillis)
                currentDice = dice.random()
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize().safeDrawingPadding().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = intervalText,
            onValueChange = { intervalText = it; error = null },
            label = { Text("Интервал в секунди") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            enabled = !running,
            isError = error != null,
            supportingText = { error?.let { Text(it) } }
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                enabled = !running && dice.isNotEmpty(),
                onClick = {
                    val parsed = intervalMillisOrNull(intervalText)
                    if (parsed == null) {
                        error = "Необходим е валиден положителен брой секунди."
                    } else {
                        error = null
                        intervalMillis = parsed
                        running = true
                    }
                }
            ) { Text("Старт") }
            Button(enabled = running, onClick = { running = false }) {
                Text("Стоп")
            }
        }
        currentDice?.let { selected ->
            val description = stringResource(selected.stringResourceId)
            Image(
                painter = painterResource(selected.drawableResourceId),
                contentDescription = description,
                modifier = Modifier.size(180.dp),
                contentScale = ContentScale.Fit
            )
            Text(description)
        }
        if (dice.isEmpty()) {
            Text("Липсват ресурси за заровете.")
        }
    }
}
```

При промяна на ключа `running` предишната корутина на `LaunchedEffect` се отменя. При `false` не се стартира нов цикъл. Напускането на композицията също отменя работата. Самото изпращане на `Activity` във фонов режим не означава непременно напускане на композицията.

Първоначално се показва първият зар от списъка, а след „Старт“ случайният избор се извършва след всеки зададен интервал. Интервалът е приблизителен и зависи от планирането на изпълнението; `delay()` не е точен часовник.

## Проверка

Да се проверят празен вход, текст вместо число, нула, отрицателна стойност и прекалено голямо число. При грешка цикълът не трябва да започва. След валиден старт да се провери промяната на изображението, неактивният бутон „Старт“, спирането чрез „Стоп“ и последващо стартиране с друг интервал. Случайният избор може да повтори предишния зар.
