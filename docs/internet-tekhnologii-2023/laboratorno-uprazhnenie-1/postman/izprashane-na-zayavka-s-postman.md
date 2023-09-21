# Изпращане на заявка с Postman

Можете да изпращате заявки в Postman за свързване с API, с които работите. Вашите заявки могат да извличат, добавят, изтриват и актуализират данни.  Могат да бъдат изпращани параметри, подробности за оторизация и всякакви основни данни, които изисквате.

Когато изпратите заявка, Postman показва отговора, получен от API сървъра, по начин, който ви позволява да го прегледате, визуализирате и, ако е необходимо, да отстраните проблема.

![](<../../../.gitbook/assets/image (6).png>)

## Създаване на заявки

Можете да създадете нова заявка от началния екран на Postman, като използвате New > HTTP Request или като изберете +, за да отворите нов раздел.

![](<../../../.gitbook/assets/image (31).png>)

Изберете Save, за да създадете вашата заявка. Можете да дадете на заявката си име и описание и да изберете или създадете колекция, в която да я запазите.

След като новият ви раздел е отворен, можете да посочите детайлите, от които се нуждаете за вашата заявка.

<figure><img src="../../../.gitbook/assets/image (57).png" alt=""><figcaption></figcaption></figure>

### Добавяне на детайли на заявката

Ако имате заявка, която искате да изпълните, е необходимо да знаете URL адреса, метода и други незадължителни детайли като параметри и данни за удостоверяване.

За първоначално тестване изпращането на заявка в Postman, можете да зададете URL адреса на примерната крайна точка на API на Postman Echo https://postman-echo.com/get и метода GET, след което изберете Send.

**Задаване на URL на заявката**

Всяка заявка, която изпращате в Postman, изисква URL адрес, представляващ крайната точка на API, с която работите.

Всяка операция, която можете да извършите с помощта на API, обикновено е свързана с крайна точка. Всяка крайна точка в API е достъпна на определен URL адрес. Това е, което въвеждате в Postman за достъп до API.

Postman автоматично ще добави http:// в началото на Вашия URL адрес, ако не посочите протокол.

По желание можете да въведете параметри на заявката в полето за URL адрес или можете да ги въведете в раздела Params. Ако вашата заявка използва параметри на пътя, можете да ги въведете директно в полето за URL адрес.

**Избор на метод на заявката**

По подразбиране Postman ще избере методът GET за нова заявка. Можете да използвате различни други методи за изпращане на данни към вашите API.

<img src="../../../.gitbook/assets/image (64).png" alt="" data-size="original">

**Изпращане на параметри**

Можете да изпращате path и query параметри, като използвате полето URL и раздела Params.

Query параметрите се добавят в края на URL адреса на заявката, след ?, изброени в двойки ключ-стойност и разделени от & чрез следния синтаксис:

?id=1\&type=new

Path параметрите формират част от URL адреса на заявката и се посочват с помощта на контейнери, предхождани от :, както в следния пример: /customer/:id

За да изпратите query параметър, добавете го директно към URL адреса или отворете Params и въведете името и стойността. Когато въведете параметрите на вашата заявка в полетата URL или Params, тези стойности ще се актуализират навсякъде, където се използват в Postman.

За да изпратите path параметър, въведете името на параметъра в полето URL след двоеточие, например :id. Когато въведете параметър на пътя, Postman ще го попълни в раздела Params, където можете също да го редактирате.

<figure><img src="../../../.gitbook/assets/image (28).png" alt=""><figcaption></figcaption></figure>

**Изпращане на параметри посредством тялото на заявката**

Изпращанe на параметри посредством тялото на заявката се налага в случаите, когато трябва да добавите или актуализирате структурирани данни с PUT, POST или PATCH заявки. За целта е предвиден разделът Body в Postman.

Изберете типа данни, от който се нуждаете за тялото на вашата заявка – form data, URL-encoded, raw, binary или GraphQL. По подразбиране Postman ще избере None — оставете го избрано, ако не е необходимо да изпращате тяло със заявката си.

* **Form data**

Формите на уебсайтове често изпращат данни към API като `multipart/form-data`. Можете да повторите това в Postman, като използвате опцията `form-data`. Позволява изпращането на двойки ключ-стойност и указване на типа на съдържанието.

![](<../../../.gitbook/assets/image (63).png>)

* **URL-encoded**

URL кодираните данни използват същото кодиране като URL параметрите. Ако вашият API изисква url-encoded данни, изберете x-www-form-urlencoded в раздела Body. Въведете своите двойки ключ-стойност, които да изпратите със заявката и Postman ще ги кодира преди изпращане.

* **Raw data**

С помощта на тази опция можете да изпращате вашите данни в суров вид под формата на текст. Използвайте отметката raw и тип от падащия списък, за да посочите формата на вашите данни (**Text**, **JavaScript**, **JSON**, **HTML** или **XML**) и Postman ще активира подчертаване на синтаксиса, както и ще се погрижи за добавяне на съответните хедъри към вашата заявка.

![](<../../../.gitbook/assets/image (14).png>)

* **Binary data**

Можете да използвате двоични данни, за да изпратите информация, която не можете да въведете ръчно в редактора на Postman с тялото на вашата заявка, като изображения, аудио и видео файлове (можете също да изпращате текстови файлове).

* **GraphQL**

Можете да изпращате GraphQL заявки с помощта на опцията GraphQL.

**Заявки, изискващи автентикация**

Някои API изискват данни за удостоверяване. Автентикацията включва потвърждаване на самоличността на клиента, изпращащ заявка, а оторизацията включва потвърждение, че клиентът има разрешение да извърши операцията на крайната точка. Конфигурирането на данните за достъп се извършва в раздела Authorization.

![](<../../../.gitbook/assets/image (33).png>)

**Конфигуриране на хедърите на заявката**

Някои API изискват да изпращането на конкретни хедъри заедно със заявките, обикновено за предоставяне на повече метаданни за операцията, която извършвате. Можете да ги настроите в раздела Headers. Въведете всички двойки ключ-стойност, от които се нуждаете, и Postman ще ги изпрати заедно с вашата заявка. Докато въвеждате текст, Postman ви подканва с общи опции, които можете да използвате, за да завършите автоматично настройката си, като Content-Type.

![](<../../../.gitbook/assets/image (69).png>)

Postman автоматично добавя определени хедъри към вашите заявки въз основа на избора и настройките на вашата заявка. Те могат да бъдат скрити или показани при избор на алтернативите Hidden/Hide auto-generated headers.

**Използване на бисквитки**

Postman позволява използването на бисквитки при изпращането на заявка. За целта изберете **Cookies (намира се под бутона Send).**

![](<../../../.gitbook/assets/image (22).png>)