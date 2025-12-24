# List available recipes
default:
    @just --list

# Start MySQL and LocalStack containers
infra-start:
    docker compose -f tools/local/docker-compose.yml up -d
    @echo "Waiting for MySQL to be ready..."
    @until docker compose -f tools/local/docker-compose.yml exec -T mysql mysqladmin ping -h localhost -u root -proot --silent 2>/dev/null; do \
        echo "MySQL is not ready yet, waiting..."; \
        sleep 2; \
    done
    @echo "MySQL is ready!"
    @echo "Waiting for LocalStack to be ready..."
    @until docker compose -f tools/local/docker-compose.yml exec -T localstack curl -sf http://localhost:4566/_localstack/health >/dev/null 2>&1; do \
        echo "LocalStack is not ready yet, waiting..."; \
        sleep 2; \
    done
    @echo "LocalStack is ready!"
    @echo "Waiting for secrets to be initialized..."
    @sleep 3
    @echo "Infrastructure is ready!"

# Stop MySQL and LocalStack containers
infra-stop:
    docker compose -f tools/local/docker-compose.yml down

# Stop containers and remove data
infra-clean:
    docker compose -f tools/local/docker-compose.yml down -v

# Show container logs
infra-logs:
    docker compose -f tools/local/docker-compose.yml logs -f

# Run the application (profile: local) - requires infrastructure running
app-run: infra-start
    #!/usr/bin/env bash
    export AWS_REGION=us-east-1
    export AWS_ACCESS_KEY_ID=test
    export AWS_SECRET_ACCESS_KEY=test
    export SPRING_PROFILES_ACTIVE=local
    export SPRING_CLOUD_AWS_SECRETSMANAGER_ENDPOINT=http://localhost:4566
    export SPRING_CLOUD_AWS_REGION_STATIC=us-east-1
    ./gradlew bootRun

# Build the project (e.g., just app-build or just app-build "-x test")
app-build *args:
    ./gradlew build {{args}}

# Run tests (uses H2 in-memory database)
app-test:
    ./gradlew test

# Clean build artifacts
app-clean:
    ./gradlew clean
