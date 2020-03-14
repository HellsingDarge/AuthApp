# **План разработки требований**

## Этап 0: Подготовительный

1. Создать репозиторий открытый в git 
2. Создать консольное приложение с функцией main в IDEA (R1.8)
3. Написать bash скрипт сборки приложения `build.sh` (R1.12)
4. Написать bash скрипт запуск приложения `run.sh` (R1.12)
5. Каждый этап требований, каждый пункт, будет комититься не менее чем одним коммитом
(в сообщении обязательно ссылку на пункт плана)
6. На каждую реализацию требований *запускать тесты* (далее про это не упоминаем)

## Этап 1: Тестирование (R1.12)

1. Описать тестовые входные данные
    1. Описать пользователей
        - u1: login: sasha, pass: 123
        - u2: login: admin, pass: qwerty
        - u3: login: q, pass: @#$%^&*!
        - u4: login: aleksandra, pass: abc (без ролей)
    2. Описать ресурсы:
        - r1: res: A, role: read, user: sasha
        - r2: res: A.AA, role: write, user: sasha
        - r3: res: A.AA.AAA, role: execute, user: sasha
        - r4: res: B, role: execute, user: admin
        - r5: res: A.B, role: write, user: admin
        - r6: res: A.B, role: write, user: sasha
        - r7: res: A.B.C, role: read, user: admin
        - r8: res: A.B.C, role: write, user: q
        - r9: res: A.B, role: execute, user: q
        - r10: res: B, role: read, user: q
        - r11: res: A.AA.AAA, role: read, user: q
        - r12: res: A, role: execute, user: q
        - r13: res: A, role: write, user: admin
        - r14: res: A.AA, role: execute, user: admin
        - r15: res: B, role: write, user: sasha
        - r16: res: A.B, role: execute, user: sasha
        - r17: res: A.B.C, role: execute, user: sasha
2. Записать тесты в `test.sh`
    1. Справка (R1.8):
    
    | # | Act | Result | Purpose |
    |:---|---|:---:|:---|
    |T1.1 |`app.jar`|1, печать справки| R1.8 |
    |T1.2 |`app.jar -h`|1, печать справки| R1.8 |
    |T1.3 |`app.jar -bla` | 0, печать справки| особенности реализации библиотеки |
    
    2. Аунтентификация (R1.1)
        
    | # | Act | Result | Purpose |
    |:---|---|:---:|:---|
    |T2.1 |`app.jar -login sasha -pass 123` | 0 | R1.9, R1.8 | 
    |T2.2 |`app.jar -pass 123 -login sasha` | 0 | R1.9, R1.10 |     
    |T2.3 |`app.jar -login SASHA -pass 123` | 2 | R1.9 Неверный формат, логин прописными |
    |T2.4 |`app.jar -login SASHA -pass 123` | 2 | R1.9 Неверный формат, логин прописными |
    |T2.5 |`app.jar -login -pass pass` | 2 | R1.9 Неверный формат, логин пустой |
    |T2.6 |`app.jar -login abashkirova -pass pass` | 2 | R1.9 Неверный формат, логин больше 10 символов |
    |T2.7 |`app.jar -login vasya -pass 123` | 3 | R1.9 Невеизвестный логин | 
    |T2.8 |`app.jar -login admin -pass 1234` | 4 | R1.9 Неверный пароль | 
    |T2.9 |`app.jar -login admin -pass ` | 4 | R1.9 Неверный пароль, пустой | 
    |T2.10 |`app.jar -login admin -pass qwerty` | 0 | R1.9 | 
    |T2.11 |`app.jar -login q -pass @#$%^&*!` | 0 | R1.9 | 
    |T2.12 |`app.jar -login aleksandra -pass abc` | 0 | R1.9 |
    |T2.13 |`app.jar -h -login aleksandra -pass abc` | 0 | R1.9 Аунтентификация |
    
    3. Авторизация (R1.3)
    
    | # | Act | Result | Purpose |
    |:---|---|:---:|:---|
    |T3.1 |`app.jar -login sasha -pass 123 -role READ -res A` | 0 | R1.3, R1.8, R1.9 Успешный доступ|
    |T3.2 |`app.jar -login sasha -pass 123 -role DELETE -res A` | 5 | R1.8, R1.9 Неизвестная роль | 
    |T3.3 |`app.jar -login sasha -pass 123 -role WRITE -res A` | 6 | R1.8, R1.9 Нет доступа (ресурс есть) | 
    |T3.4 |`app.jar -login sasha -pass 123 -role WRITE -res a.b.c` | 6 | R1.8, R1.9 Нет доступа (ресур не найден) | 
    |T3.5 |`app.jar -login sasha -pass 123 -role READ -res A.B` | 0 | R1.6 Доступ к потомкам |
    |T3.6 |`app.jar -login sasha -pass 123 -role READ -res A.B.C.D` | 0 |R1.6, R1.3, R1.8, R1.9 Доступ к потомкам | 
    |T3.7 |`app.jar -login admin -pass qwerty -role EXECUTE -res A.AA` | 0 | R1.3, R1.8, R1.9 Успешный доступ |
    |T3.8 |`app.jar -login admin -pass qwerty -role EXECUTE -res A.B` | 6 | R1.3, R1.8, R1.9 Доступ к брату |
    |T3.9 |`app.jar -login q -pass @#$%^&*! -role READ` | 0 | R1.1, R1.8, R1.9 Успешная аутентификация |
    |T3.10 |`app.jar -login q -pass 1234 -role DELETE -res A.B` | 4 | R1.1, R1.8, R1.9 Неверный пароль| 
    |T3.11 |`app.jar -login q -pass @#$%^&*! -role READ -res A.AA.AAA` | 0 | R1.3 Успешный доступ |
    |T3.12 |`app.jar -login q -pass @#$%^&*! -role READ -res A.AA` | 6 | R1.8, R1.9 Нет доступа (выше узла) |
    |T3.13 |`app.jar -role READ -res А -login sasha -pass 123` | 0 | R1.3, R1.10 |
    
    4. Аккаунтинг (R1.7)
    
    | # | Act | Result | Purpose |
    |:---|---|:---:|:---|
    |T4.1 |`app.jar -login sasha -pass 123 -role READ -res A -ds 2000-01-15 -de 2000-02-15 -vol 10` | 0 |R1.7,R1.8,R1.9|
    |T4.2 |`app.jar -login sasha -pass 123 -role READ -res A -ds 2000-01-15 -de 2000-02-15 -vol ten` | 7 |R1.9 — Некорректная активность, не приводится vol|
    |T4.3 |`app.jar -login sasha -pass 123 -role READ -res A -ds 2000-01-15 -de 2000-02-15 -vol -1` | 7 |R1.9 — Некорректная активность, не приводится vol < 0|
    |T4.4 |`app.jar -login sasha -pass 123 -role READ -res A -ds 2000-01-00 -de 2000-02-15 -vol 10` | 7 |R1.9 — Некорректная активность, не приводится дата ds|
    |T4.5 |`app.jar -login sasha -pass 123 -role READ -res A -ds 2000-01-15 -de 2000-02-32 -vol 10` | 7 |R1.9 — Некорректная активность, не приводится дата de|
    |T4.6 |`app.jar -login sasha -pass 123 -role READ -res A -ds 2000-02-15 -de 2000-01-15 -vol 10` | 7 |R1.9 — Некорректная активность, de > ds|
    |T4.7 |`app.jar -login sasha -pass 123 -role READ -res A -ds 2120-02-15 -de 2120-01-15 -vol 10` | 7 |R1.9 — Некорректная активность, today < ds < de|
    |T4.8 |`app.jar -login q -pass @#$%^&*! -role WRITE -res A.B.C -ds 2000-01-15 -de 2000-02-15 -vol 10` | 0 |R1.7,R1.8,R1.9|
    |T4.9 |`app.jar -login admin -pass qwerty -role execute -res A.AA -ds 2000-01-15 -de 2000-02-15 -vol 10` | 0 |R1.7,R1.8,R1.9|
    |T4.10 |`app.jar -login q -pass @#$%^&*! -role WRITE -res A.B.C -ds 2000-01-15 -vol 10` | 0 | R1.3 Успешная авторизация (параметров <7) |
    |T4.11 |`app.jar -login q -pass @#$%^&*! -role WRITE -res A.B.C -de 2000-02-15 -vol 10` | 0 | R1.3 Успешная авторизация (параметров <7) |
    |T4.12 |`app.jar -login q -pass @#$%^&*! -role WRITE -res A.B.C -ds 2000-01-15 -de 2000-02-15` | 0 | R1.3 Успешная авторизация (параметров <7) |
    |T4.13 |`app.jar -login q -pass @#$%^&*! -role DELETE -res A.B.C -ds 2000-01-15 -de 2000-02-15` | 0 | R1.1 Успешная аутентификация |
    |T4.14 |`app.jar -login q -pass !@#$% -role WRITE -res A.B.C -ds 2000-01-15 -de 2000-02-15` | 2 | R1.1 Неверный пароль |
    |T4.15 |`app.jar -res A.B.C -ds 2000-01-15 -vol 10 -login q -pass @#$%^&*! -role WRITE ` | 0 | R1.10 Порядок параметров |  
    |T4.16 |`app.jar -login sasha -pass 123 -role READ -res A -ds 2000-01-15 -de 2000-02-15 -vol 10.0` | 7 |R1.9 — Некорректная активность, не приводится vol |

 

## Этап 2: Работа с консольными параметрами 

1. Написать `main` c параметрами командной строки (R1.8)
2. Написать фукнцию, проверяющую наличие аргументов `argsAreNotEmpty(args: Array<String>)`
3. Создать метод печати справки `printHelp()` и `System.exit(1)` (R1.8.1, R1.8.2, R1.8.6)
4. Создать функцию, проверяющую необходима ли справка `helpIsNeeded(args: Array<String>): Boolean` (`arg[0] equal "-h"`)
5. Печатать во всех остальных случаях справку и код 0 (`System.exit(0)`)

## Этап 3: Аутентификация пользователя (R1.1)
1. Создать функцию, проверяющую надо ли аутентифицировать `authenticationIsNeeded(args: Array<String>): Boolean`
(args[0] equal -login && args[2] equal -path)
2. Создать функцию, валидирующую надо логин `validateLogin(login: String): Boolean`
(проверяем формат через regexp `[a-z]{1,10}`, код 2 ) - R1.9, R1.8.3
3. Создать функцию, проверяющую что логин существует `loginExists(login: String): Boolean`
(проверяем, что login equal sasha)
4. Создать функцию, проверяющую валидность пароля `validatePassForLogin(pass: String, login: String): Boolean`
 (проверяем, что login equal sasha && pass equal 123, код 3, если не найден) - R1.9
5. Создать `data class User` с логином и паролем - R1.1
6. Создать коллекцию `Users`, заполненную тестовыми данными
7. Отефакторить методы на работу с коллекцией пользователей
8. Создать класс `ArgHandler` (поля-аргументы `-h`, `-login`, `-pass`) для разбора параметров c функцией `getValue(key: String): String?` — R1.10
9. Перенести функции в `ArgHandler`:
     - `helpIsNeeded(args: Array<String>): Boolean`, 
     - `authenticationIsNeeded(args: Array<String>): Boolean`
10. Отрефакторим код, чтобы использовать `ArgHandler`
11. Создать класс `AuthenticationService` для аутентификации пользователя по логину и паролю - R1.1
    1. Перенести коллекцию пользователей в класс
    2. Перенести функции
        - `validateUserLogin(login: String): Boolean`
        - `findUser(user: User)`
        - `validatePassForLogin(pass: String, login: String): Boolean`
12. Создать класс `HelpHandler`
    1. Перенести функцию печати справки в класс `HelpHandler`
    2. Отрефакторить код на работу с классом `HelpHandler`
13. Добавить в класс `User` поля `salt` и `hash`, а `pass` — удалить — R1.2
14. Написать функции хеширования пароля в классе `AuthenticationService` - R1.2
    1. Написать функцию получения хеша по паролю `generateHash(pass: String, salt: String): String` 
    (встроенная библиотека `MessageDigest`, алгоритм "SHA-256")
    2. Написать функцию сравнение хешей в `isEqualsHash(pass: String, usersHash: String): Boolean` 
    (0 — успех, 4 — не успех (R1.9))
    3. Обновить тестовые данные с `salt` и `hash`

## Этап 4: Авторизация (R1.3)
1. Создать функцию, проверяющую есть ли доступ у пользователя с такой ролью к ресурсу 
    `haveAccess(res: String, role: String)` 
    (проверяем, что user.login equal sasha && role equal READ && res equal A), код 6 
2. Добавить функцию валидации роли `validateRole(role: String)`, код 5 - R1.5, R1.8.4
3. Создать перечисление `enum Role` c READ, WRITE, EXECUTE R1.3, R1.5
4. Создать `data class UsersResources` (R1.3, R1.6) с полями `path: String`, `role: Role`, `user: User` 
5. Создать функцию, проверяющую существует ли такой ресурс в списке `resourceExist(res: String): Boolean` 
(проверяем, что res equal A), код 6 — R1.9
6. Создать функцию, проверяющую доступ к потомку по родителю `haveParentAccess(res: String, role: String)` - R1.4
7. Создать список ресурсов с тестовыми данными
8. Добавить функцию в `ArgHandler`, проверяющую необходима ли авторизация `authorizationIsNeeded(): Boolean`
9. Добавить в `ArgHandler` поля `-res`, `-role`
10. Отрефакторить функции на работу с коллекцией 
11. Создать класс `AuthorizationService` (R1.3)
    1. Создать функцию поиск ресурса `getResourceRolesBy(path: String): List<Role>` 
    (найдет для этого user все возможные доступы, например для sasha по ресурсу A.AA вернется список A.AA-Write, A - Read)
    2. Перенести функции в класс:
        - `haveParentAccess(res: String, role: String)`
        - `haveAccess(res: String, role: String): Boolean`
        - `resourceExist(res: String): Boolean`
    3. Перенести список тестовых данных
12. Создать класс `Application` - R1.11
    1. Создать метод `printHelp()` 
    2. Перенести шаг аутентификации в функцию `startAuthentication(login: String, pass: String): Int`
    3. Создать функцию для шага авторизации `startAuthorization(user: User, res: String, role: String): Int`
    4. Написать функцию, выбирающую последовательность действий `run()` (справка или сценарий авторизации)
    (проверяем нужна ли справка, проверяем нужно ли аутентифицировать и/или авторизовывать)

## Этап 5: Аккаунтинг (R1.7)
1. Добавить функцию в `ArgHandler`, проверяющую необходим ли аккаунтинг `accountingIsNeeded(): Boolean`
2. Добавить в `ArgHandler` поля `-ds`, `-de`, `-vol`
3. Создать функцию в `ArgHandler` приводим строку к дате `parse(date: String): Date?` 
(если неуспешно код 7) - R1.9
4. Создать функцию в `ArgHandler`, приводим строку к `Int`с помощью`String.toIntOrNull` 
(если неуспешно код 7) - R1.9
5. Создать функцию, проверяющую возможен ли аккаунтинг `accountingIsPossible(): Boolean` 
(приведем типы здесь, проверим на null)
6. Создать функцию, проверяющую объем на неотрицательность`validateVolume(volume: Int?): Boolean`
7. Создаем функцию, проверяющую последовательность дат `validateActivity(ds: Date, de: Date): Boolean` 
(проверяем ds.isBefore(de))
8. Написать шаг аккаунтинга в `Application` в функции 
`startAccounting(user: User, res: UsersResources, ds: String, de: String, vol: String): Int` - R1.11
9. Создать класс сессии пользователя `UserSession(user: User, res: UsersResources, ds: Date, de: Date, vol: Int)`(R1.7)
10. Создать класс `AccountService` c коллекцией  `UserSession`
    1. Перенести функции в класс 
        - `validateVolume(volume: Int): Boolean`
        - `validateActivity(ds: Date, de: Date): Boolean` 
    Если одна из них неверна, вернем код 7
    2. Создать метод сохранения активности `write(session: UserSession)` 
    (записываем в коллекцию все данные аккаунтинга — пользователь, роль, даты доступа и потребленный объем — put)
11. Отрефакторить код в `Application` на работу с `AccountService` (вызываем последовательно, после авторизации) - R1.11

## Этап 6: Сборка требований
1. Написать процесс AAA в классе `Application` (R1.11)
    1. Сначала аутентификацию (два параметра и более)
    2. Если успешно, переходим к авторизации (четыре параметра и более)
    3. Если успешно, переходим к аккаунтингу (семь параметров)
2. Обновить readme 

## Этап 7: Добавление kotlin.cli (R1.10)
1. Подключить библиотеку [kotlin.cli](https://github.com/Kotlin/kotlinx.cli)
2. Описать возможные аргументы и подсказки в ArgHandler через kotlin.cli:

    | # | Название | Сокращение | Описание значений |
    |:---|:---|:---|:---|
    | A1 | help | -h | Вызов справки |
    | A2 | login | -login | Логин пользователя, строка, строчные буквы. Не более 10 символов |
    | А3 | password | -pass | Пароль, строка |
    | А4 | resource | -res | Абсолютный путь до запрашиваемого ресурса, формат A.B.C |
    | А5 | role | -role | Уровень доступа к ресурсу. Возможные варианты: READ, WRITE, EXECUTE |
    | А6 | datestart | -ds | Дата начала сессии с ресурсом, формат YYYY-MM-DD |
    | А7 | dateend | -de| Дата окончания сессии с ресурсом, формат YYYY-MM-DD |
    | А8 | volume | -vol | Потребляемый объем, целое число |
3. Обновить скрипт `build.sh` с компиляцией зависимости