#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$SCRIPT_DIR"
SCHULFOTOS_DIR="$SCRIPT_DIR/schulfotos"

# Collect CSV files in the schulfotos directory
mapfile -t CSV_FILES < <(find "$SCHULFOTOS_DIR" -maxdepth 1 -name "*.csv" | sort)

if [[ ${#CSV_FILES[@]} -eq 0 ]]; then
  echo "No CSV files found in $SCHULFOTOS_DIR"
  exit 1
fi

echo "Available CSV files:"
for i in "${!CSV_FILES[@]}"; do
  echo "  $((i + 1))) $(basename "${CSV_FILES[$i]}")"
done

echo ""
read -rp "Select a CSV file [1-${#CSV_FILES[@]}]: " SELECTION

if ! [[ "$SELECTION" =~ ^[0-9]+$ ]] || (( SELECTION < 1 || SELECTION > ${#CSV_FILES[@]} )); then
  echo "Invalid selection: $SELECTION"
  exit 1
fi

SELECTED_CSV="${CSV_FILES[$((SELECTION - 1))]}"
echo ""
echo "Using: $(basename "$SELECTED_CSV")"
echo "Output directory: $SCHULFOTOS_DIR"
echo ""

cd "$PROJECT_DIR"
mvn -q spring-boot:run \
  -Dspring-boot.run.jvmArguments="-Dapp.csv-input-path=$SELECTED_CSV -Dapp.output-path=$SCHULFOTOS_DIR"
