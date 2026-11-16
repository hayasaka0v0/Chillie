# Sổ học tập

Claude đọc file này + [loi-sai.md](loi-sai.md) đầu mỗi session để dạy tiếp đúng chỗ.

> **Đang dở (16/11):** Buổi 1 đã giảng, chờ học viên trả lời 3 câu cuối bài trong [buoi-01-toan-canh-man-hinh.md](buoi-01-toan-canh-man-hinh.md). Session mới: hỏi lại 3 câu đó, chấm, rồi sang Buổi 2.

## Học viên

- Dev Flutter, trình độ "trên intern một chút": biết thuật ngữ, biết nó dùng để làm gì, nhưng chưa nắm bản chất và chưa tự code được những phần đó.
- Mục tiêu: đi xin việc **Android dev Fresher/Junior** (Kotlin + Jetpack Compose).
- Flutter state management đã dùng: **Riverpod** → so sánh ViewModel ≈ Notifier, StateFlow ≈ state của Notifier, `collectAsStateWithLifecycle` ≈ `ref.watch`, Factory/AppContainer ≈ khai báo provider.
- Cách học muốn (16/11): **Toàn cảnh → đọc code mẫu (màn Study) → tự viết**. Chưa hiểu flow thì đừng giao viết file.
- Nhịp: **1 ý/buổi, đào sâu**.
- Chạy app: Run được trong Android Studio nhưng **màn hình đen, không thấy gì** (16/11) → cần kiểm tra.

## Tiến độ

| Ngày | Bài | Trạng thái | Ghi chú |
|---|---|---|---|
| 2026-11-14 | Setup sổ học tập + CLAUDE.md | Xong | Bài 0 (luồng bấm "Đúng rồi!") đã giảng sơ nhưng chưa học chính thức, 4 câu hỏi chưa trả lời → có thể dạy lại khi bắt đầu |
| 2026-11-14 | Đặt tên & vị trí màn chính (Home vs theo công dụng, bottom nav, splash, package theo tính năng) | Xong (câu củng cố: A sai, B đúng) | Quyết định: màn đầu tiên là `CardListScreen` trong `presentation/cardlist/`; chưa làm bottom nav (bước 3 lộ trình) |
| 2026-11-14 | Xây `CardListScreen` — bước 1: `CardListUiState.kt` | Chuyển xuống dòng 16/11 bên dưới | Kèm câu hỏi lại về `content = { ... }` (Card/Column) |
| 2026-11-14 | Chen ngang: dấu `{ }` (block vs lambda, `it`, dòng cuối là kết quả, bẫy `{}` = Map bên Dart) | Xong, 3/3 đúng | Học viên tự hỏi sau khi sai câu `content = Text(...)`. Lưu ý nhỏ: dịch `val` → `var` (đúng phải là `final`) |
| 2026-11-16 | Giảng lại: Compose không có `child`/`children`, `Text()` ≈ `print()` | Chờ trả lời 3 câu dịch Flutter → Compose | Sai câu Card/Column lần 2 → đã vào "Lỗi lặp lại". `CardListUiState.kt` vẫn tạm dừng |
| 2026-11-16 | Chen ngang: "Kotlin mọi thứ là function?" → phân biệt object (class) vs composable function | Xong: phân loại 5/5, câu "vì sao" đúng ý | Giả thuyết đúng một nửa (đúng với Compose, sai với Kotlin) |
| 2026-11-16 | Bài dịch Flutter → Compose (3 câu) | Chấm: 1 đúng một phần, 2 sai | Đúng `{ }` với 1 con, sai với nhiều con/lồng nhau; dùng `'...'` cho chuỗi. Dạy quy tắc `( )` = cài đặt, `{ }` = ruột. Chờ 2 câu luyện tiếp. `CardListUiState.kt` vẫn tạm dừng |
| 2026-11-16 | Luyện dịch Column/Row lồng nhau + Char vs String | Char/String đúng; câu dịch đúng cấu trúc, còn 4 lỗi nhỏ | Dạy thêm: `( )` ngăn bằng dấu phẩy, `{ }` ngăn bằng xuống dòng; `()` sau tên = gọi ngay. Chờ 2 câu kiểm tra nhanh, sau đó làm `CardListUiState.kt` |
| 2026-11-16 | Kiểm tra nhanh: `onClick = x` / `x()` / `{ x() }` + bắt lỗi `,` và `'B'` | Xong, 2/2 đúng | Giải thích (a) chưa chính xác: (a) là truyền chính cái hàm, không phải "viết lambda" |
| 2026-11-16 | Xây `CardListScreen` — bước 1: `CardListUiState.kt` (viết trong Android Studio) | Xong, đúng | Dùng khung có chỗ trống. Thừa `{ }` rỗng sau `)` (template Android Studio) → đã nhắc xoá |
| 2026-11-16 | Xây `CardListScreen` — bước 2: `CardListViewModel.kt` | Đang chờ học viên viết | Khung có chỗ trống, 1 câu hỏi |

## Quan sát (Claude cập nhật dần)

- Điểm mạnh: nhận ra lambda ngay; đặt câu hỏi thiết kế tốt (đặt tên, vị trí file) trước khi code.
- Điểm cần củng cố: trailing lambda (chưa gọi được tên); **mang thói quen Flutter `child: Widget(...)` sang Compose** — chưa phân biệt "truyền kết quả" với "truyền lambda". Hỏi lại chủ đề này ở các bài sau.
- Đã hiểu: package theo tính năng (sau khi được giải thích, trả lời đúng câu củng cố); `{ }` thùng vs lambda, lambda chưa chạy đến khi được gọi, dòng cuối là kết quả.
- Thói quen: trả lời ngắn, hay bỏ phần "vì sao" → nhắc giải thích lý do để luyện nói khi phỏng vấn.
- Hiểu lambda ở dạng Kotlin thuần (3/3) nhưng **chưa nối được sang Compose**: thấy `Text(...)` là nghĩ ngay object như Flutter. Lý thuyết đúng mà áp vào UI thì quay lại phản xạ cũ → cần bài tập dịch Flutter → Compose lặp lại nhiều lần.
- Thành thật nói "không biết / không nhớ" kèm lý do → rất tốt, dễ chẩn đoán.
- Tiến bộ 16/11: phân loại object vs composable 5/5, tự giải thích được "content cần callback, Text() thì vẽ luôn". Lỗi `{ }` giờ chỉ còn ở trường hợp nhiều con/lồng nhau.
- Hiểu lý thuyết nhanh nhưng **tay gõ theo phản xạ Dart** (nháy đơn, dấu phẩy, `()`), đúng/sai lẫn lộn ngay trong cùng một câu → cần cho viết code thật trong IDE để compiler bắt lỗi, thay vì chỉ gõ trong chat.
- 16/11: bị ngợp khi giao việc kèm giải thích dài + nhiều câu hỏi → giao việc bằng khung code có chỗ trống, tối đa 1–2 câu hỏi.
- Có thói quen tự rút ra quy luật tổng quát ("Flutter là X thì Kotlin là Y") → tốt, nhưng hay khái quát rộng quá (gộp Kotlin với Compose). Khi chấm, chỉ rõ phạm vi đúng của quy luật.

## Bài tiếp theo dự kiến

Lộ trình "1 ý/buổi" (chốt 16/11), sau đó quay lại mục 30 giáo trình:

| Buổi | Một ý | So với Riverpod | Trạng thái |
|---|---|---|---|
| 1 | [Toàn cảnh](buoi-01-toan-canh-man-hinh.md): một màn hình chạy thế nào (Screen ↔ ViewModel ↔ Repository), lúc mở màn & lúc bấm nút | `ref.watch` → Notifier → repository | Đã giảng 16/11, chờ trả lời 3 câu (kể lại luồng lật thẻ, vì sao Screen không tự sửa state, cú pháp `::`) |
| 2 | UiState: vì sao `data class`, `val`, `copy` | `state = state.copyWith(...)` | |
| 3 | ViewModel: `_uiState`/`uiState`, `init`, `collect`, `viewModelScope` | class `Notifier` | |
| 4 | Screen: `collectAsStateWithLifecycle`, gửi sự kiện lên (`::`) | `ref.watch` / `ref.read(...notifier)` | |
| 5 | Factory + AppContainer: ViewModel được tạo từ đâu | khai báo provider, `ProviderScope` | |
| 6 | Ghép lại: hoàn thiện `CardListScreen` + chạy thử (sửa luôn lỗi màn đen) | | |

`CardListUiState.kt` và `CardListViewModel.kt` đã viết xong (đúng, còn thừa `{ }` rỗng trong UiState), để nguyên đến buổi 6.
