---
layout: default
title: DialogFragment
parent: Лабораторно упражнение 6
grand_parent: Програмиране за мобилни и Интернет устройства
nav_order: 2
---
# DialogFragment

DialogFragment е фрагмент, използван за показване нс модален прозорец в рамките на Activity, която стой над останалата част от съдържанието.

Всеки java клас на фрагмент, импортиран да използва пространството на имената, а пространството на имената. Ако всички внесени клас (FragmentManager, DialogFragment и т.н.) използва пространство на имената, ще възникнат грешки за компилиране-време.androidx.fragment.appandroid.appandroid.app

1. XML файл за оформлението на диалоговия прозорец. Фрагмент може да се създаде с десен бутон на **app -> New -> Fragment -> Fragment (Blank)**

[![image](https://user-images.githubusercontent.com/10382663/78098928-167bae80-73e9-11ea-9ff2-56f09a1e8855.png)](https://user-images.githubusercontent.com/10382663/78098928-167bae80-73e9-11ea-9ff2-56f09a1e8855.png)

2\. Свързвания Java класа трябва да наследи базовия клас DialogFragment.

```
public class EditNameDialogFragment extends DialogFragment {

	private EditText еditText;

    // Задължително е да има празен конструктор
	public EditNameDialogFragment() { }
	
    // Метод за създаване на EditNameDialogFragment обект с данни
	public static EditNameDialogFragment newInstance(String title) {
		EditNameDialogFragment frag = new EditNameDialogFragment();
		Bundle args = new Bundle();
		args.putString("title", title);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_edit_name, container);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// Извличане на елементите от изгледа
		еditText = view.findViewById(R.id.editText);
		// Поставяне на заглавие
		String title = getArguments().getString("title", "Enter Name");
		getDialog().setTitle(title);
		// Фокуситане на курсора за писане върху еditText
		еditText.requestFocus();
        // Автоматично показване на клавиятурата
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	}
}
```

3\. Показване на диалоговия прозорец

```
public class MainActivity extends AppCompatActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      
    FragmentManager fm = getSupportFragmentManager();
    EditNameDialogFragment editNameDialogFragment = EditNameDialogFragment.newInstance("Title");
    editNameDialogFragment.show(fm, "fragment_edit_name");
  }
}
```

4\. Предаване на данни към Activity

За предаване на данни от Fragment към Activity, се създава персонализиран слушател:

* Създава се интерфейс с методи, които могат да се извикат за предаване на резултата от Fragment към Activity
* Имплементиране на интерфейс слушател от Fragment класа и поставяне на дайствие за визуалния обект, след което да се изпрати резултат към Activity
* Имплементиране на интерфейса в Activity към което ще се предават данните.
