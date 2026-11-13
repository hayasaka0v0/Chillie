# Flashcard (Kotlin + Jetpack Compose)

Mục đích chính của project: **học Kotlin / Android native**. App Flashcard chỉ là bài tập để học. Người dùng là dev Flutter/Dart đã có kinh nghiệm, mới chuyển sang Kotlin.

## Tài liệu lý thuyết: `docs/kotlin-syntax-cho-dev-flutter.md`

Đây là giáo trình chính. Khi người dùng hỏi về "phần N", "bài tập", "lộ trình", "bẫy", một cú pháp Kotlin, hoặc Compose/ViewModel/Room/Coroutines/Flow, hãy mở file này (grep theo heading `## N.`) trước khi trả lời, để câu trả lời khớp với cách giải thích và ví dụ đã có.

Các mục hay được nhắc tới:
- **1–21**: cú pháp Kotlin, mỗi phần so sánh với Dart.
- **22–25**: Compose, ViewModel + UiState, Room, Gradle Kotlin DSL.
- **26 "Đọc code trong project"**: giải thích từng file trong `app/src`.
- **27**: bài tổng hợp Flashcard chạy độc lập trên Playground.
- **28** bảng tra Dart → Kotlin · **29** những cái bẫy hay dính · **30** lộ trình 3 tuần & bài tập (thứ tự tính năng cần làm cho app).

Khi sửa/thêm file trong `app/src`, cập nhật mục 26 cho khớp với code mới. Khi người dùng muốn ghi lại kiến thức mới, thêm vào file này đúng mục thay vì tạo file docs mới.

## Cách hướng dẫn

- Giải thích bằng tiếng Việt, đối chiếu với khái niệm Flutter/Dart tương đương (Widget ↔ Composable, Stream ↔ Flow, ValueNotifier/Riverpod ↔ StateFlow, pubspec ↔ Gradle...).
- Ưu tiên giải thích *vì sao* và để người dùng tự viết; khi viết code hộ, chú thích các cú pháp Kotlin mới xuất hiện lần đầu.
- Bám theo lộ trình mục 30: tính năng làm theo thứ tự ở đó (danh sách thẻ → thêm/sửa → Navigation → Room → Deck → ôn tập ngắt quãng).

## Kiến trúc hiện tại

Package `com.huyvu.flashcard`, chia lớp kiểu clean architecture:
- `domain/` — model, interface repository, use case (Kotlin thuần, không phụ thuộc Android).
- `data/` — implement repository (hiện là `InMemoryFlashcardRepository`, sẽ thay bằng Room).
- `di/AppContainer.kt` — DI thủ công, khởi tạo trong `FlashcardApplication` (chưa dùng Hilt).
- `presentation/` — Compose screen + ViewModel + `UiState`, state đi theo hướng một chiều qua `StateFlow`.

## Lệnh

Windows, dùng `./gradlew` từ Git Bash hoặc `.\gradlew.bat` từ PowerShell:
- Unit test: `./gradlew testDebugUnitTest`
- Build debug: `./gradlew assembleDebug`
