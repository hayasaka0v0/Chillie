# Kotlin Syntax — cho dev Flutter/Dart

> Mục tiêu: đọc hiểu và tự viết được app Flashcard bằng Kotlin + Jetpack Compose.
> Mỗi phần đều so sánh với Dart để map kiến thức cũ sang.
> Cuối file có phần **"Đọc code trong project"** giải thích từng dòng code đang có trong `app/src`.

**Cách chạy thử code trong file này:**
- Nhanh nhất: https://play.kotlinlang.org (dán code có `fun main()` vào là chạy).
- Trong Android Studio: `File > New > Scratch File > Kotlin` → viết `fun main() { ... }` → bấm nút ▶.
- Code Android (Compose, ViewModel, Room) thì phải chạy trong app.

---

## Mục lục

1. [Cấu trúc file & hàm main](#1-cấu-trúc-file--hàm-main)
2. [Biến: val, var, const, lateinit, lazy](#2-biến-val-var-const-lateinit-lazy)
3. [Kiểu dữ liệu cơ bản & ép kiểu](#3-kiểu-dữ-liệu-cơ-bản--ép-kiểu)
4. [String](#4-string)
5. [Null safety](#5-null-safety)
6. [Toán tử, range, kiểm tra kiểu](#6-toán-tử-range-kiểm-tra-kiểu)
7. [Điều khiển luồng: if, when, for, while](#7-điều-khiển-luồng-if-when-for-while)
8. [Hàm (function)](#8-hàm-function)
9. [Lambda & higher-order function](#9-lambda--higher-order-function)
10. [Collections: List, Set, Map](#10-collections-list-set-map)
11. [Class, constructor, property](#11-class-constructor-property)
12. [Kế thừa, abstract, interface](#12-kế-thừa-abstract-interface)
13. [data class, object, companion, enum, sealed](#13-data-class-object-companion-enum-sealed)
14. [Extension function & property](#14-extension-function--property)
15. [Scope functions: let, run, with, apply, also](#15-scope-functions-let-run-with-apply-also)
16. [Generics](#16-generics)
17. [Xử lý lỗi: try/catch, Result, require/check](#17-xử-lý-lỗi-trycatch-result-requirecheck)
18. [Delegation: by lazy, by, class delegation](#18-delegation-by-lazy-by-class-delegation)
19. [Coroutines: suspend, launch, async](#19-coroutines-suspend-launch-async)
20. [Flow, StateFlow (≈ Stream, ValueNotifier)](#20-flow-stateflow--stream-valuenotifier)
21. [Package, import, visibility, typealias, annotation](#21-package-import-visibility-typealias-annotation)
22. [Jetpack Compose: syntax cần biết](#22-jetpack-compose-syntax-cần-biết)
23. [ViewModel + UiState](#23-viewmodel--uistate)
24. [Room (database) — chuẩn bị cho bước tiếp theo](#24-room-database--chuẩn-bị-cho-bước-tiếp-theo)
25. [Gradle Kotlin DSL (≈ pubspec.yaml)](#25-gradle-kotlin-dsl--pubspecyaml)
26. [Đọc code trong project](#26-đọc-code-trong-project)
27. [Bài tổng hợp: Flashcard core chạy được](#27-bài-tổng-hợp-flashcard-core-chạy-được)
28. [Bảng tra nhanh Dart → Kotlin](#28-bảng-tra-nhanh-dart--kotlin)
29. [Những cái bẫy hay dính](#29-những-cái-bẫy-hay-dính)

---

## 1. Cấu trúc file & hàm main

```kotlin
package com.huyvu.flashcard        // giống library/part trong Dart nhưng theo thư mục

import kotlin.math.max             // import từng thứ (không có `show/hide`)
import kotlin.math.*               // import tất cả trong package

fun main() {                       // Dart: void main() { }
    println("Hello Kotlin")        // Dart: print()
}

// main có tham số
fun main(args: Array<String>) {
    println(args.size)
}
```

Khác biệt với Dart cần nhớ ngay:
- **Không cần dấu `;`** cuối dòng (có cũng không sao, nhưng không ai viết).
- **Không có `new`** (Dart cũng optional rồi).
- **Kiểu viết SAU tên**: `name: String` chứ không phải `String name`.
- Một file `.kt` chứa được **nhiều class, nhiều hàm top-level** (giống Dart). Không bắt buộc tên file trùng tên class (khác Java), nhưng convention là trùng nếu file chỉ có 1 class.
- Comment giống Dart: `//`, `/* */`, doc comment là `/** ... */` (KDoc, Dart dùng `///`).

```kotlin
/**
 * KDoc: tài liệu cho hàm.
 * @param card thẻ cần lật
 * @return thẻ sau khi lật
 */
```

---

## 2. Biến: val, var, const, lateinit, lazy

| Dart                     | Kotlin                    | Ý nghĩa |
|--------------------------|---------------------------|---------|
| `final name = "A";`      | `val name = "A"`          | Gán 1 lần, không gán lại |
| `var count = 0;`         | `var count = 0`           | Gán lại được |
| `const pi = 3.14;`       | `const val PI = 3.14`     | Hằng compile-time |
| `late String token;`     | `lateinit var token: String` | Khởi tạo sau |
| `late final x = calc();` | `val x by lazy { calc() }`| Tính lần đầu khi dùng |
| `int count = 0;`         | `var count: Int = 0`      | Ghi rõ kiểu |

```kotlin
val name = "Huy"              // suy kiểu: String
val age: Int = 25             // ghi rõ kiểu
var score = 0
score = 10                    // OK
// name = "Khác"              // LỖI: val không gán lại được

val later: String             // khai báo trước...
later = "gán sau"             // ...gán đúng 1 lần (trong hàm)
```

**Quy tắc vàng: luôn dùng `val`, chỉ đổi sang `var` khi thực sự cần gán lại.** Android Studio sẽ gợi ý nếu `var` có thể thành `val`.

### `const val`
Chỉ dùng được cho kiểu nguyên thủy (`Int`, `Long`, `Double`, `Boolean`, `String`...), và chỉ đặt ở **top-level** hoặc trong **`object` / `companion object`**:

```kotlin
const val MAX_CARDS_PER_SESSION = 20     // top-level, ngoài mọi class

class StudyConfig {
    companion object {
        const val DEFAULT_EASE = 2.5
    }
}
```
Convention đặt tên: `SCREAMING_SNAKE_CASE`.

### `lateinit var`
- Chỉ dùng với `var`, kiểu **không null**, **không phải kiểu nguyên thủy** (không dùng với `Int`, `Boolean`...).
- Truy cập trước khi gán → crash `UninitializedPropertyAccessException`.
- Kiểm tra đã gán chưa: `::token.isInitialized`.

```kotlin
class Session {
    lateinit var userId: String

    fun start(id: String) { userId = id }
    fun isReady() = ::userId.isInitialized
}
```
Trong app hiện đại (Compose) gần như không cần `lateinit`. Gặp chủ yếu trong code Android cũ / test.

### `by lazy`
```kotlin
val heavy: List<String> by lazy {
    println("Tính lần đầu")
    loadFromDisk()            // dòng cuối là giá trị trả về
}
```
Chỉ dùng với `val`. Mặc định thread-safe. Project đang dùng ở `di/AppContainer.kt`.

---

## 3. Kiểu dữ liệu cơ bản & ép kiểu

| Kotlin    | Dart     | Ghi chú |
|-----------|----------|---------|
| `Int`     | `int`    | 32-bit (Dart int là 64-bit!) |
| `Long`    | `int`    | 64-bit, literal có hậu tố `L`: `10L` |
| `Double`  | `double` | `3.14` |
| `Float`   | —        | hậu tố `f`: `3.14f` |
| `Boolean` | `bool`   | `true` / `false` |
| `Char`    | —        | **nháy đơn**: `'A'` |
| `String`  | `String` | **nháy kép**: `"abc"` |
| `Byte`, `Short` | — | hiếm dùng |
| `Any`     | `Object` | cha của mọi kiểu (không null) |
| `Any?`    | `Object?` / `dynamic` | Kotlin **không có `dynamic`** |
| `Unit`    | `void`   | hàm không trả gì |
| `Nothing` | `Never`  | hàm không bao giờ return (luôn throw) |

```kotlin
val a = 10            // Int
val b = 10L           // Long
val c = 10.0          // Double
val d = 10f           // Float
val e = 1_000_000     // dấu _ cho dễ đọc (Dart 3.6+ cũng có)
val hex = 0xFF
val bin = 0b1010
```

### ⚠️ Không có ép kiểu ngầm
Dart cho `int` dùng chỗ `num`/`double` khá thoải mái. Kotlin thì **không tự chuyển giữa số**:

```kotlin
val i: Int = 5
// val l: Long = i          // LỖI
val l: Long = i.toLong()    // phải chuyển rõ
val d: Double = i.toDouble()
val back: Int = 3.9.toInt() // 3 (cắt phần thập phân)
val r = 3.6.roundToInt()    // 4 — cần import kotlin.math.roundToInt

// Phép tính giữa kiểu khác nhau thì tự "nâng" kiểu kết quả:
val x = 5 + 2L              // Long
val y = 5 * 1.5             // Double
```

### ⚠️ Chia số nguyên
```kotlin
5 / 2        // 2   (Int / Int = Int, giống ~/ của Dart)
5 / 2.0      // 2.5
5.0 / 2      // 2.5
5 % 2        // 1
```
Trong Dart `5 / 2 == 2.5`. Trong Kotlin là `2`. Bug rất hay gặp khi tính phần trăm:
```kotlin
val percent = correct * 100 / total            // OK nếu chỉ cần số nguyên
val ratio = correct.toDouble() / total         // muốn số thực
```

### String ↔ số
```kotlin
"42".toInt()            // 42, sai format → crash NumberFormatException
"abc".toIntOrNull()     // null — an toàn, nên dùng cái này (≈ int.tryParse)
"3.14".toDouble()
"3.14".toDoubleOrNull()
42.toString()
```

---

## 4. String

```kotlin
val name = "Huy"
val count = 3

// Template: giống Dart y hệt
"Xin chào $name"
"Có ${count + 1} thẻ"
"Thẻ đầu: ${cards.first().front}"

// Raw string / nhiều dòng: """ (Dart cũng là ''' hoặc """)
val json = """
    {
      "front": "Hello",
      "back": "Xin chào"
    }
""".trimIndent()          // bỏ thụt lề chung

// Raw string không escape \n được, muốn có ký tự $ thì viết ${'$'}
val price = """Giá: ${'$'}10"""
```

Các hàm hay dùng:
```kotlin
val s = "  Hello World  "
s.length                    // 15 — là property, không có ()
s.trim()                    // "Hello World"
s.uppercase() / s.lowercase()
s.isEmpty() / s.isBlank()   // isBlank: rỗng hoặc toàn khoảng trắng
s.isNotBlank()
s.contains("World")         // hoặc: "World" in s
s.contains("world", ignoreCase = true)
s.startsWith("  He")
s.replace("World", "Kotlin")
s.split(" ")                // List<String>
s.substring(2, 7)
s[2]                        // Char 'H'
s.first() / s.last()
s.take(5) / s.drop(5)
s.padStart(20, '*')
s.reversed()
s.lines()                   // tách theo dòng
s.equals("hello", ignoreCase = true)

"%.2f".format(3.14159)      // "3.14"
String.format("%d/%d", 3, 10)

// Nối chuỗi hiệu quả (≈ StringBuffer)
val text = buildString {
    append("Câu hỏi: ")
    appendLine("Hello")
    append("Đáp án: Xin chào")
}

// List -> String
listOf("a", "b", "c").joinToString(", ")             // "a, b, c"
listOf("a", "b").joinToString(prefix = "[", postfix = "]")
```

So sánh string dùng `==` (so nội dung), giống Dart.

---

## 5. Null safety

Ý tưởng giống Dart (sound null safety), cú pháp gần như giống hệt.

```kotlin
var a: String = "abc"
// a = null                 // LỖI
var b: String? = "abc"
b = null                    // OK
```

| Dart          | Kotlin        | Ý nghĩa |
|---------------|---------------|---------|
| `x?.length`   | `x?.length`   | null thì trả null |
| `x ?? "def"`  | `x ?: "def"`  | **Elvis operator** — khác ký hiệu! |
| `x!`          | `x!!`         | ép không null, null → crash |
| `x ??= 5`     | `x = x ?: 5`  | không có `?:=` |
| `if (x != null)` | `if (x != null)` | smart cast |

```kotlin
val card: Flashcard? = cards.firstOrNull()

val len = card?.front?.length         // Int?
val front = card?.front ?: "Không có" // String
val sure = card!!.front               // crash nếu null — tránh dùng

// Elvis + return/throw: rất hay dùng để "thoát sớm"
fun show(card: Flashcard?) {
    val c = card ?: return                        // null thì return luôn
    val id = c.id
    val back = map["key"] ?: throw IllegalStateException("thiếu key")
}
```

### Smart cast
```kotlin
fun printLen(s: String?) {
    if (s != null) {
        println(s.length)        // tự hiểu s là String
    }
    if (s.isNullOrEmpty()) return
    println(s.length)            // cũng smart cast sau khi check
}
```

⚠️ **Smart cast không hoạt động với `var` property của class** (vì thread khác có thể đổi nó):
```kotlin
class Holder {
    var card: Flashcard? = null

    fun bad() {
        if (card != null) {
            // println(card.front)     // LỖI: Smart cast impossible
        }
    }

    fun good() {
        val c = card                   // copy ra biến local
        if (c != null) println(c.front)
        // hoặc:
        card?.let { println(it.front) }
    }
}
```

### `let` với null
```kotlin
card?.let { c ->
    println(c.front)     // chỉ chạy khi card != null
}
card?.let { println(it.front) }   // `it` là tên mặc định
```

### Hàm hữu ích
```kotlin
s.isNullOrEmpty()
s.isNullOrBlank()
s.orEmpty()               // null -> ""
list.orEmpty()            // null -> emptyList()
listOfNotNull(a, b, c)    // bỏ phần tử null
list.filterNotNull()
list.mapNotNull { it.toIntOrNull() }
```

---

## 6. Toán tử, range, kiểm tra kiểu

### So sánh
```kotlin
a == b      // so sánh giá trị (gọi equals) — Dart cũng ==
a != b
a === b     // so sánh tham chiếu (cùng 1 object) — Dart: identical(a, b)
a !== b
```

### Logic & số học
```kotlin
&&  ||  !
+ - * / %
+= -= *= /= %=
++ --
```
Toán tử bit dùng **hàm** chứ không dùng ký hiệu: `a and b`, `a or b`, `a xor b`, `a shl 2`, `a shr 2`, `a.inv()`.

**Không có toán tử 3 ngôi `? :`** → dùng `if` làm biểu thức (xem phần 7).

### Range
```kotlin
1..5            // 1,2,3,4,5 (có 5)
1..<5           // 1,2,3,4  (Kotlin 1.9+)
1 until 5       // 1,2,3,4  (cách viết cũ, vẫn dùng được)
5 downTo 1      // 5,4,3,2,1
1..10 step 2    // 1,3,5,7,9
'a'..'z'

x in 1..10      // kiểm tra trong khoảng
x !in 1..10
"a" in list     // = list.contains("a")
"key" in map    // = map.containsKey("key")
```

### Kiểm tra & ép kiểu
| Dart         | Kotlin        |
|--------------|---------------|
| `x is String`| `x is String` |
| `x is! String`| `x !is String` |
| `x as String`| `x as String` (sai kiểu → crash) |
| —            | `x as? String` (sai kiểu → null) |

```kotlin
fun describe(x: Any) {
    if (x is String) println(x.length)      // smart cast
    val n = x as? Int ?: 0                  // ép an toàn
}
```

---

## 7. Điều khiển luồng: if, when, for, while

### `if` là biểu thức (thay toán tử 3 ngôi)
```kotlin
// Dart: final label = isFlipped ? card.back : card.front;
val label = if (isFlipped) card.back else card.front

val grade = if (score >= 8) {
    println("giỏi")
    "A"                     // dòng cuối block là giá trị
} else if (score >= 5) {
    "B"
} else {
    "C"
}
```

### `when` — thay `switch`, mạnh hơn nhiều
```kotlin
// có tham số
when (rating) {
    1 -> println("Quên")
    2, 3 -> println("Khó")             // nhiều giá trị
    in 4..5 -> println("Được")         // range
    else -> println("Không hợp lệ")
}

// là biểu thức
val text = when (rating) {
    1 -> "Quên"
    2 -> "Khó"
    else -> "Dễ"
}

// không tham số = chuỗi if-else if
val level = when {
    streak == 0 -> "Mới"
    streak < 3 -> "Đang học"
    streak < 10 -> "Nhớ"
    else -> "Thuộc lòng"
}

// kiểm tra kiểu
when (x) {
    is String -> println(x.length)     // smart cast
    is Int -> println(x + 1)
    null -> println("null")
    else -> {}
}

// gán biến ngay trong when (thường dùng với UiState)
when (val s = viewModel.state) {
    is Loading -> ...
    is Success -> println(s.data)
}
```
- Không cần `break` (không có fall-through).
- Với `enum` và `sealed`, nếu `when` dùng làm biểu thức và đã cover hết các trường hợp → **không cần `else`**, thêm case mới mà quên xử lý thì compile báo lỗi (giống switch exhaustive của Dart 3).

### `for`
Kotlin **không có** `for (int i = 0; i < n; i++)`.
```kotlin
for (i in 0..<10) println(i)            // 0..9
for (i in 0 until list.size) {}
for (i in list.indices) {}              // 0..size-1
for (i in 10 downTo 0 step 2) {}

for (card in cards) println(card.front)  // Dart: for (final card in cards)

for ((index, card) in cards.withIndex()) {
    println("$index: ${card.front}")
}

for ((key, value) in map) {              // destructuring
    println("$key = $value")
}

repeat(3) { i -> println("Lần $i") }     // lặp n lần
cards.forEach { println(it.front) }      // kiểu functional
```

### `while`, `do-while`
```kotlin
while (queue.isNotEmpty()) { queue.removeFirst() }
do { input = readLine() } while (input.isNullOrBlank())
```

### `break` / `continue` với label
```kotlin
outer@ for (i in 1..3) {
    for (j in 1..3) {
        if (j == 2) continue@outer
        if (i == 3) break@outer
    }
}
```

---

## 8. Hàm (function)

```kotlin
// Dart: int add(int a, int b) { return a + b; }
fun add(a: Int, b: Int): Int {
    return a + b
}

// Hàm 1 biểu thức (Dart: =>)
fun add(a: Int, b: Int): Int = a + b
fun add(a: Int, b: Int) = a + b            // suy kiểu trả về

// Không trả gì: Unit (có thể bỏ qua)
fun log(msg: String): Unit { println(msg) }
fun log(msg: String) { println(msg) }
```

### Tham số mặc định & đặt tên
Dart chia ra positional `[]` và named `{}`. **Kotlin: tham số nào cũng gọi theo tên được**, và tham số nào cũng có thể có default.

```kotlin
fun createCard(
    front: String,
    back: String,
    tags: List<String> = emptyList(),
    isStarred: Boolean = false,       // dấu phẩy cuối được phép (trailing comma)
): Flashcard { ... }

createCard("Hello", "Xin chào")
createCard("Hello", "Xin chào", isStarred = true)     // bỏ qua tags
createCard(front = "Hello", back = "Xin chào")
createCard(back = "Xin chào", front = "Hello")        // đổi thứ tự khi dùng tên
```
- Không có `required` — tham số không có default thì bắt buộc.
- Convention: tham số Boolean nên gọi kèm tên cho dễ đọc: `answer(card, isCorrect = true)`.

### `vararg` (số lượng tham số tùy ý)
```kotlin
fun addTags(vararg tags: String) {
    for (t in tags) println(t)            // tags là Array<String>
}
addTags("a", "b", "c")
val arr = arrayOf("x", "y")
addTags(*arr)                             // spread operator *
```

### Hàm local (hàm trong hàm)
```kotlin
fun process(cards: List<Flashcard>) {
    fun isValid(c: Flashcard) = c.front.isNotBlank()
    cards.filter(::isValid)
}
```

### Hàm top-level
Hàm viết ngoài class, gọi ở đâu cũng được (sau khi import). Không cần nhét vào class `Utils` như Java.

### `infix`
```kotlin
infix fun Int.times(s: String) = s.repeat(this)
val r = 3 times "ha"      // "hahaha"
// Ví dụ có sẵn: 1 to "one", 1 until 5, a and b
```

### Function reference `::`
```kotlin
fun isLong(s: String) = s.length > 5
listOf("hi", "kotlin").filter(::isLong)    // tham chiếu hàm top-level
cards.map(Flashcard::front)               // tham chiếu property
onClick = viewModel::onFlip               // tham chiếu method của object
```
Dart cũng truyền `isLong` trực tiếp được; Kotlin bắt buộc có `::`.

---

## 9. Lambda & higher-order function

### Cú pháp lambda
```kotlin
// Dart: (int a, int b) => a + b
val sum = { a: Int, b: Int -> a + b }
sum(2, 3)

// Kiểu hàm: (tham số) -> kiểu trả về
val sum2: (Int, Int) -> Int = { a, b -> a + b }
val greet: () -> Unit = { println("Hi") }
val onAnswer: (Boolean) -> Unit = { isCorrect -> println(isCorrect) }
val maybe: ((String) -> Unit)? = null      // hàm nullable
maybe?.invoke("x")

// 1 tham số → dùng `it` thay vì đặt tên
val double: (Int) -> Int = { it * 2 }

// Lambda nhiều dòng: dòng CUỐI là giá trị trả về (không viết return)
val check: (String) -> Boolean = { s ->
    println("checking $s")
    s.isNotBlank()
}

// Bỏ qua tham số không dùng bằng _
map.forEach { (_, value) -> println(value) }
```

### Trailing lambda — QUAN TRỌNG (Compose dùng khắp nơi)
Nếu tham số **cuối cùng** là một hàm, có thể đưa lambda ra **ngoài dấu ngoặc**:

```kotlin
fun runTwice(action: () -> Unit) { action(); action() }

runTwice({ println("hi") })    // cách bình thường
runTwice() { println("hi") }   // trailing lambda
runTwice { println("hi") }     // bỏ luôn () khi không còn tham số nào khác

// Đây là lý do code Compose nhìn thế này:
Button(onClick = { onAnswer(true) }) {   // { ... } là tham số `content` cuối cùng
    Text("Đúng rồi!")
}
// ≈ Button(onClick = {...}, content = { Text("Đúng rồi!") })
```

### Higher-order function (hàm nhận/trả hàm)
```kotlin
fun <T> List<T>.countWhere(predicate: (T) -> Boolean): Int {
    var n = 0
    for (x in this) if (predicate(x)) n++
    return n
}
cards.countWhere { it.streak > 3 }

fun makeMultiplier(k: Int): (Int) -> Int = { it * k }
val triple = makeMultiplier(3)
triple(5)    // 15
```

### `return` trong lambda ⚠️
```kotlin
fun findFirstLong(words: List<String>): String? {
    words.forEach {
        if (it.length > 5) return it   // return khỏi CẢ hàm findFirstLong (vì forEach là inline)
    }
    return null
}

words.forEach {
    if (it.isBlank()) return@forEach   // chỉ bỏ qua phần tử này (≈ continue)
    println(it)
}

val f = { x: Int ->
    // return x * 2     // LỖI: lambda thường không được return trần
    x * 2               // dòng cuối là giá trị
}
```

### SAM / fun interface
```kotlin
fun interface CardFilter {
    fun accept(card: Flashcard): Boolean
}
val dueFilter = CardFilter { it.streak < 3 }   // viết lambda thay vì class
```

---

## 10. Collections: List, Set, Map

### Read-only vs Mutable — khác Dart nhiều nhất
Dart: `List` mặc định sửa được. Kotlin chia rõ 2 loại **interface**:

| Read-only (chỉ đọc) | Mutable (sửa được) |
|---------------------|--------------------|
| `List<T>` — `listOf()` | `MutableList<T>` — `mutableListOf()` |
| `Set<T>` — `setOf()` | `MutableSet<T>` — `mutableSetOf()` |
| `Map<K,V>` — `mapOf()` | `MutableMap<K,V>` — `mutableMapOf()` |

```kotlin
val a = listOf(1, 2, 3)
// a.add(4)                    // LỖI: List không có add
val b = mutableListOf(1, 2, 3)
b.add(4)
b += 5                         // = add
b.remove(1)
b[0] = 100
b.removeAt(0)
b.clear()

val empty = emptyList<String>()
val typed: List<Flashcard> = listOf()
val withSize = List(5) { i -> i * i }    // [0, 1, 4, 9, 16]
```

Phân biệt `val` / `var` với mutable / read-only:
```kotlin
val list1 = mutableListOf(1)   // không gán lại biến, nhưng sửa được nội dung
var list2 = listOf(1)          // không sửa nội dung, nhưng gán lại được:
list2 = list2 + 2              // tạo list MỚI [1, 2]
```
👉 Trong state của Compose/ViewModel: **dùng `List` (read-only) + tạo list mới** khi thay đổi (giống immutable state trong Flutter Bloc/Riverpod). Đừng đưa `MutableList` vào state, UI sẽ không biết mà vẽ lại.

### Truy cập
```kotlin
val list = listOf("a", "b", "c")
list[0]                 // "a" — index ngoài phạm vi → crash
list.getOrNull(10)      // null — an toàn
list.getOrElse(10) { "x" }
list.first()            // crash nếu rỗng
list.firstOrNull()      // null nếu rỗng
list.last() / list.lastOrNull()
list.size
list.isEmpty() / list.isNotEmpty()
list.indexOf("b")
list.contains("b") / "b" in list
list.lastIndex          // size - 1
list.subList(0, 2)
```

### Thao tác functional (so với Dart)
| Dart | Kotlin |
|------|--------|
| `.map((e) => ...).toList()` | `.map { ... }` (trả List luôn, không cần toList) |
| `.where(...)` | `.filter { }` |
| `.whereType<T>()` | `.filterIsInstance<T>()` |
| `.firstWhere(..., orElse:)` | `.first { }` / `.firstOrNull { }` / `.find { }` |
| `.any` / `.every` | `.any { }` / `.all { }` / `.none { }` |
| `.fold(0, (a, b) => ...)` | `.fold(0) { acc, x -> ... }` |
| `.reduce` | `.reduce { acc, x -> }` |
| `.expand` | `.flatMap { }` / `.flatten()` |
| `.take(n)` / `.skip(n)` | `.take(n)` / `.drop(n)` |
| `.toSet()` | `.toSet()` / `.distinct()` |
| `..sort()` | `.sorted()` / `.sortedBy { }` / `.sortedByDescending { }` |
| `.length` | `.size` / `.count { }` |
| `.asMap()` / `.indexed` | `.withIndex()` / `.mapIndexed { i, x -> }` |
| `[...a, ...b]` | `a + b` |
| `[if (x) a]` | `buildList { if (x) add(a) }` |

```kotlin
data class Card(val front: String, val streak: Int, val deck: String)

val cards = listOf(
    Card("Hello", 2, "EN"), Card("Bonjour", 0, "FR"), Card("Thanks", 5, "EN"),
)

cards.map { it.front }                          // [Hello, Bonjour, Thanks]
cards.filter { it.streak > 0 }
cards.filterNot { it.streak > 0 }
cards.count { it.deck == "EN" }                 // 2
cards.sumOf { it.streak }                       // 7
cards.maxByOrNull { it.streak }                 // Card(Thanks...)
cards.minOf { it.streak }                       // 0
cards.map { it.streak }.average()              // 2.333... (trung bình, kiểu Double)
cards.sortedBy { it.streak }
cards.sortedWith(compareBy<Card> { it.deck }.thenByDescending { it.streak })
cards.groupBy { it.deck }                       // Map<String, List<Card>>
cards.associateBy { it.front }                  // Map<String, Card>
cards.associate { it.front to it.streak }       // Map<String, Int>
cards.partition { it.streak >= 3 }              // Pair(thuộc, chưa thuộc)
cards.chunked(2)                                // chia nhóm 2 phần tử
cards.windowed(2)
cards.shuffled()                                // trộn — hữu ích cho flashcard!
cards.random()
cards.distinctBy { it.deck }
cards.zip(listOf(1, 2, 3))                      // List<Pair<Card, Int>>
cards.take(10)
cards.reversed()
cards.joinToString { it.front }

val (learned, learning) = cards.partition { it.streak >= 3 }   // destructuring Pair
```

Sửa một phần tử trong list read-only (pattern dùng trong repository của project):
```kotlin
val updated = cards.map { if (it.front == "Hello") it.copy(streak = 3) else it }
val removed = cards.filter { it.front != "Hello" }
val added = cards + Card("New", 0, "EN")
val minus = cards - cards.first()
```

### Set
```kotlin
val tags = setOf("verb", "noun", "verb")   // {verb, noun}
val m = mutableSetOf<String>()
m.add("x")
a union b / a intersect b / a subtract b
```

### Map
```kotlin
// Dart: {'a': 1, 'b': 2}
val m = mapOf("a" to 1, "b" to 2)     // `to` tạo Pair
val mm = mutableMapOf<String, Int>()

m["a"]                     // 1 — kiểu Int? (key không có → null)
m.getValue("a")            // Int — không có key → crash
m.getOrDefault("z", 0)
m.getOrElse("z") { 0 }
mm["c"] = 3                // thêm/sửa
mm.getOrPut("d") { 4 }     // chưa có thì thêm (Dart: putIfAbsent)
mm.remove("c")
m.keys / m.values / m.entries
m.containsKey("a") / "a" in m
m.filter { (k, v) -> v > 1 }
m.mapValues { (_, v) -> v * 10 }
m.forEach { (k, v) -> println("$k=$v") }
m + ("z" to 26)            // map mới
```

### Pair & Triple
```kotlin
val p: Pair<String, Int> = "Hello" to 3
p.first / p.second
val (word, count) = p
val t = Triple("a", 1, true)
```
Dart 3 có record `(a, b)`. Kotlin không có record → dùng `Pair`/`Triple`, hoặc tốt hơn là tạo `data class` có tên rõ ràng.

### Array
`Array<T>`, `IntArray`... ít dùng trong app, chủ yếu gặp ở `vararg` và `main(args)`. Dùng `List` là chính.

### Sequence (lazy, ≈ Iterable lazy của Dart)
```kotlin
cards.asSequence()
    .filter { it.streak > 0 }
    .map { it.front }
    .take(5)
    .toList()
```
Chỉ cần khi list rất lớn và chuỗi thao tác dài. App flashcard bình thường dùng list là đủ.

---

## 11. Class, constructor, property

### Primary constructor — gọn hơn Dart rất nhiều
```kotlin
// Dart:
// class Deck {
//   final String name;
//   int size;
//   Deck(this.name, [this.size = 0]);
// }

class Deck(val name: String, var size: Int = 0)

val d = Deck("Tiếng Anh")          // không có new
d.name
d.size = 5
```
- `val`/`var` trong ngoặc = vừa là tham số constructor, vừa là property.
- Không có `val`/`var` = chỉ là tham số, dùng trong `init` hoặc khởi tạo property:

```kotlin
class Deck(name: String) {
    val title = name.uppercase()       // `name` không phải property
}
```

### `init` block (≈ thân constructor Dart)
```kotlin
class Deck(val name: String) {
    val createdAt = System.currentTimeMillis()

    init {
        require(name.isNotBlank()) { "Tên deck không được rỗng" }
        println("Tạo deck $name")
    }
}
```
Thứ tự: property và `init` chạy **từ trên xuống dưới** theo thứ tự viết.

### Secondary constructor (≈ named constructor Dart)
Kotlin không có named constructor `Deck.empty()`. Cách thay:
```kotlin
class Deck(val name: String, val cards: List<Card>) {
    constructor(name: String) : this(name, emptyList())   // secondary, phải gọi this(...)

    companion object {
        fun empty() = Deck("Untitled", emptyList())         // ≈ factory/named constructor
        fun fromJson(json: String): Deck { ... }
    }
}
Deck.empty()
```
Thực tế: dùng **default parameter** thay cho hầu hết secondary constructor.

### Property, getter, setter
```kotlin
class Stats(val correct: Int, val total: Int) {
    // computed property (Dart: int get accuracy => ...)
    val accuracy: Double
        get() = if (total == 0) 0.0 else correct.toDouble() / total

    val isPerfect get() = correct == total     // suy kiểu Boolean
}

class Counter {
    var count = 0
        private set              // bên ngoài đọc được, không set được (≈ getter public + field _count)

    var name: String = ""
        set(value) {
            field = value.trim() // `field` = backing field, KHÔNG viết `name = ...` (đệ quy vô hạn)
        }

    fun inc() { count++ }
}
```

### Visibility modifier
| Kotlin | Dart | Ý nghĩa |
|--------|------|---------|
| `public` (mặc định) | không `_` | ai cũng thấy |
| `private` | `_name` | trong class / trong file (nếu top-level) |
| `protected` | — | class và class con |
| `internal` | — | trong cùng module (vd module `app`) |

```kotlin
class Repo {
    private val cache = mutableMapOf<Long, Card>()
    internal fun debugDump() {}
}
private fun helper() {}       // chỉ dùng trong file này
```

### Pattern "backing property" (rất hay gặp)
```kotlin
class DeckManager {
    private val _cards = mutableListOf<Card>()   // bên trong: sửa được
    val cards: List<Card> get() = _cards         // bên ngoài: chỉ đọc
}
```
Project dùng y hệt pattern này với `_uiState` / `uiState` trong `StudyViewModel.kt`. Dấu `_` ở đây chỉ là **convention đặt tên**, không làm biến private như Dart. Private là do chữ `private`.

### `this`
```kotlin
class Card(val front: String) {
    fun same(front: String) = this.front == front
}
```

### Nested vs inner class
```kotlin
class Outer {
    private val x = 1
    class Nested { /* không truy cập được x */ }
    inner class Inner { fun get() = x }          // truy cập được x
}
```

---

## 12. Kế thừa, abstract, interface

### ⚠️ Class mặc định là `final`
Dart: class nào cũng extends được. Kotlin: **phải đánh dấu `open`** mới cho kế thừa, method cũng phải `open` mới override được.

```kotlin
open class Animal(val name: String) {
    open fun sound() = "..."
    fun eat() = "$name ăn"           // không open → không override được
}

class Dog(name: String) : Animal(name) {   // `:` thay cho extends, gọi constructor cha luôn
    override fun sound() = "Gâu"            // `override` là keyword bắt buộc (Dart: @override)
}

class Puppy(name: String) : Dog(name)       // LỖI nếu Dog không open
```

Gọi method cha: `super.sound()`.

### Abstract class
```kotlin
abstract class Shape {
    abstract val area: Double             // abstract property
    abstract fun draw()
    fun describe() = "Diện tích $area"    // method có thân
}

class Circle(val r: Double) : Shape() {
    override val area get() = Math.PI * r * r
    override fun draw() { println("○") }
}
```

### Interface
Dart dùng `implements` cho mọi class. Kotlin có `interface` riêng, **có thể có code mặc định** (giống mixin một chút):

```kotlin
interface Reviewable {
    val dueAt: Long                                 // property abstract
    fun isDue(now: Long): Boolean = dueAt <= now    // có default implementation
}

interface Taggable {
    val tags: Set<String>
}

class Card(
    val front: String,
    override val dueAt: Long,
    override val tags: Set<String> = emptySet(),
) : Reviewable, Taggable          // implement nhiều interface, ngăn cách bởi dấu phẩy

// Vừa extends class vừa implements interface:
class Special(...) : Animal("x"), Reviewable { ... }
```
- `: ClassCha()` — có `()` vì gọi constructor.
- `: InterfaceA` — không có `()`.
- Kotlin **không có mixin**. Thay bằng interface có default method, hoặc delegation (`by`, phần 18).

### Anonymous object (≈ class ẩn danh)
```kotlin
val listener = object : Reviewable {
    override val dueAt = 0L
}
```

---

## 13. data class, object, companion, enum, sealed

### `data class` — thứ dùng nhiều nhất
Dart phải dùng `freezed`/`equatable` hoặc tự viết `==`, `hashCode`, `copyWith`, `toString`. Kotlin có sẵn:

```kotlin
data class Flashcard(
    val id: Long,
    val front: String,
    val back: String,
    val streak: Int = 0,
)

val a = Flashcard(1, "Hello", "Xin chào")
val b = Flashcard(1, "Hello", "Xin chào")

a == b                          // true — so sánh theo giá trị
println(a)                      // Flashcard(id=1, front=Hello, back=Xin chào, streak=0)
val c = a.copy(streak = 3)      // ≈ copyWith của freezed
val (id, front, back) = a       // destructuring theo thứ tự khai báo
```
- Chỉ các property trong **primary constructor** mới tham gia `equals`/`copy`/`toString`.
- Nên dùng `val` cho mọi field → immutable, cập nhật bằng `copy()`.
- Có thể thêm method, computed property trong thân `{ }`.

### `object` — singleton
```kotlin
object AppConfig {
    const val API_URL = "https://..."
    var darkMode = false
    fun reset() { darkMode = false }
}
AppConfig.reset()     // không cần tạo instance
```

### `companion object` — thay cho `static`
Kotlin **không có `static`**. Đồ "static" để trong `companion object`:
```kotlin
class Flashcard(val front: String) {
    companion object {
        const val MAX_LENGTH = 200
        fun sample() = Flashcard("Hello")
    }
}
Flashcard.MAX_LENGTH
Flashcard.sample()
```
Project dùng companion để chứa `Factory` trong `StudyViewModel.kt`.

### `enum class`
```kotlin
// Dart 2.17+: enum Rating { again, hard, good, easy }
enum class Rating {
    AGAIN, HARD, GOOD, EASY
}

// Enum có thuộc tính (Dart: enhanced enum)
enum class Rating(val label: String, val score: Int) {
    AGAIN("Quên", 0),
    HARD("Khó", 1),
    GOOD("Được", 2),
    EASY("Dễ", 3);            // ← dấu ; nếu sau đó có method

    fun isPass() = score >= 2
}

Rating.GOOD.label               // "Được"
Rating.GOOD.name                // "GOOD"
Rating.GOOD.ordinal             // 2
Rating.entries                  // List tất cả giá trị (Kotlin 1.9+; cũ: values())
Rating.valueOf("EASY")          // String → enum, sai → crash
Rating.entries.find { it.name == "X" }  // an toàn

val msg = when (r) {            // không cần else vì đã cover hết
    Rating.AGAIN -> "Ôn lại ngay"
    Rating.HARD -> "Ôn sớm"
    Rating.GOOD -> "Ổn"
    Rating.EASY -> "Quá dễ"
}
```
Convention đặt tên enum: `UPPER_CASE`.

### `sealed class` / `sealed interface` — cho UiState, kết quả, event
Giống `sealed class` của Dart 3. Tập con cố định, `when` kiểm tra đủ case.

```kotlin
sealed interface StudyUiState {
    data object Loading : StudyUiState                         // không có data → data object
    data class Studying(val card: Flashcard, val flipped: Boolean) : StudyUiState
    data class Error(val message: String) : StudyUiState
    data object Finished : StudyUiState
}

fun render(state: StudyUiState): String = when (state) {
    StudyUiState.Loading -> "Đang tải..."                     // object: so sánh trực tiếp
    is StudyUiState.Studying -> state.card.front               // class: dùng `is` + smart cast
    is StudyUiState.Error -> "Lỗi: ${state.message}"
    StudyUiState.Finished -> "Xong!"
}
```
- `data object` (Kotlin 1.9+): object có `toString` đẹp. Dùng cho trạng thái không mang dữ liệu.
- `sealed interface` linh hoạt hơn `sealed class` (một class có thể thuộc nhiều sealed interface). Ưu tiên `sealed interface`.

Hiện tại project dùng **một data class `StudyUiState`** với các cờ (`isFlipped`, `isFinished`). Cả hai cách đều phổ biến. Data class đơn giản hơn khi các trạng thái có chung nhiều field. Sealed thì chặt chẽ hơn khi mỗi trạng thái có dữ liệu riêng.

### `value class` (nâng cao, biết để đọc)
```kotlin
@JvmInline
value class CardId(val value: Long)      // bọc kiểu, tránh truyền nhầm Long khác
```

---

## 14. Extension function & property

Giống `extension` của Dart, cú pháp ngắn hơn:

```kotlin
// Dart: extension on String { bool get isLong => length > 10; }
fun String.isLong(): Boolean = this.length > 10       // `this` = string đang gọi
val String.wordCount: Int get() = trim().split(Regex("\\s+")).size

"hello world".isLong()
"hello world".wordCount

// Extension cho class của mình — giữ data class gọn, logic để ngoài
fun Flashcard.isMastered() = streak >= 5
fun List<Flashcard>.mastered() = filter { it.isMastered() }

// Extension trên kiểu nullable
fun String?.orDash() = this ?: "-"
```
Toàn bộ thư viện chuẩn Kotlin (`map`, `filter`, `let`, `apply`...) và Compose (`Modifier.padding`...) đều là extension function.

---

## 15. Scope functions: let, run, with, apply, also

Thứ làm người mới bối rối nhất. Dart có `..` (cascade). Kotlin có 5 hàm này:

| Hàm | Gọi object bằng | Trả về | Dùng khi |
|-----|-----------------|--------|----------|
| `let` | `it` | kết quả lambda | xử lý nullable `x?.let {}`, biến đổi giá trị |
| `apply` | `this` | **chính object** | cấu hình object (≈ cascade `..`) |
| `also` | `it` | **chính object** | làm thêm việc phụ (log, validate) |
| `run` | `this` | kết quả lambda | tính toán dùng nhiều member của object |
| `with(x)` | `this` | kết quả lambda | giống run, nhưng không phải extension |

```kotlin
// let — nullable
val length = name?.let { it.trim().length } ?: 0

// apply — cấu hình (Dart: final p = Paint()..color = red..strokeWidth = 2;)
val deck = Deck("EN").apply {
    add("Hello", "Xin chào")      // gọi trực tiếp method, ngầm hiểu this.add
    add("Bye", "Tạm biệt")
}

// also — làm thêm việc phụ, không đổi giá trị
val card = createCard("Hi", "Chào").also { println("Đã tạo ${it.id}") }

// run — tính toán
val summary = deck.run { "$name có $size thẻ" }

// with
val text = with(card) { "$front - $back" }
```

Mẹo chọn nhanh:
- Có `?.` và cần xử lý khi không null → `let`
- Tạo object rồi set nhiều thứ → `apply`
- Muốn log/debug giữa chừng → `also`
- Còn lại → viết bình thường cho dễ đọc. **Đừng lạm dụng**, lồng 2-3 scope function là khó đọc.

---

## 16. Generics

```kotlin
class Box<T>(val value: T)
val b = Box(5)                 // Box<Int>
val s: Box<String> = Box("a")

fun <T> List<T>.secondOrNull(): T? = if (size >= 2) this[1] else null

// Ràng buộc kiểu (Dart: T extends Comparable)
fun <T : Comparable<T>> maxOf3(a: T, b: T, c: T): T = maxOf(a, maxOf(b, c))

// Nhiều ràng buộc
fun <T> copyAll(items: List<T>) where T : Reviewable, T : Taggable { }
```

### Variance `out` / `in` (để đọc hiểu, ít khi tự viết)
```kotlin
interface Source<out T> { fun next(): T }     // chỉ "trả ra" T → covariant
interface Sink<in T> { fun put(item: T) }     // chỉ "nhận vào" T → contravariant
```
`List<out E>` là lý do có thể gán `List<Dog>` vào `List<Animal>`. `MutableList` thì không được.

### `reified` (giữ được kiểu T lúc runtime)
```kotlin
inline fun <reified T> List<Any>.only(): List<T> = filterIsInstance<T>()
val strings = listOf(1, "a", 2, "b").only<String>()
```

Star projection: `List<*>` ≈ `List<dynamic>` chỉ đọc.

---

## 17. Xử lý lỗi: try/catch, Result, require/check

```kotlin
// Dart: try {...} on FormatException catch (e) {...}
try {
    val n = "abc".toInt()
} catch (e: NumberFormatException) {
    println("Sai số: ${e.message}")
} catch (e: Exception) {
    println("Lỗi khác")
} finally {
    println("Luôn chạy")
}

// try là biểu thức
val n = try { input.toInt() } catch (e: NumberFormatException) { 0 }

// throw
throw IllegalArgumentException("Thẻ không hợp lệ")

// Tự tạo exception
class CardNotFoundException(id: Long) : Exception("Không thấy thẻ $id")
```
Kotlin **không có checked exception** (không phải khai báo `throws`), giống Dart.

### Hàm kiểm tra có sẵn
```kotlin
require(front.isNotBlank()) { "Front rỗng" }     // sai → IllegalArgumentException (lỗi tham số)
check(isInitialized) { "Chưa init" }              // sai → IllegalStateException (lỗi trạng thái)
val c = requireNotNull(card) { "card null" }      // trả về non-null
error("Không thể xảy ra")                         // throw IllegalStateException, kiểu Nothing
TODO("Làm sau")                                   // throw NotImplementedError — đánh dấu chỗ chưa code
```

### `runCatching` / `Result`
```kotlin
val result: Result<Int> = runCatching { "42".toInt() }

result.isSuccess
result.getOrNull()               // 42 hoặc null
result.getOrDefault(0)
result.onSuccess { println(it) }
      .onFailure { println("Lỗi: ${it.message}") }

val value = result.fold(
    onSuccess = { it * 2 },
    onFailure = { -1 },
)
```
⚠️ Không dùng `runCatching` bọc code coroutine (`suspend`) vì nó nuốt luôn `CancellationException`. Trong coroutine dùng `try/catch` thường.

---

## 18. Delegation: by lazy, by, class delegation

### Property delegation
```kotlin
val db by lazy { createDatabase() }          // tính lần đầu khi dùng

import kotlin.properties.Delegates
var name: String by Delegates.observable("") { _, old, new ->
    println("$old -> $new")
}

// Compose:
var flipped by remember { mutableStateOf(false) }   // phần 22
// ViewModel:
val viewModel: StudyViewModel by viewModels()       // trong Activity (cách cũ, không Compose)
```
`by` nghĩa là "get/set của biến này do object kia lo". Với `mutableStateOf` cần import `getValue`/`setValue` (Android Studio tự gợi ý Alt+Enter).

### Class delegation (thay mixin/kế thừa)
```kotlin
interface Logger { fun log(msg: String) }
class ConsoleLogger : Logger { override fun log(msg: String) = println(msg) }

class CardService(logger: Logger) : Logger by logger {   // tự "chuyển tiếp" mọi method Logger sang logger
    fun doWork() = log("working")
}
```

---

## 19. Coroutines: suspend, launch, async

So với Dart:

| Dart | Kotlin |
|------|--------|
| `Future<T> f() async` | `suspend fun f(): T` |
| `await f()` | `f()` (gọi thẳng, **không cần await**) |
| `Future.wait([a(), b()])` | `async { a() }` + `async { b() }` → `awaitAll` |
| `Future.delayed(Duration(seconds: 1))` | `delay(1000)` |
| Isolate / compute | `withContext(Dispatchers.Default)` |
| `unawaited(f())` | `scope.launch { f() }` |
| `Stream<T>` | `Flow<T>` |

### `suspend` function
```kotlin
suspend fun loadCards(): List<Flashcard> {
    delay(500)                    // tạm dừng, không chặn thread
    return listOf(...)
}

suspend fun loadAll() {
    val cards = loadCards()       // chạy tuần tự, kết quả có luôn — không cần await
    val stats = loadStats()
}
```
Quy tắc: **hàm `suspend` chỉ gọi được từ hàm `suspend` khác hoặc trong coroutine** (giống `await` chỉ dùng trong hàm `async` của Dart).

### Khởi động coroutine
```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {        // chỉ dùng trong main/test, KHÔNG dùng trong app
    val job: Job = launch {       // "bắn và quên", trả Job
        delay(1000)
        println("Xong việc 1")
    }

    val deferred: Deferred<Int> = async {   // có kết quả (≈ Future)
        delay(500)
        42
    }
    println(deferred.await())     // ở đây mới cần await

    job.join()                    // chờ job xong
    job.cancel()                  // hủy
}
```

Chạy song song:
```kotlin
suspend fun loadDashboard() = coroutineScope {
    val cards = async { repo.getCards() }
    val stats = async { repo.getStats() }
    Dashboard(cards.await(), stats.await())     // chờ cả 2, tổng thời gian = cái lâu nhất
}
```

### Dispatcher (chạy trên thread nào)
```kotlin
withContext(Dispatchers.IO) {       // đọc file, database, network
    readFile()
}
withContext(Dispatchers.Default) {  // tính toán nặng (≈ compute/Isolate)
    sortHugeList()
}
// Dispatchers.Main — UI thread (mặc định của viewModelScope)
```
Room và Retrofit tự chuyển sang thread nền cho hàm `suspend`, nên thường **không cần tự gọi withContext** khi dùng chúng.

### Scope trong Android
| Scope | Ở đâu | Tự hủy khi |
|-------|-------|-----------|
| `viewModelScope` | trong ViewModel | ViewModel bị clear |
| `lifecycleScope` | Activity/Fragment | bị destroy |
| `rememberCoroutineScope()` | Composable | composable rời màn hình |
| `LaunchedEffect(key) { }` | Composable | rời màn hình hoặc key đổi |

```kotlin
class StudyViewModel(...) : ViewModel() {
    fun onAnswer(card: Flashcard) {
        viewModelScope.launch {           // tạo coroutine từ hàm thường
            repository.updateCard(card)   // gọi suspend fun
        }
    }
}
```

---

## 20. Flow, StateFlow (≈ Stream, ValueNotifier)

### Flow ≈ Stream
```kotlin
import kotlinx.coroutines.flow.*

fun countdown(): Flow<Int> = flow {       // Dart: Stream<int> f() async* { yield ... }
    for (i in 3 downTo 1) {
        emit(i)                            // ≈ yield
        delay(1000)
    }
}

// Nghe (phải trong coroutine)
viewModelScope.launch {
    countdown()
        .filter { it > 1 }
        .map { "Còn $it" }
        .collect { println(it) }           // ≈ stream.listen / await for
}

suspend fun firstValue() = countdown().first()    // lấy 1 giá trị rồi dừng
```
- Flow là **cold**: chỉ chạy khi có người `collect`, mỗi người collect chạy lại từ đầu (giống single-subscription Stream).
- Toán tử: `map`, `filter`, `onEach`, `take`, `debounce`, `distinctUntilChanged`, `combine`, `flatMapLatest`, `catch`, `onStart`...

```kotlin
// Kết hợp 2 flow (≈ Rx combineLatest)
val dueCount: Flow<Int> = combine(repo.getCards(), clockFlow) { cards, now ->
    cards.count { it.isDue(now) }
}
```

### StateFlow ≈ ValueNotifier / BehaviorSubject / state của Riverpod
Luôn có **giá trị hiện tại**, **hot**, phát lại giá trị mới nhất cho người mới nghe, bỏ qua nếu giá trị mới `==` giá trị cũ.

```kotlin
private val _uiState = MutableStateFlow(StudyUiState())    // giá trị ban đầu bắt buộc
val uiState: StateFlow<StudyUiState> = _uiState.asStateFlow()   // public read-only

_uiState.value                                    // đọc
_uiState.value = StudyUiState(isFlipped = true)   // ghi
_uiState.update { it.copy(isFlipped = !it.isFlipped) }  // ghi an toàn đa luồng — nên dùng
```

Biến Flow thành StateFlow (dùng nhiều khi nối Room → UI):
```kotlin
val cards: StateFlow<List<Flashcard>> = repository.getCards()
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )
```

### SharedFlow / Channel — cho sự kiện một lần (toast, điều hướng)
```kotlin
private val _events = MutableSharedFlow<String>()
val events = _events.asSharedFlow()
viewModelScope.launch { _events.emit("Đã lưu!") }
```

---

## 21. Package, import, visibility, typealias, annotation

```kotlin
package com.huyvu.flashcard.domain.model     // nên khớp thư mục

import com.huyvu.flashcard.domain.model.Flashcard
import androidx.compose.ui.Modifier as M     // đổi tên khi trùng (Dart: as prefix)
```
Không có `export`/`part` như Dart. Mỗi thứ import trực tiếp từ package của nó.

```kotlin
typealias CardId = Long
typealias OnAnswer = (isCorrect: Boolean) -> Unit

// Annotation: cú pháp như Dart
@Composable
@Preview(showBackground = true)
@Deprecated("Dùng hàm mới", ReplaceWith("newFun()"))
@Suppress("UNUSED_PARAMETER")
```

Operator overloading (biết để đọc):
```kotlin
data class Score(val v: Int) {
    operator fun plus(o: Score) = Score(v + o.v)
}
Score(1) + Score(2)

class AnswerCardUseCase(...) {
    suspend operator fun invoke(card: Flashcard, isCorrect: Boolean) { ... }
}
answerCard(card, true)        // gọi object như gọi hàm — nhờ operator invoke (≈ call() của Dart)
```

---

## 22. Jetpack Compose: syntax cần biết

### Map Flutter → Compose

| Flutter | Compose |
|---------|---------|
| `StatelessWidget` + `build()` | `@Composable fun` |
| `StatefulWidget` + `setState` | `remember { mutableStateOf() }` |
| `Column` / `Row` / `Stack` | `Column` / `Row` / `Box` |
| `Container`, `Padding`, `SizedBox`, `GestureDetector` | `Modifier.padding().size().background().clickable()` |
| `SizedBox(height: 16)` | `Spacer(Modifier.height(16.dp))` |
| `ListView.builder` | `LazyColumn { items(list) { } }` |
| `GridView.builder` | `LazyVerticalGrid` |
| `Text`, `Icon`, `Image` | `Text`, `Icon`, `Image` |
| `ElevatedButton`, `TextButton`, `OutlinedButton` | `Button`, `TextButton`, `OutlinedButton` |
| `TextField(controller: ...)` | `TextField(value, onValueChange)` |
| `Scaffold`, `AppBar` | `Scaffold`, `TopAppBar` |
| `Theme.of(context)` | `MaterialTheme.colorScheme` / `.typography` |
| `MainAxisAlignment` | `Arrangement` |
| `CrossAxisAlignment` | `Alignment` |
| `Expanded` | `Modifier.weight(1f)` |
| `initState` / `didUpdateWidget` | `LaunchedEffect(key)` |
| `dispose` | `DisposableEffect { onDispose { } }` |
| `Navigator` / go_router | Navigation Compose (`NavHost`) |
| `AnimatedSwitcher`, `AnimatedContainer` | `AnimatedContent`, `animate*AsState` |
| `const` widget | không cần — Compose tự bỏ qua vẽ lại khi tham số không đổi |
| `16` (logical px) | `16.dp`, chữ: `16.sp` |

### Composable function
```kotlin
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {   // convention: modifier là tham số tùy chọn đầu tiên
    Text(
        text = "Xin chào $name",
        modifier = modifier,
        style = MaterialTheme.typography.titleLarge,
    )
}
```
- Tên hàm `PascalCase` (vì nó "là" một widget).
- Không trả về widget, chỉ **gọi** các composable khác bên trong.
- Không có `BuildContext`.

### Layout + Modifier
```kotlin
Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),                         // thứ tự Modifier có ý nghĩa!
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
) {
    Text("Câu hỏi")
    Row {
        Text("Trái", Modifier.weight(1f))        // ≈ Expanded
        Text("Phải")
    }
    Box(contentAlignment = Alignment.Center) {   // ≈ Stack
        Text("Ở giữa")
    }
}
```
Thứ tự Modifier: `.padding(16.dp).background(Color.Red)` khác `.background(Color.Red).padding(16.dp)` (giống lồng `Padding(Container())` vs `Container(Padding())`).

### State
```kotlin
import androidx.compose.runtime.*      // để có getValue/setValue cho `by`

@Composable
fun FlipCard(front: String, back: String) {
    var flipped by remember { mutableStateOf(false) }     // ≈ biến trong State<> + setState
    // var flipped by rememberSaveable { mutableStateOf(false) }   // sống qua xoay màn hình

    Card(onClick = { flipped = !flipped }) {              // gán là tự vẽ lại, không cần setState
        Text(if (flipped) back else front)
    }
}
```
- `remember` = nhớ giá trị qua các lần vẽ lại (recomposition). Thiếu `remember` thì mỗi lần vẽ lại sẽ reset.
- `mutableStateOf` = giá trị mà Compose "theo dõi", đổi là vẽ lại.
- Có thể không dùng `by`: `val flipped = remember { mutableStateOf(false) }` rồi dùng `flipped.value`.

### State hoisting (≈ "lifting state up" trong Flutter)
```kotlin
@Composable
fun FlipCard(
    text: String,
    onFlip: () -> Unit,            // callback, giống VoidCallback
    modifier: Modifier = Modifier,
) {
    Card(onClick = onFlip, modifier = modifier) { Text(text) }
}
```
Composable "ngu" (stateless) nhận state + callback; composable cha/ViewModel giữ state. Project đang làm đúng vậy: `StudyScreen` (có ViewModel) và `StudyContent` (chỉ vẽ).

### List
```kotlin
LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
) {
    item { Text("Header") }                              // 1 item
    items(cards, key = { it.id }) { card ->              // nhiều item, key ≈ Key của Flutter
        CardRow(card = card, onClick = { onCardClick(card.id) })
    }
}
```
`items(...)` cần `import androidx.compose.foundation.lazy.items`.

### TextField
```kotlin
var front by remember { mutableStateOf("") }
OutlinedTextField(
    value = front,
    onValueChange = { front = it },       // không có controller; tự cập nhật state
    label = { Text("Mặt trước") },
    singleLine = true,
)
```

### Side effect
```kotlin
LaunchedEffect(Unit) {                  // chạy 1 lần khi vào màn (≈ initState), trong coroutine
    viewModel.load()
}
LaunchedEffect(cardId) { ... }          // chạy lại mỗi khi cardId đổi

val scope = rememberCoroutineScope()
Button(onClick = { scope.launch { snackbarHostState.showSnackbar("Đã lưu") } }) { Text("Lưu") }
```

### Preview (≈ widget preview)
```kotlin
@Preview(showBackground = true)
@Composable
private fun FlipCardPreview() {
    FlashcardTheme { FlipCard("Hello", onFlip = {}) }
}
```

### Kết nối ViewModel
```kotlin
@Composable
fun StudyScreen(viewModel: StudyViewModel = viewModel(factory = StudyViewModel.Factory)) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()   // StateFlow → State (≈ ref.watch / BlocBuilder)
    StudyContent(state = state, onFlip = viewModel::onFlip)
}
```

---

## 23. ViewModel + UiState

| Flutter | Android |
|---------|---------|
| ChangeNotifier / Cubit / Notifier | `ViewModel` |
| state của Cubit | `StateFlow<UiState>` |
| `emit(newState)` / `state = ...` | `_uiState.update { it.copy(...) }` |
| BlocBuilder / `ref.watch` | `collectAsStateWithLifecycle()` |
| get_it / Riverpod provider | DI thủ công (`AppContainer`) hoặc Hilt/Koin |

Khung chuẩn:
```kotlin
class DeckViewModel(private val repo: FlashcardRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(DeckUiState())
    val uiState: StateFlow<DeckUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repo.getCards().collect { cards ->
                _uiState.update { it.copy(cards = cards, isLoading = false) }
            }
        }
    }

    fun onFrontChange(text: String) = _uiState.update { it.copy(draftFront = text) }

    fun onSave() {
        val s = _uiState.value
        if (s.draftFront.isBlank()) return
        viewModelScope.launch {
            repo.addCard(s.draftFront, s.draftBack)
            _uiState.update { it.copy(draftFront = "", draftBack = "") }
        }
    }
}

data class DeckUiState(
    val cards: List<Flashcard> = emptyList(),
    val isLoading: Boolean = true,
    val draftFront: String = "",
    val draftBack: String = "",
)
```
ViewModel **sống qua xoay màn hình** (Flutter không có khái niệm này vì không bị recreate Activity).

---

## 24. Room (database) — chuẩn bị cho bước tiếp theo

Room ≈ `drift`/`floor` của Flutter: viết interface + annotation, compiler sinh code. Cần plugin **KSP** (≈ build_runner, nhưng tự chạy khi build).

```kotlin
@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val front: String,
    val back: String,
    val streak: Int = 0,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
)

@Dao
interface CardDao {
    @Query("SELECT * FROM cards ORDER BY created_at DESC")
    fun observeAll(): Flow<List<CardEntity>>          // Flow: tự phát lại khi bảng đổi

    @Query("SELECT * FROM cards WHERE id = :id")       // :id map với tham số id
    suspend fun getById(id: Long): CardEntity?

    @Insert
    suspend fun insert(card: CardEntity): Long          // trả id mới

    @Update
    suspend fun update(card: CardEntity)

    @Upsert
    suspend fun upsert(card: CardEntity)

    @Delete
    suspend fun delete(card: CardEntity)
}

@Database(entities = [CardEntity::class], version = 1)  // [ ] = array literal trong annotation
abstract class AppDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
}

// Tạo database (trong AppContainer)
val db = Room.databaseBuilder(context, AppDatabase::class.java, "flashcard.db").build()
```
- `AppDatabase::class.java` = lấy Java class (Room là thư viện gốc Java).
- Mapper giữa Entity (data) và model (domain) thường viết bằng extension:
```kotlin
fun CardEntity.toDomain() = Flashcard(id = id, front = front, back = back, streak = streak)
fun Flashcard.toEntity() = CardEntity(id = id, front = front, back = back, streak = streak)
```
Khi làm phần này thì `InMemoryFlashcardRepository` sẽ được thay bằng `RoomFlashcardRepository` implement cùng interface `FlashcardRepository`.

---

## 25. Gradle Kotlin DSL (≈ pubspec.yaml)

| Flutter | Android |
|---------|---------|
| `pubspec.yaml` | `app/build.gradle.kts` + `gradle/libs.versions.toml` |
| `flutter pub get` | Sync Gradle (nút con voi 🐘 / "Sync Now") |
| `dependencies:` | `dependencies { implementation(...) }` |
| `dev_dependencies:` | `testImplementation(...)`, `debugImplementation(...)` |
| build_runner | `ksp(...)` |

File `.gradle.kts` chính là code Kotlin:
```kotlin
dependencies {
    implementation(libs.androidx.core.ktx)     // libs.* tham chiếu tới libs.versions.toml
    implementation("androidx.room:room-runtime:2.7.0")   // hoặc viết thẳng chuỗi
    testImplementation(libs.junit)
}
```
Thêm thư viện theo kiểu version catalog: khai báo version ở `[versions]`, thư viện ở `[libraries]` trong `libs.versions.toml`, rồi `implementation(libs.ten.thu.vien)` (dấu `-` trong toml thành `.`).

---

## 26. Đọc code trong project

Code hiện có trong `app/src/main/java/com/huyvu/flashcard/`, cập nhật theo từng task của kế hoạch `docs/superpowers/plans/2026-11-19-flashcard-app.md`. Mỗi file: nó để làm gì, ai gọi nó, nó gọi ai.

### Luồng khởi động app (sau Task 1)

```
Bấm icon app
   │
   ▼
Android tạo FlashcardApplication   (@HiltAndroidApp → Hilt dựng "kho linh kiện")
   │
   ▼
Android mở MainActivity            (@AndroidEntryPoint → được lấy đồ từ kho Hilt)
   │ onCreate → setContent { ... }
   ▼
FlashcardTheme { ... }             (gắn màu + font của thiết kế)
   │
   ▼
Text("Library")                    (tạm thời; Task 6 thay bằng FlashcardNavHost)
```

### Gradle: `gradle/libs.versions.toml`, `app/build.gradle.kts`
- `libs.versions.toml` là danh bạ thư viện: `[versions]` ghi số version, `[libraries]` đặt tên ngắn cho từng thư viện, `[plugins]` cho plugin Gradle.
- Trong `app/build.gradle.kts`, `implementation(libs.hilt.android)` đọc tên ngắn từ danh bạ (dấu `-` trong tên đổi thành `.`).
- `ksp(libs.hilt.compiler)`: KSP là công cụ đọc các chữ `@...` (annotation) lúc build và sinh thêm code. Hilt và Room đều cần nó.
- `isCoreLibraryDesugaringEnabled = true` + `coreLibraryDesugaring(...)`: cho phép dùng `java.time` (`LocalDate`, `Instant`) trên Android 7 (minSdk 24), vốn chưa có sẵn mấy class này.
- `testImplementation(...)`: thư viện chỉ dùng khi chạy unit test (MockK, Turbine, coroutines-test), không bị đóng vào app.

### `FlashcardApplication.kt`
```kotlin
@HiltAndroidApp
class FlashcardApplication : Application()
```
- Để làm gì: object sống suốt đời app; `@HiltAndroidApp` bật Hilt cho cả app.
- Ai gọi: Android tự tạo, nhờ dòng `android:name=".FlashcardApplication"` trong `AndroidManifest.xml`.
- `: Application()` = kế thừa class `Application` và gọi constructor rỗng của nó. Class không có thân `{ }` vẫn hợp lệ.

### `MainActivity.kt`
- Để làm gì: cửa vào duy nhất; app chỉ có 1 Activity, mỗi màn hình là một hàm `@Composable`.
- Ai gọi: Android, khi bấm icon app (intent-filter `MAIN`/`LAUNCHER` trong manifest).
- Nó gọi: `FlashcardTheme`, rồi nội dung bên trong.
- `override fun onCreate(savedInstanceState: Bundle?)`: `Bundle?` có dấu `?` nghĩa là có thể `null` (lần mở đầu tiên thì null).
- `setContent { ... }`: `setContent` là hàm nhận một hàm khác làm tham số (higher-order function). Khối `{ ... }` là lambda được truyền vào; vì nó là tham số cuối nên viết ra ngoài, và bỏ luôn cặp `()`.
- `Modifier.fillMaxSize().background(...)`: mỗi `.xxx()` trả về một Modifier mới đã thêm 1 "lệnh trang trí"; thứ tự có ý nghĩa (phần 29).

### `presentation/theme/Color.kt`
- `object FlashcardColors { val Background = Color(0xFFFFF5F5) ... }`: `object` là class chỉ có đúng một bản, dùng thẳng `FlashcardColors.Ink`.
- `0xFFFFF5F5`: số hệ 16; `FF` đầu là độ đục, `FFF5F5` là mã màu trong thiết kế.

### `presentation/theme/Type.kt`
- `res/font/nunito.ttf` là variable font: một file chứa mọi độ đậm. `nunito(weight)` lấy ra một độ đậm.
- `@OptIn(ExperimentalTextApi::class)`: annotation (nhãn dán cho trình biên dịch đọc) báo rằng mình đồng ý dùng API còn "thử nghiệm".
- `private val materialDefault = Typography()`: bộ kiểu chữ mặc định của Material 3.
- `materialDefault.copy(displayLarge = ..., ...)`: `Typography` không sửa được (toàn `val`), nên `copy` tạo bản MỚI; chỉ những ô được nhắc tên mới đổi, còn lại giữ như bản gốc. `displayLarge = ...` ở đây là **named argument** (truyền đối số theo tên), không phải phép gán.
- `useNunito(style)`: nhận một `TextStyle`, trả về bản copy đã đổi `fontFamily`.

### `presentation/theme/Theme.kt`
- `lightColorScheme(...)`: gán màu thiết kế vào các "vai" của Material 3 (`primary`, `background`...), để nút và ô nhập tự lấy đúng màu.
- `fun FlashcardTheme(content: @Composable () -> Unit)`: kiểu `() -> Unit` = "một hàm không nhận gì, không trả gì"; nhãn `@Composable` thêm điều kiện "hàm đó được phép vẽ giao diện". Bên trong chỉ chuyền `content` cho `MaterialTheme(content = content)`, không gọi nó; `MaterialTheme` sẽ tự gọi `content()` khi vẽ.

### `domain/` (Task 2): Kotlin thuần, không phụ thuộc Android

Ai dùng: sau này repository (Task 4), use case (Task 5) và các ViewModel. Hiện mới chỉ có unit test gọi tới.

```
Flashcard ──có một──► ReviewSchedule ──status()──► CardStatus (NEW / LEARNING / MASTERED)
    │                       ▲          ──isDue(today)──► Boolean
    │ deckId                │
    ▼                       │ Sm2Scheduler.next(lịch cũ, Answer, hôm nay) → lịch MỚI (copy)
  Deck ──có một──► Emblem (Text "あ" | Icon GLOBE) ⇄ chuỗi "text:あ" / "icon:globe"
    ▲
    │ deckId, cardId
ReviewLog (ôn lúc nào, đúng/sai)
```

#### `domain/model/StudyTypes.kt`
- `enum class Answer { AGAIN, GOT_IT }`: kiểu chỉ có đúng các giá trị liệt kê.
- `SessionMode`: `NORMAL` chấm thật; `PRACTICE` học thử, không đổi lịch ôn.

#### `domain/model/ReviewSchedule.kt`
- `data class` với giá trị mặc định: `ReviewSchedule()` = lịch của thẻ mới.
- `dueDate: LocalDate?`: dấu `?` cho phép `null`; `null` = thẻ chưa ôn lần nào (New).
- `fun status()`: hàm thành viên; `when { ... }` không có biến sau `when` thì thử từng điều kiện từ trên xuống.
- `val due = dueDate ?: return true`: toán tử Elvis `?:`, "bên trái null thì làm bên phải".
- `companion object { const val MASTERED_INTERVAL_DAYS = 21 }`: hằng số thuộc về cả class, gọi `ReviewSchedule.MASTERED_INTERVAL_DAYS`.

#### `domain/model/Flashcard.kt`, `Deck.kt`, `ReviewLog.kt`
- Ba `data class` chỉ chứa dữ liệu. `id = 0` = chưa lưu vào database.
- `Instant`: một thời điểm chính xác, không phụ thuộc múi giờ. `LocalDate`: một ngày lịch (không có giờ).

#### `domain/model/Emblem.kt`
- `sealed interface Emblem` + 2 kiểu con `Text`, `Icon`: Emblem chỉ có thể là một trong hai.
- `fun Emblem.encode()`: extension function; `when (this) { is Emblem.Text -> value ... }`: `is` kiểm tra kiểu; trong nhánh đó Kotlin tự coi `this` là `Text` (smart cast) nên đọc được `value`. Không cần `else` vì sealed.
- `enum class EmblemIcon(val key: String)`: enum có thuộc tính; `EmblemIcon.entries` = danh sách mọi giá trị.
- `EmblemIcon.entries.firstOrNull { it.key == rest }`: `firstOrNull` nhận lambda `(EmblemIcon) -> Boolean`, gọi lần lượt với từng icon (tham số `it`), trả về icon đầu tiên làm lambda ra `true`, không có thì `null`.

#### `domain/srs/Sm2Scheduler.kt`
- `object`: một bản duy nhất, gọi `Sm2Scheduler.next(...)`; là hàm thuần (cùng đầu vào → cùng kết quả).
- Got it: lần 1 → 1 ngày, lần 2 → 6 ngày, sau đó `interval × ease` làm tròn (`roundToInt()`, 37.5 → 38).
- Again: về 0, đến hạn hôm nay, `lapses + 1`, ease giảm 0.54 nhưng không dưới 1.3.
- Trả về `current.copy(...)`, không sửa `current`.

#### Test: `app/src/test/.../domain/`
- `@Test` đánh dấu hàm kiểm tra; `assertEquals(mongDoi, thucTe)`; số thực so với sai số: `assertEquals(1.96, x, 1e-9)`.
- Tên hàm trong dấu `` ` `` được phép có dấu cách: `` fun `again resets the card`() ``.
- Chạy: `./gradlew testDebugUnitTest`.

## 27. Bài tổng hợp: Flashcard core chạy được

Dán vào https://play.kotlinlang.org hoặc Scratch File để chạy. Code này **độc lập** với app (dùng thuật toán ôn tập ngắt quãng đơn giản kiểu Anki), dùng gần như mọi syntax ở trên. Đọc từng dòng, đoán output, rồi chạy để kiểm tra.

```kotlin
import kotlin.math.roundToInt

// ---------- Hằng số top-level ----------
const val MINUTE_MS = 60L * 1000
const val DAY_MS = 24L * 60 * MINUTE_MS

// ---------- Enum có thuộc tính ----------
enum class Rating(val label: String) {
    AGAIN("Quên"), HARD("Khó"), GOOD("Được"), EASY("Dễ")
}

// ---------- Data class + default parameter ----------
data class Flashcard(
    val id: Long,
    val front: String,
    val back: String,
    val tags: Set<String> = emptySet(),
    val intervalDays: Int = 0,
    val ease: Double = 2.5,
    val repetitions: Int = 0,
    val dueAt: Long = 0L,
) {
    fun isDue(now: Long): Boolean = dueAt <= now
}

// ---------- Extension function: logic ôn tập, trả thẻ MỚI ----------
fun Flashcard.review(rating: Rating, now: Long): Flashcard {
    val newEase = when (rating) {
        Rating.AGAIN -> ease - 0.2
        Rating.HARD -> ease - 0.15
        Rating.GOOD -> ease
        Rating.EASY -> ease + 0.15
    }.coerceAtLeast(1.3)                         // không cho ease < 1.3

    val newReps = if (rating == Rating.AGAIN) 0 else repetitions + 1

    val newInterval = when {
        rating == Rating.AGAIN -> 0
        newReps == 1 -> 1
        newReps == 2 -> 3
        rating == Rating.HARD -> (intervalDays * 1.2).roundToInt().coerceAtLeast(1)
        rating == Rating.EASY -> (intervalDays * newEase * 1.3).roundToInt()
        else -> (intervalDays * newEase).roundToInt()
    }

    return copy(
        ease = newEase,
        repetitions = newReps,
        intervalDays = newInterval,
        dueAt = if (newInterval == 0) now + 10 * MINUTE_MS else now + newInterval * DAY_MS,
    )
}

// ---------- Class với backing property, require/check, vararg ----------
class Deck(val name: String) {
    private val _cards = mutableListOf<Flashcard>()
    val cards: List<Flashcard> get() = _cards
    val size get() = _cards.size

    private var nextId = 1L

    fun add(front: String, back: String, vararg tags: String): Flashcard {
        require(front.isNotBlank()) { "Mặt trước không được trống" }
        val card = Flashcard(
            id = nextId++,
            front = front.trim(),
            back = back.trim(),
            tags = tags.toSet(),
        )
        _cards += card
        return card
    }

    fun update(card: Flashcard) {
        val index = _cards.indexOfFirst { it.id == card.id }
        check(index != -1) { "Không tìm thấy thẻ ${card.id}" }
        _cards[index] = card
    }

    fun dueCards(now: Long): List<Flashcard> =
        _cards.filter { it.isDue(now) }.sortedBy { it.dueAt }

    fun findByTag(tag: String) = _cards.filter { tag in it.tags }
}

// ---------- Sealed interface cho trạng thái ----------
sealed interface StudyState {
    data object Empty : StudyState
    data class Question(val card: Flashcard, val remaining: Int) : StudyState
    data class Answer(val card: Flashcard, val remaining: Int) : StudyState
    data class Finished(val reviewed: Int, val correct: Int) : StudyState
}

class StudySession(private val deck: Deck, private val now: Long) {
    private val queue = ArrayDeque(deck.dueCards(now))
    private var reviewed = 0
    private var correct = 0

    var state: StudyState = if (queue.isEmpty()) StudyState.Empty else nextQuestion()
        private set

    private fun nextQuestion(): StudyState {
        val card = queue.firstOrNull() ?: return StudyState.Finished(reviewed, correct)
        return StudyState.Question(card, queue.size)
    }

    fun flip() {
        val current = state
        if (current is StudyState.Question) {               // smart cast trên biến local
            state = StudyState.Answer(current.card, current.remaining)
        }
    }

    fun rate(rating: Rating) {
        val current = state as? StudyState.Answer ?: return  // ép an toàn + thoát sớm
        deck.update(current.card.review(rating, now))
        queue.removeFirst()
        reviewed++
        if (rating != Rating.AGAIN) correct++
        state = nextQuestion()
    }
}

// ---------- main ----------
fun main() {
    val deck = Deck("Tiếng Anh").apply {
        add("apple", "quả táo", "food")
        add("run", "chạy", "verb")
        add("beautiful", "đẹp", "adj")
        add("eat", "ăn", "verb", "food")
    }

    println("Deck ${deck.name} có ${deck.size} thẻ")
    println("Động từ: " + deck.findByTag("verb").joinToString { it.front })

    val now = System.currentTimeMillis()
    val session = StudySession(deck, now)
    val ratings = listOf(Rating.GOOD, Rating.AGAIN, Rating.EASY)
    var i = 0

    while (true) {
        when (val s = session.state) {
            StudyState.Empty -> {
                println("Không có thẻ nào cần học")
                break
            }
            is StudyState.Question -> {
                println("❓ ${s.card.front}  (còn ${s.remaining})")
                session.flip()
            }
            is StudyState.Answer -> {
                val r = ratings[i++ % ratings.size]
                println("   💡 ${s.card.back} → chọn: ${r.label}")
                session.rate(r)
            }
            is StudyState.Finished -> {
                println("Xong! ${s.correct}/${s.reviewed} nhớ được")
                break
            }
        }
    }

    println()
    deck.cards
        .sortedBy { it.dueAt }
        .forEach { c ->
            val due = if (c.intervalDays == 0) "10 phút nữa" else "${c.intervalDays} ngày nữa"
            println("${c.front.padEnd(10)} | ease=${"%.2f".format(c.ease)} | ôn lại: $due")
        }

    val (easy, hard) = deck.cards.partition { it.intervalDays > 0 }
    println("\nĐã nhớ: ${easy.size}, cần ôn lại sớm: ${hard.size}")
}
```

Bài tập trên đoạn code này:
1. Thêm `Rating.AGAIN` thì đưa thẻ vào cuối `queue` (`queue.addLast(...)`) để ôn lại trong cùng phiên. Cẩn thận vòng lặp vô hạn trong `main`.
2. Viết extension `fun List<Flashcard>.stats(): String` trả về `"x thẻ, y đã nhớ, ease TB z"`.
3. Thêm hàm `Deck.remove(id: Long): Boolean`.
4. Viết extension `fun Deck.toCsv(): String` (mỗi dòng `front,back`) và hàm top-level `fun deckFromCsv(name: String, csv: String): Deck` để đọc ngược lại (dùng `lines()`, `split(",")`, `apply { add(...) }`).

---

## 28. Bảng tra nhanh Dart → Kotlin

| Dart | Kotlin |
|------|--------|
| `void main() {}` | `fun main() {}` |
| `final x = 1;` | `val x = 1` |
| `var x = 1;` | `var x = 1` |
| `const X = 1;` | `const val X = 1` |
| `int x = 1;` | `val x: Int = 1` |
| `String? s;` | `var s: String? = null` |
| `late String s;` | `lateinit var s: String` |
| `s ?? "x"` | `s ?: "x"` |
| `s!` | `s!!` |
| `a ? b : c` | `if (a) b else c` |
| `switch` | `when` |
| `for (var i = 0; i < n; i++)` | `for (i in 0..<n)` |
| `for (final x in list)` | `for (x in list)` |
| `int f(int a) => a * 2;` | `fun f(a: Int) = a * 2` |
| `void f({required int a, int b = 0})` | `fun f(a: Int, b: Int = 0)` |
| `(x) => x * 2` | `{ x -> x * 2 }` hoặc `{ it * 2 }` |
| `void Function(int)` | `(Int) -> Unit` |
| `VoidCallback` | `() -> Unit` |
| `List<int> a = [1, 2];` | `val a = listOf(1, 2)` / `mutableListOf(1, 2)` |
| `{'a': 1}` | `mapOf("a" to 1)` |
| `{1, 2}` | `setOf(1, 2)` |
| `list.where(...)` | `list.filter { }` |
| `list.map(...).toList()` | `list.map { }` |
| `list.length` | `list.size` |
| `[...a, ...b]` | `a + b` |
| `class A extends B` | `class A : B()` (B phải `open`) |
| `class A implements I` | `class A : I` |
| `with Mixin` | interface có default method / `by` |
| `@override` | `override` (keyword) |
| `_private` | `private` |
| `static` | `companion object { }` |
| `factory A.from()` | `companion object { fun from() = A(...) }` |
| `A.named()` constructor | default param / companion fun |
| freezed / equatable | `data class` |
| `copyWith` | `copy` |
| `enum` | `enum class` |
| `sealed class` | `sealed interface` / `sealed class` |
| `extension on String` | `fun String.xxx()` |
| `x is T` / `x as T` | `x is T` / `x as T` / `x as? T` |
| `identical(a, b)` | `a === b` |
| `Future<T> f() async` | `suspend fun f(): T` |
| `await f()` | `f()` |
| `Future.wait` | `async {}` + `awaitAll()` |
| `Stream<T>` | `Flow<T>` |
| `async*` / `yield` | `flow { emit() }` |
| `stream.listen` | `flow.collect { }` |
| `StreamController.broadcast` | `MutableSharedFlow` |
| `ValueNotifier<T>` | `MutableStateFlow<T>` |
| `try {} on E catch (e) {}` | `try {} catch (e: E) {}` |
| `print()` | `println()` |
| `assert(x)` | `require(x)` / `check(x)` |
| `int.tryParse(s)` | `s.toIntOrNull()` |
| `x.toString()` | `x.toString()` |
| `cascade ..` | `apply { }` |
| `typedef` | `typealias` |
| `dynamic` | (không có) `Any?` |
| `Object` | `Any` |
| `void` | `Unit` |
| `Never` | `Nothing` |
| `(int, String)` record | `Pair<Int, String>` / data class |
| `pubspec.yaml` | `build.gradle.kts` + `libs.versions.toml` |

---

## 29. Những cái bẫy hay dính

1. **`5 / 2 == 2`**: chia số nguyên ra số nguyên. Muốn số thực thì `toDouble()`.
2. **`'a'` là `Char`, không phải String.** String luôn dùng `"`.
3. **`??` → `?:`**, `!` → `!!`.
4. **Class mặc định `final`**: muốn kế thừa phải `open`.
5. **Không ép kiểu số ngầm**: `Int` không gán vào `Long`.
6. **`List` không có `add`**: cần `MutableList`. Nhưng trong state thì đừng dùng MutableList, tạo list mới.
7. **Smart cast không ăn với `var` property**: copy ra `val` local hoặc dùng `?.let`.
8. **`return` trong lambda**: dùng `return@forEach` để bỏ qua phần tử; `return` trần sẽ thoát cả hàm.
9. **Lambda không viết `return`**: dòng cuối là giá trị trả về.
10. **Quên `remember`** trong Compose → state reset mỗi lần vẽ lại.
11. **Quên import `getValue`/`setValue`** khi dùng `by remember { mutableStateOf() }` → lỗi đỏ, Alt+Enter để import.
12. **Đổi object bên trong state mà không tạo mới** (`list.add()`, sửa field của `var`) → UI không cập nhật. Luôn `copy()` / tạo list mới.
13. **Gọi hàm `suspend` ngoài coroutine** → lỗi compile. Bọc trong `viewModelScope.launch { }` hoặc `LaunchedEffect`.
14. **`runBlocking` trong app** → đứng UI. Chỉ dùng trong `main` / test.
15. **`map["key"]` trả nullable** → cần `?:` hoặc `getValue`.
16. **`==` vs `===`**: `==` là so sánh nội dung (đúng cái hay cần).
17. **`Int` là 32-bit**: timestamp millisecond phải dùng `Long` (`System.currentTimeMillis()` trả Long).
18. **Thứ tự Modifier** có ý nghĩa: padding trước hay background trước cho kết quả khác.
19. **Hot reload**: Compose có *Live Edit* / *Apply Changes* nhưng không mạnh như Flutter. Đổi cấu trúc class/ViewModel thường phải chạy lại app. Dùng `@Preview` để xem UI nhanh.
20. **Gradle sync**: sửa `build.gradle.kts` / `libs.versions.toml` xong phải Sync thì mới có thư viện.
