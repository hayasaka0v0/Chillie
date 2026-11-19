package com.huyvu.flashcard.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * Gán màu thiết kế vào các "vai" của Material 3 (primary, background...).
 * Nhờ vậy nút, ô nhập... của Material tự lấy đúng màu mà không cần set tay.
 * Thiết kế chỉ có giao diện sáng nên không có bản tối.
 */
private val LightColors = lightColorScheme(
    background = FlashcardColors.Background,
    onBackground = FlashcardColors.Ink,
    surface = FlashcardColors.White,
    onSurface = FlashcardColors.Ink,
    onSurfaceVariant = FlashcardColors.InkMuted,
    primary = FlashcardColors.Ink,
    onPrimary = FlashcardColors.Background,
    secondary = FlashcardColors.Rose,
    onSecondary = FlashcardColors.Ink,
    secondaryContainer = FlashcardColors.Blush,
    onSecondaryContainer = FlashcardColors.Ink,
    outline = FlashcardColors.Blush,
)

/**
 * Bọc toàn bộ giao diện app.
 *
 * `content: @Composable () -> Unit` đọc là: tham số `content` có kiểu
 * "một hàm không nhận gì `()`, không trả gì `Unit`, và được phép vẽ giao diện `@Composable`".
 * Người gọi truyền vào một khối `{ ... }` (lambda), ví dụ:
 *
 *     FlashcardTheme {      // ← khối này chính là `content`
 *         Text("Library")
 *     }
 *
 * Vì `content` là tham số CUỐI nên được viết khối `{ }` ra ngoài ngoặc tròn (trailing lambda),
 * và vì không còn tham số nào khác nên bỏ luôn cặp `()`.
 */
@Composable
fun FlashcardTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = FlashcardTypography,
        content = content, // đưa nguyên cái hàm `content` đi tiếp cho MaterialTheme, KHÔNG gọi nó ở đây
    )
}
