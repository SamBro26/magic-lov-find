package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AlgeriaLocations
import com.example.ui.components.CreateAdDialog
import com.example.ui.components.FilterBottomSheet
import com.example.ui.components.PropertyCard
import com.example.ui.components.PropertyDetailModal
import com.example.ui.theme.DzGoldAccent
import com.example.ui.theme.DzGoldLight
import com.example.ui.theme.DzTealDark
import com.example.ui.theme.DzTealLight
import com.example.ui.theme.DzTealPrimary
import com.example.ui.theme.DzTextMuted
import com.example.ui.theme.DzTextSecondary
import com.example.ui.viewmodel.PropertyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: PropertyViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val properties by viewModel.propertyList.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState()

    var isSearchWilayaExpanded by remember { mutableStateOf(false) }
    var isSearchCommuneExpanded by remember { mutableStateOf(false) }

    val currentSelectedWilayaObj = AlgeriaLocations.wilayas.find { 
        it.nameAr == uiState.selectedWilaya || uiState.selectedWilaya.contains(it.nameAr) || it.nameAr.contains(uiState.selectedWilaya)
    }

    val activeFilterCount = listOf(
        uiState.selectedWilaya.isNotBlank(),
        uiState.selectedCommune.isNotBlank(),
        uiState.selectedCategory.isNotBlank(),
        uiState.selectedTransaction.isNotBlank(),
        uiState.favoritesOnly
    ).count { it }

    // Enforce Arabic RTL layout
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "FindMeDZ",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = DzGoldAccent,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "دبرلي",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "تطبيق العقارات الأول في الجزائر 🇩🇿",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.toggleFavoritesOnly() },
                            modifier = Modifier.testTag("top_favorites_toggle")
                        ) {
                            Icon(
                                imageVector = if (uiState.favoritesOnly) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "المفضلة",
                                tint = if (uiState.favoritesOnly) Color.Red else Color.White
                            )
                        }

                        IconButton(
                            onClick = { viewModel.setFilterSheetOpen(true) },
                            modifier = Modifier.testTag("top_filter_button")
                        ) {
                            if (activeFilterCount > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge(containerColor = DzGoldAccent) {
                                            Text("$activeFilterCount", color = Color.White)
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.FilterList, contentDescription = "الفلترة", tint = Color.White)
                                }
                            } else {
                                Icon(Icons.Default.FilterList, contentDescription = "الفلترة", tint = Color.White)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DzTealPrimary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.setCreateAdDialogOpen(true) },
                    containerColor = DzTealLight,
                    contentColor = DzTealDark,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .size(56.dp)
                        .testTag("fab_create_ad")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "إضافة إعلان جديد",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Search Bar Container with Linked Location Dropdowns
                Surface(
                    color = DzTealPrimary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("ابحث بالعنوان، الكلمات المفتاحية...", fontSize = 13.sp, color = DzTextMuted) },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "بحث", tint = DzTealPrimary)
                            },
                            trailingIcon = {
                                if (uiState.searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = "مسح", tint = DzTextMuted)
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = DzGoldAccent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("search_bar_input")
                        )

                        // Linked Location Dropdowns (Wilaya & Commune)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // 1st Dropdown: Wilayas (All 69 Algerian Wilayas)
                            Box(modifier = Modifier.weight(1.1f)) {
                                OutlinedTextField(
                                    value = if (uiState.selectedWilaya.isEmpty()) "جميع الولايات (69)" else uiState.selectedWilaya,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("الولاية 📍", fontSize = 11.sp, color = Color.White) },
                                    trailingIcon = {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = "اختر الولاية", tint = DzTealPrimary)
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        focusedBorderColor = DzGoldAccent,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedLabelColor = Color.White,
                                        unfocusedLabelColor = Color.White.copy(alpha = 0.9f)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("search_wilaya_dropdown")
                                )
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable { isSearchWilayaExpanded = true }
                                )
                                DropdownMenu(
                                    expanded = isSearchWilayaExpanded,
                                    onDismissRequest = { isSearchWilayaExpanded = false },
                                    modifier = Modifier
                                        .fillMaxWidth(0.85f)
                                        .heightIn(max = 320.dp)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("جميع الولايات 🇩🇿", fontWeight = FontWeight.Bold, color = DzTealPrimary) },
                                        onClick = {
                                            viewModel.setSelectedWilaya("")
                                            viewModel.setSelectedCommune("")
                                            isSearchWilayaExpanded = false
                                        }
                                    )
                                    AlgeriaLocations.wilayas.forEach { w ->
                                        DropdownMenuItem(
                                            text = { Text(w.nameAr) },
                                            onClick = {
                                                viewModel.setSelectedWilaya(w.nameAr)
                                                viewModel.setSelectedCommune("") // Reset commune when wilaya changes
                                                isSearchWilayaExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // 2nd Dropdown: Communes (Linked to selected Wilaya)
                            Box(modifier = Modifier.weight(1f)) {
                                val isCommuneEnabled = currentSelectedWilayaObj != null && uiState.selectedWilaya.isNotEmpty()
                                OutlinedTextField(
                                    value = when {
                                        uiState.selectedWilaya.isEmpty() -> "اختر ولاية أولاً"
                                        uiState.selectedCommune.isEmpty() -> "جميع البلديات"
                                        else -> uiState.selectedCommune
                                    },
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("البلدية 🏛️", fontSize = 11.sp, color = Color.White) },
                                    trailingIcon = {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = "اختر البلدية", tint = if (isCommuneEnabled) DzTealPrimary else Color.Gray)
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = if (isCommuneEnabled) Color.White else Color(0xFFEFEFEF),
                                        unfocusedContainerColor = if (isCommuneEnabled) Color.White else Color(0xFFEFEFEF),
                                        focusedBorderColor = DzGoldAccent,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedLabelColor = Color.White,
                                        unfocusedLabelColor = Color.White.copy(alpha = 0.9f)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("search_commune_dropdown")
                                )
                                if (isCommuneEnabled) {
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .clickable { isSearchCommuneExpanded = true }
                                    )
                                    DropdownMenu(
                                        expanded = isSearchCommuneExpanded,
                                        onDismissRequest = { isSearchCommuneExpanded = false },
                                        modifier = Modifier
                                            .fillMaxWidth(0.85f)
                                            .heightIn(max = 300.dp)
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("جميع البلديات", fontWeight = FontWeight.Bold, color = DzTealPrimary) },
                                            onClick = {
                                                viewModel.setSelectedCommune("")
                                                isSearchCommuneExpanded = false
                                            }
                                        )
                                        currentSelectedWilayaObj?.communesAr?.forEach { communeName ->
                                            DropdownMenuItem(
                                                text = { Text(communeName) },
                                                onClick = {
                                                    viewModel.setSelectedCommune(communeName)
                                                    isSearchCommuneExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Active Filters Row (if any active)
                AnimatedVisibility(visible = activeFilterCount > 0) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (uiState.selectedWilaya.isNotEmpty()) {
                            item {
                                InputChip(
                                    selected = true,
                                    onClick = { viewModel.setSelectedWilaya("") },
                                    label = { Text("الولاية: ${uiState.selectedWilaya}", fontSize = 11.sp) },
                                    trailingIcon = { Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                )
                            }
                        }
                        if (uiState.selectedCommune.isNotEmpty()) {
                            item {
                                InputChip(
                                    selected = true,
                                    onClick = { viewModel.setSelectedCommune("") },
                                    label = { Text("البلدية: ${uiState.selectedCommune}", fontSize = 11.sp) },
                                    trailingIcon = { Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                )
                            }
                        }
                        if (uiState.selectedCategory.isNotEmpty()) {
                            item {
                                InputChip(
                                    selected = true,
                                    onClick = { viewModel.setSelectedCategory("") },
                                    label = { Text("النوع: ${uiState.selectedCategory}", fontSize = 11.sp) },
                                    trailingIcon = { Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                )
                            }
                        }
                        if (uiState.selectedTransaction.isNotEmpty()) {
                            item {
                                InputChip(
                                    selected = true,
                                    onClick = { viewModel.setSelectedTransaction("") },
                                    label = { Text("المعاملة: ${uiState.selectedTransaction}", fontSize = 11.sp) },
                                    trailingIcon = { Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                )
                            }
                        }
                        item {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.clickable { viewModel.resetFilters() }
                            ) {
                                Text(
                                    text = "إلغاء الفلاتر ✖",
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // TabBar: Tab 1 "عقارات معروضة", Tab 2 "طلبات البحث"
                TabRow(
                    selectedTabIndex = uiState.activeTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = DzTealPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[uiState.activeTabIndex]),
                            color = DzTealPrimary,
                            height = 3.dp
                        )
                    },
                    modifier = Modifier.testTag("main_tab_row")
                ) {
                    Tab(
                        selected = uiState.activeTabIndex == 0,
                        onClick = { viewModel.setTabIndex(0) },
                        modifier = Modifier.testTag("tab_offers")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Text(
                                text = "عقارات معروضة 🏠",
                                fontWeight = if (uiState.activeTabIndex == 0) FontWeight.ExtraBold else FontWeight.Normal,
                                fontSize = 15.sp,
                                color = if (uiState.activeTabIndex == 0) DzTealPrimary else DzTextSecondary
                            )
                        }
                    }

                    Tab(
                        selected = uiState.activeTabIndex == 1,
                        onClick = { viewModel.setTabIndex(1) },
                        modifier = Modifier.testTag("tab_demands")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Text(
                                text = "طلبات البحث (دبرلي) 🔍",
                                fontWeight = if (uiState.activeTabIndex == 1) FontWeight.ExtraBold else FontWeight.Normal,
                                fontSize = 15.sp,
                                color = if (uiState.activeTabIndex == 1) DzGoldAccent else DzTextSecondary
                            )
                        }
                    }
                }

                // Property List
                if (properties.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = DzTextMuted,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (uiState.activeTabIndex == 0) "لا توجد عقارات معروضة تطابق بحثك حالياً." else "لا توجد طلبات بحث تطابق معاييرك.",
                                style = MaterialTheme.typography.titleMedium,
                                color = DzTextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "انقر على زر (+) في الأسفل لإضافة إعلان أو طلب جديد!",
                                style = MaterialTheme.typography.bodySmall,
                                color = DzTextMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        items(
                            items = properties,
                            key = { it.id }
                        ) { item ->
                            PropertyCard(
                                property = item,
                                onCardClick = { viewModel.selectPropertyForDetails(item) },
                                onFavoriteToggle = { viewModel.toggleFavorite(item) }
                            )
                        }
                    }
                }
            }

            // Bottom Sheets & Dialogs
            if (uiState.isFilterSheetOpen) {
                FilterBottomSheet(
                    sheetState = sheetState,
                    selectedWilaya = uiState.selectedWilaya,
                    selectedCommune = uiState.selectedCommune,
                    selectedCategory = uiState.selectedCategory,
                    selectedTransaction = uiState.selectedTransaction,
                    favoritesOnly = uiState.favoritesOnly,
                    onWilayaChange = { viewModel.setSelectedWilaya(it) },
                    onCommuneChange = { viewModel.setSelectedCommune(it) },
                    onCategoryChange = { viewModel.setSelectedCategory(it) },
                    onTransactionChange = { viewModel.setSelectedTransaction(it) },
                    onFavoritesOnlyChange = { viewModel.toggleFavoritesOnly() },
                    onReset = { viewModel.resetFilters() },
                    onDismiss = { viewModel.setFilterSheetOpen(false) }
                )
            }

            if (uiState.isCreateAdDialogOpen) {
                CreateAdDialog(
                    onDismiss = { viewModel.setCreateAdDialogOpen(false) },
                    onSubmit = { isOffer, title, desc, price, centimes, wilaya, commune, cat, trans, rooms, area, phone, pubName, pubType, features ->
                        viewModel.createProperty(
                            isOffer = isOffer,
                            title = title,
                            description = desc,
                            priceDzd = price,
                            priceCentimesDisplay = centimes,
                            wilaya = wilaya,
                            commune = commune,
                            category = cat,
                            transactionType = trans,
                            rooms = rooms,
                            area = area,
                            phone = phone,
                            publisherName = pubName,
                            publisherType = pubType,
                            featuresList = features
                        )
                    }
                )
            }

            uiState.selectedPropertyForDetails?.let { property ->
                PropertyDetailModal(
                    property = property,
                    onDismiss = { viewModel.selectPropertyForDetails(null) },
                    onFavoriteToggle = { viewModel.toggleFavorite(property) }
                )
            }
        }
    }
}
