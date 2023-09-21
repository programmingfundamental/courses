# Пример

#### Задача

Направете списък с пациентите на ветеринарен лекар.

1. Дефинираме клас Animal един пациент

```
public class Animal {
    private String name;
    private String type;
    private String imageUrl;

    public Animal(String name, String type, String imageUrl) {
        this.name = name;
        this.type = type;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
```

1. Дефинираме клас с предварителна дефинирана колекция от Animal

```
public class AnimalSource {

    ArrayList<Animal> animals;

    public AnimalSource() {
        animals = new ArrayList<Animal>() {{
            add(new Animal("Charly", "Dog", "https://www.brandeps.com/icon-download/D/Dog-icon-vector-01.svg"));
            add(new Animal("Sara", "Cat", "https://www.brandeps.com/icon-download/C/Cat-icon-vector-01.svg"));
        }};
    }

    public ArrayList<Animal> getAnimals() {
        return animals;
    }
}
```

1. Създаваме animal\_item.xml файл с изгледа за 1 ред в RecyclerView

**layout->New->XML->Layout XML file**

Добавяме CardView с 1 ImageView и 2 TextView

1. Създаваме AnimalViewHorder клас за оформлението animal\_item.xml

```
public class AnimalViewHolder extends RecyclerView.ViewHolder {

    private ImageView image;
    private TextView name;
    private TextView type;

    public AnimalViewHolder(@NonNull View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.imageView);
        name = itemView.findViewById(R.id.textView3);
        type = itemView.findViewById(R.id.textView4);
    }

    public void setImage(String url) {
       Picasso.get().load(url).error(R.drawable.farm_animals_vector_cartoons).into(image);
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public void setType(String type) {
        this.type.setText(type);
    }
}
```

1. Добавяне на RecycleView в activity\_main.xml и създаване на Adapter

```
public class AnimalAdapter extends RecyclerView.Adapter<AnimalViewHolder> {
    private ArrayList<Animal> animals;

    public AnimalAdapter(ArrayList<Animal> animals) {
        this.animals = animals;
    }

    @NonNull
    @Override
    public AnimalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View canimalView = inflater.inflate(R.layout.animal_item, parent, false);

        return new AnimalViewHolder(canimalView);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalViewHolder holder, int position) {

        Animal animal = animals.get(position);

        holder.setName(animal.getName());
        holder.setType(animal.getType());
        holder.setImage(animal.getImageUrl());
    }

    @Override
    public int getItemCount() {
        return animals.size();
    }

    public void addAnimal(Animal animal) {
        animals.add(0, animal);
        notifyItemChanged(0);
    }
}
```

1. Създаване на Fragment - AddAnimalFragment
   * Десен бутон **app -> New -> Fragment -> Fragment (Blank)**
2. Създаване на интерфейс за прехвърляне на данни от Activity към Frqgment

```
public interface AddAnimalDialogListener {
    void onFinishAddDialog(Animal animal);
}
```

1. Имплементиране на AddAnimalFragment

```
public class AddAnimalFragment extends DialogFragment implements View.OnClickListener {

    private EditText name;
    private EditText type;
    private EditText image;
    private Button add;
    AddAnimalDialogListener listener;

    public AddAnimalFragment() {
        // Required empty public constructor
    }

    public static AddAnimalFragment newInstance() {
        return new AddAnimalFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_animal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        name = view.findViewById(R.id.editText);
        type = view.findViewById(R.id.editText2);
        image = view.findViewById(R.id.editText3);
        add = view.findViewById(R.id.button);
        add.setOnClickListener(this);

        // Фокуситане на курсора за писане върху еditText
        name.requestFocus();
        // Автоматично показване на клавиятурата
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (AddAnimalDialogListener)context;
    }

    @Override
    public void onClick(View v) {
        Animal animal = new Animal(name.getText().toString(), type.getText().toString(), image.getText().toString());

        listener.onFinishAddDialog(animal);

        dismiss();
    }
}
```

1. Използване на RecycleView и отваряне на DialogFrgment

```
public class MainActivity extends AppCompatActivity implements AddAnimalDialogListener {

    RecyclerView recyclerView;
    AnimalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        AnimalSource animals = new AnimalSource();

        adapter = new AnimalAdapter(animals.getAnimals());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                AddAnimalFragment addAnimalFragment = AddAnimalFragment.newInstance();
                addAnimalFragment.show(fm, "fragment_add_animal");
            }
        });
    }

    @Override
    public void onFinishAddDialog(Animal animal) {
        adapter.addAnimal(animal);
    }
}
```
