package co.omisego.omisego.custom.zxing.ui.decorator

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ProgressBar


/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 4/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */
class OMGScannerLoadingUI : ProgressBar {
    constructor(context: Context) :
            super(context, null, android.R.attr.progressBarStyleSmall) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) :
            super(context, null, android.R.attr.progressBarStyleSmall) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, null, android.R.attr.progressBarStyleSmall) {
        init()
    }

    private fun init() {
        visibility = View.GONE
    }

}