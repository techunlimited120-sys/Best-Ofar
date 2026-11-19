package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ArticleEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles ORDER BY createdAt DESC, id DESC")
    fun observeAllArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE isTrending = 1 ORDER BY trendingRank ASC, createdAt DESC LIMIT 10")
    fun observeTrendingArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE isBookmarked = 1 ORDER BY createdAt DESC, id DESC")
    fun observeBookmarkedArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE id = :articleId LIMIT 1")
    fun observeArticleById(articleId: Int): Flow<ArticleEntity?>

    @Query("SELECT COUNT(*) FROM articles")
    suspend fun getArticleCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<ArticleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: ArticleEntity): Long

    @Query("UPDATE articles SET isBookmarked = :bookmarked WHERE id = :articleId")
    suspend fun setBookmarked(articleId: Int, bookmarked: Boolean)

    @Query("DELETE FROM articles WHERE id = :articleId")
    suspend fun deleteArticleById(articleId: Int)
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY timestamp DESC")
    fun observeAllPayments(): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun observeProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: UserProfileEntity)
}

@Database(
    entities = [ArticleEntity::class, PaymentEntity::class, UserProfileEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BestOfarDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun paymentDao(): PaymentDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: BestOfarDatabase? = null

        fun getInstance(context: Context): BestOfarDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BestOfarDatabase::class.java,
                    "best_ofar_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
