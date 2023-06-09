package com.babblechallenge.app

import android.app.Application
import androidx.appcompat.app.AppCompatActivity

class BabbleApp : Application() {

    val applicationComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.create()
    }

    override fun onCreate() {
        super.onCreate()
        applicationComponent
    }
}

inline fun <reified T> AppCompatActivity.findComponentDependencies(): T {
    return (this.application as BabbleApp).applicationComponent as T
}