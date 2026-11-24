CREATE TABLE bank_users (
  login VARCHAR(30) PRIMARY KEY,
  password_hash VARCHAR(80) NOT NULL
);
CREATE SEQUENCE iban_number START WITH 1;
CREATE TABLE accounts (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  owner_login VARCHAR(30) NOT NULL REFERENCES bank_users(login),
  first_name VARCHAR(50) NOT NULL,
  middle_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  account_type VARCHAR(30) NOT NULL,
  iban VARCHAR(22) UNIQUE NOT NULL,
  opened_on DATE NOT NULL,
  egn_cipher VARCHAR(15) NOT NULL,
  reason VARCHAR(2000) NOT NULL
);
