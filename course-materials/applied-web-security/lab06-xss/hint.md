# Упражнение 6 — HTML изглед на задачите и XSS защита — решения и насоки


## Решение на примерния проблем

TaskPageController е @RestController с инжектиран TaskService. Методът:

```java
@GetMapping(value="/ui/tasks", produces="text/html;charset=UTF-8")
public String tasks() {
    StringBuilder html = new StringBuilder("<!doctype html><html lang=\"bg\"><meta charset=\"UTF-8\"><title>Задачи</title><body><h1>Задачи</h1>");
    for (var t : service.getAll()) {
        html.append("<article><h2>").append(org.springframework.web.util.HtmlUtils.htmlEscape(t.getSummary()))
            .append("</h2><p>").append(org.springframework.web.util.HtmlUtils.htmlEscape(t.getDescription()))
            .append("</p></article>");
    }
    return html.append("</body></html>").toString();
}
```

CSP се добавя към security chain; header съдържа `default-src 'none'; form-action 'self'; frame-ancestors 'none'; base-uri 'none'`. Test POST създава task със summary минимум 10 символа и future deadline; GET като същия user съдържа &lt;b&gt;, не raw <b>. GET като bob не съдържа нито summary, нито description на alice.

## Решение на самостоятелна задача 1

Добавяме nullable колона reference_url length=2048 и DTO поле. Преди save/update валидираме чрез URI: стойността е null/blank → null; дължина>2048/control characters → 400; scheme не е http/https, getHost()==null или getUserInfo()!=null → 400; URISyntaxException → 400. Приемаме uri.toASCIIString(). Връзката се генерира като `<a href="` + HtmlUtils.htmlEscape(url) + `">Референция</a>`. Проверяваме URL преди промяна на managed entity; при отказ транзакцията не записва промени. При blank поле изрично setReferenceUrl(null), защото CustomMapper skipNullEnabled иначе би запазил старата връзка.

Тестове: https://example.com/a?x=1&y=2 → anchor с &amp;; javascript:/data:/relative и сурова кавичка → 400 без DB промяна; empty → липсва anchor; bob не може да смени URL на alice.

## Решение на самостоятелна задача 2

Вход &lt; остава същият в DB, изходът е &amp;lt; и браузърът показва буквалното &lt;. Кавичките в URL се отказват от URI parser; encoding остава отделен контрол. javascript: се отказва независимо от escaping. Тестът проверява самия HTML, защото CSP може да скрие липсващото encoding. Браузърната проверка включва отваряне на owner страницата и наблюдение на marker без следване на външния URL.


## Въпроси за анализ

1. Защо JSON отговорът не доказва XSS?
2. Къде трябва да се извърши HTML encoding?
3. Защо URL validation е отделна от HTML escaping?

## Checklist

- [ ] Примерният проблем има работеща реализация в Task Manager.
- [ ] Самостоятелните задачи имат код/анализ и проверими резултати.
- [ ] Тестовете включват разрешен и отказан сценарий.
- [ ] Отказаната операция не променя DB.
- [ ] Изпълнените H2/PostgreSQL и браузърни проверки са разграничени.
- [ ] Отчетът не съдържа пароли, raw tokens или поверителни бележки.
