@echo off
setlocal enabledelayedexpansion

set "SCRIPT_DIR=%~dp0"
set "PROJECT_DIR=%SCRIPT_DIR%"
set "SCHULFOTOS_DIR=%SCRIPT_DIR%schulfotos"

:: Collect CSV files in the schulfotos directory
set COUNT=0
for %%F in ("%SCHULFOTOS_DIR%\*.csv") do (
  set /a COUNT+=1
  set "CSV_!COUNT!=%%F"
  set "CSV_NAME_!COUNT!=%%~nxF"
)

if %COUNT%==0 (
  echo No CSV files found in %SCHULFOTOS_DIR%
  exit /b 1
)

echo Available CSV files:
for /l %%i in (1,1,%COUNT%) do (
  echo   %%i^) !CSV_NAME_%%i!
)

echo.
set /p SELECTION="Select a CSV file [1-%COUNT%]: "

if "%SELECTION%"=="" (
  echo Invalid selection.
  exit /b 1
)

set /a CHECK=%SELECTION% 2>nul
if %CHECK% LSS 1 (
  echo Invalid selection: %SELECTION%
  exit /b 1
)
if %CHECK% GTR %COUNT% (
  echo Invalid selection: %SELECTION%
  exit /b 1
)

set "SELECTED_CSV=!CSV_%SELECTION%!"
set "SELECTED_NAME=!CSV_NAME_%SELECTION%!"

echo.
echo Using: %SELECTED_NAME%
echo Output directory: %SCHULFOTOS_DIR%
echo.

cd /d "%PROJECT_DIR%"
mvn -q spring-boot:run "-Dspring-boot.run.jvmArguments=-Dapp.csv-input-path=%SELECTED_CSV% -Dapp.output-path=%SCHULFOTOS_DIR%"
