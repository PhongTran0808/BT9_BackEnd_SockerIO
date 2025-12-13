@echo off
echo 🚀 Starting Socket.IO Chat Server...
echo.

REM Clean and compile
echo 📦 Compiling project...
mvn clean compile -q
if %ERRORLEVEL% neq 0 (
    echo ❌ Compilation failed!
    pause
    exit /b 1
)

echo ✅ Compilation successful!
echo.

REM Run Socket.IO Server
echo 🔌 Starting Socket.IO Server...
echo 📍 URL: http://localhost:9092
echo 📱 Android: http://10.0.2.2:9092
echo 👥 Test Users: customer1/123456, manager/123456
echo.
mvn exec:java

pause