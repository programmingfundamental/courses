## Bank Lab — SQL injection, XSS, and identity-number protection

Use [Bank Lab from Study resources](/courses/en/applied-web-security/resources/) and sign in with `user` / `123456`. Work locally with fictional names and ten-digit identity numbers. Restart before each task from 1 to 4: the initial bank has six accounts, three owned by `user`. The application's interface is in Bulgarian.

### Task 1 — Owned accounts and generated IBANs

List your accounts without a filter, then test `Разплащателна`, `Спестовна`, and `Депозитна` separately. Create two accounts with the same fictional identity number, different account types, and plain-text reasons. Fill in all three names and the opening date; do not enter an IBAN.

**Expected:** three initial owned accounts and one result for each type; five owned accounts after creation. The two new IBANs differ and appear with the holder's names, date, and encrypted identity number.

**Submit:** a filter/result-count table, a screenshot of the new records, and the code locations that assign the owner and generate the IBAN.

### Task 2 — SQL injection and other owners' accounts

Start with `Спестовна`. Use the SQL demonstration in Study resources to change the query condition through **Вид сметка** and retrieve other owners' records. Restore the normal list with **Изчисти**. Also try an incomplete SQL input, followed by a valid filter.

**Expected:** one owned savings account normally, six bank accounts through injection, and three owned accounts after clearing. Invalid SQL produces an error; a subsequent valid filter works.

**Submit:** exact inputs, result counts, and a screenshot showing other holders. Identify the vulnerable line in `Accounts.java`; explain the resulting `WHERE`, `AND`/`OR` precedence, and why binding only the owner does not protect the entire filter.

### Task 3 — Stored XSS in the reason for opening

Create a fictional `Спестовна` account. Adapt the worked script so that displaying its reason changes the list heading to **XSS — учебен опит**. Observe the page after saving, after reloading, with the `Депозитна` filter, and after **Изчисти**. Keep the example limited to a visible page change.

**Expected:** the script runs whenever the stored record appears. Filtering out that record leaves the ordinary heading; clearing the filter includes the record and runs it again.

**Submit:** the HTML input, screenshots before and after reloading, and a table of the four observations. Trace the form-to-database-to-template path; explain stored XSS and the roles of `th:utext` and CSP.

### Task 4 — Recognize and decode the weak cipher

Choose two different ciphertexts from the initial records, using task 2 for access to other owners' accounts. Identify the algorithm from its visible format. Write a small Java program or script that accepts `ROT5:` followed by ten digits and recovers the fictional identity number. Reject an invalid prefix or length.

Create two accounts with the same fictional identity number and compare their ciphertexts. Explain what equality reveals before decryption and whether hiding the prefix would provide reliable protection.

**Expected:** decryption returns ten digits, preserves leading zeros, and rejects invalid formats. Identical input identity numbers have identical ciphertexts.

**Submit:** source code, a run command, two input ciphertexts and results, an invalid-input check, and a short explanation of the weaknesses.

### Task 5 — A protected version of Bank Lab

Work in a separate project copy. Make the filter treat account-type input as data and enforce the current owner's restriction for every result. Display the reason as text and restrict inline scripts through CSP.

Replace ROT5 with authenticated encryption using a separate random nonce for every encryption. Keep the key outside the source code and submission archive. Continue displaying ciphertext, without adding a public decrypted-identity-number field. Document local setup of a fresh practice key and handling of invalid or modified ciphertext.

**Expected:** normal filters work; the SQL input reveals no other owners' accounts; previously stored XSS is displayed literally without changing the page. Encrypting the same identity number with distinct nonces gives different representations that decrypt to the same original; ciphertext modification is rejected.

**Submit:** changed files, key-setup instructions without the key, and a before/after comparison for all three vulnerabilities.

### Task 6 — Regression checks and submission

In the protected copy, adapt tests that previously confirmed vulnerable behavior. Cover successful/failed login, ownership and filters, attempted SQL injection, unique generated IBANs, ownership derived from the session, literal rendering of stored XSS, and encryption/decryption with tamper rejection. Verify script execution in a browser as well: text appearing in an HTTP response alone does not prove whether JavaScript executes.

**Expected:** the protected build and checks pass while login, filtering, and account creation continue to work.

**Submit one ZIP:** protected source code and tests, a README with startup/configuration commands, a short report for tasks 1–6, and screenshots. Exclude real personal data, keys, local databases, `target/`, and downloaded dependencies. State your environment and the exact test command.
