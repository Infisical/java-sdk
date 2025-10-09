<h1 align="center">
  <img width="300" src="/img/logoname-white.svg#gh-dark-mode-only" alt="infisical">
</h1>
<p align="center">
  <p align="center"><b>Infisical Java SDK</b></p>
<h4 align="center">
|
  <a href="https://infisical.com/docs/sdks/languages/java">Documentation</a> |
  <a href="https://www.infisical.com">Website</a> |
  <a href="https://infisical.com/slack">Slack</a> |
</h4>

<h4 align="center">
  <a href="https://github.com/Infisical/java-sdk/blob/main/LICENSE">
    <img src="https://img.shields.io/badge/license-MIT-blue.svg" alt="Infisical SDK's are released under the MIT license." />
  </a>
  <a href="https://infisical.com/slack">
    <img src="https://img.shields.io/badge/chat-on%20Slack-blueviolet" alt="Slack community channel" />
  </a>
  <a href="https://twitter.com/infisical">
    <img src="https://img.shields.io/twitter/follow/infisical?label=Follow" alt="Infisical Twitter" />
  </a>
</h4>

## Introduction

**[Infisical](https://infisical.com)** is the open source secret management platform that teams use to centralize their secrets like API keys, database credentials, and configurations.

If you’re working with Java, the official Infisical Java SDK package is the easiest way to fetch and work with secrets for your application. You can read the documentation [here](https://infisical.com/docs/sdks/languages/java).

## Documentation
You can find the documentation for the Java SDK on our [SDK documentation page](https://infisical.com/docs/sdks/languages/java).

## Directory Structure
Following is the directory structure of key directory ```src``` in this SDK :

```text
|
└── src/
    ├── main/
    │   ├── java/com/infisical/sdk
    │   |    ├── api    => Actually calls the HTTPs methods (GET, POST, etc.)
    |   |    ├── auth   => (AwsAuthProvider class with it's methods)
    |   |    ├── config => (Just stores the url where the infisical instance exists, "app.infisical.com")
    |   |    ├── models => (Contains data classes/models, auto getters, setters & constructors)
    |   |    ├── resources => (Contains methods users sees/interacts mostly with,
    |   |    |               This is also where auth methods are, UniversalAuth LdapAuthLogin)
    |   |    ├── util   => (Nothing serious, just checks if is string’s null/empty, converts map to object etc)
    |   |    └── InfisicalSdk.java  =>  (Wrapper class for all of above classes but uses only most useful ones.)
    |   |
    |   └── resources
    |        └── logback.xml
    |
    └── test/java/com/infisical/sdk
        ├── auth
        ├── util
        └── InfisicalSdkTest.java
```
## Security

Please do not file GitHub issues or post on our public forum for security vulnerabilities, as they are public!

Infisical takes security issues very seriously. If you have any concerns about Infisical or believe you have uncovered a vulnerability, please get in touch via the e-mail address security@infisical.com. In the message, try to provide a description of the issue and ideally a way of reproducing it. The security team will get back to you as soon as possible.

Note that this security address should be used only for undisclosed vulnerabilities. Please report any security problems to us before disclosing it publicly.
