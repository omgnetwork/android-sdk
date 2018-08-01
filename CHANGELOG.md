
# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)
and `OmiseGO` adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.1] - 2018-08-xx
### Changed
- Separated `omisego-sdk` module into `omisego-core` and `omisego-client`.

## [1.0.1] - 2018-07-26
### Added
- `addConnectionListener` and `removeConnectionListener` methods in `OMGSocketClient` class.
- `addChannelListener` and `removeChannelListener` methods in `OMGSocketClient` class.

### Changed
- Change `TransactionExchange` rate to nullable type
- Deprecated `setConnectionListener` and `setChannelListener` as a compilation error.
- Deprecated `SocketCustomEventListener.TransactionRequestListener` and `SocketCustomEventListener.TransactionConsumptionListener` as a compilation error.

### Fixed
- [Fix the websocket client doesn't rejoin the channel automatically when the network is temporarily offline.](https://github.com/omisego/android-sdk/issues/63)

## [1.0.0] - 2018-07-6
### Added
- Added documentation for missing fields in `TransactionRequest`

### Changed
- Changed kotlin version to `1.2.51`

## [0.9.6] - 2018-07-5
### Added
- Added more fields to `TransactionExchange`.
- Added `ExchangePair` model.
- Added `error` object to `Transaction` model.

### Changed
- Changed `parentId` type to nullable String.

### Fixed
- [OMGKeyManager throws an exception during the initialization steps on Android 5.1.1](https://github.com/omisego/android-sdk/issues/49)
- [Fix bug QR scanner doesn't cache invalid transaction id correctly](https://github.com/omisego/android-sdk/issues/51)

## [0.9.52] - 2018-06-26
### Changed
- Improved `OMGCameraPreview` performance
- Changed return type of `TransactionRequest.toTransactionConsumptionParams` to non-nullable.

## [0.9.51] - 2018-06-22
### Changed
- Renamed `sortDirection` to `sortDir`.

## [0.9.5] - 2018-06-22
### Changed
- [Changed some non-nullable variable to nullable variables](https://github.com/omisego/android-sdk/pull/43)
- Renamed an error code from `ErrorCode.USER_ACCESS_TOKEN_NOT_FOUND` to `ErrorCode.USER_AUTH_TOKEN_NOT_FOUND`

## [0.9.42] - 2018-06-15
### Added
- Added `metadata` and `encryptedMetadata` params to `Token` class. 

### Fixed
- [SDK package version `0.9.4` doesn't contain any class](https://github.com/omisego/android-sdk/issues/36)
- [Cannot get metadata of the user](https://github.com/omisego/android-sdk/issues/40)

### Changed
- Renamed endpoint `listTransactions` to `getTransactions`.
- Renamed endpoint `listWallets` to `getWallets`.

## [0.9.4] - 2018-06-14
### Added
- [Websocket](https://github.com/omisego/android-sdk#websocket)
- [Transfer](https://github.com/omisego/android-sdk#send-tokens-to-an-address)
- Add Parcelable supports

### Changed
- Renamed `EncryptionHelper` to `OMGEncryption`. Now, it can be only used to create an authorization header for connecting to the eWallet API.
- Changed the initialization steps of the `EWalletClient` from passing the base64 encrypted of "apiKey:authorizationToken" to pass both `apiKey` and `authenticationToken` to prevent misconfiguration.
- Removed `updatedAt` field from the `Transaction`
- Rename `MintedToken` to `Token`.
- Changed `retrieveTransactionRequest` API parameter to pass `formattedId` instead of `id`

## [0.9.3] - 2018-05-11
### Added
- [Kotlin linter](https://github.com/shyiko/ktlint)
- [Create a transaction request](https://github.com/omisego/android-sdk#generate-a-transaction-request)
- [Generate the QR code bitmap represents the transaction request](https://github.com/omisego/android-sdk#generate-qr-code-bitmap-representation-of-a-transaction-request)
- [Consume a transaction request](https://github.com/omisego/android-sdk#consume-a-transaction-request)
- [Approve a transaction consumption](https://github.com/omisego/android-sdk#approve-or-reject-a-transaction-consumption)
- [Reject a transaction consumption](https://github.com/omisego/android-sdk#approve-or-reject-a-transaction-consumption)
- [QR Code Scanner](https://github.com/omisego/android-sdk#scan-a-qr-code)

## [0.9.2] - 2018-03-28
### Added
- [Retrieves list of transactions](https://github.com/omisego/android-sdk#get-the-current-users-transactions)

### Fixed
- [OMGKeyManager KeyStoreException bug](https://github.com/omisego/android-sdk/pull/18)

## 0.9.1 - 2018-03-19
### Added
- [Retrieves the current user](https://github.com/omisego/android-sdk#get-the-current-user)
- [Retrieves the addresses of the current user](https://github.com/omisego/android-sdk#get-the-addresses-of-the-current-user)
- [Retrieves the provider settings](https://github.com/omisego/android-sdk#get-the-provider-settings)
- Logout the current user
- [OMGKeyManager - encryption and decryption helpers](https://github.com/omisego/android-sdk/pull/11)

[Unreleased]: https://github.com/omisego/android-sdk/compare/v1.0.1...HEAD
[1.0.1]: https://github.com/omisego/android-sdk/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/omisego/android-sdk/compare/v0.9.6...v1.0.0
[0.9.6]: https://github.com/omisego/android-sdk/compare/v0.9.52...v0.9.6
[0.9.52]: https://github.com/omisego/android-sdk/compare/v0.9.51...v0.9.52
[0.9.51]: https://github.com/omisego/android-sdk/compare/v0.9.5...v0.9.51
[0.9.5]: https://github.com/omisego/android-sdk/compare/v0.9.42...v0.9.5
[0.9.42]: https://github.com/omisego/android-sdk/compare/v0.9.4...v0.9.42
[0.9.4]: https://github.com/omisego/android-sdk/compare/v0.9.3...v0.9.4
[0.9.3]: https://github.com/omisego/android-sdk/compare/v0.9.2...v0.9.3
[0.9.2]: https://github.com/omisego/android-sdk/compare/v0.9.1...v0.9.2
