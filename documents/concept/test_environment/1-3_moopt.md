# Setting up MooPT for a development environment

This document describes a setup of the MooPT plugin inside a Moodle installation in a development environment setup as
described [here](1_setting_up.md).

Currently, there is a description for a setup on a Windows computer, which runs xampp.

The following description references individual settings described [here](1_setting_up.md#Individual-data).

## MooPT

The MooPT extension of Moodle consists of three plugins: a qtype plugin and two qbehaviour plugins.

### Download / fetch plugins

Clone the Git-Repositories for the Plugins:

```
cd ${xampp.home}
git clone https://github.com/hsh-elc/moodle-qtype_moopt.git htdocs/moodle/question/type/moopt
git clone https://github.com/hsh-elc/moodle-qbehaviour_immediatemoopt.git htdocs/moodle/question/behaviour/immediatemoopt
git clone https://github.com/hsh-elc/moodle-qbehaviour_deferredmoopt.git htdocs/moodle/question/behaviour/deferredmoopt
git clone https://github.com/hsh-elc/moodle-qbehaviour_deferredmooptcbm.git htdocs/moodle/question/behaviour/deferredmooptcbm
git clone https://github.com/hsh-elc/moodle-qbehaviour_immediatemooptcbm.git htdocs/moodle/question/behaviour/immediatemooptcbm
git clone https://github.com/hsh-elc/moodle-qbehaviour_interactivemoopt.git htdocs/moodle/question/behaviour/interactivemoopt
git clone https://github.com/hsh-elc/moodle-qbehaviour_adaptivemoopt.git htdocs/moodle/question/behaviour/adaptivemoopt
git clone https://github.com/hsh-elc/moodle-qbehaviour_adaptivemooptnopenalty.git htdocs/moodle/question/behaviour/adaptivemooptnopenalty

git -C htdocs/moodle/question/type/moopt checkout develop
git -C htdocs/moodle/question/behaviour/immediatemoopt checkout develop
git -C htdocs/moodle/question/behaviour/deferredmoopt checkout develop
git -C htdocs/moodle/question/behaviour/deferredmooptcbm checkout develop
git -C htdocs/moodle/question/behaviour/immediatemooptcbm checkout develop
git -C htdocs/moodle/question/behaviour/interactivemoopt checkout develop
git -C htdocs/moodle/question/behaviour/adaptivemoopt checkout develop
git -C htdocs/moodle/question/behaviour/adaptivemooptnopenalty checkout develop
```

### Install

As soon as an administrator logs in to Moodle, he will be requested to install the plugin.

### Configuration

#### qtype:

* LMS-ID: `test`
* LMS- Password: `test`
* Service URL: `http://{localip}:{tomcat.port}/grappa-webservice-2/rest`

#### moodle:

* site administration -> Security -> HTTP Security -> cURL blocked hosts list -> remove {localip} from the list -> Save
  changes
* site administration -> Security -> HTTP Security -> cURL allowed ports list -> add {tomcat.port} to the list -> Save
  changes

### Uninstalling and reinstalling MooPT from command line

This step isn't needed when setting up the development environment. It can be useful further on, when you want to
replace the currently installed MooPT plugin with a new one after some code changes, without relying on the upgrade
mechanism in Moodle plugins.

* Open a bash in WSL as administrator
* Change to directory `{xampp.home}`
* Type
  ```
  php/php.exe htdocs/moodle/admin/cli/uninstall_plugins.php --plugins=qtype_moopt,qbehaviour_immediatemoopt,qbehaviour_deferredmoopt --run
  ```
* Then copy the new version of the plugin to the apache document root, e. g. by pulling the current source from the
  remote repository:
  ```
  git -C htdocs/moodle/question/type/moopt pull
  git -C htdocs/moodle/question/behaviour/immediatemoopt pull
  git -C htdocs/moodle/question/behaviour/deferredmoopt pull
  git -C htdocs/moodle/question/behaviour/deferredmooptcbm pull
  git -C htdocs/moodle/question/behaviour/immediatemooptcbm pull
  git -C htdocs/moodle/question/behaviour/interactivemoopt pull
  git -C htdocs/moodle/question/behaviour/adaptivemoopt pull
  git -C htdocs/moodle/question/behaviour/adaptivemooptnopenalty pull
  ```
* Then navigate your web browser as site admin to the dashboard. The Plugin installation is done in the webbrowser as
  before.




