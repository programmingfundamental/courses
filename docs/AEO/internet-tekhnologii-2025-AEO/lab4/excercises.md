---
layout: default
title: Excercises
parent: Laboratory excercise 4
grand_parent: Internet Technologies
nav_order: 4
---

# Excercises

### Excercise 1

Create an application to accept applications for participation in a chess tournament. Provide the ability to register chess players in the event (FIDE ID, name, surname, ELO). The data should be stored in a collection suitable for the purpose.

Create the following servlets:

· Servlet for viewing all registered chess players (ShowAllPlayersServlet). To be accessible at URL \<project url>/players. Chess players should be displayed in descending order of their ELO.

· Servlet for registering a chess player (RegistrationServlet). To be accessible at URL \<project url>/players/add;

· Servlet for viewing a chess player by a given FIDE ID (ShowPlayerServlet). To be accessible at URL \<project url>/players/view.

Test the created functionalities using Postman.

### Excercise 2

Create an application for the needs of a "Student of the Year" competition. Users should be able to add a student (faculty number, first name, last name, major) to participate in the competition, thereby adding 1 vote to his candidacy. If the student has already been added by a previous user, he is not added again, and the votes in his favor are increased by 1.Create an application for the needs of a "Student of the Year" competition. Users should be able to add a student (faculty number, first name, last name, major) to participate in the competition, thereby adding 1 vote to his candidacy. If the student has already been added by a previous user, he is not added again, and the votes in his favor are increased by 1.
Create the following servlets:

· Servlet for adding a nomination or vote (AddVoteServlet). To be accessible at URL \<project url>/vote

· Servlet for displaying nominated students and their results (GetResultsServlet). To be accessible at URL \<project url>/results and to provide the ability to visualize students in a view sorted by number of votes.

Test the created functionalities using Postman.
