@echo off
echo Encerrando o sistema na porta 8080...

for /f "tokens=5" %%p in ('netstat -aon ^| findstr :8080 ^| findstr LISTENING') do (
    echo Encerrando processo PID %%p
    taskkill /PID %%p /F
)

echo.
echo Sistema encerrado.
pause
