package com.huyvu.flashcard.domain.model

/**
 * Hình nhỏ trong ô vuông ở góc mỗi deck: HOẶC vài ký tự, HOẶC một icon.
 *
 * `sealed interface`: một kiểu cha mà danh sách kiểu con bị "niêm phong" ngay trong file này.
 * Ở đây Emblem chỉ có thể là `Emblem.Text` hoặc `Emblem.Icon`, không ai thêm được loại thứ ba.
 * Nhờ vậy khi dùng `when (emblem)`, trình biên dịch biết chắc đã xét đủ mọi trường hợp.
 */
sealed interface Emblem {
    /** Vài ký tự, vd. "あ", "Ää", "{ }". */
    data class Text(val value: String) : Emblem

    /** Một icon nét vẽ sẵn. */
    data class Icon(val icon: EmblemIcon) : Emblem
}

/**
 * Danh sách icon có sẵn. Mỗi giá trị enum mang thêm một thuộc tính `key`
 * (tên ngắn để lưu vào database): `EmblemIcon.GLOBE.key` = "globe".
 */
enum class EmblemIcon(val key: String) {
    CELL("cell"),
    GLOBE("globe"),
    CODE("code"),
    BOOK("book"),
    MUSIC("music"),
    FLASK("flask"),
    CALC("calc"),
    STAR("star"),
}

/**
 * Đổi Emblem thành chuỗi để lưu: `Text("あ")` → "text:あ", `Icon(GLOBE)` → "icon:globe".
 *
 * Extension function: gọi bằng `emblem.encode()`; bên trong, `this` là emblem đó.
 * `when (this) { is Emblem.Text -> ... }`: kiểm tra `this` thuộc kiểu con nào.
 * Trong nhánh `is Emblem.Text`, Kotlin tự hiểu `this` là Text nên đọc được `this.value`
 * (gọi là smart cast). Không cần `else` vì Emblem là sealed: chỉ có 2 khả năng.
 */
fun Emblem.encode(): String {
    return when (this) {
        is Emblem.Text -> "text:" + value
        is Emblem.Icon -> "icon:" + icon.key
    }
}

/** Ngược lại với `encode()`. Chuỗi không hợp lệ thì trả về `Text("?")` để app không bị crash. */
fun decodeEmblem(raw: String): Emblem {
    val unknown = Emblem.Text("?")
    val kind = raw.substringBefore(":")   // phần trước dấu ":" đầu tiên
    val rest = raw.substringAfter(":", missingDelimiterValue = "") // phần sau; không có ":" thì ""

    return when (kind) {
        "text" -> if (rest.isEmpty()) unknown else Emblem.Text(rest)
        "icon" -> {
            // Tìm trong danh sách enum giá trị nào có key trùng `rest`.
            // `firstOrNull { ... }` nhận một lambda kiểu (EmblemIcon) -> Boolean,
            // gọi nó lần lượt với từng icon (icon đó là tham số `it`),
            // trả về icon ĐẦU TIÊN làm lambda trả true, hoặc null nếu không có.
            val found = EmblemIcon.entries.firstOrNull { it.key == rest }
            if (found == null) unknown else Emblem.Icon(found)
        }
        else -> unknown
    }
}
