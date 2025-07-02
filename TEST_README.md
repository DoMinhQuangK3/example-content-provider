# ContentProviders Test Suite

This document describes the comprehensive test suite for the ContentProviders Android application.

## Test Overview

Our test suite includes:
- **Unit Tests**: Testing individual components in isolation
- **Integration Tests**: Testing component interactions
- **Instrumentation Tests**: Testing on actual Android devices/emulators
- **Performance Tests**: Measuring app performance characteristics

## Test Structure

### Unit Tests (`src/test/`)
Located in the `test` directory, these tests run on the JVM and don't require an Android device:

- **VillainsTest**: Tests the Villains data class
- **VillainsRepositoryTest**: Tests the repository layer
- **VillainsViewModelTest**: Tests the ViewModel logic
- **VillainAdapterTest**: Tests the RecyclerView adapter
- **VillainProviderTest**: Tests the ContentProvider logic

### Instrumentation Tests (`src/androidTest/`)
Located in the `androidTest` directory, these tests run on an Android device or emulator:

- **MainActivityInstrumentationTest**: UI tests for MainActivity
- **VillainProviderIntegrationTest**: Integration tests for ContentProvider
- **VillainsDatabaseTest**: Database integration tests
- **PerformanceTest**: Performance benchmarking tests

## Test Dependencies

The following testing dependencies are included:

```gradle
// Unit Testing
testImplementation "junit:junit:$junitVer"
testImplementation "org.mockito:mockito-core:4.6.1"
testImplementation "org.mockito.kotlin:mockito-kotlin:4.0.0"
testImplementation "androidx.arch.core:core-testing:2.1.0"
testImplementation "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4"
testImplementation "androidx.room:room-testing:2.4.3"
testImplementation "org.robolectric:robolectric:4.8.1"

// Instrumentation Testing
androidTestImplementation "androidx.test:runner:$androidTestRunnerVer"
androidTestImplementation "androidx.test.espresso:espresso-core:$espressoCoreVersion"
androidTestImplementation "androidx.test.ext:junit:1.1.3"
androidTestImplementation "androidx.test:rules:1.4.0"
androidTestImplementation "androidx.test.espresso:espresso-contrib:3.4.0"
androidTestImplementation "androidx.test.espresso:espresso-intents:3.4.0"
androidTestImplementation "org.mockito:mockito-android:4.6.1"
androidTestImplementation "androidx.room:room-testing:2.4.3"
```

## Running Tests

### Command Line

**Run all unit tests:**
```bash
./gradlew test
```

**Run all instrumentation tests:**
```bash
./gradlew connectedAndroidTest
```

**Run specific test class:**
```bash
./gradlew test --tests VillainsTest
```

**Run tests with coverage:**
```bash
./gradlew testDebugUnitTestCoverage
```

### Android Studio

1. **Run individual tests**: Right-click on test method/class → Run
2. **Run all tests in a package**: Right-click on test package → Run tests
3. **Run with coverage**: Right-click → Run with Coverage

## Test Categories

### 1. Data Layer Tests

**VillainsTest**: 
- Tests data class creation and validation
- Tests ContentValues conversion
- Tests constant values

**VillainsDatabaseTest**:
- Tests Room database operations
- Tests CRUD operations
- Tests database constraints and relationships

### 2. Repository Layer Tests

**VillainsRepositoryTest**:
- Tests repository methods
- Tests data flow from DAO to Repository
- Mocks DAO dependencies

### 3. ViewModel Tests

**VillainsViewModelTest**:
- Tests ViewModel initialization
- Tests LiveData updates
- Tests coroutine operations
- Mocks repository dependencies

### 4. UI Layer Tests

**VillainAdapterTest**:
- Tests RecyclerView adapter
- Tests DiffUtil callbacks
- Tests item click handling

**MainActivityInstrumentationTest**:
- Tests UI interactions
- Tests activity lifecycle
- Tests RecyclerView display

### 5. ContentProvider Tests

**VillainProviderTest** (Unit):
- Tests ContentProvider CRUD operations with mocks
- Tests URI matching
- Tests MIME type handling

**VillainProviderIntegrationTest** (Integration):
- Tests ContentProvider with real database
- Tests ContentResolver operations
- Tests data persistence

### 6. Performance Tests

**PerformanceTest**:
- Tests bulk operations performance
- Tests memory usage
- Tests concurrent operations
- Sets performance benchmarks

## Test Utilities

**TestUtils**:
- Provides test data generation
- Contains helper methods for testing
- Provides validation utilities

## Test Suites

**UnitTestSuite**: Runs all unit tests together
**InstrumentationTestSuite**: Runs all instrumentation tests together

## Coverage Goals

Our test suite aims for:
- **Line Coverage**: > 80%
- **Branch Coverage**: > 70%
- **Method Coverage**: > 85%

## Continuous Integration

Tests are designed to run in CI/CD environments:
- All unit tests should pass in under 60 seconds
- Instrumentation tests should pass in under 5 minutes
- Performance tests establish baseline metrics

## Best Practices

1. **Isolation**: Each test is independent and can run in any order
2. **Mocking**: External dependencies are mocked in unit tests
3. **Assertions**: Clear, descriptive assertions with helpful messages
4. **Setup/Teardown**: Proper resource management in test lifecycle
5. **Naming**: Descriptive test method names using backticks
6. **Performance**: Performance tests include reasonable thresholds

## Troubleshooting

### Common Issues

1. **Robolectric Issues**: Ensure proper SDK version configuration
2. **Mockito Issues**: Use MockitoAnnotations.openMocks() in @Before
3. **Coroutine Issues**: Use runTest and proper coroutine test dispatchers
4. **Database Issues**: Use in-memory database for tests
5. **UI Tests**: Add appropriate waits for async operations

### Debug Tests

Enable test debugging:
```gradle
android {
    testOptions {
        unitTests {
            includeAndroidResources = true
        }
    }
}
```

## Metrics and Reporting

Test results and coverage reports are generated in:
- `app/build/reports/tests/`
- `app/build/reports/coverage/`

## Future Enhancements

Planned test improvements:
- Add integration tests for ContentProvider with external apps
- Add UI automation tests using UiAutomator
- Add stress tests for large datasets
- Add accessibility tests
- Add security tests for ContentProvider permissions
