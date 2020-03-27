#!/usr/bin/env bash
# colouring output
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'
# counting tests
TESTS_RUN=0
TEST_FAILURES=0
TEST_SUCCESS=0

function testcase {
    ARGS=$1;
    EXPECTED_CODE=$2;
    TEST_NUM=$3

    ./run.sh ${ARGS}

    RES=$?

    (("TESTS_RUN+=1"))
    if [[ ${RES} -eq "$EXPECTED_CODE" ]]; then
        echo -e "${GREEN}Test ${TEST_NUM}: passed${NC}"
        (("TEST_SUCCESS+=1"))
    else
        echo -e "${RED}Test ${TEST_NUM}: failed (Expected: $EXPECTED_CODE. Got: $RES)${NC}"
        (("TEST_FAILURES+=1"))
    fi
}

## Help
#T1.1
testcase "" 1 "T1.1"
#T1.2
testcase "-h" 0 "T1.2"
#T1.3
testcase "-bla" 1 "T1.3"

## Authentication
#T2.1
testcase "-login sasha -pass 123" 0 "T2.1"
#T2.2
testcase "-pass 123 -login sasha" 0 "T2.2"
#T2.3
testcase "-login SASHA -pass 123" 2 "T2.3"
#T2.4
testcase "-login SA12 -pass 123" 2 "T2.4"
#T2.5
testcase "-login   -pass pass" 1 "T2.5"
#T2.6
testcase "-login abcdqwertyqwerty -pass pass" 2 "T2.6"
#T2.7
testcase "-login vasya -pass 123" 3 "T2.7"
#T2.8
testcase "-login admin -pass 1234" 4 "T2.8"
#T2.9
testcase "-login admin -pass  " 1 "T2.9"
#T2.10
testcase "-login admin -pass qwerty" 0 "T2.10"
#T2.11
testcase "-login q -pass @#$%^&*!" 0 "T2.11"
#T2.12
testcase "-login abcdefghij -pass abc" 0 "T2.12"
#T2.13
testcase "-h -login abcdefghij -pass abc" 0 "T2.13"

## Authorisation
#T3.1
testcase "-login sasha -pass 123 -role READ -res A" 0 "T3.1"
#T3.2
testcase "-login sasha -pass 123 -role DELETE -res A" 5 "T3.2"
#T3.3
testcase "-login sasha -pass 123 -role WRITE -res A" 6 "T3.3"
#T3.4
testcase "-login sasha -pass 123 -role WRITE -res a.b.c" 6 "T3.4"
#T3.5
testcase "-login sasha -pass 123 -role READ -res A.B" 0 "T3.5"
#T3.6
testcase "-login sasha -pass 123 -role READ -res A.B.C.D" 0 "T3.6"
#T3.7
testcase "-login admin -pass qwerty -role EXECUTE -res A.AA" 0 "T3.7"
#T3.8
testcase "-login admin -pass qwerty -role EXECUTE -res A.B" 6 "T3.8"
#T3.9
testcase "-login q -pass @#$%^&*! -role READ" 0 "T3.9"
#T3.10
testcase "-login q -pass 1234 -role DELETE -res A.B" 4 "T3.10"
#T3.11
testcase "-login q -pass @#$%^&*! -role READ -res A.AA.AAA" 0 "T3.11"
#T3.12
testcase "-login q -pass @#$%^&*! -role READ -res A.AA" 6 "T3.12"
#T3.13
testcase "-role READ -res A -login sasha -pass 123" 0 "T3.13"
#T3.14
testcase "-login sasha -pass 123 -role Write -res A" 5 "T3.14"
#T3.15
testcase "-login sasha -pass 123 -role write -res A" 5 "T3.14"

## Accounting
#T4.1
testcase "-login sasha -pass 123 -role READ -res A -ds 2000-01-15 -de 2000-02-15 -vol 10" 0 "T4.1"
#T4.2
testcase "-login sasha -pass 123 -role READ -res A -ds 2000-01-15 -de 2000-02-15 -vol ten" 7 "T4.2"
#T4.3
testcase "-login sasha -pass 123 -role READ -res A -ds 2000-01-15 -de 2000-02-15 -vol -1" 7 "T4.3"
#T4.4
testcase "-login sasha -pass 123 -role READ -res A -ds 2000-01-00 -de 2000-02-15 -vol 10" 7 "T4.4"
#T4.5
testcase "-login sasha -pass 123 -role READ -res A -ds 2000-01-15 -de 2000-02-32 -vol 10" 7 "T4.5"
#T4.6
testcase "-login sasha -pass 123 -role READ -res A -ds 2000-02-15 -de 2000-01-15 -vol 10" 7 "T4.6"
#T4.7
testcase "-login sasha -pass 123 -role READ -res A -ds 2120-02-15 -de 2120-01-15 -vol 10" 7 "T4.7"
#T4.8
testcase "-login q -pass @#$%^&*! -role WRITE -res A.B.C -ds 2000-01-15 -de 2000-02-15 -vol 20" 0 "T4.8"
#T4.9
testcase "-login admin -pass qwerty -role EXECUTE -res A.AA -ds 2000-01-15 -de 2000-02-15 -vol 100" 0 "T4.9"
#T4.10
testcase "-login q -pass @#$%^&*! -role WRITE -res A.B.C -ds 2000-01-15 -vol 10" 0 "T4.10"
#T4.11
testcase "-login q -pass @#$%^&*! -role WRITE -res A.B.C -de 2000-02-15 -vol 10" 0 "T4.11"
#T4.12
testcase "-login q -pass @#$%^&*! -role WRITE -res A.B.C -ds 2000-01-15 -de 2000-02-15" 0 "T4.12"
#T4.13
testcase "-login q -pass @#$%^&*! -role DELETE -res A.B.C -ds 2000-01-15 -de 2000-02-15" 5 "T4.13"
#T4.14
testcase "-login q -pass !@#$% -role WRITE -res A.B.C -ds 2000-01-15 -de 2000-02-15" 4 "T4.14"
#T4.15
testcase "-res A.B.C -ds 2000-01-15 -vol 10 -login q -pass @#$%^&*! -role WRITE" 0 "T4.15"
#T4.16
testcase "-login sasha -pass 123 -role READ -res A -ds 2000-01-15 -de 2000-02-15 -vol 10.6" 7 "T4.16"

echo -e "Tests run: $TESTS_RUN, Success: ${GREEN}$TEST_SUCCESS${NC}, Failures: ${RED}$TEST_FAILURES${NC}"

if [ $TEST_FAILURES -ne 0 ]; then
    exit 1
fi