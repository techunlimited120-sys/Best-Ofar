package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.BestOfarDatabase
import com.example.data.model.ArticleCover
import com.example.data.model.NewsCategory
import com.example.data.repository.BestOfarRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var database: BestOfarDatabase
    private lateinit var repository: BestOfarRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, BestOfarDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = BestOfarRepository(
            articleDao = database.articleDao(),
            paymentDao = database.paymentDao(),
            userProfileDao = database.userProfileDao()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Best Ofar", appName)
    }

    @Test
    fun `seed and bookmark article updates flow`() = runTest {
        repository.ensureSeeded()
        val initial = repository.allArticles.first()
        assertEquals(10, initial.size)

        val firstArticle = initial.first()
        repository.toggleBookmark(firstArticle.id, firstArticle.isBookmarked)

        val bookmarked = repository.bookmarkedArticles.first()
        assertEquals(1, bookmarked.size)
        assertEquals(firstArticle.id, bookmarked.first().id)
    }

    @Test
    fun `payment activates premium and saves history`() = runTest {
        repository.ensureSeeded()
        repository.processPayment(
            method = "বিকাশ (bKash)",
            phoneNumber = "01712345678",
            transactionId = "bof12345"
        )

        val profile = repository.userProfile.first()
        val payments = repository.allPayments.first()
        assertTrue(profile?.isPremium == true)
        assertEquals(1, payments.size)
        assertEquals("BOF12345", payments.first().transactionId)
    }

    @Test
    fun `admin publish adds article to feed`() = runTest {
        repository.ensureSeeded()
        repository.publishArticle(
            title = "নতুন প্রযুক্তি মেলা শুরু",
            summary = "ঢাকায় তিন দিনব্যাপী প্রযুক্তি মেলা শুরু হয়েছে।",
            content = "বিস্তারিত প্রতিবেদন এখানে।",
            category = NewsCategory.TECH,
            location = "ঢাকা",
            cover = ArticleCover.TECH,
            isTrending = true,
            isPremiumOnly = false
        )

        val articles = repository.allArticles.first()
        assertEquals(11, articles.size)
        assertTrue(articles.any { it.title == "নতুন প্রযুক্তি মেলা শুরু" })
    }
}
