# leewan-fortress Linux堡垒机

# 一：提供以下功能 

1、SSH交互 

2、SFTP文件交互功能 

3、SSH录屏审计 

4、SFTP操作日志 

5、机器管理、机器用户管理 

6、人员管理，机器分配 



# 二：优点

1、安装方便，可支持SQLite、MYSQL

1、纯web界面，无需任何其他的插件

2、安全审计功能


# 三：使用：在Linux上无需配置就可使用，当然也可以对如下两项进行修改

1、数据库配置：默认使用SQLite，可在jar包中的 BOOT-INF\classes\application.properties中修改数据库相关配置（目前支持SQLite和MYSQL）

2、默认端口9010，如需修改,在bootstrap.yml文件中修改server.port即可


# 初次启动

系统会默认创建一个admin/123456的超级管理员
