# Setting up Tomcat and Grappa in your development environment

This document describes the setup of Tomcat and Grappa inside a development environment on a Debian machine.

Unfortunately, Grappa only runs on Tomcat version 9.x, which isn't available anymore in the default Debian Trixie repositories. Therefore, it needs to be installed manually.

Head over [here](https://tomcat.apache.org/download-90.cgi) and download the latest .tar.gz for Tomcat 9

Then unpack the downloaded file:
```bash
sudo mkdir /opt/tomcat9
sudo tar -xzf apache-tomcat-9.0.117.tar.gz -C /opt/tomcat9 --strip-components=1
```

Then create a new tomcat user on your system and give it permission on the tomcat9 installation directory:

```bash
sudo useradd -r -d /opt/tomcat9 -s /bin/false tomcat
sudo chown -R tomcat:tomcat /opt/tomcat9
sudo chmod -R u+x /opt/tomcat9/bin
```

Then create a symbolic link for storing tomcat's configs:

```bash
sudo ln -s /opt/tomcat9/conf /etc/tomcat9
```

And edit `/etc/tomcat9/tomcat-users.xml` and put the following contents into `<tomcat-users>`:

```xml
<role rolename="admin"/>
<role rolename="manager"/>
<role rolename="manager-gui"/>
<user username="admin" password="password" roles="admin,manager,manager-gui"/>
```

Then create a new service file for tomcat9:

```bash
sudo nano /etc/systemd/system/tomcat9.service
```

And put the following contents:
```
[Unit]
Description=Apache Tomcat 9 Web Application Server
After=network.target

[Service]
Type=simple

User=tomcat
Group=tomcat

Environment="JAVA_HOME=/usr/lib/jvm/jdk-17.0.12-oracle-x64"
Environment="CATALINA_HOME=/opt/tomcat9"
Environment="CATALINA_BASE=/opt/tomcat9"
Environment="CATALINA_OPTS=-Xms512M -Xmx1024M -server -XX:+UseParallelGC"
Environment="JAVA_OPTS=-Djava.awt.headless=true -Djava.security.egd=file:/dev/./urandom"

ExecStart=/opt/tomcat9/bin/catalina.sh run
ExecStop=/opt/tomcat9/bin/catalina.sh stop

Restart=always

# Optional: Logs direkt ins journal
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

Note that the `JAVA_HOME` environment variable should be set to the installation path of your Java installation. If you installed the same version as described [here](1_moodle.md), then you can leave everything as it is.

Then reload the systemd services and start tomcat9:

```bash
sudo systemctl daemon-reload
sudo systemctl start tomcat9
```

You can also have tomcat9 be automatically started on system boot:

```bash
sudo systemctl enable tomcat9
```

After that, check if tomcat is running by opening [http://localhost:8080/manager/html](http://localhost:8080/manager/html). You can login using the username and password you've set in the tomcat config file.

Next, we're going to install Grappa itself. Switch to a directory inside your home directory and clone the following sources:

```bash
git clone https://github.com/hsh-elc/grappa-webservice.git
git clone https://github.com/hsh-elc/grappa-backendplugin.git
git clone https://github.com/hsh-elc/proforma.git
```

Then run the following commands. This will compile all the needed dependencies in the right order, so that the Grappa can be finally compiled.

```bash
cd proforma
mvn clean install -DskipTests
cd ../grappa-backendplugin
mvn clean install -DskipTests
cd ../grappa-webservice
mvn clean package -DskipTests
```

After everything is compiled, you will find the `grappa-webservice-<version>.war` file inside `./grappa-webservice/grappa-webservice/target/`. This file contains the whole web service. You can now copy this into tomcat9 and restart it.

```bash
sudo systemctl stop tomcat9
sudo rm -rf /opt/tomcat9/webapps/grappa-webservice-2.war /opt/tomcat9/webapps/grappa-webservice-2/
sudo cp ~/grappa-webservice/grappa-webservice/target/grappa-webservice-2.8.0.war /opt/tomcat9/webapps/grappa-webservice-2.war
sudo chmod +x /opt/tomcat9/webapps/grappa-webservice-2.war
sudo systemctl start tomcat9
```

You will also need to create a config directory for Grappa:

```bash
sudo mkdir /etc/grappa
sudo cp ~/grappa-webservice/src/main/resources/grappa-config.yaml.example /etc/grappa/grappa-config.yaml
```

Then edit some stuff in this config:

```
cache:
  redis:
    host: "127.0.0.1"
    port: 6379
    password: "<your Redis password>"
```
And then restart Grappa:

```bash
sudo systemctl restart tomcat9
```

You can now test if Grappa is running:

```bash
curl --user test:test http://localhost:8080/grappa-webservice-2/rest
```
