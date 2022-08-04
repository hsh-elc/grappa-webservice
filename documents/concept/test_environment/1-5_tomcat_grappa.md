# Setting up Tomcat and Grappa for a development environment

This document describes a setup of Tomcat and Grappa in a development environment setup as
described [here](1_setting_up.md).

Currently, there is a description for a setup on a Windows computer, which runs xampp.

The following description references individual settings described [here](1_setting_up.md#Individual-data).

## Tomcat

Tomcat comes with the xampp installation. See [`1-2_xampp_moolde.md#Xampp`](1-2_xampp_moolde.md#Xampp)

### Configure

Edit the configuration file `tomcat-users.xml` via the xampp control panel:

```
<role rolename="admin"/>
<role rolename="manager"/>
<role rolename="manager-gui"/>
<user username="admin" password="{tomcat.adminpassword}" roles="admin,manager,manager-gui"/>
```

Then edit the file `C:\xampp\tomcat\webapps\manager\META-INF\context.xml` and comment the following line out
using `<!-- ... -->`:

```
  <!--<Valve className="org.apache.catalina.valves.RemoteAddrValve"
         allow="127\.\d+\.\d+\.\d+|::1|0:0:0:0:0:0:0:1" />-->
```

### Use it

Start Tomcat via xampp control panel on port {tomcat.port}. We currently user tomcat version 8.5.66.

Check in a web browser the address `http://{localip}:{tomcat.port}`

Navigate to `http://{localip}:{tomcat.port}/manager/html`.

### Optional but useful: Eclipse Webtools plugin

Start Eclipse and choose a workspace.

Install the Eclipse WTP Plugin from `https://marketplace.eclipse.org/content/eclipse-java-ee-developer-tools-0`.

Select Window > Preferences > Server > Runtime Environments > Add

* Apache Tomcat 8.5
* Tomcat installation directory: {xampp.home}\tomcat
* JRE: choose a jdk 10

Select File > New > Others > Server

Then

* Select Apache > Tomcat 8.5
* Server's hostname: {localip}

Go to Window > Show View > Other > Server > Servers

* Double click on the new server
* Choose Server Locations: Use Tomcat installation (takes control of Tomcat installation)
* Deploy path: {xampp.home}\tomcat\webapps
* Timeouts:
    - Start: 120
    - Stop: 30

In the Servers view you can start and stop the server from eclipse. The console log will show the Tomcat log.

## Grappa

### Fetch source

Clone the Grappa github repository. Currently we use the develop branch.

On a Windows cmd prompt type:

```
git clone https://github.com/hsh-elc/grappa-webservice.git {workspace.path}/github_hsh-elc_grappa-webservice
```

Then check, if you are on the right git branch / checkout the desired branch.

### Optional but useful: M2Eclipse Plugin

Install the M2Eclipse Plugin via market
place `https://marketplace.eclipse.org/content/eclipse-m2e-maven-support-eclipse-ide`.

Import the Grappa project by selecting menu File > Import > Existing Maven Projects. Then navigate to folger
{workspace.path}\github_hsh-elc_grappa-webservice.

First only import the `pom.xml` of the parent project (see also `https://stackoverflow.com/a/60205161`). Then for each
subfolder do a right click > Import > Existing Maven Project.

### Build

We build Grappa on the command line using Maven.

In a WSL-Terminal we build Grappa
following `https://github.com/hsh-elc/grappa-webservice/blob/develop/documents/concept/documentation.md#23-building-and-deployment`
.

Change to the mounted directory corresponding to {workspace.path} (note the backticks!):

```
cd `wslpath "{workspace.path}"`
```

Then:

```
sudo apt install maven

cd github_hsh-elc_grappa-webservice/grappa-backendplugin-dockerproxy/src/main/resources/docker/
sudo build-images.sh

cd `wslpath "{workspace.path}"`/github_hsh-elc_grappa-webservice
sudo mvn package -DskipTests
```

### Deploy / install

In a WSL terminal:

```
cd `wslpath "{workspace.path}"`/github_hsh-elc_grappa-webservice
cp grappa-webservice/target/grappa-webservice-2.0.0.war `wslpath "{xampp.home}"`/tomcat/webapps/grappa-webservice-2.war
```

Check in Tomcat Manager if the Grappa webapp has been reconized by Tomcat: `http://{localip}:{tomcat.port}/manager/html`
.

### Configure

In a WSL terminal type:

```
mkdir -p /mnt/c/etc/grappa/
cd `wslpath "{workspace.path}"`/github_hsh-elc_grappa-webservice
cp grappa-webservice/src/main/resources/grappa-config.yaml.example  /mnt/c/etc/grappa/grappa-config.yaml
```

Now edit the file `C:\etc\grappa\grappa-config.yaml` below the `cache:` section:

```
cache:
  submission_ttl_seconds: 86400 # 1 day
  task_ttl_seconds: 2592000 # 30 days
  response_ttl_seconds: 2592000 # 30 days
  redis:
    host: "127.0.0.1"
    port: 6379
    password: "{redis.password}"
```

Restart Tomcat. In the log output you should see a successful connection to the redis server like this:

```
INFO  d.h.g.a.GrappaServlet - Redis connection established
```

In addition, as expected, there is an error that the DummyGrader could not be loaded because we have not yet configured
the grader properly. We'll do that later.

### Test

Try a connection to the Grappa webapp:

- In a WSL shell type: `cat /etc/resolv.conf` . You should see something like: `nameserver 192.168.160.1`
- With that IP in mind type
  ```
  curl -v --user test:test http://{ip-from-previous-step}:{tomcat.port}/grappa-webservice-2/rest
  ```

