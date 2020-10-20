package com.tvz.portal.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import java.time.Instant
import javax.persistence.*

/**
 * A Comment.
 */
@Entity
@Table(name = "comment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,
    @Column(name = "posted")
    var posted: Instant? = null,

    @Column(name = "content")
    var content: String? = null,

    @ManyToOne @JsonIgnoreProperties(value = ["comments"], allowSetters = true)
    var author: User? = null,

    @ManyToOne @JsonIgnoreProperties(value = ["comments"], allowSetters = true)
    var article: Article? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Comment) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Comment{" +
        "id=$id" +
        ", posted='$posted'" +
        ", content='$content'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
