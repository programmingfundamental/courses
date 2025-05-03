---
layout: default
title: Filters and FilterChain
parent: Laboratory excercise 11
grand_parent: Internet Technologies
nav_order: 2
---


# Filters and FilterChain

In a typical Java web application, a client requests access to a resource from the server via HTTP or HTTPS protocol. The client request to the server is handled by a servlet. The servlet processes the HTTP request and provides an HTTP response, which is sent back to the client. A key component of the Servlet specification that plays a major role in request-response processing is the filter. The filter is located before the servlet and intercepts the request and response. It can make changes to the request and response objects. One or more filters can be configured via a FilterChain, and all filters that are part of the chain can intercept and modify the request-response objects. Many of the features of Spring Security are based on filters.

<figure><img src="../../../assets/image (159).png" alt=""><figcaption></figcaption></figure>

Similar to how a special servlet called DispatcherServlet handles all incoming requests in a Spring Web application, a special filter called DelegatingFilterProxy is used to enable Spring Security. This filter is registered in the servlet container and starts intercepting incoming requests. In a Spring Boot application, this registration is done by Spring Boot's Spring Security autoconfiguration. Let's now look at the filter interface, as shown in the following listing.

```java
public interface Filter {

    public default void init(FilterConfig filterConfig) throws
    ServletException {}
    
    public void doFilter(ServletRequest request, ServletResponse response,
    FilterChain chain) throws IOException, ServletException;
    
    public default void destroy() {}
}
```

The Filter implementation must implement three methods (init(), doFilter() and destroy(..)), as shown in the figure below.

<figure><img src="../../../assets/image (164).png" alt=""><figcaption></figcaption></figure>

Trite filtŭrni metoda sa razgledani po-dolu: · Init(..) se izvikva ot ueb konteĭnera, za da ukazhe na filtŭra, che e pusnat v eksploatatsiya. · doFilter(..) e osnovniyat metod, pri koĭto se izvŭrshva deĭstvitelnoto funktsionirane na filtŭra. Toĭ ima dostŭp do zayavkata, otgovora i obektite FilterChain. FilterChain pozvolyava na tekushtiya filtŭr da izvika sledvashtiya filtŭr vŭv verigata, sled kato obrabotkata mu priklyuchi. · Destroy() se izvikva, kogato konteĭnerŭt izvadi filtŭra ot rabota. FilterChain e drug komponent, osiguren ot konteĭnera na sŭrvleta, koĭto predostavya izgled vŭv verigata za izvikvane na filtrirana zayavka. Figurata po-dolu pokazva primerna filtŭrna veriga. Filtrite izpolzvat FilterChain, za da izvikat sledvashtiya filtŭr vŭv verigata ili deĭstvitelniya resurs (napr. sŭrvleta), ako filtŭrŭt e posledniyat vŭv verigata. FilterChain ima samo edin metod, narechen doFilter(). Metodŭt doFilter() v interfeĭsa na filtŭra ima dostŭp do FilterChain zaedno s obektite ServletRequest i ServletResponse. Po tozi nachin filtŭrŭt mozhe da izpŭlni vŭzlozhenata mu zadacha i da poluchi dostŭp do FilterChain, za da izvika sledvashtiya filtŭr vŭv verigata.
Show more
1,170 / 5,000
The three filter methods are discussed below:

· Init(..) is called by the web container to indicate to the filter that it is in service.

· doFilter(..) is the main method where the actual operation of the filter is performed. It has access to the request, response, and FilterChain objects. FilterChain allows the current filter to call the next filter in the chain after it has finished processing.

· Destroy() is called when the container takes the filter out of service.

FilterChain is another component provided by the servlet container that provides a view into the chain of invocation of a filtered request. The figure below shows a sample filter chain. Filters use FilterChain to call the next filter in the chain or the actual resource (e.g., a servlet) if the filter is the last in the chain. FilterChain has only one method, called doFilter(). The doFilter() method in the filter interface has access to the FilterChain along with the ServletRequest and ServletResponse objects. This way, the filter can perform its assigned task and access the FilterChain to call the next filter in the chain.

<figure><img src="../../../assets/image (155).png" alt=""><figcaption></figcaption></figure>

```java
public interface FilterChain {
    public void doFilter(ServletRequest request, ServletResponse response)
        throws IOException, ServletException;
}
```

Spring Security makes extensive use of filters to implement various security features. The foundation of Spring Security is based on these filters. For example, if Spring Security needs to perform authentication based on a username and password, it delegates the request to a filter called UsernamePasswordAuthenticationFilter, which is responsible for authenticating the user based on the provided credentials. Similarly, for basic HTTP authentication, Spring Security uses BasicAuthenticationFilter.
