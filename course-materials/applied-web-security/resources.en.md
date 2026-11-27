## Bank Lab — a practice bank

[Download Bank Lab (ZIP)](/courses/downloads/bank-demo.zip?v=2026-11-27)

**Package version: 2026-11-27.** The archive includes a runnable Spring Boot application, source code, tests, instructions, and assignments in `tasks.md` and `tasks.en.md`. Run it locally with Java 17 or later and use fictional data for the SQL injection, stored XSS, and ROT5 demonstrations. The application interface is in Bulgarian.

[Open Tasks — Bank Lab](/courses/en/applied-web-security/resources/zadachi/)

### Theory

**SQL injection** occurs when input becomes part of a query's syntax. Bank Lab's account-type filter allows changes to the selection condition that bypass the owner restriction.

**Stored XSS** occurs when saved user input is rendered as active HTML. The opening reason is stored in the database, and its injected JavaScript executes when the account appears in the list.

**ROT5** rotates decimal digits by a fixed amount. It is easily reversible, has no secret key, and produces identical ciphertext for identical input. The `ROT5:` prefix in this project identifies the method.

### Start and sign in

Extract the complete archive into a new folder. In `bank-demo`, run `.\start.cmd` on Windows or `sh start.sh` on Linux/macOS. After `Started BankApplication`, open `http://127.0.0.1:8087/login`. Sign in with **user** and **123456**.

The in-memory database starts with six accounts: three owned by `user` and three owned by another profile. Stop with `Ctrl+C`; restarting restores those six records and discards records added during the previous run.

### Worked example

Using a freshly started application, observe how filtering exposes other owners' accounts, how a stored reason changes the page, and how weak encryption allows recovery of an identity number.

#### 1. Account filtering and SQL injection

You initially see three owned accounts, including the holder's full name, account type, IBAN, opening date, encrypted identity number, and reason for opening. Enter `Спестовна` in **Вид сметка** to see one savings account. Replace the filter with the following text and press **Изведи сметките ми**.

```text
' OR '1'='1
```

All six initial accounts are returned, including other owners' records. **Изчисти** restores the owned-account list.

The resulting condition is `WHERE owner_login = ? AND account_type = '' OR '1'='1'`. The bound owner parameter remains, but the always-true condition after `OR` admits every account.

#### 2. Create an account and observe stored XSS

Open **Откриване на сметка**. Enter `Елена`, `Петрова`, and `Учебна` as the three names, `0000001004` as the fictional identity number, `Спестовна` as the type, and today's date. The server generates the IBAN. Enter this example in **Основание за откриване**.

```html
<script>document.querySelector('h1').textContent='XSS DEMO';</script>
```

After **Открий сметката**, the list shows four owned accounts and its heading changes to **XSS DEMO**. Reload to observe the stored script execute again. Filtering by `Депозитна` excludes the new record and leaves the ordinary heading; **Изчисти** includes it and runs the script again.

#### 3. Decode the identity number

The value `0000001004` is stored as `ROT5:5555556559`. The prefix identifies the algorithm. Rotate each digit by five modulo ten to recover the original value.

### Independent work

Continue to the separate [Bank Lab Tasks page](/courses/en/applied-web-security/resources/zadachi/). Its six assignments cover owned accounts and IBANs, SQL injection, stored XSS, decoding ROT5, a protected version, and automated checks. Each gives initial conditions, expected behavior, and submission requirements.

The archive's README contains commands and a code map; `tasks.md` and `tasks.en.md` provide the same assignments offline. To modify Java code, use JDK 17+, build with `.\mvnw.cmd package`, and run the updated result with `java -jar target/bank-demo.jar`. The packaged `start.cmd` launches the bundled `app/bank-demo.jar`.
