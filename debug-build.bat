@echo on
set "JAVA_HOME=C:\Program Files\Java\jdk-21"
echo Current directory: %CD%
echo JAVA_HOME: %JAVA_HOME%
echo PATH: %PATH%

if exist "%JAVA_HOME%\bin\java.exe" (
    echo Java found at: %JAVA_HOME%\bin\java.exe
    "%JAVA_HOME%\bin\java.exe" -version
) else (
    echo Java not found at: %JAVA_HOME%\bin\java.exe
)

echo Running Maven build...
mvnw.cmd -X clean install
pause
