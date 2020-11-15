package com.tvz.portal.web.rest

import com.tvz.portal.domain.Article
import com.tvz.portal.repository.ArticleRepository
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

/**
 * REST controller for unauthorized users
 */
@RestController
@RequestMapping("/api/no-auth/")
@Transactional
class UnauthorizedResource(
    private val articleRepository: ArticleRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/articles/category/{category}")
    fun getCategoryArticles(@PathVariable category: String): ResponseEntity<List<Article>> {
        log.debug("REST request to get Articles for category = $category")
        val articles = articleRepository.findAllByCategory(category.toUpperCase())
        return ResponseEntity.ok().body(articles)
    }
}
