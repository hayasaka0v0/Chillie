package com.huyvu.flashcard

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Object sống suốt từ lúc mở app tới lúc app bị tắt hẳn.
 *
 * `@HiltAndroidApp` bật Hilt cho cả app: lúc build, Hilt sinh ra code
 * "kho linh kiện" gắn vào đây. Từ giờ class nào cần một thứ gì
 * (repository, database...) chỉ việc khai báo, Hilt tự đưa tới.
 */
@HiltAndroidApp
class FlashcardApplication : Application()
