package com.tvz.portal.web.rest

import com.tvz.portal.domain.Article
import com.tvz.portal.repository.ArticleRepository
import com.tvz.portal.web.rest.errors.BadRequestAlertException
import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.PaginationUtil
import io.github.jhipster.web.util.ResponseUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI
import java.net.URISyntaxException

private const val ENTITY_NAME = "article"
/**
 * REST controller for managing [com.tvz.portal.domain.Article].
 */
@RestController
@RequestMapping("/api")
@Transactional
class ArticleResource(
    private val articleRepository: ArticleRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /articles` : Create a new article.
     *
     * @param article the article to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new article, or with status `400 (Bad Request)` if the article has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/articles")
    fun createArticle(@RequestBody article: Article): ResponseEntity<Article> {
        log.debug("REST request to save Article : $article")
        if (article.id != null) {
            throw BadRequestAlertException(
                "A new article cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            )
        }
        val result = articleRepository.save(article)
        return ResponseEntity.created(URI("/api/articles/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /articles` : Updates an existing article.
     *
     * @param article the article to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated article,
     * or with status `400 (Bad Request)` if the article is not valid,
     * or with status `500 (Internal Server Error)` if the article couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/articles")
    fun updateArticle(@RequestBody article: Article): ResponseEntity<Article> {
        log.debug("REST request to update Article : $article")
        if (article.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = articleRepository.save(article)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    article.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /articles` : get all the articles.
     *
     * @param pageable the pagination information.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of articles in body.
     */
    @GetMapping("/articles")
    fun getAllArticles(pageable: Pageable): ResponseEntity<List<Article>> {
        log.debug("REST request to get a page of Articles")
        val page = articleRepository.findAll(pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /articles/:id` : get the "id" article.
     *
     * @param id the id of the article to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the article, or with status `404 (Not Found)`.
     */
    @GetMapping("/articles/{id}")
    fun getArticle(@PathVariable id: Long): ResponseEntity<Article> {
        log.debug("REST request to get Article : $id")
        val article = articleRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(article)
    }
    /**
     *  `DELETE  /articles/:id` : delete the "id" article.
     *
     * @param id the id of the article to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/articles/{id}")
    fun deleteArticle(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Article : $id")

        articleRepository.deleteById(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build()
    }

    @GetMapping("/articles/category/{category}")
    fun getCategoryArticles(@PathVariable category: String): ResponseEntity<List<Article>> {
        log.debug("REST request to get Articles for category = $category")
        val articles = articleRepository.findAllByCategory(category.toUpperCase())
        return ResponseEntity.ok().body(articles)
    }
}
