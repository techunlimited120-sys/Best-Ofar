package com.example.data.repository

import com.example.data.local.ArticleDao
import com.example.data.local.PaymentDao
import com.example.data.local.UserProfileDao
import com.example.data.model.ArticleCover
import com.example.data.model.ArticleEntity
import com.example.data.model.NewsCategory
import com.example.data.model.PaymentEntity
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BestOfarRepository(
    private val articleDao: ArticleDao,
    private val paymentDao: PaymentDao,
    private val userProfileDao: UserProfileDao
) {
    val allArticles: Flow<List<ArticleEntity>> = articleDao.observeAllArticles()
    val trendingArticles: Flow<List<ArticleEntity>> = articleDao.observeTrendingArticles()
    val bookmarkedArticles: Flow<List<ArticleEntity>> = articleDao.observeBookmarkedArticles()
    val allPayments: Flow<List<PaymentEntity>> = paymentDao.observeAllPayments()
    val userProfile: Flow<UserProfileEntity?> = userProfileDao.observeProfile()

    fun observeArticleById(articleId: Int): Flow<ArticleEntity?> =
        articleDao.observeArticleById(articleId)

    suspend fun ensureSeeded() {
        if (userProfileDao.getProfileOnce() == null) {
            userProfileDao.upsertProfile(
                UserProfileEntity(
                    id = 1,
                    name = "আপনার নাম",
                    phone = "01XXXXXXXXX",
                    isPremium = false,
                    premiumPlanLabel = "নেই"
                )
            )
        }
        if (articleDao.getArticleCount() == 0) {
            articleDao.insertAll(initialArticles())
        }
    }

    suspend fun toggleBookmark(articleId: Int, currentState: Boolean) {
        articleDao.setBookmarked(articleId, !currentState)
    }

    suspend fun updateProfile(name: String, phone: String) {
        val current = userProfileDao.getProfileOnce() ?: UserProfileEntity(
            id = 1,
            name = name,
            phone = phone
        )
        userProfileDao.upsertProfile(
            current.copy(
                name = name.trim(),
                phone = phone.trim()
            )
        )
    }

    suspend fun processPayment(
        method: String,
        phoneNumber: String,
        transactionId: String,
        amountTaka: Int = 100
    ) {
        val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val nowLabel = formatter.format(Date())
        paymentDao.insertPayment(
            PaymentEntity(
                method = method,
                phoneNumber = phoneNumber.trim(),
                transactionId = transactionId.trim().uppercase(),
                amountTaka = amountTaka,
                status = "সফল",
                paidAtLabel = nowLabel
            )
        )
        val current = userProfileDao.getProfileOnce() ?: UserProfileEntity(
            id = 1,
            name = "প্রিমিয়াম সদস্য",
            phone = phoneNumber.trim()
        )
        userProfileDao.upsertProfile(
            current.copy(
                phone = if (current.phone == "01XXXXXXXXX" || current.phone.isBlank()) {
                    phoneNumber.trim()
                } else {
                    current.phone
                },
                isPremium = true,
                premiumPlanLabel = "প্রিমিয়াম (৳১০০/মাস)"
            )
        )
    }

    suspend fun publishArticle(
        title: String,
        summary: String,
        content: String,
        category: NewsCategory,
        location: String,
        cover: ArticleCover,
        isTrending: Boolean,
        isPremiumOnly: Boolean
    ) {
        articleDao.insertArticle(
            ArticleEntity(
                title = title.trim(),
                summary = summary.trim(),
                content = content.trim(),
                category = category.bengaliLabel,
                timeAgo = "এইমাত্র",
                location = location.trim().ifEmpty { "ঢাকা" },
                coverKey = cover.key,
                isBookmarked = false,
                isTrending = isTrending,
                trendingRank = if (isTrending) 1 else 0,
                viewCountLabel = "নতুন প্রকাশিত",
                isPremiumOnly = isPremiumOnly,
                isUserPublished = true,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteArticle(articleId: Int) {
        articleDao.deleteArticleById(articleId)
    }

    private fun initialArticles(): List<ArticleEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            ArticleEntity(
                title = "ঢাকায় বড় পরিবর্তন, জানুন বিস্তারিত...",
                summary = "রাজধানীর পরিবহন, ডিজিটাল সেবা এবং নগর ব্যবস্থাপনায় আসছে নতুন কাঠামো ও দ্রুতগতির প্রকল্প।",
                content = "ঢাকা শহরের যানজট নিরসন এবং নাগরিক সেবা আরও সহজ করতে নতুন সমন্বিত পরিকল্পনা গ্রহণ করা হয়েছে। মেট্রোরেল, এলিভেটেড এক্সপ্রেসওয়ে এবং আধুনিক স্মার্ট ট্রাফিক সিগন্যালিং ব্যবস্থা চালুর ফলে দৈনন্দিন যাতায়াতে সময় সাশ্রয় হবে বলে আশা করছেন নগর পরিকল্পনাবিদরা।\n\nএছাড়া প্রতিটি ওয়ার্ডে ডিজিটাল সেবা কেন্দ্র স্থাপনের মাধ্যমে জন্মনিবন্ধন, হোল্ডিং ট্যাক্স এবং নাগরিক সনদ অনলাইনে দ্রুত পাওয়া যাবে। প্রিমিয়াম পাঠকদের জন্য থাকছে সম্পূর্ণ বাজেট বিশ্লেষণ ও সময়সূচি।",
                category = NewsCategory.POLITICS.bengaliLabel,
                timeAgo = "১০ মিনিট আগে",
                location = "ঢাকা",
                coverKey = ArticleCover.DHAKA.key,
                isTrending = true,
                trendingRank = 1,
                viewCountLabel = "১৮.৫কে পঠিত",
                isPremiumOnly = false,
                createdAt = now - 600_000L
            ),
            ArticleEntity(
                title = "বাংলাদেশ জিতলো বড় ম্যাচে, দেখুন...",
                summary = "উত্তেজনাপূর্ণ শেষ ওভারে দুর্দান্ত বোলিং ও দুরন্ত ব্যাটিংয়ে ঐতিহাসিক জয় তুলে নিল টাইগাররা।",
                content = "দর্শকে ঠাসা স্টেডিয়ামে রোমাঞ্চকর লড়াই শেষে বড় জয় তুলে নিয়েছে বাংলাদেশ ক্রিকেট দল। শেষ ১২ বলে জয়ের জন্য দরকার ছিল ২২ রান, আর তখনই ঝোড়ো ব্যাটিংয়ে ম্যাচ ঘুরিয়ে দেন মিডল অর্ডার ব্যাটাররা।\n\nবল হাতেও শুরু থেকেই প্রতিপক্ষের ওপর চাপ ধরে রেখেছিলেন পেসাররা। ম্যাচসেরা পুরস্কার জিতে অধিনায়ক বলেন, দলের একতা ও দর্শকদের সমর্থনই এই জয়ের মূল শক্তি।",
                category = NewsCategory.SPORTS.bengaliLabel,
                timeAgo = "৩০ মিনিট আগে",
                location = "চট্টগ্রাম",
                coverKey = ArticleCover.CRICKET.key,
                isTrending = true,
                trendingRank = 2,
                viewCountLabel = "২৪.২কে পঠিত",
                isPremiumOnly = false,
                createdAt = now - 1_800_000L
            ),
            ArticleEntity(
                title = "নতুন সিনেমা নিয়ে তোলপাড়...",
                summary = "মুক্তির প্রথম দিনেই প্রেক্ষাগৃহে উপচে পড়া ভিড়, প্রশংসায় ভাসছে নির্মাণশৈলী ও আবহসংগীত।",
                content = "দীর্ঘ প্রতীক্ষার পর মুক্তি পাওয়া নতুন বাংলা সিনেমা ঘিরে দর্শকদের মাঝে ব্যাপক উৎসাহ দেখা গেছে। দেশের প্রধান মাল্টিপ্লেক্স ও ঐতিহ্যবাহী হলগুলোতে সকাল থেকেই সব শো হাউসফুল যাচ্ছে।\n\nসমালোচকদের মতে, গল্পের গভীরতা, শক্তিশালী অভিনয় এবং আধুনিক সিনেমাটোগ্রাফি দর্শকদের মন জয় করেছে। প্রিমিয়াম সেকশনে থাকছে পরিচালক ও কলাকুশলীদের এক্সক্লুসিভ সাক্ষাৎকার।",
                category = NewsCategory.ENTERTAINMENT.bengaliLabel,
                timeAgo = "১ ঘণ্টা আগে",
                location = "ঢাকা",
                coverKey = ArticleCover.CINEMA.key,
                isTrending = true,
                trendingRank = 3,
                viewCountLabel = "১৫.৭কে পঠিত",
                isPremiumOnly = true,
                createdAt = now - 3_600_000L
            ),
            ArticleEntity(
                title = "স্মার্টফোনে নতুন এআই ফিচার ও দ্রুতগতির চিপসেট",
                summary = "দৈনন্দিন কাজ ও বাংলা ভয়েস টাইপিং আরও নিখুঁত করতে নতুন প্রজন্মের ডিভাইসে আসছে বড় আপডেট।",
                content = "প্রযুক্তি বিশ্বে এখন সবচেয়ে বড় আলোচনা অন-ডিভাইস এআই নিয়ে। নতুন স্মার্টফোনগুলোতে ইন্টারনেট ছাড়াই রিয়েল-টাইম অনুবাদ, ছবি সম্পাদনা এবং বাংলা ভয়েস কমান্ডের সুবিধা যুক্ত হচ্ছে।\n\nবিশেষজ্ঞরা বলছেন, ব্যাটারি সাশ্রয়ী চিপসেট এবং উন্নত ক্যামেরা সেন্সরের সমন্বয় সাধারণ ব্যবহারকারীদের অভিজ্ঞতা পুরোপুরি বদলে দেবে।",
                category = NewsCategory.TECH.bengaliLabel,
                timeAgo = "২ ঘণ্টা আগে",
                location = "ঢাকা",
                coverKey = ArticleCover.TECH.key,
                isTrending = true,
                trendingRank = 4,
                viewCountLabel = "১২.৯কে পঠিত",
                isPremiumOnly = false,
                createdAt = now - 7_200_000L
            ),
            ArticleEntity(
                title = "আজকের সবচেয়ে ভাইরাল খবর: তরুণ উদ্যোক্তাদের নতুন উদ্যোগ",
                summary = "দেশীয় হস্তশিল্প ও অনলাইন মার্কেটপ্লেসের সমন্বয়ে নতুন কর্মসংস্থান সৃষ্টি করছেন তরুণরা।",
                content = "সামাজিক যোগাযোগমাধ্যমে আজ সবচেয়ে বেশি আলোচিত হয়েছে দেশের বিভিন্ন জেলার তরুণ উদ্যোক্তাদের সাফল্যের গল্প। স্থানীয় কারিগরদের তৈরি পণ্য এখন সরাসরি দেশের গণ্ডি পেরিয়ে আন্তর্জাতিক বাজারে পৌঁছে যাচ্ছে।\n\nমাত্র স্বল্প পুঁজিতে শুরু করে কীভাবে তারা ডিজিটাল মার্কেটিংয়ের মাধ্যমে সফল ব্র্যান্ড গড়ে তুলেছেন, তার বিস্তারিত কেস স্টাডি থাকছে এই প্রতিবেদনে।",
                category = NewsCategory.LIFESTYLE.bengaliLabel,
                timeAgo = "৩ ঘণ্টা আগে",
                location = "সিলেট",
                coverKey = ArticleCover.DHAKA.key,
                isTrending = true,
                trendingRank = 5,
                viewCountLabel = "২১.০কে পঠিত",
                isPremiumOnly = true,
                createdAt = now - 10_800_000L
            ),
            ArticleEntity(
                title = "অর্থনৈতিক করিডোর ও আঞ্চলিক বাণিজ্য সম্প্রসারণ আলোচনা",
                summary = "বন্দর আধুনিকায়ন এবং রপ্তানি প্রক্রিয়া সহজ করতে নতুন নীতিমালা প্রস্তাব করা হয়েছে।",
                content = "আঞ্চলিক বাণিজ্য গতিশীল করতে চট্টগ্রাম ও মোংলা বন্দরের সক্ষমতা বাড়ানোর উদ্যোগ নেওয়া হয়েছে। কাস্টমস প্রক্রিয়া সম্পূর্ণ অটোমেশন করা হলে পণ্য খালাসের সময় অর্ধেকে নেমে আসবে।\n\nঅর্থনীতিবিদদের মতে, সময় ও পরিবহন ব্যয় কমলে তৈরি পোশাক, কৃষি প্রক্রিয়াজাত পণ্য এবং আইটি খাতের রপ্তানি উল্লেখযোগ্যভাবে বৃদ্ধি পাবে।",
                category = NewsCategory.POLITICS.bengaliLabel,
                timeAgo = "৪ ঘণ্টা আগে",
                location = "চট্টগ্রাম",
                coverKey = ArticleCover.DHAKA.key,
                isTrending = true,
                trendingRank = 6,
                viewCountLabel = "৯.৪কে পঠিত",
                isPremiumOnly = true,
                createdAt = now - 14_400_000L
            ),
            ArticleEntity(
                title = "যুব ফুটবল লিগে নতুন তারকাদের ঝলক",
                summary = "দেশের জেলা পর্যায়ে প্রতিভা অন্বেষণ টুর্নামেন্ট থেকে উঠে আসছে একঝাঁক প্রতিভাবান ফুটবলার।",
                content = "জাতীয় পর্যায়ের যুব ফুটবল প্রতিযোগিতায় দারুণ নৈপুণ্য দেখিয়েছে রাজশাহী ও খুলনা বিভাগের দল। আধুনিক প্রশিক্ষণ ও ফিটনেস ক্যাম্পের মাধ্যমে তরুণ খেলোয়াড়দের আন্তর্জাতিক মানের জন্য প্রস্তুত করা হচ্ছে।\n\nকোচদের বিশ্বাস, নিয়মিত ঘরোয়া লিগ আয়োজন অব্যাহত থাকলে আগামী কয়েক বছরে জাতীয় দলের পারফরম্যান্সে বড় পরিবর্তন আসবে।",
                category = NewsCategory.SPORTS.bengaliLabel,
                timeAgo = "৫ ঘণ্টা আগে",
                location = "রাজশাহী",
                coverKey = ArticleCover.CRICKET.key,
                isTrending = true,
                trendingRank = 7,
                viewCountLabel = "৮.১কে পঠিত",
                isPremiumOnly = false,
                createdAt = now - 18_000_000L
            ),
            ArticleEntity(
                title = "ওটিটি প্ল্যাটফর্মে বাংলা ওয়েব সিরিজের জয়যাত্রা",
                summary = "বৈচিত্র্যময় গল্প ও নিখুঁত নির্মাণের কারণে আন্তর্জাতিক দর্শকদেরও নজর কাড়ছে দেশীয় কনটেন্ট।",
                content = "সাম্প্রতিক সময়ে বাংলা ওয়েব সিরিজ ও মৌলিক চলচ্চিত্র ওটিটি প্ল্যাটফর্মে নতুন দর্শকশ্রেণি তৈরি করেছে। থ্রিলার, সামাজিক নাটক এবং ঐতিহাসিক প্রেক্ষাপটের গল্পগুলো প্রশংসিত হচ্ছে।\n\nনির্মাতারা জানান, সঠিক বাজেট ও চিত্রনাট্যের প্রতি যত্নবান হওয়ায় প্রবাসী বাঙালিদের পাশাপাশি বিদেশি দর্শকদের কাছেও বাংলা কনটেন্ট জনপ্রিয় হচ্ছে।",
                category = NewsCategory.ENTERTAINMENT.bengaliLabel,
                timeAgo = "৬ ঘণ্টা আগে",
                location = "ঢাকা",
                coverKey = ArticleCover.CINEMA.key,
                isTrending = true,
                trendingRank = 8,
                viewCountLabel = "৭.৬কে পঠিত",
                isPremiumOnly = false,
                createdAt = now - 21_600_000L
            ),
            ArticleEntity(
                title = "ফ্রিল্যান্সারদের জন্য নতুন ডিজিটাল পেমেন্ট ও নিরাপত্তা গাইড",
                summary = "দ্রুত রেমিট্যান্স গ্রহণ এবং অ্যাকাউন্টের টু-ফ্যাক্টর নিরাপত্তা নিশ্চিত করার জরুরি পরামর্শ।",
                content = "দেশের কয়েক লাখ তরুণ তথ্যপ্রযুক্তি খাতে ফ্রিল্যান্সিংয়ের মাধ্যমে বৈদেশিক মুদ্রা অর্জন করছেন। নিরাপদ লেনদেন এবং সাইবার সুরক্ষা বজায় রাখতে আধুনিক ভেরিফিকেশন ও শক্তিশালী পাসকি ব্যবহারের পরামর্শ দিয়েছেন বিশেষজ্ঞরা।\n\nএছাড়া নতুন ফ্রিল্যান্সারদের জন্য ক্লাউড ডেভেলপমেন্ট ও ইউআই/ইউএক্স ডিজাইন শেখার বিনামূল্যের রিসোর্স তালিকাও যুক্ত করা হয়েছে।",
                category = NewsCategory.TECH.bengaliLabel,
                timeAgo = "৭ ঘণ্টা আগে",
                location = "খুলনা",
                coverKey = ArticleCover.TECH.key,
                isTrending = true,
                trendingRank = 9,
                viewCountLabel = "১১.৩কে পঠিত",
                isPremiumOnly = true,
                createdAt = now - 25_200_000L
            ),
            ArticleEntity(
                title = "ব্যস্ত জীবনে মানসিক প্রশান্তি ও স্বাস্থ্যকর অভ্যাসের ৫টি সহজ কৌশল",
                summary = "প্রতিদিন মাত্র ২০ মিনিট সময় বের করে কীভাবে কর্মক্ষমতা ও ঘুমের মান বাড়ানো যায়।",
                content = "আধুনিক কর্মব্যস্ত জীবনে পর্যাপ্ত ঘুম, সুষম খাদ্য এবং ডিজিটাল ডিটক্স অত্যন্ত জরুরি। চিকিৎসক ও লাইফস্টাইল কোচদের মতে, ঘুমের অন্তত এক ঘণ্টা আগে মোবাইল স্ক্রিন থেকে দূরে থাকলে গভীর ঘুম নিশ্চিত হয়।\n\nসকালের হালকা ব্যায়াম, পর্যাপ্ত পানি পান এবং প্রতিদিন অন্তত ২০ মিনিট বই পড়া মানসিক চাপ কমাতে কার্যকর ভূমিকা রাখে।",
                category = NewsCategory.LIFESTYLE.bengaliLabel,
                timeAgo = "৮ ঘণ্টা আগে",
                location = "ঢাকা",
                coverKey = ArticleCover.TECH.key,
                isTrending = true,
                trendingRank = 10,
                viewCountLabel = "১০.৫কে পঠিত",
                isPremiumOnly = false,
                createdAt = now - 28_800_000L
            )
        )
    }
}
