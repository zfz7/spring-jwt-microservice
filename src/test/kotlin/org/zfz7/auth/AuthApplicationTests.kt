package org.zfz7.auth

import com.google.gson.Gson
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.zfz7.auth.controller.AuthenticationRequest
import org.zfz7.auth.user.AuthorizedUser
import org.zfz7.auth.user.AuthorizedUserRepository
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class AuthApplicationTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var authorizedUserRepository: AuthorizedUserRepository

    val password = "$2a$10\$cr9ssGgnhc08tdEofSsBFO.IyX.QeufxGfrQNV.SOmQS6jqlmHdWK"//Encoded password: "pass"
    val gson = Gson()

    @Test
    @DisplayName("Successfull login")
    fun authTest1() {
        every { authorizedUserRepository.findByUsername("test0") } returns Optional.of(AuthorizedUser("test0", password, listOf("USER")))
        every { authorizedUserRepository.findByUsername("test1") } returns Optional.of(AuthorizedUser("test1", password, listOf("USER")))
        val req1 = AuthenticationRequest("test0", "pass")
        val req2 = AuthenticationRequest("test1", "pass")
        val test1 = mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(gson.toJson(req1)))
                .andExpect(status().isOk)
                .andReturn()

        val test2 = mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(gson.toJson(req2)))
                .andExpect(status().isOk)
                .andReturn()
        assert(test1.response.contentAsString != test2.response.contentAsString)
    }

    @Test
    @DisplayName("Failed login")
    fun authTest2() {
        every { authorizedUserRepository.findByUsername("test0") } returns Optional.of(AuthorizedUser("test0", password, listOf("USER")))
        mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(gson.toJson(AuthenticationRequest("test0", "wrongpassword"))))
                .andExpect(status().isUnauthorized)
                .andReturn()

        mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(gson.toJson(AuthenticationRequest("wrongUser", "pass"))))
                .andExpect(status().isUnauthorized)
                .andReturn()
    }

    @Test
    @DisplayName("Create new user and login")
    fun authTest3() {
        every { authorizedUserRepository.save<AuthorizedUser>(any()) } returns AuthorizedUser("test3", password, listOf("USER"))
        every { authorizedUserRepository.findByUsername("test3") } returns Optional.of(AuthorizedUser("test3", password, listOf("USER")))

        mockMvc.perform(MockMvcRequestBuilders.post("/create_user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(gson.toJson(AuthenticationRequest("test3", "pass"))))
                .andExpect(status().isOk)
                .andReturn()

        mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(gson.toJson(AuthenticationRequest("test3", "pass"))))
                .andExpect(status().isOk)
                .andReturn()
    }

    @Test
    @DisplayName("Create new user: user exists")
    fun authTest4() {
        every { authorizedUserRepository.save<AuthorizedUser>(any()) } throws IllegalArgumentException("Username taken")

        mockMvc.perform(MockMvcRequestBuilders.post("/create_user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(gson.toJson(AuthenticationRequest("test3", "pass"))))
                .andExpect(status().isConflict)
                .andReturn()
    }

    @Test
    @DisplayName("Login and access content")
    fun authTest5() {
        every { authorizedUserRepository.findByUsername("test1") } returns Optional.of(AuthorizedUser("test1", password, listOf("USER")))
        val test1 = mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(gson.toJson(AuthenticationRequest("test1", "pass"))))
                .andExpect(status().isOk)
                .andReturn()
        val jwtToken = test1.response.contentAsString.split(":")[1].substring(1).dropLast(2)
        println(jwtToken)
        mockMvc.perform(MockMvcRequestBuilders.get("/hello")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer $jwtToken"))
                .andExpect(status().isOk)
                .andReturn()
    }

    @Test
    @DisplayName("Login and access content")
    fun authTest6() {
        every { authorizedUserRepository.findByUsername("test1") } returns Optional.of(AuthorizedUser("test1", password, listOf("USER")))
        val test1 = mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(gson.toJson(AuthenticationRequest("test1", "pass"))))
                .andExpect(status().isOk)
                .andReturn()
        val jwtToken = test1.response.contentAsString.split(":")[1].substring(1).dropLast(2)
        println(jwtToken)
        mockMvc.perform(MockMvcRequestBuilders.get("/hello")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer $jwtToken"))
                .andExpect(status().isOk)
                .andReturn()
    }

    @Test
    @DisplayName("Failed JWT Token")
    fun authTest7() {
        every { authorizedUserRepository.findByUsername("test1") } returns Optional.of(AuthorizedUser("test1", password, listOf("USER")))
        mockMvc.perform(MockMvcRequestBuilders.get("/hello")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer "))
                .andExpect(status().isForbidden)
                .andReturn()

        val invalidToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0MSIsImV4cCI6MTU4NDI5NjcxMSwiaWF0IjoxNTg0MjYwNzExfQ.eumW6coCIgV08gM6g9hVX9xEyQnKvIyOLqpnAAweB15"

        mockMvc.perform(MockMvcRequestBuilders.get("/hello")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer $invalidToken"))
                .andExpect(status().isForbidden)
                .andReturn()
    }
}
