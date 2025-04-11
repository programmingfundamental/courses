---
layout: default
title: JavaScript Object Notation
parent: Laboratory excercise 7
grand_parent: Internet Technologies
nav_order: 2
---


# JavaScript Object Notation

JSON is a text-based open standard designed for human-readable data exchange. JSON represents simple data structures and objects such as associative arrays. JSON is a language-independent specification, with parsers that can convert many other languages ​​to JSON.

###  JSON format

The JSON format is often used for serializing and transmitting structured data over an Internet connection. It is mainly used to transmit data between a server and a client.

* The media type for JSON is application/json.
* The extension of files written in JSON is .json.
* Data types
  * Number (floating-point number, double precision floating-point format in JavaScript)
  * String (a string of Unicode-encoded characters enclosed in double quotes, with "special" characters represented by so-called escaping - character sequences starting with the "\" character)
  * Boolean (true or false)
  * Array (an ordered series of values ​​separated by commas and enclosed in square brackets; the values ​​do not have to be of the same type)
  * Object (unordered collection of key:value pairs, the ":" symbol separates the key and value, separated by a comma and enclosed in curly braces; keys must be strings and distinct from each other)
  * null (empty)

**Example**:

```json
{
    "firstName": "John",
    "lastName": "Doe",
    "age": 25,
    "isStudent": true,
    "jobTitle": "student",
    "address": {
        "street": "21 2nd Street",
        "city": "New York"
    },
    "phone": "212 555-1234",
    "jobSkills": [
        {
            "name": "Java",
            "value": 10.0
        },
        {
            "name": "HTML",
            "value": 5.5
        }
    ],
    "extraInfo": null
}

```
