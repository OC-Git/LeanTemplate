#!/bin/bash

SCRIPT=$(dirname $0)
source $SCRIPT/config.sh

for module in $MODULES; do
	echo "+++++++++++++++++++++++ BUILDING MODULE $module +++++++++++++++++++++++"
	pushd "$SCRIPT/../modules/${module}"
	play clean compile publish-local
	mkdir target/scala-2.9.1/classes_managed
        mkdir target/scala-2.9.1/src_managed
        mkdir target/scala-2.9.1/src_managed/main
	popd
done



