# Release Build Signing Configuration

This document explains how to configure signing for release builds.

## Setup Instructions

### Method 1: Using keystore.properties file (Recommended for local builds)

1. Copy the template file:
   ```bash
   cp keystore.properties.template keystore.properties
   ```

2. Edit `keystore.properties` and fill in your actual keystore credentials:
   ```properties
   storePassword=your_actual_store_password
   keyPassword=your_actual_key_password
   keyAlias=release
   storeFile=../release.keystore
   ```

3. The file is already added to `.gitignore` to prevent committing sensitive data.

### Method 2: Using Environment Variables (Recommended for CI/CD)

Set the following environment variables:
- `KEYSTORE_PASSWORD`: Your keystore password
- `KEY_PASSWORD`: Your key password
- `KEY_ALIAS`: Your key alias (default: release)

Example (Windows PowerShell):
```powershell
$env:KEYSTORE_PASSWORD="your_password"
$env:KEY_PASSWORD="your_password"
$env:KEY_ALIAS="release"
```

Example (Linux/Mac):
```bash
export KEYSTORE_PASSWORD="your_password"
export KEY_PASSWORD="your_password"
export KEY_ALIAS="release"
```

## Building Release APK

After configuring the signing, build the release APK:

```bash
# Build release APK
./gradlew assembleRelease

# Build release AAB (for Google Play)
./gradlew bundleRelease
```

The signed APK will be located at:
`app/build/outputs/apk/release/app-release.apk`

The signed AAB will be located at:
`app/build/outputs/bundle/release/app-release.aab`

## Keystore Files

- `release.keystore`: Used for production releases
- `upload.keystore`: Used for Google Play App Signing (if configured)

## Security Notes

- Never commit `keystore.properties` to version control
- Keep your keystore files secure and backed up
- Use different keystores for different apps
- Store keystore passwords in a secure password manager

