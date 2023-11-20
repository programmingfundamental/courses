---
layout: default
title: Примерна употреба
parent: Лабораторно упражнение 7
grand_parent: Програмиране за мобилни и Интернет устройства
nav_order: 2
---

# Пример

**Задача**

Съставете списък с контакти

[![Annotation 2020-03-25 102631](https://user-images.githubusercontent.com/10382663/77525685-154efc80-6e92-11ea-943b-a3b102627dc3.png)](https://user-images.githubusercontent.com/10382663/77525685-154efc80-6e92-11ea-943b-a3b102627dc3.png)

1. Трябва да се добви зависимост за RecyclerView

```
dependencies {
    ...
    implementation 'androidx.recyclerview:recyclerview:1.1.0'
    ...
}
```

1. Добавяне на RecyclerView в activity\_main.xml

[![Annotation 2020-03-25 102631\_1](https://user-images.githubusercontent.com/10382663/77525582-f2244d00-6e91-11ea-9331-f874e91300b6.png)](https://user-images.githubusercontent.com/10382663/77525582-f2244d00-6e91-11ea-9331-f874e91300b6.png)

1. Създаване на изглед за елементите в RecyclerView

**layout -> new -> XML -> layout XML file**

Поставете две TextView. Направете layout\_height да е wrap\_content виок колкото съдържанието в него.

[![image](https://user-images.githubusercontent.com/10382663/77532853-649b2a00-6e9e-11ea-9a5d-6336133e9e4c.png)](https://user-images.githubusercontent.com/10382663/77532853-649b2a00-6e9e-11ea-9a5d-6336133e9e4c.png)

1. Създаите класове

[![image](https://user-images.githubusercontent.com/10382663/77526083-b50c8a80-6e92-11ea-91d1-8548b62f8cca.png)](https://user-images.githubusercontent.com/10382663/77526083-b50c8a80-6e92-11ea-91d1-8548b62f8cca.png)

* Contact

```
public class Contact {
    private String name;
    private long number;

    public Contact(String name, long number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public long getNumber() {
        return number;
    }
}
```

* ContactSource

```
public class ContactSource {
    public static ArrayList<Contact> generateContactList(int count) {
        ArrayList<Contact> contacts = new ArrayList<Contact>();

        Random random = new Random();

        for (int i = 1; i <= count; i++) {
            contacts.add(new Contact("Person " + i, 1000000000L + random.nextLong() % 9999999999L ));
        }

        return contacts;
    }
}
```

* ContactViewHolder

```
public class ContactViewHolder extends RecyclerView.ViewHolder {
    private TextView name;
    private TextView phone;

    public ContactViewHolder(@NonNull View itemView) {
        super(itemView);

        // Извлича визуалните елементи от изгледа
        name = itemView.findViewById(R.id.textView);
        phone = itemView.findViewById(R.id.textView2);
    }

    public TextView getName() {
        return name;
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public TextView getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone.setText(phone);
    }
}
```

* ContactAdapter

```
public class ContactAdapter extends RecyclerView.Adapter<ContactViewHolder> {

    private List<Contact> contacts;
    private Context context;

    public ContactAdapter(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //извлича контекста където се намира RecyclerView
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Извлича персонализиран изглед
        View contactView = inflater.inflate(R.layout.contact_item, parent, false);

        // Създава viewHolder обект
        ContactViewHolder viewHolder = new ContactViewHolder((contactView));

        return viewHolder;
    }
    // Свързва данните с изгледа
    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        // Взима текущия елемент от колекцията
        Contact contact = contacts.get(position);

        // Поставя стоиност на TextView за име
        holder.setName(contact.getName());
        // Поставя стоиност на TextView за телефон
        holder.setPhone(Long.toString(contact.getNumber()));
        // Поставя слушател за натискане на елемент от списъка
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, contact.getName() + ": " + contact.getNumber(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    //Връща размера на колекцията
    @Override
    public int getItemCount() {
        return contacts.size();
    }
}
```

* Използване в MainActivity

```
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //извлича RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        //Създава адаптер
        ContactAdapter adapter = new ContactAdapter(ContactSource.generateContactList(10));
        // Поставя адаптер на RecyclerView
        recyclerView.setAdapter(adapter);
        // Поставя мениджър на изгледа
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
```
