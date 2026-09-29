package bg.tuvarna.lab;

import java.util.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.util.HtmlUtils;

@RestController
public class Web {
    private final JdbcTemplate db; private final Documents documents; private final LabMode mode;
    private final Accounts accounts; private final Vault vault; private final Tokens tokens;
    public Web(JdbcTemplate db,Documents documents,LabMode mode,Accounts accounts,Vault vault,Tokens tokens) {
        this.db=db;this.documents=documents;this.mode=mode;this.accounts=accounts;this.vault=vault;this.tokens=tokens;
    }
    @GetMapping(value="/",produces="text/html") String home() {
        return "<h1>Local Security Lab</h1><p>Само за контролирана учебна среда.</p><a href='/login'>Login</a>";
    }
    @GetMapping("/health") Map<String,String> health() { return Map.of("status","UP"); }
    @GetMapping("/csrf") Map<String,String> csrf(CsrfToken token) { return token==null?Map.of():Map.of("token",token.getToken(),"headerName",token.getHeaderName()); }
    @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED)
    void register(@RequestParam String username,@RequestParam String password) {
        if(!username.matches("[a-z][a-z0-9]{2,31}") || password.length()<12 || password.length()>64) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        db.update("INSERT INTO app_users(username,password,role,display_name) VALUES (?,?,'USER',?)",username,accounts.encode(password),username);
    }
    @GetMapping("/api/me") Map<String,String> me(Authentication user) { return Map.of("username",user.getName()); }
    @GetMapping("/admin/status") Map<String,String> admin() { return Map.of("status","admin-only"); }
    @GetMapping("/api/documents/{id}") Documents.Document document(@PathVariable long id,Authentication user) { return documents.get(id,user); }
    @GetMapping("/api/search") List<Documents.Document> search(@RequestParam(defaultValue="") String q,Authentication user) { return documents.search(q,user.getName()); }
    @GetMapping(value="/search",produces="text/html") String reflected(@RequestParam(defaultValue="") String q) { check(q,100); return "<p>Search: "+render(q)+"</p>"; }
    @PostMapping("/api/comments") @ResponseStatus(HttpStatus.CREATED) void addComment(@RequestParam String body,Authentication user) {
        check(body,1000); db.update("INSERT INTO comments(author,body) VALUES (?,?)",user.getName(),body);
    }
    @GetMapping(value="/comments",produces="text/html") String comments() {
        return "<h1>Comments</h1>"+String.join("",db.query("SELECT body FROM comments ORDER BY id",(r,n)->"<p>"+render(r.getString(1))+"</p>"));
    }
    @PostMapping("/api/profile") void profile(@RequestParam String displayName,Authentication user) {
        check(displayName,200); db.update("UPDATE app_users SET display_name=? WHERE username=?",displayName,user.getName());
    }
    @GetMapping(value="/profile",produces="text/html") String profile(Authentication user) {
        return "<h1>"+render(db.queryForObject("SELECT display_name FROM app_users WHERE username=?",String.class,user.getName()))+"</h1>";
    }
    @PostMapping("/api/sensitive") void sensitive(@RequestParam String value,Authentication user) {
        check(value,200); if(value.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        db.update("UPDATE app_users SET sensitive=? WHERE username=?",mode.vulnerable(8)?value:vault.encrypt(value,user.getName()),user.getName());
    }
    @GetMapping("/api/sensitive") Map<String,String> sensitive(Authentication user) {
        String raw=db.queryForObject("SELECT sensitive FROM app_users WHERE username=?",String.class,user.getName());
        return Map.of("value",raw==null?"":mode.vulnerable(8)?raw:vault.decrypt(raw,user.getName()));
    }
    @PostMapping("/token") Map<String,String> token(Authentication user) { return Map.of("access_token",tokens.issue(user.getName()),"token_type","Bearer"); }
    @GetMapping("/token-api/documents") Map<String,String> tokenResource(Authentication user) { return Map.of("subject",user.getName(),"message","scope accepted"); }
    private String render(String text) { return mode.vulnerable(6)?text:HtmlUtils.htmlEscape(text); }
    private void check(String text,int max) { if(text==null || text.length()>max || text.indexOf('\0')>=0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST); }
}
