@echo off
echo ========================================
echo Getting SHA-1 Certificate Fingerprint
echo ========================================
echo.

gradlew.bat signingReport

echo.
echo ========================================
echo Look for the SHA1 line above and copy it
echo Then add it to Firebase Console
echo See QUICK_START_OAUTH.md for details
echo ========================================
pause
