package org.zfz7.auth

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
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
	@BeforeEach
	fun setup() {


	}
	@AfterEach
	fun cleanUp(){

	}
	@Test
	@DisplayName("Successfull login")
	fun authTest1() {
		every { authorizedUserRepository.findByUsername("test0")} returns Optional.of(AuthorizedUser("test0",password,listOf("USER")))
		every { authorizedUserRepository.findByUsername("test1")} returns Optional.of(AuthorizedUser("test1",password,listOf("USER")))
		val test1 = mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("utf-8")
						.content("{\"username\":\"test0\",\"password\":\"pass\"}"))
						.andExpect(status().isOk)
						.andReturn()

		val test2 =mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("utf-8")
						.content("{\"username\":\"test1\",\"password\":\"pass\"}"))
						.andExpect(status().isOk)
						.andReturn()
		assert(test1.response.contentAsString!=test2.response.contentAsString)
	}
	@Test
	@DisplayName("Failed login")
	fun authTest2() {
		every { authorizedUserRepository.findByUsername("test0")} returns Optional.of(AuthorizedUser("test0",password,listOf("USER")))
		mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("utf-8")
						.content("{\"username\":\"test0\",\"password\":\"wrongpassword\"}"))
				.andExpect(status().isUnauthorized)
				.andReturn()

		mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("utf-8")
						.content("{\"username\":\"wrongUser\",\"password\":\"pass\"}"))
				.andExpect(status().isUnauthorized)
				.andReturn()
	}

	@Test
	@DisplayName("Create new user and login")
	fun authTest3() {
		every { authorizedUserRepository.save<AuthorizedUser>(any())} returns AuthorizedUser("test3",password,listOf("USER"))
		every { authorizedUserRepository.findByUsername("test3")} returns Optional.of(AuthorizedUser("test3",password,listOf("USER")))


		mockMvc.perform(MockMvcRequestBuilders.post("/create_user")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("utf-8")
						.content("{\"username\":\"test3\",\"password\":\"pass\"}"))
				.andExpect(status().isOk)
				.andReturn()

		mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("utf-8")
						.content("{\"username\":\"test3\",\"password\":\"pass\"}"))
				.andExpect(status().isOk)
				.andReturn()
	}
	@Test
	@DisplayName("Create new user: user exists")
	fun authTest4() {
		every { authorizedUserRepository.save<AuthorizedUser>(any())} throws IllegalArgumentException("Username taken")

		mockMvc.perform(MockMvcRequestBuilders.post("/create_user")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("utf-8")
						.content("{\"username\":\"test3\",\"password\":\"pass\"}"))
				.andExpect(status().isConflict)
				.andReturn()
	}

	@Test
	@DisplayName("Login and access content")
	fun authTest5() {
		every { authorizedUserRepository.findByUsername("test1")} returns Optional.of(AuthorizedUser("test1",password,listOf("USER")))
		val test1 = mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("utf-8")
						.content("{\"username\":\"test1\",\"password\":\"pass\"}"))
				.andExpect(status().isOk)
				.andReturn()
		val jwtToken = test1.response.contentAsString.split(":")[1].substring(1).dropLast(2)
		println(jwtToken)
		mockMvc.perform(MockMvcRequestBuilders.get("/hello")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization","Bearer $jwtToken"))
				.andExpect(status().isOk)
				.andReturn()

	}

	@Test
	@DisplayName("Login and access content")
	fun authTest6() {
		every { authorizedUserRepository.findByUsername("test1")} returns Optional.of(AuthorizedUser("test1",password,listOf("USER")))
		val test1 = mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("utf-8")
						.content("{\"username\":\"test1\",\"password\":\"pass\"}"))
				.andExpect(status().isOk)
				.andReturn()
		val jwtToken = test1.response.contentAsString.split(":")[1].substring(1).dropLast(2)
		println(jwtToken)
		mockMvc.perform(MockMvcRequestBuilders.get("/hello")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization","Bearer $jwtToken"))
				.andExpect(status().isOk)
				.andReturn()

	}

	@Test
	@DisplayName("Failed JWT Token")
	fun authTest7() {
		every { authorizedUserRepository.findByUsername("test1")} returns Optional.of(AuthorizedUser("test1",password,listOf("USER")))

		val invalidToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0MSIsImV4cCI6MTU4NDI5NjcxMSwiaWF0IjoxNTg0MjYwNzExfQ.eumW6coCIgV08gM6g9hVX9xEyQnKvIyOLqpnAAweB15"

		mockMvc.perform(MockMvcRequestBuilders.get("/hello")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization","Bearer $invalidToken"))
				.andExpect(status().isForbidden)
				.andReturn()

	}



}
