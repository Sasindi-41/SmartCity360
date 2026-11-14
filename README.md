# SmartCity360
## Smart City Management Platform

SmartCity360 is a Java 17, Jakarta EE 10 web application deployed on Payara Server 6 and backed by a MySQL database. It provides a centralised platform for managing city infrastructure, energy systems, civic complaints, street lighting and city policies.

The application demonstrates five object-oriented design patterns through practical smart-city management modules.

---

## Requirements

- JDK 17
- Maven 3.8+
- MySQL 8.x
- Payara Server 6
- IntelliJ IDEA (recommended)

---

## Project Structure

```text
SmartCity360/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   ├── resources/
│   │   └── webapp/
│   └── test/
│
├── database/
│   └── smartcity360_db.sql
│
├── docs/
│   └── testing/
│       ├── TEST_PLAN.md
│       └── TEST_EXECUTION_RECORD_TEMPLATE.md
│
├── scripts/
│   └── run-tests.ps1
│
├── test-evidence/
│
├── .env.example
├── .gitignore
├── README.md
└── pom.xml
```

The `test-evidence/` directory is generated when the testing script is executed. Additional documentation and UML diagrams can be stored under `docs/`.

---

## Database Configuration

SmartCity360 uses MySQL 8.x with the database name:

`smartcity360_db`

### 1. Database Installation

The database SQL file is provided at:

`database/smartcity360_db.sql`

For a fresh installation:

1. Open MySQL Workbench.
2. Open `database/smartcity360_db.sql`.
3. Execute the SQL script.
4. Verify that the `smartcity360_db` database and its required tables have been created.
5. Verify that the required lookup and sample records are available.

**Important:** Make sure the SQL export includes all tables and data required by the application, including the `login_attempts` table.

For an existing database, do not re-import the complete SQL export over your working database. Apply only the required database migrations.

The `login_attempts` table is used to track failed login attempts and support temporary login lockouts. It has already been created in the current development database.

### 2. Database User

Create a dedicated MySQL application user with only the permissions required by the application.

Avoid using the MySQL `root` account for normal application execution.

### 3. Environment Variables

Configure the following environment variables for the process that starts Payara Server:

| Variable | Description |
|---|---|
| `SMARTCITY_DB_URL` | Database connection URL. Optional; defaults to the local `smartcity360_db` URL. |
| `SMARTCITY_DB_USERNAME` | MySQL username. Optional; defaults to `root` for compatibility with an existing local setup. |
| `SMARTCITY_DB_PASSWORD` | MySQL password. Required. |
| `SMARTCITY_AES_KEY` | Base64-encoded 32-byte AES-256 key used for energy-meter authentication-token encryption. |

The application reads database credentials and encryption keys from environment variables rather than storing real credentials in source code.

### 4. PowerShell Configuration Example

The following example configures environment variables for the current PowerShell terminal session only:

```powershell
$env:SMARTCITY_DB_URL = "jdbc:mysql://localhost:3306/smartcity360_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"

$env:SMARTCITY_DB_USERNAME = "smartcity360_app"

$env:SMARTCITY_DB_PASSWORD = "YOUR_LOCAL_PASSWORD"
```

For a **new database installation**, generate a fresh AES-256 key when using the encryption decorator:

```powershell
$keyBytes = New-Object byte[] 32

$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()

$rng.GetBytes($keyBytes)

$rng.Dispose()

$env:SMARTCITY_AES_KEY = [Convert]::ToBase64String($keyBytes)
```

**Important AES key warning:**

When using an existing SmartCity360 database, configure the original `SMARTCITY_AES_KEY` that was used to encrypt existing meter authentication tokens.

Do not generate a replacement key for an existing database unless you have a proper migration and re-encryption plan. Replacing the key may prevent the application from decrypting previously stored tokens.

Keep the same AES key available to the application for as long as previously encrypted data needs to be decrypted or interpreted. Back it up securely and never commit it to source control.

Restart Payara Server after changing environment variables.

### 5. Credential Security

- Do not store real database credentials in Java, JSP, JavaScript or configuration files committed to source control.
- Do not include real passwords or encryption keys in screenshots, reports or public repositories.
- Keep local environment files private.
- The `.env.example` file should contain placeholders only.
- Rotate any database password that was previously committed or shared.

---

## Build and Deployment

Run the following commands from the project root directory, where `pom.xml` is located.

### 1. Run Unit Tests

```bash
mvn clean test
```

### 2. Build the Application

```bash
mvn clean package
```

The generated WAR file is located at:

`target/SmartCity360.war`

### 3. Deploy to Payara

1. Start Payara Server 6.
2. Configure the required database environment variables.
3. Deploy `target/SmartCity360.war` to Payara.
4. Access the application using the deployed application URL.

Ensure that MySQL is running and the application can connect to `