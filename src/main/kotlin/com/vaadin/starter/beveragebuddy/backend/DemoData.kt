package com.vaadin.starter.beveragebuddy.backend

import com.github.vokorm.db
import java.time.LocalDate
import kotlin.random.Random

internal object DemoData {

    private const val MINERAL_WATER = "Mineral Water"
    private const val SOFT_DRINK = "Soft Drink"
    private const val COFFEE = "Coffee"
    private const val TEA = "Tea"
    private const val DAIRY = "Dairy"
    private const val CIDER = "Cider"
    private const val BEER = "Beer"
    private const val WINE = "Wine"
    private const val OTHER = "Other"

    /**
     * Maps beverage name to a beverage category.
     */
    private val BEVERAGES: MutableMap<String, String> = LinkedHashMap()

    init {
        listOf("Evian",
                "Voss",
                "Veen",
                "San Pellegrino",
                "Perrier")
                .forEach { name -> BEVERAGES[name] = MINERAL_WATER }

        listOf("Coca-Cola",
                "Fanta",
                "Sprite")
                .forEach { name -> BEVERAGES[name] = SOFT_DRINK }

        listOf("Maxwell Ready-to-Drink Coffee",
                "Nescafé Gold",
                "Starbucks East Timor Tatamailau")
                .forEach { name -> BEVERAGES[name] = COFFEE }

        listOf("Prince Of Peace Organic White Tea",
                "Pai Mu Tan White Peony Tea",
                "Tazo Zen Green Tea",
                "Dilmah Sencha Green Tea",
                "Twinings Earl Grey",
                "Twinings Lady Grey",
                "Classic Indian Chai")
                .forEach { name -> BEVERAGES[name] = TEA }

        listOf("Cow's Milk",
                "Goat's Milk",
                "Unicorn's Milk",
                "Salt Lassi",
                "Mango Lassi",
                "Airag")
                .forEach { name -> BEVERAGES[name] = DAIRY }

        listOf("Crowmoor Extra Dry Apple",
                "Golden Cap Perry",
                "Somersby Blueberry",
                "Kopparbergs Naked Apple Cider",
                "Kopparbergs Raspberry",
                "Kingstone Press Wild Berry Flavoured Cider",
                "Crumpton Oaks Apple",
                "Frosty Jack's",
                "Ciderboys Mad Bark",
                "Angry Orchard Stone Dry",
                "Walden Hollow",
                "Fox Barrel Wit Pear")
                .forEach { name -> BEVERAGES[name] = CIDER }

        listOf("Budweiser",
                "Miller",
                "Heineken",
                "Holsten Pilsener",
                "Krombacher",
                "Weihenstephaner Hefeweissbier",
                "Ayinger Kellerbier",
                "Guinness Draught",
                "Kilkenny Irish Cream Ale",
                "Hoegaarden White",
                "Barbar",
                "Corsendonk Agnus Dei",
                "Leffe Blonde",
                "Chimay Tripel",
                "Duvel",
                "Pilsner Urquell",
                "Kozel",
                "Staropramen",
                "Lapin Kulta IVA",
                "Kukko Pils III",
                "Finlandia Sahti")
                .forEach { name -> BEVERAGES[name] = BEER }

        listOf("Jacob's Creek Classic Shiraz",
                "Chateau d’Yquem Sauternes",
                "Oremus Tokaji Aszú 5 Puttonyos")
                .forEach { name -> BEVERAGES[name] = WINE }

        listOf("Pan Galactic Gargle Blaster",
                "Mead",
                "Soma")
                .forEach { name -> BEVERAGES[name] = OTHER }
    }

    fun createDemoData() = db {
        // generate categories
        BEVERAGES.values.distinct().forEach { name -> Category(name = name).save() }

        /// generate reviews
        val reviewCount: Int = 20 + Random.nextInt(30)
        val beverages: List<MutableMap.MutableEntry<String, String>> = BEVERAGES.entries.toList()

        for (i in 0 until reviewCount) {
            val review = Review()
            val beverage: MutableMap.MutableEntry<String, String> = beverages.random()
            val category: Category = Category.getByName(beverage.value)
            review.name = beverage.key
            val testDay: LocalDate = LocalDate.of(
                1930 + Random.nextInt(88),
                1 + Random.nextInt(12),
                1 + Random.nextInt(28))
            review.date = testDay
            review.score = 1 + Random.nextInt(5)
            review.category = category.id
            review.count = 1 + Random.nextInt(15)
            review.save()
        }
    }
}
