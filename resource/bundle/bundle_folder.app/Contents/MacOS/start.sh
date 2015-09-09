#!/bin/bash

ABSOLUTE_PATH=$(cd `dirname "${BASH_SOURCE[0]}"` && pwd)/`basename "${BASH_SOURCE[0]}"`
SCRIPTPATH=`dirname $ABSOLUTE_PATH`

java -XstartOnFirstThread -jar $SCRIPTPATH/../Resources/app.jar