package bg.tuvarna.bank;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BankController {
    private final Accounts accounts;

    BankController(Accounts accounts) {
        this.accounts = accounts;
    }

    @ModelAttribute("accountTypes")
    List<String> accountTypes() {
        return Accounts.TYPES;
    }

    @GetMapping("/")
    String home() {
        return "redirect:/accounts";
    }

    @GetMapping("/login")
    String login() {
        return "login";
    }

    @GetMapping("/demo/accounts")
    String oldRegister() {
        return "redirect:/accounts";
    }

    @GetMapping("/accounts")
    String list(@RequestParam(defaultValue = "") String type, Principal principal,
                Model model, HttpServletResponse response) {
        if (type.length() > 500) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        model.addAttribute("type", type);
        try {
            model.addAttribute("rows", accounts.find(principal.getName(), type));
        } catch (DataAccessException exception) {
            response.setStatus(400);
            model.addAttribute("rows", List.of());
            model.addAttribute("error", "Филтърът образува невалидна SQL заявка. Коригирайте вида сметка.");
        }
        return "accounts";
    }

    @GetMapping("/accounts/new")
    String form(Model model) {
        return formValues(model, "", "", "", Accounts.TYPES.get(0), LocalDate.now().toString(), "", "", "");
    }

    private String formValues(Model model, String firstName, String middleName, String lastName,
                              String type, String openedOn, String egn, String reason, String error) {
        model.addAttribute("firstName", firstName);
        model.addAttribute("middleName", middleName);
        model.addAttribute("lastName", lastName);
        model.addAttribute("type", type);
        model.addAttribute("openedOn", openedOn);
        model.addAttribute("egn", egn);
        model.addAttribute("reason", reason);
        model.addAttribute("error", error);
        return "create";
    }

    @PostMapping("/accounts")
    String create(@RequestParam String firstName, @RequestParam String middleName,
                  @RequestParam String lastName, @RequestParam String type,
                  @RequestParam String openedOn, @RequestParam String egn,
                  @RequestParam String reason, Principal principal, Model model,
                  RedirectAttributes flash, HttpServletResponse response) {
        try {
            LocalDate date = LocalDate.parse(openedOn);
            accounts.add(principal.getName(), firstName, middleName, lastName, type, date, egn.strip(), reason);
        } catch (DateTimeParseException exception) {
            response.setStatus(400);
            return formValues(model, firstName, middleName, lastName, type, openedOn, egn, reason,
                "Попълнете валидна дата на откриване.");
        } catch (IllegalArgumentException exception) {
            response.setStatus(400);
            return formValues(model, firstName, middleName, lastName, type, openedOn, egn, reason,
                exception.getMessage());
        }
        flash.addFlashAttribute("created", true);
        return "redirect:/accounts";
    }

    @GetMapping("/accounts/{id}")
    String account(@PathVariable long id, Principal principal, Model model) {
        model.addAttribute("row", accounts.one(id, principal.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        return "account";
    }
}
