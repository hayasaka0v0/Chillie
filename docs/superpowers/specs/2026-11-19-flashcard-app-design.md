# Thiết kế: App Flashcard theo bản "Flashcard App UI"

Ngày: 2026-11-19 · Trạng thái: đã duyệt (2026-11-19)

## 1. Mục đích và lý do

- Dựng lại **đúng luồng và giao diện** của bản thiết kế "Flashcard App UI" (Claude Design, 4 màn: Library → Deck detail → Flashcard → Session complete): https://claude.ai/artifact/2KACQe3Z6buJKv3AqYBBVH
- Người dùng mới học Kotlin/Android native, làm app này để **ghi vào CV** xin việc Android Fresher/Junior. Vì vậy:
  - Mọi tính năng trên màn hình phải **chạy thật** với dữ liệu thật, không có con số gắn cứng.
  - Dùng bộ thư viện mà tin tuyển dụng hay ghi (phương án B, người dùng đã chọn).
  - Người dùng phải **giải thích được** code khi đi phỏng vấn.
- Cách học: Claude viết code mẫu, có chú thích cú pháp Kotlin mới; người dùng đọc rồi hỏi. Không so sánh với Flutter.

## 2. Mục tiêu / Không làm / Tuyệt đối tránh

**Mục tiêu**
- 4 màn trong thiết kế, đúng bố cục, màu, font, hành vi.
- Thêm các màn tối thiểu để app dùng được thật nhưng thiết kế chưa vẽ: tạo/sửa bộ thẻ, quản lý thẻ, kho tải bộ thẻ (Import).
- Dữ liệu lưu trên máy (Room), tắt app không mất.
- Lịch ôn ngắt quãng (SM-2 rút gọn) quyết định thẻ nào "đến hạn".
- Thông báo nhắc ôn hằng ngày.
- Có unit test và vài UI test.

**Không làm (bản đầu)**
- Tài khoản, đồng bộ lên mạng, backend riêng.
- Dark mode (thiết kế chỉ có giao diện sáng).
- Chia nhiều module Gradle.
- Import/Export file JSON trong máy (để sau, dùng lại cùng định dạng với kho deck).
- Ảnh/âm thanh trong thẻ, layout tablet.

**Tuyệt đối tránh**
- Màn hình trông giống thiết kế nhưng số liệu là giả.
- Trừu tượng hoá quá mức (mapper/use case cho mọi thứ, base class chung chung) khiến người mới không giải thích nổi.
- Code không có chú thích cho cú pháp mới xuất hiện lần đầu.

## 3. Ràng buộc

- Project hiện có: AGP 9.4.1, Kotlin 2.2.10, Compose BOM 2026.02.01, minSdk 24, targetSdk 37, namespace `com.huyvu.flashcard`, Windows.
- Kiến trúc: MVVM + Repository; tầng domain chỉ có use case khi có luật nghiệp vụ thật. State đi một chiều qua `StateFlow`.
- Giữ cách chia thư mục `domain/`, `data/`, `presentation/` (con của `presentation/` chia theo màn).
- `docs/kotlin-syntax-cho-dev-flutter.md` mục 26 phải được cập nhật khi thêm/sửa file trong `app/src`; cây thư mục trong `CLAUDE.md` cũng vậy.

## 4. Các phương án đã cân nhắc

| | Nội dung | Được | Mất |
|---|---|---|---|
| A. Gọn, offline | Room, Navigation Compose, Hilt, Coroutines/Flow, kotlinx.serialization, JUnit/MockK/Turbine | Ít thứ phải học, xong nhanh | CV thiếu Retrofit (mảng gọi mạng gần như tin nào cũng ghi) |
| **B. Đủ bộ CV (đã chọn)** | A + Retrofit/OkHttp (Import từ kho deck online), DataStore, WorkManager, Compose UI test | Phủ gần đủ từ khoá tuyển Junior | Nhiều thứ hơn để học |
| Nhiều module (loại) | B + tách `core/`, `feature/*` | Giống dự án lớn | Quá nặng cho người mới; có thể tách sau |
| Koin thay Hilt (loại) | | Dễ hơn | Tin tuyển dụng ghi Hilt nhiều hơn |

## 5. Bộ thư viện

- UI: Jetpack Compose + Material 3 (đã có), **Navigation Compose** dạng type-safe (route là class `@Serializable`).
- DI: **Hilt** (+ `hilt-navigation-compose` để lấy ViewModel), chạy bằng **KSP**. Thay `AppContainer` và `StudyViewModel.Factory` hiện tại.
- Database: **Room** (KSP), DAO trả về `Flow`.
- Mạng: **Retrofit + OkHttp** (+ logging interceptor bản debug), JSON bằng **kotlinx.serialization**.
- Lưu cài đặt nhỏ: **DataStore Preferences**.
- Việc chạy nền: **WorkManager** (+ `hilt-work`).
- Test: JUnit4, **MockK**, **Turbine**, `kotlinx-coroutines-test`; Room DAO test và **Compose UI test** chạy trên máy/emulator.
- Font: **Nunito** đóng gói trong `res/font` (không tải mạng lúc chạy). Chữ Nhật dùng font hệ thống.
- Version cụ thể: builder chọn bản ổn định mới nhất, tương thích AGP 9.4.1 / Kotlin 2.2.10, khai báo trong `gradle/libs.versions.toml`.

## 6. Dữ liệu

### Deck (bộ thẻ)
`id`, `title`, `description` (có thể trống), `category` (vd. "Language · Japanese", có thể trống), `emblem` (hình trong ô vuông ở góc thẻ deck: **hoặc** 1–3 ký tự như "あ", "Ää", "{ }", **hoặc** 1 trong ~8 icon nét có sẵn như tế bào, quả địa cầu, code, sách... giống thiết kế; lưu dạng `text:あ` / `icon:globe`; Deck editor có chỗ chọn) *(Claude chọn)*, `colorIndex` (0–2, chọn 1 trong 3 nền của thiết kế), `source` (`CREATED` | `IMPORTED`), `remoteId` (id trong kho deck, để biết đã tải chưa), `createdAt`, `lastStudiedAt` (null nếu chưa học).

### Card (thẻ)
- Nội dung: `id`, `deckId`, `front`, `back`. Các trường phụ có thể trống: `label` (vd. "Noun", hiện dưới mặt trước), `reading` (vd. "みず", hiện trên nghĩa ở mặt sau), `pronunciation` (vd. "mizu"), `example`, `exampleTranslation`.
- Lịch ôn: `repetitions`, `easeFactor` (mặc định 2.5), `intervalDays`, `dueAt`, `lapses`.
- Mặt sau trong thiết kế: reading → back (chữ to) → pronunciation → ô ví dụ (example + exampleTranslation). Trường nào trống thì ẩn.

### ReviewLog (nhật ký ôn)
`id`, `cardId`, `deckId`, `reviewedAt`, `correct`. Dùng cho biểu đồ tuần, chuỗi ngày học, "Studied yesterday".

### DataStore
`reminderEnabled` (mặc định true), `reminderHour` (mặc định 20), `notificationPermissionAsked`.

### Xoá
Xoá deck thì xoá luôn thẻ và log của nó (khoá ngoại `CASCADE`).

## 7. Luật nghiệp vụ

- **Trạng thái thẻ**: *New* = chưa ôn lần nào; *Mastered* = `intervalDays ≥ 21`; còn lại là *Learning*.
- **Đến hạn tính theo ngày lịch** (không theo giờ phút): `dueAt` lưu dạng ngày (epoch day / `LocalDate`). Thẻ đến hạn nếu là New hoặc `dueAt ≤ hôm nay`. Nhờ vậy "Due today", badge và thông báo 20:00 khớp nhau. *(Claude chọn)*
- **Học thử (practice)**: lượt "Study all anyway" gồm thẻ chưa đến hạn → vẫn ghi ReviewLog (tính vào biểu đồ, chuỗi ngày, số Got it/Again) nhưng **không đổi lịch ôn** của thẻ. Lượt "Review N missed cards" thì thẻ đã được Again đưa về đến hạn hôm nay nên chấm SM-2 bình thường. *(Claude chọn)*
- **Chấm điểm (SM-2 rút gọn, 2 nút)**:
  - *Got it* (chất lượng 4): lần 1 → 1 ngày, lần 2 → 6 ngày, sau đó `interval × easeFactor` (làm tròn); `easeFactor` giữ theo công thức SM-2 với q = 4; `repetitions + 1`.
  - *Again* (chất lượng 1): `repetitions = 0`, `intervalDays = 0`, `dueAt = hôm nay` (đến hạn lại ngay), `lapses + 1`, `easeFactor` giảm theo SM-2 nhưng không dưới 1.3.
  - Mỗi lần chấm ghi 1 ReviewLog. Đây là use case chính, phải có unit test đầy đủ.
- **% tiến độ deck** = số thẻ Mastered / tổng số thẻ.
- **Chuỗi ngày học** (Day streak): số ngày liên tiếp tính tới hôm nay (hoặc tới hôm qua nếu hôm nay chưa học) có ít nhất 1 ReviewLog. Tính theo múi giờ máy. Chuỗi trên màn Deck tính theo deck đó.
- **Biểu đồ "This week"**: số lần ôn mỗi ngày từ Thứ 2 tới Chủ nhật tuần hiện tại, của deck đó; cột hôm nay tô đậm.
- **"Studied yesterday"**: ngày học gần nhất ghi dạng tương đối (today / yesterday / N days ago / ngày cụ thể).

## 8. Các màn hình

Giao diện: chuỗi chữ tiếng Anh như thiết kế, để trong `strings.xml`. Màu lấy từ thiết kế: nền `#FFF5F5`, chữ/nút chính `#4A4A4A`, chữ phụ `#5E5557`, hồng nhạt `#F7D6D0`, hồng đậm `#E2B4BD`, trắng `#FFFFFF`. Bo góc lớn (thẻ deck 24, thẻ học 32, nút 28), nút cao 56. Chi tiết từng px xem file `.dc.html` trong canvas.

### 8.1 Library (màn đầu tiên)
- Tiêu đề "Your collection / Library", nút tròn **Import** (mở màn Import).
- Ô tìm kiếm: lọc deck theo tên khi gõ.
- Chip lọc **All / Created / Imported** (theo `source`).
- Dòng "N decks" và "Recently studied" (danh sách luôn sắp theo `lastStudiedAt` mới nhất, deck chưa học xếp sau theo `createdAt`).
- Lưới 2 cột: ô emblem, badge góc phải ("N due" nếu có thẻ đến hạn, không thì "Imported" nếu là deck tải về, không thì không có badge), tên, "N cards · P%", thanh tiến độ. Bấm → Deck detail.
- Nút nổi **New deck** → màn tạo deck.
- Rỗng: lời nhắc + 2 nút "Import a deck" / "Create a deck".

### 8.2 Deck detail
- Nút Back; nút **⋯** mở menu: Edit deck, Manage cards, Delete deck (có hộp xác nhận).
- Khối đầu: category, tên, mô tả, "Created <ngày> · Studied <tương đối>".
- 3 ô số: Cards, Due today, Day streak.
- Vòng tròn Mastered/Learning/New + chú thích số lượng.
- Biểu đồ "This week" + "N cards reviewed".
- Nút dưới cùng **"Start · N cards due"** → màn Study. N là số thẻ của lượt này (tối đa 20); ô "Due today" vẫn hiện tổng số thật (vd. 120). Khi 0 thẻ đến hạn: nút thành "Nothing due · Study all anyway" (lượt học thử, cũng tối đa 20 thẻ, xem mục 7). Deck không có thẻ: nút "Add cards" → Manage cards.

### 8.3 Study (học)
- Nút X (thoát về Deck, kết quả đã chấm vẫn giữ), tên deck, bộ đếm "i / n", thanh tiến độ.
- Thẻ lớn lật 3D quanh trục dọc khi bấm vào thẻ hoặc nút **Show answer**; khi đã lật hiện 2 nút **Again** và **Got it**.
- Hàng đợi = các thẻ đến hạn lúc bắt đầu (tối đa 20 thẻ/lượt, New xếp sau thẻ đến hạn cũ), không thêm thẻ trong lúc học. Again không đưa thẻ quay lại lượt này; thẻ sai được ôn lại qua nút ở màn Done.
- Hết hàng đợi → sang thẳng màn Done (bỏ bước "All cards reviewed" trung gian của canvas; mọi thông tin đó đã có ở Done). *Đây là lựa chọn của Claude, người dùng có thể đổi.*
- Ghi `lastStudiedAt` của deck khi chấm thẻ đầu tiên.

### 8.4 Session complete (Done)
- Nút X → Library.
- "You reviewed N cards from <deck>".
- 3 ô: Got it, Again, Time (mm:ss của lượt học).
- "Deck progress P1% → P2%" với thanh 2 màu (phần tăng thêm màu hồng đậm).
- "Next review: <ngày dueAt sớm nhất trong deck> · N day streak".
- Nút **"Review N missed cards"** (chỉ hiện khi có thẻ Again) → lượt học mới chỉ gồm các thẻ đó; **Back to deck**; **Go to library**.
- Lượt học đầu tiên hoàn thành mà chưa hỏi quyền thông báo (Android 13+) → hỏi quyền ở đây, một lần.

### 8.5 Các màn thêm (thiết kế chưa vẽ, làm cùng phong cách)
- **Deck editor** (tạo/sửa): title (bắt buộc), description, category, emblem, chọn 1 trong 3 màu. Tạo xong → sang Manage cards.
- **Manage cards**: danh sách thẻ (front, back, trạng thái), nút thêm, bấm để sửa, vuốt hoặc nút để xoá.
- **Card editor**: front, back (bắt buộc), các trường phụ gom trong phần "More fields".
- **Import**: danh sách deck trong kho online (tên, mô tả, số thẻ); nút Download; deck đã tải hiện "Downloaded". Có trạng thái đang tải, lỗi mạng (nút Retry). Tải xong → deck vào Library với `source = IMPORTED`.

## 9. Kho deck online (Retrofit)

- File JSON tĩnh nằm ngay trong repo: `deck-store/index.json` (danh sách: `id`, `title`, `description`, `category`, `emblem`, `cardCount`, `file`) và `deck-store/decks/<id>.json` (thông tin deck + mảng thẻ với các trường ở mục 6).
- App đọc file qua raw GitHub (repo `hayasaka0v0/Chillie` đang public). Base URL đặt trong `BuildConfig`, khác nhau theo bản build:
  - debug: `https://raw.githubusercontent.com/hayasaka0v0/Chillie/dev/deck-store/`
  - release: `https://raw.githubusercontent.com/hayasaka0v0/Chillie/main/deck-store/` (có dữ liệu khi `dev` được merge vào `main`; việc merge do người dùng làm).
- Người dùng đã đồng ý (2026-11-19) cho push thư mục `deck-store/` lên nhánh `dev` khi làm tới bước Import.
- Claude soạn sẵn 4–6 deck mẫu có nội dung thật (vd. Japanese N5, German A1, Kotlin basics, World capitals), mỗi deck 20–60 thẻ.

## 10. Nhắc ôn (WorkManager)

- Việc định kỳ mỗi ngày, chạy quanh `reminderHour`. Nếu có thẻ đến hạn ở bất kỳ deck nào: thông báo "N cards due today", bấm vào mở Library.
- Bản đầu chưa có màn Settings; giá trị mặc định đọc từ DataStore.

## 11. Kiểm thử

- Unit test (JVM): luật SM-2, chuỗi ngày học, số liệu biểu đồ tuần, % tiến độ, các ViewModel (fake repository + Turbine), parse JSON kho deck.
- Test trên máy: Room DAO (truy vấn đến hạn, xoá cascade), Compose UI test cho luồng Library → Deck → Study → Done.

## 12. Phần do người dùng quyết

- Phương án B; app phải chạy thật; học bằng cách Claude viết mẫu rồi người dùng hỏi; không so sánh với Flutter.
- Kho deck nằm trong repo, push lên nhánh `dev` (bản debug đọc từ `dev`).

## 13. Phần để builder tự quyết

- Version thư viện, cách tổ chức file trong từng thư mục, cách vẽ vòng tròn/biểu đồ (Canvas), hiệu ứng lật (`graphicsLayer`), cách tính giờ chạy của WorkManager, nội dung deck mẫu.
- Thứ tự làm: chia thành nhiều bước nhỏ, mỗi bước chạy được. **Hết mỗi bước thì dừng**: người dùng đọc code và hỏi, Claude hỏi lại 3–5 câu kiểm tra (theo CLAUDE.md), xong mới sang bước sau. Chú thích code và giải thích không so sánh với Flutter (CLAUDE.md đã cập nhật).
- Code cũ (`InMemoryFlashcardRepository`, `AppContainer`, màn Study cũ): thay thế hoặc xoá khi có bản mới, cập nhật mục 26 giáo trình và cây thư mục trong `CLAUDE.md`.
