# Setting up a development computer for MooPT and Grappa

This document describes the setup of a windows 10 computer for developing for MooPT and Grappa.

## Overall picture

See [`1-1_overall_picture.md`](1-1_overall_picture.md)

## Individual data

There are many placeholders in this document, where you should put your own usernames, paths or passwords. Here's a list
of all of the placeholders:

* mysql.moodleuser, e. g. moodleuser
* mysql.moodlepassword
* mysql.port, e. g. 3306
* xampp.home, e. g. C:\xampp
* moodle.datafolder, e. g. D:\moodledata
* moodle.adminpassword
* moodle.adminemail
* apache.port, e. g. 8081
* apache.sslport, e. g. 8443
* localip, e. g. 10.10.10.65, if you are using a loopback network device, otherwise 127.0.0.1 or localhost
* editor.path, e. g. C:\Program Files (x86)\Notepad++\notepad++.exe
* tomcat.port, e. g. 8080
* tomcat.adminpassword
* redis.password
* workspace.path, e. g. D:\ws

## Xampp and Moodle

See [`1-2_xampp_moodle.md`](1-2_xampp_moodle.md)

## MooPT

See [`1-3_moopt.md`](1-3_moopt.md)

## Windows subsystem for linux (WSL), Redis, Docker

See [`1-4_wsl_docker_redis.md`](1-4_wsl_docker_redis.md)

## Tomcat and Grappa

See [`1-5_tomcat_grappa.md`](1-5_tomcat_grappa.md)

## Grappa Backend plugins

See [`1-6_grappa_bp.md`](1-6_grappa_bp.md)

## System test

Now we are ready to test the whole system from end to end.

See [`1-7_sys_test.md`](1-7_sys_test.md)


