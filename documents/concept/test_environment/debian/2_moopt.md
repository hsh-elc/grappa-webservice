# Setting up MooPT for development

This document describes the setup of MooPT inside a development Moodle environment on a Debian machine.

First, clone all necessary repositories:

```bash
cd /var/www/html/moodle/public
git clone https://github.com/hsh-elc/moodle-qtype_moopt.git moodle/question/type/moopt
git clone https://github.com/hsh-elc/moodle-qbehaviour_immediatemoopt.git moodle/question/behaviour/immediatemoopt
git clone https://github.com/hsh-elc/moodle-qbehaviour_deferredmoopt.git moodle/question/behaviour/deferredmoopt
git clone https://github.com/hsh-elc/moodle-qbehaviour_deferredmooptcbm.git moodle/question/behaviour/deferredmooptcbm
git clone https://github.com/hsh-elc/moodle-qbehaviour_immediatemooptcbm.git moodle/question/behaviour/immediatemooptcbm
git clone https://github.com/hsh-elc/moodle-qbehaviour_interactivemoopt.git moodle/question/behaviour/interactivemoopt
git clone https://github.com/hsh-elc/moodle-qbehaviour_adaptivemoopt.git moodle/question/behaviour/adaptivemoopt
git clone https://github.com/hsh-elc/moodle-qbehaviour_adaptivemooptnopenalty.git moodle/question/behaviour/adaptivemooptnopenalty
```

Then switch to the development branch:

```bash
git -C moodle/question/type/moopt switch develop
git -C moodle/question/behaviour/immediatemoopt switch develop
git -C moodle/question/behaviour/deferredmoopt switch develop
git -C moodle/question/behaviour/deferredmooptcbm switch develop
git -C moodle/question/behaviour/immediatemooptcbm switch develop
git -C moodle/question/behaviour/interactivemoopt switch develop
git -C moodle/question/behaviour/adaptivemoopt switch develop
git -C moodle/question/behaviour/adaptivemooptnopenalty switch develop
```

Open http://localhost and click through.

```
LMS-ID: test
LMS-Password: test
Service URL: http://localhost:8080/grappa-webservice-2/rest
```
