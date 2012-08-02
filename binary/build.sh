#!/bin/bash

SCRIPT=$(dirname $0)
source $SCRIPT/config.sh

for module in $MODULES; do
	echo "+++++++++++++++++++++++ BUILDING MODULE $module +++++++++++++++++++++++"
	pushd "$SCRIPT/../modules/${module}"
	play clean compile publish-local
	popd
done



