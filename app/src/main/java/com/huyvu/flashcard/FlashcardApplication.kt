package com.huyvu.flashcard

import android.app.Application
import com.huyvu.flashcard.di.AppContainer

/** Sống suốt vòng đời app, giữ AppContainer để mọi màn hình dùng chung. */
class FlashcardApplication : Application() {
    val container: AppContainer by lazy { AppContainer() }
}
