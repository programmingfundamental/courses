---
layout: default
title: Proxy
parent: Лабораторно упражнение 8
grand_parent: Обектно-ориентирано програмиране - 2 част
nav_order: 2
---

# Proxy

Прокси шаблонът предоставя контейнер за друг обект, който да контролира достъпа до него. Този модел се използва, когато искаме да осигурим контролиран достъп до функционалност.

**Осигурява заместител или контейнер за друг обект, за да контролирате достъпа до него.** Самата дефиниция е много ясна и моделът на прокси дизайн се използва, когато искаме да осигурим контролиран достъп до функционалност. Да кажем, че имаме клас, който може да изпълнява някаква команда в системата. Сега, ако го използваме, е добре, но ако искаме да дадем тази програма на клиентско приложение, тя може да има сериозни проблеми, защото клиентската програма може да издаде команда за изтриване на някои системни файлове или промяна на някои настройки, които не искате. Тук може да се създаде прокси клас, който да осигури контролиран достъп до програмата.

#### Модел за дизайн на прокси сървър - основен клас

Тъй като кодираме Java по отношение на интерфейси, тук е нашият интерфейс и неговият клас на изпълнение.

```
public interface CommandExecutor {

	public void runCommand(String cmd) throws Exception;
}
```

```
import java.io.IOException;

public class CommandExecutorImpl implements CommandExecutor {

	@Override
	public void runCommand(String cmd) throws IOException {
                //some heavy implementation
		Runtime.getRuntime().exec(cmd);
		System.out.println("'" + cmd + "' command executed.");
	}

}
```

#### Шаблон за дизайн на прокси сървър - прокси клас

Сега искаме да предоставим само администраторски потребители да имат пълен достъп до горния клас, ако потребителят не е администратор, тогава ще бъдат разрешени само ограничени команди. Ето нашата много проста реализация на прокси клас.

```
public class CommandExecutorProxy implements CommandExecutor {

	private boolean isAdmin;
	private CommandExecutor executor;
	
	public CommandExecutorProxy(String user, String pwd){
		if("Pankaj".equals(user) && "J@urnalD$v".equals(pwd)) isAdmin=true;
		executor = new CommandExecutorImpl();
	}
	
	@Override
	public void runCommand(String cmd) throws Exception {
		if(isAdmin){
			executor.runCommand(cmd);
		}else{
			if(cmd.trim().startsWith("rm")){
				throw new Exception("rm command is not allowed for non-admin users.");
			}else{
				executor.runCommand(cmd);
			}
		}
	}

}
```

```
public class Application {

	public static void main(String[] args){
		CommandExecutor executor = new CommandExecutorProxy("Pankaj", "wrong_pwd");
		try {
			executor.runCommand("ls -ltr");
			executor.runCommand(" rm -rf abc.pdf");
		} catch (Exception e) {
			System.out.println("Exception Message::"+e.getMessage());
		}
		
	}

}
```

Изходът на по-горе прокси дизайн модел примерна програма е:

```
'ls -ltr' command executed.
Exception Message::rm command is not allowed for non-admin users.
```

Общите приложения на прокси дизайна са за контрол на достъпа или за осигуряване на изпълнение на обвивката за по-добра производителност.

\
