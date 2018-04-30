package com.vaadin.starter.beveragebuddy.backend

import com.github.vok.framework.sql2o.vaadin.*
import com.github.vokorm.DaoOfAny
import com.github.vokorm.Entity
import com.github.vokorm.db
import java.time.LocalDate
import javax.validation.constraints.*

/**
 * Represents a beverage review.
 * @property name the beverage name
 * @property score the score, 1..5, 1 being worst, 5 being best
 * @property date when the review was done
 * @property category the beverage category [Category.id]
 * @property count times tasted, 1..99
 */
// must be open: https://github.com/vaadin/flow/issues/2636
open class Review(override var id: Long? = null,
                  
                  @field:NotNull
                  @field:Min(1)
                  @field:Max(5)
                  open var score: Int = 1,

                  @field:NotBlank
                  @field:Size(min = 3)
                  open var name: String = "",

                  @field:NotNull
                  @field:PastOrPresent
                  open var date: LocalDate = LocalDate.now(),

                  open var category: Long? = null,

                  @field:NotNull
                  @field:Min(1)
                  @field:Max(99)
                  open var count: Int = 1) : Entity<Long> {
    override fun toString() = "${javaClass.simpleName}(id=$id, score=$score, name='$name', date=$date, category=$category, count=$count)"

    fun copy() = Review(id, score, name, date, category, count)

    companion object : DaoOfAny<Review> {
        /**
         * Computes the total sum of [count] for all reviews belonging to given [categoryId].
         * @return the total sum, 0 or greater.
         */
        fun getTotalCountForReviewsInCategory(categoryId: Long): Long = db {
            con.createQuery("select sum(r.count) from Review r where r.category = :catId")
                    .addParameter("catId", categoryId)
                    .executeScalar(Long::class.java) ?: 0L
        }
    }
}

/**
 * Holds the join of Review and its Category.
 * @property categoryName the [Category.name]
 */
// must be open - Flow requires non-final classes for ModelProxy. Also can't have constructors: https://github.com/mvysny/karibu-dsl/issues/3
open class ReviewWithCategory : Review() {
    open var categoryName: String? = null
    override fun toString() = super.toString() + "(category $categoryName)"
    companion object {
        /**
         * Fetches the reviews matching the given filter text.
         *
         * The matching is case insensitive. When passed an empty filter text,
         * the method returns all categories. The returned list is ordered
         * by name.
         *
         * This data provider provides sorting/paging/filtering and may be used for
         * SELECTs returning huge amount of data.
         */
        val dataProvider: VokDataProvider<ReviewWithCategory>
        get() = sqlDataProvider(ReviewWithCategory::class.java, """select r.*, IFNULL(c.name, 'Undefined') as categoryName
                FROM Review r left join Category c on r.category = c.id
                WHERE 1=1 {{WHERE}} ORDER BY 1=1{{ORDER}} {{PAGING}}""", idMapper = { it.id!! })
    }
}

/**
 * This utility function sets a *global filter* to the data provider. Global filter searches for given [filter] text
 * in all [Review] and [ReviewWithCategory] fields.
 *
 * This function is compatible with data providers as returned by [ReviewWithCategory.dataProvider].
 */
fun VokDataProvider<ReviewWithCategory>.setFilterText(filter: String?) {
    if (filter.isNullOrBlank()) {
        setFilter(null)
    } else {
        val normalizedFilter = filter!!.trim().toLowerCase() + "%"
        setFilter {
            """r.name ILIKE :filter
                    or IFNULL(c.name, 'Undefined') ILIKE :filter
                    or CAST(r.score as VARCHAR) ILIKE :filter
                    or CAST(r.count as VARCHAR) ILIKE :filter""".trimMargin()("filter" to normalizedFilter)
        }
    }
}
