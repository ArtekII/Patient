# Patient — PostgreSQL setup

Requires Java 21, a Jakarta-compatible Tomcat, and PostgreSQL.

## Database configuration

Copy `src/main/.env.example` to `src/main/.env` if you do not already have one,
then enter your PostgreSQL database URL, username and password. Existing files
are preserved. Plain `KEY=value` and `export KEY='value'` lines are accepted;
shell expansion is not performed. Environment variables override file values.

When running from the project directory, the application reads `src/main/.env`.
For Tomcat, set the absolute file path in Tomcat's `bin/setenv.sh`:

```bash
export DB_CONFIG_FILE='/home/itu/Bureau/S5/MrNaina/PROJET_PATIENT/Patient/src/main/.env'
```

Restart Tomcat after changing its environment. Credentials are excluded from Git
and from the WAR.

Create your database and role if needed, then create the table using your actual
host, database and username (psql prompts for the password):

```bash
psql -h localhost -U patient -d patient -W -v ON_ERROR_STOP=1 -f sql/init.sql
```

## Build and check

```bash
bash deploy.sh --build-only
java -cp 'build/WEB-INF/classes:lib/*' patient.config.CheckDatabase
```

Spring modules in `lib/` use version `7.0.0-M8`. The JDBC and transaction jars
must be binary artifacts, not `-javadoc.jar` files. Replaced jars are kept in
`lib/unused/` and are excluded from compilation and packaging. The Servlet API
is used for compilation and supplied by Tomcat at runtime.