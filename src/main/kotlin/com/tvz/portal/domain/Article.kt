package com.tvz.portal.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.tvz.portal.domain.enumeration.Category
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import java.time.Instant
import javax.persistence.*

/**
 * A Article.
 */
@Entity
@Table(name = "article")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Article(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    var category: Category? = null,

    @Column(name = "posted")
    var posted: Instant? = null,

    @Column(name = "title")
    var title: String? = null,

    @Column(name = "tag")
    var tag: String? = null,

    @Column(name = "summary")
    var summary: String? = null,

    @Column(name = "photo_url")
    var photoURL: String? = null,

    @Column(name = "photo_author")
    var photoAuthor: String? = null,

    @Column(name = "powered_by")
    var poweredBy: String? = null,

    @Column(name = "powered_by_url")
    var poweredByURL: String? = null,

    @Column(name = "content")
    var content: String? = null,

    @Column(name = "is_hero")
    var isHero: Boolean? = null,

    @OneToMany(mappedBy = "article")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    var comments: MutableSet<Comment>? = mutableSetOf(),

    @ManyToOne @JsonIgnoreProperties(value = ["articles"], allowSetters = true)
    var author: User? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    fun addComments(comment: Comment): Article {
        this.comments!!.add(comment)
        comment.article = this
        return this
    }

    fun removeComments(comment: Comment): Article {
        this.comments!!.remove(comment)
        comment.article = null
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Article) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Article{" +
        "id=$id" +
        ", category='$category'" +
        ", posted='$posted'" +
        ", title='$title'" +
        ", tag='$tag'" +
        ", summary='$summary'" +
        ", photoURL='$photoURL'" +
        ", photoAuthor='$photoAuthor'" +
        ", poweredBy='$poweredBy'" +
        ", poweredByURL='$poweredByURL'" +
        ", content='$content'" +
        ", isHero='$isHero'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
