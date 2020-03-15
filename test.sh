#!/usr/bin/env bash
# colouring output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;32m'
NC='\033[0m'
# counting tests
TESTS_RUN=0
TEST_FAILURES=0
TEST_SUCCESS=0

function testcase {
    ARGS=$1;
    EXPECTED_CODE=$2;
    PURPOSE=$3

    echo -e "${YELLOW}$PURPOSE${NC}"
    echo "$ARGS"

    java -jar app.jar ${ARGS}

    RES=$?

    (("TESTS_RUN+=1"))
    if [[ ${RES} -eq "$EXPECTED_CODE" ]]; then
        echo -e "${GREEN}✅  Test passed${NC}"
        (("TEST_SUCCESS+=1"))
    else
        echo -e "${RED}🚫 Test failed. Expected $EXPECTED_CODE. Actual $RES${NC}"
        (("TEST_FAILURES+=1"))
    fi
    echo
}

# Справка
#T1.1
testcase "" 1 "#T1.1: R1.8 Печать справки"
#T1.2
testcase "-h" 1 "#T1.2: R1.8 Печать справки"
#T1.3
testcase "-bla" 0 "#T1.3: R1.8 Печать справки"

# Аунтентификация
#T2.1
testcase "-login sasha -pass 123" 0 "T2.1: R1.9, R1.8 Успешная Аунтентификация U1"
##T2.2
testcase "-pass 123 -login sasha" 0 "T2.2: R1.9, R1.10 Успешная Аунтентификация U1"
##T2.3
testcase "-login SASHA -pass 123" 2 "T2.3: R1.9 Неверный формат, логин прописными"
##T2.4
testcase "-login SA12 -pass 123" 2 "T2.4: R1.9 Неверный формат, логин c цифрами"
##T2.5
testcase "-login   -pass pass" 2 "T2.5: R1.9 Неверный формат, логин пустой"
##T2.6
testcase "-login abcdqwertyqwerty -pass pass" 2 "T2.6: R1.9 Неверный формат, логин больше 10 символов"
##T2.7
testcase "-login vasya -pass 123" 3 "T2.7: R1.9 Невеизвестный логин"
##T2.8
testcase "-login admin -pass 1234" 4 "T2.8: R1.9 Неверный пароль"
##T2.9
testcase "-login admin -pass  " 4 "T2.9: R1.9 Неверный пароль, пустой"
##T2.10
testcase "-login admin -pass qwerty" 0 "T2.10: R1.9 Успешная Аунтентификация U2"
##T2.11
testcase "-login q -pass @#$%^&*!" 0 "T2.11: R1.9 Успешная Аунтентификация U3"
##T2.12
testcase "-login abcdefghij -pass abc" 0 "T2.12: R1.9 Успешная Аунтентификация U4"
##T2.13
testcase "-h -login abcdefghij -pass abc" 0 "T2.13: R1.9 R1.10 Аунтентификация при дополнительном вызове справки"

## Авторизация
#
##T3.1
testcase "-login sasha -pass 123 -role READ -res A" 0 "T3.1: R1.3, R1.8, R1.9 Успешный доступ"
##T3.2
testcase "-login sasha -pass 123 -role DELETE -res A" 5 "T3.2: R1.8, R1.9 Неизвестная роль"
##T3.3
testcase "-login sasha -pass 123 -role Write -res A" 6 "T3.3: R1.8, R1.9 Нет доступа (ресурс есть)"
##T3.4
testcase "-login sasha -pass 123 -role write -res a.b.c" 6 "T3.4: R1.8, R1.9 Нет доступа (ресур не найден)"
##T3.5
testcase "-login sasha -pass 123 -role READ -res A.B" 0 "T3.5: R1.6 Доступ к потомкам"
##T3.6
testcase "-login sasha -pass 123 -role ReAd -res A.B.C.D" 0 "T3.6: R1.6, R1.3, R1.8, R1.9 Доступ к потомкам"
##T3.7
testcase "-login admin -pass qwerty -role EXECUTE -res A.AA" 0 "T3.7: R1.3, R1.8, R1.9 Успешный доступ"
##T3.8
testcase "-login admin -pass qwerty -role execute -res A.B" 6 "T3.8: R1.3, R1.8, R1.9 Доступ к брату"
##T3.9
testcase "-login q -pass @#$%^&*! -role READ" 0 "T3.9: R1.1, R1.8, R1.9 Успешная аутентификация"
##T3.10
testcase "-login q -pass 1234 -role DELETE -res A.B" 4 "T3.10: R1.1, R1.8, R1.9 Неверный пароль"
##T3.11
testcase "-login q -pass @#$%^&*! -role READ -res A.AA.AAA" 0 "T3.11: R1.3 Успешный доступ"
##T3.12
testcase "-login q -pass @#$%^&*! -role read -res A.AA" 6 "T3.12: R1.8, R1.9 Нет доступа (выше узла)"
##T3.13
testcase "-role READ -res A -login sasha -pass 123" 0 "T3.13: R1.3, R1.10 - Успешный доступ, порядок аргументов"

## Аккаунтинг
#
##T4.1
testcase "-login sasha -pass 123 -role READ -res A -ds 2000-01-15 -de 2000-02-15 -vol 10" 0 "T4.1: R1.7,R1.8,R1.9 Успешный аккаунитнг"
##T4.2
testcase "-login sasha -pass 123 -role READ -res A -ds 2000-01-15 -de 2000-02-15 -vol ten" 7 "T4.2: R1.9 — Некорректная активность, не приводится vol"
##T4.3
testcase "-login sasha -pass 123 -role READ -res A -ds 2000-01-15 -de 2000-02-15 -vol -1" 7 "T4.3: R1.9 — Некорректная активность, не приводится vol<0"
##T4.4
testcase "-login sasha -pass 123 -role READ -res A -ds 2000-01-00 -de 2000-02-15 -vol 10" 7 "T4.4: R1.9 — Некорректная активность, не приводится дата ds"
##T4.5
testcase "-login sasha -pass 123 -role READ -res A -ds 2000-01-15 -de 2000-02-32 -vol 10" 7 "T4.5: R1.9 — Некорректная активность, не приводится дата de"
##T4.6
testcase "-login sasha -pass 123 -role READ -res A -ds 2000-02-15 -de 2000-01-15 -vol 10" 7 "T4.6: R1.9 — Некорректная активность, ds > ds"
##T4.7
testcase "-login sasha -pass 123 -role READ -res A -ds 2120-02-15 -de 2120-01-15 -vol 10" 7 "T4.7: R1.9 — Некорректная активность, today < ds < de"
##T4.8
testcase "-login q -pass @#$%^&*! -role WRITE -res A.B.C -ds 2000-01-15 -de 2000-02-15 -vol 20" 0 "T4.8: R1.7,R1.8,R1.9 Успешный аккаунитнг"
##T4.9
testcase "-login admin -pass qwerty -role execute -res A.AA -ds 2000-01-15 -de 2000-02-15 -vol 100" 0 "T4.9: R1.7,R1.8,R1.9 Успешный аккаунитнг"
##T4.10
testcase "-login q -pass @#$%^&*! -role WRITE -res A.B.C -ds 2000-01-15 -vol 10" 0 "T4.10: R1.3 Успешная авторизация(не передан de)"
##T4.11
testcase "-login q -pass @#$%^&*! -role WRITE -res A.B.C -de 2000-02-15 -vol 10" 0 "T4.11: R1.3 Успешная авторизация(не передан ds)"
##T4.12
testcase "-login q -pass @#$%^&*! -role WRITE -res A.B.C -ds 2000-01-15 -de 2000-02-15" 0 "T4.12: R1.3 Успешная авторизация(не передан vol)"
##T4.13
testcase "-login q -pass @#$%^&*! -role DELETE -res A.B.C -ds 2000-01-15 -de 2000-02-15" 0 "T4.13: R1.1 Успешная аутентификация(не передан vol, несуществующая роль)"
##T4.14
testcase "-login q -pass !@#$% -role WRITE -res A.B.C -ds 2000-01-15 -de 2000-02-15" 2 "T4.14: R1.1 Неверный пароль"
##T4.15
testcase "-res A.B.C -ds 2000-01-15 -vol 10 -login q -pass @#$%^&*! -role WRITE" 0 "T4.15: R1.10 Успешный аккаунитнг, Порядок параметров"
##T4.16
testcase "-login sasha -pass 123 -role READ -res A -ds 2000-01-15 -de 2000-02-15 -vol 10.0" 7 "T4.16 R1.9 — Некорректная активность, не приводится vol"

echo -e "Tests run: $TESTS_RUN, Success: ${GREEN}$TEST_SUCCESS${NC}, Failures: ${RED}$TEST_FAILURES${NC}"