# Developer Documentation

This document is meant for _developers_ to assist integrating own/new graders into Grappa and testing them.

Overview:
- [Setting up test environment](#setting-up-test-environment)
- [Building Grader Docker Images for Grappa](#building-grader-docker-images-for-grappa)
  * [Recommendation: Backend-Plugin Repo Structure](#recommendation--backend-plugin-repo-structure)
  * [Version number convention](#version-number-convention)
  * [Docker-Interface](#docker-interface)
    + [With Java (inherit from `grappa-backend-base`)](#with-java--inherit-from--grappa-backend-base--)
      - [Individual Entrypoint](#individual-entrypoint)
      - [If the base image was updated](#if-the-base-image-was-updated)
      - [To understand DockerProxy calling sequence](#to-understand-dockerproxy-calling-sequence)
    + [Without Java (independent Grader)](#without-java--independent-grader-)
    + [Private Docker-Image-Registry](#private-docker-image-registry)
    + [Build Images](#build-images)
    + [Changes in remote images](#changes-in-remote-images)
    + [Docker Troubleshoot](#docker-troubleshoot)
  * [Github Actions for automated pushing Images](#github-actions-for-automated-pushing-images)

# Setting up test environment

_Note: These are our internal notes for setting up test environments. 
These may drive you crazy, but may help. 
Also these docs are partially outdated. 
Consult these notes on your own risk ;)_

Here we collect all documents that are relevant for developers of Grappa and MooPT. 

Currently we have:

- [Quickly set up Docker-Compose Test Environment for Grappa and MooPT](https://github.com/hsh-elc/grappa-moopt-test-env-docker)
- [Setting up a development computer (Win10) for MooPT and Grappa](test_environment/1_setting_up.md)
- [Setup dev. Servers for MooPT and Grappa as Virtual Box VMs](test_environment/vb_vms.md)

# Building Grader Docker Images for Grappa

## Recommendation: Backend-Plugin Repo Structure
We recommend to structure the repo according to the [module of the Dummygrader](grappa-backendplugin-dummygrader/)
- Here you'll find a folder `docker/` at root of repo
  - containing file `Dockerfile` and folder `grader/` (containing `jar`-files).
- If you build the backend plugin, compiled `jar`-file should be copied into `docker/grader/`.
- Now you can build the docker image according to [Build Images](#build-images):
  ```
  docker build --tag <image-name>:<version> docker/
  ```

Following these structure you can easily configure GitHub Actions for building and pushing images 
to [GitHub Container Registry (GHCR)](http://ghcr.io) as below in [Github Actions for automated pushing Images](#github-actions-for-automated-pushing-images).

<!-- ...
Example backend-plugins/actions:
- GraFLAP with Link to `./github/workflows`
- Graja with Link to `./github/workflows`
 -->


## Version number convention
* Since the version number is used for the image tag that identifies images on local docker installation, this should be increased with every update of the BP.
* The backend plugin takes the same version number as the contained grader extended by one place for the backend plugin itself
	* eg. grader version: `2.1` -> backend plugin version `2.1.0`
	* eg. grader version: `0.1b` -> backend plugin version `0.1b.13`

* Images of develop branches get the branch name as postfix.
	* eg. `develop` branch of grader version: `2.1` -> backend plugin version `2.1.0-develop`

* Additionally the image of the current `master` branch gets the tag `latest` also
	```
	docker tag <image-name-of-current-master> <image-name>:latest
	docker push <image-name>:latest
	```

* Note: For production you should always choose one fix version tag within `grappa-config.yaml`

## Docker-Interface

### With Java (inherit from `grappa-backend-base`)
* Easy way: you have some jars:
    * place jars in `/opt/grader/`
    * set `ENV GRAPPA_PLUGIN_GRADER_CLASSNAME="<full-qualified-classname>"` in `Dockerfile` 
* More than jars?
    * set `ENV GRAPPA_PLUGIN_GRADER_FILEEXTENSION=".jar;.class;.zip"`
* Additional absolute paths not below `/opt/grader/`?
    * set `ENV GRAPPA_PLUGIN_GRADER_CLASSPATHES="/opt/grader;/some/abs/path"`
        * Note to include also `/opt/grader` as long as grader files are here
* If you want to change more than these, you should be thinking about an independent grader ;)


See [Dummygrader's Dockerfile](grappa-backendplugin-dummygrader/docker/Dockerfile) for more details.
This file should be used as a blueprint.

For a minimal example this is a complete Dockerfile for a backend plugin:
```
FROM ghcr.io/hsh-elc/grappa-backend-base:latest
ADD ["grader", "/opt/grader"]
ENV GRAPPA_PLUGIN_GRADER_CLASSNAME="de.hsh.grappa.backendplugin.graflap.GraFlapPlugin"
```

#### Individual Entrypoint
If you need an individual Entrypoint (usually not), make sure you run `/setup.sh` manually in there.
Eg. define own entrypoint script:
```
ENTRYPOINT ["/bin/bash", "/some_script.sh"]
```
and within call in last line: 
```
#last line of "/some_script.sh"
source /setup.sh
```

To find what script you need to call, inspect image:
```
docker inspect image grappa-backend-base:latest
```
Look for tag `Config.Cmd`, here you will find entry `/setup.sh`. 
Alternatively, entrypoint is set in tag `Config.Entrypoint` which is currently null.

#### If the base image was updated
If you choose `latest`-tag for your base image, this does not automatically updates your local images, if base image changes.
You’ll have to manually update your images.
Just run another `docker build` and from the first line of your image (`FROM some-base:xy`) Docker will pull remote updates and build your new image locally (you’ll have to do this for _each_ child.image).

Thinking in Github Actions this means, you’ll have to manually trigger your workflow that pulls updated base image and builds your image accordingly. 

#### To understand DockerProxy calling sequence

* Baseimage is built from `grappa-backendstarter/docker/`
* Dummygrader is buit from `grappa-backendplugin-dummygrader/docker/`
* DockerProxyBP writes `graderBP.properties` on the fly (acc. to `grappa-config.yaml`) and copies it into grader container at grading time
	* These properties will be given the `init()` method of the backend plugin implementation.
	* What happens next is up to the developer of the grader.

* Withtin the baseimage the script `setup.sh` generates the file `grappa-grader-backend-starter.properties` acc. to `ENV`'s defined in the Dockerfile
	* Default values are set in baseimage and can be overwritten in inherited grader images (see [With Java (inherit from `grappa-backend-base`)](#with-java-inherit-from-grappa-backend-base))).

* Folders within baseimage
    * `/opt/grader/` contains all grader files. 
    This folder is set as `ENV` as the default class-path.
    By default this folder will be searched recursively for all files matching defined `fileextension`.
    * `/opt/grader-backend-starter/` contains the backend-starter jar next to the `setup.sh` which creates the `grappa-grader-backend-starter.properties`.


### Without Java (independent Grader)
Since you are not inheriting anything, do whatever you want.
Therefore define the 3 directories in `grappa-config.yaml` below `docker_jvm_bp` of a grader:  
(But you can use default values for sure. )
* Read ProFormA-Submission from where you want:  
`copy_submission_to_dir_path: /where/should/the/submission/be/placed`
* You can parse properties if you want (this file will be created anyways):  
`copy_grader_plugin_defaults_properties_to_file: /where/to/place/graderBP.properties`
* Place ProFormA-Response where you want:  
`load_response_from_dir_path: /where/is/response/expected`

Make sure, pathes set here already exist within the container.

Further settings from `grappa-config.yaml` comes within the environment variable `SYSPROPS` (actually meant for use in JVM, but you can sniff them here).
For example the enviroment variable `SYSPROPS` could be look like this at runtime:
```
$ echo $SYSPROPS
-Dfile.encoding=UTF8 -Duser.country=DE -Duser.language=de -Duser.timezone=Europe/Berlin -Dlogging.level=DEBUG
```

### Private Docker-Image-Registry
Since grappa only runs locally installed images you need to pull images independently from Grappa.
To pull a private Docker image you need to login with an authorized user, which has a PAT registered.

According to [Docker's documentation](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry#authenticating-to-the-container-registry) you can login:
```bash
export CR_PAT=<PAT>
echo $CR_PAT | docker login ghcr.io -u <github-user> --password-stdin
```
(This PAT will be stored in `~/.docker/config.json`.)

After that login you can pull private images that you have read-permission for. E.g.
```
docker pull ghcr.io/hsh-elc/grappa-backendplugin-graflap:latest
```


### Build Images 
From the root of a backend plugin repo or module you can build and tag image with:
```
docker build -t <image-name>:<image-tag> ./docker
```
- `<image name>` should be something short like `grappa-backend-graderxy` to distinguish it from remote images (whose names begin with registry url like `ghcr.io`).
- `<image-tag>` should be the version of the grader (see [Version number convention](#version-number-convention)).

### Changes in remote images
Docker will search for image names locally at first. 
If there is a matching image name this image will be chosen.

**BUT!** If you configured the `latest` tag, this means Docker will run latest local version of this image and _not remote one_.
So if you know, there are changes on this latest image remote, you’ll have to call manually:
```
docker pull image-xyz:latest
```

### Docker Troubleshoot
* cannot pull image (`Error response from daemon: Head "https://ghcr.io/v2/<xyz>/latest": denied: denied`)
    * At first try `docker logout`.
    * If error still stays inspect folder `~/.docker` for some configs
        * I had some old stuff here in `~/.docker/config.json` like: 
        ```json
        {
         	"auths": { 
        		"ghcr.io": { 
					"auth": "abc…XYz" 		
				} 	
			}
		 } 
        ```
        * After removing these (or rather by `mv .docker/ .dockerBKP`) I was able to pull.

## Github Actions for automated pushing Images 

See [Github Docs](https://docs.github.com/en/packages/managing-github-packages-using-github-actions-workflows/publishing-and-installing-a-package-with-github-actions#upgrading-a-workflow-that-accesses-ghcrio)
for publishing actions
(at the very end of this page there’s a beautiful example for building and pushing a Docker image into GHCR).

You can view actions ([`.github/workflows`](/.github/workflows).) for this repo, since these build the images for the base and dummygrader.
Another example for only one backend plugin can be found in [GraFLAP](https://github.com/hsh-elc/grappa-backendplugin-graflap/blob/dev/kilian_21/.github/workflows/java2docker_graflap.yml).

Note: You’ll have to push the `latest`-tag manually with every new version you push. 
So tag your newest image as `latest` also and push both images.
E.g.
```
jobs: [...]
  build: [...]
    steps: [...]
      - name: Push image
        run: |
          docker tag $IMAGE_NAME $IMAGE_ID:$IMAGE_TAG
          docker tag $IMAGE_NAME $IMAGE_ID:latest
          docker push $IMAGE_ID:$IMAGE_TAG
          docker push $IMAGE_ID:latest
```

Viewing your GitHub Packages usage follow:  
Profile (/Organisation) ->Settings ->Access ->Billing and plans  
Shorthand: https://docs.github.com/en/billing/managing-billing-for-github-packages/viewing-your-github-packages-usage 





