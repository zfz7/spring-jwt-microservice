package org.zfz7.auth.controller

import org.zfz7.auth.utils.JwtUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.zfz7.auth.user.AuthorizedUserDetailsService

@RestController
class AuthenticationController {
    @Autowired
    lateinit var authorizedUserDetailsService: AuthorizedUserDetailsService

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @Autowired
    lateinit var jwtUtil: JwtUtil

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @GetMapping("/hello")
    fun home():String{
        return "Hello"
    }

    @PostMapping("/create_user")
    fun createNewUser(@RequestBody potentialUser: AuthenticationRequest):ResponseEntity<String>{
        return try{
            authorizedUserDetailsService.createAuthorizedUser(
                    username = potentialUser.username,
                    hashedPassword = passwordEncoder.encode(potentialUser.password),
                    roles = listOf("USER")
            )
            ResponseEntity.status(HttpStatus.OK).body("Added")
        }catch(e:Exception){
            println(potentialUser.username + ":" +e)
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username taken")
        }
    }
    @PostMapping("/authenticate")
    fun authenticate(@RequestBody potentialUser: AuthenticationRequest): ResponseEntity<AuthenticationResponse> {
        try{
            authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(potentialUser.username,potentialUser.password))
        }catch(e: Exception){
            println(potentialUser.username + ":" +e)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(AuthenticationResponse(""))
        }
        val authorizedUser = authorizedUserDetailsService.loadUserByUsername(potentialUser.username)
        val jwt = jwtUtil.generateToken(authorizedUser)
        return ResponseEntity.ok(AuthenticationResponse(jwt))
    }
}