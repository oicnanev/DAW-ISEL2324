First we need to configure an environment variable with the database connection string:
```KEY_DB_URL=jdbc:postgresql://db-test:5432/db?user=dbuser&password=changeit```

> in linux add it to /etc/environment



For build and run all tests, execute:

```bash
./gradlew clean build
```

For normal run, execute:

```bash
./gradlew composeUp
```