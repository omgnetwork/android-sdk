# OmiseGO Android SDK

The [OmiseGO](https://omisego.network) Android SDK allows developers to easily interact with a node of the OmiseGO eWallet.


# Table of Contents

- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
  - [Initialization](#initialization)
  - [Retrieving resources](#retrieving-resources)
    - [Get the current user](#get-the-current-user)
    - [Get the addresses of the current user](#get-the-addresses-of-the-current-user)
    - [Get the provider settings](#get-the-provider-settings)
    - [Get the current user's transactions](#get-the-current-users-transactions)
  - [QR codes](#qr-codes)
    - [Generation](#generation)
- [Test](#test)
- [Contributing](#contributing)
- [License](#license)

## Requirements

- Minimum Android SDK version 19

## Installation

To use the OmiseGO SDK in your android project, simply add the following line in the module's build.gradle
 
```groovy
dependencies {
    implementation 'co.omisego:omisego-sdk:0.9.1'
}
```

## Usage

### Initialization

Before using the SDK to retrieve a resource, you need to initialize the client (`OMGApiClient`) with a builder (`OMGApiClient.builder`).
You should to this as soon as you obtain a valid authentication token corresponding to the current user from the Wallet API.

```kotlin
 val token = EncryptionHelper.encryptBase64(apiKey, authToken)
 
 val eWalletClient = EWalletClient.Builder {
     baseURL = baseURL
     authenticationToken = token
     debug = true // Print request and response log
 }.build()
 
 val omgAPIClient = OMGApiClient(eWalletClient)
```

Where:
`apiKey` is the api key generated from your OmiseGO admin panel.
`authenticationToken` is the token corresponding to an OmiseGO Wallet user retrievable using one of our server-side SDKs.
`baseURL` is the URL of the OmiseGO Wallet API.
> You can find more info on how to retrieve this token in the OmiseGO server SDK documentations.

## Retrieving resources

Once you have an initialized client, you can retrieve different resources.
Each call takes a `OMGCallback` interface that returns a `OMGResponse` object:

```kotlin
interface OMGCallback<in T> {
    fun success(response: OMGResponse<T>)
    fun fail(response: OMGResponse<ApiError>)
}
```

```kotlin
data class OMGResponse<out T>(val version: String, val success: Boolean, val data: T)

data class ApiError(val code: ErrorCode, val description: String)
```

Where:

`success` is the function invoked when the `success` boolean in the response is `true`. This function will provide the corresponding data model to an API endpoint.

`fail` is the function invoked when the `success` boolean in the response is `false`. This function will provide the `ApiError` object which contains informations about the failure.

### Get the current user

```kotlin
omgAPIClient.getCurrentUser().enqueue(object: OMGCallback<User>{
    override fun fail(response: OMGResponse<APIError>) {
        
    }

    override fun success(response: OMGResponse<User>) {
        
    }
})
```

### Get the addresses of the current user

```kotlin
omgAPIClient.listBalances().enqueue(object: OMGCallback<BalanceList>{
    override fun fail(response: OMGResponse<APIError>) {
        
    }

    override fun success(response: OMGResponse<BalanceList>) {
        
    }
})
```

> Note: For now a user will have only one address.

### Get the provider settings

```kotlin
omgAPIClient.getSettings().enqueue(object: OMGCallback<Setting>{
    override fun fail(response: OMGResponse<APIError>) {
        
    }

    override fun success(response: OMGResponse<Setting>) {
        
    }
})
```

### Get the current user's transactions
This returns a paginated filtered list of transactions.

In order to get this list you will need to create a `TransactionListParams` object:

```kotlin
val request = TransactionListParams.create(
    page = 1,
    perPage = 10,
    sortBy = Paginable.Transaction.SortableFields.CREATE_AT,
    sortDirection = SortDirection.ASCENDING,
    searchTerm = "confirmed", // or searchTerms = mapOf(STATUS to "completed")
    address = null
)
```

Where

* `page` is the page you wist to receive
* `perPage` is the number of results per page
* `sortBy` is the sorting field. The available values are:

    `ID`, `STATUS`, `FROM`, `TO`, `CREATED_AT`, `UPDATED_AT`
    
    > `import co.omisego.omisego.model.pagination.Paginable.Transaction.SortableFields.*`
    
* `sortDirection` is the sorting direction. The available values are:
    
    `ASCENDING`, `DESCENDING`
    
    > `import co.omisego.omisego.model.pagination.SortDirection.*`
    
* `searchTerm` *(optional)* is a term to search for all of the searchable fields. 
      Conflict with `searchTerms`, only use one of them. The available values are:
    
    `ID`, `STATUS`, `FROM`, `TO`, `CREATED_AT`, `UPDATED_AT`
      
    > `import co.omisego.omisego.model.pagination.Paginable.Transaction.SearchableFields.*`
    
* `searchTerms` *(optional)* is a key-value map of fields to search with the available fields (same as `searchTerm`)
    For example:
    
    ```kotlin
    mapOf(FROM to "some_address", ID to "some_id")
    ```

* `address` *(optional)* is an optional address that belongs to the current user (primary address by default)

Then you can call:

```kotlin
omgAPIClient.listTransactions(request).enqueue(object: OMGCallback<PaginationList<Transaction>>{
    override fun fail(response: OMGResponse<APIError>) {
        //TODO: Handle the error
    }

    override fun success(response: OMGResponse<PaginationList<Transaction>>) {
        //TODO: Do something with the paginated list of transactions
    }
})
```
   
There is `PaginationList<Transaction>` inside the `response.data` which contains `data: List<Transaction>` and `pagination: Pagination`

Where:
* `data` is an array of transactions
* `pagination` is a `Pagination` object
    
    Where:
    * `perPage` is the number of results per page.
    * `currentPage` is the retrieved page.
    * `isFirstPage` is a bool indicating if the page received is the first page
    * `isLastPage` is a bool indicating if the page received is the last page

## QR Codes
This SDK offers the possibility to generate and consume transaction requests. Typically these actions should be done through the generation and scan of QR codes.

### Generation
To generate a new transaction request you can call:

```kotlin
val request = TransactionRequestCreateParams(
    type = TransactionRequestType.RECEIVE,
    tokenId = "a_token_id",
    amount = 10.24
    address = "receiver_address"
    correlationId = "correlation_id"
)

omgAPIClient.createTransactionRequest(request).enqueue(object : OMGCallback<TransactionRequest> {
    override fun success(response: OMGResponse<TransactionRequest>) {
        //TODO: Do something with the transaction request (get the QR code representation for example)
        val qrBitmap = response.data.generateQRCode(512)) // Generate the QR bitmap
    }

    override fun fail(response: OMGResponse<APIError>) {
        //TODO: Handle the error
    }
})
```

Where:
* `request` is a `TransactionRequestCreateParams` data class constructed using:
    * `type`: The QR code type, only supports `TransactionRequestType.RECEIVE` for now.
    * `tokenId`: The id of the desired token.
    * `amount`: (optional) The amount of token to receive. This amount can be either inputted when generating or consuming a transaction request.
    * `address`: (optional) The address specifying where the transaction should be sent to. If not specified, the current user's primary address will be used.
    * `correlationId`: (optional) An id that can uniquely identify a transaction. Typically an order id from a provider.

A `TransactionRequest` object is passed to the success callback, you can get its QR code representation using `transactionRequest.generateQRCode(size)`.

# Run Kotlin Lint
Simply run `./gradlew ktlintCheck` under project root directory.

# Test
In order to run the live tests (bound to a working server) you need to fill the corresponding in the file `src/test/resources/secret.json`. 
> Note : You can see the reference in the file `secret.example.json`

The variables are:

* `base_url`
* `api_key`
* `auth_token`
* `access_key`
* `secret_key`

You can then run the test under the `src/test` folder from the Android Studio or run the command `./gradlew test`.

# Contributing

See [how you can help](.github/CONTRIBUTING.md).

## License

The OmiseGO Android SDK is released under the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).

## Sample Project

You can check out the latest sample app from the following repo : [OMGShop](https://github.com/omisego/sample-android)