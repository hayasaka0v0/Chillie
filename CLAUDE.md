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

## Sổ học tập: `learning/` (đọc đầu MỖI session)

Mục tiêu của người dùng: xin việc Android dev Fresher/Junior. Trình độ Flutter "trên intern một chút": biết thuật ngữ, chưa nắm bản chất.

| File | Chứa gì |
|---|---|
| `learning/README.md` | Hồ sơ học viên, bảng tiến độ từng bài, nhận xét điểm mạnh/yếu, bài tiếp theo dự kiến |
| `learning/loi-sai.md` | Nhật ký câu trả lời sai + mục "Lỗi lặp lại" (sai từ 2 lần trở lên) |

- Đầu session: đọc 2 file trên. Mở đầu buổi học bằng 1 câu ôn lại từ mục "Lỗi lặp lại" nếu có.
- Sau mỗi lượt người dùng trả lời: chấm điểm, ghi câu sai vào `loi-sai.md`, cập nhật bảng tiến độ và mục "Quan sát" trong `README.md`.

Quy tắc dạy (bắt buộc):
- Feedback loop: mỗi bài kết thúc bằng 3–5 câu hỏi kiểm tra, chờ người dùng trả lời rồi mới sang bài mới.
- Khi giải thích code, hỏi lại xem người dùng có hiểu **cú pháp** của đoạn đó không (chứ không chỉ hiểu nó làm gì).
- Phần nói về luồng (data flow, lifecycle, coroutine...) phải có sơ đồ/hình.
- Code nhắc tới phải có link file kèm dòng, ví dụ `[StudyViewModel.kt:40](app/src/main/java/com/huyvu/flashcard/presentation/study/StudyViewModel.kt:40)`.
- Giọng văn: vui nhộn, hóm hỉnh, ít thuật ngữ, dùng ví dụ đời thường.

## Cách hướng dẫn

- Giải thích bằng tiếng Việt, đối chiếu với khái niệm Flutter/Dart tương đương (Widget ↔ Composable, Stream ↔ Flow, ValueNotifier/Riverpod ↔ StateFlow, pubspec ↔ Gradle...).
- Ưu tiên giải thích *vì sao* và để người dùng tự viết; khi viết code hộ, chú thích các cú pháp Kotlin mới xuất hiện lần đầu.
- Bám theo lộ trình mục 30: tính năng làm theo thứ tự ở đó (danh sách thẻ → thêm/sửa → Navigation → Room → Deck → ôn tập ngắt quãng).

## Cấu trúc dự án

Chia lớp kiểu clean architecture, state đi một chiều qua `StateFlow`. Khi thêm/xoá/đổi tên file, cập nhật cây này.

```
CLAUDE.md                      ← file này
docs/kotlin-syntax-cho-dev-flutter.md   ← giáo trình (mục 1–30)
learning/                      ← sổ học tập (xem mục trên)
build.gradle.kts, settings.gradle.kts   ← Gradle gốc
gradle/libs.versions.toml      ← version các thư viện (≈ pubspec.yaml)
app/build.gradle.kts           ← dependencies của module app
app/src/
├── main/AndroidManifest.xml   ← khai báo FlashcardApplication, MainActivity
├── main/res/                  ← strings, theme XML, icon (ít khi sửa)
├── main/java/com/huyvu/flashcard/
│   ├── FlashcardApplication.kt   ← sống suốt app, giữ AppContainer
│   ├── MainActivity.kt           ← điểm vào, setContent { StudyScreen() }
│   ├── di/AppContainer.kt        ← DI thủ công: chọn bản Repository nào (chưa Hilt)
│   ├── domain/                   ← Kotlin thuần, không phụ thuộc Android
│   │   ├── model/Flashcard.kt          ← data class id/front/back/streak
│   │   ├── repository/FlashcardRepository.kt  ← interface getCards()/updateCard()
│   │   └── usecase/AnswerCardUseCase.kt       ← đúng: streak+1, sai: về 0
│   ├── data/repository/InMemoryFlashcardRepository.kt  ← lưu trong MutableStateFlow (sẽ thay bằng Room)
│   └── presentation/
│       ├── study/StudyScreen.kt     ← Composable: StudyScreen (lấy VM) + StudyContent (chỉ vẽ)
│       ├── study/StudyViewModel.kt  ← nhận sự kiện, gọi use case, giữ uiState + Factory
│       ├── study/StudyUiState.kt    ← data class state của màn học
│       └── theme/Color.kt, Theme.kt, Type.kt  ← theme Material 3
├── test/java/com/huyvu/flashcard/
│   └── domain/usecase/AnswerCardUseCaseTest.kt  ← unit test chạy trên JVM
└── androidTest/                 ← test cần máy/emulator (hiện chỉ có file mẫu)
```

## Lệnh

Windows, dùng `./gradlew` từ Git Bash hoặc `.\gradlew.bat` từ PowerShell:
- Unit test: `./gradlew testDebugUnitTest`
- Build debug: `./gradlew assembleDebug`
