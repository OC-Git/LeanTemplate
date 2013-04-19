#!/bin/bash

SCRIPT=$(dirname $0)
source $SCRIPT/config.sh

for module in $MODULES; do
	echo "+++++++++++++++++++++++ BUILDING MODULE $module +++++++++++++++++++++++"
	pushd "$SCRIPT/../modules/${module}"
	play clean compile publish-local
	if [ $? -ne 0 ]; then
    	echo "###################################################################"
    	echo "COMPILATION ERRORS IN ${module} !!!!"
    	echo "###################################################################"
    	exit 1
	fi
	mkdir target/scala-2.10/classes_managed
        mkdir target/scala-2.10/src_managed
        mkdir target/scala-2.10/src_managed/main
	popd
done



