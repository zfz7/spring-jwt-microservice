package org.zfz7.auth.filter

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.zfz7.auth.user.AuthorizedUserDetailsService
import org.zfz7.auth.utils.JwtUtil
import java.lang.Exception
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
@Component
class JwtRequestFilter : OncePerRequestFilter(){
    @Autowired
    lateinit var authorizedUserDetailsService: AuthorizedUserDetailsService
    @Autowired
    lateinit var jwtUtil: JwtUtil

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val authorizationHeader = request.getHeader("Authorization")
        var username = ""
        var jwt = ""
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            jwt = authorizationHeader.substring(7)
            try{
                username = jwtUtil.extractUsername(jwt)
            }catch(e: Exception){
                println("$jwt : $e")
                filterChain.doFilter(request,response)
                return
            }
        }
        if(username != "" && SecurityContextHolder.getContext().authentication == null){
            val userDetails = authorizedUserDetailsService.loadUserByUsername(username)

            if(jwtUtil.validateToken(jwt,userDetails)){
                val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(userDetails,null,userDetails?.authorities)
                usernamePasswordAuthenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
            }
        }
        filterChain.doFilter(request,response)
    }

}