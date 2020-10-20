package com.tvz.portal.repository

import com.tvz.portal.domain.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Comment] entity.
 */
@Suppress("unused")
@Repository
interface CommentRepository : JpaRepository<Comment, Long> {

    @Query("select comment from Comment comment where comment.author.login = ?#{principal.username}")
    fun findByAuthorIsCurrentUser(): MutableList<Comment>
}
