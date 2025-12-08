#!/bin/bash

set -e # exit on first error

PFLIBVER=0.4.0
GHURL="https://github.com/hsh-elc/proforma/releases/download"

# Jars to be downloaded and installed:
declare -A arrayDownloads=(\
  [proforma-${PFLIBVER}.pom]="${GHURL}/v${PFLIBVER}/proforma-${PFLIBVER}.pom" \
  [proformaxml-${PFLIBVER}.jar]="${GHURL}/v${PFLIBVER}/proformaxml-${PFLIBVER}.jar" \
  [proformaxml-2-1-${PFLIBVER}.jar]="${GHURL}/v${PFLIBVER}/proformaxml-2-1-${PFLIBVER}.jar"  \
  [proformautil-${PFLIBVER}.jar]="${GHURL}/v${PFLIBVER}/proformautil-${PFLIBVER}.jar"  \
  [proformautil-2-1-${PFLIBVER}.jar]="${GHURL}/v${PFLIBVER}/proformautil-2-1-${PFLIBVER}.jar"  \
)


# Working directory:
mkdir -p target/tmp/download

# Repository path:
unameOut="$(uname -s)"
PWN=`pwd`
case "${unameOut}" in
    CYGWIN*)    REPOPATH=`cygpath -w ${PWD}`;;
    *)          REPOPATH=${PWD}
esac
REPOPATH="${REPOPATH}/maven-repository"

mkdir -p "${REPOPATH}"



download() {
    local file=$1
    local url=$2
    echo "-------------------------------------------------------------------------------------"
    echo "   downloading from $url to target/tmp/download/$file"
    echo "-------------------------------------------------------------------------------------"
    curl --follow \
        -o target/tmp/download/$file \
        $url
        
    if [[ $file =~ \.jar$ ]]; then
        extension="${file##*.}"
        filename="${file%.*}"
        rm -vrf "target/tmp/download/${filename}_META-INF"
        mkdir -v -p "target/tmp/download/${filename}_META-INF"
        unzip -j -d "target/tmp/download/${filename}_META-INF" target/tmp/download/$file "META-INF/maven/proforma/*"
        mv -fv "target/tmp/download/${filename}_META-INF/pom.xml" "target/tmp/download/${filename}.pom" 
        rm -vrf "target/tmp/download/${filename}_META-INF"
    fi
}

deploy() {
    local file=$1
    echo "-------------------------------------------------------------------------------------"
    echo "  mvn deploy $file to file:///${REPOPATH}"
    echo "-------------------------------------------------------------------------------------"

    extension="${file##*.}"
    filename="${file%.*}"

    mvn deploy:deploy-file \
      -Dfile="target/tmp/download/$file" \
      -DpomFile="target/tmp/download/$filename.pom" \
      -Durl="file:///${REPOPATH}"
}




for i in "${!arrayDownloads[@]}"
do
    download "$i" "${arrayDownloads[$i]}"
    deploy "$i"
done
