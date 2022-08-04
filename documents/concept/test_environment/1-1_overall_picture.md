# Software stack for a development environment

This document gives an overview of the software stack needed to run Moodle, Grappa and Graders in a development
environment setup as described [here](1_setting_up.md).

Currently this description assumes a windows computer running xampp and WSL.

## Overall picture

### Software stack

You need to install a web server (Apache), a database (MySQL), and PHP as well as Tomcat, Redis and Docker. You also
need WSL and (as a proposal) eclipse as your IDE. Some Grappa backend plugins need Gradle as a build tool. Grappa will
be built using Maven. As Java versions you need JDK SE 10 and (for some graders) JDK SE 8.

* Apache, MySQL, PHP and Tomcat come with the xampp distribution and will run on Windows.
* Eclipse will run on Windows.
* The Windows subsystem for linux (WSL) will be installed on top of Windows
* Redis and Docker will run in WSL
* Maven will run in WSL
* Gradle could run in WSL or Windows

On top of these software packages you have to install an offical Moodle release and the HsH Moodle plugins called MooPT.

### Call chain

The call chain of software modules when grading a submission is as follows:

* The student's computer
    - Student submits a solution on the web interface of Moodle using a web browser
* Your machine running e. g. Windows (computer W)
    - Apache receives a HTTP request and routes it to Moodle's php implementation
    - Moodle reads and writes data on the MySQL database running on your development computer's OS
    - Moodle delegates to the MooPT plugin
    - The MooPT plugin contacts the Tomcat Server, which is also running on your development computer.
    - The Tomcat server delegates to the Grappa webapp
    - The Grappa webapp contacts and reads and writes data on the Redis database, which lives in the WSL subsystem
    - The Grappa webapp delegates to the so-called grappa-backend-plugin-docker-proxy. This is a jar file installed on
      your development computer and running inside the Tomcat JVM.
    - The grappa-backend-plugin-docker-proxy connects to the docker daemon, which runs in the WSL. The
      grappa-backend-plugin-docker-proxy passes the name of the docker image to be executed.
* WSL subsystem, living inside W
    - The Grappa webapp reads and writes data on the Redis database, which lives in the WSL
    - The docker daemon receives the docker image name and starts the respective image in a docker container.
* Docker container, living inside WSL
    - A JVM is started and executes the grappa-grader-backend-starter. This is a lightweight main program.
    - The grappa-grader-backend-starter delegates to the Grappa-Backend-Plugin of the specific Grader, e. g. the
      Grappa-Backend-Plugin for Graja. This is a jar file installed inside the docker container and running in the same
      JVM as grappa-grader-backend-starter.
    - The Grappa-Backend-Plugin starts the grader. This could include further steps and connections to other servers
      like database servers.

