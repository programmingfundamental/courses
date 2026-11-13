package bg.tuvarna.bank;

import java.util.List;

public record SampleAccount(String alias, String iban, String egn, String note) {
    static final List<SampleAccount> EXAMPLES = List.of(
        new SampleAccount("Ателие Орбита", "BG00DEMO00000000000001", "0000001001", "Месечен абонамент за учебното ателие."),
        new SampleAccount("Студио Маяк", "BG00DEMO00000000000002", "0000001002", "<b>Преглед на извлечение</b>"),
        new SampleAccount("Фонд Хоризонт", "BG00DEMO00000000000003", "0000001001", "Учебна бележка: [[МАРКЕР]]")
    );
}
