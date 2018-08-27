<img src="assets/logo.png" align="right" />

# OmiseGO Android SDK

[![Build Status](https://travis-ci.org/omisego/android-sdk.svg?branch=master)](https://travis-ci.org/omisego/android-sdk)
[![Download](https://api.bintray.com/packages/omise-go/maven/omisego-sdk/images/download.svg) ](https://bintray.com/omise-go/maven/omisego-sdk/_latestVersion)

The [OmiseGO](https://omisego.network) Android SDK allows developers to easily interact with a node of the OmiseGO eWallet. This SDK is split into 3 modules : [Core](omisego-core), [Client](omisego-client) and [Admin](omisego-admin). The `Core` is a common dependency that contains the shared logic. The `Client` and `Admin` modules contain specific logic to respectively access the client or admin API. 

# Table of Contents

- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
  - [Client](omisego-client)
  - [Admin](omisego-admin)
- [Lint](#lint)
- [Tests](#tests)
- [Contributing](#contributing)
- [License](#license)

## Requirements

- Minimum Android SDK version 19

## Installation

If you only need to integrate and support the `Client` API, add the following line in the module's `build.gradle`

```
dependencies {
    implementation 'co.omisego:omisego-client:<latest-sdk-version>'
}
```

Or if you want to support the `Admin` API, you can use:

```
dependencies {
    implementation 'co.omisego:omisego-admin:<latest-sdk-version>'
}
```

## Usage

[Client API Usage](omisego-client)

[Admin API Usage](omisego-admin)

## Lint

Simply run `./gradlew ktlintCheck` under the project root directory.

## Tests

### Unit Test

#### Running all tests
```bash
./gradlew clean test
```

#### Running tests for `omisego-core` module

```bash
./gradlew omisego-core:test -Plive=false
```

#### Running tests for `omisego-client` module

```bash
./gradlew omisego-client:test -Plive=false
```

#### Running tests for `omisego-admin` module

```bash
./gradlew omisego-admin:test -Plive=false
```

### Live Test

In order to run the live tests (bound to a working server), you need to create a file `secret.json` under `src/liveTest/resources/` directory in the `omisego-client` module (You can take a template from `secret.example.json` there).

The `secret.json` file will be using the following format which is the same as the `secret.example.json` file.

```json
{
  "base_url": "YOUR_BASE_URL",
  "socket_base_url": "YOUR_SOCKET_BASE_URL",
  "api_key": "YOUR_API_KEY",
  "auth_token": "YOUR_AUTH_TOKEN"
}
```

You will need to fill the corresponding variables, then runs the following command to execute the `client` live test.

```bash
./gradlew omisego-client:test -Plive=true
```

Or if you want to execute the `admin` live test.

```bash
./gradlew omisego-admin:test -Plive=true
```

## Contributing

See [how you can help](.github/CONTRIBUTING.md).

## License

The OmiseGO Android SDK is released under the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).

## Sample Project

You can check out the latest sample app from the following repo : [OMGShop](https://github.com/omisego/sample-android