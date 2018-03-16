package co.omisego.omisego.custom.retrofit2.executor

import java.util.concurrent.Executor
import java.util.concurrent.Executors

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 15/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

class ExecutorProvider {
    fun provideMainThreadExecutor(): Executor {
        return MainThreadExecutor()
    }

    fun provideSingleThreadExecutor(): Executor {
        return Executors.newSingleThreadExecutor()
    }
}