---
layout: default
title: Конфигуриране на филтри
parent: Лабораторно упражнение 11
grand_parent: Интернет технологии
nav_order: 4
---


# Конфигуриране на филтри

Начинът, по който Spring действително работи с HttpSecurity, е чрез редица защитни филтри. Всъщност той създава верига за филтър за сигурност. Погледнете по-отблизо изхода в конзолата за отстраняване на грешки, когато стартирате приложението:

<figure><img src="../../../assets/image (162).png" alt=""><figcaption></figcaption></figure>

Както можете да видите, Spring взема настройките в нашата персонализирана конфигурация за сигурност и ги поставя в редица предварително дефинирани (по подразбиране) филтри за сигурност. Списъкът с тези филтри всъщност е подреден списък от филтри, който се управлява от Spring, и всички те образуват DefaultSecurityFilterChain.

Новият начин за сигурност, който се поддържа от Spring, е да използваме нашите персонализирани http филтри за сигурност (във веригата на филтъра за сигурност), вместо да ги конфигурираме чрез клас, който трябва да разшири WebSecurityConfigurerAdapter. По същество Spring ни позволява да конфигурираме нашия HttpSecurity с помощта на bean метод, който изгражда и връща нашата персонализирана реализация на интерфейса SecurityFilterChain.

```java
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("api/auth/**")
                                .permitAll()
                                .anyRequest()
                                .authenticated())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.IF_REQUIRED))
                .logout(logout -> logout
                        .logoutUrl("api/auth/logout")
                        .invalidateHttpSession(true));

        return http.build();
    }
```
