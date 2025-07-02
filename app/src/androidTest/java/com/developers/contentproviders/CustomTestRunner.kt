package com.developers.contentproviders

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

/**
 * Custom test runner for instrumentation tests
 * This allows for custom test application setup if needed
 */
class CustomTestRunner : AndroidJUnitRunner() {
    
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, TestApplication::class.java.name, context)
    }
}

/**
 * Test application class for instrumentation tests
 */
class TestApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Custom test setup can go here
    }
}
