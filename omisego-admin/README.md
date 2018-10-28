# OmiseGO Admin Android SDK

The Admin Android SDK allows developers to easily interact with the [OmiseGO Admin eWallet API](https://ewallet.staging.omisego.io/api/admin/docs.ui).

## Table of Contents
- [Initialization](#initialization)
- [Retrieving resources](#retrieving-resources)
  - [Login](#login)
  - [Transferring Token](#transferring-token)
  - [PaginationList](#paginationlist)
    - [Get transaction list](#get-transaction-list)
    - [Get account list](#get-account-list)
    - [Get token list](#get-token-list)
    - [Get account's wallet list](#get-accounts-wallet-list)
    - [Get user's wallet list](#get-users-wallet-list)

## Initialization

Before using the SDK to retrieve a resource, you need to initialize the `EWalletAdmin` with an `AdminConfiguration` object.

Then you need to pass it to the `EWalletAdmin.Builder` and call `build()` to get the `EWalletAdmin` instance.

Lastly, you will need to pass the instance that you got from the previous step to the `OMGAPIAdmin`'s constructor.

For example,
```kotlin
 val config = AdminConfiguration(baseURL = "YOUR_BASE_URL")

 val eWalletAdmin = EWalletAdmin.Builder {
     clientConfiguration = config
     debug = false
 }.build()
 
 val omgAPIAdmin = OMGAPIAdmin(eWalletAdmin)
```

Where:
* `baseURL` is the URL of the OmiseGO Wallet API. **Note**: This need to be ended with '/'.
* `debug` *(Optional)* is a boolean indicating if the SDK should print logs in the console. default: `false`. 

## Retrieving resources

Once you have an `OMGAPIAdmin` object from the [initialization section](#initialization), you can retrieve different resources.
Every call takes an `OMGCallback` interface that returns an `OMGResponse` object:

```kotlin
interface OMGCallback<in T> {
    fun success(response: OMGResponse<T>)
    fun fail(response: OMGResponse<APIError>)
}
```

```kotlin
data class OMGResponse<out T>(val version: String, val success: Boolean, val data: T)

data class APIError(val code: ErrorCode, val description: String)
```

Where:

`success` is the function invoked when the `success` boolean in the response is `true`. This function will provide the corresponding data model to an API endpoint.

`fail` is the function invoked when the `success` boolean in the response is `false`. This function will provide the `APIError` object which contains information about the failure.

### Login

```kotlin
val params = LoginParams(
    "john.doe@omise.co",
    "password"
)

omgAPIAdmin.login(params).enqueue(object: OMGCallback<User>{
    override fun fail(response: OMGResponse<APIError>) {
        
    }

    override fun success(response: OMGResponse<AuthenticationToken>) {
        
    }
})
```

> Note: The `Authentication` header will be set automatically when logging successfully.

### Transferring token

#### Create a transaction

```kotlin
val params = TransactionCreateParams(
    fromAddress = "1e3982f5-4a27-498d-a91b-7bb2e2a8d3d1",
    toAddress = "2e3982f5-4a27-498d-a91b-7bb2e2a8d3d1",
    amount = 1000.bd,
    tokenId = "BTC:xe3982f5-4a27-498d-a91b-7bb2e2a8d3d1",
    idempotencyToken = "some token"
)

omgAPIClient.createTransaction(request).enqueue(object : OMGCallback<Transaction>{
    override fun success(response: OMGResponse<Transaction>) {
        // Do something
    }

    override fun fail(response: OMGResponse<APIError>) {
        // Handle error
    }
})
```

There are different ways to initialize a `TransactionCreateParams` by specifying either `toAddress`, `toAccountId` or `toProviderUserId`.

### Generate a transaction request

A more configurable way to transfer tokens between 2 wallets is to use the transaction request flow. 

To make a transaction happen, a `TransactionRequest` needs to be created and consumed by a `TransactionConsumption`.

To generate a transaction request you can call:

```kotlin
val params = TransactionRequestCreateParams(
    type = TransactionRequestType.RECEIVE,
    tokenId = "a token id",
    amount = 1.bd, // BigDecimal
    address = "an address",
    requireConfirmation = true,
    allowAmountOverride = true,
    correlationId = "a correlation id",
    maxConsumptions = 10,
    maxConsumptionsPerUser = 5,
    consumptionLifetime = 60_000,
    expirationDate = null,
    metadata = mapOf(),
    encryptedMetadata = mapOf()
)

omgAPIClient.createTransactionRequest(params).enqueue(object: OMGCallback<TransactionRequest> {
    override fun success(response: OMGResponse<TransactionRequest>) {
        // Do something
    }

    override fun fail(response: OMGResponse<APIError>) {
        // Do something
    }
})
```

Where:
* `params` is a `TransactionRequestCreateParams` data class constructed using:
  * `type`: The QR code type, `TransactionRequestType.RECEIVE` or `TransactionRequestType.SEND`.
  * `tokenId`: The id of the desired token.
  In the case of a type "send", this will be the token taken from the requester. In the case of a type "receive" this will be the token received by the requester
  * `amount`: (optional) The amount of token to receive. This amount can be either inputted when generating or consuming a transaction request.
  * `address`: (optional) The address specifying where the transaction should be sent to. If not specified, the current user's primary wallet address will be used.
  * `correlationId`: (optional) An id that can uniquely identify a transaction. Typically an order id from a provider.
  * `requireConfirmation`: (optional) A boolean indicating if the request needs a confirmation from the requester before being proceeded
  * `maxConsumptions`: (optional) The maximum number of time that this request can be consumed
  * `consumptionLifetime`: (optional) The amount of time in millisecond during which a consumption is valid
  * `expirationDate`: (optional) The date when the request will expire and not be consumable anymore
  * `allowAmountOverride`: (optional) Allow or not the consumer to override the amount specified in the request. This needs to be true if the amount is not specified
  > Note that if `amount` is `null` and `allowAmountOverride` is false the init will fail and throw an exception.
``
  * `maxConsumptionsPerUser`: The maximum number of consumptions allowed per unique user
  * `metadata`: Additional metadata embedded with the request
  * `encryptedMetadata`: Additional encrypted metadata embedded with the request

#### Consume a transaction request

The previously created `transactionRequest` can then be consumed:

```kotlin
/* Short version */
val request = TransactionConsumptionParams.create(
    transactionRequest
)

/* Full version */
val request = TransactionConsumptionParams.create(
    transactionRequest,
    amount = 25_000.bd, // BigDecimal
    address = "an address",
    tokenId = "A token id",
    idempotencyToken = "An idempotency token",
    correlationId = "a correlation id",
    metadata = mapOf(),
    encryptedMetadata = mapOf()
)

omgAPIClient.consumeTransactionRequest(request).enqueue(object : OMGCallback<TransactionConsumption> {
    override fun success(response: OMGResponse<TransactionConsumption>) {
        // Handle success
    }

    override fun fail(response: OMGResponse<APIError>) {
        // Handle error
    }
})
```

Where 
* `request` is a `TransactionConsumptionParams` data class constructed using:
    * `transactionRequest`: The transactionRequest obtained from the QR scanner.
    * `address`: (optional) The address from which to take the funds. If not specified, the current user's primary wallet address will be used.
    * `tokenId`: (optional) The token id to use for the consumption.
    * `amount`: (optional) The amount of token to send. This amount can be either inputted when generating or consuming a transaction request.
    
        > Note that if the amount was not specified in the transaction request it needs to be specified here, otherwise the init will fail and throw `IllegalArgumentException`.
        
    * `idempotencyToken`: The idempotency token used to ensure that the transaction will be executed one time only on the server. If the network call fails, you should reuse the same idempotencyToken when retrying the request.
    * `correlationId`: (optional) An id that can uniquely identify a transaction. Typically an order id from a provider.
    * `metadata`: A dictionary of additional data to be stored for this transaction consumption.
    * `encryptedMetadata`: A dictionary of additional encrypted data to be stored for this transaction consumption.

### Approve or Reject a transaction consumption
The `TransactionConsumption` object can be used to `approve` or `reject` the transaction consumption. 
Once you receive the `transactionConsumption` object, you can call `approve` or `reject` function. 
The function will then return the `OMGCall<TransactionConsumption>` object to be used for making the actual request to the API.
 
```kotlin
val approveRequest = transactionConsumption.approve(omgAPIClient)
val rejectRequest = transactionConsumption.reject(omgAPIClient)

// Approve a transaction consumption
approveRequest.enqueue(object: OMGCallback<TransactionConsumption>{
    override fun success(response: OMGResponse<TransactionConsumption>) {
        // Handle success
    }

    override fun fail(response: OMGResponse<APIError>) {
        // Handle error
    }
})

// Reject a transaction consumption
rejectRequest.enqueue(object: OMGCallback<TransactionConsumption>{
    override fun success(response: OMGResponse<TransactionConsumption>) {
        // Handle success
    }

    override fun fail(response: OMGResponse<APIError>) {
        // Handle error
    }
})
```

### PaginationList
`PaginationList` is an object representing a paginated filtered data set.
This object will be returned from any API listing resources (e.g. accounts, transactions, tokens, etc.).

The general structure of the paginated request parameters consist of:

* `page` is the page you wish to receive
* `perPage` is the number of results per page
* `sortBy` contains the field used to sort. The value depends on which endpoint is being used.
* `sortDir` contains the direction for sorting. Available sorting directions are:
    
    `ASCENDING`, `DESCENDING`
    
    > `import co.omisego.omisego.model.pagination.SortDirection.*`
    
* `searchTerm` *(optional)* will search in all the searchable fields.
    Conflict with `searchTerms`, only use one of them.
* `searchTerms` *(optional)* is a key-value map of fields to search with the available fields (same as `searchTerm`)
    For example:
    
    ```kotlin
    mapOf(FROM to "some_address", ID to "some_id")
    ```

There is an generic `PaginationList<T>` inside the `response.data` which contains `data: List<T>` and `pagination: Pagination`

Where:
* `data` is an array of `T` object.
* `pagination` is a `Pagination` object:
    * `perPage` is the number of results per page.
    * `currentPage` is the retrieved page.
    * `isFirstPage` is a bool indicating if the page received is the first page
    * `isLastPage` is a bool indicating if the page received is the last page

#### Get transaction list
This returns a paginated filtered list of `transactions`.

In order to get the `transaction list` you will need to create a `TransactionListParams` object:

```kotlin
val request = TransactionListParams.create(
    page = 1,
    perPage = 10,
    sortBy = Paginable.Transaction.SortableFields.CREATE_AT,
    sortDir = SortDirection.ASCENDING,
    searchTerm = "confirmed", // or searchTerms = mapOf(STATUS to "completed")
    address = null
)
```

Where:

* `sortBy` is the sorting field. The available values are:

    `ID`, `STATUS`, `FROM`, `TO`, `CREATED_AT`
    
    > `import co.omisego.omisego.model.pagination.Paginable.Transaction.SortableFields.*`
    
* `address` *(optional)* is an optional address that belongs to the current user (primary wallet address by default)

Then you can call:

```kotlin
omgAPIAdmin.getTransactions(request).enqueue(object: OMGCallback<PaginationList<Transaction>>{
    override fun fail(response: OMGResponse<APIError>) {
        //TODO: Handle the error
    }

    override fun success(response: OMGResponse<PaginationList<Transaction>>) {
        //TODO: Do something with the paginated list of transactions
    }
})
```
   
#### Get account list

This returns a paginated filtered list of `accounts`.

In order to get the `account list` you will need to create a `AccountListParams` object in a similar way to [Get transaction list](#get-transaction-list)

Then you can call:

```kotlin
omgAPIAdmin.getAccounts(request).enqueue(object: OMGCallback<PaginationList<Account>>{
    override fun fail(response: OMGResponse<APIError>) {
        //TODO: Handle the error
    }

    override fun success(response: OMGResponse<PaginationList<Account>>) {
        //TODO: Do something with the paginated list of accounts
    }
})
```

#### Get token list

This returns a paginated filtered list of `tokens`.

In order to get the `token list` you will need to create a `TokenListParams` object in a similar way to [Get transaction list](#get-transaction-list)
                                                                                  
Then you can call:

```kotlin
omgAPIAdmin.getTokens(request).enqueue(object: OMGCallback<PaginationList<Token>>{
  override fun fail(response: OMGResponse<APIError>) {
      //TODO: Handle the error
  }

  override fun success(response: OMGResponse<PaginationList<Token>>) {
      //TODO: Do something with the paginated list of tokens
  }
})
```

#### Get account's wallet list

This returns a paginated filtered list of `account's wallet`.

In order to get the `account's wallet list` you will need to create a `AccountWalletListParams` object in a similar way to [Get transaction list](#get-transaction-list)
                                                                                  
Then you can call:

```kotlin
omgAPIAdmin.getAccountWalletListParams(request).enqueue(object: OMGCallback<PaginationList<Wallet>>{
  override fun fail(response: OMGResponse<APIError>) {
      //TODO: Handle the error
  }

  override fun success(response: OMGResponse<PaginationList<Wallet>>) {
      //TODO: Do something with the paginated list of wallets
  }
})
```

#### Get user's wallet list

This returns a paginated filtered list of user's wallet.

In order to get the `user's wallet list` you will need to create a `UserWalletListParams` object in a similar way to [Get transaction list](#get-transaction-list)
                                                                                  
Then you can call:

```kotlin
omgAPIAdmin.getUserWalletListParams(request).enqueue(object: OMGCallback<PaginationList<Wallet>>{
  override fun fail(response: OMGResponse<APIError>) {
      //TODO: Handle the error
  }

  override fun success(response: OMGResponse<PaginationList<Wallet>>) {
      //TODO: Do something with the paginated list of wallets
  }
})
```
