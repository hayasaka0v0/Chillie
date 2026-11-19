package com.huyvu.flashcard.domain.model

/**
 * `enum class` = một kiểu chỉ có ĐÚNG mấy giá trị được liệt kê sẵn.
 * Biến kiểu `Answer` chỉ có thể là `Answer.AGAIN` hoặc `Answer.GOT_IT`, không thể là gì khác.
 */

/** Người học bấm nút nào sau khi lật thẻ. */
enum class Answer { AGAIN, GOT_IT }

/**
 * Kiểu lượt học:
 * - NORMAL: chấm điểm thật, đổi lịch ôn của thẻ.
 * - PRACTICE: học thử ("Study all anyway"), chỉ ghi nhật ký, KHÔNG đổi lịch ôn.
 */
enum class SessionMode { NORMAL, PRACTICE }
