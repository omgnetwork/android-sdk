# OmiseGO Client Android SDK

The Client Android SDK allows developers to easily interact with the [OmiseGO Client eWallet API](https://ewallet.staging.omisego.io/api/client/docs.ui).

The SDK will work with 2 different schemes: [HTTP Requests](#http-requests) and [Websocket](#websocket).

# Table of Contents
  - [HTTP Requests](#http-requests)
    - [HTTP Requests Initialization](#http-requests-initialization)
    - [Retrieving resources](#retrieving-resources)
      - [Get the current user](#get-the-current-user)
      - [Get the wallets of the current user](#get-the-wallets-of-the-current-user)
      - [Get the provider settings](#get-the-provider-settings)
      - [Get the current user's transactions](#get-the-current-users-transactions)
    - [Transferring tokens](#transferring-tokens)
      - [Create a transaction](#create-a-transaction)
      - [Generate a transaction request](#generate-a-transaction-request)
      - [Consume a transaction request](#consume-a-transaction-request)
      - [Approve or Reject a transaction consumption](#approve-or-reject-a-transaction-consumption)
    - [QR codes](#qr-codes)
      - [Generate a QR code](#generate-qr-code-bitmap-representation-of-a-transaction-request)
      - [Scan a QR code](#scan-a-qr-code)
    - [Reset user password](#reset-user-password)
      - [Reset password](#reset-password)
      - [Update password](#update-password)
  - [Websocket](#websocket)
    - [Websocket Initialization](#websocket-initialization)
    - [Listen for the system event](#listen-for-system-events)
        - [Connection Event](#connection-event)
        - [Channel Event](#channel-event)
    - [Listen for the custom event](#listen-for-custom-events)
        - [TransactionRequest Event](#transactionrequest-event)
        - [TransactionConsumption Event](#transactionconsumption-event)
        - [User Event](#user-events)
        - [Filter Strategy](#filter-strategy)
    - [Stop listen for the custom event](#stop-listening-for-the-custom-event)
    - [Stop listen for the system event](#stop-listening-for-the-system-event)

# HTTP Requests
This section describes the use of the http client in order to retrieve or create resources.

## HTTP Requests Initialization

Before using the SDK to retrieve a resource, you need to initialize the client (`EWalletClient`) with a `ClientConfiguration` object.

You should do this as soon as you obtain a valid authentication token corresponding to the current user from the Wallet API.

Then you need to pass it to the `EWalletClient.Builder` and call `build()` to get the `EWalletClient` instance.

Lastly, you will need to pass the instance that you got from the previous step to the `OMGAPIClient`'s constructor.

For example,
```kotlin
 val config = ClientConfiguration(
     baseURL = "YOUR_BASE_URL",
     apiKey = "YOUR_API_KEY",
     authenticationToken = "YOUR_AUTH_TOKEN"
 )

 val eWalletClient = EWalletClient.Builder {
     clientConfiguration = config
     debug = false
 }.build()
 
 val omgAPIClient = OMGAPIClient(eWalletClient)
```

Where:
* `apiKey` is the api key generated from your OmiseGO admin panel.
* `authenticationToken` is the token corresponding to an OmiseGO Wallet user retrievable using one of our server-side SDKs.
> You can find more info on how to retrieve this token in the [OmiseGO server SDK documentations](https://github.com/omisego/ruby-sdk#login).
* `baseURL` is the URL of the OmiseGO Wallet API. **Note**: This need to be ended with '/'.
* `debug` *(Optional)* is a boolean indicating if the SDK should print logs in the console. default: `false`. 

## Retrieving resources

Once you have a client object from the [initialization section](#initialization), you can retrieve different resources.
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

### Get the current user

```kotlin
omgAPIClient.getCurrentUser().enqueue(object: OMGCallback<User>{
    override fun fail(response: OMGResponse<APIError>) {
        
    }

    override fun success(response: OMGResponse<User>) {
        
    }
})
```

### Get the wallets of the current user

```kotlin
omgAPIClient.getWallets().enqueue(object: OMGCallback<WalletList>{
    override fun fail(response: OMGResponse<APIError>) {
        
    }

    override fun success(response: OMGResponse<WalletList>) {
        
    }
})
```

> Note: For now a user will have only one wallet.

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

In order to get this list you will need to create a `ListTransactionParams` object:

```kotlin
val request = ListTransactionParams.create(
    page = 1,
    perPage = 10,
    sortBy = Paginable.Transaction.SortableFields.CREATE_AT,
    sortDir = SortDirection.ASCENDING,
    address = null
)
```

Where

* `page` is the page you wist to receive
* `perPage` is the number of results per page
* `sortBy` is the sorting field. The available values are:

    `ID`, `STATUS`, `FROM`, `TO`, `CREATED_AT`
    
    > `import co.omisego.omisego.model.pagination.Paginable.Transaction.SortableFields.*`
    
* `sortDir` is the sorting direction. The available values are:
    
    `ASCENDING`, `DESCENDING`
    
    > `import co.omisego.omisego.model.pagination.SortDirection.*`
    
* `address` *(optional)* is an optional address that belongs to the current user (primary wallet address by default)

Then you can call:

```kotlin
omgAPIClient.getTransactions(request).enqueue(object: OMGCallback<PaginationList<Transaction>>{
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
* `pagination` is a `Pagination` object:
    * `perPage` is the number of results per page.
    * `currentPage` is the retrieved page.
    * `isFirstPage` is a bool indicating if the page received is the first page
    * `isLastPage` is a bool indicating if the page received is the last page

## Transferring tokens

The SDK offers 2 ways for transferring tokens between addresses:
- A simple one way transfer from one of the current user's wallets to an address.
- A highly configurable send/receive mechanism in 2 steps using transaction requests.

#### Create a transaction

The most basic way to transfer tokens is to use the `omgAPIClient.createTransaction()` method, which allows the current user to send tokens from one of its wallets to a specific address.

```kotlin
val request = TransactionCreateParams(
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

A more configurable way to transfer tokens between 2 addresses is to use the transaction request flow.
To make a transaction happen, a `TransactionRequest` needs to be created and consumed by a `TransactionConsumption`.

To generate a new transaction request you can call:

```kotlin
/* Short version */
val request = TransactionRequestCreateParams(
    type = TransactionRequestType.RECEIVE,
    tokenId = "a_token_id"
)

/* Full version */
val request = TransactionRequestCreateParams(
    type = TransactionRequestType.RECEIVE,
    tokenId = "a_token_id",
    amount = 10_240, /* If the token has subUnitToUnit = 1,000, it means this request want to receive 10.24 OMG */
    address = "receiver_address",
    correlationId = "correlation_id",
    requireConfirmation = false,
    maxConsumption = 10,
    consumptionLifetime = 60000,
    expirationDate = null,
    allowAmountOverride = true,
    maxConsumptionsPerUser = null,
    metadata = mapof<String, Any>(),
    encryptedMetadata = mapOf<String, Any>()
)

val omgAPIClient = YOUR_OMG_API_CLIENT

omgAPIClient.createTransactionRequest(request).enqueue(object : OMGCallback<TransactionRequest> {
    override fun success(response: OMGResponse<TransactionRequest>) {
        //TODO: Do something with the transaction request (get the QR code representation for example)
    }

    override fun fail(response: OMGResponse<APIError>) {
        //TODO: Handle the error
    }
})
```

Where:
* `request` is a `TransactionRequestParams` data class constructed using:
    * `type`: The QR code type, only supports `TransactionRequestType.RECEIVE` for now.
    * `tokenId`: The id of the desired token.
    * `amount`: (optional) The amount of token to receive. This amount can be either inputted when generating or consuming a transaction request.
    * `address`: (optional) The address specifying where the transaction should be sent to. If not specified, the current user's primary wallet address will be used.
    * `correlationId`: (optional) An id that can uniquely identify a transaction. Typically an order id from a provider.
    * `requireConfirmation`: A boolean indicating if the request needs a confirmation from the requester before being proceeded.
    * `maxConsumptions`: (optional) The maximum number of time that this request can be consumed. Default `null` (unlimited).
    * `consumptionLifetime`: (optional) The amount of time in millisecond during which a consumption is valid. Default `null` (forever).
    * `expirationDate`: (optional) The date when the request will expire and not be consumable anymore. Default `null` (never expired).
    * `allowAmountOverride`: (optional) Allow or not the consumer to override the amount specified in the request. This needs to be true if the amount is not specified
    > Note that if the amount is null and allowAmountOverride is false the init will fail and return null.
    * `maxConsumptionsPerUser`: (optional) The maximum number of consumptions allowed per unique user. Default `null` (unlimited).
    * `metadata`: Additional metadata embedded with the request
    * `encryptedMetadata`: Additional encrypted metadata embedded with the request

A `TransactionRequest` object is passed to the success listener, you can generate its QR code representation using `transactionRequest.generateQRCode(size)`.

### Consume a transaction request
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

## QR Codes
This SDK offers the possibility to generate and consume transaction requests. Typically these actions should be done through the generation and scan of QR codes.

### Generate QR Code Bitmap representation of a transaction request
Once a `TransactionRequest` is created, you can get its QR code representation using `generateQRCode(size)`.
This method takes an optional `size` param that can be used to define the expected size of the generated QR bitmap.

```kotlin
val bitmap = txRequest.generateQRCode(512) // Create a 512x512 QR code 
```

### Scan a QR code
You can then use the `OMGQRScannerView` to scan the generated QR code.

<p align="center">
  <img src="assets/qr_scanner.png">
</p>

**First**, you need to add `OMGQRScannerView` to your xml.

> Note: You can customize the border color of the QR code frame of the scanner like the following 

**activity_qr_scanner.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
             xmlns:app="http://schemas.android.com/apk/res-auto"
             android:layout_width="match_parent"
             android:layout_height="match_parent">

    <co.omisego.omisego.qrcode.scanner.OMGQRScannerView
        android:id="@+id/scannerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:borderColor="@color/qrBorderColor"
        app:borderColorLoading="@color/qrBorderColorLoading" />
        
</FrameLayout>
```

**Then**, you can call `scannerView.startCameraWithVerifier` passing the `OMGQRVerifier` to start the camera.

> Note: You need to handle the camera permission first

```kotlin
class QRScannerActivity : AppCompatActivity(), OMGQRVerifierListener {
    private lateinit var omgAPIClient: OMGAPIClient
    private lateinit var verifier: OMGQRVerifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrscanner)
        
        omgAPIClient = your_omg_api_client
        scannerView = findViewById(R.id.scannerView) as OMGQRScannerView
        verifier = OMGQRVerifier(scannerView, omgAPIClient, this)
    }

    override fun scannerDidCancel(view: OMGQRScannerContract.View) {

    }

    override fun scannerDidDecode(view: OMGQRScannerContract.View, payload: OMGResponse<TransactionRequest>) {

    }

    override fun scannerDidFailToDecode(view: OMGQRScannerContract.View, exception: OMGResponse<APIError>) {
    
    }

    override fun onStop() {
        super.onStop()
        scannerView.stopCamera()
    } 

    override fun onStart() {
        super.onStart()
        scannerView.startCameraWithVerifier(verifier)
    }
}
```

As you can see, the `OMGQRVerifierListener` offers the following interface:

```kotlin
/**
 * Called when the user taps on the screen. The request to the backend will be canceled.
 *
 * @param view The QR scanner view
 */
fun scannerDidCancel(view: OMGQRScannerContract.View)

/**
 * Called when a QR code was successfully decoded to a TransactionRequest object
 *
 * @param view The QR scanner view
 * @param transactionRequest The transaction request decoded by the scanner
 */
fun scannerDidDecode(view: OMGQRScannerContract.View, transactionRequest: OMGResponse<TransactionRequest>)

/**
 * Called when a QR code has been scanned but the scanner was not able to decode it as a TransactionRequest
 *
 * @param view The QR scanner view
 * @param exception The error returned by the scanner
 */
fun scannerDidFailToDecode(view: OMGQRScannerContract.View, exception: OMGResponse<APIError>)
```

When the scanner successfully decodes a `TransactionRequest` it will call its delegate method `scannerDidDecode(view: OMGQRScannerContract.View, transactionRequest: TransactionRequest)`.


**Finally**, add the following lines to your `AndroidManifest.xml`

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.your.package">
    
    <!-- Add this 3 lines -->
    <uses-feature android:name="android.hardware.camera" />
    <uses-feature android:name="android.hardware.camera.autofocus" />
    <uses-permission android:name="android.permission.CAMERA" />
    
    <application>
        <!-- Your activities -->
    </application>
</manifest>
```

## Reset user password

> Note: Only available if the eWallet is running in standalone mode.
If a user forget his password, he will be able to reset it by requesting  a reset password link.
This link will contain a unique token that will need to be submitted along with the updated password chosen by the user.

### Reset password
To get a password reset link, you can call:

```kotlin
val params = ResetPasswordParams("email@example.com", "uri://redirect.url")

omgAPIClient.resetPassword(params).enqueue(object : OMGCallback<Empty>{
    override fun success(response: OMGResponse<Empty>) {
        // Do something
    }

    override fun fail(response: OMGResponse<APIError>) {
        // Handle error
    }
})
```

Where `params` is a `ResetPasswordParams` data class constructed using:
- `email`: The email of the user (ie: email@example.com)
- `redirectURL`: A redirect URL that will be built on the server by replacing the `{email}` and `{token}` params.
For example, if you provide this url: `my-app-scheme-uri://user/reset_password?email={email}&token={token}`, the user will receive a link by email that will look like this: `my-app-scheme-uri://user/reset_password?email=email@example.com&token=XXXXXXXXXXXXXXXXXXXXXX`.
You can then handle the params passed to your application upon launch from this deep link and pass them to the `omgAPIClient.updatePassword` method.

### Update password
To update the user with a new password, you can call:

```kotlin
val params = UpdatePasswordParams(
    "email@example.com",
    "your_token",
    "password",
    "new_password"
)

omgAPIClient.updatePassword(params).enqueue(object : OMGCallback<Empty>{
    override fun success(response: OMGResponse<Empty>) {
        // Do something
    }

    override fun fail(response: OMGResponse<APIError>) {
        // Handle error
    }
})
```
Where `params` is a `UpdatePasswordParams` data class constructed using:
- `email`: The email obtained in the previous step
- `token`: The token obtained in the previous step
- `password`: The updated user's password
- `passwordConfirmation`: The updated user's password

# Websocket

This section describes the use of the socket client in order to listen for events for a resource, especially the transaction request flow.

The transaction request flow is as follow:

1. [Create a TransactionRequest](#generate-a-transaction-request) with a simple http request successfully.
2. [Consume the TransactionRequest](#consume-a-transaction-request) which will generate a TransactionConsumption.
3. [Approve or reject the TransactionConsumption](#approve-or-reject-a-transaction-consumption) generated in step 2.

To listen for events which happen after step 2 or step 3, the WebSocket API will need to be used by first initialize the `OMGSocketClient`.

## Websocket Initialization

Similarly to the `OMGAPIClient`, the `OMGSocketClient` needs to be first initialized with a `ClientConfiguration` before using it.

```kotlin
val config = ClientConfiguration(
     baseURL = "YOUR_BASE_URL",
     apiKey = "YOUR_API_KEY",
     authenticationToken = "YOUR_AUTH_TOKEN"
 )
 
val socketClient = OMGSocketClient.Builder {
    clientConfiguration = config
    debug = false
}.build()
```

Where:
* `apiKey` is the API key (typically generated on the admin panel).
* `authenticationToken` is the token corresponding to an OmiseGO Wallet user retrievable using one of our server-side SDKs.
> You can find more info on how to retrieve this token in the [OmiseGO server SDK documentations](https://github.com/omisego/ruby-sdk#login). 
* `baseURL` is the URL of the OmiseGO Wallet API, this needs to be an ws(s) url. **Note**: This need to be ended with '/'.
* `debug` *(Optional)* is a boolean indicating if the SDK should print logs in the console. default: `false`.

## Listen for system events

The system event is giving general information related to the status of the web socket connection.
All listenable system events are the **Connection status** and the **Channel status**. 

### Connection Event

`SocketConnectionListener` *(optional)* is the listener that listens for a **web socket's server connection status**. The possible events are:

* `onConnected()`: Invoked when the web socket client has connected to the eWallet web socket API successfully.
* `onDisconnected(throwable: Throwable)`: Invoked when the web socket client has disconnected from the eWallet web socket API.
Throws an exception if the web socket was not disconnected successfully.

**Usage**
```kotlin
socketClient.addConnectionListener(object : SocketConnectionListener {
    override fun onConnected() {
        // Do something
    }

    override fun onDisconnected(throwable: Throwable?) {
        // Do something
    }
})
```
### Channel Event

`SocketChannelListener` *(optional)* is the listener that listens for a **channel connection status**. The possible events are:

* `onJoinedChannel(topic: String)`: Invoked when the client has joined the channel successfully.
* `onLeftChannel(topic: String)`: Invoked when the client has left the channel successfully.
* `onError(apiError: APIError)`: Invoked when something goes wrong while connecting to a channel.
    
**Usage**
```kotlin
socketClient.addChannelListener(object : SocketChannelListener {
    override fun onJoinedChannel(topic: String) {
        // Do something
    }

    override fun onLeftChannel(topic: String) {
        // Do something
    }

    override fun onError(apiError: APIError) {
        // Handle an error
    }
})
```

## Listen for custom events

A custom event is special events that are currently limited to the `TransactionRequest` and the `TransactionConsumption` event.
All custom events will be a sub-class of the `SocketCustomEventListener`. `SocketCustomEventListener` is a **required** generic listener that you will need to pass its sub-class when joining to the channel for listening to the events.
All possible events are the following:

### TransactionRequest Event

When creating a `TransactionRequest` that requires a confirmation it is possible to listen for all incoming events using the `TransactionRequestListener`.
The possible events are: 
* `onTransactionConsumptionRequest(TransactionConsumption)`: Invoked when a `TransactionConsumption` is trying to consume the `TransactionRequest`. 
This allows the requester to [confirm](https://github.com/omisego/android-sdk#approve-or-reject-a-transaction-consumption) or not the consumption if legitimate. 
* `onTransactionConsumptionFinalizedSuccess(TransactionConsumption)`: Invoked if a `TransactionConsumption` has been finalized successfully, and the transfer was made between the 2 addresses.
* `onTransactionConsumptionFinalizedFail(TransactionConsumption, APIError)`: Invoked if a `TransactionConsumption` fails to consume the request.
        
```kotlin
// The transaction requestor listen for the event 
transactionRequest.startListeningEvents(socketClient, listener = object: TransactionRequestListener() {
   override fun onTransactionConsumptionRequest(transactionConsumption: TransactionConsumption) {
       // Do something
   }

   override fun onTransactionConsumptionFinalizedSuccess(transactionConsumption: TransactionConsumption) {
       // Do something
   }

   override fun onTransactionConsumptionFinalizedFail(transactionConsumption: TransactionConsumption, apiError: APIError) {
       // Do something
   }
})
```        
        
### TransactionConsumption Event
 
Similarly to the `TransactionRequestListener`, a `TransactionConsumptionListener` can be listened for incoming confirmations using the `TransactionConsumptionListener`.
The possible events are:
* `onTransactionConsumptionFinalizedSuccess(TransactionConsumption)`: Invoked if a `TransactionConsumption` has been finalized successfully, and the transfer was made between the 2 addresses.
* `onTransactionConsumptionFinalizedFail(TransactionConsumption, APIError)`: Invoked if a `TransactionConsumption` fails to consume the request.

**Usage**
```kotlin
// The transaction consumer listen for the event
transactionConsumption.startListeningEvents(socketClient, listener = object: TransactionConsumptionListener() {
  override fun onTransactionConsumptionFinalizedSuccess(transactionConsumption: TransactionConsumption) {
      // Do something
  }

  override fun onTransactionConsumptionFinalizedFail(transactionConsumption: TransactionConsumption, apiError: APIError) {
      // Do something
  }
})
```

### User Event
A `user` can also be listened and will receive all events that are related to him:

```kotlin
user.startListeningEvents(socketClient, listener = SocketCustomEventListener.forEvent<TransactionConsumptionRequestEvent> { event -> 
       // Do something
})
```

Where:
* `event`:  An object which is containing a raw response from the eWallet socket API.

For more information can be found [here](https://github.com/omisego/ewallet/blob/develop/docs/websockets/ewallet_api.md).

Additionally, you might want to control how you receive the event manually, we've also provided the `FilterStrategy` to control the event filtering mechanism.

### Filter Strategy
`FilterStrategy` is a strategy that is used when receiving an event coming from the WebSocket server for filtering the events.
There're currently 4 strategies available:

* **Event**: Accept an event when the event is one of the given `SocketEvent` classes (e.g. TransactionConsumptionRequestEvent, TransactionConsumptionFinalizedEvent).
* **Topic**: Accept an event when the `SocketTopic` of the event is the same as the given `SocketTopic`.
* **None**: Accept all events.
* **Custom**: Manually custom filtering by implementing a lambda that receives `SocketEvent` to return a boolean.

In order to use `FilterStrategy`, it is needed to be listening for custom events by using `addCustomEventListener` method of the `OMGSocketClient` instance.

To avoid requiring to implement the `SocketCustomEventListener` manually, we recommend you to use conventional methods which are very useful for listening for the event easier. For example,

1. Use `SocketCustomEventListener.forEvent<SocketEvent>` to listen for the specific event. 
```kotlin
socketClient.addCustomEventListener(SocketCustomEventListener.forEvent<TransactionConsumptionRequestEvent> { socketEvent ->
    // Do something with `socketEvent.socketReceive` manually                         
})
```


2. Use `SocketCustomEventListener.forListenable` to listen for any events that related to the `SocketTopic`.
```kotlin
socketClient.addCustomEventListener(SocketCustomEventListener.forListenable(transactionRequest) { socketEvent ->
    // Do something with `socketEvent.socketReceive` manually
})
```

3. Use `SocketCustomEventListener.forStrategy` for listening for any events, but they will be filtered out by the provided `FilterStrategy`.
```kotlin
val customStrategy = FilterStrategy.Custom {
  it.socketReceive.topic.contains("transaction_consumption")
}

socketClient.addCustomEventListener(SocketCustomEventListener.forStrategy(customStrategy) { socketEvent ->

})
```

## Stop listening for the custom event

When you don't need to receive events anymore, you should call `stopListening(client: SocketClient)` for the corresponding `Listenable` object.
This will leave the corresponding socket channel and close the connection if no other channel is active.

For example,

```kotlin
transactionRequest.stopListening(socketClient)
// Or
transactionConsumption.stopListening(socketClient)
```

## Stop listening for the system event

The web socket client will be disconnected automatically if no other channel is active, so you won't receive any system event after that.

By the way, if you want to stop listening before that happen, you can call `socketClient.removeConnectionListener(listener)` or `socketClient.removeChannelListener(listener)`.
