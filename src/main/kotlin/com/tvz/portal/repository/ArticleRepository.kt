package com.tvz.portal.repository

import com.tvz.portal.domain.Article
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Article] entity.
 */
@Suppress("unused")
@Repository
interface ArticleRepository : JpaRepository<Article, Long> {

    @Query("select article from Article article where article.author.login = ?#{principal.username}")
    fun findByAuthorIsCurrentUser(): MutableList<Article>
}
