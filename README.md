# Infisical Java SDK

The Infisical SDK provides a convenient way to interact with the Infisical API. 

## Installation

Replace `{version}` with the version of the SDK you wish to use. This documentation covers version >=3.0.0.

### Maven

```xml
<dependency>
    <groupId>com.infisical</groupId>
    <artifactId>sdk</artifactId>
    <version>{version}</version>
</dependency>
```

### Gradle
```gradle
  implementation group: 'com.infisical', name: 'sdk', version: '{version}'
```


### Others
For other build tools, please check our [package snippets](https://central.sonatype.com/artifact/com.infisical/sdk), and select the build tool you're using for your project.



## Getting Started

```java
package com.example.example;

import com.infisical.sdk.InfisicalSdk;

public class Example {

  public static void main(String[] args) {
    var sdk = new InfisicalSdk(
      new SdkConfig.Builder()
        // Optional, will default to https://app.infisical.com
        .withSiteUrl("https://your-infisical-instance.com")
        .build()
    );

    sdk.Auth().UniversalAuthLogin(
      "CLIENT_ID",
      "CLIENT_SECRET"
    );

    var secret = sdk.Secrets().GetSecret(
      "<secret-name>",
      "<project-id>",
      "<env-slug>",
      "<secret-path>",
      null, // Expand Secret References (boolean, optional)
      null, // Include Imports (boolean, optional)
      null  // Secret Type (shared/personal, defaults to shared, optional)
      );


    System.out.println(secret);
  }
}
```

## Core Methods

The SDK methods are organized into the following high-level categories:

1. `Auth()`: Handles authentication methods.
2. `Secrets()`: Manages CRUD operations for secrets.

### `Auth`

The `Auth` component provides methods for authentication:

### Universal Auth

#### Authenticating
```java
sdk.Auth().UniversalAuthLogin(
  "CLIENT_ID",
  "CLIENT_SECRET"
);
```


**Parameters:**
- `clientId` (string): The client ID of your Machine Identity.
- `clientSecret` (string): The client secret of your Machine Identity.

### Access Token Auth

#### Authenticating
```java
sdk.Auth().SetAccessToken("ACCESS_TOKEN");
```

**Parameters:**
- `accessToken` (string): The access token you want to use for authentication.

### `Secrets`

This sub-class handles operations related to secrets:

#### List Secrets

```java
public List<Secret> ListSecrets(
    String projectId,
    String environmentSlug,
    String secretPath,
    Boolean expandSecretReferences,
    Boolean recursive,
    Boolean includeImports
)

throws InfisicalException
```

```java
List<Secret> secrets = await sdk.Secrets().ListSecrets(
  "<project-id>",
  "<env-slug>", // dev, prod, staging, etc.
  "/secret/path", // `/` is the root folder
  false, // Should expand secret references
  false, // Should get secrets recursively from sub folders
  false, // Should include imports
);
```

**Parameters:**
- `projectId` (string): The ID of your project.
- `environmentSlug` (string): The environment in which to list secrets (e.g., "dev").
- `secretPath` (string): The path to the secrets.
- `expandSecretReferences` (boolean): Whether to expand secret references.
- `recursive` (boolean): Whether to list secrets recursively.
- `includeImports` (boolean): Whether to include imported secrets.

**Returns:**
- `List<Secret>`: The response containing the list of secrets.

#### Create Secret


```java
public Secret CreateSecret(
    String secretName,
    String secretValue,
    String projectId,
    String environmentSlug,
    String secretPath
)
throws InfisicalException
```

```java
Secret newSecret = sdk.Secrets().CreateSecret(
  "NEW_SECRET_NAME",
  "secret-value",
  "<project-id>",
  "<env-slug>", // dev, prod, staging, etc.
  "/secret/path", // `/` is the root folder
);
```

**Parameters:**
- `secretName` (string): The name of the secret to create
- `secretValue` (string): The value of the secret.
- `projectId` (string): The ID of your project.
- `environmentSlug` (string): The environment in which to create the secret.
- `secretPath` (string, optional): The path to the secret.

**Returns:**
- `Secret`: The created secret.

#### Update Secret

```java
public Secret UpdateSecret(
    String secretName,
    String projectId,
    String environmentSlug,
    String secretPath,
    String newSecretValue,
    String newSecretName
  )
    
throws InfisicalException
```


```java
Secret updatedSecret = sdk.Secrets().UpdateSecret(
  "SECRET_NAME",
  "<project-id>",
  "<env-slug>", // dev, prod, staging, etc.
  "/secret/path", // `/` is the root folder
  "NEW_SECRET_VALUE", // nullable
  "NEW_SECRET_NAME" // nullable
);
```

**Parameters:**
- `secretName` (string): The name of the secret to update.`
- `projectId` (string): The ID of your project.
- `environmentSlug` (string): The environment in which to update the secret.
- `secretPath` (string): The path to the secret.
- `newSecretValue` (string, nullable): The new value of the secret.
- `newSecretName` (string, nullable): A new name for the secret.

**Returns:**
- `Secret`: The updated secret.

#### Get Secret by Name

```java
public Secret GetSecret(
    String secretName,
    String projectId,
    String environmentSlug,
    String secretPath,
    Boolean expandSecretReferences,
    Boolean includeImports,
    String secretType
  )
throws InfisicalException
```

```java
Secret secret = sdk.Secrets().GetSecret(
  "SECRET_NAME",
  "<project-id>",
  "<env-slug>", // dev, prod, staging, etc.
  "/secret/path", // `/` is the root folder
  false, // Should expand secret references
  false, // Should get secrets recursively from sub folders
  false, // Should include imports
  "shared" // Optional Secret Type (defaults to "shared")
);
```

**Parameters:**
- `secretName` (string): The name of the secret to get`
- `projectId` (string): The ID of your project.
- `environmentSlug` (string): The environment in which to retrieve the secret.
- `secretPath` (string): The path to the secret.
- `expandSecretReferences` (boolean, optional): Whether to expand secret references.
- `includeImports` (boolean, optional): Whether to include imported secrets.
- `secretType` (personal | shared, optional): The type of secret to fetch.


**Returns:**
- `Secret`: The fetched secret.

#### Delete Secret by Name

```java
public Secret DeleteSecret(
    String secretName,
    String projectId,
    String environmentSlug,
    String secretPath
  )
throws InfisicalException
```

```java
Secret deletedSecret = sdk.Secrets().DeleteSecret(
  "SECRET_NAME", 
  "<project-id>",
  "<env-slug>", // dev, prod, staging, etc.
  "/secret/path", // `/` is the root folder
);
```

**Parameters:**
- `secretName` (string): The name of the secret to delete.
- `projectId` (string): The ID of your project.
- `environmentSlug` (string): The environment in which to delete the secret.
- `secretPath` (string, optional): The path to the secret.

**Returns:**
- `Secret`: The deleted secret.

