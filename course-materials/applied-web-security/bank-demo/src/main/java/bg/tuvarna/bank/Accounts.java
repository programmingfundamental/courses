package bg.tuvarna.bank;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.sql.PreparedStatement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class Accounts {
    public record Account(long id, String alias, String iban, String egn, String note) {
        public String getIbanTail() { return ShiftCipher.guess(iban,ShiftCipher.KEY).substring(18); }
    }
    private final JdbcTemplate db;
    private static final String FIELDS = "select id, alias, iban_cipher, egn_cipher, note from accounts ";
    private static final RowMapper<Account> ROW = (r,n) -> new Account(r.getLong(1),r.getString(2),r.getString(3),r.getString(4),r.getString(5));
    Accounts(JdbcTemplate db) { this.db=db; }
    public List<Account> find(String term) {
        String text=term.strip().toUpperCase(Locale.ROOT);
        String encoded=ShiftCipher.encrypt(text);
        return db.query(FIELDS+"where locate(?,upper(alias))>0 or locate(?,iban_cipher)>0 or locate(?,egn_cipher)>0 order by id",
            ROW,text,encoded,encoded);
    }
    public Optional<Account> one(long id) {
        return db.query(FIELDS+"where id=?",ROW,id).stream().findFirst();
    }
    public long add(String alias,String iban,String egn,String note) {
        if(alias.isBlank() || alias.length()>60 || note.length()>300 || !iban.matches("BG00DEMO[0-9]{14}") || !egn.matches("000000[0-9]{4}"))
            throw new IllegalArgumentException("Invalid demo account");
        var key=new GeneratedKeyHolder();
        db.update(c -> {
            PreparedStatement s=c.prepareStatement("insert into accounts(alias,iban_cipher,egn_cipher,note) values (?,?,?,?)",new String[]{"ID"});
            s.setString(1,alias.strip()); s.setString(2,ShiftCipher.encrypt(iban)); s.setString(3,ShiftCipher.encrypt(egn)); s.setString(4,note);
            return s;
        },key);
        return key.getKey().longValue();
    }
}
