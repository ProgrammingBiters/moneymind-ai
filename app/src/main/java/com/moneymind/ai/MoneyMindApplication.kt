package com.moneymind.ai

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point. Annotated for Hilt so the dependency graph
 * (security, database, use cases) is available across the app.
 */
@HiltAndroidApp
class MoneyMindApplication : Application()
