/*
 * Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.ppaas.tools.artifactmigration;

import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

public class RestClient {
    /**
     * Override the default host name verifier to allow any certificate. (Constants.ENABLE_SELF_CERTIFIED have
     * to be disabled when in normal use.)
     */
    static {
        if (Boolean.parseBoolean(System.getProperty(Constants.ENABLE_SELF_CERTIFIED))) {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return hostname.equals(System.getProperty(Constants.HOSTNAME));
                }
            });
        }
    }

    final private Client client;

    /**
     * Constructor to verify the certificate and connect to the rest endpoint
     */
    public RestClient(String username, String password) {
        SslConfigurator sslConfig = SslConfigurator.newInstance().trustStoreFile(Constants.CERTIFICATE_PATH)
                .trustStorePassword(Constants.CERTIFICATE_PASSWORD).keyStoreFile(Constants.CERTIFICATE_PATH)
                .keyPassword(Constants.CERTIFICATE_PASSWORD);
        SSLContext sslContext = sslConfig.createSSLContext();
        client = ClientBuilder.newBuilder().sslContext(sslContext).build();
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(username, password);
        client.register(feature);
    }

    /**
     * Method to get the JSON file
     *
     * @param resourcePath path of the resource
     * @return JSON string
     */
    public String doGet(String resourcePath) {
        return client.target(resourcePath).request().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.WILDCARD).get().readEntity(String.class);
    }
}