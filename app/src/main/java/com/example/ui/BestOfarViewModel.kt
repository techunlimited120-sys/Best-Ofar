package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.BestOfarDatabase
import com.example.data.model.ArticleCover
import com.example.data.model.ArticleEntity
import com.example.data.model.NewsCategory
import com.example.data.model.NotificationItem
import com.example.data.model.PaymentEntity
import com.example.data.model.UserProfileEntity
import com.example.data.repository.BestOfarRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BestOfarUiState(
    val selectedCategory: NewsCategory = NewsCategory.LATEST,
    val isSearchVisible: Boolean = false,
    val searchQuery: String = "",
    val filteredArticles: List<ArticleEntity> = emptyList(),
    val allArticles: List<ArticleEntity> = emptyList(),
    val trendingArticles: List<ArticleEntity> = emptyList(),
    val bookmarkedArticles: List<ArticleEntity> = emptyList(),
    val userProfile: UserProfileEntity = UserProfileEntity(
        id = 1,
        name = "আপনার নাম",
        phone = "01XXXXXXXXX",
        isPremium = false,
        premiumPlanLabel = "নেই"
    ),
    val paymentHistory: List<PaymentEntity> = emptyList(),
    val notifications: List<NotificationItem> = emptyList(),
    val hasUnreadNotifications: Boolean = true,
    val snackbarMessage: String? = null
)

private data class FilterParams(
    val category: NewsCategory,
    val isSearchVisible: Boolean,
    val searchQuery: String,
    val hasUnreadNotifications: Boolean,
    val snackbarMessage: String?
)

class BestOfarViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BestOfarRepository

    private val _selectedCategory = MutableStateFlow(NewsCategory.LATEST)
    private val _isSearchVisible = MutableStateFlow(false)
    private val _searchQuery = MutableStateFlow("")
    private val _hasUnreadNotifications = MutableStateFlow(true)
    private val _snackbarMessage = MutableStateFlow<String?>(null)

    init {
        val db = BestOfarDatabase.getInstance(application)
        repository = BestOfarRepository(
            articleDao = db.articleDao(),
            paymentDao = db.paymentDao(),
            userProfileDao = db.userProfileDao()
        )
        viewModelScope.launch {
            repository.ensureSeeded()
        }
    }

    private val filterParamsFlow: Flow<FilterParams> = combine(
        _selectedCategory,
        _isSearchVisible,
        _searchQuery,
        _hasUnreadNotifications,
        _snackbarMessage
    ) { category, isSearchVisible, query, hasUnread, snackbar ->
        FilterParams(category, isSearchVisible, query, hasUnread, snackbar)
    }

    private val dataFlow = combine(
        repository.allArticles,
        repository.trendingArticles,
        repository.bookmarkedArticles,
        repository.userProfile,
        repository.allPayments
    ) { all, trending, bookmarked, profile, payments ->
        DataBundle(all, trending, bookmarked, profile, payments)
    }

    val uiState: StateFlow<BestOfarUiState> = combine(
        filterParamsFlow,
        dataFlow
    ) { filters, data ->
        val query = filters.searchQuery.trim()
        val filtered = data.allArticles.filter { article ->
            val categoryMatch = filters.category == NewsCategory.LATEST ||
                article.category == filters.category.bengaliLabel
            val queryMatch = query.isEmpty() ||
                article.title.contains(query, ignoreCase = true) ||
                article.summary.contains(query, ignoreCase = true) ||
                article.location.contains(query, ignoreCase = true) ||
                article.category.contains(query, ignoreCase = true)
            categoryMatch && queryMatch
        }

        val dynamicNotifications = data.allArticles.take(4).mapIndexed { index, article ->
            NotificationItem(
                id = index + 1,
                title = article.title,
                subtitle = "${article.category} • ${article.location}",
                timeAgo = article.timeAgo,
                articleId = article.id
            )
        }

        BestOfarUiState(
            selectedCategory = filters.category,
            isSearchVisible = filters.isSearchVisible,
            searchQuery = filters.searchQuery,
            filteredArticles = filtered,
            allArticles = data.allArticles,
            trendingArticles = data.trendingArticles,
            bookmarkedArticles = data.bookmarkedArticles,
            userProfile = data.userProfile ?: UserProfileEntity(
                id = 1,
                name = "আপনার নাম",
                phone = "01XXXXXXXXX",
                isPremium = false,
                premiumPlanLabel = "নেই"
            ),
            paymentHistory = data.payments,
            notifications = dynamicNotifications,
            hasUnreadNotifications = filters.hasUnreadNotifications,
            snackbarMessage = filters.snackbarMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BestOfarUiState()
    )

    fun observeArticle(articleId: Int): Flow<ArticleEntity?> =
        repository.observeArticleById(articleId)

    fun selectCategory(category: NewsCategory) {
        _selectedCategory.value = category
    }

    fun toggleSearch() {
        val next = !_isSearchVisible.value
        _isSearchVisible.value = next
        if (!next) {
            _searchQuery.value = ""
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun markNotificationsRead() {
        _hasUnreadNotifications.value = false
    }

    fun toggleBookmark(article: ArticleEntity) {
        viewModelScope.launch {
            repository.toggleBookmark(article.id, article.isBookmarked)
            _snackbarMessage.value = if (!article.isBookmarked) {
                "বুকমার্কে সংরক্ষণ করা হয়েছে"
            } else {
                "বুকমার্ক থেকে সরানো হয়েছে"
            }
        }
    }

    fun updateProfile(name: String, phone: String) {
        viewModelScope.launch {
            repository.updateProfile(name, phone)
            _snackbarMessage.value = "প্রোফাইল তথ্য আপডেট হয়েছে"
        }
    }

    fun submitPayment(
        method: String,
        phoneNumber: String,
        transactionId: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.processPayment(
                method = method,
                phoneNumber = phoneNumber,
                transactionId = transactionId,
                amountTaka = 100
            )
            _snackbarMessage.value = "অভিনন্দন! আপনার প্রিমিয়াম মেম্বারশিপ সক্রিয় হয়েছে"
            onSuccess()
        }
    }

    fun publishArticle(
        title: String,
        summary: String,
        content: String,
        category: NewsCategory,
        location: String,
        cover: ArticleCover,
        isTrending: Boolean,
        isPremiumOnly: Boolean,
        onPublished: () -> Unit
    ) {
        viewModelScope.launch {
            repository.publishArticle(
                title = title,
                summary = summary,
                content = content,
                category = category,
                location = location,
                cover = cover,
                isTrending = isTrending,
                isPremiumOnly = isPremiumOnly
            )
            _snackbarMessage.value = "নতুন সংবাদ সফলভাবে প্রকাশিত হয়েছে"
            onPublished()
        }
    }

    fun deleteArticle(articleId: Int) {
        viewModelScope.launch {
            repository.deleteArticle(articleId)
            _snackbarMessage.value = "সংবাদটি মুছে ফেলা হয়েছে"
        }
    }

    fun consumeSnackbar() {
        _snackbarMessage.value = null
    }

    private data class DataBundle(
        val allArticles: List<ArticleEntity>,
        val trendingArticles: List<ArticleEntity>,
        val bookmarkedArticles: List<ArticleEntity>,
        val userProfile: UserProfileEntity?,
        val payments: List<PaymentEntity>
    )
}
