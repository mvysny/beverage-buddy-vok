package com.vaadin.starter.beveragebuddy.backend

import com.github.vokorm.*
import com.gitlab.mvysny.jdbiorm.Dao
import com.gitlab.mvysny.jdbiorm.DaoOfJoin
import com.gitlab.mvysny.jdbiorm.TableProperty
import com.gitlab.mvysny.jdbiorm.condition.Condition
import com.gitlab.mvysny.jdbiorm.vaadin.EntityDataProvider
import java.time.LocalDate
import jakarta.validation.constraints.*
import org.jdbi.v3.core.mapper.Nested
import org.jdbi.v3.core.mapper.reflect.ColumnName
import java.io.Serializable

/**
 * Represents a beverage review.
 * @property name the beverage name
 * @property score the score, 1..5, 1 being worst, 5 being best
 * @property date when the review was done
 * @property category the beverage category [Category.id]. May be null if the category has been deleted.
 * @property count times tasted, 1..99
 */
data class Review(override var id: Long? = null,
                  
                  @field:NotNull
                  @field:Min(1)
                  @field:Max(5)
                  var score: Int = 1,

                  @field:NotBlank
                  @field:Size(min = 3)
                  var name: String = "",

                  @field:NotNull
                  @field:PastOrPresent
                  var date: LocalDate = LocalDate.now(),

                  @field:NotNull
                  var category: Long? = null,

                  @field:NotNull
                  @field:Min(1)
                  @field:Max(99)
                  var count: Int = 1) : KEntity<Long> {

    companion object : Dao<Review, Long>(Review::class.java) {
        val NAME = TableProperty.of<Review, String>(Review::class.java, "name")
        val SCORE = TableProperty.of<Review, Int>(Review::class.java, "score")
        val DATE = TableProperty.of<Review, LocalDate>(Review::class.java, "date")
        val CATEGORY = TableProperty.of<Review, Long?>(Review::class.java, "category")
        val COUNT = TableProperty.of<Review, Int>(Review::class.java, "count")
        /**
         * Computes the total sum of [count] for all reviews belonging to given [categoryId].
         * @return the total sum, 0 or greater.
         */
        fun getTotalCountForReviewsInCategory(categoryId: Long): Long = db {
            handle.createQuery("select sum(r.count) from Review r where r.category = :catId")
                    .bind("catId", categoryId)
                    .mapTo(Long::class.java).one() ?: 0L
        }
    }
}

/**
 * Holds the join of Review and its Category.
 * @property categoryName the [Category.name]
 */
data class ReviewWithCategory(
    @field:Nested
    var review: Review? = null,
    @field:ColumnName("categoryName")
    var categoryName: String? = null
) : Serializable {
    companion object {
        /**
         * Fetches the reviews matching the given filter text.
         *
         * This data provider provides sorting/paging/filtering and may be used for
         * SELECTs returning huge amount of data.
         */
        val dataProvider: EntityDataProvider<ReviewWithCategory>
            // we need to use SQL alias here, since both r.name and c.name exist and H2 would complain of a name clash.
        get() = EntityDataProvider(DaoOfJoin(ReviewWithCategory::class.java, """select Review.*, IFNULL(c.name, 'Undefined') as categoryName
                FROM Review left join Category c on Review.category = c.id"""))
    }
}

/**
 * This utility function returns a new loader which searches for given [filter] text
 * in all [Review] and [ReviewWithCategory] fields.
 */
fun EntityDataProvider<ReviewWithCategory>.setFilterText(filter: String?) {
    if (filter.isNullOrBlank()) {
        this.filter = Condition.NO_CONDITION
    } else {
        val normalizedFilter: String = filter.trim().lowercase() + "%"
        val c: Condition = buildCondition<ReviewWithCategory> {
            """Review.name ILIKE :filter
                    or IFNULL(c.name, 'Undefined') ILIKE :filter
                    or CAST(Review.score as VARCHAR) ILIKE :filter
                    or CAST(Review.count as VARCHAR) ILIKE :filter""".trimMargin()("filter" to normalizedFilter)
        }
        this.filter = c
    }
}
