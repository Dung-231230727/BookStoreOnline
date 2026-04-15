$outputFile = "D:\WorkSpace\MPJ\BookStoreOnline\ALL_JAVA_CONTENT.txt"
$javaFolder = "D:\WorkSpace\MPJ\BookStoreOnline\bookstoreonline\src\main\java"

$javaFiles = @(Get-ChildItem -Path $javaFolder -Recurse -Filter "*.java" -ErrorAction SilentlyContinue | Sort-Object FullName)
$total = $javaFiles.Count
Write-Host "Tim duoc $total file Java"

if (Test-Path $outputFile) { Remove-Item $outputFile -Force }

"================================================================================" | Out-File $outputFile -Encoding UTF8
"                    TONG HOP NOI DUNG TAT CA FILE JAVA                          " | Out-File $outputFile -Append -Encoding UTF8
"================================================================================" | Out-File $outputFile -Append -Encoding UTF8
"" | Out-File $outputFile -Append -Encoding UTF8
"Thoi gian tao: $((Get-Date).ToString('dd/MM/yyyy HH:mm:ss'))" | Out-File $outputFile -Append -Encoding UTF8
"Tong so file: $total" | Out-File $outputFile -Append -Encoding UTF8
"" | Out-File $outputFile -Append -Encoding UTF8

$count = 0
foreach ($file in $javaFiles) {
    $count++
    $relativePath = $file.FullName.Replace("$javaFolder\", "")
    
    "" | Out-File $outputFile -Append -Encoding UTF8
    "################################################################################" | Out-File $outputFile -Append -Encoding UTF8
    "# FILE [$count / $total]: $relativePath" | Out-File $outputFile -Append -Encoding UTF8
    "# Full Path: $($file.FullName)" | Out-File $outputFile -Append -Encoding UTF8
    "################################################################################" | Out-File $outputFile -Append -Encoding UTF8
    "" | Out-File $outputFile -Append -Encoding UTF8
    
    try {
        $content = Get-Content -Path $file.FullName -Raw -Encoding UTF8
        $content | Out-File $outputFile -Append -Encoding UTF8
    } catch {
        "LOI: Khong the doc file - $($_.Exception.Message)" | Out-File $outputFile -Append -Encoding UTF8
    }
    
    "" | Out-File $outputFile -Append -Encoding UTF8
    
    if ($count % 10 -eq 0 -or $count -eq 1 -or $count -eq $total) {
        Write-Host "Da xu ly $count/$total file..."
    }
}

if (Test-Path $outputFile) {
    Write-Host "HOAN THANH JAVA!"
}