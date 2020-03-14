package org.zfz7.auth.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


@Service
class AuthorizedUserDetailsService:UserDetailsService{

    @Autowired
    lateinit var authorizedUserRepository: AuthorizedUserRepository

    override fun loadUserByUsername(username: String?): UserDetails? {
        username?.let{
            val user = authorizedUserRepository.findByUsername(username)
            user.orElseThrow{ UsernameNotFoundException("No user found for:$username") }
            return AuthorizedUserDetails(user.get())
        }?: return null
    }

    @Throws(IllegalArgumentException::class)
    fun createAuthorizedUser(username: String, clearTextPassword: String, roles: List<String>){
        val newUser = AuthorizedUser(username,clearTextPassword, roles)
        authorizedUserRepository.save(newUser)

    }


}