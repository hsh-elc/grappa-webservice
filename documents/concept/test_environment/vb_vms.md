
# WIP: Setup dev. Servers for MooPT and Grappa as Virtual Box VMs
as a group in VirtualBox connected with a Host-only-network (I'm sure if there's an equivalent in VMWare...)
- Moodle (with MooPT)
- Grappa (containing BackendPlugins and Graders)

Note: Especially the chars "..." in this document imply TBD-sections...

Overview:
- ["HW" Setup](#-hw--setup)
  * [Moodle Machine](#moodle-machine)
  * [Grappa Machine](#grappa-machine)
  * [Bringing them together](#bringing-them-together)
- [Now install SW...](#now-install-sw)
  * [MooPT](#moopt)
  * [Grappa](#grappa)
    + [Reqs](#reqs)
      - [Redis](#redis)
      - [Docker (should be done already ;)](#docker--should-be-done-already---)
      - [Tomcat](#tomcat)
      - [Maven](#maven)
    + [Grappa](#grappa-1)
      - [Config grappa:](#config-grappa-)


# "HW" Setup

## Moodle Machine 
...Complicated way? Or just finished image?
`ova` Image from [bitnami](https://bitnami.com/stack/moodle/virtual-machine) (app. 700 MB)

- set name: `abp_moodle_moopt_bitnami`
- 2 CPU
- 1 GB RAM
- set group: ABP (easier for setting up shared network)

- run login with default-pw and set new 
... I chose: bitnami & `moodle-moopt`


Moodle should be confirgured already
- above in yellow the current IP and user Name and PW is stated
	- or see file `/home/bitnami/bitnami_credentials`
	- ... In my cas it is `user` and `GrEky5GJNWrc`
- goto IP and login into Moodle
... Better the way with the Host-only network

**Enable ssh**  
```
sudo vim /etc/ssh/sshd_conifg
```
change `PasswordAuthentication` to `yes`

```
sudo rm -f /etc/ssh/sshd_not_to_be_run
sudo systemctl enable ssh
sudo systemctl start ssh
```
Check with `sudo systemctl status ssh`

If not already exists make directory `~/.ssh` and copy your public key into `authorized_keys`.

From now we need the VMs only headless :)
...See script `start_vms_and_ssh_to_grappa.sh`
Option `headless` brings these two machines down to 10% of one CPU on idle ;)



## Grappa Machine


Add new machine from chosen image
- new machine
- name: `abp_grappa_ubuntu_20-04`
- 2GB RAM
- 50 GB HDD
- Iso-Image from [here](https://ubuntu.com/download/server)
	- I chose Ubuntu Server 20.04

Run installation acc. to https://ubuntu.com/tutorials/install-ubuntu-server#1-overview

- choose install OpenSSH
	- Alternatively LINK...
Features
- choose `docker` (in order to skip this step later ;) )
	- Alternatively LINK...

User
...I chose
- `grappa` & `grappa`

If installation hangs on "curtin hooks"-spinning wheel, but it says "Installation complete!" at the top, you can go on, rebooting the server ;)




## Bringing them together

- VB->Tools->Network
- create new "Host-only-adapter"
	- note it's IP (here it is 192.168.**57**.**1**)
	- create DHCP server with 192.168.57.**2**, so your clients run with 3 and 4
- in both VMs add this as an adapter next to network bridge
	- first is our host-only (`vboxnet1`)
	- second network bridge
- now they can have static IPs in the host-only network and can reach each other independently from host network

...since it's an Debian, we need some packages...



Ubuntu needs second interface
- Get interafce name of bridged interface by `ip a` (here it was `enp0s8`)
- `sudo nano /etc/netplan/00-installer-config.yaml`
	- add following two lines  
		```
		enp0s8:
		dhcp4: true
		```
	above the version-tag.
- apply with: `sudo netplan apply`


# Now install SW...


## MooPT

```
sudu apt install git
```

...Long story short:

```
git clone https://github.com/hsh-elc/moodle-qtype_moopt.git /home/bitnami/stack/moodle/question/type/programmingtask
git clone https://github.com/hsh-elc/moodle-qbehaviour_immediatemoopt.git /home/bitnami/stack/moodle/question/behaviour/immediateprogrammingtask
git clone https://github.com/hsh-elc/moodle-qbehaviour_deferredmoopt.git /home/bitnami/stack/moodle/question/behaviour/deferredprogrammingtask

git -C /home/bitnami/stack/moodle/question/type/programmingtask checkout develop
git -C /home/bitnami/stack/moodle/question/behaviour/immediateprogrammingtask checkout develop
git -C /home/bitnami/stack/moodle/question/behaviour/deferredprogrammingtask checkout develop
```

I needed to correct permissions to upload files. 
(Not sure, if all of these are necessary, but before it wasn't possible to import tasks.)

...Set correct perms: https://discuss.moodlebox.net/d/88-permissions-for-moodle-folder-files/4
```
sudo chmod -R 0755 /home/bitnami/stack/moodle/question/type/programmingtask
sudo find /home/bitnami/stack/moodle/question/type/programmingtask -type f -exec chmod 0644 {} \;
sudo chgrp -R daemon /home/bitnami/stack/moodle/question/type/programmingtask
sudo chmod g+w -R /home/bitnami/stack/moodle/question/type/programmingtask

sudo chmod -R 0755 /home/bitnami/stack/moodle/question/behaviour/immediateprogrammingtask
sudo find /home/bitnami/stack/moodle/question/behaviour/immediateprogrammingtask -type f -exec chmod 0644 {} \;
sudo chgrp -R daemon /home/bitnami/stack/moodle/question/behaviour/immediateprogrammingtask
sudo chmod g+w -R /home/bitnami/stack/moodle/question/behaviour/immediateprogrammingtask

sudo chmod -R 0755 /home/bitnami/stack/moodle/question/behaviour/deferredprogrammingtask
sudo find /home/bitnami/stack/moodle/question/behaviour/deferredprogrammingtask -type f -exec chmod 0644 {} \;
sudo chgrp -R daemon /home/bitnami/stack/moodle/question/behaviour/deferredprogrammingtask
sudo chmod g+w -R /home/bitnami/stack/moodle/question/behaviour/deferredprogrammingtask
```


Login as admin user (here `user` above)...
- "Upgrade Moodle database now"
- "Continue"
- Configure Grappa-URL: `http://{localip}:{tomcat.port}/grappa-webservice-2.0.0/rest` 
	So here: `http://192.168.57.4:8080/grappa-webservice-2.0.0/rest`
...Port above...
- "Save changes"

	
... Set DEBUG Prefs (as stated in Robert's Doku)
In the Moodle web interface logged in as the site amin go to:
* site administration -> Development -> Debugging -> Debug messages = ALL.
* site administration -> Appearance -> Ajax and Javascript -> Cache Javascript = No. (see also: https://docs.moodle.org/dev/Making_changes_show_up_during_development#JavaScript)
* site administration -> Advanced feature -> Enable web services = Yes (this might help in calling web services of the qtype plugin via curl for debugging purposes)
* site administration -> Security -> Site security settings -> Enable trusted content-> Yes (this allows Javascript uploaded by teachers in text fields, which is used by Graja tasks). Then Save Changes.
* site administration -> Users -> Permissions -> Capability overview -> search for trustcontent -> select the capability “moodle/site:trustcontent” and choose “Get the overview”. Check, that teachers have the permission.

	
## Grappa 

### Reqs
#### Redis
```
sudo apt-get update && sudo apt-get -y upgrade 
sudo apt-get -y install redis-server
```

```
sudo nano /etc/redis/redis.conf
bind 0.0.0.0
requirepass grappa-redis-pw
```

`sudo service redis-server restart`
	

to start on reboot: `sudo systemctl enable redis-server`

#### Docker (should be done already ;)

#### Tomcat
first java:
`sudo apt -y install default-jdk`

`sudo apt install tomcat9 tomcat9-admin`

`sudo nano /etc/tomcat9/tomcat-users.xml`

```	
<role rolename="admin"/>
<role rolename="manager"/>
<role rolename="manager-gui"/>
<user username="admin" password="grappa-tomcat-pw" roles="admin,manager,manager-gui"/>
```

...I think I can skip step with commenting out `RemoteAddrValve`...

`sudo systemctl restart tomcat9`

...available on http://192.168.57.4:8080 resp. http://192.168.57.4:8080/manager/html

#### Maven
```
sudo apt update
sudo apt -y install maven
```

### Grappa

```
git clone https://github.com/hsh-elc/grappa-webservice.git ~/grappa-webservice
git -C ~/grappa-webservice checkout develop
```

...Redo after every change ;)

...Build docker-images (after adding pathes to new Dockerfiles)
```
(cd ~/grappa-webservice/grappa-backend-plugin-docker-proxy/src/main/resources/docker; sudo sh build-images.sh)
```
(Doing this in a sub-shell doesn't require to change to this directory ;) )
...Do I need sudo here?

Build `grappa-webservice`:
```
cd ~/grappa-webservice
mvn install package -DskipTests
```

Copy into tomcats webapp:
```
sudo cp ~/grappa-webservice/grappa-webservice/target/grappa-webservice-2.0.0.war /var/lib/tomcat9/webapps/
```
Now tomcat should recognize grappa in http://192.168.57.4:8080/manager/html.
	
	
#### Config grappa:
```
sudo mkdir -p /etc/grappa
sudo cp ~/grappa-webservice/grappa-webservice/src/main/resources/grappa-config.yaml.example /etc/grappa/grappa-config.yaml
```

Edit `/etc/grappa/grappa-config.yaml` below `cache:redis:` by entering redis' IP and password.


`sudo systemctl restart tomcat9`

HTTP resposne should be a `200` ;)
```
curl -v --user test:test http://127.0.0.1:8080/grappa-webservice-2.0.0/rest
```












