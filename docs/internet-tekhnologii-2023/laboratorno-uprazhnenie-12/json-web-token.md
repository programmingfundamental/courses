# JSON Web Token

JSON Web Token (JWT) е отворен стандарт, който дефинира компактен и самостоятелен начин за сигурно предаване на информация между страните под формата на JSON обект. Тази информация може да бъде проверена и надеждна, тъй като е цифрово подписана. JWT могат да бъдат подписани с помощта на secret (с алгоритъма HMAC) или двойка публичен/частен ключ, използвайки RSA или ECDSA.

### Употреба

&#x20;Ето някои сценарии, при които JSON уеб токените са полезни:

·       Authorization: Това е най-честият сценарий за използване на JWT. След като потребителят влезе, всяка следваща заявка ще включва JWT, позволявайки на потребителя достъп до маршрути, услуги и ресурси, които са разрешени с този токен. Единичното влизане е функция, която широко използва JWT в днешно време, поради малкият изискуем ресурс и възможността да се използва лесно в различни домейни.

·       Обмен на информация: JSON уеб токените са добър начин за сигурно предаване на информация между страните. Тъй като JWT могат да бъдат подписани – например с помощта на двойки публичен/частен ключ – можете да сте сигурни, че подателите са тези, за които се представят. Освен това, тъй като подписът се изчислява с помощта на хедъра и полезният товар на съобщението, можете също да проверите дали последното не е било манипулирано.

### Структура на JSON Web Token

В своята компактна форма JSON уеб токените се състоят от три части, разделени с точки (.), които са:

·       Хедър;

·       Payload;

·       Подпис.

Следователно JWT обикновено изглежда по следния начин.

xxxxx.yyyyy.zzzzz

#### Хедър

Хедърът обикновено се състои от две части: типа на токена, който е JWT, и използвания алгоритъм за подписване, като HMAC SHA256 или RSA. Например: &#x20;

```json
{
	"alg": "HS256",
  	"typ": "JWT"
} 
```

Този JSON е Base64Url кодиран, за да формира първата част на JWT.

#### Payload

Втората част от токена е полезният товар, който съдържа claims. Claims са изявления за обект (обикновено потребител) и допълнителни данни. Има три вида claims: регистрирани, публични и частни claims. &#x20;

·       Регистрирани claims: Това са набор от предварително дефинирани claims, които не са задължителни, но се препоръчват, за да предоставят набор от полезни, оперативно съвместими искове. Някои от тях са: iss (издател), exp (време на изтичане), sub (тема), aud (аудитория) и други. Забележете, че имената на искове са дълги само три знака, тъй като JWT е предназначен да бъде компактен.

·       Публични claims: Те могат да бъдат дефинирани по желание от тези, които използват JWT. Но за да се избегнат сблъсъци, те трябва да бъдат дефинирани в IANA JSON Web Token Registry или да бъдат дефинирани като URI, който съдържа устойчиво на сблъсък пространство от имена.

·       Частни claims: Това са персонализирани claims, създадени за споделяне на информация между страни, които са съгласни да ги използват, и не са нито регистрирани, нито публични claims. Примерен payload може да бъде:

```json
{
  "sub": "1234567890",
  "name": "John Doe",
  "admin": true
}
```

След това полезният товар се кодира в Base64Url, за да формира втората част на JSON Web Token. Имайте предвид, че за подписани токени тази информация, макар и защитена срещу подправяне, може да бъде прочетена от всеки. Не поставяйте секретна информация в полезния товар или хедъра на JWT, освен ако не е шифрована.

#### Подпис

За да създадете частта за подпис, трябва да вземете кодирания хедър, кодирания полезен товар, секретен ключ, алгоритъма, посочен в хедъра, и да го подпишете. Например, ако искате да използвате алгоритъма HMAC SHA256, подписът ще бъде създаден по следния начин:

```java
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret)
```

Подписът се използва, за да се потвърди, че съобщението не е променено по пътя, а в случай на токени, подписани с частен ключ, може също така да провери дали подателят на JWT е този, за когото се представя.

### Всичко заедно

Резултатът е три Base64-URL низа, разделени с точки, които могат лесно да се предават в HTML и HTTP среди, като същевременно са по-компактни в сравнение със стандартите, базирани на XML, като SAML. Следващото показва JWT, в който се съдържат показаните по-горе кодирани хедър и полезен товар, подписани със secret.

<figure><img src="../../.gitbook/assets/image (156).png" alt=""><figcaption></figcaption></figure>

### Как работят JSON уеб токените?

При удостоверяване, когато потребителят влезе успешно, използвайки своите идентификационни данни, ще бъде върнат JSON уеб токен. Тъй като токените са идентификационни данни, трябва да се внимава много, за да се предотвратят проблеми със сигурността. По принцип не трябва да съхранявате токени по-дълго от необходимото. Също така не трябва да съхранявате чувствителни данни за сесията в хранилището на браузъра поради липса на сигурност. Всеки път, когато потребителят иска да получи достъп до защитен маршрут или ресурс, потребителският агент трябва да изпрати JWT, обикновено в хедъра Authorization, използвайки **Bearer** schema. Съдържанието на хедъра трябва да изглежда по следния начин:

```
Authorization: Bearer <token>
```

<figure><img src="../../.gitbook/assets/image (158).png" alt=""><figcaption></figcaption></figure>

В определени случаи това може да бъде механизъм за оторизация без запазване на състояние. Защитените маршрути на сървъра ще проверяват за валиден JWT в хедъра Authorization и ако той присъства, на потребителя ще бъде разрешен достъп до защитените ресурси. Имайте предвид, че ако изпращате JWT токени чрез HTTP хедъри, трябва да се опитате да предотвратите увеличаването на техния размер. Някои сървъри не приемат повече от 8 KB в хедърите.