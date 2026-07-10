@echo off
echo =========================================
echo  Menjalankan C+ E-Commerce (Spring Boot)
echo =========================================
echo.
echo Pastikan server MySQL berjalan di localhost:3306 (user: root, tanpa password).
echo.
echo Sedang melakukan kompilasi dan menyalakan server...
echo Harap tunggu sekitar 15-20 detik hingga Chrome terbuka secara otomatis.
echo.

:: Menjalankan spring boot di jendela terminal baru (agar mudah dilihat log-nya)
start "C+ Server" cmd /k ".\mvnw.cmd clean spring-boot:run"

:: Menunggu 20 detik agar Tomcat Server siap sebelum membuka Chrome
timeout /t 20 /nobreak > nul

:: Membuka Google Chrome ke localhost
echo Membuka Google Chrome...
start chrome "http://localhost:8080"
