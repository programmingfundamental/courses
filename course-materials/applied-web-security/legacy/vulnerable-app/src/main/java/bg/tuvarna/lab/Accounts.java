package bg.tuvarna.lab;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class Accounts implements UserDetailsService, ApplicationRunner {
    private final JdbcTemplate db;
    private final PasswordEncoder encoder;
    private final LabMode mode;
    public Accounts(JdbcTemplate db, PasswordEncoder encoder, LabMode mode) { this.db=db; this.encoder=encoder; this.mode=mode; }
    public String encode(String password) { return mode.vulnerable(2) ? "{noop}"+password : encoder.encode(password); }
    public UserDetails loadUserByUsername(String name) {
        return db.query("SELECT username,password,role FROM app_users WHERE username=?", (r,n) ->
            User.withUsername(r.getString(1)).password(r.getString(2)).roles(r.getString(3)).build(), name)
            .stream().findFirst().orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
    }
    public void run(ApplicationArguments args) {
        for (String name : new String[]{"alice","bob","admin"}) {
            if (db.queryForObject("SELECT COUNT(*) FROM app_users WHERE username=?", Integer.class,name)==0)
                db.update("INSERT INTO app_users(username,password,role,display_name) VALUES (?,?,?,?)", name,encode("Lab-"+name+"-2026!"),name.equals("admin")?"ADMIN":"USER",name);
        }
        if (db.queryForObject("SELECT COUNT(*) FROM documents",Integer.class)==0) {
            db.update("INSERT INTO documents VALUES (1,'alice','Alice notes'),(2,'bob','Bob invoice'),(3,'alice','O''Reilly guide')");
        }
    }
}
