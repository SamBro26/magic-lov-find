package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.PropertyDao
import com.example.data.model.PropertyItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [PropertyItem::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun propertyDao(): PropertyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "findmedz_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Seed database in background
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    database.propertyDao().insertProperties(getInitialSeedData())
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private fun getInitialSeedData(): List<PropertyItem> {
            return listOf(
                // Offers (عقارات معروضة)
                PropertyItem(
                    isOffer = true,
                    title = "شقة F4 فاخرة للكراء مجهزة بالكامل بباب الزوار",
                    description = "شقة F4 مجهزة بالكامل بكافة المستلزمات، أثاث فاخر، مكيفات هواء في جميع الغرف، مطبخ مجهز، قريب جداً من الجامعات وحافلات النقل والترامواي.",
                    priceDzd = 65000,
                    priceDisplayCentimes = "6.5 مليون/شهر",
                    wilaya = "16 - الجزائر العاصمة",
                    commune = "باب الزوار",
                    category = "شقة",
                    transactionType = "للكراء",
                    rooms = "F4",
                    areaSqMeters = 125,
                    phone = "0551 23 45 67",
                    publisherName = "وكالة العاصمة العقارية",
                    publisherType = "وكالة عقارية",
                    featuresList = "عقد توثيقي,غاز,ماء,كهرباء,مصعد,مرآب,تدفئة مركزية,مكيف",
                    isFavorite = true
                ),
                PropertyItem(
                    isOffer = true,
                    title = "فيلا حديثة للبيع 300م² بحيدرة موقع استراتيجي",
                    description = "فيلا فخمة من 3 طوابق مع حديقة ومسبح خاص، كراج يسع لـ 3 سيارات، حراسة ومراقبة كاميرات. عقد توثيقي ودفتر عقاري مشهر.",
                    priceDzd = 68000000,
                    priceDisplayCentimes = "6.8 مليار (6800 مليون)",
                    wilaya = "16 - الجزائر العاصمة",
                    commune = "حيدرة",
                    category = "فيلا",
                    transactionType = "للبيع",
                    rooms = "F6+",
                    areaSqMeters = 300,
                    phone = "0662 98 76 54",
                    publisherName = "الحاج بوعلام",
                    publisherType = "مالك",
                    featuresList = "عقد توثيقي,دفتر عقاري,حديقة,مسبح,مرآب,غاز,ماء,كهرباء",
                    isFavorite = false
                ),
                PropertyItem(
                    isOffer = true,
                    title = "شقة F3 للبيع بوسط مدينة وهران مع عقد توثيقي",
                    description = "شقة F3 بعمارة هادئة بقلب وهران، إطلالة على البحر، قريب من الشارع التجاري ومحطة القطار. جاهزة للسكن فوراً.",
                    priceDzd = 14500000,
                    priceDisplayCentimes = "1.45 مليار (1450 مليون)",
                    wilaya = "31 - وهران",
                    commune = "وهران",
                    category = "شقة",
                    transactionType = "للبيع",
                    rooms = "F3",
                    areaSqMeters = 95,
                    phone = "0770 11 22 33",
                    publisherName = "سفيان الوهراني",
                    publisherType = "مالك",
                    featuresList = "عقد توثيقي,دفتر عقاري,مصعد,غاز,ماء,كهرباء",
                    isFavorite = true
                ),
                PropertyItem(
                    isOffer = true,
                    title = "استوديو للكراء اليومي قريب من شاطئ عين الترك",
                    description = "استوديو مكيف ومجهز بالكير المريح للكراء اليومي للعائلات أو المصطافين. تبعد 2 دقيقة مشياً عن الشاطئ.",
                    priceDzd = 6000,
                    priceDisplayCentimes = "600 ألف/ليلة",
                    wilaya = "31 - وهران",
                    commune = "عين الترك",
                    category = "استوديو",
                    transactionType = "كراء يومي",
                    rooms = "F1",
                    areaSqMeters = 45,
                    phone = "0558 44 55 66",
                    publisherName = "إقامة الباهية",
                    publisherType = "خاص",
                    featuresList = "مكيفة,مجهزة,ماء 24/24,قريب للبحر,مرآب",
                    isFavorite = false
                ),
                PropertyItem(
                    isOffer = true,
                    title = "محل تجاري 80م² للبيع في موقع حيوي بعلي منجلي",
                    description = "محل تجاري ذو واجهة واسعة زجاجية، كهرباء 380V متوفرة، صالح لجميع النشاطات التجارية والصيدليات أو المقاهي.",
                    priceDzd = 22000000,
                    priceDisplayCentimes = "2.2 مليار (2200 مليون)",
                    wilaya = "25 - قسنطينة",
                    commune = "علي منجلي",
                    category = "محل تجاري",
                    transactionType = "للبيع",
                    rooms = "محل",
                    areaSqMeters = 80,
                    phone = "0661 77 88 99",
                    publisherName = "وكالة سيرتا",
                    publisherType = "وكالة عقارية",
                    featuresList = "عقد توثيقي,كهرباء 380V,ماء,غاز,واجهة زجاجية",
                    isFavorite = false
                ),
                PropertyItem(
                    isOffer = true,
                    title = "أرض بناء 250م² للبيع في سطيف عين أرنات",
                    description = "قطعة أرض مستوية ذات واجهتين، واقعة بالتجزئة السكنية الجديدة بعين أرنات سطيف. عقد وصافي 100%.",
                    priceDzd = 18500000,
                    priceDisplayCentimes = "1.85 مليار (1850 مليون)",
                    wilaya = "19 - سطيف",
                    commune = "عين أرنات",
                    category = "أرض",
                    transactionType = "للبيع",
                    rooms = "أرض",
                    areaSqMeters = 250,
                    phone = "0554 32 10 98",
                    publisherName = "عمر الهضاب",
                    publisherType = "مالك",
                    featuresList = "عقد توثيقي,دفتر عقاري,جميع الشبكات متوفرة",
                    isFavorite = false
                ),

                // Requests (طلبات البحث - "دبرلي")
                PropertyItem(
                    isOffer = false,
                    title = "مطلوب شقة F3 للكراء العائلي بالشراقة أو دالي إبراهيم",
                    description = "دبرلي سكن: عائلة صغيرة محترمة تبحث عن شقة F3 نظيفة للكراء بعقد سنوي. الدفع متوفر 12 شهر مسبقاً فور الاتفاق.",
                    priceDzd = 45000,
                    priceDisplayCentimes = "حتى 4.5 مليون/شهر",
                    wilaya = "16 - الجزائر العاصمة",
                    commune = "الشراقة",
                    category = "شقة",
                    transactionType = "للكراء",
                    rooms = "F3",
                    areaSqMeters = 85,
                    phone = "0553 11 22 33",
                    publisherName = "كريم بن علي",
                    publisherType = "باحث عن كراء",
                    featuresList = "ماء 24/24,غاز,حي هادئ",
                    isFavorite = false
                ),
                PropertyItem(
                    isOffer = false,
                    title = "ابحث عن قطعة أرض للبناء بوفاريك 150م²-200م²",
                    description = "دبرلي أرض: مشتري جاد بحوزته المبلغ كاش يبحث عن أرض بعقد توثيقي ودفتر عقاري بمدينة بوفاريك أو أولاد يعيش البليدة.",
                    priceDzd = 12000000,
                    priceDisplayCentimes = "حتى 1.2 مليار (1200 مليون)",
                    wilaya = "09 - البليدة",
                    commune = "بوفاريك",
                    category = "أرض",
                    transactionType = "للبيع",
                    rooms = "أرض",
                    areaSqMeters = 180,
                    phone = "0665 99 88 77",
                    publisherName = "عبد القادر البليدي",
                    publisherType = "مشتري جاد",
                    featuresList = "عقد توثيقي,دفتر عقاري",
                    isFavorite = true
                ),
                PropertyItem(
                    isOffer = false,
                    title = "مطلوب محل تجاري للكراء بوسط مدينة تيزي وزو",
                    description = "دبرلي محل: مطلوب محل تجاري مساحته بين 40م² و 70م² للكراء لفتح نشاط صيدلية أو تجارة ملابس بوسط المدينة.",
                    priceDzd = 80000,
                    priceDisplayCentimes = "حتى 8 ملايين/شهر",
                    wilaya = "15 - تيزي وزو",
                    commune = "تيزي وزو",
                    category = "محل تجاري",
                    transactionType = "للكراء",
                    rooms = "محل",
                    areaSqMeters = 50,
                    phone = "0772 33 44 55",
                    publisherName = "دحمان جرجرة",
                    publisherType = "مستأجر",
                    featuresList = "شارع حيوي,كهرباء 380V",
                    isFavorite = false
                ),
                PropertyItem(
                    isOffer = false,
                    title = "ابحث عن فيلا صغيرة أو منزل فردي للبيع ببومرداس",
                    description = "دبرلي فيلا: ابحث عن منزل فردي أو فيلا صغيرة بمدينة بومرداس أو بودواو، عقد توثيقي ودفتر عقاري، تحتوي مرآب سيارة.",
                    priceDzd = 35000000,
                    priceDisplayCentimes = "حتى 3.5 مليار (3500 مليون)",
                    wilaya = "35 - بومرداس",
                    commune = "بومرداس",
                    category = "فيلا",
                    transactionType = "للبيع",
                    rooms = "F5+",
                    areaSqMeters = 220,
                    phone = "0557 66 77 88",
                    publisherName = "أمين الجزائري",
                    publisherType = "مشتري جاد",
                    featuresList = "عقد توثيقي,مرآب,حديقة",
                    isFavorite = false
                )
            )
        }
    }
}
