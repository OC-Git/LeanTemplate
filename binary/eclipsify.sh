#!/bin/bash

SCRIPT=$(dirname $0)
MODULES="monitoring commons search crud crud-view abtest"

EclipsifyCurrentProject()
{
	cp project/Build.scala project/Build.scala.bak
	sed -i 's/JAVA/SCALA/g' project/Build.scala
	play "eclipsify with-source=true"
	rm project/Build.scala
	mv project/Build.scala.bak project/Build.scala
	mkdir target/scala-2.9.1/classes_managed
        mkdir target/scala-2.9.1/src_managed
        mkdir target/scala-2.9.1/src_managed/main
}

for module in $MODULES; do
	echo "+++++++++++++++++++++++ ECLIPSIFY MODULE $module +++++++++++++++++++++++"
	pushd "$SCRIPT/../modules/${module}"
	EclipsifyCurrentProject
	popd
done

EclipsifyCurrentProject



