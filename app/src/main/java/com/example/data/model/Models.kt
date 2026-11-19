package com.example.data.model

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.R

enum class NewsCategory(val bengaliLabel: String) {
    LATEST("সর্বশেষ"),
    POLITICS("রাজনীতি"),
    SPORTS("খেলাধুলা"),
    ENTERTAINMENT("বিনোদন"),
    TECH("প্রযুক্তি"),
    LIFESTYLE("লাইফস্টাইল");

    companion object {
        fun publishableCategories(): List<NewsCategory> =
            listOf(POLITICS, SPORTS, ENTERTAINMENT, TECH, LIFESTYLE)
    }
}

enum class ArticleCover(val key: String, val label: String, @DrawableRes val drawableRes: Int) {
    DHAKA("dhaka", "ঢাকা নগরী", R.drawable.img_news_dhaka),
    CRICKET("cricket", "ক্রিকেট স্টেডিয়াম", R.drawable.img_news_cricket),
    CINEMA("cinema", "সিনেমা হল", R.drawable.img_news_cinema),
    TECH("tech", "প্রযুক্তি ও স্মার্টফোন", R.drawable.img_news_tech);

    companion object {
        fun fromKey(key: String): ArticleCover =
            entries.firstOrNull { it.key == key } ?: DHAKA
    }
}

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val summary: String,
    val content: String,
    val category: String,
    val timeAgo: String,
    val location: String,
    val coverKey: String,
    val isBookmarked: Boolean = false,
    val isTrending: Boolean = false,
    val trendingRank: Int = 0,
    val viewCountLabel: String = "১.২কে পঠিত",
    val isPremiumOnly: Boolean = false,
    val isUserPublished: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val method: String,
    val phoneNumber: String,
    val transactionId: String,
    val amountTaka: Int,
    val status: String,
    val paidAtLabel: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val phone: String,
    val isPremium: Boolean = false,
    val premiumPlanLabel: String = "নেই"
)

data class NotificationItem(
    val id: Int,
    val title: String,
    val subtitle: String,
    val timeAgo: String,
    val articleId: Int? = null
)
