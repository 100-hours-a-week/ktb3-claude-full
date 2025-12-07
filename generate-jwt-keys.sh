#!/bin/bash

# 상대 경로 기준 키 저장 위치
MAIN_KEY_DIR="src/main/resources/keys"
TEST_KEY_DIR="src/test/resources/keys"
MAIN_PRIVATE_KEY="${MAIN_KEY_DIR}/jwtRS256.key"
MAIN_PUBLIC_KEY="${MAIN_KEY_DIR}/jwtRS256.key.pub"
TEST_PRIVATE_KEY="${TEST_KEY_DIR}/jwtRS256.key"
TEST_PUBLIC_KEY="${TEST_KEY_DIR}/jwtRS256.key.pub"

generate_key_pair() {
  local private_key=$1
  local public_key=$2
  local target_dir
  target_dir=$(dirname "${private_key}")

  mkdir -p "${target_dir}"

  echo "Generating RSA private key at ${private_key}..."
  ssh-keygen -t rsa -b 2048 -m PEM -f "${private_key}" -N ""

  echo "Extracting public key to ${public_key}..."
  openssl rsa -in "${private_key}" -pubout -outform PEM -out "${public_key}"

  chmod 600 "${private_key}"
  chmod 644 "${public_key}"
}

generate_key_pair "${MAIN_PRIVATE_KEY}" "${MAIN_PUBLIC_KEY}"
generate_key_pair "${TEST_PRIVATE_KEY}" "${TEST_PUBLIC_KEY}"

echo "RSA key pairs generated successfully!"
echo "Main  - Private: ${MAIN_PRIVATE_KEY}"
echo "Main  - Public : ${MAIN_PUBLIC_KEY}"
echo "Test  - Private: ${TEST_PRIVATE_KEY}"
echo "Test  - Public : ${TEST_PUBLIC_KEY}"
