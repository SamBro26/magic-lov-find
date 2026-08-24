package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.PropertyItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val senderName: String,
    val text: String,
    val isFromUser: Boolean,
    val timestamp: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PropertyDetailModal(
    property: PropertyItem,
    onDismiss: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    val context = LocalContext.current
    var isChatOpen by remember { mutableStateOf(false) }
    
    // Rating State
    var userRating by remember { mutableIntStateOf(0) }
    var ratingSubmittedToast by remember { mutableStateOf(false) }

    // Dark Palette Colors
    val darkBackground = Color(0xFF141722)
    val darkCardBg = Color(0xFF1E2232)
    val darkCardBorder = Color(0xFF2C3246)
    val darkTextPrimary = Color(0xFFF1F5F9)
    val darkTextSecondary = Color(0xFF94A3B8)
    val accentBlue = Color(0xFF0080FF)
    val goldColor = Color(0xFFFFC107)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.96f)
                    .heightIn(max = 720.dp)
                    .padding(8.dp),
                shape = RoundedCornerShape(24.dp),
                color = darkBackground,
                tonalElevation = 12.dp
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // MAIN CONTENT COLUMN WITH FIXED BOTTOM BAR
                    Column(modifier = Modifier.fillMaxSize()) {
                        // SCROLLABLE DETAILS CONTENT
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            // Header with Badges & Close
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = if (property.isOffer) Color(0xFF004D40) else Color(0xFF5D4037),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (property.isOffer) "عرض عقار معروض 🏠" else "طلب بحث - دبرلي 🔍",
                                        color = if (property.isOffer) Color(0xFF80CBC4) else Color(0xFFFFCC80),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = onFavoriteToggle) {
                                        Icon(
                                            imageVector = if (property.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "مفضلة",
                                            tint = if (property.isFavorite) Color(0xFFFF5252) else darkTextSecondary
                                        )
                                    }
                                    IconButton(onClick = onDismiss) {
                                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = darkTextPrimary)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Title
                            Text(
                                text = property.title,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    lineHeight = 28.sp
                                ),
                                color = darkTextPrimary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Location Banner
                            Surface(
                                color = darkCardBg,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, darkCardBorder, RoundedCornerShape(12.dp))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "الموقع",
                                        tint = accentBlue,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "الموقع الجغرافي بالجزائر 🇩🇿",
                                            fontSize = 11.sp,
                                            color = darkTextSecondary
                                        )
                                        Text(
                                            text = "${property.wilaya} • ${property.commune}",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = darkTextPrimary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Price Card
                            Surface(
                                color = darkCardBg,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, goldColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = if (property.isOffer) "السعر المطلـوب 💰" else "الميزانية المخصصة 💰",
                                        fontSize = 12.sp,
                                        color = goldColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = property.priceDisplayCentimes,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = goldColor
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "المبلغ بالدفع القانوني: %,d د.ج (دينار جزائري)".format(property.priceDzd),
                                        fontSize = 13.sp,
                                        color = darkTextSecondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Rating Stars Card
                            Surface(
                                color = darkCardBg,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, darkCardBorder, RoundedCornerShape(14.dp))
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "تقييم العقار والناشر 🌟",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = darkTextPrimary
                                            )
                                            Text(
                                                text = "${property.rating} من 5.0 (${property.reviewsCount} تقييم مراجعة)",
                                                fontSize = 12.sp,
                                                color = darkTextSecondary
                                            )
                                        }

                                        // Display stars
                                        Row {
                                            repeat(5) { index ->
                                                Icon(
                                                    imageVector = if (index < property.rating.toInt()) Icons.Filled.Star else Icons.Outlined.Star,
                                                    contentDescription = null,
                                                    tint = goldColor,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // User Interactive Rating Prompt
                                    Text(
                                        text = "أضف تقييمك لهذا العقار:",
                                        fontSize = 12.sp,
                                        color = darkTextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        repeat(5) { starIndex ->
                                            val starValue = starIndex + 1
                                            IconButton(
                                                onClick = {
                                                    userRating = starValue
                                                    ratingSubmittedToast = true
                                                },
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .testTag("rate_star_$starValue")
                                            ) {
                                                Icon(
                                                    imageVector = if (starValue <= userRating) Icons.Filled.Star else Icons.Outlined.Star,
                                                    contentDescription = "تقييم $starValue نجوم",
                                                    tint = if (starValue <= userRating) goldColor else darkTextSecondary,
                                                    modifier = Modifier.size(26.dp)
                                                )
                                            }
                                        }

                                        if (ratingSubmittedToast) {
                                            Text(
                                                text = "شكراً لتقييمك! ($userRating/5) ⭐",
                                                color = goldColor,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(start = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Specs Summary Grid
                            Text("مواصفات العقار الرئيسية:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = darkTextPrimary)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                DarkSpecBox(title = "نوع العقار", value = property.category, modifier = Modifier.weight(1f))
                                DarkSpecBox(title = "نوع المعاملة", value = property.transactionType, modifier = Modifier.weight(1f))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                DarkSpecBox(title = "الغرف / التقسيم", value = property.rooms, modifier = Modifier.weight(1f))
                                DarkSpecBox(title = "المساحة الإجمالية", value = "${property.areaSqMeters} م²", modifier = Modifier.weight(1f))
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Description
                            Text("تفاصيل الوصف:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = darkTextPrimary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = property.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = darkTextPrimary.copy(alpha = 0.9f),
                                lineHeight = 22.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Features
                            if (property.featuresList.isNotBlank()) {
                                Text("الوثائق والخدمات المتوفرة:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = darkTextPrimary)
                                Spacer(modifier = Modifier.height(8.dp))

                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    property.featuresList.split(",").forEach { feature ->
                                        if (feature.isNotBlank()) {
                                            Surface(
                                                color = darkCardBg,
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.border(1.dp, darkCardBorder, RoundedCornerShape(8.dp))
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = accentBlue,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(feature.trim(), fontSize = 12.sp, color = darkTextPrimary, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Publisher Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = darkCardBg),
                                border = androidx.compose.foundation.BorderStroke(1.dp, darkCardBorder),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(accentBlue, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = property.publisherName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = darkTextPrimary
                                        )
                                        Text(
                                            text = "صفة الناشر: ${property.publisherType}",
                                            fontSize = 12.sp,
                                            color = darkTextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        // FIXED BOTTOM NAVIGATION ACTION BAR
                        Surface(
                            color = Color(0xFF1A1D2B),
                            tonalElevation = 10.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = darkCardBorder,
                                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                // Primary Blue Button: "إرسال رسالة داخلية"
                                Button(
                                    onClick = { isChatOpen = true },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("detail_contact_chat_button"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = accentBlue)
                                ) {
                                    Icon(Icons.Default.Message, contentDescription = "رسالة داخلية", tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "إرسال رسالة داخلية 💬",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Secondary Quick Options (Call & WhatsApp)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                                data = Uri.parse("tel:${property.phone}")
                                            }
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp)
                                            .testTag("detail_call_button"),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = "اتصال", tint = darkTextPrimary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("اتصال هاتف 📞", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = darkTextPrimary)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            val cleanPhone = property.phone.replace(" ", "")
                                            val whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
                                                data = Uri.parse("https://api.whatsapp.com/send?phone=+213${cleanPhone.removePrefix("0")}&text=السلام عليكم، بخصوص إعلانكم على FindMeDZ: ${property.title}")
                                            }
                                            context.startActivity(whatsappIntent)
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp)
                                            .testTag("detail_whatsapp_button"),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("واتساب 💬", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = darkTextPrimary)
                                    }
                                }
                            }
                        }
                    }

                    // MINI IN-APP CHAT OVERLAY BOX
                    AnimatedVisibility(
                        visible = isChatOpen,
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                    ) {
                        MiniInAppChatBox(
                            property = property,
                            onClose = { isChatOpen = false }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MiniInAppChatBox(
    property: PropertyItem,
    onClose: () -> Unit
) {
    var messageInput by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val chatMessages = remember {
        mutableStateListOf(
            ChatMessage(
                senderName = property.publisherName,
                text = "السلام عليكم ورحمة الله 👋 أهلاً وسهلاً بك! بخصوص \"${property.title}\"، العقار ما يزال متوفراً. كيف يمكنني مساعدتك؟",
                isFromUser = false,
                timestamp = "الآن"
            )
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        color = Color(0xFF10131E),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            // Chat Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E2232), RoundedCornerShape(16.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF0080FF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = property.publisherName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Text(
                            text = "🟢 متصل الآن • ${property.publisherType}",
                            fontSize = 11.sp,
                            color = Color(0xFF80CBC4)
                        )
                    }
                }

                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "إغلاق الدردشة", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Property Preview Banner in Chat
            Surface(
                color = Color(0xFF1E2232).copy(alpha = 0.6f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "محادثات بخصوص: ${property.title} • (${property.priceDisplayCentimes})",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(chatMessages, key = { it.id }) { msg ->
                    ChatBubbleItem(message = msg)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Input Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = messageInput,
                    onValueChange = { messageInput = it },
                    placeholder = { Text("اكتب رسالتك للمعلن...", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1E2232),
                        unfocusedContainerColor = Color(0xFF1E2232),
                        focusedBorderColor = Color(0xFF0080FF),
                        unfocusedBorderColor = Color(0xFF2C3246),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field")
                )

                IconButton(
                    onClick = {
                        if (messageInput.isNotBlank()) {
                            val userText = messageInput.trim()
                            chatMessages.add(
                                ChatMessage(
                                    senderName = "أنت",
                                    text = userText,
                                    isFromUser = true,
                                    timestamp = "الآن"
                                )
                            )
                            messageInput = ""

                            coroutineScope.launch {
                                listState.animateScrollToItem(chatMessages.size - 1)
                                delay(1200) // Simulate advertiser typing
                                
                                val responseText = when {
                                    userText.contains("سعر") || userText.contains("سوم") || userText.contains("تخفيض") ->
                                        "السعر المذكور قابل للتفاوض البسيط جداً عند المعاينة الميدانية 👍"
                                    userText.contains("موقع") || userText.contains("مكان") || userText.contains("عنوان") ->
                                        "العقار يقع بالضبط في ${property.commune} بـ ${property.wilaya} في حي هادئ ومخدم بكل المرافق."
                                    userText.contains("زيارة") || userText.contains("معاينة") || userText.contains("موعد") ->
                                        "مرحباً بك لمعاينة العقار! يرجى الاتصال بي هاتفياً على الرقم ${property.phone} لتحديد الوقت المناسب."
                                    else ->
                                        "شكراً لتواصلك الجاد! تم استلام رسالتك وسأقوم بالرد التفصيلي عليك في أقرب وقت. يمكنك أيضاً الاتصال بي مباشره عبر ${property.phone}."
                                }

                                chatMessages.add(
                                    ChatMessage(
                                        senderName = property.publisherName,
                                        text = responseText,
                                        isFromUser = false,
                                        timestamp = "الآن"
                                    )
                                )
                                listState.animateScrollToItem(chatMessages.size - 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF0080FF), CircleShape)
                        .testTag("chat_send_button")
                ) {
                    Icon(Icons.Default.Send, contentDescription = "إرسال", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun ChatBubbleItem(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (message.isFromUser) Color(0xFF0080FF) else Color(0xFF1E2232),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isFromUser) 16.dp else 4.dp,
                bottomEnd = if (message.isFromUser) 4.dp else 16.dp
            ),
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .border(
                    width = 1.dp,
                    color = if (message.isFromUser) Color.Transparent else Color(0xFF2C3246),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = message.text,
                    fontSize = 14.sp,
                    color = Color.White,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.timestamp,
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    if (message.isFromUser) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "✓✓",
                            fontSize = 11.sp,
                            color = Color(0xFF80CBC4),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DarkSpecBox(title: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        color = Color(0xFF1E2232),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.border(1.dp, Color(0xFF2C3246), RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(title, fontSize = 11.sp, color = Color(0xFF94A3B8))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF1F5F9))
        }
    }
}
