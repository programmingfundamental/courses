@echo off
setlocal
pushd "%~dp0" || exit /b 1
where java >nul 2>nul || (echo Java 17 or newer is required. & popd & exit /b 1)
if exist "app\bank-demo.jar" goto bundled
echo Building the source project with Maven...
call mvnw.cmd -q package
if errorlevel 1 (popd & exit /b 1)
set "BANK_JAR=target\bank-demo.jar"
goto run
:bundled
set "BANK_JAR=app\bank-demo.jar"
:run
echo Bank Lab: http://127.0.0.1:8087/login  -  admin / admin
echo Stop with Ctrl+C. Restart to reset the demo data.
java -jar "%BANK_JAR%" --server.address=127.0.0.1 --server.port=8087
set "BANK_RESULT=%ERRORLEVEL%"
popd
exit /b %BANK_RESULT%
