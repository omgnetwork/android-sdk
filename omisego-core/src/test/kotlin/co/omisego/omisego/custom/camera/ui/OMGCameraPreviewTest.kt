@file:Suppress("DEPRECATION")

package co.omisego.omisego.custom.camera.ui

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 24/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.hardware.Camera
import android.view.WindowManager
import co.omisego.omisego.custom.camera.CameraWrapper
import com.nhaarman.mockito_kotlin.timeout
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.experimental.runBlocking
import org.amshove.kluent.any
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldNotBe
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowCamera

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21])
class OMGCameraPreviewTest {
    private val mockCameraWrapper: CameraWrapper = mock()
    private val mockCameraPreviewCallback: Camera.PreviewCallback = mock()
    private val omgCameraPreview: OMGCameraPreview by lazy {
        OMGCameraPreview(
            RuntimeEnvironment.application,
            mockCameraWrapper,
            mockCameraPreviewCallback
        )
    }
    private val spyOMGCameraPreview by lazy { Mockito.spy(omgCameraPreview) }

    @Test
    fun `OMGCameraPreview should get previewWidth properly`() {
        spyOMGCameraPreview.previewWidth
        verify(spyOMGCameraPreview, times(1)).width
    }

    @Test
    fun `OMGCameraPreview should get previewHeight properly`() {
        spyOMGCameraPreview.previewHeight
        verify(spyOMGCameraPreview, times(1)).height
    }

    @Test
    fun `OMGCameraPreview should get displayOrientation properly`() {
        omgCameraPreview.mOMGCameraLogic = mock()

        omgCameraPreview.displayOrientation
        omgCameraPreview.apply { cameraWrapper = null }.displayOrientation

        verify(omgCameraPreview.mOMGCameraLogic, times(1)).getDisplayOrientation(true)
        verify(omgCameraPreview.mOMGCameraLogic, times(1)).getDisplayOrientation(false)
    }

    @Test
    fun `OMGCameraPreview should get windowManager properly`() {
        omgCameraPreview.windowManager shouldNotBe null
        omgCameraPreview.windowManager shouldBeInstanceOf WindowManager::class.java
    }

    @Test
    fun `OMGCameraPreview should get previewLayoutParams properly`() {
        spyOMGCameraPreview
            .apply { layoutParams = mock() }
            .previewLayoutParams
        verify(spyOMGCameraPreview, times(1)).layoutParams
    }

    @Test
    fun `OMGCameraPreview should get supportPreviewSizes properly`() {
        whenever(mockCameraWrapper.camera).thenReturn(mock())
        whenever(mockCameraWrapper.camera?.parameters).thenReturn(mock())

        omgCameraPreview.supportedPreviewSizes

        verify(mockCameraWrapper.camera?.parameters, times(1))?.supportedPreviewSizes
    }

    @Test
    fun `OMGCameraPreview should stopCameraPreview properly`() {
        whenever(mockCameraWrapper.camera).thenReturn(mock())

        omgCameraPreview.stopCameraPreview()

        verify(mockCameraWrapper.camera, timeout(1000).times(1))?.setPreviewCallback(null)
        verify(mockCameraWrapper.camera, timeout(1000).times(1))?.stopPreview()
    }

    @Test
    fun `OMGCameraPreview should call showCameraPreview properly`() = runBlocking {
        ShadowCamera.addCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, Camera.CameraInfo())
        val mockCamera = mock<Camera>()
        whenever(mockCameraWrapper.camera).thenReturn(mockCamera)
        whenever(spyOMGCameraPreview.mOMGCameraLogic).thenReturn(mock())
        whenever(spyOMGCameraPreview.mOMGCameraLogic.getDisplayOrientation(true)).thenReturn(0)
        whenever(spyOMGCameraPreview.setupCameraParameters()).thenReturn(null)

        spyOMGCameraPreview.startCameraPreview()

        verify(mockCamera, timeout(1000).times(1)).setPreviewDisplay(any())
        verify(mockCamera, timeout(1000).times(1)).setDisplayOrientation(any())
        verify(mockCamera, timeout(1000).times(1)).setPreviewCallback(any())
        verify(mockCamera, timeout(1000).times(1)).startPreview()
    }
}
