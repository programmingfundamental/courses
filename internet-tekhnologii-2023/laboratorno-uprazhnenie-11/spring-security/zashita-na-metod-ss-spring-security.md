# Защита на метод със Spring Security

Една от характеристиките на Spring Security е възможността за защита на ниво метод. Защитата на ниво метод позволява на разработчиците да определят ограничения на сигурността на конкретни методи в рамките на клас, вместо да прилагат ограничения на сигурността към целия клас или приложение. Това позволява по-прецизен контрол върху достъпа до конкретни части на приложението. За да внедрят защита на ниво метод в Spring Security, разработчиците могат да използват анотациите @Secured, @RolesAllowed и @PreAuthorize. За да бъдат активирани последните, създайте Spring Security configuration клас, който да позволи включването на method-level security с помощта на анотацията @EnableMethodSecurity.

#### Анотация @Secured

Анотацията @Secured се използва за указване на списък с роли, на които е разрешен достъп до определен метод.&#x20;

<pre class="language-java"><code class="lang-java"><strong>@Secured("ROLE_ADMIN")
</strong>public void deleteUser(int userId) {
// Method logic here
}
</code></pre>

#### Анотация @PreAuthorize

Анотацията @PreAuthorize се използва за определяне на по-сложни ограничения за сигурност с помощта на SpEL (Spring Expression Language).

```java
@PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #userId == principal.userId)")
public void updateUser(int userId) {
// Method logic here
}
```

Тази анотация позволява достъп до метода updateUser() само на потребители с роля ROLE\_ADMIN или потребители с роля ROLE\_USER и същия потребителски идентификатор като главния (текущо удостоверения потребител).&#x20;

Можете да използвате SpEL, за да дефинирате изразите за сигурност, както следва:

• hasRole(role): връща true, ако текущият потребител има указаната роля;

• hasAnyRole(role1,role2): връща true, ако текущият потребител има такива от предоставените роли;

• isAnonymous(): връща true, ако текущият потребител е анонимен;

• isAuthenticated(): връща true, ако потребителят не е анонимен;

• isFullyAuthenticated(): връща true, ако потребителят не е анонимен или Remember-Me потребител;

Можете да комбинирате тези изрази с помощта на логическите оператори AND, OR и NOT(!).

```java
@PreAuthorize("hasRole('ADMIN') OR hasRole('USER')")
@PreAuthorize("isFullyAuthenticated() AND hasRole('ADMIN')")
@PreAuthorize("!isAnonymous()")
```
