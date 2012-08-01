#!/bin/bash

SCRIPT=$(dirname $0)
pushd "$SCRIPT/../.."
git clone git@github.com:SebastianBaltesObjectCode/sebastianbaltesobjectcode.github.com.git
cd sebastianbaltesobjectcode.github.com/
mkdir releases
cp -rv ${PLAY_HOME}/repository/local/leantemplate.* releases
git add .
git commit -m "initial"
git push origin master
popd



