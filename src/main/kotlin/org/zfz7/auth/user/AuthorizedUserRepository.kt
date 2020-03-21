package org.zfz7.auth.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AuthorizedUserRepository : JpaRepository<AuthorizedUser, Long> {
    fun findByUsername(username: String): Optional<AuthorizedUser>
}