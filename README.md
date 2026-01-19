# FakeStore API - Android Application

A modern Android application built with Java that demonstrates authentication, secure token handling, and consumption of protected APIs using the FakeStore API.

## Features

- 🔐 **Authentication System**
  - Login screen with username/password
  - Secure token storage using SharedPreferences
  - Token expiration simulation (60 seconds)
  - Automatic token refresh flow

- 🛡️ **Token Management**
  - Authorization Interceptor automatically adds `Bearer <token>` to requests
  - Token expiry detection (60 seconds validity)
  - Expired tokens are never sent
  - Automatic refresh flow when token expires
  - Concurrency handling - only one refresh request runs at a time

- 📦 **Products Management**
  - Products list screen with RecyclerView
  - Product details screen
  - Material Design UI

- 🏗️ **Architecture**
  - MVVM (Model-View-ViewModel) architecture
  - Feature-based module structure
  - Dependency Injection with Dagger 2
  - Clean separation of concerns

## Tech Stack

- **Language:** Java
- **Architecture:** MVVM (Model-View-ViewModel)
- **Dependency Injection:** Dagger 2
- **Networking:** Retrofit + OkHttp
- **UI:** Material Design 3
- **Architecture Components:** LiveData, ViewModel
- **Build System:** Gradle (Kotlin DSL)

## Project Structure

```
app/src/main/java/com/mustafa/androidtesttaskjava/
├── core/
│   ├── auth/
│   │   └── AuthManager.java          # Token management
│   ├── di/
│   │   ├── component/                # Dagger components
│   │   ├── module/                   # Dagger modules
│   │   └── scope/                    # Dagger scopes
│   └── network/
│       ├── AuthInterceptor.java      # Authorization interceptor
│       ├── RetrofitClient.java       # Retrofit client factory
│       ├── TokenRefreshHandler.java  # Refresh handler interface
│       └── TokenRefreshHandlerImpl.java
├── feature/
│   ├── auth/
│   │   ├── data/
│   │   │   ├── model/                # LoginRequest, LoginResponse
│   │   │   ├── remote/               # AuthApiService
│   │   │   └── repository/           # AuthRepository
│   │   └── ui/
│   │       ├── LoginActivity.java
│   │       └── viewmodel/            # LoginViewModel
│   └── products/
│       ├── data/
│       │   ├── model/                # Product model
│       │   ├── remote/               # ProductApiService
│       │   └── repository/           # ProductRepository
│       └── ui/
│           ├── ProductsActivity.java
│           ├── ProductDetailActivity.java
│           ├── adapter/              # ProductAdapter
│           ├── fragment/             # ProductDetailFragment
│           └── viewmodel/            # ProductsViewModel, ProductDetailViewModel
└── App.java                          # Application class
```

## Setup Instructions

### Prerequisites

- Android Studio Hedgehog or later
- JDK 11 or higher
- Android SDK (API 26+)
- Git

### Installation

1. Clone the repository:
```bash
git clone https://github.com/MustafaHasria/fake-store-api.git
cd fake-store-api
```

2. Open the project in Android Studio

3. Sync Gradle files (File → Sync Project with Gradle Files)

4. Build the project (Build → Make Project)

5. Run on an emulator or physical device

## API Endpoints

The app uses the FakeStore API:

- **Login:** `POST https://fakestoreapi.com/auth/login`
  - Body: `{"username": "mor_2314", "password": "83r5^_"}`
  - Response: `{"token": "..."}`

- **Products:** `GET https://fakestoreapi.com/products`
- **Product Details:** `GET https://fakestoreapi.com/products/{id}`

**Note:** While FakeStore doesn't enforce authentication, this app treats all product endpoints as protected and requires valid tokens.

## Token Management Details

### Token Expiration
- Tokens are valid for **60 seconds** from the time they are saved
- The app stores `tokenSavedAt` timestamp to track expiration

### Refresh Flow
- When a token expires, the app automatically triggers a refresh
- Refresh is simulated by re-calling the login endpoint
- Only one refresh request runs at a time (concurrency handling)
- Other requests wait for the refresh to complete

### Security Features
- Tokens are stored securely using SharedPreferences
- Expired tokens are never sent in requests
- Failed refresh attempts force logout (prevents infinite retry loops)

## Architecture Highlights

### Dependency Injection (Dagger 2)
- `AppComponent` - Main component providing app-wide dependencies
- `AppModule` - Provides Application and AuthManager
- `NetworkModule` - Provides Retrofit, API services, TokenRefreshHandler
- `RepositoryModule` - Provides repositories
- `ViewModelModule` - Provides ViewModels

### MVVM Pattern
- **Model:** Data models and repositories
- **View:** Activities, Fragments, Adapters
- **ViewModel:** Business logic and LiveData management

### Feature-Based Structure
Each feature (auth, products) contains:
- Data layer (models, remote services, repositories)
- UI layer (activities, fragments, viewmodels, adapters)

## UI Design

- Material Design 3 components
- Light mode enforced (dark mode disabled)
- Responsive layouts with ConstraintLayout
- Card-based design for product items
- Clean, simple product detail screen

## Testing Credentials

Use these credentials to test the login:
- **Username:** `mor_2314`
- **Password:** `83r5^_`

## License

This project is open source and available for educational purposes.

## Author

**Mustafa Hasria**
- GitHub: [@MustafaHasria](https://github.com/MustafaHasria)

## Acknowledgments

- FakeStore API for providing the test API endpoints
- Material Design for UI components
- Dagger 2 for dependency injection
