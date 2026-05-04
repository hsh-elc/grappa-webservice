# Setting up Moodle for a development environment

This document describes the setup of Moodle in a development environment on a Debian machine.

First, install the necessary packages:

```bash
sudo apt install apache2
sudo apt install redis-server
sudo apt install maven
sudo apt install mariadb-server
sudo apt install php php-mbstring php-zip php-gd php-json php-curl php-intl php-soap
```

You also need to install Java JDK 17. Since it is not available in the default Debian Trixie repositories, you need to download and install it manually.

Head over [here](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) and download the Linux x64 Debian package for Java SE Development Kit 17.0.12.

Then install it from your Downloads directory:

```bash
sudo apt install ./jdk-17.0.12_linux-x64_bin.deb
```

If you have multiple versions of Java installed on your system at the same time, you might need to choose JDK 17 as the used version:

```bash
sudo update-alternatives --config java
# And then choose JDK 17 from the list by typing the selection number. Then do the same for javac.
sudo update-alternatives --config javac
```

Then click through the MariaDB setup:

```bash
sudo mariadb-secure-installation
```

Then set `max_allowed_packet = 1G` or at least `100M` inside the MariaDB server config, located at `/etc/mysql/mariadb.conf.d/50-server.cnf`.

Then inside MariaDB, create the necessary user and database (open with `sudo mariadb`):

```sql
CREATE DATABASE moodle DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'moodleuser'@'localhost' IDENTIFIED BY 'moodlepassword';
GRANT SELECT,INSERT,UPDATE,DELETE,CREATE,CREATE TEMPORARY TABLES,DROP,INDEX,ALTER ON moodle.* TO 'moodleuser'@'localhost';
FLUSH PRIVILEGES;
```

Then download Moodle from the GitHub repo (maybe you have to give yourself permissions to the directory first):

```bash
cd /var/www/html
git clone https://github.com/moodle/moodle.git
cd moodle
```

The currently installed productive version is v5.1.4, which is the latest commit on the `MOODLE_501_STABLE` branch at the moment. Please inform yourself about the version you need to install, so that you don't install an outdated version.

```bash
git switch MOODLE_501_STABLE
```

Then create the moodledata directory:

```bash
sudo mkdir /var/moodledata
sudo chown www-data:www-data /var/moodledata
sudo chmod 770 /var/moodledata
```

Then set all the necessary parameters inside Moodle's config file

```bash
cp config-dist.php config.php
nano config.php
```

Set the following parameters inside the `config.php`:

```php
$CFG->dbtype = 'mariadb';
$CFG->dbhost = 'localhost';
$CFG->dbname = 'moodle';
$CFG->dbuser = 'moodleuser';
$CFG->dbpass = 'moodlepassword';
$CFG->wwwroot = 'http://localhost';
$CFG->dataroot = '/var/moodledata';
$CFG->admin = 'admin';
```

Then enable two php modules:

```bash
sudo phpenmod intl
sudo phpenmod soap
```

Then edit the php configs:

```bash
sudo nano /etc/php/8.3/apache2/php.ini
```

And set `max_input_vars = 6000` or at least `5000`.

Then edit `/etc/apache2/sites-available/000-default.conf` and set `DocumentRoot /var/www/html/moodle/public`.

Then restart apache2 with `sudo systemctl restart apache2` and open http://localhost in your browser.

```
Username: admin
Password: {your Password}
First name: Admin
Last name: User
Email address: {your E-Mail}
Full site name: Moodle Development Site
Short site name: MDS
Support email: {your E-Mail} (required-field)
```

Then alter some settings in the *Site administration*

```
Site administration -> Development -> Debugging -> Debug messages: ALL
Site administration -> Appearance -> AJAX and Javascript -> Cache Javascript: No
Site administration -> Advanced features -> Enable web services: Yes
Site administration -> Server -> Manage protocols -> REST protocol: Enable
Site administration -> Security -> Site security settings -> Enable trusted content: Yes
Site administration -> Security -> HTTP security -> cURL blocked hosts list: Remove localhost and 127.0.0.0/8
Site administration -> Security -> HTTP security -> cURL allowed ports list: Add 8080 (default port for Tomcat9)
Site administration -> Users -> Permissions -> Capability overview -> moodle/size:trustcontent -> Get the overview: Teacher: Allow
```
