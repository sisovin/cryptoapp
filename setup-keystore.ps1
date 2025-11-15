# PowerShell script to set up keystore.properties
# Run this script to create keystore.properties with your credentials

Write-Host "=== Keystore Properties Setup ===" -ForegroundColor Cyan
Write-Host ""

# Check if keystore.properties already exists
if (Test-Path "keystore.properties") {
    Write-Host "keystore.properties already exists!" -ForegroundColor Yellow
    $overwrite = Read-Host "Do you want to overwrite it? (y/N)"
    if ($overwrite -ne "y" -and $overwrite -ne "Y") {
        Write-Host "Setup cancelled." -ForegroundColor Red
        exit
    }
}

# Get keystore information
Write-Host "Please provide your keystore information:" -ForegroundColor Green
$storePassword = Read-Host "Enter store password" -AsSecureString
$keyPassword = Read-Host "Enter key password" -AsSecureString
$keyAlias = Read-Host "Enter key alias (default: release)"
$storeFile = Read-Host "Enter keystore file path (default: ../release.keystore)"

# Use defaults if not provided
if ([string]::IsNullOrWhiteSpace($keyAlias)) { $keyAlias = "release" }
if ([string]::IsNullOrWhiteSpace($storeFile)) { $storeFile = "../release.keystore" }

# Convert SecureString to plain text
$BSTR = [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($storePassword)
$storePasswordPlain = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto($BSTR)
[System.Runtime.InteropServices.Marshal]::ZeroFreeBSTR($BSTR)

$BSTR = [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($keyPassword)
$keyPasswordPlain = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto($BSTR)
[System.Runtime.InteropServices.Marshal]::ZeroFreeBSTR($BSTR)

# Create keystore.properties
$content = @"
# Keystore properties
# DO NOT commit this file to version control!

storePassword=$storePasswordPlain
keyPassword=$keyPasswordPlain
keyAlias=$keyAlias
storeFile=$storeFile
"@

$content | Out-File -FilePath "keystore.properties" -Encoding UTF8

Write-Host ""
Write-Host "✓ keystore.properties created successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "IMPORTANT: This file contains sensitive credentials." -ForegroundColor Yellow
Write-Host "Make sure it's listed in .gitignore to prevent committing it." -ForegroundColor Yellow
Write-Host ""
Write-Host "You can now build the release APK with:" -ForegroundColor Cyan
Write-Host "  .\gradlew assembleRelease" -ForegroundColor White

