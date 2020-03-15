package org.zfz7.auth.controller

data class AuthenticationRequest(
        val username: String,
        val password: String
)