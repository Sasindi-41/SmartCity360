# SmartCity360 — Test Execution Record

> Complete this document after running the tests. This file is a blank evidence template; it does not claim that any test has passed.

## 1. Execution details

| Field | Actual value |
|---|---|
| Tester | [Enter name] |
| Date and time | [Enter date/time] |
| Project version / ZIP | [Enter version or archive name] |
| Operating system | [Enter OS] |
| JDK version | [Paste `java -version` result] |
| Maven version | [Paste `mvn -version` result] |
| Payara version | [Enter version used for deployment] |
| MySQL version | [Enter version used for integration tests] |
| Test database | [Enter test database name; never include credentials] |
| Browser | [Enter browser and version] |

## 2. Automated unit test result

Run `mvn clean test` from the folder containing `pom.xml` and copy the actual summary from Maven/Surefire.

| Item | Actual result |
|---|---|
| Command | `mvn clean test` |
| Total tests | [Copy actual number] |
| Passed | [Copy actual number] |
| Failures | [Copy actual number] |
| Errors | [Copy actual number] |
| Skipped | [Copy actual number] |
| Maven exit code | [0 = success; non-zero = failure] |
| Surefire report location | `target/surefire-reports/` |
| Overall status | [Passed / Failed / Not Run] |

### 2.1 Test-class breakdown

| Test class | Tests | Passed | Failed | Errors | Skipped | Status |
|---|---:|---:|---:|---:|---:|---|
| DecoratorPatternTest | [ ] | [ ] | [ ] | [ ] | [ ] | [ ] |
| CompositePatternTest | [ ] | [ ] | [ ] | [ ] | [ ] | [ ] |
| ComplaintChainTest | [ ] | [ ] | [ ] | [ ] | [ ] | [ ] |
| FlyweightPatternTest | [ ] | [ ] | [ ] | [ ] | [ ] | [ ] |
| InterpreterPatternTest | [ ] | [ ] | [ ] | [ ] | [ ] | [ ] |

## 3. Build/package result

| Item | Actual result |
|---|---|
| Command | `mvn clean package` |
| Maven exit code | [Enter] |
| WAR created | [Yes / No] |
| WAR path | `target/SmartCity360.war` |
| Payara deployment | [Successful / Failed / Not Run] |
| Relevant log evidence | [File name or screenshot reference] |

## 4. Manual test execution summary

Use the IDs from `TEST_PLAN.md`. Add rows as needed. Keep screenshots or sanitized log extracts in the evidence folder.

| Test ID | Actual result observed | Status (Passed / Failed / Not Run) | Evidence reference | Notes / defect ID |
|---|---|---|---|---|
| IT-01 | [ ] | [ ] | [ ] | [ ] |
| IT-02 | [ ] | [ ] | [ ] | [ ] |
| IT-03 | [ ] | [ ] | [ ] | [ ] |
| IT-04 | [ ] | [ ] | [ ] | [ ] |
| IT-05 | [ ] | [ ] | [ ] | [ ] |
| IT-06 | [ ] | [ ] | [ ] | [ ] |
| IT-07 | [ ] | [ ] | [ ] | [ ] |
| IT-08 | [ ] | [ ] | [ ] | [ ] |
| IT-09 | [ ] | [ ] | [ ] | [ ] |
| IT-10 | [ ] | [ ] | [ ] | [ ] |
| IT-11 | [ ] | [ ] | [ ] | [ ] |
| IT-12 | [ ] | [ ] | [ ] | [ ] |
| FT-01 to FT-14 | [Summarise each ID or attach a detailed table] | [ ] | [ ] | [ ] |
| RT-01 to RT-11 | [Summarise each ID or attach a detailed table] | [ ] | [ ] | [ ] |
| UI-01 to UI-07 | [Summarise each ID or attach a detailed table] | [ ] | [ ] | [ ] |

## 5. Defects and corrective actions

| Defect ID | Test ID | Description | Severity | Corrective action | Retest result |
|---|---|---|---|---|---|
| [ ] | [ ] | [ ] | [Low / Medium / High] | [ ] | [ ] |

## 6. Overall conclusion

[Write a short conclusion based on the actual results. State which test groups were completed, any tests not run, unresolved defects, and whether the application was successfully packaged and deployed. Do not claim complete verification if important tests remain Not Run.]

## 7. Evidence checklist

- [ ] Maven unit-test console output saved.
- [ ] Surefire `.txt` and `.xml` reports saved.
- [ ] Maven package/build output saved.
- [ ] Payara deployment evidence saved.
- [ ] Screenshots of the five working pattern modules saved.
- [ ] Sanitized database evidence saved where relevant.
- [ ] Role/access test evidence saved without exposing credentials or personal data.
- [ ] No passwords, AES keys, authentication tokens or private user data included.
