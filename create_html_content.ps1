$outputFile = "D:\WorkSpace\MPJ\BookStoreOnline\ALL_HTML_CONTENT.txt"
$staticFolder = "D:\WorkSpace\MPJ\BookStoreOnline\bookstoreonline\src\main\resources\static\web"

$htmlFiles = @(Get-ChildItem -Path $staticFolder -Recurse -Filter "*.html" -ErrorAction SilentlyContinue | Sort-Object FullName)
$total = $htmlFiles.Count
Write-Host "Tim duoc $total file HTML"

if (Test-Path $outputFile) { Remove-Item $outputFile -Force }

"================================================================================" | Out-File $outputFile -Encoding UTF8
"                    TONG HOP NOI DUNG TAT CA FILE HTML                          " | Out-File $outputFile -Append -Encoding UTF8
"================================================================================" | Out-File $outputFile -Append -Encoding UTF8
"" | Out-File $outputFile -Append -Encoding UTF8
"Thoi gian tao: $((Get-Date).ToString('dd/MM/yyyy HH:mm:ss'))" | Out-File $outputFile -Append -Encoding UTF8
"Tong so file: $total" | Out-File $outputFile -Append -Encoding UTF8
"" | Out-File $outputFile -Append -Encoding UTF8
"--------------------------------------------------------------------------------" | Out-File $outputFile -Append -Encoding UTF8
"" | Out-File $outputFile -Append -Encoding UTF8

$count = 0
foreach ($file in $htmlFiles) {
    $count++
    $relativePath = $file.FullName.Replace("$staticFolder\", "")
    
    "" | Out-File $outputFile -Append -Encoding UTF8
    "" | Out-File $outputFile -Append -Encoding UTF8
    "################################################################################" | Out-File $outputFile -Append -Encoding UTF8
    "# FILE [$count / $total]: $relativePath" | Out-File $outputFile -Append -Encoding UTF8
    "# Full Path: $($file.FullName)" | Out-File $outputFile -Append -Encoding UTF8
    "# Size: $([Math]::Round($file.Length / 1024, 2)) KB" | Out-File $outputFile -Append -Encoding UTF8
    "################################################################################" | Out-File $outputFile -Append -Encoding UTF8
    "" | Out-File $outputFile -Append -Encoding UTF8
    
    try {
        $content = Get-Content -Path $file.FullName -Raw -Encoding UTF8
        $content | Out-File $outputFile -Append -Encoding UTF8
    } catch {
        "LOI: Khong the doc file - $($_.Exception.Message)" | Out-File $outputFile -Append -Encoding UTF8
    }
    
    "" | Out-File $outputFile -Append -Encoding UTF8
    "" | Out-File $outputFile -Append -Encoding UTF8
    
    if ($count % 10 -eq 0 -or $count -eq 1 -or $count -eq $total) {
        Write-Host "Da xu ly $count/$total file..."
    }
}

if (Test-Path $outputFile) {
    $fileInfo = Get-Item $outputFile
    Write-Host "HOAN THANH!"
    Write-Host "File output: $outputFile"
}
