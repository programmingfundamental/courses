package bg.tuvarna.lab;

import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class Documents {
    public record Document(long id,String owner,String title) {}
    private final JdbcTemplate db;
    private final LabMode mode;
    public Documents(JdbcTemplate db, LabMode mode) { this.db=db; this.mode=mode; }
    @PreAuthorize("isAuthenticated()")
    public Document get(long id, Authentication user) {
        Document d=db.query("SELECT id,owner,title FROM documents WHERE id=?",(r,n)->new Document(r.getLong(1),r.getString(2),r.getString(3)),id)
            .stream().findFirst().orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        boolean admin=user.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"));
        if(!mode.vulnerable(3) && !admin && !d.owner.equals(user.getName())) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return d;
    }
    public List<Document> search(String q, String owner) {
        if(q==null || q.length()>100 || q.indexOf('\0')>=0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        String sql="SELECT id,owner,title FROM documents WHERE owner=? AND title LIKE ? ORDER BY id";
        if(mode.vulnerable(5)) return db.query("SELECT id,owner,title FROM documents WHERE owner='"+owner+"' AND title LIKE '%"+q+"%' ORDER BY id",
            (r,n)->new Document(r.getLong(1),r.getString(2),r.getString(3)));
        return db.query(sql,(r,n)->new Document(r.getLong(1),r.getString(2),r.getString(3)),owner,"%"+q+"%");
    }
}
