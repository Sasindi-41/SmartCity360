# SmartCity360 — Software Testing and Validation Plan

**Project:** SmartCity360 Smart City Management Platform  
**Module:** Object Oriented Design Patterns II  
**Application stack:** Java 17, JSP/Servlet (Jakarta EE 10), Payara Server 6, MySQL 8, Maven, JUnit 5  
**Document status:** Test plan — execute the tests and complete the execution record before submission.

---

## 1. Purpose

This plan describes how SmartCity360 will be tested to check that the five required design patterns are implemented correctly and that the integrated web application works with its database and user roles.

The supplied OODP II assessment brief requires a working application, an explanation and demonstration of the five design patterns, comprehensive documentation, UML class diagrams, and a 10–15-minute MP4 demonstration. It does **not** prescribe JMeter, a particular testing framework, or a named testing methodology. JUnit 5 is used here as supporting engineering evidence; manual integration and application tests provide evidence for behaviour that cannot safely be verified by isolated unit tests.

## 2. Test scope

### 2.1 Included

1. Unit tests for Decorator, Composite, Chain of Responsibility, Flyweight and Interpreter.
2. Database integration checks using a separate test database or a backed-up development database.
3. Functional checks of the five application modules.
4. Authentication and role-based access checks.
5. Browser/UI checks, including validation, navigation, tables, filters and responsive layout.
6. Build and deployment checks for the WAR on Payara Server 6.

### 2.2 Excluded from automated unit tests

- Tests that insert, update or delete records in the developer's live database.
- Tests that require real login credentials, AES keys, or production data.
- Load/performance testing with JMeter. This is not explicitly required by the assessment brief and is not part of the current test setup.

## 3. Test environment

| Item | Required environment |
|---|---|
| Java | JDK 17 |
| Build | Maven 3.8 or later |
| Unit testing | JUnit Jupiter 5.10.2 / Maven Surefire 3.2.5 |
| Application server | Payara Server 6 |
| Database | MySQL 8.x with the SmartCity360 schema and lookup data |
| Browser | Current Chrome, Edge or Firefox |
| Application | The WAR produced from this project |

Use a dedicated test database wherever possible. Before database testing, create a backup and confirm the JDBC URL points to the intended test database. Never run destructive test cases against the only copy of the working database. Do not place passwords, AES keys, personal information or real tokens in test evidence.

## 4. Automated unit testing

The project already contains five JUnit 5 test classes. The tests are intentionally isolated from MySQL and Payara so that `mvn clean test` can run without connecting to the application database.

| Test ID | Pattern / test class | Current test methods | Main behaviour covered |
|---|---|---:|---|
| UT-A | `DecoratorPatternTest` | 9 | Basic stream, valid/invalid authentication, compression round-trip, encryption/decryption, tamper detection, invalid AES key, decorator composition and authentication failure. |
| UT-B | `CompositePatternTest` | 13 | Hierarchy construction, valid/invalid parent-child relationships, duplicate/removal rules, recursive shutdown/restore, empty branches and usage aggregation/reporting. |
| UT-C | `ComplaintChainTest` | 13 | Priority mapping, low/medium/high/critical routing, escalation records, handler stop/forward behaviour and missing staff/lookup data. Uses a fake repository, not the live database. |
| UT-D | `FlyweightPatternTest` | 8 | Shared-instance reuse, distinct configurations, hash validation, 10,000 streetlights reusing 20 configurations, retention and concurrent access. |
| UT-E | `InterpreterPatternTest` | 19 | Comparisons, logical operators, precedence, parentheses, case handling, allowed variables and invalid expressions. |
| **Total** | **5 classes** | **62** | **Pattern-level unit test coverage.** |

The count above is the number of `@Test` methods present in the supplied source. It is **not** a statement that the tests have passed. Record the actual Maven result after running the commands in Section 5.

### 4.1 Run tests in IntelliJ IDEA

1. Open the project folder containing `pom.xml`.
2. Allow IntelliJ to load/reload the Maven project.
3. Confirm Project SDK and Maven runner use JDK 17.
4. Open the Maven tool window and run `Lifecycle → clean`, then `Lifecycle → test`; alternatively use the terminal commands below.
5. Expand `target/surefire-reports` and inspect the generated `.txt` and `.xml` files.

## 5. Build and test commands

Run these commands in the project root — the folder containing `pom.xml`.

```bash
mvn clean test
```

This compiles the project and executes the JUnit tests. A successful Maven exit code and Surefire reports with zero failures/errors are the evidence to record.

```bash
mvn clean package
```

This performs a clean build, runs the test lifecycle, and packages the deployable WAR. The expected output is:

```text
target/SmartCity360.war
```

Do not manually create or edit Surefire result files. Preserve the actual reports generated by Maven.

For Windows PowerShell, the optional helper script is:

```powershell
.\scripts\run-tests.ps1
```

The script runs the test and package lifecycle and saves console output and Surefire reports under `test-evidence/`. It does not connect to MySQL, start Payara, or change application data.

## 6. Database integration test cases

These tests verify the boundary between the application and MySQL. Perform them manually with a dedicated test database and valid lookup records. Select existing foreign-key values from the database; do not assume IDs or insert guessed lookup IDs.

| ID | Module | Test action | Expected result |
|---|---|---|---|
| IT-01 | Connection | Start MySQL and open each module that loads database data. | Application connects using configured environment variables; no credentials are displayed. |
| IT-02 | City hierarchy | Load city, district, street and building data. | The page displays the records returned by the database, including nested children. |
| IT-03 | City hierarchy | Add a test building under an existing street using valid required fields. | The record is saved and appears after refresh; the parent relationship is correct. |
| IT-04 | Energy | Load meters and available buildings/options. | Dropdowns and meter list contain database-backed values. |
| IT-05 | Energy | Process a valid test meter stream with a valid authentication token. | The request succeeds and the processing result/log is persisted as designed. |
| IT-06 | Energy | Submit an invalid token or invalid input. | Processing is rejected safely; no successful reading is recorded for the rejected request. |
| IT-07 | Civic requests | Submit a test complaint with valid category, location and priority lookup values. | Complaint is saved with the authenticated submitter and expected initial status/assignment. |
| IT-08 | Civic requests | Review the complaint escalation history. | Escalation records correspond to the expected handler sequence and complaint ID. |
| IT-09 | Street lighting | Load existing streetlights and shared configurations. | Records and configuration choices are retrieved from the database. |
| IT-10 | Street lighting | Add a test streetlight using a valid district/street/configuration. | The generated streetlight ID is returned; the saved record appears after refresh. |
| IT-11 | City policies | Load policy summary, policy definitions and evaluation history. | The page reflects database results; empty data is shown as empty rather than invented demo records. |
| IT-12 | City policies | Evaluate a valid policy expression. | The Java Interpreter returns the expected result and the evaluation is recorded if persistence is enabled. |

For every write test, record the test database name, the test record identifier, and the result. Remove test records only through an approved cleanup process after confirming they are not needed as evidence. Do not delete production records.

## 7. Functional test cases

| ID | Module | Test action | Expected result |
|---|---|---|---|
| FT-01 | Login | Sign in with a valid active account. | User reaches the appropriate landing page and session information is available. |
| FT-02 | Login | Sign in with an incorrect password. | Login is rejected without revealing whether a password or account detail was incorrect. |
| FT-03 | City hierarchy / Composite | Expand and collapse city hierarchy nodes. | City → District → Street → Building relationships are displayed consistently. |
| FT-04 | City hierarchy / Composite | Run an allowed power operation on a test hierarchy node. | The operation applies to the intended component/subtree and the resulting state is reflected. |
| FT-05 | Energy / Decorator | Process a valid meter stream with authentication enabled. | Authentication is applied before the later processing layers. |
| FT-06 | Energy / Decorator | Enable encryption and/or compression and process a test payload. | The selected decorators are applied; valid processing can recover the expected original payload where applicable. |
| FT-07 | Civic requests / Chain | Submit Low, Medium and High/Critical test complaints. | Routing follows the configured escalation policy: Local Staff, then Supervisor, then City Manager as required. |
| FT-08 | Civic requests / Chain | Inspect complaint history. | The displayed assignment/escalation sequence belongs to the selected complaint. |
| FT-09 | Street lighting / Flyweight | Add multiple lights with the same configuration. | Each light retains its own extrinsic data while the shared configuration is reused. |
| FT-10 | Street lighting / Flyweight | Add a light with a different configuration. | A distinct configuration is used; the generated ID is read-only and unique. |
| FT-11 | City policies / Interpreter | Evaluate `NOISE_LEVEL > 60 AND PARKING_HOUR < 8` using Noise 75 and Parking Hour 6. | Evaluation returns true. |
| FT-12 | City policies / Interpreter | Change Parking Hour to 12 and evaluate the same expression. | Evaluation returns false. |
| FT-13 | Navigation | Use the sidebar to open each module and return to the dashboard. | Links work and the correct active navigation item is highlighted. |
| FT-14 | Forms | Submit missing or invalid required values. | Validation prevents invalid submission and gives a clear message. |

## 8. Authentication and role-based access tests

The current application defines these role IDs: Citizen = 1, Local Staff = 2, Supervisor = 3, City Manager = 4. Use real test accounts from the test database and verify the role assigned to each account before testing. Role IDs are not user IDs.

| ID | Account / role | Test action | Expected result |
|---|---|---|---|
| RT-01 | No session | Open a protected page directly. | Browser is redirected to login, or a protected API returns HTTP 401. |
| RT-02 | Citizen | Open a city-management page/API. | Access is denied; protected city-management operations are not available. |
| RT-03 | Local Staff | Open a city-management page/API. | Access is denied; protected city-management operations are not available. |
| RT-04 | Supervisor | Open a city-management page/API. | Access is denied; protected city-management operations are not available. |
| RT-05 | City Manager | Open city hierarchy, energy, street lighting and city policies. | Access is allowed, subject to normal validation and server-side checks. |
| RT-06 | Citizen | Submit a valid complaint. | Complaint submission is accepted and records the signed-in user as submitter. |
| RT-07 | Local Staff | Submit a valid complaint. | Complaint submission is accepted and records the signed-in user as submitter. |
| RT-08 | Supervisor | Submit a valid complaint. | Complaint submission is accepted and records the signed-in user as submitter. |
| RT-09 | City Manager | Submit a valid complaint. | Complaint submission is accepted and records the signed-in user as submitter. |
| RT-10 | Each role | View complaint list/details available to that role. | Results are restricted according to the application's role-specific complaint scope. |
| RT-11 | Invalid session/role | Use a session with a missing, invalid or unsupported role value. | Access is rejected; no protected action is performed. |

If a role's account is unavailable in the test database, mark the test **Not Run** and explain why. Do not use another role's account and label it as the missing role.

## 9. Browser and user-interface tests

| ID | Area | Test action | Expected result |
|---|---|---|---|
| UI-01 | All pages | Open each page at desktop width. | No broken layout, missing styles, JavaScript errors or horizontal overflow. |
| UI-02 | All pages | Resize to tablet and mobile widths. | Navigation and page content remain usable and readable. |
| UI-03 | Tables | Load pages with records and with no records. | Data, empty states and loading/error states are clear and do not show fabricated records. |
| UI-04 | Forms | Test required fields, dropdowns, dates and optional fields. | Labels and validation messages are clear; optional fields can remain blank where allowed. |
| UI-05 | Actions | Test add, evaluate, filter, expand/collapse and power buttons. | Each visible control performs the action indicated by its label. |
| UI-06 | Feedback | Trigger success and failure cases. | Feedback is understandable and does not expose SQL, stack traces, passwords, tokens or keys. |
| UI-07 | Session | Sign out and use browser back to revisit a protected page. | Protected content is not available from a stale cached page. |

## 10. Deployment and final verification

1. Configure `SMARTCITY_DB_URL`, `SMARTCITY_DB_USERNAME`, `SMARTCITY_DB_PASSWORD` and (where required) `SMARTCITY_AES_KEY` in the Payara process environment.
2. Build the WAR with `mvn clean package`.
3. Deploy `target/SmartCity360.war` to Payara Server 6.
4. Check Payara logs for deployment errors and application exceptions.
5. Sign in and repeat the essential functional checks against the intended database.
6. Confirm all five design-pattern modules are available and demonstrable.
7. Capture screenshots of actual successful test results and relevant database records for the report. Hide credentials and personal data.

## 11. Test completion criteria

Testing is ready for submission when:

- The five JUnit test classes execute successfully and their actual Surefire reports are saved.
- `mvn clean package` completes successfully and creates `target/SmartCity360.war`.
- Critical database, module, authentication and UI test cases have been executed in the test environment.
- Failed tests are fixed and rerun, or documented honestly with their impact and resolution.
- The execution record in `TEST_EXECUTION_RECORD_TEMPLATE.md` is completed using actual outcomes.
- The report and demonstration describe only functionality that was verified in the deployed application.

**Important:** Do not label a test as Passed based only on its expected result. A test is Passed only after the action was performed and the actual result matched the expected result.


## Security regression checks added in the 11 November review

Run these checks manually on the deployed Payara application after applying the login-attempt migration:

1. Sign in with a valid account for each role in the `roles` table (Citizen, Local Staff, Supervisor, City Manager and Administrator).
2. Confirm City Manager and Administrator can access City Hierarchy, Energy Systems, Street Lighting and City Policies. Confirm Citizen, Local Staff and Supervisor receive an access-denied response for those city-management routes.
3. Submit an authenticated POST/PUT/DELETE request without `X-CSRF-Token` or `_csrf`; it must be rejected with HTTP 403. Repeat with the current session token; the request should proceed subject to its normal role and validation rules.
4. Sign out using the sidebar button. Confirm the request is POST and the session is invalidated. A GET request to `/logout` must not sign out the user.
5. Submit five invalid login attempts for the same normalized email and client IP. Confirm the next attempt is temporarily blocked for 15 minutes. Confirm a successful login clears the failed-attempt record.
6. Confirm the application reports a clear database error if the login-attempt migration has not been applied; apply the migration and retry.

Do not test lockout by repeatedly submitting invalid passwords against an account you need for a live demonstration. Use a dedicated test account and restore its access after testing.