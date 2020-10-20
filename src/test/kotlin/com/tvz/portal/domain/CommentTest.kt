package com.tvz.portal.domain

import com.tvz.portal.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CommentTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Comment::class)
        val comment1 = Comment()
        comment1.id = 1L
        val comment2 = Comment()
        comment2.id = comment1.id
        assertThat(comment1).isEqualTo(comment2)
        comment2.id = 2L
        assertThat(comment1).isNotEqualTo(comment2)
        comment1.id = null
        assertThat(comment1).isNotEqualTo(comment2)
    }
}
