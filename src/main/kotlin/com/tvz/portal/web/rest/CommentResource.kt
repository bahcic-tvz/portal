package com.tvz.portal.web.rest

import com.tvz.portal.domain.Comment
import com.tvz.portal.repository.CommentRepository
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

private const val ENTITY_NAME = "comment"
/**
 * REST controller for managing [com.tvz.portal.domain.Comment].
 */
@RestController
@RequestMapping("/api")
@Transactional
class CommentResource(
    private val commentRepository: CommentRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /comments` : Create a new comment.
     *
     * @param comment the comment to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new comment, or with status `400 (Bad Request)` if the comment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/comments")
    fun createComment(@RequestBody comment: Comment): ResponseEntity<Comment> {
        log.debug("REST request to save Comment : $comment")
        if (comment.id != null) {
            throw BadRequestAlertException(
                "A new comment cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            )
        }
        val result = commentRepository.save(comment)
        return ResponseEntity.created(URI("/api/comments/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /comments` : Updates an existing comment.
     *
     * @param comment the comment to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated comment,
     * or with status `400 (Bad Request)` if the comment is not valid,
     * or with status `500 (Internal Server Error)` if the comment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/comments")
    fun updateComment(@RequestBody comment: Comment): ResponseEntity<Comment> {
        log.debug("REST request to update Comment : $comment")
        if (comment.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = commentRepository.save(comment)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    false,
                    ENTITY_NAME,
                    comment.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /comments` : get all the comments.
     *
     * @param pageable the pagination information.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of comments in body.
     */
    @GetMapping("/comments")
    fun getAllComments(pageable: Pageable): ResponseEntity<List<Comment>> {
        log.debug("REST request to get a page of Comments")
        val page = commentRepository.findAll(pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /comments/:id` : get the "id" comment.
     *
     * @param id the id of the comment to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the comment, or with status `404 (Not Found)`.
     */
    @GetMapping("/comments/{id}")
    fun getComment(@PathVariable id: Long): ResponseEntity<Comment> {
        log.debug("REST request to get Comment : $id")
        val comment = commentRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(comment)
    }
    /**
     *  `DELETE  /comments/:id` : delete the "id" comment.
     *
     * @param id the id of the comment to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/comments/{id}")
    fun deleteComment(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Comment : $id")

        commentRepository.deleteById(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build()
    }
}
