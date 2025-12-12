@echo off
echo 🚀 Starting Socket.IO Customer Support Server...
echo.

REM Compile project
echo 📦 Compiling project...
mvn compile -q
if %ERRORLEVEL% neq 0 (
    echo ❌ Compilation failed!
    pause
    exit /b 1
)

echo ✅ Compilation successful!
echo.

REM Run Socket.IO Server
echo 🔌 Starting Socket.IO Server on ws://localhost:9092...
echo 📱 Android app should connect to: ws://10.0.2.2:9092
echo.
java -cp target/classes com.example.support.SocketIOServer

pause