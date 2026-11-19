package com.huyvu.flashcard.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.huyvu.flashcard.R

/**
 * File `res/font/nunito.ttf` là "variable font": MỘT file chứa mọi độ đậm.
 * Mỗi lần gọi `nunito(...)` lấy ra một độ đậm từ file đó.
 * (Android 7 không hỗ trợ variable font nên hiện cùng một độ đậm; Android 8+ hiện đúng.)
 */
@OptIn(ExperimentalTextApi::class) // FontVariation còn là API "thử nghiệm", phải ghi rõ là đồng ý dùng
private fun nunito(weight: Int): Font = Font(
    resId = R.font.nunito,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

val NunitoFamily = FontFamily(
    nunito(400),
    nunito(600),
    nunito(700),
    nunito(800),
)

/** Đổi font của một kiểu chữ sang Nunito, giữ nguyên cỡ chữ, độ đậm, khoảng cách dòng. */
private fun useNunito(style: TextStyle): TextStyle = style.copy(fontFamily = NunitoFamily)

/** Bộ kiểu chữ mặc định của Material 3 (cỡ chữ chuẩn, font hệ thống). */
private val materialDefault = Typography()

/**
 * Bộ kiểu chữ của app: giống hệt bộ mặc định, chỉ đổi font sang Nunito.
 *
 * `Typography` không cho sửa trực tiếp (mọi thuộc tính là `val`),
 * nên dùng `.copy(...)` để tạo ra một bộ MỚI, khai báo những ô cần đổi.
 * Những ô không nhắc tới thì giữ nguyên như bản gốc.
 */
val FlashcardTypography: Typography = materialDefault.copy(
    displayLarge = useNunito(materialDefault.displayLarge),
    displayMedium = useNunito(materialDefault.displayMedium),
    displaySmall = useNunito(materialDefault.displaySmall),
    headlineLarge = useNunito(materialDefault.headlineLarge),
    headlineMedium = useNunito(materialDefault.headlineMedium),
    headlineSmall = useNunito(materialDefault.headlineSmall),
    titleLarge = useNunito(materialDefault.titleLarge),
    titleMedium = useNunito(materialDefault.titleMedium),
    titleSmall = useNunito(materialDefault.titleSmall),
    bodyLarge = useNunito(materialDefault.bodyLarge),
    bodyMedium = useNunito(materialDefault.bodyMedium),
    bodySmall = useNunito(materialDefault.bodySmall),
    labelLarge = useNunito(materialDefault.labelLarge),
    labelMedium = useNunito(materialDefault.labelMedium),
    labelSmall = useNunito(materialDefault.labelSmall),
)
