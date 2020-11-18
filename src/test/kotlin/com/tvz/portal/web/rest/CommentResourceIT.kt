package com.tvz.portal.web.rest

import com.tvz.portal.PortalApp
import com.tvz.portal.domain.Comment
import com.tvz.portal.repository.CommentRepository
import com.tvz.portal.web.rest.errors.ExceptionTranslator
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.persistence.EntityManager
import kotlin.test.assertNotNull

/**
 * Integration tests for the [CommentResource] REST controller.
 *
 * @see CommentResource
 */
@SpringBootTest(classes = [PortalApp::class])
@AutoConfigureMockMvc
@WithMockUser
class CommentResourceIT {

    @Autowired
    private lateinit var commentRepository: CommentRepository

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var exceptionTranslator: ExceptionTranslator

    @Autowired
    private lateinit var validator: Validator

    @Autowired
    private lateinit var em: EntityManager

    private lateinit var restCommentMockMvc: MockMvc

    private lateinit var comment: Comment

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val commentResource = CommentResource(commentRepository)
        this.restCommentMockMvc = MockMvcBuilders.standaloneSetup(commentResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        comment = createEntity()
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createComment() {
        val databaseSizeBeforeCreate = commentRepository.findAll().size

        // Create the Comment
        restCommentMockMvc.perform(
            post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(comment))
        ).andExpect(status().isCreated)

        // Validate the Comment in the database
        val commentList = commentRepository.findAll()
        assertThat(commentList).hasSize(databaseSizeBeforeCreate + 1)
        val testComment = commentList[commentList.size - 1]
        assertThat(testComment.posted).isEqualTo(DEFAULT_POSTED)
        assertThat(testComment.content).isEqualTo(DEFAULT_CONTENT)
    }

    @Test
    @Transactional
    fun createCommentWithExistingId() {
        val databaseSizeBeforeCreate = commentRepository.findAll().size

        // Create the Comment with an existing ID
        comment.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restCommentMockMvc.perform(
            post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(comment))
        ).andExpect(status().isBadRequest)

        // Validate the Comment in the database
        val commentList = commentRepository.findAll()
        assertThat(commentList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllComments() {
        // Initialize the database
        commentRepository.saveAndFlush(comment)

        // Get all the commentList
        restCommentMockMvc.perform(get("/api/comments?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(comment.id?.toInt())))
            .andExpect(jsonPath("$.[*].posted").value(hasItem(DEFAULT_POSTED.toString())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getComment() {
        // Initialize the database
        commentRepository.saveAndFlush(comment)

        val id = comment.id
        assertNotNull(id)

        // Get the comment
        restCommentMockMvc.perform(get("/api/comments/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(comment.id?.toInt() as Any))
            .andExpect(jsonPath("$.posted").value(DEFAULT_POSTED.toString()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingComment() {
        // Get the comment
        restCommentMockMvc.perform(get("/api/comments/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateComment() {
        // Initialize the database
        commentRepository.saveAndFlush(comment)

        val databaseSizeBeforeUpdate = commentRepository.findAll().size

        // Update the comment
        val id = comment.id
        assertNotNull(id)
        val updatedComment = commentRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedComment are not directly saved in db
        em.detach(updatedComment)
        updatedComment.posted = UPDATED_POSTED
        updatedComment.content = UPDATED_CONTENT

        restCommentMockMvc.perform(
            put("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedComment))
        ).andExpect(status().isOk)

        // Validate the Comment in the database
        val commentList = commentRepository.findAll()
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate)
        val testComment = commentList[commentList.size - 1]
        assertThat(testComment.posted).isEqualTo(UPDATED_POSTED)
        assertThat(testComment.content).isEqualTo(UPDATED_CONTENT)
    }

    @Test
    @Transactional
    fun updateNonExistingComment() {
        val databaseSizeBeforeUpdate = commentRepository.findAll().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommentMockMvc.perform(
            put("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(comment))
        ).andExpect(status().isBadRequest)

        // Validate the Comment in the database
        val commentList = commentRepository.findAll()
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteComment() {
        // Initialize the database
        commentRepository.saveAndFlush(comment)

        val databaseSizeBeforeDelete = commentRepository.findAll().size

        // Delete the comment
        restCommentMockMvc.perform(
            delete("/api/comments/{id}", comment.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val commentList = commentRepository.findAll()
        assertThat(commentList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private val DEFAULT_POSTED: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_POSTED: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private const val DEFAULT_CONTENT = "AAAAAAAAAA"
        private const val UPDATED_CONTENT = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(): Comment {
            val comment = Comment(
                posted = DEFAULT_POSTED,
                content = DEFAULT_CONTENT
            )

            return comment
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
//        @JvmStatic
//        fun createUpdatedEntity(): Comment {
//            val comment = Comment(
//                posted = UPDATED_POSTED,
//                content = UPDATED_CONTENT
//            )
//
//            return comment
//        }
    }
}
