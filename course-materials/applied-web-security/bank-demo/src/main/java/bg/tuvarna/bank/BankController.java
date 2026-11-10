package bg.tuvarna.bank;

import java.util.Locale;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BankController {
    private final Accounts accounts;
    BankController(Accounts accounts) { this.accounts=accounts; }

    @GetMapping("/") String home() { return "redirect:/accounts"; }

    @GetMapping("/login") String login(@RequestParam(name="case", defaultValue="mistake") String scenario, Model m) {
        // Fixed truth-table model. It neither queries users nor modifies the security context.
        boolean correct = scenario.equals("match");
        boolean altered = scenario.equals("altered");
        m.addAttribute("scenario",correct?"match":altered?"altered":"mistake");
        m.addAttribute("correct",correct); m.addAttribute("altered",altered);
        m.addAttribute("weakResult",correct || altered);
        return "login";
    }
    @GetMapping("/accounts") String list(@RequestParam(defaultValue="") String q,Model m) {
        if(q.length()>100) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        m.addAttribute("q",q);m.addAttribute("rows",accounts.find(q));return "accounts";
    }
    @GetMapping("/accounts/new") String form(@RequestParam(defaultValue="") String example, Model m) {
        if(example.equals("bold") || example.equals("marker")) {
            var rnd=java.util.concurrent.ThreadLocalRandom.current();
            return formValues(m,"Учебно ателие",String.format(Locale.ROOT,"BG00DEMO%014d",rnd.nextLong(100000000000000L)),
                String.format(Locale.ROOT,"000000%04d",rnd.nextInt(10000)),
                example.equals("bold")?"<b>Месечен отчет</b>":"Преглед: [[МАРКЕР]]","");
        }
        return formValues(m,"","","","","");
    }
    private String formValues(Model m,String alias,String iban,String egn,String note,String error) {
        m.addAttribute("alias",alias);m.addAttribute("iban",iban);m.addAttribute("egn",egn);
        m.addAttribute("note",note);m.addAttribute("error",error);return "create";
    }
    @PostMapping("/accounts") String create(@RequestParam String alias,@RequestParam String iban,@RequestParam String egn,
        @RequestParam(defaultValue="") String note,Model m,RedirectAttributes flash,jakarta.servlet.http.HttpServletResponse response) {
        iban=iban.strip().toUpperCase(Locale.ROOT);egn=egn.strip();
        String err=null;
        if(alias.isBlank() || alias.length()>60) err="Въведете учебен псевдоним от 1 до 60 знака.";
        else if(!iban.matches("BG00DEMO[0-9]{14}")) err="Учебният IBAN трябва да е BG00DEMO, последвано от точно 14 цифри.";
        else if(!egn.matches("000000[0-9]{4}")) err="Учебното ЕГН трябва да започва с 000000 и да съдържа още 4 цифри.";
        else if(note.length()>300) err="Бележката може да е до 300 знака.";
        if(err!=null) { response.setStatus(400); return formValues(m,alias,iban,egn,note,err); }
        try {
            long id=accounts.add(alias,iban,egn,note);flash.addFlashAttribute("created",true);
            return "redirect:/accounts/"+id;
        } catch (DuplicateKeyException e) {
            response.setStatus(409);return formValues(m,alias,iban,egn,note,"Този учебен IBAN вече съществува.");
        }
    }
    @GetMapping("/accounts/{id}") String account(@PathVariable long id,@RequestParam(defaultValue="0") int shift,Model m) {
        if(shift<0 || shift>9) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        var a=accounts.one(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        m.addAttribute("row",a);m.addAttribute("shift",shift);
        m.addAttribute("guessIban",ShiftCipher.guess(a.iban(),shift));m.addAttribute("guessEgn",ShiftCipher.guess(a.egn(),shift));
        m.addAttribute("match",shift==ShiftCipher.KEY);
        // Only a fixed, inert presentation vocabulary is recognized. Never evaluate supplied markup.
        String n=a.note();boolean bold=n.startsWith("<b>") && n.endsWith("</b>") && n.length()>=7;
        m.addAttribute("boldModel",bold);m.addAttribute("modelText",bold?n.substring(3,n.length()-4):n);
        m.addAttribute("marker",n.contains("[[МАРКЕР]]"));
        return "account";
    }
}
