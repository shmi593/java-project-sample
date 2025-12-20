# List available recipes
default:
    @just --list

# Start MySQL container
db-start:
    docker compose -f tools/local/docker-compose.yml up -d
    @echo "Waiting for MySQL to be ready..."
    @until docker compose -f tools/local/docker-compose.yml exec -T mysql mysqladmin ping -h localhost -u root -proot --silent 2>/dev/null; do \
        echo "MySQL is not ready yet, waiting..."; \
        sleep 2; \
    done
    @echo "MySQL is ready!"

# Stop MySQL container
db-stop:
    docker compose -f tools/local/docker-compose.yml down

# Stop MySQL container and remove data
db-clean:
    docker compose -f tools/local/docker-compose.yml down -v

# Show MySQL container logs
db-logs:
    docker compose -f tools/local/docker-compose.yml logs -f mysql

# Run the application (profile: local) - requires MySQL running
app-run: db-start
    ./gradlew bootRun --args='--spring.profiles.active=local'

# Build the project (e.g., just app-build or just app-build "-x test")
app-build *args:
    ./gradlew build {{args}}

# Run tests (uses H2 in-memory database)
app-test:
    ./gradlew test

# Clean build artifacts
app-clean:
    ./gradlew clean
