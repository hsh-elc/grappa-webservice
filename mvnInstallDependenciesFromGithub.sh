#!/bin/bash

set -e # exit on first error

MVNOPTS=""
CURLOPTS=""
VERBOSE="1"
GBPLIBVER="$(mvn help:evaluate -Dexpression=de.hsh.grappa.grappa-backendplugin.version -q -DforceStdout)"
PFLIBVER="$(mvn help:evaluate -Dexpression=proforma.version -q -DforceStdout)"
TMPDIR=/tmp

unameOut="$(uname -s)"
case "${unameOut}" in
    CYGWIN*)    TMPDIRWIN=`cygpath -w ${TMPDIR}`;;
    *)          TMPDIRWIN=${TMPDIR}
esac


while getopts 'q:h' opt; do
  case "$opt" in
    q)
      MVNOPTS="-q"
      CURLOPTS="-s"
      VERBOSE="0"
      ;;

    ?|h)
      echo "This script downloads libraries dependencies from github and"
      echo "installs it to your local maven repository."
      echo ""
      echo "Usage: $(basename $0) [-q]"
      echo "  -h            help"
      echo "  -q            quiet"
      exit 1
      ;;
  esac
done
shift "$(($OPTIND -1))"



declare -a arrayProformaDownloads=(\
  proforma-${PFLIBVER}.pom \
  proformaxml-${PFLIBVER}.pom \
  proformaxml-2-1-${PFLIBVER}.pom  \
  proformautil-${PFLIBVER}.pom  \
  proformautil-2-1-${PFLIBVER}.pom  \
  proformaxml-${PFLIBVER}.jar \
  proformaxml-2-1-${PFLIBVER}.jar  \
  proformautil-${PFLIBVER}.jar  \
  proformautil-2-1-${PFLIBVER}.jar  \
)

declare -a arrayGBPDownloads=(\
  grappa-backendplugin-${GBPLIBVER}.pom \
  grappa-backendplugin-${GBPLIBVER}.jar \
)


# Working directory:
WDIR="$TMPDIR/mvnInstallDependenciesFromGithub"
mkdir -p "$WDIR"
WDIRWIN="$TMPDIRWIN/mvnInstallDependenciesFromGithub"


echoline() {
    local text=$1
    if [ $VERBOSE -ne 0 ]; then
        echo "-------------------------------------------------------------------------------------"
        echo $text
        echo "-------------------------------------------------------------------------------------"
    fi
}



download() {
    local file=$1
    local url=$2
    echoline "   downloading from $url to $WDIR/$file"
    wget $CURLOPTS \
        -O "$WDIR/$file" \
        $url
}

downloadGrappaBackendplugin() {
    local file=$1
    local url="https://github.com/hsh-elc/grappa-backendplugin/releases/download/v${GBPLIBVER}/$file"
    download "$file" "$url"
}

downloadProforma() {
    local file=$1
    local url="https://github.com/hsh-elc/proforma/releases/download/v${PFLIBVER}/$file"
    download "$file" "$url"
}



deploy() {
    local file=$1
    echoline "   mvn install $file"

    extension="${file##*.}"
    filename="${file%.*}"

    mvn $MVNOPTS install:install-file \
      -Dfile="$WDIRWIN/$file" \
      -DpomFile="$WDIRWIN/$filename.pom" 
}




for i in "${arrayProformaDownloads[@]}"
do
    downloadProforma "$i"
    deploy "$i"
done

for i in "${arrayGBPDownloads[@]}"
do
    downloadGrappaBackendplugin "$i"
    deploy "$i"
done

# cleanup
if [ -d "$WDIR" ]; then
  rm -rf $WDIR
fi
