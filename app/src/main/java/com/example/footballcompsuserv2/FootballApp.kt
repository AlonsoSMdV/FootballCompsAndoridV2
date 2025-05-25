package com.example.footballcompsuserv2

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.footballcompsuserv2.ui.datastores.LangPrefs
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

@HiltAndroidApp
class FootballApp: Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()


    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)


        CoroutineScope(Dispatchers.IO).launch {
            LangPrefs.langPref(applicationContext).collectLatest { en ->
                withContext(Dispatchers.Main) {
                    val langTag = if (en) "en" else "es"
                    val localeList = LocaleListCompat.forLanguageTags(langTag)
                    AppCompatDelegate.setApplicationLocales(localeList)
                }
            }
        }
    }
}
