package co.omisego.omisego.custom.retrofit2.executor

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 15/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor

/**
 * An Executor that operates on an android main thread
 */
class MainThreadExecutor : Executor {
    private val handler: Handler = Handler(Looper.getMainLooper())

    override fun execute(command: Runnable) {
        handler.post(command)
    }

    override fun toString(): String {
        return "MainThreadExecutor"
    }
}
