---
layout: default
title: Tasks
parent: Laboratory excercise 11
grand_parent: Internet Technologies
nav_order: 7
---

# Task

1\. Create functionalities for user registration and login in the task management application. Follow these steps:

a. Create the following entity classes:

· User – id, name, username, password, email;

· Role – id, name. 

/Roles will be saved with prefixes as ROLE_USER, ROLE_ADMIN/

Annotate the many-to-many (M:M) relationship between the two entities. Create the tables by running the application.

b. Add two repository layers for managing users and roles. Add methods for finding a user by username (as well as a boolean method to determine whether such a user exists) and for finding a role by name.

c. Create a class that implements the UserDetailsService interface. Implement the method loadUserByUsername(String username), and have it return a UserDetails object containing the user's data and their roles;

d. Create a configuration class (annotated with @Configuration, @EnableWebSecurity, and @EnableMethodSecurity) that injects the UserDetailsService implementation and configures the AuthenticationManager and PasswordEncoder. Include a SecurityFilterChain method that sets free access to resources related to registration, login, and logout, and requires authentication for all others.

e. Add a service layer, controller, and the necessary DTOs to implement the registration, login, and logout logic.

f. Make tasks only addable, editable, and deleted by users with the admin role.
