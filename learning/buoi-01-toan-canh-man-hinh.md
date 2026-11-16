# Buổi 1: Một màn hình chạy thế nào? 🗺️

> Trạng thái: **đã giảng (16/11), học viên chưa trả lời 3 câu cuối bài.** Session sau: mở đầu bằng 3 câu này.

Chưa viết file nào trong buổi này. Mục tiêu là nắm được bức tranh dưới đây.

## Sơ đồ 3 tầng

```
            ┌──────────────────────────┐
            │       StudyScreen        │  chỉ vẽ theo state      ≈ ConsumerWidget
            └──────────────────────────┘
   state: uiState  ▲            │  sự kiện: onFlip()
   ≈ ref.watch     │            ▼  ≈ ref.read(notifier)
            ┌──────────────────────────┐
            │      StudyViewModel      │  giữ state, xử lý sự kiện   ≈ Notifier
            └──────────────────────────┘
 getCards(): Flow  ▲            │  updateCard(card)
 kho phát list mới │            ▼  ghi vào kho
            ┌──────────────────────────┐
            │   FlashcardRepository    │  kho dữ liệu thẻ        ≈ repository provider
            └──────────────────────────┘

   ▲ dữ liệu đi lên màn hình      ▼ sự kiện đi xuống kho
```

## Bạn đã biết bức tranh này, chỉ là bằng Riverpod

```dart
class StudyNotifier extends Notifier<StudyState> {
  void onFlip() => state = state.copyWith(isFlipped: !state.isFlipped);
}
// Trong widget:
final state = ref.watch(studyProvider);
onTap: () => ref.read(studyProvider.notifier).onFlip();
```

| Vai | Riverpod | Android (project này) |
|---|---|---|
| Người vẽ | `ConsumerWidget` | `StudyScreen` ([StudyScreen.kt](../app/src/main/java/com/huyvu/flashcard/presentation/study/StudyScreen.kt)) |
| Người quản lý | `Notifier` | `StudyViewModel` ([StudyViewModel.kt](../app/src/main/java/com/huyvu/flashcard/presentation/study/StudyViewModel.kt)) |
| Bảng trạng thái | `state` | `uiState` (`StudyUiState`, [StudyUiState.kt](../app/src/main/java/com/huyvu/flashcard/presentation/study/StudyUiState.kt)) |
| Nhìn state | `ref.watch(...)` | `collectAsStateWithLifecycle()` |
| Báo sự kiện | `ref.read(...notifier).onFlip()` | `viewModel.onFlip()` |
| Kho | repository provider | `FlashcardRepository` |

**Nguyên tắc một câu:** màn hình không tự sửa gì cả. Nó chỉ (1) nhìn state để vẽ và (2) báo sự kiện lên. Mọi thay đổi đều do ViewModel làm.

## Thời điểm 1: lúc mở màn hình

| # | Chuyện gì xảy ra | Code |
|---|---|---|
| 1 | App mở, đặt `StudyScreen` lên màn hình | [MainActivity.kt:21](../app/src/main/java/com/huyvu/flashcard/MainActivity.kt) |
| 2 | Screen xin một ViewModel (lần đầu tạo mới, sau đó dùng lại) | [StudyScreen.kt:31](../app/src/main/java/com/huyvu/flashcard/presentation/study/StudyScreen.kt) |
| 3 | ViewModel vừa sinh ra là chạy `init`: bắt đầu **nghe** kho (≈ `build()` của Notifier) | [StudyViewModel.kt:29-35](../app/src/main/java/com/huyvu/flashcard/presentation/study/StudyViewModel.kt) |
| 4 | Kho đưa list 4 thẻ → ViewModel ghi vào bảng `_uiState` | [StudyViewModel.kt:33](../app/src/main/java/com/huyvu/flashcard/presentation/study/StudyViewModel.kt) |
| 5 | Screen đang nhìn bảng → thấy đổi → vẽ thẻ "Hello" | [StudyScreen.kt:33](../app/src/main/java/com/huyvu/flashcard/presentation/study/StudyScreen.kt) |

## Thời điểm 2: lúc bấm vào thẻ để lật

| # | Chuyện gì xảy ra | Code |
|---|---|---|
| 1 | Bấm `Card` → gọi `onFlip` | [StudyScreen.kt:73](../app/src/main/java/com/huyvu/flashcard/presentation/study/StudyScreen.kt) |
| 2 | `onFlip` chính là hàm `onFlip` của ViewModel, được truyền xuống | [StudyScreen.kt:37](../app/src/main/java/com/huyvu/flashcard/presentation/study/StudyScreen.kt) |
| 3 | ViewModel tạo bảng mới với `isFlipped` đảo ngược | [StudyViewModel.kt:38](../app/src/main/java/com/huyvu/flashcard/presentation/study/StudyViewModel.kt) |
| 4 | Screen thấy bảng đổi → vẽ lại → hiện "Xin chào" | [StudyScreen.kt:78](../app/src/main/java/com/huyvu/flashcard/presentation/study/StudyScreen.kt) |

Lật thẻ không đụng tới kho (chỉ là hiển thị). Bấm "Đúng rồi!" thì đi tiếp xuống kho để lưu streak (mũi tên `updateCard` dưới cùng).

**Vì sao phải chia ra?** Xoay ngang điện thoại → Android huỷ và vẽ lại toàn bộ màn hình, nhưng ViewModel **sống sót**. State nằm trong Screen thì xoay một cái là quay về thẻ số 1.

## 🎯 3 câu kiểm tra (chưa trả lời)

1. Không nhìn bảng, kể lại 4 bước khi bấm lật thẻ, dùng các tên: `Card(onClick)`, `onFlip`, `_uiState`, Screen vẽ lại.
2. Vì sao `StudyScreen` không tự giữ biến `isFlipped` rồi tự đảo nó, mà phải nhờ ViewModel? (Có thể trả lời bằng ví dụ Riverpod.)
3. (Cú pháp) Ở [StudyScreen.kt:37](../app/src/main/java/com/huyvu/flashcard/presentation/study/StudyScreen.kt) có `onFlip = viewModel::onFlip`. Dấu `::` nghĩa là gì? Gợi ý: bài trước, cách (a) `onClick = onRight`.

<!-- Đáp án cho Claude (đừng đọc cho học viên trước khi họ trả lời):
1. Card(onClick = onFlip) bấm → onFlip (= viewModel::onFlip) → _uiState.update { it.copy(isFlipped = !it.isFlipped) } → uiState phát giá trị mới, collectAsStateWithLifecycle nhận → Screen vẽ lại, Text hiện card.back.
2. Screen bị huỷ/vẽ lại (xoay màn hình, recompose) còn ViewModel sống sót; một nguồn sự thật duy nhất; Screen chỉ vẽ nên dễ preview/test. Riverpod: không sửa state trong widget, gọi notifier.
3. `::` = tham chiếu tới hàm (function reference): đưa chính hàm onFlip của viewModel, không gọi ngay. ≈ tear-off `notifier.onFlip` bên Dart. Tương đương `{ viewModel.onFlip() }`.
-->
