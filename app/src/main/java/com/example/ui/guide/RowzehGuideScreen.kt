package com.example.ui.guide

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.ui.components.ShamsehDivider
import com.example.ui.theme.AlertCrimson
import com.example.ui.theme.GoldDark
import com.example.ui.theme.SpiritualIvory
import com.example.ui.theme.TurquoiseLight
import com.example.ui.theme.TurquoisePrimary

/**
 * Guide feature item definition
 */
data class GuideSection(
    val title: String,
    val icon: ImageVector,
    val iconColor: Color,
    val bulletPoints: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RowzehGuideScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    var showFullScreenPoster by remember { mutableStateOf(false) }

    val guideSections = listOf(
        GuideSection(
            title = "طراحی سنتی و مینیمال",
            icon = Icons.Default.Palette,
            iconColor = TurquoisePrimary,
            bulletPoints = listOf(
                "ترکیب رنگ اصیل فیروزه‌ای و طلایی ایرانی",
                "نقوش هندسی شمسه و حاشیه‌های آرامش‌بخش",
                "پس‌زمینه عاجی سازگار با چشم و باوقار",
                "رابط کاربری راست‌به‌چپ (RTL) متناسب با زبان فارسی",
                "آیکون لانچر اختصاصی با نماد ساعت و ضریح مطهر"
            )
        ),
        GuideSection(
            title = "پخش تصادفی در بازه زمانی",
            icon = Icons.Default.AccessTime,
            iconColor = GoldDark,
            bulletPoints = listOf(
                "انتخاب بازه دلخواه (ساعت شروع و پایان)",
                "زمان‌بندی هوشمند دقیق بدون اختلال در مصرف باتری",
                "پخش تصادفی و غافلگیرکننده در طول روز جهت پیوند قلبی با اهل‌بیت (ع)"
            )
        ),
        GuideSection(
            title = "هشدار پیش از پخش",
            icon = Icons.Default.NotificationsActive,
            iconColor = AlertCrimson,
            bulletPoints = listOf(
                "اعلان اولویت‌بالا همراه با لرزش و آوای هشدار",
                "دیالوگ هشدار درون‌برنامه‌ای با شمارش معکوس آمادگی دل",
                "دکمه‌های انتخابی «پخش روضه» یا «انصراف» در شرایط نامناسب",
                "دکمه تست سریع جهت شنیدن آنی روضه یا تست کارکرد"
            )
        ),
        GuideSection(
            title = "تکرار و میزان صدا",
            icon = Icons.Default.Repeat,
            iconColor = TurquoisePrimary,
            bulletPoints = listOf(
                "تکرار روزانه یا هفتگی طبق برنامه مدنظر شما",
                "امکان انتخاب تک‌تک روزهای دلخواه هفته",
                "اسلایدر تنظیم دقیق بلندی صدای پخش (از ۵٪ تا ۱۰۰٪)",
                "اعمال مستقیم و بدون تاخیر روی پخش‌کننده روضه"
            )
        ),
        GuideSection(
            title = "مدیریت فهرست روضه‌ها",
            icon = Icons.Default.QueueMusic,
            iconColor = GoldDark,
            bulletPoints = listOf(
                "فهرست اختصاصی صوتی با چک‌باکس فعال/غیرفعال برای هر اثر",
                "امکان گزینش روضه‌های مورد علاقه جهت شرکت در قرعه‌کشی پخش",
                "امکان حذف یا مدیریت آسان فایل‌ها با یک لمس"
            )
        ),
        GuideSection(
            title = "ضبط صدای روضه",
            icon = Icons.Default.Mic,
            iconColor = AlertCrimson,
            bulletPoints = listOf(
                "ضبط مستقیم نوای دلخواه با میکروفون باکیفیت گوشی",
                "نمایش تایمر زنده و طول مدت ضبط",
                "امکان نام‌گذاری و ثبت نام مداح برای هر صوت ضبط‌شده"
            )
        ),
        GuideSection(
            title = "بارگذاری فایل صوتی",
            icon = Icons.Default.UploadFile,
            iconColor = TurquoisePrimary,
            bulletPoints = listOf(
                "افزودن نامحدود فایل‌های صوتی دلخواه از حافظه گوشی",
                "پشتیبانی از انواع فرمت‌های رایج: MP3, M4A, WAV, OGG",
                "کپی امن در حافظه داخلی اختصاصی نرم‌افزار"
            )
        ),
        GuideSection(
            title = "ویجت صفحه اصلی (Home Widget)",
            icon = Icons.Default.Widgets,
            iconColor = GoldDark,
            bulletPoints = listOf(
                "نمایش وضعیت فعال یا غیرفعال بودن برنامه روی دسکتاپ",
                "نمایش بازه زمانی مشخص‌شده و عنوان فایل در حال پخش",
                "کنترل سریع روشن/خاموش کردن برنامه بدون ورود به اپ",
                "دکمه پخش یا توقف فوری روضه با یک لمس"
            )
        ),
        GuideSection(
            title = "تنظیمات اختصاصی و چندگانه",
            icon = Icons.Default.Tune,
            iconColor = TurquoisePrimary,
            bulletPoints = listOf(
                "مدیریت چندین بازه زمانی دلخواه در طول روز (صبح، ظهر، غروب، شب)",
                "الگوهای پیش‌فرض کاربردی با قابلیت فعال‌سازی با یک لمس",
                "امکان افزودن، ویرایش و حذف بازه‌های دلخواه",
                "ذخیره‌سازی مطمئن و دائمی در پایگاه‌داده محلی"
            )
        )
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "راهنما و پوستر ساعت روضه",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.testTag("btn_guide_back")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "بازگشت",
                                tint = TurquoisePrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .testTag("guide_scroll_column"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Poster Card with Full Screen Preview Trigger
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("card_poster_preview"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.5.dp, TurquoisePrimary.copy(alpha = 0.5f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Fullscreen,
                                        contentDescription = null,
                                        tint = TurquoisePrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "پوستر معرفی و راهنمای جامع",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = GoldDark.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "راهنمای تصویری",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = GoldDark,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Clickable Poster Thumbnail
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(260.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.Black.copy(alpha = 0.05f))
                                    .clickable { showFullScreenPoster = true }
                                    .testTag("poster_thumbnail_click"),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_rowzeh_guide_poster),
                                    contentDescription = "پوستر راهنمای ساعت روضه",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                // Overlay with Zoom hint
                                Surface(
                                    shape = RoundedCornerShape(24.dp),
                                    color = Color.Black.copy(alpha = 0.65f),
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ZoomIn,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "برای بزرگ‌نمایی و مشاهده کامل لمس کنید",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { showFullScreenPoster = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_open_fullscreen_poster"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TurquoisePrimary)
                            ) {
                                Icon(Icons.Default.Fullscreen, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("مشاهده تمام‌صفحه پوستر راهنما", fontSize = 13.sp)
                            }
                        }
                    }
                }

                // 2. Spiritual Quote Card from Poster
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = TurquoiseLight.copy(alpha = 0.25f)
                        ),
                        border = BorderStroke(1.dp, TurquoisePrimary.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "ساعت روضه",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = TurquoisePrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "روزی چند دقیقه، دلمان را زنده نگه داریم...",
                                fontSize = 14.sp,
                                color = GoldDark,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "اپلیکیشن «ساعت روضه» با طراحی سنتی و اصیل به شما کمک می‌کند تا در طول روز، در زمان‌های دلخواه، به صورت خودکار و غافلگیرکننده روضه‌های اهل‌بیت (ع) را بشنوید و دلتان را با یادشان صفا دهید.",
                                fontSize = 12.5.sp,
                                lineHeight = 22.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            ShamsehDivider(modifier = Modifier.padding(vertical = 10.dp))

                            // 4 Pillars Row from bottom of poster
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PillarBadge(icon = Icons.Default.Headphones, text = "شنیدن روضه")
                                PillarBadge(icon = Icons.Default.Favorite, text = "آرامش دل")
                                PillarBadge(icon = Icons.Default.Star, text = "یاد اهل‌بیت")
                                PillarBadge(icon = Icons.Default.AccessTime, text = "هر زمان و مکان")
                            }
                        }
                    }
                }

                // 3. Section Header
                item {
                    Text(
                        text = "راهنمای قابلیت‌ها و امکانات برنامه",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // 4. Feature Cards
                items(guideSections.size) { index ->
                    val section = guideSections[index]
                    GuideSectionCard(section = section, index = index + 1)
                }

                // 5. Final Prayer & Slogan from poster
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "«اللهم عجل لولیک الفرج»",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = TurquoisePrimary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "ساعت روضه — همراه همیشگی دل‌های عاشق",
                                fontSize = 13.sp,
                                color = GoldDark,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

    // Full Screen Zoomable Poster Dialog
    if (showFullScreenPoster) {
        FullScreenPosterDialog(
            onDismiss = { showFullScreenPoster = false }
        )
    }
}

@Composable
private fun PillarBadge(
    icon: ImageVector,
    text: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = TurquoisePrimary.copy(alpha = 0.15f),
            modifier = Modifier.size(38.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TurquoisePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun GuideSectionCard(
    section: GuideSection,
    index: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("guide_card_$index"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = section.iconColor.copy(alpha = 0.12f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = section.icon,
                            contentDescription = null,
                            tint = section.iconColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = section.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            section.bulletPoints.forEach { point ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "•",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = section.iconColor,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Text(
                        text = point,
                        fontSize = 12.5.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Interactive full-screen zoomable poster viewer dialog
 */
@Composable
fun FullScreenPosterDialog(
    onDismiss: () -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 4f)
        if (scale > 1f) {
            offset += panChange
        } else {
            offset = Offset.Zero
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .testTag("dialog_fullscreen_poster")
        ) {
            // Zoomable and Pannable Image Container
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .transformable(state = transformState),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_rowzeh_guide_poster),
                    contentDescription = "پوستر راهنمای نرم‌افزار ساعت روضه",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        ),
                    contentScale = ContentScale.Fit
                )
            }

            // Top action bar overlay with close button and hint
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "با دو انگشت می‌توانید بزرگ‌نمایی کنید",
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.65f))
                        .testTag("btn_close_fullscreen_poster")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "بستن",
                        tint = Color.White
                    )
                }
            }

            // Reset zoom button when zoomed in
            if (scale > 1.1f) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = TurquoisePrimary,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                        .clickable {
                            scale = 1f
                            offset = Offset.Zero
                        }
                ) {
                    Text(
                        text = "بازگشت به اندازه اصلی",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}
