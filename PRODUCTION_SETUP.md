# Production Setup Checklist

## 1. Secrets

Create a local `.env` from `.env.example` and fill in real values. Never commit `.env`.

Rotate any credentials that were previously committed to Git, especially the Gmail app password and JWT signing secret.

## 2. Database

Create a dedicated MySQL application user instead of using `root`, for example:

```sql
CREATE USER 'order_app'@'%' IDENTIFIED BY 'REPLACE_WITH_A_STRONG_PASSWORD';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX, REFERENCES ON order_management.* TO 'order_app'@'%';
FLUSH PRIVILEGES;
```

The application is configured for `ddl-auto=validate` by default. Use a migration tool such as Flyway or Liquibase for schema creation and upgrades before production deployment.

## 3. Bootstrap admin

Default admin creation is disabled by default. For a new environment, temporarily set:

```text
APP_BOOTSTRAP_ADMIN_ENABLED=true
APP_BOOTSTRAP_ADMIN_EMAIL=admin@example.com
APP_BOOTSTRAP_ADMIN_PASSWORD=<strong-password>
```

After the initial admin account is created, set `APP_BOOTSTRAP_ADMIN_ENABLED=false`.

## 4. Docker

The Docker application uses Kafka's internal listener at `kafka:29092` and Redis at `redis:6379`.

Redis and Kafka health checks are used before starting the application container.

## 5. Monitoring

Only these actuator endpoints are publicly exposed:

- `/actuator/health`
- `/actuator/info`
- `/actuator/prometheus`

Other actuator endpoints require the ADMIN role.
