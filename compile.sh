#!/bin/bash

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Project directories
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SRC_DIR="${PROJECT_DIR}/src"
BIN_DIR="${PROJECT_DIR}/bin"
LIB_DIR="${PROJECT_DIR}/lib"

echo -e "${BLUE}Java Game Engine Compilation Script${NC}"
echo -e "${BLUE}====================================${NC}"

# Check if Java is installed
if ! command -v javac &> /dev/null; then
    echo -e "${RED}Error: javac not found. Please install Java JDK 8 or higher.${NC}"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(javac -version 2>&1 | awk '{print $2}' | cut -d'.' -f1-2)
echo -e "${BLUE}Java version: ${JAVA_VERSION}${NC}"

# Create bin directory if it doesn't exist
mkdir -p "${BIN_DIR}"

# Build classpath - include all JAR files
echo -e "${YELLOW}Building classpath...${NC}"
CLASSPATH=""

# Add all JAR files from lib directory and subdirectories
while IFS= read -r -d '' jar_file; do
    if [[ -z "$CLASSPATH" ]]; then
        CLASSPATH="$jar_file"
    else
        CLASSPATH="$CLASSPATH:$jar_file"
    fi
done < <(find "${LIB_DIR}" -name "*.jar" -print0)

echo -e "${GREEN}Found $(echo "$CLASSPATH" | tr ':' '\n' | wc -l) JAR files in classpath${NC}"

# Find all Java source files
echo -e "${YELLOW}Finding Java source files...${NC}"
SOURCE_FILES=($(find "${SRC_DIR}" -name "*.java"))
echo -e "${GREEN}Found ${#SOURCE_FILES[@]} Java source files${NC}"

# Compile the project
echo -e "${YELLOW}Compiling Java Game Engine...${NC}"
echo -e "${BLUE}Command: javac -cp \"${CLASSPATH}\" -d \"${BIN_DIR}\" ${SOURCE_FILES[@]}${NC}"

if javac -cp "${CLASSPATH}" -d "${BIN_DIR}" "${SOURCE_FILES[@]}"; then
    echo -e "${GREEN}✓ Compilation successful!${NC}"
else
    echo -e "${RED}✗ Compilation failed!${NC}"
    exit 1
fi

# Copy resources to bin directory
echo -e "${YELLOW}Copying resources...${NC}"

# Copy configuration files
if [ -f "${SRC_DIR}/default-gui.ini" ]; then
    cp "${SRC_DIR}/default-gui.ini" "${BIN_DIR}/"
    echo -e "${GREEN}✓ Copied default-gui.ini${NC}"
fi

if [ -f "${SRC_DIR}/default-style.stl" ]; then
    cp "${SRC_DIR}/default-style.stl" "${BIN_DIR}/"
    echo -e "${GREEN}✓ Copied default-style.stl${NC}"
fi

# Copy language files
if [ -d "${PROJECT_DIR}/lang" ]; then
    cp -r "${PROJECT_DIR}/lang" "${BIN_DIR}/"
    echo -e "${GREEN}✓ Copied language files${NC}"
fi

# Copy sources directory (textures, assets)
if [ -d "${PROJECT_DIR}/sources" ]; then
    cp -r "${PROJECT_DIR}/sources" "${BIN_DIR}/"
    echo -e "${GREEN}✓ Copied game assets${NC}"
fi

echo -e "${GREEN}✓ Build complete!${NC}"
echo
echo -e "${BLUE}Output directory: ${BIN_DIR}${NC}"
echo -e "${BLUE}Main class:${NC}"
echo -e "  ${YELLOW}Main:${NC} com.gameengine.Main"
echo
echo -e "${BLUE}To run the engine, use:${NC}"
echo -e "${YELLOW}java -cp \"${BIN_DIR}:${CLASSPATH}\" com.gameengine.Main${NC}"
echo
echo -e "${BLUE}The engine will initialize with default configuration.${NC}"

# Automatically try to run the engine
echo
echo -e "${YELLOW}Attempting to run the engine...${NC}"
if java -cp "${BIN_DIR}:${CLASSPATH}" com.gameengine.Main; then
    echo -e "${GREEN}✓ Engine ran successfully!${NC}"
else
    echo -e "${RED}✗ Engine failed to run. Check the error messages above.${NC}"
fi