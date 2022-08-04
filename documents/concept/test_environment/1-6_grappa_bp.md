# Setting up Grappa Backend plugins for a development environment

This document describes a setup of Grappa Backend plugins in a development environment setup as
described [here](1_setting_up.md).

Currently, there is a description for a setup on a Windows computer, which runs Tomcat inside xampp and which runs
docker on the WSL.

The following description references individual settings described [here](1_setting_up.md#Individual-data).

## Grappa Backend plugins

There is a so-called dummy grader plugin, which is part of the grappa-webservice project, and a Grappa Backend Plugin
for Graja. Both plugins will not run inside the Tomcat JVM, but will be invoked by grappa-backendplugin-dockerproxy,
which *is* running in the Tomcat JVM. So we have to deal with three Grappa Backend Plugins:

* grappa-backendplugin-dummygrader
* grappa-backendplugin-graja
* grappa-backendplugin-dockerproxy while the last plugin is only a wrapper around the first and the second one.

We'll first build the Grappa Backend Plugin for Graja (grappa-backendplugin-graja).

### Fetch sources of Grappa Backend Plugin for Graja

In a windows cmd prompt clone the git repository:

```
git clone https://github.com/hsh-elc/grappa-backendplugin-graja.git {workspace.path}/github_hsh-elc_grappa-backendplugin-graja
```

Check, if you are on the right git branch / checkout the desired branch.

### Build Grappa Backend Plugin for Graja

On the command line type:

```
bash gradlew --info build
bash gradlew --info eclipse
```

### Optional but useful: Buildship plugin for Eclipse

Install the Buildship-Plugin in Eclipse via
marketplace: `https://marketplace.eclipse.org/content/buildship-gradle-integration`

Then import the project via menu File -> Import -> Gradle -> Existing Gradle Project. In the wizard check that "Gradle
wrapper" is selected.

### Install Grappa Backend Plugin for the Dummy Grader

As said, the grappa-backendplugin-dockerproxy is a wrapper around the grappa-backendplugin-dummygrader and the
grappa-backendplugin-graja, let's denote this by:

* grappa-backendplugin-dockerproxy(grappa-backendplugin-dummygrader)
* grappa-backendplugin-dockerproxy(grappa-backendplugin-graja)

The Grappa configuration considers each of these two combinations as a separate backend, which can and must be
configured separately. The grappa-backendplugin-dockerproxy consists of a jar file (which is the same for both
combinations) and a properties file (which is different for the two combinations).

Let's first install the jar file - which has been built by the above maven run - by typing in a WSL terminal:

```
mkdir -p /mnt/c/usr_local_graders/dockerproxy-dummygrader
cd `wslpath "{workspace.path}"`/github_hsh-elc_grappa-webservice
cp grappa-backendplugin-dockerproxy/target/grappa-backendplugin-dockerproxy-0.1-jar-with-dependencies.jar /mnt/c/usr_local_graders/dockerproxy-dummygrader/grappa-backendplugin-dockerproxy.jar
```

So, the first combination's jar file is stored at `C:\usr_local_graders\dockerproxy-dummygrader`. We configure this path
in the file `C:\etc\grappa\grappa-config.yaml` as follows below the `graders:` section:

```
graders:
  - id: "DummyGrader"
    name: "DummyGrader" # user friendly name
    enabled: true  # enable or disable grader
    class_path: "/usr_local_graders/dockerproxy-dummygrader/grappa-backendplugin-dockerproxy.jar"
    file_extension: ".jar"
    class_name: "de.hsh.grappa.backendplugin.dockerproxy.DockerProxyBackendPlugin"
    config_path: "/usr_local_graders/dockerproxy-dummygrader/grappa-backendplugin-dockerproxy.properties"
    timeout_seconds: 60
    # concurrent_grading_processes sets the number of max grader instances in a grader pool
    concurrent_grading_processes: 5
```

The Docker proxy backend plugin requires a properties file that is transferred by the Grappa core to the plugin in its
init method. We have to create this file as well. The content of the file can be found here as an example
file: `{workspace.path}\github_hsh-elc_grappa-webservice\grappa-backendplugin-dockerproxy\src\main\resources/grappa-backendplugin-dockerproxy.properties.example`
.

For the dummy grader we create the following content in the properties file:

```
# docker host uri (including port)
dockerproxybackendplugin.docker_host=tcp://localhost:2375
# Name of the docker image to create a container from
dockerproxybackendplugin.container_image=grappa-backend-dummygrader
dockerproxybackendplugin.copy_submission_to_directory_path=/var/grb_starter/tmp
dockerproxybackendplugin.response_result_directory_path=/var/grb_starter/tmp
```

Store the file to `C:\usr_local_graders\dockerproxy-dummygrader\grappa-backendplugin-dockerproxy.properties`.

Comment: the name of the container image (here `grappa-backend-dummygrader`) is the one that is to be defined by
running `{workspace.path}\github_hsh-elc_grappa-webservice\grappa-backendplugin-dockerproxy\src\main\resources\docker\build-images.sh`
.

### Install Grappa Backend Plugin for Graja

We repeat these steps for the Grader Graja.

First install the jar file:

```
mkdir -p /mnt/c/usr_local_graders/dockerproxy-graja-2.2
cd `wslpath "{workspace.path}"`/github_hsh-elc_grappa-webservice
cp grappa-backendplugin-dockerproxy/target/grappa-backendplugin-dockerproxy-0.1-jar-with-dependencies.jar /mnt/c/usr_local_graders/dockerproxy-graja-2.2/grappa-backendplugin-dockerproxy.jar
```

So, the second combination's jar file is stored at `C:\usr_local_graders\dockerproxy-graja-2.2`. We configure this path
in the file `C:\etc\grappa\grappa-config.yaml` as follows below the `graders:` section:

```
graders:
  - id: "DummyGrader"
       ...
  - id: "Graja2.2"
    name: "Graja 2.2"
    enabled: true  # enable or disable grader
    class_path: "/usr_local_graders/dockerproxy-graja-2.2/grappa-backendplugin-dockerproxy.jar"
    file_extension: ".jar"
    class_name: "de.hsh.grappa.backendplugin.dockerproxy.DockerProxyBackendPlugin"
    config_path: "/usr_local_graders/dockerproxy-graja-2.2/grappa-backendplugin-dockerproxy.properties"
    timeout_seconds: 60
    # concurrent_grading_processes sets the number of max grader instances in a grader pool
    concurrent_grading_processes: 5
```

For the Graja grader we create the following content in the properties file:

```
# docker host uri (including port)
dockerproxybackendplugin.docker_host=tcp://localhost:2375
# Name of the docker image to create a container from
dockerproxybackendplugin.container_image=grappa-backend-graja-2.2
dockerproxybackendplugin.copy_submission_to_directory_path=/var/grb_starter/tmp
dockerproxybackendplugin.response_result_directory_path=/var/grb_starter/tmp
```

Store the file to `C:\usr_local_graders\dockerproxy-graja-2.2\grappa-backendplugin-dockerproxy.properties`.

### Build Docker images

Up to now we have installed two identical jar files and two different properties files below `C:\usr_local_graders`.
During runtime the two identical jar files will connect to the docker daemon and request a container with a docker image
that has been configured in the two different properties files. To make the docker container start, we have to build the
two images. This is the aim of the following steps. Since the docker images live as guests on the WSL, we need to
execute these steps in the WSL terminal.

There is a script `build-images.sh` in the
folder `{workspace.path}\github_hsh-elc_grappa-webservice\grappa-backendplugin-dockerproxy\src\main\resources\docker`.
Let's
call `{workspace.path}\github_hsh-elc_grappa-webservice\grappa-backendplugin-dockerproxy\src\main\resources\docker`
the *docker image specs folder*.

We need to execute this script in a WSL terminal. We therefore create a clone of the whole docker image specs folder and
store the clone e. g. to `/home/<you>/grappa_transfer_files/docker`.

**Note!** Temporarily, the newest version of the docker image specs folder is on the host grappadev in the
folder `/home/grappadev/grappa_transfer_files/docker`.

Below the clone of the docker image specs folder we have to prepare one subfolder per grader. The subfolder will need an
up to date version of the respective Grappa Backend Plugin.

* For the Grappa Backend Plugin of Graja we need to copy the jar file
  from `` `wslpath "{workspace.path}"`/github_hsh-elc_grappa-backendplugin-graja/build/libs/grappa-backendplugin-graja-0.2.jar``
  to `/home/<you>/grappa_transfer_files/docker/grappa-backend-graja-2.2/grajaplugin-0.2.jar`. Addionally we need to copy
  the properties file
  from `` `wslpath "{workspace.path}"`/github_hsh-elc_grappa-backendplugin-graja/src/main/resources/grajaplugin.properties.example``
  to `/home/<you>/grappa_transfer_files/docker/grappa-backend-graja-2.2/grajaplugin.properties`.
* For the Grappa Backend Plugin of the Dummy Grader we need to copy the jar file
  from `` `wslpath "{workspace.path}"`/github_hsh-elc_grappa-webservice/grappa-backendplugin-dummygrader/target/grappa-backendplugin-dummygrader-0.1-jar-with-dependencies.jar``
  to `/home/<you>/grappa_transfer_files/docker/grappa-backend-dummygrader/DummyGraderGrappaV2.jar`.
* Both docker images are based on a common base image, which includes a jar file that needs to be copied
  from `` `wslpath "{workspace.path}"`/github_hsh-elc_grappa-webservice/grappa-backendstarter/target/grappa-backendstarter-0.1-jar-with-dependencies.jar``
  to `/home/<you>/grappa_transfer_files/docker/grappa-backend-base/starter/grappa-backendstarter.jar`.

Before starting the `build-images.sh` script, you should open it and check it.

* On your development computer, the following args might not be needed and should be
  removed: `--network=host --build-arg=http_proxy="http://some.proxy.org:3128"`.

Now make sure, that the docker daemon is running, and in a WSL terminal change to the docker image specs folder and then
type:

```
sudo bash build-images.sh
```

Check, if the docker images have been built successfully:

```
sudo docker image ls
```

You should see a list like this:

```
REPOSITORY                   TAG       IMAGE ID       CREATED         SIZE
grappa-backend-graja-2.2     latest    78fdba5d4a93   4 minutes ago   1.11GB
grappa-backend-dummygrader   latest    59d6650b8b95   4 minutes ago   779MB
grappa-backend-base          latest    29cd788bfd61   4 minutes ago   777MB
ubuntu                       latest    9873176a8ff5   4 days ago      72.7MB
hello-world                  latest    d1165f221234   3 months ago    13.3kB
```

When there is one of the images missing, check the output of the `build-images.sh` script.

Some tipps to workaround errors:

* in WSL terminal: `sudo apt-get update`. Then run the script again.
* in WSL terminal: remove the ubuntu image and rebuild it, i. e. `sudo docker rmi ubuntu`. Then run the script again.
  (
  source: `https://stackoverflow.com/questions/37706635/in-docker-apt-get-install-fails-with-failed-to-fetch-http-archive-ubuntu-com`)
* add the option `--no-cache` to all docker build lines in `build-images.sh`. Then run the script again. After that,
  remove the `--no-cache` option from the script.
  (source: https://stackoverflow.com/a/37727984)
* In case of "Temporary failure resolving ‘archive.ubuntu.com’" have a look at the "Troubleshooting DNS resolving"
  section in [Setting up Redis and Docker for a development environment](1-4_wsl_docker_redis.md).

You should check, whether the names of the docker images (column *REPOSITORY* above) match the names in the two
properties files that we have configured below `C:\usr_local_graders`.

### Test

Restart Tomcat and watch the output. There should be lines like these:

```
2021-12-15 00:06:58,778 INFO  d.h.g.s.GraderPool - Loading grader plugin 'DummyGrader' with classpathes '/usr_local_graders/dockerproxy-dummygrader/grappa-backendplugin-dockerproxy.jar'...
2021-12-15 00:06:58,780 DEBUG d.h.g.u.ClassPathClassLoader - Current classpathes: [/usr_local_graders/dockerproxy-dummygrader/grappa-backendplugin-dockerproxy.jar]
2021-12-15 00:06:58,780 DEBUG d.h.g.u.ClassPathClassLoader - Current extensions: [.jar]
2021-12-15 00:06:58,781 INFO  d.h.g.s.GraderPool - Loading grader config file '/usr_local_graders/dockerproxy-dummygrader/grappa-backendplugin-dockerproxy.properties'...
2021-12-15 00:06:58,782 INFO  d.h.g.s.GraderPool - Using grader 'DummyGrader' with 5 concurrent instances.
2021-12-15 00:06:58,784 INFO  d.h.g.s.GraderPool - Loading grader plugin 'Graja2.2' with classpathes '/usr_local_graders/dockerproxy-graja-2.2/grappa-backendplugin-dockerproxy.jar'...
2021-12-15 00:06:58,784 DEBUG d.h.g.u.ClassPathClassLoader - Current classpathes: [/usr_local_graders/dockerproxy-graja-2.2/grappa-backendplugin-dockerproxy.jar]
2021-12-15 00:06:58,784 DEBUG d.h.g.u.ClassPathClassLoader - Current extensions: [.jar]
2021-12-15 00:06:58,785 INFO  d.h.g.s.GraderPool - Loading grader config file '/usr_local_graders/dockerproxy-graja-2.2/grappa-backendplugin-dockerproxy.properties'...
2021-12-15 00:06:58,786 INFO  d.h.g.s.GraderPool - Using grader 'Graja2.2' with 5 concurrent instances.
```


