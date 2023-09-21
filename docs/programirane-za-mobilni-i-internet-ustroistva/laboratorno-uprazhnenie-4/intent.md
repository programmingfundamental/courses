# Intent

Intent е обект-съобщение, което се използва, за да се извика действие от друг компонент на системата

### Видове:

* Експлицитни интенти - задават кой компонент конкретно искат да се стартира

```
Intent i = new Intent(this, NewActivity.class);
```

* Имплицитни интенти - задават само какво трябва да може компонента, който се стартира

```
Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
```

### Извикване на Activity

```
startActivity(intent);
```

### Предаване на данни към Activity

```
intent.putExtra("objectName", obj);
```

Прочитане на данни

```
 Intent intent = getIntent(); //извлича Intent обекта изпратеn от извикващото Activity в извиканото
```

Има методи на зазличните типове данни на съобщенията, които се изпращат посредством Intent

* getStringExtra()
* getIntExtra()
* getIntArrayExtra()
* и т.н.

```
 intent.getSerializableExtra("objectName"); //извлича изпратен обект като съобщение през Intent
```

**Стартиране на активити за приемане на резултат след прекратяването му startActivityForResult() и onActivityResult обработва резултата в родителското Activity след като е прекратено изпълнението да извиканото**

```
public class MainActivity extends Activity {
     // ...

     static final int PICK_CONTACT_REQUEST = 0;

     public boolean onKeyDown(int keyCode, KeyEvent event) {
         if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
             // След натискане на бутон се стартира Activity за изпор на контакт
             startActivityForResult(
                 new Intent(Intent.ACTION_PICK,
                 new Uri("content://contacts")),
                 PICK_CONTACT_REQUEST);
            return true;
         }
         return false;
     }

     protected void onActivityResult(int requestCode, int resultCode,
             Intent data) {
         if (requestCode == PICK_CONTACT_REQUEST) {
             if (resultCode == RESULT_OK) {
                 // След като е избран контакт тук ще го покаже на екрана
                 startActivity(new Intent(Intent.ACTION_VIEW, data));
             }
         }
     }
 }
```



