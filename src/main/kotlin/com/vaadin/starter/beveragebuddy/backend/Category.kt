package com.vaadin.starter.beveragebuddy.backend

import com.github.vokorm.*
import com.gitlab.mvysny.jdbiorm.Dao
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

/**
 * Represents a beverage category.
 * @property id
 * @property name the category name
 */
data class Category(
    override var id: Long? = null,

    @field:NotBlank
    var name: String = ""
) : KEntity<Long> {

    companion object : Dao<Category, Long>(Category::class.java) {
        fun findByName(name: String): Category? = findSingleBy { Category::name eq name }
        fun getByName(name: String): Category = singleBy { Category::name eq name }
        fun existsWithName(name: String): Boolean = findByName(name) != null
        override fun deleteAll() {
            db {
                handle.createUpdate("update Review set category = NULL").execute()
                super.deleteAll()
            }
        }
    }

    override fun delete() {
        db {
            if (id != null) {
                handle.createUpdate("update Review set category = NULL where category=:catId")
                        .bind("catId", id!!)
                        .execute()
            }
            super.delete()
        }
    }
}
