package ru.kadefrase.authapp

import io.mockk.every
import io.mockk.mockk
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import ru.kafedrase.authapp.dao.AuthenticationDAO
import ru.kafedrase.authapp.domain.User
import ru.kafedrase.authapp.services.AuthenticationService

object AuthenticationTest : Spek({

    // sasha - 123
    // admin - admin
    // q - @#$%^&*!
    // abcdefghij - abc
    val users = listOf(
        User("sasha", "bc4725cd5915a9cda45d2835bdd8e444be15c7c9aabdd0dc8693d7a7d2500dc3", "V9Me2nx"),
        User("admin", "e0feb157dadff817c0f11b48d0441e56b475a27289117c6cb1ca7fd0b108b13c", "6xInN0l"),
        User("q", "2002c9e01093b6d8b7d3699d6b7bd1a5fb8200340b1275f52ae5794d59854849", "4bxdOU7"),
        User("abcdefghij", "d880929e469c4a2c19352f76460853be52ee581f7fcdd3097f86f670f690e910", "TM36tOy")
    )

    val authenDAO = mockk<AuthenticationDAO> {
        every { getUserByLogin(ofType()) } answers { users.find { it.login == firstArg() } }
    }

    val authenService = AuthenticationService(authenDAO)

    Feature("Authentication Service verification") {

        Scenario("Successful service start (user exists)") {
            Then("Should successfully start (from test date)") {
                assertTrue(authenService.start("abcdefghij"))
            }
        }

        Scenario("Successful password check") {
            Given("valid login") {
                authenService.start("q")
            }

            Then("giving right password should be successful") {
                assertTrue(authenService.verifyPass("@#\$%^&*!"))
            }
        }

        Scenario("Unsuccessful service start") {
            Then("Fail if login is in wrong case") {
                assertFalse(authenService.start("Sasha"))
                assertFalse(authenService.start("ADMIN"))
            }

            Then("Fail if user doesn't exist") {
                assertFalse(authenService.start("adminn"))
                assertFalse(authenService.start("vasya"))
            }

            Then("Fail if login is invalid") {
                assertFalse(authenService.start("\u0000"))
            }
        }

        Scenario("Unsuccessful password check") {
            Given("Existing user") {
                authenService.start("admin")
            }

            Then("It should fail if password is wrong") {
                assertFalse(authenService.verifyPass("adminn"))
            }

            Then("It should fail if password is invalid") {
                assertFalse(authenService.verifyPass("\u0000"))
            }

            Then("It should fail if password is for another user") {
                assertFalse(authenService.verifyPass("abc"))
            }
        }
    }
})
