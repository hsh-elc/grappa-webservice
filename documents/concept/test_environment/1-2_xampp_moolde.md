# Setting up Moodle for a development environment

This document describes a setup of Moodle in a development environment setup as described [here](1_setting_up.md).

Currently, there is a description for a setup on a Windows computer, which runs xampp.

The following description references individual settings described [here](1_setting_up.md#Individual-data).

## Xampp

### Installation

Install Xampp with php version 7.4 (switch off the virus protection on access scan) to folder {xampp.home}.

### Configuration

Configure the xampp-control.exe as being executed as admin always (context menu -> handling of compatibility problems).

Edit {xampp.home}/apache/conf/httpd.conf:
`Listen {localip}:{apache.port}`

Edit apache/httpd-ssl.conf:
`Listen {localip}:{apache.sslport}`

Edit xampp-control.ini:
```
[Common]
Edition=
Editor={editor.path}
...

[ServicePorts]
Apache={apache.port}
ApacheSSL={apache.sslport}
MySQL={mysql.port}
...
```

Edit mysql/bin/my.ini:
```
max_allowed_packet=100M
```
This allows for the import of larger database dumps.

### Test

* Start a web browser at {localip}:{apache.port}.
* Change to phpMyAdmin


## Moodle

Download Moodle 3.10.3+

Install Moodle as described in https://docs.moodle.org/310/en/Installation_quick_guide, i. e.

* Unzip the downloaded zip into {xampp.home}\htdocs\moodle
* On the webinterface {localip}:{apache.port} go to phpMyAdmin and do the following:
  - `CREATE DATABASE moodle DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`
  - create a user {mysql.moodleuser} with the password {mysql.moodlepassword}  for host localhost
  - `GRANT SELECT,INSERT,UPDATE,DELETE,CREATE,CREATE TEMPORARY TABLES,DROP,INDEX,ALTER ON moodle.* TO '{mysql.moodleuser}'@'localhost' IDENTIFIED BY '{mysql.moodlepassword}';`
* Create an empty folder {moodle.datafolder}
* Not sure, when we had to make this next step exactly. Maybe now? 
  Edit file `{xampp.home}\moodle\config.php` (or create one as a copy of `config-dist.php`) and modify the following lines:
  ```
  $CFG->wwwroot   = 'http://{localip}:{apache.port}/moodle';
  $CFG->dataroot  = '{moodle.datafolder}'; // e. g. 'D:\\moodledata'
  $CFG->admin     = 'admin';
  ```
  Note the double backslash in the value of dataroot.

* Navigate web browser to {localip}:{apache.port}/moodle
* select English
* select MariaDB
* The installation process will show missing php modules. You must enable them by editing php.ini (accessible via xampp-control.exe -> Apache -> Config -> php.ini)
    - enable:

        extension=intl
        extension=xmlrpc
        extension=soap

    - opcache not needed (see also https://docs.moodle.org/310/en/OPcache).
      If needed, you could configure:

        opcache.enable=1
        opcache.memory_consumption=128
        opcache.max_accelerated_files=10000
        opcache.use_cwd=1
        opcache.validate_timestamps=1
        opcache.save_comments=1
        opcache.enable_file_override=0

    - You need to restart apache after editing php.ini, else moodle will still show that there are missing modules

* Now the installation proceeds in the web browser. Take a coffee break.
* mod_lti reports an error: `LTI 1.3 requires a valid openssl.cnf to be configured and available to your web server. Please contact the site administrator to configure and enable openssl for this site`. Since we don't user LTI on a development machine, we ignore the message. 
* Moodle installer asks for the site admin:

  Username: admin
  PW: {moodle.adminpassword}
  E-Mail: {moodle.adminemail}

* Define site name, e. g. Development Moodle Site (short name: DMS)
* No-Reply-Address:  {moodle.adminemail}


Now we make some site specific configurations:

In the Moodle web interface logged in as the site amin go to: 
* site administration -> Development -> Debugging -> Debug messages = ALL.
* site administration -> Appearance -> Ajax and Javascript -> Cache Javascript = No.  (see also: https://docs.moodle.org/dev/Making_changes_show_up_during_development#JavaScript)
* site administration -> Advanced feature -> Enable web services = Yes  (this might help in calling web services of the qtype plugin via curl for debugging purposes)
* site administration -> Plugins -> Webservices -> Manage protocols -> REST -> enable -> Save changes (this might help in calling web services of the qtype plugin via curl for debugging purposes)
* site administration -> Security -> Site security settings -> Enable trusted content-> Yes  (this allows Javascript uploaded by teachers in text fields, which is used by Graja tasks). Then Save Changes.
* site administration -> Security -> HTTP Security -> cURL blocked hosts list -> remove {localip} from the list -> Save changes
* site administration -> Security -> HTTP Security -> cURL allowed ports list -> add {tomcat.port} to the list -> Save changes
* site administration -> Users -> Permissions -> Capability overview -> search for trustcontent -> select the capability "moodle/site:trustcontent" and choose "Get the overview". Check, that teachers have the permission.

In your webbrowser disable Javascript caching. E. g. Chrome:
* Developer tools > Network > Disable cache


### Troubleshooting

If Moodle responds with "Error: Database connection failed", the reason could be a bug in xampp, that corrupts the mysql.user database table. Try the following steps:
* In phpMyAdmin select all tables of the `mysql` database and choose *Repair table*
* Repeat the following SQL statement:
  `GRANT SELECT,INSERT,UPDATE,DELETE,CREATE,CREATE TEMPORARY TABLES,DROP,INDEX,ALTER ON moodle.* TO '{mysql.moodleuser}'@'localhost' IDENTIFIED BY '{mysql.moodlepassword}';`

### Theme Plugin

This section describes the installation of a Moodle theme in order get the development environment as close as possible to the productive environment. If your productive environment runs with the default theme, you can skip this section.

#### Download / fetch the plugin

In order to install a theme, you simply clone a git repository into the moodle document tree. E. g. to install the [Boost Campus Plugin](https://moodle.org/plugins/theme_boost_campus):

```
cd ${xampp.home}
git clone https://github.com/moodleuulm/moodle-theme_boost_campus.git htdocs/moodle/theme/boost_campus
```

#### Install

As soon as an administrator logs in to Moodle, he will be requested to install the plugin.

After installation, select the new theme via Site administration > Appearance > Theme selector > Change theme.

#### Configuration

Optionally you could make the following changes to the configuration of the plugin:

* Select a theme preset: Site administration > Appearance > Themes > Boost campus > General settings > Additional theme preset files > (add a file). Then after saving the changes select the theme preset.
* Brand colours: Site administration > Appearance > Themes > Boost campus > General settings > Brand colour > #333333
* In course settings: Site administration > Appearance > Themes > Boost campus > Course Layout settings > In course settings menu = YES
* Enable info banner in order do make it clearly visible that this is a development installation: Site administration > Appearance > Themes > Boost campus > Info banner settings > 
  - Enable perpetual info banner = YES
  - Perpetual information banner content = Development installation (or any other text)
  - Page layouts to display the info banner on: select all
  - Bootstrap css class for the perpetual info banner: Info color
  - Save


## Moodle for developers

### Documentation

Moodle documentation for developers:
* https://docs.moodle.org/dev/Main_Page
* https://docs.moodle.org/dev/Finding_your_way_into_the_Moodle_code
* https://docs.moodle.org/en/Developer_tools
* https://docs.moodle.org/en/Debugging
* https://docs.moodle.org/en/Administration_via_command_line
* https://wimski.org/api/3.10/classes.html


Moodle documentation for developers of question type plugins:
* https://docs.moodle.org/dev/Overview_of_the_Moodle_question_engine
* https://docs.moodle.org/dev/Question_behaviours
* https://docs.moodle.org/dev/Question_types
* https://docs.moodle.org/dev/Using_the_question_engine_from_module

### Solutions for common problems

* Purge all Moodle caches:
  https://docs.moodle.org/en/Administration_via_command_line#Purge_caches

