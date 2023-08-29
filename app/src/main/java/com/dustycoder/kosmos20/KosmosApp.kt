package com.dustycoder.kosmos20

import android.app.Application

class KosmosApp : Application() {
    val db by lazy {
        KosmosDatabase.getInstance(this)
    }
}