# Setting up Tomcat and Grappa for a development environment

This document describes a setup of Tomcat and Grappa in a development environment setup as
described [here](1_setting_up.md).

Currently, there is a description for a setup on a Windows computer, which runs xampp.

The following description references individual settings described [here](1_setting_up.md#Individual-data).

## Tomcat

In WSL type `sudo apt install tomcat9 tomcat9-admin` to install tomcat.

### Configure

Oddly enough, the directories `conf` and `webapps` are missing in `/usr/share/tomcat9`. Fix this with: 
- `sudo ln -s /etc/tomcat9/ /usr/share/tomcat9/conf` 
- `sudo ln -s /var/lib/tomcat9/webapps/ /usr/share/tomcat9/webapps`

Edit the configuration file `/etc/tomcat9/tomcat-users.xml`:

```
<role rolename="admin"/>
<role rolename="manager"/>
<role rolename="manager-gui"/>
<user username="admin" password="{tomcat.adminpassword}" roles="admin,manager,manager-gui"/>
```

### Use it

Start Tomcat: `sudo /usr/share/tomcat9/bin/catalina.sh run`

Check in a web browser the address `http://{wslip}:{tomcat.port}` while Tomcat is running.

You can find out `{wslip}` with: `ip addr show eth0 | grep "inet\b" | awk '{print $2}' | cut -d/ -f1`.

Navigate to `http://{wslip}:{tomcat.port}/manager/html`.

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

cd `wslpath "{workspace.path}"`/github_hsh-elc_grappa-webservice
sudo mvn package -DskipTests
```

### Deploy / install

In a WSL terminal:

```
cd `wslpath "{workspace.path}"`/github_hsh-elc_grappa-webservice
sudo cp grappa-webservice/target/grappa-webservice-2.5.0.war `/var/lib/tomcat9/webapps/grappa-webservice-2.war
```

Check in Tomcat Manager if the Grappa webapp has been reconized by Tomcat: `http://{wslip}:{tomcat.port}/manager/html`
.

### Configure

In a WSL terminal type:

```
sudo mkdir /etc/grappa/
cd `wslpath "{workspace.path}"`/github_hsh-elc_grappa-webservice
cp grappa-webservice/src/main/resources/grappa-config.yaml.example  /etc/grappa/grappa-config.yaml
```

Now edit the file `/etc/grappa/grappa-config.yaml` below the `cache:` section:

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

### MooPT
Now that Tomcat and Grappa are installed, MooPT must be configured accordingly. On the Moodle web interface, go to: Site Administration -> Plugins -> Question Types -> MooPT and enter the following value for the service URL: `http://{wslip}:{tomcat.port}/grappa-webservice-2/rest`


### Test

Try a connection to the Grappa webapp:

- In a WSL shell type: 
  ```
  curl -v --user test:test http://127.0.0.1:{tomcat.port}/grappa-webservice-2/rest
  ```

