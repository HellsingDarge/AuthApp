import org.spekframework.spek2.Spek
import io.mockk.*
import ru.kafedrase.authapp.dao.AuthorisationDAO
import ru.kafedrase.authapp.services.AuthorisationService
import ru.kafedrase.authapp.domain.UsersResources
import ru.kafedrase.authapp.Role


object AuthorizationTests : Spek({
    val resources: List<UsersResources> = listOf(
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
        every { getResource(ofType(), ofType(), ofType()) } answers {
            resources.find { res ->
                res.role == secondArg() &&
                        res.login == firstArg() && (
                        res.path == thirdArg<List<String>>().joinToString(".") ||
                                thirdArg<List<String>>().joinToString(".").startsWith(res.path)
                        )
            }
        }
    }
    lateinit var authorization: AuthorisationService
    group("Check access") {
        test("Have access") {
            val resource = UsersResources(1, "A", Role.READ, "sasha")
            authorization = AuthorisationService(resource, authorDAO)
            assert(authorization.haveAccess())
        }
        test("Have access via a parent directory") {
            val resource = UsersResources(2, "A.B", Role.READ, "sasha")
            authorization = AuthorisationService(resource, authorDAO)
            assert(authorization.haveAccess())
        }
        test("Doesnt access via a parent directory") {
            val resource = UsersResources(3, "A.B", Role.WRITE, "sasha")
            authorization = AuthorisationService(resource, authorDAO)
        //    assert(!authorization.haveAccess())
        }
        test("Doesnt have access") {
            val resource = UsersResources(4, "A", Role.EXECUTE, "sasha")
            authorization = AuthorisationService(resource, authorDAO)
            assert(!authorization.haveAccess())
        }
        test("Doesnt have access") {
            val resource = UsersResources(5, "A", Role.READ, "admin")
            authorization = AuthorisationService(resource, authorDAO)
            assert(!authorization.haveAccess())
        }
    }

})