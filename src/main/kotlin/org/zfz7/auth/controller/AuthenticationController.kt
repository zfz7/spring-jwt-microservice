package org.zfz7.auth.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.zfz7.auth.user.AuthorizedUser
import org.zfz7.auth.user.AuthorizedUserDetailsService

@RestController
class AuthenticationController {
    @Autowired
    lateinit var authorizedUserDetailsService: AuthorizedUserDetailsService
    @GetMapping("/hello")
    fun home():String{
        return "Hello"
    }

    @GetMapping("/new")
    fun createNewUser():String{
        return try{
            authorizedUserDetailsService.createAuthorizedUser(
                    username = "user",
                    clearTextPassword = "pass",
                    roles = listOf("USER")
            )
            "Added"
        }catch(e:Exception){
            "Not added"
        }

    }
}