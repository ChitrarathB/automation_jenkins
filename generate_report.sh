#!/bin/bash

echo "====== Running Tests & Generating ExtentReports ======"

# Clean and run tests
echo "Cleaning and running tests..."
mvn clean test

echo "====== Test Execution Complete ======"

# Find and copy reports to a fixed location for easy access
echo "Locating reports..."

# Create target directory for fixed copies of the reports
mkdir -p target/reports

# Find the latest extent-reports directory
LATEST_DIR=$(find target -name "extent-reports*" -type d | grep -v "reports/" | sort -r | head -1)

if [ -n "$LATEST_DIR" ]; then
    echo "Found reports in: $LATEST_DIR"
    
    # Look for the PDF file
    PDF_PATH=$(find "$LATEST_DIR" -name "*.pdf" | head -1)
    if [ -n "$PDF_PATH" ]; then
        # Copy to a fixed location with a consistent name
        cp "$PDF_PATH" target/reports/test-report.pdf
        echo "PDF Report copied to: target/reports/test-report.pdf ($(du -h target/reports/test-report.pdf | cut -f1))"
    else
        echo "No PDF report found."
    fi
    
    # Look for the HTML file
    HTML_PATH=$(find "$LATEST_DIR" -name "*.html" | head -1)
    if [ -n "$HTML_PATH" ]; then
        # Copy to a fixed location with a consistent name
        cp "$HTML_PATH" target/reports/test-report.html
        echo "HTML Report copied to: target/reports/test-report.html"
    else
        echo "No HTML report found."
    fi
else
    echo "No report directories found in target/"
fi

# Copy the standard Cucumber reports too
if [ -f "target/cucumber-reports/cucumber-html-report.html" ]; then
    cp "target/cucumber-reports/cucumber-html-report.html" target/reports/cucumber-report.html
    echo "Cucumber HTML Report copied to: target/reports/cucumber-report.html"
fi

if [ -f "target/cucumber-reports/cucumber-json-report.json" ]; then
    cp "target/cucumber-reports/cucumber-json-report.json" target/reports/cucumber-report.json
    echo "Cucumber JSON Report copied to: target/reports/cucumber-report.json"
fi

echo -e "\n====== Report Summary ======"
echo "All reports have been copied to the target/reports directory for easy access."
echo "Available Reports:"
ls -la target/reports/
echo "========================================================="