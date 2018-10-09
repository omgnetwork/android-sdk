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

```kotlin
val request = TransactionCreateParams(
    fromAddress = "1e3982f5-4a27-498d-a91b-7bb2e2a8d3d1",
    toAddress = "2e3982f5-4a27-498d-a91b-7bb2e2a8d3d1",
    amount = 1000.bd,
    tokenId = "BTC:xe3982f5-4a27-498d-a91b-7bb2e2a8d3d1",
    idempotencyToken = "some token"
)

omgAPIClient.transfer(request).enqueue(object : OMGCallback<Transaction>{
    override fun success(response: OMGResponse<Transaction>) {
        // Do something
    }

    override fun fail(response: OMGResponse<APIError>) {
        // Handle error
    }
})
```

There are different ways to initialize a `TransactionCreateParams` by specifying either `toAddress`, `toAccountId` or `toProviderUserId`.

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
