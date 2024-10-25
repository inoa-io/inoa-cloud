@echo off
setlocal enabledelayedexpansion

set MCNOISE_REPLICAS=0
set INOA_REPLICAS=1
set INOA_IP=

@REM we will not do anything until we had a proper argument
set RUN_HELP=0
set RUN_NOINOA=0
set RUN_INSTALL=0
set RUN_K3S=0
set RUN_UI=0
set RUN_TOKENGET=0

@REM welcome message
echo.
echo Welcome to the Inoa Startup process. I hope you enjoy your time watching these fancy console outputs fly by.
echo.

@REM no arguments no problem
if "%1"=="" (
    echo You gotta give me some arguments to work with. Try -h for a helpful list.
    goto :TheEnd
)

@REM find ip by parsing ipconfig
echo First I will determine your ip adress.

for /f "tokens=2 delims=:" %%a in ('ipconfig ^| findstr /c:"IPv4"') do (
    set INOA_IP=%%a
    set INOA_IP=!INOA_IP:~1!
    goto :IpLoopEnd
)
:IpLoopEnd

@REM no ip found, straight to jail
if not defined INOA_IP (
    echo Error: Your IP adress could not be determined.
    goto :TheEnd
)

@REM ip found, all good
echo The IP adress I found is: !INOA_IP!
echo.

@REM loop through all arguments and set variables that are used later to execute (or not) certain things
:ProcessArgs
    if "%1"=="-h" set RUN_HELP=1
    if "%1"=="-n" set RUN_NOINOA=1
    if "%1"=="-c" set RUN_INSTALL=1
    if "%1"=="-l" set RUN_K3S=1
    if "%1"=="-u" set RUN_UI=1
    if "%1"=="-t" set RUN_TOKENGET=1
    if "%1"=="" goto :ProcessArgsLoopEnd
    shift
    goto :ProcessArgs

:ProcessArgsLoopEnd

@REM show help
if %RUN_HELP%==1 (
    echo Syntax: inoa-startup.bat [ -t ^| -h ^| -n ^| -c ^| -l ^| -u ]
    echo.
    echo Options:
    echo -t     Get KeyCloak Access Token
    echo -h     Print this help message.
    echo -n     Set INOA Cloud replicas to 0 ^(INOA backend will not be started^)
    echo -c     Do a clean install before start.
    echo -l     Launch k3s Server.
    echo -u     Start the Ground Control UI locally.
    echo.
)

@REM set inoa replicas to 0
if %RUN_NOINOA%==1 (
    echo Setting Inoa Replicas to 0...
    echo.
    set INOA_REPLICAS=0
)

@REM running clean build
if %RUN_INSTALL%==1 (
    echo Doing a clean install of Inoa...
    echo.
    call mvn clean install -DskipTests
)

@REM startig all the k3s stuff
if %RUN_K3S%==1 (
    echo.
    echo Starting Inoa...

    call mvn pre-integration-test -Dk3s.failIfExists=false -Dinoa.domain=%INOA_IP%.nip.io -Dmcnoise.replicas=%MCNOISE_REPLICAS% -pl ./test/
    echo Opening Help-Page...
    echo.
    start http://help.%INOA_IP%.nip.io:8080
)

@REM start ui
if %RUN_UI%==1 (
    echo.
    echo Starting UI...
    echo.
    cd app || exit /b
    call yarn start
)

@REM get keycloak access token
if %RUN_TOKENGET%==1 (
    for /f "tokens=*" %%a in ('curl.exe http://keycloak.%INOA_IP%.nip.io:8080/realms/inoa/protocol/openid-connect/token --silent --data "client_id=inoa-swagger" --data "username=admin" --data "password=password" --data "grant_type=password" ^| powershell -command "$input | ConvertFrom-Json | Select-Object -ExpandProperty access_token"') do (
        set "access_token=%%a"
    )

    echo Access Token: !access_token!
)

@REM This is the end
:TheEnd

echo.
echo I hope you enjoyed this exciting experience. Let's do it again some time.
