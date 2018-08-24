@file:Suppress("DEPRECATION")

package co.omisego.omisego.qrcode.scanner

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 4/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.hardware.Camera
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import kotlin.coroutines.experimental.CoroutineContext

internal class OMGQRScannerPreview(
    private val omgQRScannerView: OMGQRScannerContract.View,
    override val verifier: OMGQRScannerContract.Preview.Verifier?,
    private var previewJob: Job? = null,
    private val decoder: OMGQRScannerContract.Preview.Decoder = OMGQRScannerPreviewDecoder(omgQRScannerView)
) : OMGQRScannerContract.Preview, OMGQRScannerContract.Preview.PostVerification {
    private var nullablePreviewSize: Deferred<Camera.Size?>? = null

    /* Keep the QR payload that being sent to the server to prevent spamming */
    override val qrPayloadCache: MutableSet<String> = mutableSetOf()

    init {
        verifier?.postVerification = this
    }

    internal fun provideAndroidContext(): CoroutineContext = UI

    internal fun provideAsyncContext(): CoroutineContext = CommonPool

    internal fun getPreviewOrientation(): Int {
        return omgQRScannerView.cameraPreview?.displayOrientation ?: 1
    }

    /**
     * Handle logic when previewing a frame from the camera
     *
     * @param data the contents of the preview frame in the format defined by ImageFormat,
     * which can be queried with getPreviewFormat(). If setPreviewFormat(int) is never called, the default will be the YCbCr_420_SP (NV21) format.
     * @param camera the Camera service object
     */
    override fun onPreviewFrame(data: ByteArray, camera: Camera) {
        /* Don't process anything if currently loading */
        if (omgQRScannerView.isLoading) return

        previewJob = launch(provideAndroidContext(), start = CoroutineStart.LAZY) {
            if (!isActive) return@launch

            /* Process in background thread */
            nullablePreviewSize = nullablePreviewSize ?: async {
                try {
                    camera.parameters.previewSize
                } catch (ex: Exception) {
                    return@async null
                }
            }

            val previewOrientation = withContext(DefaultDispatcher) { getPreviewOrientation() }
            val previewSize = nullablePreviewSize?.await() ?: return@launch
            val rawResult = withContext(DefaultDispatcher) {
                decoder.decode(data, previewOrientation, previewSize)
            }

            /* Wait result in the UI thread */
            rawResult?.text?.let { text ->
                /* Return immediately if we've already processed this [text] and it failed with [ErrorCode.TRANSACTION_REQUEST_NOT_FOUND] */
                if (isCached(text)) {
                    return@let
                }

                qrPayloadCache.add(text)

                /* Show loading */
                omgQRScannerView.isLoading = true

                /* Verify transactionId with the eWallet backend */
                verifier?.onDecoded(text)
            }
        }

        if (previewJob?.isCancelled == false) {
            previewJob?.start()
        }
    }

    override fun onRemoveCache(cacheText: String) {
        qrPayloadCache.remove(cacheText)
    }

    override fun onStopLoading() {
        omgQRScannerView.isLoading = false
    }

    /**
     * Cancel loading that verifying the QR code with the backend
     */
    override fun cancelLoading() {
        verifier?.onCanceled()
    }

    /**
     * Check whether the given [payload] has already cached.
     *
     * @param payload The payload of the QR code
     * @return true if the given [payload] has already cached, otherwise false.
     */
    override fun isCached(payload: String) = qrPayloadCache.contains(payload)

    override fun stopCamera() {
        previewJob?.cancel(null)
    }
}
