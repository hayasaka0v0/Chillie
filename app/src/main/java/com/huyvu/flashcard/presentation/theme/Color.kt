package com.huyvu.flashcard.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * Bảng màu lấy đúng từ bản thiết kế "Flashcard App UI".
 *
 * `object` = một "hộp" chỉ có đúng MỘT bản duy nhất trong cả app,
 * không cần (và không thể) gọi `FlashcardColors()` để tạo thêm.
 * Dùng: `FlashcardColors.Ink`.
 *
 * `Color(0xFFFFF5F5)`: 0x là số hệ 16. `FF` đầu = độ đục 100%, sau đó là mã màu `FFF5F5`.
 */
object FlashcardColors {
    val Background = Color(0xFFFFF5F5) // nền hồng rất nhạt của mọi màn
    val Ink = Color(0xFF4A4A4A)        // chữ chính, nút chính
    val InkMuted = Color(0xFF5E5557)   // chữ phụ (mô tả, nhãn nhỏ)
    val Blush = Color(0xFFF7D6D0)      // hồng nhạt: nút tròn, thẻ deck, thanh tiến độ nền
    val Rose = Color(0xFFE2B4BD)       // hồng đậm: mặt sau thẻ, phần "Learning"
    val White = Color(0xFFFFFFFF)
}
