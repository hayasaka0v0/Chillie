# Flashcard (Kotlin + Jetpack Compose)

Mục đích chính của project: **học Kotlin / Android native**. App Flashcard chỉ là bài tập để học. Người dùng là dev Flutter/Dart đã có kinh nghiệm, mới chuyển sang Kotlin.

## Tài liệu lý thuyết: `docs/kotlin-syntax-cho-dev-flutter.md`

Đây là giáo trình chính. Khi người dùng hỏi về "phần N", "bài tập", "bẫy", một cú pháp Kotlin, hoặc Compose/ViewModel/Room/Coroutines/Flow, hãy mở file này (grep theo heading `## N.`) trước khi trả lời, để câu trả lời khớp với cách giải thích và ví dụ đã có.

Các mục hay được nhắc tới:
- **1–21**: cú pháp Kotlin, mỗi phần so sánh với Dart.
- **22–25**: Compose, ViewModel + UiState, Room, Gradle Kotlin DSL.
- **26 "Đọc code trong project"**: giải thích từng file trong `app/src`.
- **27**: bài tổng hợp Flashcard chạy độc lập trên Playground.
- **28** bảng tra Dart → Kotlin · **29** những cái bẫy hay dính.

Khi sửa/thêm file trong `app/src`, cập nhật mục 26 cho khớp với code mới. Khi người dùng muốn ghi lại kiến thức mới, thêm vào file này đúng mục thay vì tạo file docs mới.

## Mục tiêu hiện tại

Dựng lại đúng luồng và giao diện của bản thiết kế "Flashcard App UI" (canvas Claude Design, 4 màn: Library → Deck detail → Flashcard → Session complete): https://claude.ai/artifact/2KACQe3Z6buJKv3AqYBBVH. Vừa làm vừa học; người dùng nhắm tới việc làm Android dev Fresher/Junior nên kiến trúc (MVVM, repository, use case) vẫn cần nắm.

Quy tắc dạy (bắt buộc):
- KHÔNG coi người dùng đã biết lambda, higher-order function, function reference (`::`), trailing lambda, `it`. Gặp chúng trong code thì tách ra giải thích từng phần cú pháp.
- Giải thích TỪ NỀN LÊN, không nói như điều hiển nhiên: mỗi khái niệm phải được định nghĩa (bằng ví dụ đời thường + 1 đoạn Kotlin nhỏ chạy được) TRƯỚC khi dùng nó để giải thích khái niệm khác. Một thuật ngữ chưa giải thích (annotation, receiver/`this`, named argument, immutable, `copy`, scope function, extension...) thì không được xuất hiện trơn.
- Code mẫu ưu tiên cách viết thẳng, dễ đọc; tránh mẹo rút gọn (`run`, `apply`, lồng lambda) khi cách viết thường rõ hơn. Không đặt tên biến trùng tên class.
- Khi viết hoặc giải thích một file: nói file đó tồn tại để làm gì, ai gọi nó, nó gọi ai, kèm sơ đồ luồng chạy.
- Feedback loop: mỗi bài kết thúc bằng 3–5 câu hỏi kiểm tra, chờ người dùng trả lời rồi mới sang bài mới.
- Khi giải thích code, hỏi lại xem người dùng có hiểu **cú pháp** của đoạn đó không (chứ không chỉ hiểu nó làm gì).
- Phần nói về luồng (data flow, lifecycle, coroutine...) phải có sơ đồ/hình.
- Code nhắc tới phải có link file kèm dòng, ví dụ `[StudyViewModel.kt:40](app/src/main/java/com/huyvu/flashcard/presentation/study/StudyViewModel.kt:40)`.
- Giọng văn: vui nhộn, hóm hỉnh, ít thuật ngữ, dùng ví dụ đời thường.

## Cách hướng dẫn

- Giải thích bằng tiếng Việt. KHÔNG so sánh với Flutter/Dart nữa (người dùng yêu cầu), dùng ví dụ đời thường thay thế.
- Cách học: Claude viết code mẫu (chú thích cú pháp Kotlin mới xuất hiện lần đầu), người dùng đọc rồi hỏi lại những chỗ chưa hiểu.
- App làm để ghi vào CV: các tính năng phải chạy thật, dùng các thư viện mà nhà tuyển dụng Android hay yêu cầu.

## Cấu trúc dự án

MVVM + Repository (domain / data / presentation), state đi một chiều qua `StateFlow`. Đang làm theo kế hoạch `docs/superpowers/plans/2026-11-19-flashcard-app.md` (thiết kế: `docs/superpowers/specs/2026-11-19-flashcard-app-design.md`). Khi thêm/xoá/đổi tên file, cập nhật cây này.

```
CLAUDE.md                      ← file này
docs/kotlin-syntax-cho-dev-flutter.md   ← giáo trình (mục 1–29)
build.gradle.kts, settings.gradle.kts   ← Gradle gốc
gradle/libs.versions.toml      ← version các thư viện (≈ pubspec.yaml)
app/build.gradle.kts           ← dependencies của module app
docs/superpowers/              ← bản thiết kế (specs/) và kế hoạch (plans/)
app/src/
├── main/AndroidManifest.xml   ← khai báo FlashcardApplication, MainActivity
├── main/res/                  ← strings, theme XML, icon (ít khi sửa)
├── main/res/font/nunito.ttf   ← font Nunito (variable font, giấy phép OFL)
├── main/java/com/huyvu/flashcard/
│   ├── FlashcardApplication.kt   ← @HiltAndroidApp, sống suốt app
│   ├── MainActivity.kt           ← @AndroidEntryPoint, điểm vào, setContent { FlashcardTheme { ... } }
│   ├── domain/model/Flashcard.kt ← bản cũ, Task 2 viết lại
│   └── presentation/theme/Color.kt, Type.kt, Theme.kt  ← màu + font Nunito của thiết kế, chỉ giao diện sáng
├── test/java/com/huyvu/flashcard/ ← unit test chạy trên JVM (hiện chỉ có file mẫu)
└── androidTest/                 ← test cần máy/emulator (hiện chỉ có file mẫu)
```

## Lệnh

Windows, dùng `./gradlew` từ Git Bash hoặc `.\gradlew.bat` từ PowerShell:
- Unit test: `./gradlew testDebugUnitTest`
- Build debug: `./gradlew assembleDebug`
