#!/bin/bash
# --------------------------------------------------------------
#
#  Copyright 2005-2015 WSO2, Inc. (http://wso2.com)
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#  http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#
# --------------------------------------------------------------

set -e
prgdir=`dirname "$0"`
script_path=`cd "$prgdir"; pwd`

project_version="4.1.0-SNAPSHOT"
das_template_module_path=`cd ${script_path}/../../../cartridges/templates-modules/wso2das-3.0.0/; pwd`
clean=false
if [ "$1" = "clean" ]; then
   clean=true
fi

if ${clean} ; then
   echo "----------------------------------"
   echo "Building DAS template module"
   echo "----------------------------------"
   pushd ${das_template_module_path}
   mvn clean install
   cp -v target/wso2das-3.0.0-template-module-${project_version}.zip ${script_path}/packages/
   popd
fi



echo "----------------------------------"
echo "Building DAS docker image"
echo "----------------------------------"
docker build -t wso2/das:3.0.0-SNAPSHOT .
echo "DAS docker image built successfully"
