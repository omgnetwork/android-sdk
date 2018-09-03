@file:Suppress("DEPRECATION")

package co.omisego.omisego.custom.camera.ui

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 23/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.graphics.Point
import android.hardware.Camera
import android.view.Display
import android.view.Surface
import android.view.ViewGroup
import android.view.WindowManager
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.amshove.kluent.mock
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowCamera

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21])
class OMGCameraLogicTest {

    private val mockCameraPreviewView: CameraPreviewContract.View = mock()
    private val omgCameraLogic: OMGCameraLogic by lazy { OMGCameraLogic(mockCameraPreviewView) }
    private lateinit var mCamera: Camera

    @Before
    fun setup() {
        ShadowCamera.addCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, Camera.CameraInfo())
        mCamera = Camera.open()
    }

    @Test
    fun `getDisplayOrientation should return the orientation correctly`() {
        omgCameraLogic.getDisplayOrientation(false) shouldEqualTo 0

        val mockWindowManager: WindowManager = mock()
        val mockDefaultDisplay: Display = mock()

        whenever(mockCameraPreviewView.windowManager).thenReturn(mockWindowManager)
        whenever(mockWindowManager.defaultDisplay).thenReturn(mockDefaultDisplay)

        whenever(mockDefaultDisplay.rotation).thenReturn(Surface.ROTATION_0)
        omgCameraLogic.getDisplayOrientation(true) shouldEqualTo 0

        whenever(mockDefaultDisplay.rotation).thenReturn(Surface.ROTATION_90)
        omgCameraLogic.getDisplayOrientation(true) shouldEqualTo 270

        whenever(mockDefaultDisplay.rotation).thenReturn(Surface.ROTATION_180)
        omgCameraLogic.getDisplayOrientation(true) shouldEqualTo 180

        whenever(mockDefaultDisplay.rotation).thenReturn(Surface.ROTATION_270)
        omgCameraLogic.getDisplayOrientation(true) shouldEqualTo 90
    }

    @Test
    fun `getOptimalPreviewSize should return optimal result`() {
        val previewSizes = listOf(
            mCamera.Size(1920, 1080),
            mCamera.Size(1600, 1200),
            mCamera.Size(1440, 1080),
            mCamera.Size(1280, 960),
            mCamera.Size(1280, 768),
            mCamera.Size(1280, 720),
            mCamera.Size(1024, 768),
            mCamera.Size(800, 600),
            mCamera.Size(864, 480),
            mCamera.Size(800, 480),
            mCamera.Size(720, 480),
            mCamera.Size(640, 480),
            mCamera.Size(640, 360)
        )
        whenever(mockCameraPreviewView.displayOrientation).thenReturn(90)
        whenever(mockCameraPreviewView.supportedPreviewSizes).thenReturn(previewSizes)

        /* Test ratio 1.467 (1080, 1584) */
        whenever(mockCameraPreviewView.previewWidth).thenReturn(1080)
        whenever(mockCameraPreviewView.previewHeight).thenReturn(1584)
        val optimalSize = omgCameraLogic.getOptimalPreviewSize()

        /* Expect ratio 1.5 (720, 480) */
        optimalSize?.width shouldEqual 720
        optimalSize?.height shouldEqual 480

        /* Test ratio 1.896 */
        whenever(mockCameraPreviewView.previewWidth).thenReturn(1080)
        whenever(mockCameraPreviewView.previewHeight).thenReturn(2048)
        val optimalSize2 = omgCameraLogic.getOptimalPreviewSize()

        /* Expect ratio 1.8 */
        optimalSize2?.width shouldEqual 864
        optimalSize2?.height shouldEqual 480

        /* Test ratio 1.33 (1500, 2000) */
        whenever(mockCameraPreviewView.previewWidth).thenReturn(1500)
        whenever(mockCameraPreviewView.previewHeight).thenReturn(2000)
        val optimalSize3 = omgCameraLogic.getOptimalPreviewSize()

        /* Expect ratio 1.8 (1600, 1200) */
        optimalSize3?.width shouldEqual 1600
        optimalSize3?.height shouldEqual 1200

        /* Test ratio 1.33 (900, 1200)*/
        whenever(mockCameraPreviewView.previewWidth).thenReturn(900)
        whenever(mockCameraPreviewView.previewHeight).thenReturn(1200)
        val optimalSize4 = omgCameraLogic.getOptimalPreviewSize()

        /* Expect ratio 1.33 (1280, 960)*/
        optimalSize4?.width shouldEqual 1280
        optimalSize4?.height shouldEqual 960

        /* Test ratio 1.33 (300, 400) */
        whenever(mockCameraPreviewView.previewWidth).thenReturn(300)
        whenever(mockCameraPreviewView.previewHeight).thenReturn(400)
        val optimalSize5 = omgCameraLogic.getOptimalPreviewSize()

        /* Expect ratio 1.33 (640, 480)*/
        optimalSize5?.width shouldEqual 640
        optimalSize5?.height shouldEqual 480
    }

    @Test
    fun `adjustViewSize should be work properly if cameraRatio equal or more than screenRatio and the screen is portrait`() {
        /* Portrait */
        whenever(mockCameraPreviewView.displayOrientation).thenReturn(270)

        whenever(mockCameraPreviewView.previewWidth).thenReturn(300)
        whenever(mockCameraPreviewView.previewHeight).thenReturn(400)

        /* screenRatio == cameraRatio */
        omgCameraLogic.adjustViewSize(mCamera.Size(640, 480))
        verify(mockCameraPreviewView, times(1)).setViewSize(400, 300)

        /* screenRatio < cameraRatio */
        omgCameraLogic.adjustViewSize(mCamera.Size(720, 480))
        verify(mockCameraPreviewView, times(1)).setViewSize(400, 266)
    }

    @Test
    fun `adjustViewSize should be work properly if cameraRatio less than screenRatio and the screen is portrait`() {
        /* Portrait */
        whenever(mockCameraPreviewView.displayOrientation).thenReturn(270)

        whenever(mockCameraPreviewView.previewWidth).thenReturn(300)
        whenever(mockCameraPreviewView.previewHeight).thenReturn(400)

        /* screenRatio > cameraRatio */
        omgCameraLogic.adjustViewSize(mCamera.Size(600, 480))
        verify(mockCameraPreviewView, times(1)).setViewSize(375, 300)
    }

    @Test
    fun `adjustViewSize should be work properly if cameraRatio equal or more than screenRatio and the screen is landscape`() {
        /* Portrait */
        whenever(mockCameraPreviewView.displayOrientation).thenReturn(180)

        whenever(mockCameraPreviewView.previewWidth).thenReturn(300)
        whenever(mockCameraPreviewView.previewHeight).thenReturn(400)

        /* screenRatio == cameraRatio */
        omgCameraLogic.adjustViewSize(mCamera.Size(640, 480))
        verify(mockCameraPreviewView, times(1)).setViewSize(300, 225)

        /* screenRatio < cameraRatio */
        omgCameraLogic.adjustViewSize(mCamera.Size(720, 480))
        verify(mockCameraPreviewView, times(1)).setViewSize(300, 200)
    }

    @Test
    fun `adjustViewSize should be work properly if cameraRatio less than screenRatio and the screen is landscape`() {
        /* Portrait */
        whenever(mockCameraPreviewView.displayOrientation).thenReturn(180)

        whenever(mockCameraPreviewView.previewWidth).thenReturn(300)
        whenever(mockCameraPreviewView.previewHeight).thenReturn(400)

        /* screenRatio > cameraRatio */
        omgCameraLogic.adjustViewSize(mCamera.Size(600, 480))
        verify(mockCameraPreviewView, times(1)).setViewSize(300, 240)
    }

    @Test
    fun `getPointByOrientation in portrait should be work correctly`() {
        whenever(mockCameraPreviewView.displayOrientation).thenReturn(270)
        omgCameraLogic.getPointByOrientation(Point(640, 480)) shouldEqual Point(480, 640)

        whenever(mockCameraPreviewView.displayOrientation).thenReturn(90)
        omgCameraLogic.getPointByOrientation(Point(640, 480)) shouldEqual Point(480, 640)
    }

    @Test
    fun `getPointByOrientation in landscape should be work correctly`() {
        whenever(mockCameraPreviewView.displayOrientation).thenReturn(0)
        omgCameraLogic.getPointByOrientation(Point(640, 480)) shouldEqual Point(640, 480)

        whenever(mockCameraPreviewView.displayOrientation).thenReturn(180)
        omgCameraLogic.getPointByOrientation(Point(640, 480)) shouldEqual Point(640, 480)
    }

    @Test
    fun `calculateLayoutParams should calculate correctly`() {
        val dimen = 1056 to 1584
        val pDimen = 1080 to 1584
        val layoutParams = ViewGroup.LayoutParams(1080, 1620)

        whenever(mockCameraPreviewView.previewLayoutParams).thenReturn(layoutParams)

        omgCameraLogic.calculateLayoutParams(dimen, pDimen) shouldEqual layoutParams
    }
}