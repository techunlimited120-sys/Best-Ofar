package com.example.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailDefaults
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.R
import com.example.ui.components.BestOfarTopBar
import com.example.ui.components.NotificationsBottomSheet
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.ArticleDetailScreen
import com.example.ui.screens.BookmarkScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PaymentScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.TrendingScreen

object BestOfarRoutes {
    const val HOME = "home"
    const val TRENDING = "trending"
    const val BOOKMARK = "bookmark"
    const val PROFILE = "profile"
    const val PAYMENT = "payment"
    const val ADMIN = "admin"
    const val ARTICLE_DETAIL = "article_detail/{articleId}"

    fun articleDetail(articleId: Int): String = "article_detail/$articleId"
}

private data class TopLevelTab(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val TopLevelTabs = listOf(
    TopLevelTab(
        route = BestOfarRoutes.HOME,
        labelRes = R.string.nav_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        testTag = "nav_home"
    ),
    TopLevelTab(
        route = BestOfarRoutes.TRENDING,
        labelRes = R.string.nav_trending,
        selectedIcon = Icons.Filled.LocalFireDepartment,
        unselectedIcon = Icons.Outlined.LocalFireDepartment,
        testTag = "nav_trending"
    ),
    TopLevelTab(
        route = BestOfarRoutes.BOOKMARK,
        labelRes = R.string.nav_bookmark,
        selectedIcon = Icons.Filled.Bookmark,
        unselectedIcon = Icons.Outlined.BookmarkBorder,
        testTag = "nav_bookmark"
    ),
    TopLevelTab(
        route = BestOfarRoutes.PROFILE,
        labelRes = R.string.nav_profile,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        testTag = "nav_profile"
    )
)

@Composable
fun BestOfarApp(
    viewModel: BestOfarViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val snackbarHostState = remember { SnackbarHostState() }
    var showNotificationsSheet by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.snackbarMessage) {
        val message = uiState.snackbarMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.consumeSnackbar()
        }
    }

    val isTopLevelDestination = currentRoute in TopLevelTabs.map { it.route }

    fun navigateToTab(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavigationRail = maxWidth >= 600.dp

        Row(modifier = Modifier.fillMaxSize()) {
            if (useNavigationRail && isTopLevelDestination) {
                NavigationRail(
                    windowInsets = NavigationRailDefaults.windowInsets,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    TopLevelTabs.forEach { tab ->
                        val selected = currentRoute == tab.route
                        NavigationRailItem(
                            selected = selected,
                            onClick = { navigateToTab(tab.route) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = stringResource(tab.labelRes)
                                )
                            },
                            label = { Text(stringResource(tab.labelRes)) },
                            modifier = Modifier.testTag(tab.testTag)
                        )
                    }
                }
            }

            Scaffold(
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.background,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                topBar = {
                    if (isTopLevelDestination) {
                        BestOfarTopBar(
                            isSearchActive = uiState.isSearchVisible,
                            hasUnreadNotifications = uiState.hasUnreadNotifications,
                            isPremiumUser = uiState.userProfile.isPremium,
                            onSearchToggle = {
                                if (currentRoute != BestOfarRoutes.HOME) {
                                    navigateToTab(BestOfarRoutes.HOME)
                                }
                                viewModel.toggleSearch()
                            },
                            onNotificationsClick = {
                                viewModel.markNotificationsRead()
                                showNotificationsSheet = true
                            }
                        )
                    }
                },
                bottomBar = {
                    if (!useNavigationRail && isTopLevelDestination) {
                        NavigationBar(
                            windowInsets = NavigationBarDefaults.windowInsets,
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            TopLevelTabs.forEach { tab ->
                                val selected = currentRoute == tab.route
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { navigateToTab(tab.route) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = stringResource(tab.labelRes)
                                        )
                                    },
                                    label = { Text(stringResource(tab.labelRes)) },
                                    modifier = Modifier.testTag(tab.testTag)
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = BestOfarRoutes.HOME,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    composable(BestOfarRoutes.HOME) {
                        HomeScreen(
                            uiState = uiState,
                            onCategorySelect = viewModel::selectCategory,
                            onSearchQueryChange = viewModel::updateSearchQuery,
                            onReadArticle = { id ->
                                navController.navigate(BestOfarRoutes.articleDetail(id))
                            },
                            onToggleBookmark = viewModel::toggleBookmark,
                            onNavigateToPayment = {
                                navController.navigate(BestOfarRoutes.PAYMENT)
                            }
                        )
                    }

                    composable(BestOfarRoutes.TRENDING) {
                        TrendingScreen(
                            uiState = uiState,
                            onReadArticle = { id ->
                                navController.navigate(BestOfarRoutes.articleDetail(id))
                            },
                            onToggleBookmark = viewModel::toggleBookmark,
                            onNavigateToPayment = {
                                navController.navigate(BestOfarRoutes.PAYMENT)
                            }
                        )
                    }

                    composable(BestOfarRoutes.BOOKMARK) {
                        BookmarkScreen(
                            uiState = uiState,
                            onReadArticle = { id ->
                                navController.navigate(BestOfarRoutes.articleDetail(id))
                            },
                            onToggleBookmark = viewModel::toggleBookmark,
                            onNavigateToPayment = {
                                navController.navigate(BestOfarRoutes.PAYMENT)
                            },
                            onExploreHome = {
                                navigateToTab(BestOfarRoutes.HOME)
                            }
                        )
                    }

                    composable(BestOfarRoutes.PROFILE) {
                        ProfileScreen(
                            uiState = uiState,
                            onUpdateProfile = viewModel::updateProfile,
                            onNavigateToPayment = {
                                navController.navigate(BestOfarRoutes.PAYMENT)
                            },
                            onNavigateToAdmin = {
                                navController.navigate(BestOfarRoutes.ADMIN)
                            }
                        )
                    }

                    composable(BestOfarRoutes.PAYMENT) {
                        PaymentScreen(
                            isAlreadyPremium = uiState.userProfile.isPremium,
                            defaultPhone = uiState.userProfile.phone,
                            onBack = { navController.popBackStack() },
                            onSubmitPayment = { method, phone, trxId ->
                                viewModel.submitPayment(
                                    method = method,
                                    phoneNumber = phone,
                                    transactionId = trxId,
                                    onSuccess = { navController.popBackStack() }
                                )
                            }
                        )
                    }

                    composable(BestOfarRoutes.ADMIN) {
                        AdminScreen(
                            allArticles = uiState.allArticles,
                            onBack = { navController.popBackStack() },
                            onPublishArticle = { title, summary, content, category, location, cover, isTrending, isPremiumOnly ->
                                viewModel.publishArticle(
                                    title = title,
                                    summary = summary,
                                    content = content,
                                    category = category,
                                    location = location,
                                    cover = cover,
                                    isTrending = isTrending,
                                    isPremiumOnly = isPremiumOnly,
                                    onPublished = {}
                                )
                            },
                            onDeleteArticle = viewModel::deleteArticle
                        )
                    }

                    composable(
                        route = BestOfarRoutes.ARTICLE_DETAIL,
                        arguments = listOf(navArgument("articleId") { type = NavType.IntType })
                    ) { entry ->
                        val articleId = entry.arguments?.getInt("articleId") ?: -1
                        val article by viewModel.observeArticle(articleId)
                            .collectAsStateWithLifecycle(initialValue = null)
                        ArticleDetailScreen(
                            article = article,
                            isUserPremium = uiState.userProfile.isPremium,
                            onBack = { navController.popBackStack() },
                            onToggleBookmark = viewModel::toggleBookmark,
                            onNavigateToPayment = {
                                navController.navigate(BestOfarRoutes.PAYMENT)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showNotificationsSheet) {
        NotificationsBottomSheet(
            notifications = uiState.notifications,
            onDismiss = { showNotificationsSheet = false },
            onNotificationClick = { articleId ->
                navController.navigate(BestOfarRoutes.articleDetail(articleId))
            }
        )
    }
}
