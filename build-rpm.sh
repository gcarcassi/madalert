#!/bin/bash
PKG_NAME=`ls target/dist/`
mkdir target/rpm
cp target/dist/$PKG_NAME/dist/$PKG_NAME.tar.gz target/rpm
cd target/rpm
tar xzvf $PKG_NAME.tar.gz
cd $PKG_NAME
python setup.py bdist --formats=rpm
ls dist


