package ru.kadefrase.authapp

import io.mockk.every
import io.mockk.mockk
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import ru.kafedrase.authapp.Role
import ru.kafedrase.authapp.dao.AuthorisationDAO
import ru.kafedrase.authapp.domain.UsersResources
import ru.kafedrase.authapp.services.AuthorisationService

object AuthorisationTest : Spek({
    var resources: List<UsersResources> = listOf(
        UsersResources(0, "A", Role.READ, "sasha"),
        UsersResources(0, "A.AA", Role.WRITE, "sasha"),
        UsersResources(0, "A.AA.AAA", Role.EXECUTE, "sasha"),
        UsersResources(0, "B", Role.EXECUTE, "admin"),
        UsersResources(0, "A.B", Role.WRITE, "admin"),
        UsersResources(0, "A.B", Role.WRITE, "sasha"),
        UsersResources(0, "A.B.C", Role.READ, "admin"),
        UsersResources(0, "A.B.C", Role.WRITE, "q"),
        UsersResources(0, "A.B", Role.EXECUTE, "q"),
        UsersResources(0, "B", Role.READ, "q"),
        UsersResources(0, "A.AA.AAA", Role.READ, "q"),
        UsersResources(0, "A", Role.EXECUTE, "q"),
        UsersResources(0, "A", Role.WRITE, "admin"),
        UsersResources(0, "A.AA", Role.EXECUTE, "admin"),
        UsersResources(0, "B", Role.WRITE, "sasha"),
        UsersResources(0, "A.B", Role.EXECUTE, "sasha"),
        UsersResources(0, "A.B.C", Role.EXECUTE, "sasha")
    )

    val authorDAO = mockk<AuthorisationDAO> {
        every { getResource(ofType(), ofType(), ofType()) } answers { resources.find { res ->
            res.role == secondArg() &&
                    res.login == firstArg() && (
                    res.path == thirdArg<List<String>>().joinToString(".") ||
                    thirdArg<List<String>>().joinToString(".").startsWith(res.path)
                    )
        } } }

    Feature("Authorisation verification") {
        lateinit var authorService: AuthorisationService
        Scenario("Direct access") {
            Given("User have direct access") {
                val usersResources = UsersResources(0, "A", Role.READ, "sasha")
                authorService = AuthorisationService(usersResources, authorDAO)
            }

            Then("May access") {
                assertTrue(authorService.haveAccess())
            }
        }

        Scenario("Direct access denied") {
            Given("User doesn't have direct access") {
                val usersResource = UsersResources(0, "A", Role.READ, "abcdefghij")
                authorService = AuthorisationService(usersResource, authorDAO)
            }

            Then("Mayn't access") {
                assertFalse(authorService.haveAccess())
            }
        }

        Scenario("Relative access") {
            Given("User have access to existing resource") {
                val usersResources = UsersResources(0, "A", Role.EXECUTE, "q")
                authorService = AuthorisationService(usersResources, authorDAO)
            }

            Then("May access existing resource") {
                assertTrue(authorService.haveAccess())
            }

            Given("User have access to non existing resource") {
                val usersResources = UsersResources(0, "A.B.C.D", Role.EXECUTE, "q")
                authorService = AuthorisationService(usersResources, authorDAO)
            }

            Then("May access non existing resource") {
                assertTrue(authorService.haveAccess())
            }
        }

        Scenario(("Parent access denied")) {
            Given("User doesn't have any form of parent access") {
                val usersResource = UsersResources(0, "A.AA", Role.READ, "q")
                authorService = AuthorisationService(usersResource, authorDAO)
            }

            Then("mayn't access") {
                assertFalse(authorService.haveAccess())
            }
        }
    }
})
