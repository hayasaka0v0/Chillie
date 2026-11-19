# Flashcard App (theo bản thiết kế) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Dựng lại app Flashcard theo bản thiết kế 4 màn (Library → Deck → Study → Done), chạy thật với Room, Hilt, Navigation, Retrofit, DataStore, WorkManager.

**Architecture:** MVVM + Repository. `domain/` là Kotlin thuần (model, interface repository, luật SM-2, hàm tính số liệu, use case chấm thẻ). `data/` cài đặt repository bằng Room, Retrofit, DataStore. `presentation/` chia theo màn, mỗi màn có `XxxScreen` (lấy ViewModel), `XxxContent` (chỉ vẽ), `XxxViewModel`, `XxxUiState`. State đi một chiều qua `StateFlow`.

**Tech Stack:** Kotlin 2.2.10, AGP 9.4.1 (Kotlin có sẵn trong AGP), Compose BOM 2026.02.01 + Material 3, Navigation Compose (type-safe), Hilt + KSP, Room, Retrofit + OkHttp + kotlinx.serialization, DataStore Preferences, WorkManager, JUnit4 + MockK + Turbine + kotlinx-coroutines-test, Compose UI test.

**Spec:** `docs/superpowers/specs/2026-11-19-flashcard-app-design.md`

## Global Constraints

- Package gốc `com.huyvu.flashcard`, source ở `app/src/main/java/com/huyvu/flashcard/`.
- minSdk 24 nên phải bật core library desugaring để dùng `java.time` (`LocalDate`, `Instant`, `Clock`).
- Màu: nền `#FFF5F5`, chữ/nút chính `#4A4A4A`, chữ phụ `#5E5557`, hồng nhạt `#F7D6D0`, hồng đậm `#E2B4BD`, trắng `#FFFFFF`. Chỉ có giao diện sáng, không dùng dynamic color.
- Font Nunito (400/600/700/800) đóng gói trong `res/font`. Bo góc: thẻ deck 24dp, thẻ học 32dp, nút 28dp (cao 56dp), nút tròn 44dp.
- Chữ trên giao diện bằng tiếng Anh, giống thiết kế, để trong `res/values/strings.xml`.
- Version thư viện để ở `gradle/libs.versions.toml`, chọn bản ổn định mới nhất build được với AGP 9.4.1 / Kotlin 2.2.10 (KSP phải khớp version Kotlin).
- Học thử (practice) không đổi lịch ôn. Hạn ôn tính theo ngày lịch ở múi giờ của máy. Mỗi lượt học tối đa 20 thẻ.
- Mỗi file mới: chú thích tiếng Việt cho cú pháp Kotlin xuất hiện lần đầu (lambda, trailing lambda, `it`, `::`, higher-order function phải được tách ra giải thích). Không so sánh với Flutter.
- **Hết mỗi task thì dừng**: tóm tắt file mới (file để làm gì, ai gọi nó, nó gọi ai, có sơ đồ luồng), chờ người dùng hỏi, hỏi lại 3–5 câu kiểm tra cú pháp. Commit chỉ khi người dùng đồng ý.
- Cuối mỗi task: cập nhật mục 26 của `docs/kotlin-syntax-cho-dev-flutter.md` và cây thư mục trong `CLAUDE.md` cho khớp code.

## Review Focus

1. **Thẻ đổi lịch giữa lượt học**: chấm một thẻ làm Flow của Room phát list mới. Hàng đợi của lượt học phải chụp lại một lần lúc bắt đầu, không bị đổi hay nhảy thẻ. → test ở Task 10.
2. **Import khi mất mạng hoặc JSON lỗi**: hiện lỗi + Retry, không để lại deck dở dang trong DB. → test ở Task 12.
3. **Import cùng một deck hai lần**: hiện "Downloaded", không tạo bản trùng. → test ở Task 12.
4. **Ranh giới ngày**: ôn lúc 23:59 và 00:01 phải tính là 2 ngày khác nhau theo giờ máy (chuỗi ngày, biểu đồ tuần). → test ở Task 3.
5. **Deck 0 thẻ**: % tiến độ là 0 (không chia cho 0), nút dưới cùng là "Add cards". → test ở Task 3 và Task 9.

---

## Cấu trúc file (khi xong)

```
app/src/main/java/com/huyvu/flashcard/
├── FlashcardApplication.kt        @HiltAndroidApp, Configuration.Provider (WorkManager), lên lịch nhắc ôn
├── MainActivity.kt                @AndroidEntryPoint, setContent { FlashcardTheme { FlashcardNavHost() } }
├── di/                            AppModule (Clock), DatabaseModule, RepositoryModule, NetworkModule, DataStoreModule
├── domain/
│   ├── model/                     Deck.kt, Emblem.kt, Flashcard.kt, ReviewSchedule.kt, ReviewLog.kt, StudyTypes.kt, StoreDeck.kt
│   ├── repository/                DeckRepository, FlashcardRepository, ReviewLogRepository, DeckStoreRepository, SettingsRepository
│   ├── srs/Sm2Scheduler.kt        luật SM-2
│   ├── stats/StudyStats.kt        streak, biểu đồ tuần, %, hàng đợi, ngày tương đối
│   └── usecase/AnswerCardUseCase.kt
├── data/
│   ├── local/                     FlashcardDatabase, Converters, entity/*, dao/*
│   ├── remote/                    DeckStoreApi, dto/*
│   ├── repository/                RoomDeckRepository, RoomFlashcardRepository, RoomReviewLogRepository, NetworkDeckStoreRepository
│   └── settings/DataStoreSettingsRepository.kt
├── work/                          ReviewReminderWorker, ReminderScheduler, NotificationHelper
└── presentation/
    ├── navigation/                Routes.kt, FlashcardNavHost.kt
    ├── theme/                     Color.kt, Type.kt, Theme.kt
    ├── components/                EmblemIcons.kt, EmblemView.kt, DeckColors.kt, PillButton.kt, CircleIconButton.kt
    ├── library/  deck/  study/  done/  deckeditor/  cards/  cardeditor/  store/
deck-store/                        index.json, decks/*.json (ở gốc repo)
```

---

### Task 1: Nền móng (Gradle, Hilt, theme, dọn code cũ)

**Files:**
- Modify: `gradle/libs.versions.toml`, `build.gradle.kts`, `app/build.gradle.kts`
- Modify: `app/src/main/java/com/huyvu/flashcard/FlashcardApplication.kt`, `MainActivity.kt`
- Modify: `presentation/theme/Color.kt`, `Type.kt`, `Theme.kt`
- Create: `app/src/main/res/font/nunito.ttf` (bản variable font từ `github.com/google/fonts` thư mục `ofl/nunito`, giấy phép OFL; **hỏi người dùng trước khi tải**)
- Delete: `di/AppContainer.kt`, `data/repository/InMemoryFlashcardRepository.kt`, `domain/usecase/AnswerCardUseCase.kt`, `domain/repository/FlashcardRepository.kt`, cả thư mục `presentation/study/`, `app/src/test/.../domain/usecase/AnswerCardUseCaseTest.kt`

**Interfaces:**
- Produces: `FlashcardTheme(content: @Composable () -> Unit)`; object `FlashcardColors` với `Background`, `Ink`, `InkMuted`, `Blush`, `Rose`, `White`; `NunitoFamily: FontFamily`.

- [ ] **Step 1: Thêm plugin và thư viện**: KSP, Hilt (plugin + `hilt-android` + `hilt-compiler` qua `ksp`), `hilt-navigation-compose`, kotlinx-coroutines-test, MockK, Turbine (testImplementation). Bật `isCoreLibraryDesugaringEnabled = true` + `coreLibraryDesugaring(desugar_jdk_libs)`. Các thư viện còn lại thêm ở task cần đến chúng.
- [ ] **Step 2: Xoá code cũ** liệt kê ở trên. `Flashcard.kt` giữ lại, Task 2 sẽ viết lại.
- [ ] **Step 3: Theme**: `Color.kt` chứa `FlashcardColors`; `Type.kt` khai báo `NunitoFamily` (4 weight qua `FontVariation`) và `Typography` dùng nó; `Theme.kt` dùng `lightColorScheme(background = Background, surface = White, primary = Ink, onPrimary = Background, secondary = Rose, secondaryContainer = Blush, onBackground = Ink, onSurface = Ink, onSurfaceVariant = InkMuted)`, bỏ dark/dynamic.
- [ ] **Step 4: Hilt**: thêm `@HiltAndroidApp` cho `FlashcardApplication` (bỏ `container`), `@AndroidEntryPoint` cho `MainActivity`. Tạm thời `setContent { FlashcardTheme { Text("Library") } }`.
- [ ] **Step 5: Kiểm tra**: chạy `./gradlew assembleDebug testDebugUnitTest`. Kỳ vọng: BUILD SUCCESSFUL. Cài lên emulator: nền hồng nhạt, chữ "Library" font Nunito.
- [ ] **Step 6: Cập nhật docs mục 26 + cây thư mục `CLAUDE.md`. Dừng cho hỏi đáp. Commit** `chore: set up Hilt, desugaring, design theme; remove old study stack`.

### Task 2: Model domain + luật SM-2

**Files:**
- Create: `domain/model/Deck.kt`, `Emblem.kt`, `ReviewSchedule.kt`, `ReviewLog.kt`, `StudyTypes.kt`; Modify: `domain/model/Flashcard.kt`
- Create: `domain/srs/Sm2Scheduler.kt`
- Test: `app/src/test/java/com/huyvu/flashcard/domain/srs/Sm2SchedulerTest.kt`, `domain/model/EmblemTest.kt`, `domain/model/ReviewScheduleTest.kt`

**Interfaces:**
- Produces:
  - `data class Deck(val id: Long = 0, val title: String, val description: String = "", val category: String = "", val emblem: Emblem, val colorIndex: Int = 0, val source: DeckSource = DeckSource.CREATED, val remoteId: String? = null, val createdAt: Instant, val lastStudiedAt: Instant? = null)`
  - `enum class DeckSource { CREATED, IMPORTED }`
  - `sealed interface Emblem { data class Text(val value: String); data class Icon(val icon: EmblemIcon) }` + `fun Emblem.encode(): String` (`"text:あ"`, `"icon:globe"`) + `fun decodeEmblem(raw: String): Emblem` (chuỗi không hợp lệ → `Emblem.Text("?")`)
  - `enum class EmblemIcon(val key: String) { CELL("cell"), GLOBE("globe"), CODE("code"), BOOK("book"), MUSIC("music"), FLASK("flask"), CALC("calc"), STAR("star") }`
  - `data class ReviewSchedule(val repetitions: Int = 0, val easeFactor: Double = 2.5, val intervalDays: Int = 0, val dueDate: LocalDate? = null, val lapses: Int = 0)`; `dueDate == null` nghĩa là thẻ New.
  - `enum class CardStatus { NEW, LEARNING, MASTERED }`, `fun ReviewSchedule.status(): CardStatus`, `fun ReviewSchedule.isDue(today: LocalDate): Boolean`
  - `data class Flashcard(val id: Long = 0, val deckId: Long, val front: String, val back: String, val label: String = "", val reading: String = "", val pronunciation: String = "", val example: String = "", val exampleTranslation: String = "", val schedule: ReviewSchedule = ReviewSchedule())`
  - `data class ReviewLog(val id: Long = 0, val cardId: Long, val deckId: Long, val reviewedAt: Instant, val correct: Boolean)`
  - `enum class Answer { AGAIN, GOT_IT }`, `enum class SessionMode { NORMAL, PRACTICE }`
  - `object Sm2Scheduler { fun next(current: ReviewSchedule, answer: Answer, today: LocalDate): ReviewSchedule }`

- [ ] **Step 1: Viết test (chưa chạy được)**

```kotlin
// Sm2SchedulerTest — today = LocalDate.of(2026, 11, 19)
@Test fun gotIt_onNewCard_dueTomorrow() // reps 1, interval 1, dueDate 2026-11-20, ease 2.5
@Test fun gotIt_secondTime_interval6()  // từ reps 1/interval 1 → reps 2, interval 6, due 2026-11-25
@Test fun gotIt_thirdTime_multipliesByEase() // từ reps 2/interval 6/ease 2.5 → interval 15
@Test fun gotIt_roundsHalfUp()          // từ reps 3/interval 15/ease 2.5 → interval 38
@Test fun again_resetsAndDueToday()     // từ reps 3/interval 15/ease 2.5/lapses 0 → reps 0, interval 0, due 2026-11-19, lapses 1, ease 1.96 (±1e-9)
@Test fun again_easeNeverBelow1_3()     // ease 1.5 → 1.3
// ReviewScheduleTest
@Test fun status_newWhenNeverReviewed() ; @Test fun status_masteredFrom21Days() // interval 21 → MASTERED, 20 → LEARNING
@Test fun isDue_newCardAlwaysDue() ; @Test fun isDue_byCalendarDay() // due hôm nay → true, ngày mai → false
// EmblemTest
@Test fun encodeDecode_roundTrip() // Text("あ") và Icon(GLOBE) ; @Test fun decode_garbage_returnsQuestionMark()
```

- [ ] **Step 2: Chạy** `./gradlew testDebugUnitTest`. Kỳ vọng: FAIL do chưa có class.
- [ ] **Step 3: Viết model + `Sm2Scheduler.next`**. Ease mới = `max(1.3, ease + 0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))` với q = 4 (Got it) hoặc 1 (Again). Got it: `reps + 1`; interval = 1 nếu reps mới = 1, 6 nếu = 2, còn lại `(interval * easeMới).roundToInt()`; `dueDate = today.plusDays(interval)`.
- [ ] **Step 4: Chạy lại**. Kỳ vọng: PASS.
- [ ] **Step 5: Docs mục 26, dừng hỏi đáp, commit** `feat(domain): models and SM-2 scheduler`.

### Task 3: Hàm tính số liệu

**Files:**
- Create: `domain/stats/StudyStats.kt`
- Test: `app/src/test/java/com/huyvu/flashcard/domain/stats/StudyStatsTest.kt`

**Interfaces:**
- Consumes: model ở Task 2.
- Produces (top-level functions trong `StudyStats.kt`):
  - `fun dayStreak(reviewDays: Set<LocalDate>, today: LocalDate): Int`
  - `fun weekCounts(reviewDays: List<LocalDate>, today: LocalDate): List<Int>` (7 phần tử, Thứ 2 → Chủ nhật của tuần chứa `today`)
  - `fun masteredPercent(cards: List<Flashcard>): Int` (0–100, list rỗng → 0)
  - `fun statusCounts(cards: List<Flashcard>): Map<CardStatus, Int>` (đủ cả 3 key)
  - `fun buildSessionQueue(cards: List<Flashcard>, today: LocalDate, mode: SessionMode, limit: Int = 20): List<Flashcard>`
  - `sealed interface RelativeDay { data object Today; data object Yesterday; data class DaysAgo(val days: Int); data class On(val date: LocalDate) }` + `fun relativeDay(date: LocalDate, today: LocalDate): RelativeDay` (2–6 ngày → DaysAgo, ≥7 → On)
  - `fun Instant.toLocalDate(zone: ZoneId): LocalDate`

- [ ] **Step 1: Viết test**

```kotlin
@Test fun streak_countsConsecutiveDaysEndingToday()      // 17,18,19 với today 19 → 3
@Test fun streak_stillCountsIfOnlyYesterday()            // 17,18 với today 19 → 2
@Test fun streak_brokenByGap()                           // 15,17 với today 19 → 0
@Test fun weekCounts_mondayToSunday()                    // today = Thứ 5 2026-11-19; 2 log ngày 16, 1 log ngày 19 → [2,0,0,1,0,0,0]
@Test fun weekCounts_ignoresLastWeek()                   // log ngày 15 (Chủ nhật tuần trước) không được tính
@Test fun toLocalDate_usesZone()                         // 2026-11-19T16:59Z ở Asia/Ho_Chi_Minh → 19; 17:01Z → 20 (Review Focus 4)
@Test fun masteredPercent_emptyIsZero()                  // (Review Focus 5)
@Test fun masteredPercent_rounds()                       // 1/3 mastered → 33
@Test fun queue_normal_dueBeforeNew_capped()             // 25 thẻ due + 5 new → 20 thẻ, due cũ (dueDate tăng dần) đứng trước new
@Test fun queue_normal_skipsNotDue()
@Test fun queue_practice_takesNotDueSortedByDueDate()    // practice: lấy thẻ chưa đến hạn, dueDate tăng dần, tối đa 20
@Test fun relativeDay_cases()                            // 0 → Today, 1 → Yesterday, 3 → DaysAgo(3), 8 → On(date)
```

- [ ] **Step 2: Chạy**, kỳ vọng FAIL. **Step 3: Viết hàm.** **Step 4: Chạy**, kỳ vọng PASS.
- [ ] **Step 5: Docs mục 26, dừng hỏi đáp, commit** `feat(domain): study statistics`.

### Task 4: Room + repository

**Files:**
- Modify: `gradle/libs.versions.toml`, `app/build.gradle.kts` (Room runtime/ktx/compiler qua KSP; schema export vào `app/schemas`)
- Create: `domain/repository/DeckRepository.kt`, `FlashcardRepository.kt`, `ReviewLogRepository.kt`
- Create: `data/local/FlashcardDatabase.kt`, `Converters.kt`, `entity/DeckEntity.kt`, `entity/CardEntity.kt`, `entity/ReviewLogEntity.kt`, `dao/DeckDao.kt`, `dao/CardDao.kt`, `dao/ReviewLogDao.kt`
- Create: `data/repository/RoomDeckRepository.kt`, `RoomFlashcardRepository.kt`, `RoomReviewLogRepository.kt`
- Create: `di/AppModule.kt` (cung cấp `Clock.systemDefaultZone()`), `di/DatabaseModule.kt`, `di/RepositoryModule.kt` (`@Binds`)
- Test: `app/src/androidTest/java/com/huyvu/flashcard/data/local/FlashcardDaoTest.kt` (DB in-memory)

**Interfaces:**
- Produces:
  - `interface DeckRepository { fun observeDecks(): Flow<List<Deck>>; fun observeDeck(id: Long): Flow<Deck?>; suspend fun insert(deck: Deck): Long; suspend fun update(deck: Deck); suspend fun delete(id: Long); suspend fun markStudied(id: Long, at: Instant); fun observeImportedRemoteIds(): Flow<Set<String>>; suspend fun insertWithCards(deck: Deck, cards: List<Flashcard>): Long }` (`insertWithCards` chạy trong 1 transaction, thẻ được gán `deckId` mới)
  - `interface FlashcardRepository { fun observeAll(): Flow<List<Flashcard>>; fun observeByDeck(deckId: Long): Flow<List<Flashcard>>; suspend fun get(id: Long): Flashcard?; suspend fun upsert(card: Flashcard): Long; suspend fun delete(id: Long); suspend fun updateSchedule(cardId: Long, schedule: ReviewSchedule); suspend fun countDue(today: LocalDate): Int }`
  - `interface ReviewLogRepository { suspend fun add(log: ReviewLog); fun observeByDeck(deckId: Long): Flow<List<ReviewLog>> }`
  - Bảng: `decks` (emblem lưu chuỗi `encode()`, `source` lưu tên enum), `cards` (khoá ngoại `deckId` → `decks.id` `CASCADE`, `dueEpochDay: Long?`), `review_logs` (khoá ngoại `cardId` và `deckId`, cùng `CASCADE`). `Instant` lưu epoch millis qua `Converters`.
  - `CardDao.countDue(today: Long)`: `SELECT COUNT(*) FROM cards WHERE dueEpochDay IS NULL OR dueEpochDay <= :today`.
  - Mapper: `DeckEntity.toDomain()`, `Deck.toEntity()`, tương tự cho Card và ReviewLog, đặt cuối file entity.

- [ ] **Step 1: Viết test DAO**

```kotlin
@Test fun deleteDeck_cascadesCardsAndLogs()     // xoá deck → observeByDeck rỗng, log của deck rỗng
@Test fun insertWithCards_assignsDeckId()       // 3 thẻ deckId = 0 → sau khi insert đều có deckId mới
@Test fun countDue_newAndDueToday()             // thẻ new + thẻ due hôm nay + thẻ due ngày mai → 2
@Test fun scheduleRoundTrip()                   // updateSchedule rồi get → schedule giống hệt (kể cả dueDate)
@Test fun emblemRoundTrip()                     // Icon(GLOBE) lưu rồi đọc lại vẫn là Icon(GLOBE)
```

- [ ] **Step 2: Chạy** `./gradlew connectedDebugAndroidTest` (cần emulator), kỳ vọng FAIL. **Step 3: Viết code.** **Step 4: Chạy**, kỳ vọng PASS; thêm `./gradlew assembleDebug` PASS.
- [ ] **Step 5: Docs mục 26 (có sơ đồ Entity ↔ Domain ↔ Repository), dừng hỏi đáp, commit** `feat(data): Room database and repositories`.

### Task 5: Use case chấm thẻ

**Files:**
- Create: `domain/usecase/AnswerCardUseCase.kt`
- Test: `app/src/test/java/com/huyvu/flashcard/domain/usecase/AnswerCardUseCaseTest.kt`, `app/src/test/java/com/huyvu/flashcard/testing/FakeRepositories.kt` (fake bằng `MutableStateFlow`, dùng lại ở các test ViewModel)

**Interfaces:**
- Consumes: `FlashcardRepository`, `ReviewLogRepository`, `DeckRepository` (Task 4), `Sm2Scheduler` (Task 2), `java.time.Clock`.
- Produces: `class AnswerCardUseCase @Inject constructor(cards: FlashcardRepository, logs: ReviewLogRepository, decks: DeckRepository, clock: Clock) { suspend operator fun invoke(card: Flashcard, answer: Answer, mode: SessionMode) }`; `FakeDeckRepository`, `FakeFlashcardRepository`, `FakeReviewLogRepository`.

- [ ] **Step 1: Viết test** (Clock cố định `2026-11-19T10:00:00Z`, zone `Asia/Ho_Chi_Minh`)

```kotlin
@Test fun normal_gotIt_updatesScheduleLogsAndMarksStudied() // schedule = Sm2Scheduler.next(...); 1 log correct=true; deck.lastStudiedAt = now
@Test fun normal_again_logsIncorrect()
@Test fun practice_doesNotChangeSchedule()                  // schedule giữ nguyên, vẫn có 1 log, vẫn markStudied
```

- [ ] **Step 2: Chạy**, FAIL. **Step 3: Viết code.** **Step 4: Chạy**, PASS.
- [ ] **Step 5: Docs, dừng hỏi đáp, commit** `feat(domain): answer card use case`.

### Task 6: Navigation + màn Library

**Files:**
- Modify: `gradle/libs.versions.toml`, `app/build.gradle.kts` (navigation-compose, plugin + thư viện kotlinx-serialization-json)
- Create: `presentation/navigation/Routes.kt`, `FlashcardNavHost.kt`
- Create: `presentation/components/EmblemIcons.kt`, `EmblemView.kt`, `DeckColors.kt`, `PillButton.kt`, `CircleIconButton.kt`
- Create: `presentation/library/LibraryScreen.kt`, `LibraryViewModel.kt`, `LibraryUiState.kt`
- Modify: `MainActivity.kt` (hiện `FlashcardNavHost()`), `res/values/strings.xml`
- Test: `app/src/test/java/com/huyvu/flashcard/presentation/library/LibraryViewModelTest.kt`

**Interfaces:**
- Produces:
  - Routes (`@Serializable`): `data object LibraryRoute`, `data class DeckRoute(val deckId: Long)`, `data class StudyRoute(val deckId: Long, val practice: Boolean = false, val onlyCardIds: List<Long> = emptyList())`, `data class DoneRoute(val deckId: Long, val gotIt: Int, val again: Int, val durationSeconds: Long, val progressBefore: Int, val missedCardIds: List<Long>)`, `data class DeckEditorRoute(val deckId: Long = 0L)` (0 = tạo mới), `data class ManageCardsRoute(val deckId: Long)`, `data class CardEditorRoute(val deckId: Long, val cardId: Long = 0L)`, `data object ImportRoute`. Route chưa có màn thì tạm hiện `Text(tên route)`.
  - `fun deckColors(index: Int): Pair<Color, Color>` (nền thẻ, nền ô emblem): 0 = Blush/Background, 1 = Rose/Background, 2 = White có viền Blush/Blush.
  - `@Composable fun EmblemView(emblem: Emblem, background: Color, modifier: Modifier = Modifier)` (ô 40dp, bo 14dp); `EmblemIcons`: 8 `ImageVector` nét, 3 cái cell/code/globe chép path từ `Main.dc.html`.
  - `enum class LibraryFilter { ALL, CREATED, IMPORTED }`; `sealed interface DeckBadge { data class Due(val count: Int); data object Imported; data object None }`
  - `data class DeckTileUi(val id: Long, val title: String, val emblem: Emblem, val colorIndex: Int, val cardCount: Int, val percent: Int, val badge: DeckBadge)`
  - `data class LibraryUiState(val query: String = "", val filter: LibraryFilter = LibraryFilter.ALL, val decks: List<DeckTileUi> = emptyList(), val isEmptyLibrary: Boolean = false)`
  - `LibraryViewModel`: `uiState: StateFlow<LibraryUiState>`, `onQueryChange(String)`, `onFilterChange(LibraryFilter)`.

- [ ] **Step 1: Viết test ViewModel** (fake repo từ Task 5, Turbine)

```kotlin
@Test fun sortsByLastStudiedThenCreated()   // deck học hôm qua đứng trước deck chưa học; 2 deck chưa học: tạo sau đứng trước
@Test fun searchIgnoresCase()               // "japan" khớp "Japanese N5 Vocabulary"
@Test fun filterImported()
@Test fun badge_dueWinsOverImported()       // deck imported có 5 thẻ new → Due(5); không có thẻ đến hạn → Imported; created + 0 due → None
@Test fun emptyLibraryFlag()                // 0 deck → isEmptyLibrary = true; có deck nhưng search không ra → false
```

- [ ] **Step 2: Chạy**, FAIL. **Step 3: Viết VM**: `combine(decks, cards, query, filter)`, ngày hôm nay lấy từ `Clock`. **Step 4: Chạy**, PASS.
- [ ] **Step 5: Viết UI** theo `Main.dc.html`: header (eyebrow 14sp/600, tiêu đề 34sp/800), nút Import tròn 44dp, ô tìm kiếm cao 48dp bo 16dp viền Blush, 3 chip cao 36dp (đang chọn: nền Ink chữ Background), dòng "N decks" và "Recently studied", `LazyVerticalGrid` 2 cột cách 14dp, ô deck cao 168dp bo 24dp, nút nổi "New deck" cao 56dp. Trạng thái rỗng: lời nhắc + "Import a deck" / "Create a deck". Bấm ô deck → `DeckRoute`, New deck → `DeckEditorRoute()`, Import → `ImportRoute`. `@Preview` cho `LibraryContent`.
- [ ] **Step 6: Kiểm tra** `./gradlew assembleDebug`, mở app thấy trạng thái rỗng. Docs, dừng hỏi đáp, commit `feat: navigation and library screen`.

### Task 7: Tạo/sửa deck

**Files:**
- Create: `presentation/deckeditor/DeckEditorScreen.kt`, `DeckEditorViewModel.kt`, `DeckEditorUiState.kt`
- Test: `app/src/test/java/com/huyvu/flashcard/presentation/deckeditor/DeckEditorViewModelTest.kt`

**Interfaces:**
- Consumes: `DeckEditorRoute` (đọc bằng `savedStateHandle.toRoute()`), `DeckRepository`, `EmblemView`, `EmblemIcons`.
- Produces: `data class DeckEditorUiState(val title: String = "", val description: String = "", val category: String = "", val emblem: Emblem = Emblem.Text("A"), val colorIndex: Int = 0, val isNew: Boolean = true, val canSave: Boolean = false, val savedDeckId: Long? = null)`; VM: `onTitleChange`, `onDescriptionChange`, `onCategoryChange`, `onEmblemChange(Emblem)`, `onColorChange(Int)`, `save()`.

- [ ] **Step 1: Test**: `canSave_requiresNonBlankTitle`, `emblemText_maxThreeChars` (gõ "abcd" → "abc"), `saveNew_insertsWithCreatedSource_andSetsSavedId`, `editExisting_loadsAndUpdates_keepsCreatedAt`.
- [ ] **Step 2: Chạy** FAIL → **Step 3: Viết** → **Step 4: Chạy** PASS.
- [ ] **Step 5: UI**: `OutlinedTextField` cho các trường, chọn emblem (ô nhập chữ hoặc lưới 8 icon), 3 ô màu, nút Save. Lưu xong: tạo mới → `ManageCardsRoute(id)` (bỏ màn editor khỏi back stack), sửa → quay lại.
- [ ] **Step 6: Kiểm tra trên máy** tạo được deck và thấy nó ở Library. Docs, dừng hỏi đáp, commit `feat: deck editor`.

### Task 8: Quản lý thẻ + sửa thẻ

**Files:**
- Create: `presentation/cards/ManageCardsScreen.kt`, `ManageCardsViewModel.kt`; `presentation/cardeditor/CardEditorScreen.kt`, `CardEditorViewModel.kt`, `CardEditorUiState.kt`
- Test: `app/src/test/java/com/huyvu/flashcard/presentation/cardeditor/CardEditorViewModelTest.kt`

**Interfaces:**
- Consumes: `ManageCardsRoute`, `CardEditorRoute`, `FlashcardRepository`, `DeckRepository`.
- Produces: `ManageCardsViewModel.uiState: StateFlow<ManageCardsUiState>` với `deckTitle` và `cards: List<Flashcard>`, `delete(cardId: Long)`; `CardEditorUiState(front, back, label, reading, pronunciation, example, exampleTranslation, showMoreFields: Boolean, canSave: Boolean, isSaved: Boolean)`; VM `onFieldChange(field: CardField, value: String)` với `enum class CardField`, `save()`.

- [ ] **Step 1: Test**: `canSave_requiresFrontAndBack`, `saveNew_hasNewSchedule`, `editExisting_keepsSchedule` (sửa chữ không reset lịch ôn), `moreFields_expandedWhenEditingCardThatHasThem`.
- [ ] **Step 2–4:** FAIL → viết → PASS.
- [ ] **Step 5: UI**: danh sách (front đậm, back mờ, chip trạng thái New/Learning/Mastered), nút thêm, bấm để sửa, nút xoá có hộp xác nhận. Card editor: front/back, phần "More fields" thu gọn được.
- [ ] **Step 6: Kiểm tra trên máy**: thêm, sửa, xoá thẻ; Library hiện đúng "N cards · 0%" và "N due". Docs, dừng hỏi đáp, commit `feat: manage and edit cards`.

### Task 9: Màn Deck detail

**Files:**
- Create: `presentation/deck/DeckDetailScreen.kt`, `DeckDetailViewModel.kt`, `DeckDetailUiState.kt`, `presentation/deck/MasteryDonut.kt`, `presentation/deck/WeekBarChart.kt`
- Test: `app/src/test/java/com/huyvu/flashcard/presentation/deck/DeckDetailViewModelTest.kt`

**Interfaces:**
- Consumes: `DeckRoute`, 3 repository, các hàm ở `StudyStats.kt`, `Clock`.
- Produces:
  - `sealed interface StartAction { data class Study(val count: Int); data class PracticeAll(val count: Int); data object AddCards }`
  - `data class DeckDetailUiState(val deck: Deck? = null, val cardCount: Int = 0, val dueCount: Int = 0, val dayStreak: Int = 0, val percent: Int = 0, val statusCounts: Map<CardStatus, Int> = emptyMap(), val week: List<Int> = List(7) { 0 }, val weekTotal: Int = 0, val todayIndex: Int = 0, val lastStudied: RelativeDay? = null, val startAction: StartAction = StartAction.AddCards, val isDeleted: Boolean = false)`
  - VM: `delete()`.

- [ ] **Step 1: Test**: `startAction_studyCapsAt20` (120 thẻ due → `Study(20)`, `dueCount` = 120), `startAction_practiceWhenNothingDue`, `startAction_addCardsWhenEmpty` (Review Focus 5), `weekAndStreak_fromLogs`, `delete_setsIsDeleted`.
- [ ] **Step 2–4:** FAIL → viết → PASS.
- [ ] **Step 5: UI** theo `Deck.dc.html`: nút Back + nút ⋯ (`DropdownMenu`: Edit deck → `DeckEditorRoute(id)`, Manage cards, Delete deck → `AlertDialog`), khối thông tin, 3 ô số, donut 104dp nét 12dp vẽ bằng `Canvas` (Mastered = Ink, Learning = Rose, New = Blush), biểu đồ cột cao 100dp (hôm nay = Ink, có ôn = Rose, không = Blush, cột tối thiểu 6dp), thanh dưới cùng cố định với nút theo `StartAction` ("Start · N cards due" / "Nothing due · Study all anyway" / "Add cards").
- [ ] **Step 6: Kiểm tra trên máy**, docs, dừng hỏi đáp, commit `feat: deck detail screen`.

### Task 10: Màn Study

**Files:**
- Create: `presentation/study/StudyScreen.kt`, `StudyViewModel.kt`, `StudyUiState.kt`, `presentation/study/FlipCard.kt`
- Test: `app/src/test/java/com/huyvu/flashcard/presentation/study/StudyViewModelTest.kt`

**Interfaces:**
- Consumes: `StudyRoute`, `AnswerCardUseCase` (Task 5), `buildSessionQueue`, `masteredPercent`, `Clock`.
- Produces: `data class StudyUiState(val deckTitle: String = "", val queue: List<Flashcard> = emptyList(), val index: Int = 0, val isFlipped: Boolean = false, val isLoading: Boolean = true, val finished: DoneRoute? = null)` với `currentCard` và `progress` (0f..1f) là property tính sẵn; VM: `onFlip()`, `onAnswer(Answer)`, `onFinishedHandled()`.
- Luật: hàng đợi được dựng **một lần** từ `first()` của Flow lúc VM khởi tạo. `onlyCardIds` khác rỗng → chỉ lấy các thẻ đó (mode NORMAL). `practice = true` → `SessionMode.PRACTICE`. `progressBefore` = `masteredPercent` lúc bắt đầu. Hết thẻ → `finished = DoneRoute(...)` với số Got it/Again, số giây từ lúc bắt đầu (theo `Clock`), id các thẻ Again.

- [ ] **Step 1: Test**

```kotlin
@Test fun queueIsSnapshot_notChangedByRepositoryUpdates() // chấm thẻ 1 → fake repo phát list mới → queue vẫn y nguyên (Review Focus 1)
@Test fun answer_advancesAndUnflips()
@Test fun lastAnswer_emitsDoneRouteWithCountsAndMissedIds()
@Test fun onlyCardIds_limitsQueue()
@Test fun practiceFlag_passesPracticeMode()               // kiểm tra fake: lịch ôn không đổi
```

- [ ] **Step 2–4:** FAIL → viết → PASS.
- [ ] **Step 5: UI** theo `Study.dc.html`: nút X 44dp → `popBackStack()` về Deck, tên deck + "i / n", thanh tiến độ cao 8dp. `FlipCard`: lật bằng `graphicsLayer { rotationY; cameraDistance = 12f * density }` + `animateFloatAsState` 550ms; mặt trước nền White bo 32dp viền Blush (chip "Question", chữ front 72sp/700, label, "Tap to flip"), mặt sau nền Rose (chip "Answer", reading, back 40sp/800, pronunciation, ô ví dụ nền Background bo 20dp; trường trống thì ẩn). Thẻ mới hiện ngay ở mặt trước, không chạy hiệu ứng lật ngược. Chưa lật: nút viền "Show answer"; đã lật: 2 nút "Again" (Blush) / "Got it" (Ink). `finished` khác null → `LaunchedEffect` điều hướng sang `DoneRoute`, bỏ Study khỏi back stack, rồi gọi `onFinishedHandled()`.
- [ ] **Step 6: Kiểm tra trên máy** học hết một deck. Docs (có sơ đồ luồng bấm Got it), dừng hỏi đáp, commit `feat: study screen with flip card`.

### Task 11: Màn Done + DataStore + quyền thông báo

**Files:**
- Modify: `gradle/libs.versions.toml`, `app/build.gradle.kts` (datastore-preferences), `AndroidManifest.xml` (`POST_NOTIFICATIONS`)
- Create: `domain/repository/SettingsRepository.kt`, `data/settings/DataStoreSettingsRepository.kt`, `di/DataStoreModule.kt`
- Create: `presentation/done/DoneScreen.kt`, `DoneViewModel.kt`, `DoneUiState.kt`
- Test: `app/src/test/java/com/huyvu/flashcard/presentation/done/DoneViewModelTest.kt`

**Interfaces:**
- Produces:
  - `interface SettingsRepository { val reminderEnabled: Flow<Boolean>; val reminderHour: Flow<Int>; val notificationPermissionAsked: Flow<Boolean>; suspend fun setNotificationPermissionAsked() }` (mặc định true / 20 / false)
  - `data class DoneUiState(val deckTitle: String = "", val reviewed: Int = 0, val gotIt: Int = 0, val again: Int = 0, val durationText: String = "0:00", val progressBefore: Int = 0, val progressAfter: Int = 0, val nextReview: RelativeDay? = null, val dayStreak: Int = 0, val missedCardIds: List<Long> = emptyList(), val shouldAskNotificationPermission: Boolean = false)`; VM: `onPermissionAsked()`.
  - `nextReview` = ngày `dueDate` sớm nhất trong deck (thẻ New hoặc đã đến hạn → Today); "Next review: <relative>" format ở UI.

- [ ] **Step 1: Test**: `duration_formatsMinutesSeconds` (192 → "3:12"), `progressAfter_isLive`, `nextReview_earliestDueDate`, `askPermission_onlyOnce`.
- [ ] **Step 2–4:** FAIL → viết → PASS.
- [ ] **Step 5: UI** theo `Done.dc.html`: nút X → Library (xoá back stack), huy hiệu cúp 132dp, "Session complete", "Nice work. You reviewed N cards from <deck>.", 3 ô (Got it nền Ink, Again nền Blush, Time nền White), thanh "Deck progress P1% → P2%" 2 màu, "Next review: … · N day streak", nút "Review N missed cards" (chỉ khi N > 0) → `StudyRoute(deckId, onlyCardIds = missed)`, "Back to deck", "Go to library". Android 13+ và `shouldAskNotificationPermission` → `rememberLauncherForActivityResult(RequestPermission)` một lần rồi `onPermissionAsked()`.
- [ ] **Step 6: Kiểm tra trên máy**, docs, dừng hỏi đáp, commit `feat: session complete screen and settings store`.

### Task 12: Kho deck online + màn Import

**Files:**
- Create: `deck-store/index.json`, `deck-store/decks/*.json` (5 deck: Japanese N5 Vocabulary, German A1 Phrases, Kotlin Basics, World Capitals, Biology: Cell Structure; 20–60 thẻ mỗi deck, nội dung thật)
- Modify: `gradle/libs.versions.toml`, `app/build.gradle.kts` (Retrofit, converter kotlinx-serialization, OkHttp + logging-interceptor; `buildConfig = true`; `buildConfigField("String", "DECK_STORE_BASE_URL", …)` debug = `https://raw.githubusercontent.com/hayasaka0v0/Chillie/dev/deck-store/`, release = `…/main/deck-store/`); `app/src/main/keepRules/rules.keep` nếu bản release cần giữ DTO; `AndroidManifest.xml` (`INTERNET`)
- Create: `domain/model/StoreDeck.kt`, `domain/repository/DeckStoreRepository.kt`, `data/remote/DeckStoreApi.kt`, `data/remote/dto/StoreDtos.kt`, `data/repository/NetworkDeckStoreRepository.kt`, `di/NetworkModule.kt`
- Create: `presentation/store/ImportScreen.kt`, `ImportViewModel.kt`, `ImportUiState.kt`
- Test: `app/src/test/java/com/huyvu/flashcard/data/repository/NetworkDeckStoreRepositoryTest.kt` (MockK cho `DeckStoreApi`), `app/src/test/java/com/huyvu/flashcard/data/remote/StoreDtosTest.kt` (parse file thật trong `deck-store/`), `app/src/test/java/com/huyvu/flashcard/presentation/store/ImportViewModelTest.kt`

**Interfaces:**
- Produces:
  - JSON: `index.json` = `[{ "id", "title", "description", "category", "emblem", "cardCount", "file" }]`; `decks/<file>` = `{ "id", "title", "description", "category", "emblem", "cards": [{ "front", "back", "label"?, "reading"?, "pronunciation"?, "example"?, "exampleTranslation"? }] }`. `emblem` dùng định dạng `encode()`.
  - `data class StoreDeck(val id: String, val title: String, val description: String, val category: String, val emblem: Emblem, val cardCount: Int, val file: String)`
  - `interface DeckStoreRepository { suspend fun fetchCatalog(): Result<List<StoreDeck>>; suspend fun download(item: StoreDeck): Result<Long> }`; `download` gọi `DeckRepository.insertWithCards` với `source = IMPORTED`, `remoteId = item.id`, `colorIndex` = vị trí trong catalog % 3.
  - `interface DeckStoreApi { @GET("index.json") suspend fun index(): List<StoreDeckDto>; @GET("decks/{file}") suspend fun deck(@Path("file") file: String): StoreDeckDetailDto }`
  - `sealed interface ImportUiState { data object Loading; data class Error(val message: String); data class Ready(val items: List<StoreItemUi>) }`, `data class StoreItemUi(val deck: StoreDeck, val status: ItemStatus)`, `enum class ItemStatus { AVAILABLE, DOWNLOADING, DOWNLOADED, FAILED }`; VM: `retry()`, `download(id: String)`.

- [ ] **Step 1: Test**

```kotlin
@Test fun allStoreFilesParse()                       // đọc mọi file trong deck-store/, cardCount trong index = số thẻ thật
@Test fun download_networkError_returnsFailure_andInsertsNothing() // (Review Focus 2)
@Test fun download_malformedJson_returnsFailure_andInsertsNothing()
@Test fun catalog_marksAlreadyDownloaded()           // remoteId đã có → DOWNLOADED, bấm lại không gọi download (Review Focus 3)
@Test fun fetchError_showsErrorThenRetryLoads()
```

- [ ] **Step 2–4:** FAIL → viết → PASS.
- [ ] **Step 5: UI**: tiêu đề "Import", danh sách thẻ (emblem, tên, mô tả, "N cards"), nút "Download" / vòng quay / "Downloaded" / "Retry"; trạng thái lỗi toàn màn có nút Retry.
- [ ] **Step 6: Push kho**: commit `deck-store/` rồi `git push origin dev` (người dùng đã đồng ý push lên `dev`). Kiểm tra `https://raw.githubusercontent.com/hayasaka0v0/Chillie/dev/deck-store/index.json` trả về 200.
- [ ] **Step 7: Kiểm tra trên máy** tải 1 deck, thấy ở Library với badge Due. Docs (sơ đồ: nút → VM → Repository → Retrofit → Room → Flow → Library), dừng hỏi đáp, commit `feat: deck store import via Retrofit`.

### Task 13: Nhắc ôn hằng ngày (WorkManager)

**Files:**
- Modify: `gradle/libs.versions.toml`, `app/build.gradle.kts` (work-runtime-ktx, hilt-work, androidx.hilt compiler); `AndroidManifest.xml` (gỡ `WorkManagerInitializer` mặc định); `FlashcardApplication.kt` (`Configuration.Provider` với `HiltWorkerFactory`, gọi `ReminderScheduler.schedule()` trong `onCreate`)
- Create: `work/ReviewReminderWorker.kt`, `work/ReminderScheduler.kt`, `work/NotificationHelper.kt`
- Test: `app/src/test/java/com/huyvu/flashcard/work/ReminderSchedulerTest.kt`

**Interfaces:**
- Consumes: `FlashcardRepository.countDue`, `SettingsRepository`, `Clock`.
- Produces: `fun delayUntilNextReminder(now: LocalDateTime, hour: Int): Duration` (top-level trong `ReminderScheduler.kt`); `class ReminderScheduler @Inject constructor(...) { suspend fun schedule() }` dùng `enqueueUniquePeriodicWork("review-reminder", UPDATE, 24h)` với initial delay; `ReviewReminderWorker` (`@HiltWorker`): `reminderEnabled` false hoặc `countDue == 0` thì không làm gì; ngược lại thông báo "N cards due today", bấm mở `MainActivity`. `NotificationHelper.ensureChannel(context)`.

- [ ] **Step 1: Test**: `before8pm_sameDay` (19:30 → 30 phút), `after8pm_nextDay` (20:30 → 23 giờ 30 phút), `exactly8pm_nextDay` (→ 24 giờ).
- [ ] **Step 2–4:** FAIL → viết → PASS.
- [ ] **Step 5: Kiểm tra trên máy**: dùng App Inspection › Background Task Inspector (hoặc `adb shell cmd jobscheduler run`) chạy worker ngay, có thẻ đến hạn thì hiện thông báo.
- [ ] **Step 6: Docs, dừng hỏi đáp, commit** `feat: daily review reminder`.

### Task 14: UI test luồng chính + chốt tài liệu

**Files:**
- Modify: `gradle/libs.versions.toml`, `app/build.gradle.kts` (`hilt-android-testing`, custom test runner)
- Create: `app/src/androidTest/java/com/huyvu/flashcard/HiltTestRunner.kt`, `app/src/androidTest/java/com/huyvu/flashcard/MainFlowTest.kt`, `app/src/androidTest/java/com/huyvu/flashcard/di/TestDatabaseModule.kt` (`@TestInstallIn` thay DB thật bằng DB in-memory có sẵn 1 deck 2 thẻ)
- Delete: `app/src/androidTest/.../ExampleInstrumentedTest.kt`, `app/src/test/.../ExampleUnitTest.kt`

- [ ] **Step 1: Viết test** `libraryToDone_happyPath`: thấy deck ở Library → bấm → bấm "Start · 2 cards due" → bấm thẻ → "Got it" → bấm thẻ → "Again" → thấy "Session complete", ô Got it = 1, Again = 1, nút "Review 1 missed card".
- [ ] **Step 2: Chạy** `./gradlew connectedDebugAndroidTest`, kỳ vọng PASS (nếu fail thì sửa code app, không sửa test cho qua).
- [ ] **Step 3: Chạy toàn bộ** `./gradlew testDebugUnitTest assembleRelease`, kỳ vọng PASS (bản release có R8, kiểm tra Import vẫn parse được).
- [ ] **Step 4: Chốt tài liệu**: mục 26 khớp toàn bộ code, cây thư mục `CLAUDE.md`, đánh dấu spec "đã làm xong". Dừng hỏi đáp, commit `test: main flow UI test; docs`.
