配置 Maven Daemon 路径MNDHMED:\program\maven-mvnd-1..3-windows-amd64\maven-mvnd-1.0.3-windows-amd64\maven-mvnd-1.0.3-windows-amd64DCMD%MVHME%\bin\mvnd.cmd
REM检查 aven 是否可用MVN_VAIABEif xis%MNDCMD% (    VAILABLE1    MVCMD%MVND_CMD%)el(    whrevn >nul 2>&1    if !rrorlevel!equ 0 (        MVN_AVAILA1        MVN_CMDvn    ))

eu   后端端口: 8081 前端端口 8080访问地址:http://localhost:8080
echo  ----------------------------------------
echo   测试账号:
echo     学生: student / 23456
echo     教师: teacher / 123456
echo  ========================================
echo
echo [1] 全自动部署 (推荐首次使用)
echo  [2] 仅[3]仅[4]仅[5]  [6] 停止服务0退出t /p choice=请选择 [0-6]: if"%hoice%"=="1" goto fu_deploy
if"%oic%"=="2" goto hec
f "%chic%"=="3" gooconfig_mirrorchoice4gotoinst_deps
if"%choic%"=="5" goto r_ervicesif"%choice%"=="6"gotostop_servies
if"%oi%"=="0" gto ed
goto u
REM ========================================REM 全自动部署========================================:ull_deploy
cls
echo.
echo ======================================echo      全自动部署
  ========================================.

call:do_check_env
if"NV_OK"=="0"( [部署终止]先缺失的后重试mnudo_do_s
if "%DEPS_OK%"=="0" (
    cho.
    ho  [部署终止] 依赖安装失败
    cho.
    paue
    goto menu)do_stop_servces
cal :o_startsevices

echo.
echo  ========================================
ech            部署完成！
ho  ========================================echo.echo 服务正在中，请等待约 30-60 秒...
echo.echo  访问地址: http://lohost:8080
echo.
echo 后端就绪标志 Sted IniewSystemApplation
cho  前端就绪标志: App running at: http://localhot:8080
echo.pausemnu检查==============================
echo         
echo ==============================call :do_check_envcho.pau
goomenu
:do_chckenvEV1]%%  +OK0检查...%AVAILABLE%=( %CD% +set "ENV_OK=0"]%%  +    set "ENV_OK=0"
]%%      set "ENV_OK=0"
 backend/pom.xml  backend/pom.xml
    set "ENV_OK=0" frontend/package.json  frontend/package.json    set "ENV_OK=0")V结果] 结果] 失败，请安装缺失软件配置镜像源recho         echo 
echo.call do_paus
gotmenu
:do_onfig_mirrr] ]阿里云 配置  提示l 不存在建议参考 DEPLY.d配置镜像==============================
echo         
echo ==============================call:do_install_depso.
pause
gotmeu

:do__depsst"DP_OK=1"
  [安装] 前端依赖.. not正...这可能需要几分钟，请耐心等待...cho.                          tDESOK=0) K前端已存在启动服务stat_srvies====================         启动服务========================================cho.
:dohc_nv"%ENV_OK%"=="".错误环境检查未通过无法服务.
pause
gotomenu)
all:ostop_srviesall:d_sar_sevics
ch.echo echo   服务已！
echo  ----------------------------------------
echo    请等待约 30-60 秒启动完成echo.echo    后端就绪标志
echo      Sd IntewSytemApplicationeho.    前端就绪标志:    App running at: http://localhost:8080
echo  ====================pugoom
:d_sat_srvies启动2启动ntend"

if o xist "ode_moules (
    echo         首次运行，正在安装依赖...
    call npm install
)

goto :eof

REM ========================================
REM 停止服务
REM ========================================
:stop_servicescls停止服务
echo.
call :do_stop_services已停止paus
gotmenu

do_so_servie  [停止] 清理端口占用..
taskkill /F /IM node.ex >nul2>&1
iferrorlevel equ 0 (          OK已终止No 进程 (前端)
)else(           -无Node进程运行
)

askkill /F /IM java.x>nul2>&if %rrrlevl%equ0(            OK已终止Java进程()
)l (       -无Jav进程运行
)

imeou t 2 nbrek >nu
go eof
REM========================================
REM退出
REM========================================
endcho  感谢使用！ech.
exi /b0