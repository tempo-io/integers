#!/bin/bash

PASSPHRASE="$2"
VERSION="$1"

if [ -z "$VERSION" -o -z "$PASSPHRASE" ]; then echo "usage: $0 <version> <passphrase>"; exit 1; fi

echo "Uploading version $VERSION. Press Enter to continue."
read something

PARAMS="\
 -Dgpg.homedir=$HOME/.m2/gpg \
 -Dgpg.passphrase=$PASSPHRASE \
 -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/ \
 -DrepositoryId=sonatype-nexus-staging \
 -DgroupId=com.almworks.integers \
 -Dversion=$VERSION "
 
m="mvn gpg:sign-and-deploy-file $PARAMS"
integers="$m -DartifactId=integers -DpomFile=integers.pom"
wrappers="$m -DartifactId=integers-wrappers -DpomFile=integers-wrappers.pom"

$integers -Dfile=integers.jar &&
$integers -Dclassifier=sources -Dfile=integers-sources.jar &&
$integers -Dclassifier=javadoc -Dfile=integers-javadoc.jar &&
$wrappers -Dfile=integers-wrappers.jar &&
$wrappers -Dclassifier=sources -Dfile=integers-wrappers-sources.jar &&
$wrappers -Dclassifier=javadoc -Dfile=integers-wrappers-javadoc.jar

