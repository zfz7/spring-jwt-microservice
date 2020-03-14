package org.zfz7.auth.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.zfz7.auth.user.AuthorizedUserDetails
import org.zfz7.auth.user.AuthorizedUserDetailsService
import org.zfz7.auth.user.AuthorizedUserRepository


@EnableWebSecurity
class SecurityConfiguration : WebSecurityConfigurerAdapter(){

    @Autowired
    lateinit var authorizedUserDetailsService: AuthorizedUserDetailsService

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(authorizedUserDetailsService)
    }
    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
                .antMatchers("/new").permitAll()
                .antMatchers("/hello").authenticated()
            .and().formLogin()
    }
    @Bean
    fun passwordEncoder(): PasswordEncoder{
        return NoOpPasswordEncoder.getInstance()
    }
}