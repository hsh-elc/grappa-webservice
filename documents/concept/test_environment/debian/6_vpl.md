# Setting up VPL in your development environment

This document describes the setup of VPL inside a development environment on a Debian machine.

Setting up VPL for development on your machine involves multiple steps.

## 1. Setup the VPL Jail System

First, head over to [https://vpl.dis.ulpgc.es/](https://vpl.dis.ulpgc.es/) and download the latest version of the VPL Jail System to your computer. Then, decompress the downloaded file and run the installer.

```bash
tar -xzf vpl-jail-system-[version].tar.gz
cd vpl-jail-system-[version]
sudo ./install-vpl-sh
```

Then follow all instructions in the terminal for installing the VPL Jail System. At some time, it will ask you which programming language development packages it should install (minimum, basic, standard or full). Since we run VPL using ProFormA compatible graders, there is no need to install a lot of extra software. The basic or even the minimum package should be enough in our case.

After the installation is finished, you can edit the VPL Jail System settings by editing `/etc/vpl/vpl-jail-system.conf`. There are some useful settings that you want to set for your developtment environment:

```bash
PORT=8085 # The port on which the jail system should listen
SECURE_PORT=0 # Disable the https/wss port on your local machine
URLPATH=/qwe # Just use an easy url path
FIREWALL=0 # No firewall enabled on our local system
LOGLEVEL=8 # Show all the logs
```

All other settings can be left on their defaults. After changing the settings, you can restart the VPL Jail System:

```bash
sudo systemctl restart vpl-jail-system
```

## 2. Installing the extended VPL Moodle plugin

We now need to install the extended VPL Moodle plugin which allows easy setup of ProFormA tasks inside VPL.

First, clone the sources into your Moodle installation:

```bash
cd /var/www/html/moodle/public/mod
git clone https://github.com/hsh-elc/moodle-mod_vplproforma.git
cd vpl/
git switch develop
```

After that, open your local Moodle, log in as the Admin and set VPL's plugin settings. Most settings can be left at their default values, but there is one important setting called `Execution servers list`. Scroll down until you see that setting and add a server there:
```
http://localhost:8085/qwe
```
- The port 8085 is the port you've set in the Jail System settings.
- The `/qwe` part is the `URLPATH` setting you've set in the Jail System settings.

Then, you've successfully installed VPL for development on your system. For using VPL with ProFormA compatible graders, you can easily create VPL assignments for that. The documentation on how to do that is available in the [vpl-grappa-integration](https://github.com/hsh-elc/vpl-grappa-integration) repository.
