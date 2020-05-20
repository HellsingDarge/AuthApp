package ru.kadefrase.authapp

import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import ru.kafedrase.authapp.ArgHandler

object ArgHandlerTest : Spek({
    var argHandler = ArgHandler

    Feature("Validating arguments") {
        Scenario("Validating login") {
            Then("should pass validation") {
                assertTrue(argHandler.isLoginValid("sasha"))
            }

            Then("should fail validation - not lowercase") {
                assertFalse(argHandler.isLoginValid("Sasha"))
            }

            Then("should fail validation - invalid symbol") {
                assertFalse(argHandler.isLoginValid("q!"))
            }

            Then("should fail validation - invalid literal") {
                assertFalse(argHandler.isLoginValid("\u0000")) // null byte
            }

            Then("should fail validation - unicode symbol instead of ascii") {
                assertFalse(argHandler.isLoginValid("аdmin")) // cyrillic а
            }

            Then("should fail validation - invisible unicode symbol present") {
                assertFalse(argHandler.isLoginValid("lorem\u200b")) // zero width space
            }
        }
        Scenario("Validation role") {
            Then("should pass validation") {
                assertTrue(argHandler.isRoleValid("EXECUTE"))
            }

            Then("should fail - not upper case") {
                assertFalse(argHandler.isRoleValid("read"))
            }

            Then("should fail - invalid literal") {
                assertFalse(argHandler.isRoleValid("\u0000"))
            }

            Then("should fail - doesn't exist") {
                assertFalse(argHandler.isRoleValid("DELETE"))
            }
        }
    }
})
