@echo off
setlocal

if not defined JAVA_HOME (
    where java >nul 2>&1
    if errorlevel 1 (
        echo ERRO: Java 21 ou superior nao foi encontrado.
        echo Instale o Java ou defina a variavel JAVA_HOME.
        pause
        exit /b 1
    )
) else (
    if not exist "%JAVA_HOME%\bin\java.exe" (
        echo ERRO: JAVA_HOME aponta para um diretorio invalido:
        echo %JAVA_HOME%
        pause
        exit /b 1
    )
)

if not defined GOOGLE_SHEETS_SPREADSHEET_ID (
    echo ERRO: A variavel GOOGLE_SHEETS_SPREADSHEET_ID nao foi definida.
    echo Defina-a antes de iniciar o sistema.
    pause
    exit /b 1
)

set "PATH=%JAVA_HOME%\bin;%PATH%"

echo Iniciando o sistema, aguarde...
echo (na primeira vez pode demorar alguns minutos baixando dependencias)
echo.

cd /d "%~dp0"
call mvnw.cmd spring-boot:run

set "EXIT_CODE=%ERRORLEVEL%"
pause
exit /b %EXIT_CODE%
