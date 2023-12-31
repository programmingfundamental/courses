# Получаване на отговор

Прегледът на отговорите на Postman ви помага да визуализирате и проверите коректността на отговорите на API. Отговорът на API се състои от тяло на отговора, хедъри и HTTP код на състоянието.

### **Тяло на отговора**

Разделът Body предоставя няколко изгледа на отговора: Pretty, Raw, Preview и Visualize.

#### **Pretty**

Изгледът Pretty форматира JSON или XML отговорите, така че да са по-лесни за преглед. Хипервръзките в изгледа Pretty са маркирани и избирането им може да зареди GET заявка в Postman с URL адреса на връзката.

![](<../../../../assets/image (29).png>)

#### **Raw**

Суровият изглед е голяма текстова област с тялото на отговора. Може да покаже дали вашият отговор е минифициран.

![](<../../../../assets/image (21).png>)

#### **Preview**

Изгледът за визуализация изобразява отговора в iframe sandbox. Някои уеб framework по подразбиране връщат HTML грешки и Preview може да бъде особено полезен за отстраняване на грешки в тези случаи.

Поради ограниченията на iframe sandbox, JavaScript и изображенията са изключени. За типове двоични отговори можете да изберете „Изпращане и изтегляне“, за да запазите отговора локално. След това можете да го прегледате с помощта на подходящия визуализатор. Това ви дава гъвкавостта да тествате аудио файлове, PDF файлове, zip файлове или всякакви други типове файлове, които API връща.

![](<../../../../assets/image (10).png>)

#### **Visualize**

Изгледът Visualize изобразява данните в отговора на API според кода за визуализация, добавен към тестовете на заявките.

### &#x20;**Бисквитки**

Можете да проверите бисквитките, изпратени от сървъра, в раздела **Cookies**. Записът на бисквитката включва нейното име, стойност, свързаните домейн и път, както и друга информация за бисквитката.

### **Хедъри**

Хедърите се показват като двойки ключ-стойност в раздела **Headers**. Задръжте курсора на мишката над иконата за информация.

### **Мрежова информация**

Postman показва мрежова информация, когато вашият API върне отговор. Задръжте курсора на мишката върху иконата на мрежата ![](<../../../../assets/image (37).png>), за да получите локалния и отдалечения IP адрес за заявката, която сте изпратили.

Когато направите https заявка, иконата на мрежата включва катинар. Когато задържите курсора на мишката върху иконата, мрежовата информация ще покаже повече информация, включително подробности за проверка на сертификата.

![](<../../../../assets/image (71).png>)

### Код на отговора

Postman показва кода на отговора, върнат от API. Задръжте курсора на мишката върху кода за отговора, за да получите кратко описание.

![](<../../../../assets/image (7).png>)

### Време за отговор

Postman автоматично изчислява времето в милисекунди, необходимо на отговора да пристигне от сървъра. Тази информация може да бъде полезна за някои предварителни тестове на производителността. Задръжте курсора на мишката върху времето за реакция за графика с информация колко време е отнело всяко събитие в процеса.

![](<../../../../assets/image (13).png>)

### Размер на отговора

Postman показва приблизителния размер на отговора. Задръжте курсора на мишката над размера на отговора, за да получите разбивка по размери на тялото и заглавието.
