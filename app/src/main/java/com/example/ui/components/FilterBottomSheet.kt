package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlgeriaLocations
import com.example.ui.theme.DzTealPrimary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    sheetState: SheetState,
    selectedWilaya: String,
    selectedCommune: String,
    selectedCategory: String,
    selectedTransaction: String,
    favoritesOnly: Boolean,
    onWilayaChange: (String) -> Unit,
    onCommuneChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onTransactionChange: (String) -> Unit,
    onFavoritesOnlyChange: () -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    var isWilayaMenuExpanded by remember { mutableStateOf(false) }
    var isCommuneMenuExpanded by remember { mutableStateOf(false) }

    val categories = listOf("الجميع", "شقة", "فيلا", "أرض", "محل تجاري", "استوديو", "دوبلكس")
    val transactions = listOf("الجميع", "للبيع", "للكراء", "كراء يومي", "تبادل")

    val selectedWilayaObj = AlgeriaLocations.wilayas.find { it.nameAr == selectedWilaya || selectedWilaya.contains(it.nameAr) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "تصفية العقارات (الفلترة)",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = DzTealPrimary
                    )
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "إغلاق")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Wilaya Selector
            Text("تصفية حسب الولاية والبلدية:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.weight(1.2f)) {
                    OutlinedTextField(
                        value = if (selectedWilaya.isEmpty()) "جميع الولايات" else selectedWilaya,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("الولاية") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, "اختر الولاية") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isWilayaMenuExpanded = true }
                            .testTag("filter_wilaya_select"),
                        shape = RoundedCornerShape(12.dp)
                    )
                    DropdownMenu(
                        expanded = isWilayaMenuExpanded,
                        onDismissRequest = { isWilayaMenuExpanded = false },
                        modifier = Modifier.heightIn(max = 280.dp)
                    ) {
                        DropdownMenuItem(
                            text = { Text("جميع الولايات") },
                            onClick = {
                                onWilayaChange("")
                                isWilayaMenuExpanded = false
                            }
                        )
                        AlgeriaLocations.wilayas.forEach { w ->
                            DropdownMenuItem(
                                text = { Text(w.nameAr) },
                                onClick = {
                                    onWilayaChange(w.nameAr)
                                    isWilayaMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = if (selectedCommune.isEmpty()) "جميع البلديات" else selectedCommune,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("البلدية") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, "اختر البلدية") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { if (selectedWilayaObj != null) isCommuneMenuExpanded = true }
                            .testTag("filter_commune_select"),
                        shape = RoundedCornerShape(12.dp),
                        enabled = selectedWilayaObj != null
                    )
                    DropdownMenu(
                        expanded = isCommuneMenuExpanded,
                        onDismissRequest = { isCommuneMenuExpanded = false },
                        modifier = Modifier.heightIn(max = 280.dp)
                    ) {
                        DropdownMenuItem(
                            text = { Text("جميع البلديات") },
                            onClick = {
                                onCommuneChange("")
                                isCommuneMenuExpanded = false
                            }
                        )
                        selectedWilayaObj?.communesAr?.forEach { com ->
                            DropdownMenuItem(
                                text = { Text(com) },
                                onClick = {
                                    onCommuneChange(com)
                                    isCommuneMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category Chips
            Text("نوع العقار:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = (cat == "الجميع" && selectedCategory.isEmpty()) || selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategoryChange(if (cat == "الجميع") "" else cat) },
                        label = { Text(cat, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Transaction Type Chips
            Text("نوع المعاملة:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                transactions.forEach { trans ->
                    val isSelected = (trans == "الجميع" && selectedTransaction.isEmpty()) || selectedTransaction == trans
                    FilterChip(
                        selected = isSelected,
                        onClick = { onTransactionChange(if (trans == "الجميع") "" else trans) },
                        label = { Text(trans, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Favorites Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("إظهار المفضلة فقط ❤️", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Switch(
                    checked = favoritesOnly,
                    onCheckedChange = { onFavoritesOnlyChange() },
                    modifier = Modifier.testTag("filter_favorites_switch")
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier.weight(1f).testTag("filter_reset_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("إعادة ضبط")
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f).testTag("filter_apply_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DzTealPrimary)
                ) {
                    Text("تطبيق الفلترة")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
