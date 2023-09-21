# Servlet Filter

Сървлет филтрите са Java класове, които могат да бъдат използвани в Java web програмирането за следните цели :

* За прихващане на заявки от клиент преди да му се даде достъп до ресурсите в системата;
* За манипулация на отговорите от сървъра преди те да бъдат изпратени обратно на клиента.

**Видове филтри:**

* филтри за аутентикация;
* филтри за компресия на данни;
* филтри за кодиране и декодиране;
* филтри, които управляват достъпа до ресурси;
* филтри за преобразуване на изображения;
* филтри за управление на достъпа до системата;
* верига от филтри за MIME - TYPE;
* Филтри за токенайзер (обработка на стрингови низове с цел разделяне на поднизове);
* XSL / Т филтри, които трансформират XML съдържание. За да станат активни и за да се окаже спрямо кой ресурс от уеб приложението те ще работят

**Деклариране на филтри:**

* Единият начин за декларация е запис в дескриптора на приложението - web.xml, където се асоциират с дадено сървлет име и URL адрес.
* Вторият начин за декларация е чрез анотацията @WebFilter

Когато уеб контейнера стартира уеб приложението, той създава инстанция на всеки филтър, който е деклариран в дескриптора. Филтрите се изпълняват в реда, в който са декларирани.

**Методи на сървлет филтър**

Филтърът е Java клас, който реализира интерфейса javax.servlet.Filter.

Интерфейсът `javax.servlet.Filter` дефинира методите:

* `public void doFilter (ServletRequest, ServletResponse, FilterChain)` - Този метод се извиква от контейнера всеки път при обработка на request/response, в който се изисква достъп до ресурс от клиент;
* `public void init(FilterConfig filterConfig)` - Този метод се извиква от уеб контейнера, за да се окаже на филтъра, че е вече активен;
* `public void destroy()` - Този метод се извиква от уеб контейнера, за да се окаже на филтъра, че вече не е активен.

**Пример:**

Servlet филтър, отпечатващ IP адреса на клиенти и текущия час дата:

```
@WebFilter(urlPatterns = "/CheckBox")
public class LogFilter implements Filter {
	public void init(FilterConfig fConfig) throws ServletException {
        String testParam = fConfig.getInitParameter("test-param");
        System.out.println("Test Param: "+ testParam);
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String ipAddress = request.getRemoteAddr();
        System.out.println("IP "+ipAddress + ", Time " + new Date().toString());
        chain.doFilter(request, response);
    }
}
```

Анотацията @WebFilter декларира класа, който го съдържа, като сървлет филтър. Параметърът на анотацията - urlPatterns = "/CheckBox") оказва името на сървлета, за който ще бъде приложен филтъра. В зависимост от броя и типа параметри на анотацията има различни случаи на използване на филтрите:

* филтър работещ за няколко ресурса

```
@WebFilter(servletNames = {"MyOwnServlet", "UploadServlet"})
public class MyFilter implements Filter {
...
}
```

* филтър с инициализиращи параметри

```
@WebFilter(
 urlPatterns = "/uploadFilter",
 initParams = @WebInitParam(name = "fileTypes", value =
"doc;xls;zip;txt;jpg;png;gif")
)
public class UploadFilter implements Filter {
 ...
}
```

* филтър с допълнителна информация

```
@WebFilter(
 urlPatterns = "/admin/*",
 filterName = "AdminFilter",
 description = "Filter all admin URLs"
)
public class MyFilter implements Filter {
 ...
}
```

* филтър с оказване типа на диспечера на обработката

```
@WebFilter(
 urlPatterns = "/admin",
 dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD}
)
public class MyFilter implements Filter {
 ...
}
```
