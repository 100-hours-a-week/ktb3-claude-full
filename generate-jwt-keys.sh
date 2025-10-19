#!/bin/bash

# 상대 경로 기준 키 저장 위치
KEY_DIR="src/main/resources/keys"
PRIVATE_KEY="${KEY_DIR}/jwtRS256.key"
PUBLIC_KEY="${KEY_DIR}/jwtRS256.key.pub"

# 키 디렉토리 생성 (없을 경우)
mkdir -p "${KEY_DIR}"

echo "🔐 Generating RSA private key..."
ssh-keygen -t rsa -b 2048 -m PEM -f "${PRIVATE_KEY}" -N ""

echo "📤 Extracting public key..."
openssl rsa -in "${PRIVATE_KEY}" -pubout -outform PEM -out "${PUBLIC_KEY}"

# 권한 설정
chmod 600 "${PRIVATE_KEY}"
chmod 644 "${PUBLIC_KEY}"

echo "✅ RSA key pair generated successfully!"
echo "📍 Private key: ${PRIVATE_KEY}"
echo "📍 Public key:  ${PUBLIC_KEY}"
