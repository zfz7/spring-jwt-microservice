package org.zfz7.auth.user

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class AuthorizedUserDetails: UserDetails {
     var user: AuthorizedUser

    constructor(user: AuthorizedUser){
        this.user = user;
    }
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        var authorities = mutableListOf<SimpleGrantedAuthority>()
        user.roles.forEach{authorities.add(SimpleGrantedAuthority("ROLE_$it"))}
        return authorities
    }

    override fun isEnabled(): Boolean {
        return user.isEnabled;
    }

    override fun getUsername(): String {
        return user.username
    }
    override fun isCredentialsNonExpired(): Boolean {
        return user.isNonExpired
    }

    override fun getPassword(): String {
        return user.password
    }

    override fun isAccountNonExpired(): Boolean {
        return user.isNonExpired
    }

    override fun isAccountNonLocked(): Boolean {
        return user.isNonLocked
    }

}