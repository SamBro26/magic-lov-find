package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AlgeriaLocations
import com.example.ui.theme.DzGoldAccent
import com.example.ui.theme.DzTealLight
import com.example.ui.theme.DzTealPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateAdDialog(
    onDismiss: () -> Unit,
    onSubmit: (
        isOffer: Boolean,
        title: String,
        description: String,
        priceDzd: Long,
        priceCentimesDisplay: String,
        wilaya: String,
        commune: String,
        category: String,
        transactionType: String,
        rooms: String,
        area: Int,
        phone: String,
        publisherName: String,
        publisherType: String,
        featuresList: String
    ) -> Unit
) {
    var isOffer by remember { mutableStateOf(true) } // true = عرض عقار, false = طلب بحث (دبرلي)
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var selectedWilayaObj by remember { mutableStateOf(AlgeriaLocations.wilayas.find { it.id == 16 } ?: AlgeriaLocations.wilayas.first()) }
    var selectedCommune by remember { mutableStateOf(selectedWilayaObj.communesAr.firstOrNull() ?: "") }
    var isWilayaMenuExpanded by remember { mutableStateOf(false) }
    var isCommuneMenuExpanded by remember { mutableStateOf(false) }

    var selectedCategory by remember { mutableStateOf("شقة") }
    var selectedTransaction by remember { mutableStateOf("للبيع") }
    var selectedRooms by remember { mutableStateOf("F3") }
    var areaText by remember { mutableStateOf("100") }
    var phoneText by remember { mutableStateOf("0550 00 11 22") }
    var publisherNameText by remember { mutableStateOf("مستخدم دبرلي") }
    var publisherType by remember { mutableStateOf("مالك") }

    val categories = listOf("شقة", "فيلا", "أرض", "محل تجاري", "استوديو", "دوبلكس", "مكتب")
    val transactions = listOf("للبيع", "للكراء", "كراء يومي", "تبادل")
    val roomsOptions = listOf("F1", "F2", "F3", "F4", "F5+", "محل", "أرض")
    val publisherTypes = listOf("مالك", "وكالة عقارية", "مشتري / باحث عن كراء")

    val availableFeatures = listOf("عقد توثيقي", "دفتر عقاري", "غاز", "ماء", "كهرباء", "مصعد", "مرآب", "تدفئة مركزية", "مكيف", "حديقة", "مسبح")
    val selectedFeatures = remember { mutableStateListOf("غاز", "ماء", "كهرباء") }

    // Automatic Centimes conversion
    val convertedCentimesDisplay by remember {
        derivedStateOf {
            val priceVal = priceText.toLongOrNull() ?: 0L
            if (priceVal <= 0L) {
                if (selectedTransaction == "للكراء") "مثال: 4.5 مليون/شهر" else "مثال: 450 مليون سنتيم"
            } else {
                formatDzdToCentimes(priceVal, selectedTransaction)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 680.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Header with Close Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isOffer) "إضافة عقار معروض" else "نشر طلب بحث (دبرلي)",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DzTealPrimary
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Ad Type Segmented Control
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isOffer = true }
                                .testTag("tab_offer_ad"),
                            color = if (isOffer) DzTealPrimary else Color.Transparent,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "عرض عقار 🏠",
                                color = if (isOffer) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 10.dp),
                                fontSize = 14.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isOffer = false }
                                .testTag("tab_demand_ad"),
                            color = if (!isOffer) DzGoldAccent else Color.Transparent,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "طلب بحث (دبرلي) 🔍",
                                color = if (!isOffer) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 10.dp),
                                fontSize = 14.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (isOffer) "عنوان الإعلان (مثال: شقة F3 للكراء بباب الزوار)" else "عنوان الطلب (مثال: مطلوب شقة F3 للكراء بالشراقة)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ad_title_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Wilaya and Commune Selection
                Text("الموقع الجغرافي (الولاية والبلدية):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Wilaya Dropdown
                    Box(modifier = Modifier.weight(1.2f)) {
                        OutlinedTextField(
                            value = selectedWilayaObj.nameAr,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("الولاية") },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, "اختر الولاية") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isWilayaMenuExpanded = true }
                                .testTag("ad_wilaya_select"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        DropdownMenu(
                            expanded = isWilayaMenuExpanded,
                            onDismissRequest = { isWilayaMenuExpanded = false },
                            modifier = Modifier.heightIn(max = 280.dp)
                        ) {
                            AlgeriaLocations.wilayas.forEach { w ->
                                DropdownMenuItem(
                                    text = { Text(w.nameAr) },
                                    onClick = {
                                        selectedWilayaObj = w
                                        selectedCommune = w.communesAr.firstOrNull() ?: ""
                                        isWilayaMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Commune Dropdown
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = selectedCommune,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("البلدية") },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, "اختر البلدية") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isCommuneMenuExpanded = true }
                                .testTag("ad_commune_select"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        DropdownMenu(
                            expanded = isCommuneMenuExpanded,
                            onDismissRequest = { isCommuneMenuExpanded = false },
                            modifier = Modifier.heightIn(max = 280.dp)
                        ) {
                            selectedWilayaObj.communesAr.forEach { com ->
                                DropdownMenuItem(
                                    text = { Text(com) },
                                    onClick = {
                                        selectedCommune = com
                                        isCommuneMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Price Input with Automatic Centimes Converter
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it.filter { char -> char.isDigit() } },
                    label = { Text(if (isOffer) "السعر بالدينار الجزائري (د.ج)" else "الميزانية القصوى بالدينار (د.ج)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ad_price_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Display converted centimes
                Surface(
                    color = DzTealLight,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "💰 المعادلة بالسنتيم: $convertedCentimesDisplay",
                        color = DzTealPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Category Chips
                Text("نوع العقار:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 12.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Transaction Type
                Text("نوع المعاملة:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    transactions.forEach { trans ->
                        FilterChip(
                            selected = selectedTransaction == trans,
                            onClick = { selectedTransaction = trans },
                            label = { Text(trans, fontSize = 12.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Rooms & Area
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = areaText,
                        onValueChange = { areaText = it.filter { char -> char.isDigit() } },
                        label = { Text("المساحة (م²)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ad_area_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = phoneText,
                        onValueChange = { phoneText = it },
                        label = { Text("رقم الهاتف") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("ad_phone_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("وصف تفصيلي") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .testTag("ad_desc_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Features Checkboxes
                Text("المميزات والوثائق المتوفرة:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    availableFeatures.forEach { feature ->
                        val isChecked = selectedFeatures.contains(feature)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable {
                                    if (isChecked) selectedFeatures.remove(feature) else selectedFeatures.add(feature)
                                }
                                .padding(vertical = 2.dp)
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = {
                                    if (it) selectedFeatures.add(feature) else selectedFeatures.remove(feature)
                                }
                            )
                            Text(feature, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Submit Button
                Button(
                    onClick = {
                        val finalPrice = priceText.toLongOrNull() ?: 0L
                        val finalArea = areaText.toIntOrNull() ?: 100
                        val finalTitle = title.ifBlank {
                            if (isOffer) "عقار $selectedCategory $selectedTransaction بـ $selectedCommune"
                            else "طلب $selectedCategory $selectedTransaction بـ $selectedCommune"
                        }
                        val finalDesc = description.ifBlank { "لا يوجد وصف تفصيلي." }
                        val centimesText = formatDzdToCentimes(finalPrice, selectedTransaction)

                        onSubmit(
                            isOffer,
                            finalTitle,
                            finalDesc,
                            finalPrice,
                            centimesText,
                            selectedWilayaObj.nameAr,
                            selectedCommune,
                            selectedCategory,
                            selectedTransaction,
                            selectedRooms,
                            finalArea,
                            phoneText,
                            publisherNameText,
                            publisherType,
                            selectedFeatures.joinToString(",")
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_ad_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DzTealPrimary)
                ) {
                    Text(
                        text = if (isOffer) "نشر الإعلان 🚀" else "نشر طلب البحث (دبرلي) 🚀",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

fun formatDzdToCentimes(priceDzd: Long, transactionType: String): String {
    if (priceDzd <= 0) return "حسب الاتفاق"
    val centimes = priceDzd * 100
    val suffix = if (transactionType == "للكراء") "/شهر" else if (transactionType == "كراء يومي") "/ليلة" else ""

    return when {
        centimes >= 100_000_000 -> {
            val milyar = centimes.toDouble() / 100_000_000.0
            "%.2f مليار سنتيم$suffix".replace(".00", "")
        }
        centimes >= 1_000_000 -> {
            val milyon = centimes.toDouble() / 1_000_000.0
            "%.1f مليون سنتيم$suffix".replace(".0", "")
        }
        else -> {
            val alf = centimes / 1_000
            "$alf ألف سنتيم$suffix"
        }
    }
}
