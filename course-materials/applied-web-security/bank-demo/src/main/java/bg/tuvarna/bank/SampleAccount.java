package bg.tuvarna.bank;

import java.time.LocalDate;
import java.util.List;

public record SampleAccount(String owner, String firstName, String middleName, String lastName,
                            String type, LocalDate openedOn, String egn, String reason) {
    static final List<SampleAccount> EXAMPLES = List.of(
        new SampleAccount("user", "Иван", "Петров", "Димов", "Разплащателна", LocalDate.of(2026, 1, 10), "0000001001", "Учебни месечни плащания"),
        new SampleAccount("user", "Иван", "Петров", "Димов", "Спестовна", LocalDate.of(2026, 2, 15), "0000001001", "Учебни спестявания"),
        new SampleAccount("user", "Иван", "Петров", "Димов", "Депозитна", LocalDate.of(2026, 3, 20), "0000001001", "Учебен срочен депозит"),
        new SampleAccount("admin", "Мария", "Георгиева", "Стоянова", "Разплащателна", LocalDate.of(2026, 4, 5), "0000001002", "Учебни текущи разходи"),
        new SampleAccount("admin", "Мария", "Георгиева", "Стоянова", "Спестовна", LocalDate.of(2026, 5, 12), "0000001002", "Учебен резерв"),
        new SampleAccount("admin", "Николай", "Иванов", "Колев", "Депозитна", LocalDate.of(2026, 6, 18), "0000001003", "Учебен депозит")
    );
}
