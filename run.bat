@echo off
set JAVAFX_DIR=C:\Users\DESHNA\.m2\repository\org\openjfx
set JAVAFX_VER=21.0.3

REM Compile the project
if not exist out_run mkdir out_run
dir /s /b src\*.java > sources.txt
javac --module-path "%JAVAFX_DIR%\javafx-controls\%JAVAFX_VER%\javafx-controls-%JAVAFX_VER%-win.jar;%JAVAFX_DIR%\javafx-graphics\%JAVAFX_VER%\javafx-graphics-%JAVAFX_VER%-win.jar;%JAVAFX_DIR%\javafx-base\%JAVAFX_VER%\javafx-base-%JAVAFX_VER%-win.jar" --add-modules javafx.controls -d out_run @sources.txt
del sources.txt

if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed.
    pause
    exit /b %ERRORLEVEL%
)

echo Compilation successful. Running SolveStack...
java --module-path "%JAVAFX_DIR%\javafx-controls\%JAVAFX_VER%\javafx-controls-%JAVAFX_VER%-win.jar;%JAVAFX_DIR%\javafx-graphics\%JAVAFX_VER%\javafx-graphics-%JAVAFX_VER%-win.jar;%JAVAFX_DIR%\javafx-base\%JAVAFX_VER%\javafx-base-%JAVAFX_VER%-win.jar" --add-modules javafx.controls -cp "out_run;src" main.SolveStackApp
pause
