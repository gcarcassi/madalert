#!/bin/bash
PKG_NAME=`ls target/dist/`
rpm -e madalert
rpm -i target/rpm/$PKG_NAME/dist/*.noarch.rpm 
