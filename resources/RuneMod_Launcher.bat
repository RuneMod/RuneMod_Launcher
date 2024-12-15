@echo off
chdir /d %temp%

set /p localVersion=<%temp%\localVersion.txt

echo echo localVersion: %localVersion%

echo gettings latest versionNumber...
curl -A "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64)" -L "https://pub-64c85893ea904aedab24caeb10432ae1.r2.dev/launcher/version.txt" -o latestVersion.txt

if errorlevel 1 (
    echo Unable to reach the runemod webserver. Please notify RuneFace of the issue.
    echo Press any key to continue...
    pause >nul
    exit
)

set /p latestVersion=<%temp%\latestVersion.txt
echo echo latestVersion: %latestVersion%

If %latestVersion% == %localVersion% (echo rm plugin version is up to date!) ELSE (
echo:
echo rm plugin updating to version %latestVersion%...
curl -A "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64)" -L "https://pub-64c85893ea904aedab24caeb10432ae1.r2.dev/launcher/runemod-all.jar" -o runemod-all.jar

rem set local version now that latest version has been downloaded.

(echo %latestVersion%) > %temp%\localVersion.txt
)

java -jar -ea %temp%\runemod-all.jar --developer-mode >>log_debug.txt