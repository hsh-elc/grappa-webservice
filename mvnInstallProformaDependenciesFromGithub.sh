#!/bin/bash

set -e # exit on first error

MVNOPTS=""
CURLOPTS=""
VERBOSE="1"
PFLIBVER="$(mvn help:evaluate -Dexpression=proforma.version -q -DforceStdout)"
TMPDIR=/tmp

unameOut="$(uname -s)"
case "${unameOut}" in
    CYGWIN*)    TMPDIRWIN=`cygpath -w ${TMPDIR}`;;
    *)          TMPDIRWIN=${TMPDIR}
esac


while getopts 'qr:h' opt; do
  case "$opt" in
    q)
      MVNOPTS="-q"
      CURLOPTS="-s"
      VERBOSE="0"
      ;;

    r)
      arg="$OPTARG"
      PFLIBVER=${OPTARG}
      ;;
   
    ?|h)
      echo "This script downloads a proforma java library release from github and"
      echo "installs it to your local maven repository."
      echo "All releases can be found here:"
      echo "  https://github.com/hsh-elc/proforma/releases"
      echo ""
      echo "Usage: $(basename $0) [-q] [-r release]"
      echo "  -h            help"
      echo "  -q            quiet"
      echo "  -r release    select release. Default release is $PFLIBVER"
      exit 1
      ;;
  esac
done
shift "$(($OPTIND -1))"



declare -a arrayDownloads=(\
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


# Working directory:
WDIR="$TMPDIR/mvnInstallProformaDependenciesFromGithub"
mkdir -p "$WDIR"
WDIRWIN="$TMPDIRWIN/mvnInstallProformaDependenciesFromGithub"


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
    local url="https://github.com/hsh-elc/proforma/releases/download/v${PFLIBVER}/$file"
    echoline "   downloading from $url to $WDIR/$file"
    curl $CURLOPTS -L \
        -o "$WDIR/$file" \
        $url
        
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




for i in "${arrayDownloads[@]}"
do
    download "$i"
    deploy "$i"
done

# cleanup
if [ -d "$WDIR" ]; then
  rm -rf $WDIR
fi
