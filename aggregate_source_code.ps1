# Script to aggregate all source code files (.java, .js, .html, .css) into a single text file
# Usage: .\aggregate_source_code.ps1

$sourceDir = "d:\WorkSpace\MPJ\BookStoreOnline\bookstoreonline"
$outputFile = "d:\WorkSpace\MPJ\BookStoreOnline\PROJECT_SOURCE_CODE.txt"

# Clear output file if it exists
if (Test-Path $outputFile) {
    Remove-Item $outputFile -Force
}

# Get all source files, excluding document and template folders
$files = Get-ChildItem -Path $sourceDir -Include "*.java","*.js","*.html","*.css" -Recurse | `
    Where-Object { 
        $_.FullName -notlike "*\document\*" -and `
        $_.FullName -notlike "*\template\*" -and `
        -not $_.PSIsContainer 
    } | Sort-Object FullName

Write-Host "Found $($files.Count) source files" -ForegroundColor Green
Write-Host "Writing to: $outputFile" -ForegroundColor Yellow

$fileIndex = 0
foreach ($file in $files) {
    $fileIndex++
    $relativePath = $file.FullName.Replace($sourceDir, "")
    
    # Write header
    Add-Content -Path $outputFile -Value ""
    Add-Content -Path $outputFile -Value ("=" * 100)
    Add-Content -Path $outputFile -Value "FILE $fileIndex : $relativePath"
    Add-Content -Path $outputFile -Value ("=" * 100)
    Add-Content -Path $outputFile -Value ""
    
    # Write file content
    try {
        $content = Get-Content -Path $file.FullName -Raw -ErrorAction Stop
        Add-Content -Path $outputFile -Value $content
    } catch {
        Add-Content -Path $outputFile -Value "[ERROR: Could not read file - $_]"
    }
    
    Write-Host "[$fileIndex/$($files.Count)] $relativePath"
}

Write-Host ""
Write-Host "✓ Completed! File saved to: $outputFile" -ForegroundColor Green
$lineCount = (Get-Content $outputFile | Measure-Object -Line).Lines
Write-Host "Total lines: $lineCount" -ForegroundColor Cyan
