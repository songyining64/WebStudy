@echo off
echo 正在修复系统公告时间...

REM 配置MySQL连接信息（请修改为您的实际配置）
set MYSQL_USER=root
set MYSQL_PASSWORD=123456
set MYSQL_DATABASE=mental_health
set MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 8.0\bin

REM 检查MySQL路径是否存在
if not exist "%MYSQL_PATH%\mysql.exe" (
    echo MySQL路径不正确，请修改MYSQL_PATH变量
    echo 您当前的MySQL路径设置为: %MYSQL_PATH%
    echo 请手动执行SQL脚本: src\main\resources\db\fix_system_notice_time.sql
    pause
    exit /b 1
)

REM 执行SQL脚本修复时间
echo 执行SQL修复脚本...
"%MYSQL_PATH%\mysql" -u%MYSQL_USER% -p%MYSQL_PASSWORD% %MYSQL_DATABASE% < src\main\resources\db\fix_system_notice_time.sql

if %ERRORLEVEL% NEQ 0 (
    echo SQL脚本执行失败，请检查MySQL连接信息和权限
    echo 您可以尝试手动执行SQL脚本: src\main\resources\db\fix_system_notice_time.sql
    pause
    exit /b 1
)

echo SQL脚本执行成功！
echo 重启应用后，系统公告时间显示将正确显示。

pause

REM 如果需要，重新编译项目
echo 重新编译项目...
mvn clean package -DskipTests

REM 重启应用
echo 重启应用...
java -jar target/mental-health-system-1.0.0.jar

echo 修复完成! 