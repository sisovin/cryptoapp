# Crypto App

A modern, cross-platform cryptocurrency tracking application built with Kotlin Multiplatform, Jetpack Compose, Firebase, and CoinGecko API.

## Overview

Crypto App is a comprehensive mobile application that allows users to track cryptocurrency prices, manage watchlists, and stay updated with real-time market data. The app features a clean, intuitive UI built with Jetpack Compose and supports both Android and iOS platforms through Kotlin Multiplatform.

## Features

### Core Functionality

- **Real-time Market Data**: Live cryptocurrency prices from CoinGecko API
- **Watchlist Management**: Add/remove cryptocurrencies to personal watchlist
- **Detailed Coin Information**: Comprehensive coin details including price charts, market cap, and trading volume
- **Search & Filter**: Search cryptocurrencies by name or symbol
- **Offline Support**: Cached data for offline viewing

### User Experience

- **Modern UI**: Built with Material 3 and Jetpack Compose
- **Dark/Light Theme**: Automatic theme switching based on system preferences
- **Responsive Design**: Optimized for various screen sizes
- **Smooth Animations**: Fluid transitions and loading states

### Authentication

- **Google Sign-In**: Secure authentication with Google accounts
- **Email/Password**: Traditional email-based authentication
- **Firebase Integration**: Cloud-based user management and data synchronization

## Architecture

### Tech Stack

- **Frontend**: Jetpack Compose (Android), SwiftUI (iOS)
- **Backend**: Kotlin Multiplatform shared business logic
- **Networking**: Ktor HTTP client
- **Database**: SQLDelight for local caching
- **Dependency Injection**: Koin
- **State Management**: MVI pattern with StateFlow
- **Authentication**: Firebase Auth
- **Realtime Database**: Firebase RTDB for user data

### Project Structure

```
crypto-app/
├── android/                    # Android application
│   ├── app/                    # Main Android app module
│   ├── shared/                 # KMP shared module
│   ├── core-ui/                # Reusable UI components
│   └── core-testing/           # Test utilities
├── ios/                        # iOS application (future)
└── docs/                       # Documentation
```

### Architecture Pattern

The app follows Clean Architecture principles with MVI (Model-View-Intent) pattern:

- **Presentation Layer**: ViewModels, Compose UI
- **Domain Layer**: Use cases, business logic
- **Data Layer**: Repositories, API services, local database

## Getting Started

### Prerequisites

- **Android Studio**: Koala (2024.1.1) or newer
- **Kotlin**: 1.9.0+
- **Java**: 17 (recommended)
- **Gradle**: 8.10.2+
- **Firebase Project**: With Authentication and Realtime Database enabled

### Setup

1. **Clone the repository**

   ```bash
   git clone https://github.com/sisovin/crypto-app.git
   cd crypto-app/android
   ```

2. **Firebase Configuration**
   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Enable Google Authentication and Realtime Database
   - Download `google-services.json` and place it in:
     - `app/src/debug/`
     - `app/src/release/`

3. **Build the project**

   ```bash
   ./gradlew build
   ```

4. **Run on device/emulator**

   ```bash
   ./gradlew installDebug
   ```

### Firebase Rules

Set up the following Realtime Database rules for development:

```json
{
  "rules": {
    ".read": "auth != null",
    ".write": "auth != null"
  }
}
```

## API Integration

### CoinGecko API

The app integrates with CoinGecko's public API for market data:

- **Markets Endpoint**: `/api/v3/coins/markets`
- **Coin Details**: `/api/v3/coins/{id}`
- **Rate Limits**: Respects API limits with caching and debouncing

### Firebase Services

- **Authentication**: Google Sign-In and email/password
- **Realtime Database**: User watchlists and preferences
- **Analytics**: User interaction tracking (optional)

## Development

### Building Modules

```bash
# Build all modules
./gradlew build

# Build specific module
./gradlew :shared:build
./gradlew :app:build

# Run tests
./gradlew test
./gradlew :app:connectedAndroidTest
```

### Code Style

The project follows Kotlin coding conventions and uses:

- Detekt for static analysis
- Ktlint for code formatting
- Spotless for consistent formatting

### Testing Strategy

- **Unit Tests**: Business logic, ViewModels, repositories
- **Integration Tests**: API calls, database operations
- **UI Tests**: Compose component testing
- **End-to-End Tests**: Full user flows

## Deployment

### Android

1. **Generate signed APK/AAB**

   ```bash
   ./gradlew assembleRelease
   ./gradlew bundleRelease
   ```

2. **Firebase App Distribution**

   ```bash
   ./gradlew appDistributionUploadRelease
   ```

### CI/CD

GitHub Actions workflow includes:

- Automated testing
- Code quality checks
- Release builds
- Firebase distribution

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Review Process

- All PRs require review
- Tests must pass
- Code coverage maintained
- Follows established patterns

## Security

- **API Keys**: No sensitive keys stored in code
- **Authentication**: Firebase Auth for secure user sessions
- **Data Encryption**: Local database encrypted
- **Network Security**: HTTPS-only communications

## Performance

- **Lazy Loading**: Efficient list rendering with LazyColumn
- **Image Caching**: Coil for optimized image loading
- **Database Optimization**: SQLDelight with proper indexing
- **Memory Management**: Proper ViewModel scoping

## Known Issues & Limitations

- iOS support: Currently Android-only, iOS implementation planned
- Offline sync: Basic caching implemented, full sync pending
- Push notifications: Not implemented yet

## Future Enhancements

- **iOS App**: Complete SwiftUI implementation
- **Price Alerts**: Custom price notification system
- **Portfolio Tracking**: Investment portfolio management
- **News Integration**: Cryptocurrency news feeds
- **Advanced Charts**: Interactive price charts with indicators

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [CoinGecko API](https://www.coingecko.com/en/api) for market data
- [Firebase](https://firebase.google.com/) for backend services
- [Jetpack Compose](https://developer.android.com/jetpack/compose) for UI framework
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) for cross-platform development

## Support

For support, email <support@cryptoapp.com> or join our Discord community.

---

**Built with ❤️ using Kotlin Multiplatform**
