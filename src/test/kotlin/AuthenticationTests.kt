import org.spekframework.spek2.Spek
import io.mockk.*
import ru.kafedrase.authapp.dao.AuthenticationDAO
import ru.kafedrase.authapp.services.AuthenticationService
import ru.kafedrase.authapp.domain.User


object AuthenticationTest: Spek({
    val authenDAOMock = mockk<AuthenticationDAO> {
        every { getUserByLogin(eq("sasha")) } returns User("sasha", "bc4725cd5915a9cda45d2835bdd8e444be15c7c9aabdd0dc8693d7a7d2500dc3", "V9Me2nx")
        every { getUserByLogin(eq("admin")) } returns User("admin", "e0feb157dadff817c0f11b48d0441e56b475a27289117c6cb1ca7fd0b108b13c", "6xInN0l")
        every { getUserByLogin(eq("q")) } returns User("q", "2002c9e01093b6d8b7d3699d6b7bd1a5fb8200340b1275f52ae5794d59854849", "4bxdOU7")
        every { getUserByLogin(eq("abcdefghij")) } returns User("abcdefghij", "d880929e469c4a2c19352f76460853be52ee581f7fcdd3097f86f670f690e910", "TM36tOy")
        every { getUserByLogin(match { !listOf("sasha", "admin", "q", "abcdefghij").contains(it) }) } returns null

    }
    lateinit var authentication: AuthenticationService
    beforeEachTest {
        authentication = AuthenticationService(authenDAOMock)
    }
    group("Check Login") {
        test("Login exists") {
            val login = "sasha"
            assert(authentication.start(login))
        }
        test("Login not exists") {
            val login = "sash"
            assert(!authentication.start(login))
        }
    }
    group("Check Password") {
        beforeEachTest {
            authentication.start("sasha")
        }
        test("Correct password") {
            val password = "123"
            assert(authentication.verifyPass(password))
        }
        test("Incorrect password") {
            val password = "1"
            assert(!authentication.verifyPass(password))
        }
    }

})
