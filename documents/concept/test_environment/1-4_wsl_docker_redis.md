# Setting up Redis and Docker for a development environment

This document describes a setup of a Redis database and a Docker daemon on a Linux system that is to be included in a
development environment setup as described [here](1_setting_up.md).

Currently, there is a description for a setup on a Windows computer, which runs the WSL.

The following description references individual settings described [here](1_setting_up.md#Individual-data).

## Windows subsystem for linux (WSL)

Use the following instructions as a guide: `https://docs.microsoft.com/de-de/windows/wsl/install-win10`

### Install

Use manual installation.

Open Powershell as Admin. Type:
`dism.exe /online /enable-feature /featurename:Microsoft-Windows-Subsystem-Linux /all /norestart`

Then:
`dism.exe /online /enable-feature /featurename:VirtualMachinePlatform /all /norestart`

Restart computer.

Then download the wsl-installer wsl_update_x64.msi and execute it.

### Configure

Open Powershell as Admin. Type:
`wsl --set-default-version 2`

Go to the Microsoft Store and select Ubuntu 20.04 wählen. Download it (about 0,5 GB). Then "Start" (or altenatively
type `wsl.exe` in a command prompt "cmd"). Take a coffee break.

Set up a unix user:

* Username: (you could use your windows user name)
* Password: (you could use your windows password)

In the WSL command prompt type:

```
sudo apt-get update
sudo apt-get install gcc g++ make
sudo apt-get install --reinstall pkg-config cmake-data
sudo apt-get install tcl tcltls tcllib tclx
sudo apt-get install net-tools
```

### Use it

You could open more than one WSL terminal, by typing `ubuntu2004` in a cmd command prompt.

If you want to query the IP of the WSL guest, on a Windows cmd prompt type: `wsl hostname -I`. The ip changes on every
WSL startup.

(source: `https://superuser.com/questions/1582234/make-ip-address-of-wsl2-static`)

Windows drives are mounted by default on /mnt/c, /mnt/d, etc.

### Troubleshooting DNS resolving

Sometimes WSL's /etc/resolv.conf does not get an actual nameserver IP address. This can result in docker builds failing
with a "Temporary failure resolving 'archive.ubuntu.com'".

Automated Workaround: Create a file `patch-resolv-conf.sh` in your home directory with the following content:

```
#!/bin/bash
grep -v nameserver /etc/resolv.conf > /tmp/new-resolv.conf
cmd.exe /C nslookup www.google.de 2>&1 | grep Address | head -1 | sed -e 's+Address:+nameserver+g' | dos2unix >>  /tmp/new-resolv.conf
echo new-resolv.conf content:
cat /tmp/new-resolv.conf
# rm symbolic link
sudo rm /etc/resolv.conf
sudo cp /tmp/new-resolv.conf /etc/resolv.conf
```

Make this file executable. Run this script, whenever DNS resolving fails.

Manual Workaround (source: https://github.com/microsoft/WSL/issues/4285#issuecomment-522201021):

- get the nameserver of the windows host by typing `ipconfig /all` in a cmd prompt.
- replace the nameserver declaration inside the WSL in `/etc/resolv.conf` .
- This replacement is temporary. It will be overwritten on the next start of the WSL. If you want to save the
  configuration from being overwritten, then edit `/etc/wsl.conf` and put the lines
  `[network]`
  `generateResolvConf = false`

### Shutdown

When you want to shutdown the WSL instance, type `wsl --shutdown` in a windows cmd prompt.

## Redis

We install Redis inside the WSL. So most of the work is done in a WSL terminal. More details can be found
here: `https://redislabs.com/blog/redis-on-windows-10/`.

```
sudo apt-get update
sudo apt-get upgrade
sudo apt-get install redis-server
```

Now edit the file `/etc/redis/redis.conf`:

* comment out line `bind 127.0.0.1 ::1`
* add line `bind 0.0.0.0`
* activate line `requirepass foobared` with a password of your choosing. The password must be set in Grappa's
  configuration file.
  `requirepass {redis.password}`

Now restart redis using
`sudo service redis-server restart`

Test if everything is properly running by logging into redis' command line interface:

```
redis-cli
auth redispass
exit	
```

Stopping Redis is done by:
`sudo service redis-server stop`

## Docker

We install Docker inside the WSL. So most of the work is done in a WSL terminal. The installation
follows `https://docs.docker.com/engine/install/ubuntu/`.

### Install

In the WSL-Terminal type:

```
sudo apt-get update
sudo apt-get install apt-transport-https ca-certificates curl gnupg lsb-release

curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

echo \
  "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
```

Check, if the sources list have been properly modified by:
`cat /etc/apt/sources.list.d/docker.list`

Now type:

```
sudo apt-get update
sudo apt-get install docker-ce docker-ce-cli containerd.io
```

### Configure

In order to be able to use the docker CLI as root in a WSL terminal, you should add the following line at the end
of `.bashrc` of user root:
`export DOCKER_HOST=unix:///var/run/docker.sock`

### Test

Reloading the Docker service should work using
`sudo service docker start`

You can verify that Docker Engine is installed correctly by running the hello-world image: `sudo docker run hello-world`
.

Stopping the docker daemon is done by: `/etc/init.d/docker stop`.


