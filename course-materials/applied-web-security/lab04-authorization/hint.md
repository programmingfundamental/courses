# Упражнение 4 — Собственост на задачи и контрол на достъпа — решения и насоки


## Решение на примерния проблем

Task получава поле `@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="owner_id",nullable=false) @Setter private User owner;`. TaskRepository добавя `List<Task> findByOwnerUsername(String username);`. В TaskServiceImp инжектираме UserRepository и TaskPolicy. При create след mapping:

```java
task.setOwner(users.findByUsername(policy.currentUser()).orElseThrow(
    () -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED)));
```

TaskPolicy е @Component; ядрото му е:

```java
public String currentUser() { return authentication().getName(); }
private org.springframework.security.core.Authentication authentication() {
    var a = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
    if (a == null || !a.isAuthenticated() || a instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)
        throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED);
    return a;
}
public boolean isAdmin() { return authentication().getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")); }
public void requireReadable(Task task) {
    if (!isAdmin() && !task.getOwner().getUsername().equals(currentUser()))
        throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND);
}
```

getAll използва policy.isAdmin()?repository.findAll():repository.findByOwnerUsername(policy.currentUser()). findById зарежда със ResponseStatusException(NOT_FOUND), извиква requireReadable и после mapper. Операциите са @Transactional(readOnly=true), за да се прочете LAZY owner в service. Create е @Transactional. Миграцията за PostgreSQL се изпълнява от owner на схемата: nullable колона → проверени UPDATE по username/ID → foreign key → NOT NULL.

Преди POST регистрираме alice в работната база и влизаме през Postman. При добавяне на owner свързваме съществуващите задачи с проверени потребители.

## Решение на самостоятелна задача 1

update/delete са @Transactional: зареждат Task, извикват policy.requireReadable и чак тогава променят/изтриват. DTO няма owner; изрично задаваме summary/description/deadline или конфигурираме mapper да пропуска owner. При delete първо проверяваме repository за свързани reports, а при конкуриращ INSERT използваме flush и обработваме DataIntegrityViolationException като 409 извън провалената транзакция. Чужд обект се отказва преди каквато и да е промяна.

Матрица: alice редактира своя задача=200, bob същата=404 и непроменен ред; admin=200; anonymous=401. DELETE без отчети=200 и редът липсва; с отчет=409 и редът остава. GET /reports/task/{id} като USER=403, ADMIN=200. При включване на CSRF в упражнение 8 Postman заявките вече подават header с token от GET /auth/csrf.

## Решение на самостоятелна задача 2

Текст или long overflow → 400; отрицателно/липсващо ID → 404. owner в JSON не се използва; после DB owner е същият. Нов user има празен GET /tasks. Чрез преглед на кода установяваме, че policy се прилага в service преди промяната; HTTP проверката не доказва самостоятелно всички вътрешни извиквания. Бъдещ transfer изисква заключване на реда с @Lock(PESSIMISTIC_WRITE) в транзакция или условен UPDATE с очаквания owner/version; иначе проверка и промяна могат да се разминават.


## Въпроси за анализ

1. Защо hasRole(USER) не доказва ownership?
2. Защо owner не е поле в входния DTO?
3. Каква е разликата между 403 и избрания 404 за чужда задача?

## Checklist

- [ ] Примерният проблем има работеща реализация в Task Manager.
- [ ] Самостоятелните задачи имат код/анализ и проверими резултати.
- [ ] Ръчните проверки включват разрешен и отказан сценарий.
- [ ] Отказаната операция не променя DB.
- [ ] Ръчните API проверки с Postman, DB наблюденията и браузърните проверки са разграничени.
- [ ] Отчетът не съдържа пароли, raw tokens или поверителни бележки.
