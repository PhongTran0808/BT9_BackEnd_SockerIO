@echo off
echo 🚀 Starting Customer Support HTTP Server...
echo.
echo Compiling project...
mvn compile -q
echo.
echo Starting server on http://localhost:8080
echo Press Ctrl+C to stop server
echo.
java -cp target/classes com.example.support.SimpleServer