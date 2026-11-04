@echo off
REM Compila e abre o Sistema de Biblioteca.
REM Procura o Java no PATH, no JAVA_HOME e nas IDEs instaladas.
cd /d "%~dp0"
setlocal

set "JAVAC_EXE="
set "JAVA_EXE="

where javac >nul 2>nul
if not errorlevel 1 (
    set "JAVAC_EXE=javac"
    set "JAVA_EXE=java"
    goto compilar
)

if defined JAVA_HOME if exist "%JAVA_HOME%\bin\javac.exe" (
    set "JAVAC_EXE=%JAVA_HOME%\bin\javac.exe"
    set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
    goto compilar
)

for %%P in (
    "%LOCALAPPDATA%\Programs\CLion\jbr"
    "%LOCALAPPDATA%\Programs\IntelliJ IDEA Ultimate\jbr"
    "%LOCALAPPDATA%\Programs\IntelliJ IDEA Community Edition\jbr"
    "%ProgramFiles%\Java\jdk-21"
    "%ProgramFiles%\Java\jdk-17"
) do (
    if exist "%%~P\bin\javac.exe" (
        set "JAVAC_EXE=%%~P\bin\javac.exe"
        set "JAVA_EXE=%%~P\bin\java.exe"
        goto compilar
    )
)

echo.
echo Nao encontrei o Java neste computador.
echo Abra a pasta src em uma IDE e execute a classe br.ufu.biblioteca.Main,
echo ou instale o JDK 17 ou superior.
echo.
pause
exit /b 1

:compilar
if not exist build mkdir build

REM Compila a partir da classe principal: o -sourcepath faz o javac buscar e
REM compilar sozinho todas as classes usadas pelo sistema.
"%JAVAC_EXE%" -encoding UTF-8 -sourcepath src -d build src\br\ufu\biblioteca\Main.java
if errorlevel 1 (
    echo.
    echo A compilacao falhou.
    pause
    exit /b 1
)

"%JAVA_EXE%" -cp build br.ufu.biblioteca.Main
endlocal
