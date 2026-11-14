@echo off
cd /d "%~dp0"
java -cp "out;lib\mysql-connector-j-8.1.0.jar" com.java.libray.Launcher
pause
