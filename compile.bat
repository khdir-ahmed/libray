@echo off
cd /d "%~dp0"
javac -cp "lib\mysql-connector-j-8.1.0.jar" -d out src\main\java\com\java\libray\*.java
echo Done.
pause
