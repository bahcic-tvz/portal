package com.tvz.portal.web.rest

import com.tvz.portal.domain.Article
import com.tvz.portal.domain.Comment
import com.tvz.portal.domain.enumeration.Category
import com.tvz.portal.repository.ArticleRepository
import com.tvz.portal.repository.CommentRepository
import com.tvz.portal.service.UserService
import io.github.jhipster.web.util.ResponseUtil
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.time.Instant

/**
 * REST controller for unauthorized users
 */
@RestController
@RequestMapping("/api/no-auth/")
@Transactional
class UnauthorizedResource(
    private val articleRepository: ArticleRepository,
    private val commentRepository: CommentRepository,
    private val userService: UserService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/articles/category/{category}")
    fun getCategoryArticles(@PathVariable category: String): ResponseEntity<List<Article>> {
        log.debug("REST request to get Articles for category = $category")
        val articles = articleRepository.findAllByCategoryOrderByIsHeroDesc(Category.valueOf(category.toUpperCase()))
        return ResponseEntity.ok().body(articles)
    }

    @GetMapping("/articles/{id}")
    fun getArticle(@PathVariable id: Long): ResponseEntity<Article> {
        log.debug("REST request to get Article = $id")
        val article = articleRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(article)
    }

    @PostMapping("/comments")
    fun createComment(@RequestBody comment: CommentDTO) {
        log.debug("REST request to save Comment : $comment")
        val user = userService.getUserWithAuthorities().get()
        val article = articleRepository.findById(comment.articleId).get()
        commentRepository.save(
            Comment(
                posted = Instant.now(),
                content = comment.content,
                article = article,
                author = user
            )
        )
    }

    @GetMapping("/comments/article/{articleId}")
    fun getAllComments(@PathVariable articleId: Long): ResponseEntity<List<Comment>> {
        log.debug("REST request to get a page of Comments")
        val comments = commentRepository.findAllByArticleId(articleId)
        return ResponseEntity.ok().body(comments)
    }

    data class CommentDTO(
        val content: String,
        val articleId: Long
    )
}
