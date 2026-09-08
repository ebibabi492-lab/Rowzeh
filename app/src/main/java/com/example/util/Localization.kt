package com.example.util

import androidx.compose.runtime.compositionLocalOf

/**
 * Supported application languages
 */
enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val isRtl: Boolean
) {
    PERSIAN(code = "fa", displayName = "Persian", nativeName = "فارسی", isRtl = true),
    ARABIC(code = "ar", displayName = "Arabic", nativeName = "العربية", isRtl = true),
    ENGLISH(code = "en", displayName = "English", nativeName = "English", isRtl = false);

    companion object {
        fun fromCode(code: String?): AppLanguage {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: PERSIAN
        }
    }
}

/**
 * Format numbers into Persian, Arabic, or English digits based on language
 */
fun formatNumberByLanguage(text: String, language: AppLanguage): String {
    return when (language) {
        AppLanguage.PERSIAN -> {
            val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
            text.map { ch ->
                if (ch in '0'..'9') persianDigits[ch - '0'] else ch
            }.joinToString("")
        }
        AppLanguage.ARABIC -> {
            val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
            text.map { ch ->
                if (ch in '0'..'9') arabicDigits[ch - '0'] else ch
            }.joinToString("")
        }
        AppLanguage.ENGLISH -> text
    }
}

data class GuideSectionItem(
    val title: String,
    val points: List<String>
)

/**
 * Localized string resources for the entire app
 */
data class AppStrings(
    val appTitle: String,
    val appSubtitle: String,
    val statusActive: String,
    val statusInactive: String,
    val btnQuickTest: String,
    val btnSettings: String,
    val btnGuide: String,
    val btnBack: String,
    val languageLabel: String,

    // Main Screen Cards
    val autoPlayTitle: String,
    val autoPlaySubtitle: String,
    val timeRangeCardTitle: String,
    val timeRangeCardSubtitle: String,
    val repeatTitle: String,
    val repeatDaily: String,
    val repeatWeekly: String,
    val volumeTitle: String,
    val btnOpenSettings: String,
    val btnRecordVoice: String,
    val btnUploadAudio: String,
    val trackListTitle: String,
    val trackListEmptyTitle: String,
    val trackListEmptyDesc: String,
    val filterAll: String,
    val filterRecorded: String,
    val filterUploaded: String,
    val badgeDefault: String,
    val badgeRecorded: String,
    val badgeUploaded: String,
    val playNow: String,
    val stopPlay: String,
    val deleteTrack: String,
    val confirmDeleteTitle: String,
    val confirmDeleteMessage: String,
    val deleteConfirmBtn: String,
    val deleteCancelBtn: String,

    // Days of week short labels (Sat to Fri)
    val daySaturday: String,
    val daySunday: String,
    val dayMonday: String,
    val dayTuesday: String,
    val dayWednesday: String,
    val dayThursday: String,
    val dayFriday: String,

    // Alert Dialog
    val alertTitle: String,
    val alertSubtitle: String,
    val alertHeartPrep: String,
    val btnPlayRowzeh: String,
    val btnDismiss: String,

    // Voice Recording Dialog
    val recordDialogTitle: String,
    val recordDialogSubtitle: String,
    val btnStartRecord: String,
    val btnStopRecord: String,
    val inputTrackTitle: String,
    val inputMaddah: String,
    val btnSaveRecord: String,
    val btnCancelRecord: String,

    // Settings Screen
    val settingsTitle: String,
    val settingsSubtitle: String,
    val nextSchedulePrefix: String,
    val settingsGuideBannerTitle: String,
    val settingsGuideBannerDesc: String,
    val customIntervalsTitle: String,
    val customIntervalsSubtitle: String,
    val btnAddInterval: String,
    val presetsTitle: String,
    val presetMorning: String,
    val presetNoon: String,
    val presetSunset: String,
    val presetNight: String,
    val dialogAddIntervalTitle: String,
    val dialogEditIntervalTitle: String,
    val intervalLabelName: String,
    val intervalStartTime: String,
    val intervalEndTime: String,
    val btnSave: String,
    val btnCancel: String,
    val switchActive: String,
    val switchInactive: String,

    // Guide Screen
    val guideScreenTitle: String,
    val guidePosterTitle: String,
    val guidePosterBadge: String,
    val guideZoomHint: String,
    val btnFullscreenPoster: String,
    val guideSpiritualSlogan: String,
    val guideSpiritualDesc: String,
    val pillarHearing: String,
    val pillarPeace: String,
    val pillarRemembrance: String,
    val pillarAnytime: String,
    val guideFeaturesHeader: String,
    val guidePrayer: String,
    val guideFooterSlogan: String,
    val btnResetZoom: String,
    val zoomGestureHint: String,
    val guideSections: List<GuideSectionItem>
)

val LocalAppStrings = compositionLocalOf { StringsProvider.getStrings(AppLanguage.PERSIAN) }
val LocalAppLanguage = compositionLocalOf { AppLanguage.PERSIAN }

object StringsProvider {

    fun getStrings(language: AppLanguage): AppStrings {
        return when (language) {
            AppLanguage.PERSIAN -> PERSIAN_STRINGS
            AppLanguage.ARABIC -> ARABIC_STRINGS
            AppLanguage.ENGLISH -> ENGLISH_STRINGS
        }
    }

    private val PERSIAN_STRINGS = AppStrings(
        appTitle = "ساعت روضه",
        appSubtitle = "روزی چند دقیقه، دلمان را زنده نگه داریم...",
        statusActive = "برنامه پخش تصادفی فعال است",
        statusInactive = "برنامه غیرفعال است",
        btnQuickTest = "تست هشدار",
        btnSettings = "تنظیمات بازه‌ها",
        btnGuide = "راهنمای نرم‌افزار",
        btnBack = "بازگشت",
        languageLabel = "زبان برنامه",

        autoPlayTitle = "پخش خودکار و تصادفی",
        autoPlaySubtitle = "انتخاب تصادفی یک روضه در بازه معین",
        timeRangeCardTitle = "بازه زمانی پخش (ساعت شروع تا پایان)",
        timeRangeCardSubtitle = "پخش در زمان نامشخص و غافلگیرکننده در این بازه",
        repeatTitle = "انتخاب تکرار روضه",
        repeatDaily = "تکرار روزانه",
        repeatWeekly = "تکرار هفتگی",
        volumeTitle = "میزان صدای پخش روضه",
        btnOpenSettings = "صفحه تنظیمات و بازه‌های زمانی دلخواه",
        btnRecordVoice = "ضبط صدای روضه",
        btnUploadAudio = "بارگذاری فایل صوتی",
        trackListTitle = "فهرست روضه‌ها و نواهای شما",
        trackListEmptyTitle = "هنوز روضه‌ای اضافه نکرده‌اید",
        trackListEmptyDesc = "فایل‌های پیش‌فرض طبق درخواست شما حذف شدند. برای پخش اتفاقی روضه در ساعات تعیین‌شده، لطفاً با دکمه‌های بالا اولین فایل صوتی خود را اضافه یا ضبط کنید.",
        filterAll = "همه",
        filterRecorded = "ضبط‌شده‌ها",
        filterUploaded = "بارگذاری‌شده‌ها",
        badgeDefault = "پیش‌فرض",
        badgeRecorded = "ضبط‌شده",
        badgeUploaded = "فایل شخصی",
        playNow = "پخش",
        stopPlay = "توقف",
        deleteTrack = "حذف روضه",
        confirmDeleteTitle = "حذف فایل صوتی",
        confirmDeleteMessage = "آیا از حذف این روضه از فهرست اطمینان دارید؟",
        deleteConfirmBtn = "بله، حذف شود",
        deleteCancelBtn = "انصراف",

        daySaturday = "ش",
        daySunday = "ی",
        dayMonday = "د",
        dayTuesday = "س",
        dayWednesday = "چ",
        dayThursday = "پ",
        dayFriday = "ج",

        alertTitle = "زمان روضه",
        alertSubtitle = "پخش خودکار نوای معنوی تا لحظاتی دیگر...",
        alertHeartPrep = "دل را به نور ذکر و یاد اهل‌بیت (ع) روشن کنیم...",
        btnPlayRowzeh = "پخش روضه",
        btnDismiss = "انصراف",

        recordDialogTitle = "ضبط صدای روضه",
        recordDialogSubtitle = "با میکروفون گوشی نوای دلخواه را ضبط کنید",
        btnStartRecord = "شروع ضبط",
        btnStopRecord = "توقف ضبط",
        inputTrackTitle = "عنوان روضه (اختیاری)",
        inputMaddah = "نام مداح یا ذاکر (اختیاری)",
        btnSaveRecord = "ذخیره در فهرست",
        btnCancelRecord = "لغو و انصراف",

        settingsTitle = "تنظیمات بازه‌های زمانی",
        settingsSubtitle = "مدیریت ساعات و زمان‌بندی تصادفی روضه",
        nextSchedulePrefix = "هشدار روضه بعدی:",
        settingsGuideBannerTitle = "راهنما و پوستر ساعت روضه",
        settingsGuideBannerDesc = "مشاهده پوستر تصویری و شرح ۹ قابلیت برنامه",
        customIntervalsTitle = "بازه‌های زمانی دلخواه",
        customIntervalsSubtitle = "تعیین بازه‌های مختلف برای صبح، ظهر، عصر و شب",
        btnAddInterval = "افزودن بازه جدید",
        presetsTitle = "الگوهای پیشنهادی آماده",
        presetMorning = "سحر و صبحگاه",
        presetNoon = "ظهر و بعدازظهر",
        presetSunset = "غروب و شامگاه",
        presetNight = "شبانگاهی",
        dialogAddIntervalTitle = "افزودن بازه زمانی جدید",
        dialogEditIntervalTitle = "ویرایش بازه زمانی",
        intervalLabelName = "عنوان بازه (اختیاری)",
        intervalStartTime = "ساعت شروع",
        intervalEndTime = "ساعت پایان",
        btnSave = "ذخیره",
        btnCancel = "انصراف",
        switchActive = "فعال",
        switchInactive = "غیرفعال",

        guideScreenTitle = "راهنما و پوستر ساعت روضه",
        guidePosterTitle = "پوستر معرفی و راهنمای جامع",
        guidePosterBadge = "راهنمای تصویری",
        guideZoomHint = "برای بزرگ‌نمایی و مشاهده کامل لمس کنید",
        btnFullscreenPoster = "مشاهده تمام‌صفحه پوستر راهنما",
        guideSpiritualSlogan = "روزی چند دقیقه، دلمان را زنده نگه داریم...",
        guideSpiritualDesc = "اپلیکیشن «ساعت روضه» با طراحی سنتی و اصیل به شما کمک می‌کند تا در طول روز، در زمان‌های دلخواه، به صورت خودکار و غافلگیرکننده روضه‌های اهل‌بیت (ع) را بشنوید و دلتان را با یادشان صفا دهید.",
        pillarHearing = "شنیدن روضه",
        pillarPeace = "آرامش دل",
        pillarRemembrance = "یاد اهل‌بیت",
        pillarAnytime = "هر زمان و مکان",
        guideFeaturesHeader = "راهنمای قابلیت‌ها و امکانات برنامه",
        guidePrayer = "«اللهم عجل لولیک الفرج»",
        guideFooterSlogan = "ساعت روضه — همراه همیشگی دل‌های عاشق",
        btnResetZoom = "بازگشت به اندازه اصلی",
        zoomGestureHint = "با دو انگشت می‌توانید بزرگ‌نمایی کنید",
        guideSections = listOf(
            GuideSectionItem(
                "طراحی سنتی و مینیمال",
                listOf(
                    "ترکیب رنگ اصیل فیروزه‌ای و طلایی ایرانی",
                    "نقوش هندسی شمسه و حاشیه‌های آرامش‌بخش",
                    "پس‌زمینه عاجی سازگار با چشم و باوقار",
                    "رابط کاربری راست‌به‌چپ (RTL) متناسب با زبان فارسی",
                    "آیکون لانچر اختصاصی با نماد ساعت و ضریح مطهر"
                )
            ),
            GuideSectionItem(
                "پخش تصادفی در بازه زمانی",
                listOf(
                    "انتخاب بازه دلخواه (ساعت شروع و پایان)",
                    "زمان‌بندی هوشمند دقیق بدون اختلال در مصرف باتری",
                    "پخش تصادفی و غافلگیرکننده در طول روز جهت پیوند قلبی با اهل‌بیت (ع)"
                )
            ),
            GuideSectionItem(
                "هشدار پیش از پخش",
                listOf(
                    "اعلان اولویت‌بالا همراه با لرزش و آوای هشدار",
                    "دیالوگ هشدار درون‌برنامه‌ای با شمارش معکوس آمادگی دل",
                    "دکمه‌های انتخابی «پخش روضه» یا «انصراف» در شرایط نامناسب",
                    "دکمه تست سریع جهت شنیدن آنی روضه یا تست کارکرد"
                )
            ),
            GuideSectionItem(
                "تکرار و میزان صدا",
                listOf(
                    "تکرار روزانه یا هفتگی طبق برنامه مدنظر شما",
                    "امکان انتخاب تک‌تک روزهای دلخواه هفته",
                    "اسلایدر تنظیم دقیق بلندی صدای پخش (از ۵٪ تا ۱۰۰٪)",
                    "اعمال مستقیم و بدون تاخیر روی پخش‌کننده روضه"
                )
            ),
            GuideSectionItem(
                "مدیریت فهرست روضه‌ها",
                listOf(
                    "فهرست اختصاصی صوتی با چک‌باکس فعال/غیرفعال برای هر اثر",
                    "امکان گزینش روضه‌های مورد علاقه جهت شرکت در قرعه‌کشی پخش",
                    "امکان حذف یا مدیریت آسان فایل‌ها با یک لمس"
                )
            ),
            GuideSectionItem(
                "ضبط صدای روضه",
                listOf(
                    "ضبط مستقیم نوای دلخواه با میکروفون باکیفیت گوشی",
                    "نمایش تایمر زنده و طول مدت ضبط",
                    "امکان نام‌گذاری و ثبت نام مداح برای هر صوت ضبط‌شده"
                )
            ),
            GuideSectionItem(
                "بارگذاری فایل صوتی",
                listOf(
                    "افزودن نامحدود فایل‌های صوتی دلخواه از حافظه گوشی",
                    "پشتیبانی از انواع فرمت‌های رایج: MP3, M4A, WAV, OGG",
                    "کپی امن در حافظه داخلی اختصاصی نرم‌افزار"
                )
            ),
            GuideSectionItem(
                "ویجت صفحه اصلی (Home Widget)",
                listOf(
                    "نمایش وضعیت فعال یا غیرفعال بودن برنامه روی دسکتاپ",
                    "نمایش بازه زمانی مشخص‌شده و عنوان فایل در حال پخش",
                    "کنترل سریع روشن/خاموش کردن برنامه بدون ورود به اپ",
                    "دکمه پخش یا توقف فوری روضه با یک لمس"
                )
            ),
            GuideSectionItem(
                "تنظیمات اختصاصی و چندگانه",
                listOf(
                    "مدیریت چندین بازه زمانی دلخواه در طول روز (صبح، ظهر، غروب، شب)",
                    "الگوهای پیش‌فرض کاربردی با قابلیت فعال‌سازی با یک لمس",
                    "امکان افزودن، ویرایش و حذف بازه‌های دلخواه",
                    "ذخیره‌سازی مطمئن و دائمی در پایگاه‌داده محلی"
                )
            )
        )
    )

    private val ARABIC_STRINGS = AppStrings(
        appTitle = "ساعة الروضة",
        appSubtitle = "بضع دقائق يومياً، لنحيي بها قلوبنا...",
        statusActive = "جدول البث العشوائي مفعّل",
        statusInactive = "البرنامج معطّل",
        btnQuickTest = "اختبار التنبيه",
        btnSettings = "إعدادات الفترات",
        btnGuide = "دليل التطبيق والملصق",
        btnBack = "رجوع",
        languageLabel = "لغة التطبيق",

        autoPlayTitle = "التشغيل التلقائي والعشوائي",
        autoPlaySubtitle = "اختيار عشوائي للروضة في الفترة المحددة",
        timeRangeCardTitle = "الفترة الزمنية للبث (من وقت البدء حتى الانتهاء)",
        timeRangeCardSubtitle = "بث في وقت غير محدد ومفاجئ خلال هذه الفترة",
        repeatTitle = "تكرار البث",
        repeatDaily = "تكرار يومي",
        repeatWeekly = "تكرار أسبوعي",
        volumeTitle = "مستوى صوت البث",
        btnOpenSettings = "صفحة الإعدادات والفترات الزمنية المخصصة",
        btnRecordVoice = "تسجيل صوتي",
        btnUploadAudio = "تحميل ملف صوتي",
        trackListTitle = "قائمة الروضات والتسجيلات الخاصة بك",
        trackListEmptyTitle = "لم تقم بإضافة أي روضة بعد",
        trackListEmptyDesc = "تم حذف الملفات الافتراضية بناءً على طلبك. للبث العشوائي في الأوقات المحددة، يرجى إضافة أو تسجيل أول ملف صوتي باستخدام الأزرار أعلاه.",
        filterAll = "الكل",
        filterRecorded = "المسجلة",
        filterUploaded = "المحمّلة",
        badgeDefault = "افتراضي",
        badgeRecorded = "تسجيل",
        badgeUploaded = "ملف شخصي",
        playNow = "تشغيل",
        stopPlay = "إيقاف",
        deleteTrack = "حذف التسجيل",
        confirmDeleteTitle = "حذف الملف الصوتي",
        confirmDeleteMessage = "هل أنت متأكد من حذف هذه الروضة من القائمة؟",
        deleteConfirmBtn = "نعم، حذف",
        deleteCancelBtn = "إلغاء",

        daySaturday = "س",
        daySunday = "ح",
        dayMonday = "ن",
        dayTuesday = "ث",
        dayWednesday = "ر",
        dayThursday = "خ",
        dayFriday = "ج",

        alertTitle = "حان وقت الروضة",
        alertSubtitle = "البث التلقائي للروضة سيبدأ بعد قليل...",
        alertHeartPrep = "لننير قلوبنا بذكر أهل البيت (عليهم السلام)...",
        btnPlayRowzeh = "تشغيل الروضة",
        btnDismiss = "إلغاء",

        recordDialogTitle = "تسجيل صوت الروضة",
        recordDialogSubtitle = "سجل المقطع الصوتي المرغوب باستخدام الميكروفون",
        btnStartRecord = "بدء التسجيل",
        btnStopRecord = "إيقاف التسجيل",
        inputTrackTitle = "عنوان الروضة (اختياري)",
        inputMaddah = "اسم الرادود أو الذاكر (اختياري)",
        btnSaveRecord = "حفظ في القائمة",
        btnCancelRecord = "إلغاء",

        settingsTitle = "إعدادات الفترات الزمنية",
        settingsSubtitle = "إدارة أوقات وجداول البث العشوائي للروضة",
        nextSchedulePrefix = "موعد التنبيه القادم:",
        settingsGuideBannerTitle = "دليل وملصق ساعة الروضة",
        settingsGuideBannerDesc = "مشاهدة الملصق التوضيحي وشرح 9 ميزات للتطبيق",
        customIntervalsTitle = "الفترات الزمنية المخصصة",
        customIntervalsSubtitle = "تحديد فترات متعددة للصباح، الظهر، المساء والليل",
        btnAddInterval = "إضافة فترة جديدة",
        presetsTitle = "النماذج المقترحة الجاهزة",
        presetMorning = "السحر والصباح",
        presetNoon = "الظهر والعصر",
        presetSunset = "الغروب والمساء",
        presetNight = "الليل",
        dialogAddIntervalTitle = "إضافة فترة زمنية جديدة",
        dialogEditIntervalTitle = "تعديل الفترة الزمنية",
        intervalLabelName = "اسم الفترة (اختياري)",
        intervalStartTime = "وقت البدء",
        intervalEndTime = "وقت الانتهاء",
        btnSave = "حفظ",
        btnCancel = "إلغاء",
        switchActive = "مفعّل",
        switchInactive = "معطّل",

        guideScreenTitle = "دليل وملصق ساعة الروضة",
        guidePosterTitle = "الملصق التعريفي الشامل",
        guidePosterBadge = "دليل توضيحي",
        guideZoomHint = "المس للتكبير والمشاهدة الكاملة",
        btnFullscreenPoster = "عرض الملصق بالشاشة الكاملة",
        guideSpiritualSlogan = "بضع دقائق يومياً، لنحيي بها قلوبنا...",
        guideSpiritualDesc = "تطبيق «ساعة الروضة» بتصميمه التراثي والأصيل يساعدك على الاستماع إلى روضات أهل البيت (ع) بشكل عشوائي وتلقائي في الأوقات التي تختارها لتنوير القلب بذكراهم.",
        pillarHearing = "سماع الروضة",
        pillarPeace = "طمأنينة القلب",
        pillarRemembrance = "ذكر أهل البيت",
        pillarAnytime = "في أي وقت ومكان",
        guideFeaturesHeader = "دليل ميزات وإمكانيات التطبيق",
        guidePrayer = "«اللهم عجل لوليك الفرج»",
        guideFooterSlogan = "ساعة الروضة — الرفيق الدائم للقلوب العاشقة",
        btnResetZoom = "العودة للحجم الأصلي",
        zoomGestureHint = "يمكنك التكبير بإصبعين",
        guideSections = listOf(
            GuideSectionItem(
                "تصميم تراثي وبسيط",
                listOf(
                    "تناسق لوني بين الفيروزي والذهبي الأصيل",
                    "زخارف هندسية إسلامية مريحة للنظر",
                    "خلفية عاجية مريحة للعين ووقورة",
                    "واجهة تدعم الاتجاه من اليمين إلى اليسار (RTL)",
                    "أيقونة خاصة مستوحاة من المحراب والضريح الشريف"
                )
            ),
            GuideSectionItem(
                "بث عشوائي في فترة زمنية",
                listOf(
                    "اختيار الفترة المرغوبة (وقت البدء والانتهاء)",
                    "جدولة ذكية دقيقة دون استهلاك إضافي للبطارية",
                    "بث عشوائي ومفاجئ لتجديد العهد مع أهل البيت (ع)"
                )
            ),
            GuideSectionItem(
                "تنبيه مسبق قبل البث",
                listOf(
                    "إشعار ذو أولوية قصوى مصحوب باهتزاز وصوت تنبيه",
                    "نافذة تنبيه تفاعلية داخل التطبيق لإعداد القلب",
                    "أزرار اختيار «تشغيل الروضة» أو «إلغاء» عند عدم توفر الظروف",
                    "زر اختبار سريع للاستماع الفوري أو فحص الإعدادات"
                )
            ),
            GuideSectionItem(
                "التكرار ومستوى الصوت",
                listOf(
                    "تكرار يومي أو أسبوعي حسب جدولك الخاص",
                    "إمكانية تحديد كل يوم من أيام الأسبوع بشكل مستقل",
                    "شريط تمرير دقيق للتحكم بمستوى الصوت (من 5% إلى 100%)",
                    "تطبيق فوري على مشغل الصوت دون أي تأخير"
                )
            ),
            GuideSectionItem(
                "إدارة قائمة الروضات",
                listOf(
                    "قائمة صوتية مخصصة مع خيار تفعيل/تعطيل لكل تسجيل",
                    "إمكانية اختيار المقاطع المفضلة للدخول في قرعة البث",
                    "إمكانية حذف أو إدارة الملفات بلمسة واحدة"
                )
            ),
            GuideSectionItem(
                "تسجيل صوت الروضة",
                listOf(
                    "تسجيل صوتي مباشر بميكروفون الهاتف عالي الجودة",
                    "عرض مؤقت زمني ومدة التسجيل بدقة",
                    "إمكانية تسمية التسجيل وتدوين اسم القارئ/الرادود"
                )
            ),
            GuideSectionItem(
                "تحميل الملفات الصوتية",
                listOf(
                    "إضافة عدد غير محدود من الملفات الصوتية من ذاكرة الهاتف",
                    "دعم الصيغ الشائعة: MP3, M4A, WAV, OGG",
                    "نسخ آمن إلى الذاكرة الداخلية الخاصة بالتطبيق"
                )
            ),
            GuideSectionItem(
                "أداة الشاشة الرئيسية (Widget)",
                listOf(
                    "عرض حالة تفعيل أو تعطيل البرنامج على الشاشة الرئيسية",
                    "عرض الفترة الزمنية المحددة وعنوان المقطع الصوتي الحالي",
                    "تحكم سريع بالتشغيل/الإيقاف دون الحاجة لفتح التطبيق",
                    "زر تشغيل أو إيقاف فوري بلمسة واحدة"
                )
            ),
            GuideSectionItem(
                "إعدادات وفترات متعددة",
                listOf(
                    "إدارة فترات زمنية متعددة خلال اليوم (الصباح، الظهر، الغروب، الليل)",
                    "نماذج جاهزة قابلة للتفعيل بلمسة واحدة",
                    "إمكانية إضافة وتعديل وحذف الفترات بسهولة",
                    "تخزين موثوق ودائم في قاعدة البيانات المحلية (Room)"
                )
            )
        )
    )

    private val ENGLISH_STRINGS = AppStrings(
        appTitle = "Rowzeh Clock",
        appSubtitle = "A few minutes a day to enliven our hearts...",
        statusActive = "Random playback schedule is active",
        statusInactive = "Program is disabled",
        btnQuickTest = "Test Alert",
        btnSettings = "Interval Settings",
        btnGuide = "Guide & Poster",
        btnBack = "Back",
        languageLabel = "App Language",

        autoPlayTitle = "Automatic Random Playback",
        autoPlaySubtitle = "Random selection of an audio track in the set window",
        timeRangeCardTitle = "Playback Time Window (Start to End)",
        timeRangeCardSubtitle = "Surprise playback at an unexpected time in this range",
        repeatTitle = "Repetition Schedule",
        repeatDaily = "Daily Repeat",
        repeatWeekly = "Weekly Repeat",
        volumeTitle = "Playback Volume Level",
        btnOpenSettings = "Custom Intervals & Settings Screen",
        btnRecordVoice = "Record Voice",
        btnUploadAudio = "Upload Audio File",
        trackListTitle = "Your Audio Tracks List",
        trackListEmptyTitle = "No audio tracks added yet",
        trackListEmptyDesc = "Default audio files were removed per your preference. To enable scheduled surprise playback, please add or record your first audio track using the buttons above.",
        filterAll = "All",
        filterRecorded = "Recorded",
        filterUploaded = "Uploaded",
        badgeDefault = "Default",
        badgeRecorded = "Recorded",
        badgeUploaded = "Custom File",
        playNow = "Play",
        stopPlay = "Stop",
        deleteTrack = "Delete Track",
        confirmDeleteTitle = "Delete Audio Track",
        confirmDeleteMessage = "Are you sure you want to remove this track from the list?",
        deleteConfirmBtn = "Yes, Delete",
        deleteCancelBtn = "Cancel",

        daySaturday = "Sat",
        daySunday = "Sun",
        dayMonday = "Mon",
        dayTuesday = "Tue",
        dayWednesday = "Wed",
        dayThursday = "Thu",
        dayFriday = "Fri",

        alertTitle = "Time for Rowzeh",
        alertSubtitle = "Spiritual audio track will play momentarily...",
        alertHeartPrep = "Let us illuminate our hearts with the remembrance of Ahlulbayt (a.s)...",
        btnPlayRowzeh = "Play Rowzeh",
        btnDismiss = "Dismiss",

        recordDialogTitle = "Record Voice",
        recordDialogSubtitle = "Record recitation using your device microphone",
        btnStartRecord = "Start Recording",
        btnStopRecord = "Stop Recording",
        inputTrackTitle = "Track title (Optional)",
        inputMaddah = "Reciter / Maddah (Optional)",
        btnSaveRecord = "Save to List",
        btnCancelRecord = "Cancel",

        settingsTitle = "Time Intervals Settings",
        settingsSubtitle = "Manage hours and random playback schedules",
        nextSchedulePrefix = "Next Scheduled Rowzeh:",
        settingsGuideBannerTitle = "Rowzeh Clock Guide & Poster",
        settingsGuideBannerDesc = "View visual poster and guide for 9 app features",
        customIntervalsTitle = "Custom Time Intervals",
        customIntervalsSubtitle = "Set multiple periods for morning, noon, evening, and night",
        btnAddInterval = "Add New Interval",
        presetsTitle = "Preset Time Intervals",
        presetMorning = "Dawn & Morning",
        presetNoon = "Noon & Afternoon",
        presetSunset = "Sunset & Evening",
        presetNight = "Night",
        dialogAddIntervalTitle = "Add New Time Interval",
        dialogEditIntervalTitle = "Edit Time Interval",
        intervalLabelName = "Interval title (Optional)",
        intervalStartTime = "Start Time",
        intervalEndTime = "End Time",
        btnSave = "Save",
        btnCancel = "Cancel",
        switchActive = "Active",
        switchInactive = "Inactive",

        guideScreenTitle = "Rowzeh Clock Guide & Poster",
        guidePosterTitle = "Comprehensive Introductory Poster",
        guidePosterBadge = "Visual Guide",
        guideZoomHint = "Tap to enlarge and view full poster",
        btnFullscreenPoster = "View Full Screen Poster",
        guideSpiritualSlogan = "A few minutes a day to enliven our hearts...",
        guideSpiritualDesc = "'Rowzeh Clock' app with its authentic traditional design helps you listen to holy Ahlulbayt (a.s) recitations randomly and automatically during the day at your desired hours to bring tranquility to your heart.",
        pillarHearing = "Listening",
        pillarPeace = "Tranquility",
        pillarRemembrance = "Remembrance",
        pillarAnytime = "Anytime, Anywhere",
        guideFeaturesHeader = "Application Features & Overview",
        guidePrayer = "«O Allah, hasten the reappearance of Your Guardian»",
        guideFooterSlogan = "Rowzeh Clock — Constant Companion for Devoted Hearts",
        btnResetZoom = "Reset Zoom",
        zoomGestureHint = "Pinch with two fingers to zoom",
        guideSections = listOf(
            GuideSectionItem(
                "Traditional & Minimal Design",
                listOf(
                    "Authentic Persian turquoise and antique gold color palette",
                    "Serene Islamic geometric motifs and Shamseh dividers",
                    "Warm ivory background that is easy on the eyes",
                    "Full RTL support for Persian/Arabic and LTR for English",
                    "Custom launcher icon inspired by holy shrine and clock"
                )
            ),
            GuideSectionItem(
                "Random Playback in Time Windows",
                listOf(
                    "Choose custom start and end hours",
                    "Precise smart scheduling with zero battery drain",
                    "Surprise random playback to reconnect with spirituality"
                )
            ),
            GuideSectionItem(
                "Pre-Playback Alert Notice",
                listOf(
                    "High-priority notification with gentle vibration and ringtone",
                    "In-app dialog countdown for heart readiness",
                    "Options to Play Now or Dismiss if currently occupied",
                    "Quick Test button to verify alarms and audio immediately"
                )
            ),
            GuideSectionItem(
                "Repetition & Volume Control",
                listOf(
                    "Daily or weekly repeat schedules according to your routine",
                    "Select individual days from Saturday to Friday",
                    "Precise volume slider (5% to 100%)",
                    "Direct real-time application to audio player"
                )
            ),
            GuideSectionItem(
                "Track List Management",
                listOf(
                    "Dedicated audio list with active/inactive checkmarks",
                    "Pick favorite recitations to enter the random playback pool",
                    "Effortlessly remove or manage tracks with one tap"
                )
            ),
            GuideSectionItem(
                "Voice Recording",
                listOf(
                    "Record heartfelt recitations directly with phone microphone",
                    "Live timer display and duration tracking",
                    "Label track with title and reciter/maddah name"
                )
            ),
            GuideSectionItem(
                "Audio File Upload",
                listOf(
                    "Add unlimited audio files directly from phone storage",
                    "Supports common formats: MP3, M4A, WAV, OGG",
                    "Safely stored in app's private internal storage"
                )
            ),
            GuideSectionItem(
                "Home Screen Widget",
                listOf(
                    "View active schedule status right on your home screen",
                    "Shows current active time window and playing track title",
                    "Toggle schedule on/off without opening the app",
                    "One-touch instant playback or stop button"
                )
            ),
            GuideSectionItem(
                "Custom Multiple Intervals",
                listOf(
                    "Configure multiple custom windows across morning, noon, and night",
                    "One-touch preset templates ready for quick activation",
                    "Add, edit, or remove time intervals seamlessly",
                    "Persistent offline storage using local Room Database"
                )
            )
        )
    )
}
