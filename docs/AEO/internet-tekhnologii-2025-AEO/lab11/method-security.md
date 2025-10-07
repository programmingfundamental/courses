---
layout: default
title: Method Security 
parent: Laboratory excercise 11
grand_parent: Internet Technologies
nav_order: 6
---

# Method Security with Spring Security

One of the features of Spring Security is the ability to provide method-level security. Method-level security allows developers to specify security restrictions for specific methods within a class, rather than applying security restrictions to the entire class or application. This allows for finer control over access to specific parts of the application. To implement method-level security in Spring Security, developers can use the @Secured, @RolesAllowed, and @PreAuthorize annotations. To enable the latter, create a Spring Security configuration class that allows method-level security using the @EnableMethodSecurity annotation.

## @Secured Annotation

The @Secured annotation is used to specify a list of roles that are allowed to access a particular method.

```java
@Secured("ROLE_ADMIN")
public void deleteUser(int userId) {
// Method logic here
}
```

## @PreAuthorize Annotation

The @PreAuthorize annotation is used to define more complex security constraints using SpEL (Spring Expression Language).

```java
@PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #userId == principal.userId)")
public void updateUser(int userId) {
// Method logic here
}
```

This annotation allows access to the updateUser() method only to users with the ROLE_ADMIN role or users with the ROLE_USER role and the same user ID as the principal (currently authenticated user).

You can use SpEL to define security expressions as follows:

• hasRole(role): returns true if the current user has the specified role;

• hasAnyRole(role1,role2): returns true if the current user has any of the provided roles;

• isAnonymous(): returns true if the current user is anonymous;

• isAuthenticated(): returns true if the user is not anonymous;

• isFullyAuthenticated(): returns true if the user is not anonymous or a Remember-Me user;

You can combine these expressions using the logical operators AND, OR, and NOT(!).

```java
@PreAuthorize("hasRole('ADMIN') OR hasRole('USER')")
@PreAuthorize("isFullyAuthenticated() AND hasRole('ADMIN')")
@PreAuthorize("!isAnonymous()")
