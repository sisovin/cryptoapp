# Release Build Signing - Quick Start Guide

## ✅ What Was Added

Your project now has **complete signing configuration** for release builds:

### 1. Signing Configuration in `app/build.gradle.kts`
- ✓ Added `signingConfigs` block with release configuration
- ✓ Configured to load credentials from multiple sources (properties file, environment variables, or defaults)
- ✓ Applied signing config to the `release` buildType

### 2. Security Files
- ✓ `keystore.properties.template` - Template for your credentials
- ✓ `keystore.properties` - Added to `.gitignore` (safe to use)
- ✓ `setup-keystore.ps1` - Interactive setup script

### 3. Documentation
- ✓ `SIGNING_README.md` - Complete signing documentation
- ✓ This quick start guide

---

## 🚀 Quick Setup (Choose One Method)

### Method A: Using PowerShell Script (Easiest)
```powershell
.\setup-keystore.ps1
```
Follow the prompts to enter your keystore credentials.

### Method B: Manual Setup
1. Copy the template:
   ```bash
   copy keystore.properties.template keystore.properties
   ```

2. Edit `keystore.properties` with your actual credentials:
   ```properties
   storePassword=YOUR_ACTUAL_PASSWORD
   keyPassword=YOUR_ACTUAL_PASSWORD
   keyAlias=release
   storeFile=../release.keystore
   ```

### Method C: Using Environment Variables (CI/CD)
```powershell
$env:KEYSTORE_PASSWORD="your_password"
$env:KEY_PASSWORD="your_password"
$env:KEY_ALIAS="release"
```

---

## 📦 Building Release APK

After setting up credentials, build your signed release APK:

```bash
# Build release APK
.\gradlew assembleRelease

# Output location:
# app/build/outputs/apk/release/app-release.apk
```

For Google Play (AAB format):
```bash
.\gradlew bundleRelease

# Output location:
# app/build/outputs/bundle/release/app-release.aab
```

---

## 🔍 Verify Signing

Check your signing configuration:
```bash
.\gradlew signingReport
```

Look for the "Variant: release" section to see your release signing info.

---

## 📋 What's in Your Project Now

```
cryptoapp/
├── release.keystore              # Your release keystore (already exists)
├── upload.keystore               # Your upload keystore (already exists)
├── keystore.properties.template  # Template for credentials (NEW)
├── keystore.properties           # Your actual credentials (NEW - in .gitignore)
├── setup-keystore.ps1            # Setup helper script (NEW)
├── SIGNING_README.md             # Full documentation (NEW)
├── QUICK_START.md                # This file (NEW)
├── .gitignore                    # Updated to include keystore.properties
└── app/
    └── build.gradle.kts          # Updated with signing config
```

---

## ⚠️ Important Security Notes

1. ✅ `keystore.properties` is in `.gitignore` - safe to use
2. ✅ Never commit `keystore.properties` to Git
3. ✅ Keep your keystore files backed up securely
4. ✅ Use a password manager for keystore passwords

---

## 🧪 Test Your Setup

1. Set up credentials (using any method above)
2. Build release: `.\gradlew assembleRelease`
3. Check output: `app\build\outputs\apk\release\app-release.apk`

If successful, your APK is signed and ready for distribution!

---

## 🆘 Troubleshooting

**Error: "keystore password was incorrect"**
- Check your credentials in `keystore.properties`
- Verify the keystore file path is correct
- Ensure the keyAlias matches your keystore

**Error: "keystore.properties not found"**
- This is normal if not created yet
- The build will use default values (password: "android")
- Run setup script or create the file manually

**Want to see what's being used?**
```bash
.\gradlew signingReport
```

---

## 📚 Need More Info?

See `SIGNING_README.md` for comprehensive documentation.

---

**Status: ✅ Your project is ready for signed release builds!**

