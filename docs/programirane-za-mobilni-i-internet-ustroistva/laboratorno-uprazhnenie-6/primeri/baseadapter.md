# BaseAdapter

## BaseAdapter

**BaseAdapter**, както подсказва името му, е базовият клас за реализации на конкретни адаптери в Android. Той е абстрактна и следователно не може да бъде пряко използван.

## Използване на BaseAdapter

#### Задача:

```
Да се създаде приложение извеждащо списък 
от градове и пощенски кодове.
```

[![CreateProject](https://user-images.githubusercontent.com/10382663/77051296-be4db100-69d3-11ea-876f-9785a8a70d4e.png)](https://user-images.githubusercontent.com/10382663/77051296-be4db100-69d3-11ea-876f-9785a8a70d4e.png)

При използването на BaseAdapter с ListView трябва да бъде създаде клас, който реализира следните методи:

```
int getCount()
Object getItem(int position)
long getItemId(int position)
View getView(int position, View convertView, ViewGroup parent)
```

Преди да се създаде имплементация на BaseAdapter, трябва да се създаде оформлението за един реда ListView, а също и клас за елементите (данните) в ListView.

### Kлас за данни

[![models](https://user-images.githubusercontent.com/10382663/77051303-bf7ede00-69d3-11ea-952c-a4711856e033.png)](https://user-images.githubusercontent.com/10382663/77051303-bf7ede00-69d3-11ea-952c-a4711856e033.png)

Всеки ред в ListView ще съдържа име на _град_ и _пощенски код_, така че класът ще изглежда:

```
public class City {
    private String name;
    private int PostCode;

    public City(String name, int postCode) {
        this.name = name;
        PostCode = postCode;
    }

    public String getName() {
        return name;
    }

    public int getPostCode() {
        return PostCode;
    }
}
```

### Генератор на данни

```
public class CitySource {
    public static ArrayList<City> generateItemsList() {
        ArrayList<City> cites = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            cites.add(new City("City " + i, 9000 + i));
        }
        return  cites;
    }
}
```

### ListView оформление (layout)

Създава се с десен бутон върху папката **layout -> new -> XML -> layout XML file**

файла ще се казва: **layout\_list\_view\_row\_items.xml**, защото ще съдържа визуалното описние на един ред от ListView. Root Tag ще е: LinearLayout - този таг оказва, че всички визуални елементи в него ще се подреждат в линия

От палитрата с ресурси изберете две TexView и ги провлачете върху изгледа, който създадохте.

[![listView-text](https://user-images.githubusercontent.com/10382663/77053954-c871ae80-69d7-11ea-8add-b6fa7862730d.png)](https://user-images.githubusercontent.com/10382663/77053954-c871ae80-69d7-11ea-8add-b6fa7862730d.png)

### Добавяне на ListView

В аctivity\_мain.xml трябва да се добави ListView от палитрата.

[![listView](https://user-images.githubusercontent.com/10382663/77051302-bf7ede00-69d3-11ea-8ded-385f16890b37.png)](https://user-images.githubusercontent.com/10382663/77051302-bf7ede00-69d3-11ea-8ded-385f16890b37.png)

### Създаване на персонализирана BaseAdapter реализация

1. Създаваме клас който щр бъде адаптера, този клас трябва да съдържа:

```
public class CityAdapter extends BaseAdapter {
    private Context context; //контекст в който ще се използва адаптера
    private ArrayList<City> items; //колекция с данните в адаптера

    //Конструктор за създаване на обект от класа със сетване на променливите в класа
    public CityAdapter(Context context, ArrayList<City> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size(); //връщане броя на елементите в масива
    }

    @Override
    public Object getItem(int position) {
        return items.get(position); //връща елемент от списъка на посочената позиция
    }

    @Override
    public long getItemId(int position) {
        return position; //връща индекса на елемента от списъка
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // ако липсва View обект се попълва layout за всеки елемент в списъка
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.layout_list_view_row_items, parent, false);
        }

        // извличане на текушия елемент от списъка за показване
        City currentItem = (City) getItem(position);

        // извлича TextView за име на град и пощенски код на град
        TextView textViewItemName = convertView.findViewById(R.id.textView);
        TextView textViewItemDescription = convertView.findViewById(R.id.textView2);

        //поставя текста за има не град и пощенски код на град
        textViewItemName.setText(currentItem.getName());
        textViewItemDescription.setText(Integer.toString(currentItem.getPostCode()));

        // връща View което е изгледа на елемента в реда
        return convertView;
    }
}
```

### Използване на персонализиран адаптер

Адаптерът се използва лесно, като се създаде с необходимите параметри и зададете като адаптер на listview.

```
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // създаване на списък с данни
        ArrayList<City> cities = CitySource.generateItemsList(); // функция за получawane списък с елементи

        //Създаване на адаптер за персонален списък
        CityAdapter adapter =  new  CityAdapter(this, cities);

        // извличане ListView
        ListView itemsListView = findViewById(R.id.listView);
        // задаване на адаптер
        itemsListView.setAdapter(adapter);
    }
}
```
