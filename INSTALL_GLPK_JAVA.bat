@echo off
@setlocal enableextensions

if not "%1"=="am_admin" (powershell start -verb runas '%0' am_admin & exit /b)

@cd /d "%~dp0"

reg Query "HKLM\Hardware\Description\System\CentralProcessor\0" | find /i "x86" > NUL && set OS=32 || set OS=64

if "%OS%"=="32" (
	copy /Y w32\\*.dll C:\Windows\System32
) else (
    copy /Y w32\\*.dll C:\Windows\SysWOW64
    copy /Y w64\\*.dll C:\Windows\System32
)

pause