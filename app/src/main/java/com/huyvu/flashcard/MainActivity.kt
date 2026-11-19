package com.huyvu.flashcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.huyvu.flashcard.presentation.theme.FlashcardTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Cửa vào duy nhất của app (app chỉ có 1 Activity, các màn là Composable).
 *
 * `@AndroidEntryPoint`: cho phép Hilt đưa đồ vào Activity này và vào các
 * ViewModel của các màn bên trong nó.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // setContent { ... }: khối lambda bên trong là giao diện của Activity.
        setContent {
            FlashcardTheme {
                // Tạm thời: Task 6 sẽ thay bằng FlashcardNavHost() để chuyển giữa các màn.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Library", style = MaterialTheme.typography.headlineLarge)
                }
            }
        }
    }
}
