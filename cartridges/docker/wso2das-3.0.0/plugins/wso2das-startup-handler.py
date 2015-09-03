# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

import mdsclient
from plugins.contracts import ICartridgeAgentPlugin
from xml.dom.minidom import parse
import socket
from modules.util.log import LogFactory
import time
import subprocess
import os


class WSO2DASStartupHandler(ICartridgeAgentPlugin):
    log = LogFactory().get_log(__name__)


    def run_plugin(self, values):

        # start server
        log.info("Starting WSO2 DAS...")
        profile = os.environ['CONFIG_PARAM_PROFILE']
        log.info("Profile : %s " % profile)
        start_command = None
        if profile:
            if profile == "receiver":
                start_command = "exec ${CARBON_HOME}/bin/wso2server.sh start -DdisableAnalyticsExecution=true -DdisableAnalyticsEngine=true"
            elif profile == "analytics":
                start_command = "exec ${CARBON_HOME}/bin/wso2server.sh start -DdisableEventSink=true"
            elif profile == "dashboard":
                start_command = "exec ${CARBON_HOME}/bin/wso2server.sh start -DdisableEventSink=true -DdisableAnalyticsExecution=true -DdisableAnalyticsEngine=true"
            elif profile == "default":
                start_command = "exec ${CARBON_HOME}/bin/wso2server.sh start"
            else:
                log.info("Invalid profile :" + profile)
        log.info("Start command : %s" % start_command)
        env_var = os.environ.copy()
        p = subprocess.Popen(start_command, env=env_var, shell=True)
        output, errors = p.communicate()
        log.debug("WSO2 DAS started successfully")


    def create_database(self, databasename, username, password):

        mds_response = mdsclient.get(app=True)
        if mds_response is not None and mds_response.properties.get("MYSQL_HOST") is not None:
            remote_host = str(mds_response.properties["MYSQL_HOST"])
            remote_username = str(mds_response.properties["MYSQL_ROOT_USERNAME"])
            remote_password = str(mds_response.properties["MYSQL_ROOT_PASSWORD"])
            log.info("mysql server conf [host]:%s [username]:%s [password]:%s", remote_host,
                     remote_username, remote_password)
            con = None
            try:
                con = db.connect(host=remote_host, user=remote_username, passwd=remote_password)
                cur = con.cursor()
                cur.execute('CREATE DATABASE IF NOT EXISTS ' + databasename + ';')
                cur.execute('USE ' + databasename + ';')
                cur.execute(
                    'GRANT ALL PRIVILEGES ON ' + databasename + '.* TO ' + username + '@"%" IDENTIFIED BY "' + password + '";')
                log.info("Database %s created successfully" % databasename)
            except db.Error, e:
                log.error("Error in creating database %d: %s" % (e.args[0], e.args[1]))

            finally:
                if con:
                    con.close()
        else:
            log.error('mysql details not published to metadata service')

    def map_hbase_hostname(self):
        log = LogFactory().get_log(__name__)
        mds_response = mdsclient.get(app=True)
        if mds_response is not None and mds_response.properties.get("CONFIG_PARAM_HBASE_REGIONSERVER_DATA") is not None:
            hbase_rs_hostmap=mds_response.properties["CONFIG_PARAM_HBASE_REGIONSERVER_DATA"]
            log.info("Hbase RS hostnames : %s" %hbase_rs_hostmap)
            if isinstance(hbase_rs_hostmap, (str, unicode)):
                hbase_list = hbase_rs_hostmap.split(":")
                config_command = "echo "+hbase_list[1]+"    "+hbase_list[0]+"  >> /etc/hosts"
                log.info("Config command %s" % config_command)
                env_var = os.environ.copy()
                p = subprocess.Popen(config_command, env=env_var, shell=True)
                output, errors = p.communicate()
                log.info("Entry added to /etc/hosts")
            else:
                for entry in hbase_rs_hostmap:
                    hbase_list = entry.split(":")
                    config_command = "echo "+hbase_list[1]+"    "+hbase_list[0]+"  >> /etc/hosts"
                    log.info("Config command %s" % config_command)
                    env_var = os.environ.copy()
                    p = subprocess.Popen(config_command, env=env_var, shell=True)
                    output, errors = p.communicate()

        else:
            log.error("HBASE RS data not found in metadata service")