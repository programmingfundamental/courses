---
layout: default
title: Activity Lifecycle
parent: Лабораторно упражнение 3
grand_parent: Програмиране за мобилни и Интернет устройства
nav_order: 1
---
# Activity Lifecycle

Докато потребителят се придвижва, излиза и се връща обратно към приложението, активностите преминават на приложението премибнават през различни състояния в жизнения си цикъл. Activity класът предоставя редица методи за обратно повикване, които позволяват на всяко Activity да се знае, че състоянието се е променило: тоест системата създава, спира или възобновява Activity, или унищожаване на процеса, в който се намира то.

За да навигирате между етапите на жизнения цикъл на Activity, класът Activity предоставя основен набор от шест метода за обратно извикване onCreate(), onStart(), onResume(), onPause(), onStop(), и onDestroy(). Системата извиква всеки от тези методи като дейността влиза в ново състояние.

![Диаграма на жизнения цикъл на Activity](<../../../assets/image (46).png>)

### Методи за обратно извикване

#### - onCreate() <a href="#oncreate" id="oncreate"></a>

Този метод се изпълнява когато системата за първи път създава Activity. След като се изпълни дейноста е в сустояние "_Created_ ". В този метод се поставя логика тоято трябва да се изпълни еднократно при създаване на Activity, примерно инициализиране на обектите в класа на дейността.

#### - onStart() <a href="#onstart" id="onstart"></a>

Този метод се извиква когато дейността стане активна за потребителя и преминава в състояние "Started"

#### **- onResume()** <a href="#onstart" id="onstart"></a>

Метода се изпълнява, когато дейността идва на преден план и състоянието и става "Resumed", Това е състоянието, в което приложението взаимодейства с потребителя. Приложението остава в това състояние, докато друго събитие не предизвика преместване на фокуса от приложението. Такова събитие може да е например получаване на телефонно обаждане, навигация на потребителя до друга дейност или изключване на екрана на устройството.

#### - onPause() <a href="#onpause" id="onpause"></a>

Системата извиква този метод като първата индикация, че потребителят напуска даденото Activity (това не винаги означава, че дейността се унищожава); това показва, че дейността вече не е на преден план (въпреки че все още може да се вижда, ако потребителят е в режим на няколко прозореца). В този метод се поставят инструкции за прекъсване операции, които не трябва да продължат докато приложението не е на преден план.

#### - onStop() <a href="#onstop" id="onstop"></a>

Този метод се изпълнява, когато дейността вече не е видима за потребителя, тя е влязъла в състояние " _Stopped_ ". Това може да се случи, например, когато новосъздадена дейност покрива целия екран. Системата може също да извика този метод, когато активността е на път да бъде прекратена.

#### - onSaveInstanceState() <a href="#save-simple-lightweight-ui-state-using-onsaveinstancestate" id="save-simple-lightweight-ui-state-using-onsaveinstancestate"></a>

Когато дейноста започне да спира системата извиква този метод, за да може да се запазят данни за състоянието на приложението. По подразбиране този метод запазва информацията записана в контролите за въвеждане.

#### - onRestoreInstanceState() <a href="#save-simple-lightweight-ui-state-using-onsaveinstancestate" id="save-simple-lightweight-ui-state-using-onsaveinstancestate"></a>

Когота дейността се създаде отново след като е била унищожена, могат да се възтановят данните който системата прехгвърля от предишното изпълнение на дейността. Метода получава същия обект като onCreate, който съдържа информация за състоянието на Activity записана от метода onSaveInstanceState

Пример:

```
TextView textView;

// Преходно състояние на инстанцията на класа Activity
String gameState;

@Override
public void onCreate(Bundle savedInstanceState) {
    // извиква родителския метод onCreate, за да се завърши създаването на дейността в йерархията на изгледа
    super.onCreate(savedInstanceState);

    // възстановяване състоянието на инстанция
    if (savedInstanceState != null) {
        gameState = savedInstanceState.getString(GAME_STATE_KEY);
    }

    // задаване на оформлението на потребителския интерфейс за това Activity
    // файлът на оформлението е дефиниран в res/layout/main_activity.xml 
    setContentView(R.layout.main_activity);

    // инициализиране на полето TextView за да може да се обработва по късно
    textView = (TextView) findViewById(R.id.text_view);
}

// Този метод се извиква само когато има запазена инстанция, с помощта на
// onSaveInstanceState().
// Тук може да се възтанови състояние, след завършване на onStart().
// Обекта saveInstanceState е същият като този, използван в onCreate(). 
@Override
public void onRestoreInstanceState(Bundle savedInstanceState) {
    textView.setText(savedInstanceState.getString(TEXT_VIEW_KEY));
}

// Извиквасе когато Activity е на път да бъде унищожено
@Override
public void onSaveInstanceState(Bundle outState) {
    outState.putString(GAME_STATE_KEY, gameState);
    outState.putString(TEXT_VIEW_KEY, textView.getText());

    //извиква метода от базовия клас за да запази йерархията на изгледа
    super.onSaveInstanceState(outState);
}
```
