---
title: "Study resources"
sidebar:
  order: 0
---

## Bank Lab — a practice account register

[Download the Bank Lab project (ZIP)](/courses/downloads/bank-demo.zip)

The archive contains a runnable Spring Boot application, its source code, and startup scripts. You need Java 17 or later. The interface and the exercises in the bundled README are in Bulgarian.

### Start the application

1. Extract the archive and open a terminal in the `bank-demo` folder.
2. Run `.\start.cmd` on Windows or `sh start.sh` on Linux or macOS.
3. When the terminal shows `Started BankApplication`, open `http://127.0.0.1:8087/login`. Use `admin` for both the username and the password.

### Try the three demonstrations

| Risk | What to try | What to look for |
| --- | --- | --- |
| SQL injection | On the login page, select **Променено условие** (altered condition). | The simulated query accepts the record even though the password check rejects it. Sign in with the practice account to open the register. |
| Stored XSS | Under **Нова сметка**, select **Форматиране**, create the account, and open its details. | One panel displays the literal note `<b>Месечен отчет</b>`. The simulation displays the same saved words in bold. |
| Weak encryption | Compare the stored ciphertext with the known practice IBAN prefix `BG00DEMO`. Try a reverse shift under **Обратно отместване** and select **Провери**. | The shift that recovers the prefix also decodes the fictional identity number. |

Stop the application with `Ctrl+C`. Restarting it restores the three original accounts.
