package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.ArticleCover
import com.example.data.model.ArticleEntity
import com.example.data.model.NewsCategory
import com.example.ui.theme.EmeraldSuccess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    allArticles: List<ArticleEntity>,
    onBack: () -> Unit,
    onPublishArticle: (
        title: String,
        summary: String,
        content: String,
        category: NewsCategory,
        location: String,
        cover: ArticleCover,
        isTrending: Boolean,
        isPremiumOnly: Boolean
    ) -> Unit,
    onDeleteArticle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val publishableCategories = remember { NewsCategory.publishableCategories() }
    var title by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("ঢাকা") }
    var selectedCategory by remember { mutableStateOf(publishableCategories.first()) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }
    var selectedCover by remember { mutableStateOf(ArticleCover.DHAKA) }
    var isTrending by remember { mutableStateOf(true) }
    var isPremiumOnly by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.admin_panel_title)) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("admin_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .widthIn(max = 640.dp)
                    .fillMaxSize()
            ) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "নতুন সংবাদ প্রকাশ করুন",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            OutlinedTextField(
                                value = title,
                                onValueChange = {
                                    title = it
                                    errorText = null
                                },
                                label = { Text("সংবাদের শিরোনাম") },
                                placeholder = { Text("উদাহরণ: মেট্রোরেলের নতুন সময়সূচি ঘোষণা...") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_title_input")
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = selectedCategory.bengaliLabel,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("বিভাগ") },
                                        trailingIcon = {
                                            Icon(
                                                imageVector = Icons.Filled.ArrowDropDown,
                                                contentDescription = null
                                            )
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("admin_category_dropdown")
                                    )
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .clickable { categoryMenuExpanded = true }
                                    )
                                    DropdownMenu(
                                        expanded = categoryMenuExpanded,
                                        onDismissRequest = { categoryMenuExpanded = false }
                                    ) {
                                        publishableCategories.forEach { cat ->
                                            DropdownMenuItem(
                                                text = { Text(cat.bengaliLabel) },
                                                onClick = {
                                                    selectedCategory = cat
                                                    categoryMenuExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                OutlinedTextField(
                                    value = location,
                                    onValueChange = { location = it },
                                    label = { Text("স্থান") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("admin_location_input")
                                )
                            }

                            OutlinedTextField(
                                value = summary,
                                onValueChange = {
                                    summary = it
                                    errorText = null
                                },
                                label = { Text("সংক্ষিপ্ত বিবরণ") },
                                placeholder = { Text("২ লাইনের মূল সারসংক্ষেপ লিখুন...") },
                                maxLines = 2,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_summary_input")
                            )

                            OutlinedTextField(
                                value = content,
                                onValueChange = {
                                    content = it
                                    errorText = null
                                },
                                label = { Text("বিস্তারিত সংবাদ") },
                                placeholder = { Text("সম্পূর্ণ প্রতিবেদন এখানে লিখুন...") },
                                minLines = 3,
                                maxLines = 6,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_content_input")
                            )

                            Text(
                                text = "কভার ছবি নির্বাচন করুন",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(ArticleCover.entries, key = { it.key }) { cover ->
                                    val selected = cover == selectedCover
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(
                                            width = if (selected) 2.5.dp else 1.dp,
                                            color = if (selected) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.outline
                                            }
                                        ),
                                        modifier = Modifier
                                            .width(120.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { selectedCover = cover }
                                    ) {
                                        Column {
                                            Image(
                                                painter = painterResource(id = cover.drawableRes),
                                                contentDescription = cover.label,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(64.dp)
                                            )
                                            Text(
                                                text = cover.label,
                                                style = MaterialTheme.typography.labelSmall,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.padding(6.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ট্রেন্ডিং Top 10-এ যুক্ত করুন",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Switch(
                                    checked = isTrending,
                                    onCheckedChange = { isTrending = it }
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "প্রিমিয়াম প্রতিবেদন হিসেবে চিহ্নিত করুন",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Switch(
                                    checked = isPremiumOnly,
                                    onCheckedChange = { isPremiumOnly = it }
                                )
                            }

                            if (errorText != null) {
                                Text(
                                    text = errorText!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }

                            Button(
                                onClick = {
                                    when {
                                        title.isBlank() -> {
                                            errorText = "সংবাদের শিরোনাম আবশ্যক।"
                                        }
                                        content.isBlank() && summary.isBlank() -> {
                                            errorText = "সংক্ষিপ্ত বিবরণ বা বিস্তারিত সংবাদ লিখুন।"
                                        }
                                        else -> {
                                            val finalSummary = summary.ifBlank {
                                                content.take(120)
                                            }
                                            val finalContent = content.ifBlank { summary }
                                            onPublishArticle(
                                                title,
                                                finalSummary,
                                                finalContent,
                                                selectedCategory,
                                                location,
                                                selectedCover,
                                                isTrending,
                                                isPremiumOnly
                                            )
                                            title = ""
                                            summary = ""
                                            content = ""
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EmeraldSuccess,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("publish_article_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Publish,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "সংবাদ প্রকাশ করুন",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "প্রকাশিত সংবাদ পরিচালনা (${allArticles.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                items(allArticles, key = { it.id }) { article ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = article.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${article.category} • ${article.timeAgo} • ${article.location}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = { onDeleteArticle(article.id) },
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("delete_article_${article.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "মুছুন",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
