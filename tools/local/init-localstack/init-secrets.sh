#!/bin/bash
# Initialize Secrets Manager and Parameter Store with database credentials

echo "Creating secrets in LocalStack Secrets Manager..."
# Create database URL parameter
awslocal secretsmanager create-secret \
    --name /app/db/url \
    --secret-string "jdbc:mysql://localhost:3306/app_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Tokyo&characterEncoding=UTF-8" \
    --region us-east-1

# Create username secret (plaintext)
awslocal secretsmanager create-secret \
    --name /app/db/username \
    --secret-string "app_user" \
    --region us-east-1

# Create password secret (plaintext)
awslocal secretsmanager create-secret \
    --name /app/db/password \
    --secret-string "app_password" \
    --region us-east-1

echo "Secrets created successfully!"

# List created secrets for verification
awslocal secretsmanager list-secrets --region us-east-1
