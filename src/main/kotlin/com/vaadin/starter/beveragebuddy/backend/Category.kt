package com.vaadin.starter.beveragebuddy.backend

import com.github.vokorm.*
import com.gitlab.mvysny.jdbiorm.Dao

/**
 * Represents a beverage category.
 * @property id
 * @property name the category name
 */
class Category(override var id: Long? = null, var name: String = "") : KEntity<Long> {

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

    override fun toString() = "Category(id=$id, name='$name')"

    fun copy() = Category(id, name)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Category
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

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
