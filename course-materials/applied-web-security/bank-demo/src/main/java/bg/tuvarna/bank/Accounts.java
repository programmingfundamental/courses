package bg.tuvarna.bank;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class Accounts {
    public static final List<String> TYPES = List.of("Разплащателна", "Спестовна", "Депозитна");

    public record Account(long id, String firstName, String middleName, String lastName,
                          String accountType, String iban, LocalDate openedOn, String egn, String reason) {
        public String getHolderName() {
            return firstName + " " + middleName + " " + lastName;
        }
    }

    private static final String SELECT = """
        select id, first_name, middle_name, last_name, account_type, iban, opened_on, egn_cipher, reason
        from accounts
        """;
    private static final RowMapper<Account> ROW = (row, number) -> new Account(
        row.getLong("id"), row.getString("first_name"), row.getString("middle_name"),
        row.getString("last_name"), row.getString("account_type"), row.getString("iban"),
        row.getDate("opened_on").toLocalDate(), row.getString("egn_cipher"), row.getString("reason")
    );
    private final JdbcTemplate db;

    Accounts(JdbcTemplate db) {
        this.db = db;
    }

    public List<Account> find(String owner, String type) {
        if (type.isBlank()) {
            return db.query(SELECT + "where owner_login = ? order by id", ROW, owner);
        }
        // Intentional SQL injection in the local classroom filter.
        String sql = SELECT + "where owner_login = ? and account_type = '" + type + "' order by id";
        return db.query(sql, ROW, owner);
    }

    public Optional<Account> one(long id, String owner) {
        return db.query(SELECT + "where id = ? and owner_login = ?", ROW, id, owner).stream().findFirst();
    }

    @Transactional
    public long add(String owner, String firstName, String middleName, String lastName,
                    String type, LocalDate openedOn, String egn, String reason) {
        for (String name : List.of(firstName, middleName, lastName)) {
            if (!name.strip().matches("[\\p{L}][\\p{L}'’-]{0,49}")) {
                throw new IllegalArgumentException("Попълнете трите имена с букви, до 50 знака за всяко име.");
            }
        }
        if (!TYPES.contains(type)) {
            throw new IllegalArgumentException("Изберете валиден вид сметка.");
        }
        if (openedOn == null) {
            throw new IllegalArgumentException("Попълнете дата на откриване.");
        }
        if (reason.isBlank() || reason.length() > 2000) {
            throw new IllegalArgumentException("Попълнете основание от 1 до 2000 знака.");
        }
        String encryptedEgn = ShiftCipher.encrypt(egn);
        long number = db.queryForObject("select next value for iban_number", Long.class);
        String iban = generateIban(number);
        var key = new GeneratedKeyHolder();
        db.update(connection -> {
            PreparedStatement statement = connection.prepareStatement("""
                insert into accounts(owner_login, first_name, middle_name, last_name,
                                     account_type, iban, opened_on, egn_cipher, reason)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, new String[]{"ID"});
            statement.setString(1, owner);
            statement.setString(2, firstName.strip());
            statement.setString(3, middleName.strip());
            statement.setString(4, lastName.strip());
            statement.setString(5, type);
            statement.setString(6, iban);
            statement.setDate(7, Date.valueOf(openedOn));
            statement.setString(8, encryptedEgn);
            statement.setString(9, reason);
            return statement;
        }, key);
        return key.getKey().longValue();
    }

    private static String generateIban(long number) {
        String bban = String.format(Locale.ROOT, "DEMO000100%08d", number);
        StringBuilder numeric = new StringBuilder();
        for (char character : (bban + "BG00").toCharArray()) {
            numeric.append(Character.digit(character, 36));
        }
        int checksum = 98 - new BigInteger(numeric.toString()).mod(BigInteger.valueOf(97)).intValue();
        return String.format(Locale.ROOT, "BG%02d%s", checksum, bban);
    }
}
