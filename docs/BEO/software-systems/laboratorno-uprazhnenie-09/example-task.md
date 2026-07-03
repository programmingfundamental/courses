---
layout: default
title: Примерна задача
parent: Лабораторно упражнение 9
grand_parent: Програмни системи
nav_order: 2
---

Примерна задача

Да се разшири приложението за управление на данни за софтуерни разработчици, така че да поддържа работа с база от данни и редактиране информацията на софтуерен разработчик.

Приложението трябва да реализира следните функционалности:

Създаване на база данни за съхранение информацията за разработчиците. 

- Записване на нов разработчик
- Визуализация на всички разработчици
- Изтриване на съществуващ разработчик
- Редактиране на съществуващ разработчик

За редайтирането трябва да се добави: 
1. бутон в menu и context menu команда „Редакция“
2. нов edit-developer-dialog.fxml
3. нов controller за този прозорец
4. логика в основния DeveloperController, която:
- взима избрания програмист
- отваря диалога
- зарежда текущите му данни
- записва промените

Стъпка 1: Доцавяне на библиотека за работа с базата данни:

```xml
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>2.4.240</version>
        </dependency>
```

Стъпка 2: Създаване връзката към базата данни:

```java
public class DatabaseConnection {

    private static final String URL = "jdbc:h2:./data/developersdb";
    private static final String USER = "javafx";
    private static final String PASSWORD = "fxjava";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
```

Стъпка 3: Създаване на базата данни:

```java
public class DatabaseInitializer {
    public static void initDatabase() {
        String sql = """
                CREATE TABLE IF NOT EXISTS developers (
                    id IDENTITY PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    level VARCHAR(50) NOT NULL,
                    skills VARCHAR(500) NOT NULL
                )
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

Стъпка 4: Имплементиране на методите за селектиране, записване и изтриване от базата:

```java
public class DeveloperDAO {
    public List<Developer> findAll() {
        List<Developer> developers = new ArrayList<>();

        String sql = "SELECT id, name, level, skills FROM developers";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                Long id = rs.getLong("id");
                String name = rs.getString("name");
                Level level = Level.valueOf(rs.getString("level"));
                List<Technology> skills = parseSkills(rs.getString("skills"));

                developers.add(new Developer(id, name, level, skills));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Грешка при зареждане на разработчиците.", e);
        }

        return developers;
    }

    public Developer save(Developer developer) {
        String sql = "INSERT INTO developers(name, level, skills) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, developer.getName());
            ps.setString(2, developer.getLevel().name());
            ps.setString(3, skillsToString(developer.getSkills()));

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    developer.setId(rs.getLong(1));
                }
            }

            return developer;
        } catch (SQLException e) {
            throw new RuntimeException("Грешка при запис на разработчик.", e);
        }
    }

    public Developer update(Developer developer) {
        String sql = "UPDATE developers SET name = ?, level = ?, skills = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, developer.getName());
            ps.setString(2, developer.getLevel().name());
            ps.setString(3, skillsToString(developer.getSkills()));
            ps.setLong(4, developer.getId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Няма намерен разработчик за обновяване.");
            }

            return developer;

        } catch (SQLException e) {
            throw new RuntimeException("Грешка при обновяване на разработчик.", e);
        }
    }
    
    public void deleteById(Long id) {
        String sql = "DELETE FROM developers WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Грешка при изтриване на разработчик.", e);
        }
    }

    private String skillsToString(List<Technology> skills) {
        return skills.stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));
    }

    private List<Technology> parseSkills(String skillsText) {
        if (skillsText == null || skillsText.isBlank()) {
            return new ArrayList<>();
        }

        return Arrays.stream(skillsText.split(","))
                .map(String::trim)
                .map(Technology::valueOf)
                .collect(Collectors.toList());
    }
}
```

Стъпка 5: Актуализиране на DeveloperController:

```java
    private void loadDevelopersFromDatabase() {
        developersData.clear();
        developersData.addAll(developerDAO.findAll());
    }
```

```java
    @FXML
    public void initialize() {

        initComboBox();

        initListView();

        initTableView();

        loadDevelopersFromDatabase();
    }
```

```java
    private boolean createDeveloper(String name, Level level, List<Technology> selectedTechs) {
        try {
            Developer newDev = new Developer(name, level, selectedTechs);
            Developer savedDeveloper = developerDAO.save(newDev);
            return developersData.add(savedDeveloper);
        } catch (IllegalArgumentException exception) {
            throw exception;
        }
    }
```

```java
    public void handleDeleteSelected() {
        Developer selectedDeveloper = developerTable.getSelectionModel().getSelectedItem();
        if (selectedDeveloper == null) {
            showAlert(Alert.AlertType.WARNING, "Предупреждение",null, "Моля, изберете разработчик от таблицата.");
        } else {
            Optional<ButtonType> result =  showAlert(Alert.AlertType.WARNING, "Предупреждение", "Изтриване на разработчик", "Сигурни ли сте, че искате да изтриете избрания разработчик?");
            if (result.isPresent() && result.get() == ButtonType.OK) {
                developerDAO.deleteById(selectedDeveloper.getId());
                developersData.remove(selectedDeveloper);
            }
        }
    }
```

Стъпка 6: Добавяне на бутоните в менюто за редактиране

```xml
<Menu text="Файл">
    <MenuItem text="Нов" onAction="#handleNew"/>
    <MenuItem text="Изтрий избран" onAction="#handleDeleteSelected"/>
    <MenuItem text="Редакция избран" onAction="#handleEditDeveloper"/>
    <MenuItem text="Изход" onAction="#handleExit"/>
</Menu>
```

```xml
<ContextMenu>
    <items>
        <MenuItem text="Редакция" onAction="#handleEditDeveloper"/>
        <MenuItem text="Изтрий" onAction="#handleDeleteSelected"/>
    </items>
</ContextMenu>
```

Стъпка 7: Създаване на метода за редктиране в DeveloperController:

public void handleEditDeveloper(ActionEvent actionEvent) {
}


Стъпка 8: Създаване на fxml файла за редактиране:

```xml
<VBox stylesheets="@css/styles.css"
      xmlns:fx="http://javafx.com/fxml"
      fx:controller="bg.tu_varna.sit.ps.lab7.task1.controllers.EditDeveloperDialogController"
      spacing="10" prefWidth="350" styleClass="main-container">

    <Label text="Редакция на програмист"/>

    <Label text="Име и фамилия:"/>
    <TextField fx:id="nameField"/>

    <Label text="Ниво:"/>
    <ComboBox fx:id="levelCombo"/>

    <Label text="Умения:"/>
    <ListView fx:id="techListView" prefHeight="150"/>

    <Label fx:id="errorLabel" style="-fx-text-fill: red;" visible="false" wrapText="true"/>

    <HBox spacing="10">
        <Button text="Запази" onAction="#handleSave"/>
        <Button text="Отказ" onAction="#handleCancel"/>
    </HBox>
</VBox>
```

Стъпка 9: Обновяване на Developer класа:

``` java

public class Developer {
    private String name;
    private Level level;
    private List<Technology> skills;

    public Developer(String name, Level level, List<Technology> skills) {
        setName(name);
        setLevel(level);
        setSkills(skills);
    }

    public String getName() {
        return name;
    }

    public Level getLevel() {
        return level;
    }

    public List<Technology> getSkills() {
        return skills;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Моля, въведете име.");
        }
        this.name = name.trim();
    }

    public void setLevel(Level level) {
        if (level == null) {
            throw new IllegalArgumentException("Моля, изберете ниво.");
        }
        this.level = level;
    }

    public void setSkills(List<Technology> skills) {
        if (skills == null || skills.isEmpty()) {
            throw new IllegalArgumentException("Моля, изберете поне едно умение.");
        }
        this.skills = new ArrayList<>(skills);
    }
}

```

Стъпка 10: Създаване на контролера за редактиране:

```java
public class EditDeveloperDialogController {

    @FXML
    private TextField nameField;

    @FXML
    private ComboBox<Level> levelCombo;

    @FXML
    private ListView<Technology> techListView;

    @FXML
    private Label errorLabel;

    private Developer developer;

    private DeveloperDAO developerDAO;

    private boolean saved = false;

    @FXML
    public void initialize() {
        levelCombo.setItems(FXCollections.observableArrayList(Level.values()));
        techListView.setItems(FXCollections.observableArrayList(Technology.values()));
        techListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;

        nameField.setText(developer.getName());
        levelCombo.setValue(developer.getLevel());

        techListView.getSelectionModel().clearSelection();
        for (Technology technology : developer.getSkills()) {
            techListView.getSelectionModel().select(technology);
        }
    }

    public void setDeveloperDao(DeveloperDAO developerDAO) {
        this.developerDAO = developerDAO;
    }

    public boolean isSaved() {
        return saved;
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    public void handleSave(ActionEvent actionEvent) {
        try {
            String name = nameField.getText();
            Level level = levelCombo.getValue();
            List<Technology> skills = techListView.getSelectionModel().getSelectedItems();

            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Името е задължително.");
            }
            if (level == null) {
                throw new IllegalArgumentException("Изберете ниво.");
            }
            if (skills == null || skills.isEmpty()) {
                throw new IllegalArgumentException("Изберете поне едно умение.");
            }

            developer.setName(name);
            developer.setLevel(level);
            developer.setSkills(skills);

            if(developerDAO.update(developer) != null) {
                saved = true;
            }
            closeWindow();
        } catch (IllegalArgumentException e) {
            errorLabel.setText(e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    public void handleCancel(ActionEvent actionEvent) {
        closeWindow();
    }
}
```

Стъпка 11: Отваряне на прозореца за редкатиране от DeveloperController:

```java

    public void handleEditDeveloper(ActionEvent actionEvent) {
        Developer selectedDeveloper = developerTable.getSelectionModel().getSelectedItem();

        if (selectedDeveloper == null) {
            showAlert(Alert.AlertType.WARNING, "Предупреждение", null, "Моля, изберете програмист от таблицата.");
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/bg/tu_varna/sit/ps/lab7/task1/edit-developer-dialog.fxml")
                );
                Parent root = loader.load();

                EditDeveloperDialogController controller = loader.getController();
                controller.setDeveloper(selectedDeveloper);
                controller.setDeveloperDao(developerDAO);

                Stage stage = new Stage();
                stage.setTitle("Редакция на програмист");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root));
                stage.showAndWait();

                if (controller.isSaved()) {
                    developerTable.refresh();
                    showAlert(Alert.AlertType.INFORMATION, "Успешно", null, "Данните са редактирани успешно.");
                }

            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Грешка", "Проблем при отваряне на прозореца", e.getMessage());
            }   
        }
    }

```
