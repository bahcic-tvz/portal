package com.tvz.portal.web.rest

import com.tvz.portal.PortalApp
import com.tvz.portal.domain.Article
import com.tvz.portal.domain.enumeration.Category
import com.tvz.portal.repository.ArticleRepository
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
 * Integration tests for the [ArticleResource] REST controller.
 *
 * @see ArticleResource
 */
@SpringBootTest(classes = [PortalApp::class])
@AutoConfigureMockMvc
@WithMockUser
class ArticleResourceIT {

    @Autowired
    private lateinit var articleRepository: ArticleRepository

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

    private lateinit var restArticleMockMvc: MockMvc

    private lateinit var article: Article

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val articleResource = ArticleResource(articleRepository)
        this.restArticleMockMvc = MockMvcBuilders.standaloneSetup(articleResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        article = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createArticle() {
        val databaseSizeBeforeCreate = articleRepository.findAll().size

        // Create the Article
        restArticleMockMvc.perform(
            post("/api/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(article))
        ).andExpect(status().isCreated)

        // Validate the Article in the database
        val articleList = articleRepository.findAll()
        assertThat(articleList).hasSize(databaseSizeBeforeCreate + 1)
        val testArticle = articleList[articleList.size - 1]
        assertThat(testArticle.category).isEqualTo(DEFAULT_CATEGORY)
        assertThat(testArticle.posted).isEqualTo(DEFAULT_POSTED)
        assertThat(testArticle.title).isEqualTo(DEFAULT_TITLE)
        assertThat(testArticle.tag).isEqualTo(DEFAULT_TAG)
        assertThat(testArticle.summary).isEqualTo(DEFAULT_SUMMARY)
        assertThat(testArticle.photoURL).isEqualTo(DEFAULT_PHOTO_URL)
        assertThat(testArticle.photoAuthor).isEqualTo(DEFAULT_PHOTO_AUTHOR)
        assertThat(testArticle.poweredBy).isEqualTo(DEFAULT_POWERED_BY)
        assertThat(testArticle.poweredByURL).isEqualTo(DEFAULT_POWERED_BY_URL)
        assertThat(testArticle.content).isEqualTo(DEFAULT_CONTENT)
        assertThat(testArticle.isHero).isEqualTo(DEFAULT_IS_HERO)
    }

    @Test
    @Transactional
    fun createArticleWithExistingId() {
        val databaseSizeBeforeCreate = articleRepository.findAll().size

        // Create the Article with an existing ID
        article.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restArticleMockMvc.perform(
            post("/api/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(article))
        ).andExpect(status().isBadRequest)

        // Validate the Article in the database
        val articleList = articleRepository.findAll()
        assertThat(articleList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllArticles() {
        // Initialize the database
        articleRepository.saveAndFlush(article)

        // Get all the articleList
        restArticleMockMvc.perform(get("/api/articles?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(article.id?.toInt())))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].posted").value(hasItem(DEFAULT_POSTED.toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].tag").value(hasItem(DEFAULT_TAG)))
            .andExpect(jsonPath("$.[*].summary").value(hasItem(DEFAULT_SUMMARY)))
            .andExpect(jsonPath("$.[*].photoURL").value(hasItem(DEFAULT_PHOTO_URL)))
            .andExpect(jsonPath("$.[*].photoAuthor").value(hasItem(DEFAULT_PHOTO_AUTHOR)))
            .andExpect(jsonPath("$.[*].poweredBy").value(hasItem(DEFAULT_POWERED_BY)))
            .andExpect(jsonPath("$.[*].poweredByURL").value(hasItem(DEFAULT_POWERED_BY_URL)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].isHero").value(hasItem(DEFAULT_IS_HERO)))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getArticle() {
        // Initialize the database
        articleRepository.saveAndFlush(article)

        val id = article.id
        assertNotNull(id)

        // Get the article
        restArticleMockMvc.perform(get("/api/articles/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(article.id?.toInt()))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY.toString()))
            .andExpect(jsonPath("$.posted").value(DEFAULT_POSTED.toString()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.tag").value(DEFAULT_TAG))
            .andExpect(jsonPath("$.summary").value(DEFAULT_SUMMARY))
            .andExpect(jsonPath("$.photoURL").value(DEFAULT_PHOTO_URL))
            .andExpect(jsonPath("$.photoAuthor").value(DEFAULT_PHOTO_AUTHOR))
            .andExpect(jsonPath("$.poweredBy").value(DEFAULT_POWERED_BY))
            .andExpect(jsonPath("$.poweredByURL").value(DEFAULT_POWERED_BY_URL))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.isHero").value(DEFAULT_IS_HERO))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingArticle() {
        // Get the article
        restArticleMockMvc.perform(get("/api/articles/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateArticle() {
        // Initialize the database
        articleRepository.saveAndFlush(article)

        val databaseSizeBeforeUpdate = articleRepository.findAll().size

        // Update the article
        val id = article.id
        assertNotNull(id)
        val updatedArticle = articleRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedArticle are not directly saved in db
        em.detach(updatedArticle)
        updatedArticle.category = UPDATED_CATEGORY
        updatedArticle.posted = UPDATED_POSTED
        updatedArticle.title = UPDATED_TITLE
        updatedArticle.tag = UPDATED_TAG
        updatedArticle.summary = UPDATED_SUMMARY
        updatedArticle.photoURL = UPDATED_PHOTO_URL
        updatedArticle.photoAuthor = UPDATED_PHOTO_AUTHOR
        updatedArticle.poweredBy = UPDATED_POWERED_BY
        updatedArticle.poweredByURL = UPDATED_POWERED_BY_URL
        updatedArticle.content = UPDATED_CONTENT
        updatedArticle.isHero = UPDATED_IS_HERO

        restArticleMockMvc.perform(
            put("/api/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedArticle))
        ).andExpect(status().isOk)

        // Validate the Article in the database
        val articleList = articleRepository.findAll()
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate)
        val testArticle = articleList[articleList.size - 1]
        assertThat(testArticle.category).isEqualTo(UPDATED_CATEGORY)
        assertThat(testArticle.posted).isEqualTo(UPDATED_POSTED)
        assertThat(testArticle.title).isEqualTo(UPDATED_TITLE)
        assertThat(testArticle.tag).isEqualTo(UPDATED_TAG)
        assertThat(testArticle.summary).isEqualTo(UPDATED_SUMMARY)
        assertThat(testArticle.photoURL).isEqualTo(UPDATED_PHOTO_URL)
        assertThat(testArticle.photoAuthor).isEqualTo(UPDATED_PHOTO_AUTHOR)
        assertThat(testArticle.poweredBy).isEqualTo(UPDATED_POWERED_BY)
        assertThat(testArticle.poweredByURL).isEqualTo(UPDATED_POWERED_BY_URL)
        assertThat(testArticle.content).isEqualTo(UPDATED_CONTENT)
        assertThat(testArticle.isHero).isEqualTo(UPDATED_IS_HERO)
    }

    @Test
    @Transactional
    fun updateNonExistingArticle() {
        val databaseSizeBeforeUpdate = articleRepository.findAll().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restArticleMockMvc.perform(
            put("/api/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(article))
        ).andExpect(status().isBadRequest)

        // Validate the Article in the database
        val articleList = articleRepository.findAll()
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteArticle() {
        // Initialize the database
        articleRepository.saveAndFlush(article)

        val databaseSizeBeforeDelete = articleRepository.findAll().size

        // Delete the article
        restArticleMockMvc.perform(
            delete("/api/articles/{id}", article.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val articleList = articleRepository.findAll()
        assertThat(articleList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private val DEFAULT_CATEGORY: Category = Category.NEWS
        private val UPDATED_CATEGORY: Category = Category.SHOW

        private val DEFAULT_POSTED: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_POSTED: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private const val DEFAULT_TITLE = "AAAAAAAAAA"
        private const val UPDATED_TITLE = "BBBBBBBBBB"

        private const val DEFAULT_TAG = "AAAAAAAAAA"
        private const val UPDATED_TAG = "BBBBBBBBBB"

        private const val DEFAULT_SUMMARY = "AAAAAAAAAA"
        private const val UPDATED_SUMMARY = "BBBBBBBBBB"

        private const val DEFAULT_PHOTO_URL = "AAAAAAAAAA"
        private const val UPDATED_PHOTO_URL = "BBBBBBBBBB"

        private const val DEFAULT_PHOTO_AUTHOR = "AAAAAAAAAA"
        private const val UPDATED_PHOTO_AUTHOR = "BBBBBBBBBB"

        private const val DEFAULT_POWERED_BY = "AAAAAAAAAA"
        private const val UPDATED_POWERED_BY = "BBBBBBBBBB"

        private const val DEFAULT_POWERED_BY_URL = "AAAAAAAAAA"
        private const val UPDATED_POWERED_BY_URL = "BBBBBBBBBB"

        private const val DEFAULT_CONTENT = "AAAAAAAAAA"
        private const val UPDATED_CONTENT = "BBBBBBBBBB"

        private const val DEFAULT_IS_HERO: Boolean = false
        private const val UPDATED_IS_HERO: Boolean = true

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Article {
            val article = Article(
                category = DEFAULT_CATEGORY,
                posted = DEFAULT_POSTED,
                title = DEFAULT_TITLE,
                tag = DEFAULT_TAG,
                summary = DEFAULT_SUMMARY,
                photoURL = DEFAULT_PHOTO_URL,
                photoAuthor = DEFAULT_PHOTO_AUTHOR,
                poweredBy = DEFAULT_POWERED_BY,
                poweredByURL = DEFAULT_POWERED_BY_URL,
                content = DEFAULT_CONTENT,
                isHero = DEFAULT_IS_HERO
            )

            return article
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Article {
            val article = Article(
                category = UPDATED_CATEGORY,
                posted = UPDATED_POSTED,
                title = UPDATED_TITLE,
                tag = UPDATED_TAG,
                summary = UPDATED_SUMMARY,
                photoURL = UPDATED_PHOTO_URL,
                photoAuthor = UPDATED_PHOTO_AUTHOR,
                poweredBy = UPDATED_POWERED_BY,
                poweredByURL = UPDATED_POWERED_BY_URL,
                content = UPDATED_CONTENT,
                isHero = UPDATED_IS_HERO
            )

            return article
        }
    }
}
