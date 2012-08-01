#!/bin/bash

SCRIPT=$(dirname $0)
MODULES="monitoring commons search crud crud-view abtest"


for module in $MODULES; do
	echo "+++++++++++++++++++++++ BUILDING MODULE $module +++++++++++++++++++++++"
	pushd "$SCRIPT/../modules/${module}"
	play clean compile publish-local
	popd
done



