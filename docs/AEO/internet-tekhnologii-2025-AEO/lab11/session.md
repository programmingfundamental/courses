---
layout: default
title: Sessions
parent: Laboratory excercise 11
grand_parent: Internet Technologies
nav_order: 4
---


# Sessions

HTTP is a stateless protocol, which means that each time a client retrieves a web page, the client opens a separate connection to the web server, and the server does not automatically keep any record of the client's previous request. There are still three ways to maintain a session between a web client and a web server:

* Cookies - The web server can set a unique session ID in the form of a cookie for each web client, and for subsequent requests from that client, it can be recognized. This is not an efficient method because it is possible that the browser has disabled cookies in its settings, so this approach is not recommended for session management.

* Hidden fields - The web server can send a hidden field on an HTML form, along with a unique session ID:

```
<input type="hidden" name="sessionid" value="12345">
```

Using this method means that the specified name and value are automatically included in the GET or POST data. Each time a web browser makes a request, the sessionid value can be used to track its identity. This can be an effective way to track a session, but activating a simple (\<a href...>) hyperlink does not result in a form submission, which means that hidden form fields cannot provide a universal way to track a session either.

* URL rewriting. Some additional data can be added to the end of the request of each URL to identify it with a specific session, and the server can associate this session "identifier" with data intended for that session. URL rewriting is a better way to maintain sessions and works for browsers that do not support cookies, but the disadvantage here is that you will have to dynamically generate each URL in order for a given session ID to be associated with it, even when using simple static HTML pages.

## HttpSession object

Unlike the three methods described above, a servlet has an HttpSession interface, which provides a way to identify a user across more than one page request or website visit and to store information about that user. The servlet container uses this interface to create a session between an HTTP client and an HTTP server. A session lasts for a specified period of time, across more than one connection or page request by the user.

```
HttpSession session = request.getSession();
```

**Using HttpSession**

The HttpServletRequest interface provides two methods for obtaining an HttpSession object:

* public HttpSession getSession() - returns the current session associated with this request, or if the request does not have a session, creates one.
* public HttpSession getSession(boolean create) - returns the current HttpSession associated with this request, or, if there is no current session and the parameter is true, returns a new session.

Methods of the HttpSession interface:

* public String getId() - returns a string containing the value of the unique identifier.
* public long getCreationTime() - returns the time this session was created, measured in milliseconds since midnight on January 1, 1970 GMT.
* public long getLastAccessedTime() - returns the last time the client sent a request associated with this session, as the number of milliseconds since midnight on January 1, 1970 GMT.
* public void invalidate() - invalidates the session and then unbinds all objects associated with it.
* public void setAttribute(String name, Object value) - this method sets an object to this session using the specified name.
* public Object getAttribute(String name) - this method returns the object associated with the specified name in this session, or null if no object is bound by the name.
* public int getMaxInactiveInterval() - retrieves the time in seconds until the session expires
* public void setMaxInactiveInterval(int interval) - this method sets the time, in seconds, between client requests before the servlet container will invalidate this session.
* public void removeAttribute(String name) - this method removes a variable from the session
