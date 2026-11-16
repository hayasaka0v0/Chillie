# Câu trả lời sai & lỗi hay lặp lại

Mỗi khi học viên trả lời sai hoặc hiểu nhầm, Claude thêm một dòng. Câu nào sai **2 lần trở lên** thì chuyển lên mục "Lỗi lặp lại" và hỏi lại ở các bài sau.

## Lỗi lặp lại (ưu tiên ôn)

- **Truyền `Text(...)` / danh sách con như Flutter thay vì truyền lambda** (sai 2 lần: 14/11, 16/11). Học viên nghĩ `content = Text(...)` "không sai vì Flutter viết vậy" và không biết `Column` nhận con thế nào. Cách giải thích đã dùng: (1) bánh đã nướng vs công thức (14/11, chưa thấm); (2) `Text()` trong Compose giống `print()`: chạy ngay, không trả về gì, `Column` không có `children`, gọi lần lượt trong `{ }` (16/11). Lần sau: cho dịch 1 widget Flutter có `child`/`children` sang Compose ngay đầu buổi.
  - Lần 3 (16/11, bài dịch): `content = { Text(...) }` khi **1 con** thì đã đúng, nhưng khi **nhiều con / lồng nhau** thì thay `children: [...]` bằng `( ... )`: `Row(Text(..) Text(..))`, `Card(content = Column(Text(..), Text(..)))`. Quy tắc đã dạy: **`( )` = cài đặt, `{ }` = ruột**.
  - Lần 4 (16/11): cấu trúc `Column { Row { } }` đã đúng 🎉, nhưng trong cùng một câu có nút viết `content = { Text(..) }` (đúng) và nút viết `content = Text(..)` (sai) → hiểu rồi nhưng chưa thành phản xạ.
  - Kiểm tra nhanh 16/11: 2/2 đúng (chọn đúng `onClick = onRight` / `{ onRight() }`, bắt được dấu phẩy + nháy đơn). Giữ ở mục này đến khi viết đúng trong code thật (`CardListScreen`).
- **Dùng nháy đơn `'...'` cho chuỗi** (thói quen Dart; 3 lần trong 1 bài, 16/11). Kotlin: `'A'` là `Char`, chuỗi phải `"..."` (bẫy số 2, mục 29 giáo trình). Lần 2 (16/11): trả lời đúng lý thuyết `'A'` là Char nhưng vẫn viết `Text('Thẻ 1/4')` trong cùng lượt.
- **`()` sau tên = gọi ngay**: `onClick = onWrong()` gọi hàm luôn lúc vẽ thay vì truyền đi (16/11). Cùng gốc với lỗi `content = Text(..)`. Đúng: `onClick = onWrong` hoặc `{ onWrong() }`.

## Nhật ký câu sai

| Ngày | Chủ đề | Câu hỏi | Học viên trả lời | Đáp án đúng / hiểu nhầm ở đâu |
|---|---|---|---|---|
| 2026-11-14 | Cú pháp: lambda | `{ innerPadding -> ... }` trong `Scaffold(...)` là gì? | "Lambda, anonymous function" | Đúng một nửa: thiếu khái niệm **trailing lambda** (tham số cuối là hàm thì được đặt lambda ra ngoài ngoặc `()`), chưa nói được `innerPadding` là tham số của lambda |
| 2026-11-14 | Cấu trúc project | Vì sao ViewModel để chung thư mục với Screen của nó (package theo tính năng)? | Không biết | Code của một màn (Screen + ViewModel + UiState) luôn sửa cùng nhau → để chung một chỗ; xoá tính năng thì xoá một thư mục |
| 2026-11-14 | Compose vs Flutter: content là lambda | Viết lại `Button(onClick = onRestart) { Text("Học lại") }` không dùng trailing lambda | `Button(onClick = onRestart, content = Text("Hoc lai")` | Truyền **kết quả** `Text(...)` như `child:` bên Flutter. Compose cần truyền **một hàm**: `content = { Text("Học lại") }`. Thói quen Flutter "truyền widget" → Compose "truyền lambda gọi composable". (Thiếu cả `)` đóng.) |
| 2026-11-14 | Cú pháp: val/var | Dịch `val double = { x: Int -> x * 2 }` sang Dart | `var greet = (int x) => x*2;` | Lambda đúng, nhưng `val` (không gán lại được) ↔ `final` bên Dart, không phải `var`. Lỗi nhỏ, theo dõi xem có lặp lại không |
| 2026-11-16 | Compose vs Flutter: content là lambda (lần 2) | Sửa `Card(onClick = onFlip, content = Text(card.front))` và `Column(children = { Text("A"); Text("B") })` | Không sửa được: "dòng Card theo syntax Flutter không sai", "dòng 2 không rõ" | `Card(onClick = onFlip) { Text(card.front) }`; `Column { Text("A"); Text("B") }`. Compose không có `children`/`child`; con được **gọi** lần lượt trong trailing lambda |
| 2026-11-16 | Kotlin vs Compose | (Học viên tự đưa giả thuyết) "Flutter mọi thứ là widget/object → Kotlin mọi thứ là function?" | Đúng một nửa | Sai phạm vi: **Kotlin** vẫn là OOP, mọi thứ là object (`Flashcard`, ViewModel...). Chỉ **UI trong Compose** là function (`@Composable fun`). Nguồn nhầm: `Text("A")` trông giống hệt gọi constructor `Flashcard(...)` vì Kotlin không có `new` và composable viết hoa chữ đầu |
| 2026-11-16 | Dịch Flutter → Compose | `ElevatedButton(onPressed: save, child: Text('Lưu'))` | `ElevatedButton(onPressed = save, content = {Text('Lưu')})` | Lambda đúng ✅. Sai: `onPressed` → `onClick`; `'Lưu'` là Char → `"Lưu"`. Đáp án: `Button(onClick = save) { Text("Lưu") }` |
| 2026-11-16 | Dịch Flutter → Compose | `Row(children: [Text('Bộ thẻ'), Text('12 thẻ')])` | `Row( Text('Bộ thẻ') Text('12 thẻ'))` | Bỏ `children` đúng, nhưng con phải vào `{ }` chứ không phải `( )`: `Row { Text("Bộ thẻ"); Text("12 thẻ") }` |
| 2026-11-16 | Dịch Flutter → Compose (lồng nhau) | `Card(child: Column(children: [Text('Hello'), Text('Xin chào')]))` | `Card(content = Column(Text('Hello') Text('Xin chào'),),)` | Lặp lại lỗi truyền kết quả: `content = Column(...)`. Đáp án: `Card { Column { Text("Hello"); Text("Xin chào") } }` |
| 2026-11-16 | Dịch Flutter → Compose (Column/Row lồng nhau) | Dịch `Column(children: [Text, Row(children: [OutlinedButton, ElevatedButton])])` | `Column{ Text('Thẻ 1/4'), Row{ OutlinedButton(onClick = onWrong(), content = {Text("Sai rồi")}), ElevatedButton(onClick = onRight(), content = Text("Đúng rồi!")), } }` | Đúng: `Column { Row { } }`, nút 1 `content = { }`. Sai: nháy đơn; **dấu `,` giữa các lệnh trong `{ }`** (chỉ dùng trong `( )`); `onWrong()` gọi ngay; nút 2 `content = Text(..)` |
