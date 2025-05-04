---
layout: default
title: JSON Web Token
parent: Laboratory excercise 12
grand_parent: Internet Technologies
nav_order: 1
---

# JSON Web Token

JSON Web Token (JWT) is an open standard that defines a compact and self-contained way to securely transmit information between parties in the form of a JSON object. This information can be verified and trusted because it is digitally signed. JWTs can be signed using a secret (with the HMAC algorithm) or a public/private key pair using RSA or ECDSA.

### Usage

Here are some scenarios where JSON web tokens are useful:

· Authorization: This is the most common scenario for using JWT. Once a user has logged in, each subsequent request will include a JWT, allowing the user to access routes, services, and resources that are authorized with that token. Single sign-on is a feature that makes widespread use of JWT today, due to its small resource requirement and the ability to be easily used across domains.

· Information exchange: JSON web tokens are a good way to securely transmit information between parties. Since JWTs can be signed—for example, using public/private key pairs—you can be sure that the senders are who they say they are. Additionally, since the signature is computed using the header and payload of the message, you can also verify that the message has not been tampered with.

### JSON Web Token structure

In its compact form, JSON web tokens consist of three parts, separated by periods (.), which are:

· Header;

· Payload;

· Signature.

Therefore, a JWT typically looks like this.

xxxxx.yyyyy.zzzzz

#### Header

The header usually consists of two parts: the token type, which is a JWT, and the signing algorithm used, such as HMAC SHA256 or RSA. For example:

```json
{
	"alg": "HS256",
  	"typ": "JWT"
} 
```

This JSON is Base64Url encoded to form the first part of the JWT.

#### Payload

The second part of the token is the payload, which contains claims. Claims are statements about an entity (usually a user) and additional data. There are three types of claims: registered, public, and private claims.

· Registered claims: These are a set of predefined claims that are not required, but are recommended to provide a set of useful, interoperable claims. Some of them are: iss (issuer), exp (expiration time), sub (subject), aud (audience), and others. Note that claim names are only three characters long, as JWT is intended to be compact.

· Public claims: These can be defined at will by those using JWT. However, to avoid collisions, they must be defined in the IANA JSON Web Token Registry or defined as a URI that contains a collision-resistant namespace.

· Private claims: These are custom claims created to share information between parties who agree to use them, and are neither registered nor public claims. An example payload could be:

```json
{
  "sub": "1234567890",
  "name": "John Doe",
  "admin": true
}
```

The payload is then Base64Url encoded to form the second part of the JSON Web Token. Note that for signed tokens, this information, although tamper-proof, can be read by anyone. Do not put any secret information in the payload or header of the JWT unless it is encrypted.

#### Signature

To create the signature part, you need to take the encrypted header, encrypted payload, secret key, the algorithm specified in the header, and sign it. For example, if you want to use the HMAC SHA256 algorithm, the signature would be created like this:

```java
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret)
```

The signature is used to verify that the message has not been altered along the way, and in the case of tokens signed with a private key, it can also verify that the sender of the JWT is who they claim to be.

### All Together

The result is three Base64-URL strings separated by periods that can be easily passed in HTML and HTTP environments, while being more compact compared to XML-based standards such as SAML. The following shows a JWT containing the encoded header and payload shown above, signed with a secret.

<figure><img src="../../../assets/image (156).png" alt=""><figcaption></figcaption></figure>

### How do JSON web tokens work?

During authentication, when a user successfully logs in using their credentials, a JSON web token will be returned. Since tokens are credentials, great care must be taken to prevent security issues. In general, you should not store tokens longer than necessary. You should also not store sensitive session data in the browser's storage due to lack of security. Whenever a user wants to access a secure route or resource, the user agent must send a JWT, typically in the Authorization header, using the **Bearer** schema. The content of the header should look like this:

```
Authorization: Bearer <token>
```

<figure><img src="../../../assets/image (158).png" alt=""><figcaption></figcaption></figure>

In certain cases, this can be a stateless authorization mechanism. Secure routes on the server will check for a valid JWT in the Authorization header, and if it is present, the user will be allowed to access the protected resources. Note that if you are sending JWT tokens via HTTP headers, you should try to prevent their size from increasing. Some servers do not accept more than 8 KB in the headers.
