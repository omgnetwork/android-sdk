package co.omisego.omisego.custom.camera.ui

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 24/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.content.Context
import android.hardware.Camera
import android.view.WindowManager
import co.omisego.omisego.custom.camera.CameraWrapper
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.amshove.kluent.any
import org.amshove.kluent.mock
import org.amshove.kluent.shouldEqual
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
    fun `OMGCameraPreview should be retrieve previewWidth properly`() {
        spyOMGCameraPreview.previewWidth
        verify(spyOMGCameraPreview, times(1)).width
    }

    @Test
    fun `OMGCameraPreview should be retrieve previewHeight properly`() {
        spyOMGCameraPreview.previewHeight
        verify(spyOMGCameraPreview, times(1)).height
    }

    @Test
    fun `OMGCameraPreview should be retrieve displayOrientation properly`() {
        omgCameraPreview.mOMGCameraLogic = mock()

        omgCameraPreview.displayOrientation
        omgCameraPreview.apply { cameraWrapper = null }.displayOrientation

        verify(omgCameraPreview.mOMGCameraLogic, times(1)).getDisplayOrientation(true)
        verify(omgCameraPreview.mOMGCameraLogic, times(1)).getDisplayOrientation(false)
    }

    @Test
    fun `OMGCameraPreview should be retrieve windowManager properly`() {
        omgCameraPreview.windowManager shouldEqual
            RuntimeEnvironment.application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    @Test
    fun `OMGCameraPreview should be retrieve previewLayoutParams properly`() {
        spyOMGCameraPreview
            .apply { layoutParams = mock() }
            .previewLayoutParams
        verify(spyOMGCameraPreview, times(1)).layoutParams
    }

    @Test
    fun `OMGCameraPreview should be retrieve supportPreviewSizes properly`() {
        whenever(mockCameraWrapper.camera).thenReturn(mock())
        whenever(mockCameraWrapper.camera?.parameters).thenReturn(mock())

        omgCameraPreview.supportedPreviewSizes

        verify(mockCameraWrapper.camera?.parameters, times(1))?.supportedPreviewSizes
    }

    @Test
    fun `OMGCameraPreview should be stopCameraPreview properly`() {
        whenever(mockCameraWrapper.camera).thenReturn(mock())

        omgCameraPreview.stopCameraPreview()

        verify(mockCameraWrapper.camera)?.setPreviewCallback(null)
        verify(mockCameraWrapper.camera)?.stopPreview()
    }

    @Test
    fun `OMGCameraPreview should be call showCameraPreview properly`() {
        ShadowCamera.addCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, Camera.CameraInfo())
        val camera = mock<Camera>()
        whenever(mockCameraWrapper.camera).thenReturn(camera)
        whenever(spyOMGCameraPreview.mOMGCameraLogic).thenReturn(mock())
        whenever(spyOMGCameraPreview.mOMGCameraLogic.getDisplayOrientation(true)).thenReturn(0)
        whenever(spyOMGCameraPreview.setupCameraParameters()).thenReturn(null)

        spyOMGCameraPreview.showCameraPreview()

        verify(camera, times(1)).setPreviewDisplay(any())
        verify(camera, times(1)).setDisplayOrientation(any())
        verify(camera, times(1)).setPreviewCallback(any())
        verify(camera, times(1)).startPreview()
    }
}
