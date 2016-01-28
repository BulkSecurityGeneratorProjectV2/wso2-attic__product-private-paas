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
package org.wso2.ppaas.tools.artifactmigration.loader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;
import org.apache.stratos.manager.dto.Cartridge;
import org.apache.stratos.rest.endpoint.bean.CartridgeInfoBean;
import org.apache.stratos.rest.endpoint.bean.autoscaler.partition.Partition;
import org.apache.stratos.rest.endpoint.bean.autoscaler.policy.autoscale.AutoscalePolicy;
import org.apache.stratos.rest.endpoint.bean.autoscaler.policy.deployment.DeploymentPolicy;
import org.apache.stratos.rest.endpoint.bean.cartridge.definition.ServiceDefinitionBean;
import org.apache.stratos.rest.endpoint.bean.subscription.domain.SubscriptionDomainBean;
import org.wso2.ppaas.tools.artifactmigration.Constants;
import org.wso2.ppaas.tools.artifactmigration.RestClient;
import org.wso2.ppaas.tools.artifactmigration.exception.ArtifactLoadingException;

import java.io.File;
import java.util.List;

/**
 * Fetches the JSON files from PPaaS 4.0.0
 */
public class ArtifactLoader400 {

    private static final Logger log = Logger.getLogger(ArtifactLoader400.class);
    private static final Gson gson = new Gson();

    /**
     * Method to fetch Partition Lists from PPaaS 4.0.0. API endpoint
     *
     * @return Partition List
     * @throws ArtifactLoadingException
     */
    public static List<Partition> fetchPartitionList() throws ArtifactLoadingException {
        String partitionString = readUrl(System.getProperty(Constants.BASE_URL400) + Constants.URL_PARTITION);
        String partitionListString;
        if (partitionString != null) {
            partitionListString = partitionString
                    .substring(partitionString.indexOf('['), (partitionString.lastIndexOf(']') + 1));
        } else {
            String msg = "Error while fetching network partition list";
            log.error(msg);
            throw new ArtifactLoadingException(msg);
        }
        return gson.fromJson(partitionListString, new TypeToken<List<Partition>>() {
        }.getType());
    }

    /**
     * Method to fetch Auto Scale Policy from PPaaS 4.0.0. API endpoint
     *
     * @return Auto Scale Policy List
     * @throws ArtifactLoadingException
     */
    public static List<AutoscalePolicy> fetchAutoscalePolicyList() throws ArtifactLoadingException {
        String autoscalePolicyString = readUrl(
                System.getProperty(Constants.BASE_URL400) + Constants.URL_POLICY_AUTOSCALE);
        String autoscalePolicyListString;
        if (autoscalePolicyString != null) {
            autoscalePolicyListString = autoscalePolicyString
                    .substring(autoscalePolicyString.indexOf('['), (autoscalePolicyString.lastIndexOf(']') + 1));
        } else {
            String msg = "Error while fetching autoscaling policies";
            log.error(msg);
            throw new ArtifactLoadingException(msg);
        }
        return gson.fromJson(autoscalePolicyListString, new TypeToken<List<AutoscalePolicy>>() {
        }.getType());
    }

    /**
     * Method to fetch Deployment Policy from PPaaS 4.0.0. API endpoint
     *
     * @return Deployment Policy List
     * @throws ArtifactLoadingException
     */
    public static List<DeploymentPolicy> fetchDeploymentPolicyList() throws ArtifactLoadingException {
        String deploymentPolicyString = readUrl(
                System.getProperty(Constants.BASE_URL400) + Constants.URL_POLICY_DEPLOYMENT);
        String deploymentPolicyListString;
        if (deploymentPolicyString != null) {
            deploymentPolicyListString = deploymentPolicyString
                    .substring(deploymentPolicyString.indexOf('['), (deploymentPolicyString.lastIndexOf(']') + 1));
        } else {
            String msg = "Error while fetching deployment policies";
            log.error(msg);
            throw new ArtifactLoadingException(msg);
        }
        return gson.fromJson(deploymentPolicyListString, new TypeToken<List<DeploymentPolicy>>() {
        }.getType());
    }

    /**
     * Method to fetch Cartridges from PPaaS 4.0.0. API endpoint
     *
     * @return Cartridges List
     * @throws ArtifactLoadingException
     */
    public static List<Cartridge> fetchCartridgeList() throws ArtifactLoadingException {
        String cartridgeString = readUrl(System.getProperty(Constants.BASE_URL400) + Constants.URL_CARTRIDGE);
        String cartridgeListString;
        if (cartridgeString != null) {
            cartridgeListString = cartridgeString
                    .substring(cartridgeString.indexOf('['), (cartridgeString.lastIndexOf(']') + 1));
            //Updating port, protocol and proxy port names to be compatible with the bean classes
            cartridgeListString = cartridgeListString.replaceAll("port", "localPort");
            cartridgeListString = cartridgeListString.replaceAll("protocol", "localProtocol");
            cartridgeListString = cartridgeListString.replaceAll("proxyPort", "localProxyPort");
            cartridgeListString = cartridgeListString.replaceAll("localPortMappings", "portMappings");
        } else {
            String msg = "Error while fetching cartridge lists";
            log.error(msg);
            throw new ArtifactLoadingException(msg);
        }
        return gson.fromJson(cartridgeListString, new TypeToken<List<Cartridge>>() {
        }.getType());
    }

    /**
     * Method to fetch Multi Tenant Cartridges from PPaaS 4.0.0 API endpoint
     *
     * @return multi tenant cartridges
     * @throws ArtifactLoadingException
     */
    public static List<Cartridge> fetchMultiTenantCartridgeList() throws ArtifactLoadingException {

        String multiTenantCartridgeString = readUrl(
                System.getProperty(Constants.BASE_URL400) + Constants.URL_MULTI_TENANT_CARTRIDGE);
        String cartridgeListString;
        if (multiTenantCartridgeString != null) {
            cartridgeListString = multiTenantCartridgeString.substring(multiTenantCartridgeString.indexOf('['),
                    (multiTenantCartridgeString.lastIndexOf(']') + 1));
        } else {
            String msg = "Error while fetching cartridge lists";
            log.error(msg);
            throw new ArtifactLoadingException(msg);
        }
        return gson.fromJson(cartridgeListString, new TypeToken<List<Cartridge>>() {
        }.getType());

    }

    /**
     * Method to fetch Services from PPaaS 4.0.0. API endpoint
     *
     * @return Services List
     * @throws ArtifactLoadingException
     */
    public static List<ServiceDefinitionBean> fetchMultiTenantServiceList() throws ArtifactLoadingException {

        String serviceString = readUrl(System.getProperty(Constants.BASE_URL400) + Constants.URL_MULTI_TENANT_SERVICE);
        String serviceListString;
        if (serviceString != null) {
            serviceListString = serviceString
                    .substring(serviceString.indexOf('['), (serviceString.lastIndexOf(']') + 1));
        } else {
            String msg = "Error while fetching cartridge lists";
            log.error(msg);
            throw new ArtifactLoadingException(msg);
        }
        return gson.fromJson(serviceListString, new TypeToken<List<ServiceDefinitionBean>>() {
        }.getType());

    }

    /**
     * Method to fetch Cartridges from PPaaS 4.0.0. API endpoint
     *
     * @return Cartridges List
     * @throws ArtifactLoadingException
     */
    public static List<CartridgeInfoBean> fetchSubscriptionDataList() throws ArtifactLoadingException {
        String cartridgeString = readUrl(System.getProperty(Constants.BASE_URL400) + Constants.URL_SUBSCRIPTION);
        String cartridgeListString;
        if (cartridgeString != null) {
            cartridgeListString = cartridgeString
                    .substring(cartridgeString.indexOf('['), (cartridgeString.lastIndexOf(']') + 1));
        } else {
            String msg = "Error while fetching subscription data list";
            log.error(msg);
            throw new ArtifactLoadingException(msg);
        }
        return gson.fromJson(cartridgeListString, new TypeToken<List<CartridgeInfoBean>>() {
        }.getType());
    }

    /**
     * Method to fetch domain mapping list from PPaaS 4.0.0. API endpoint
     *
     * @param cartridgeType     cartridge type
     * @param subscriptionAlias subscription alias
     * @return domain mapping
     */
    public static List<SubscriptionDomainBean> fetchDomainMappingList(String cartridgeType, String subscriptionAlias) {
        String domainString = readUrl(
                System.getProperty(Constants.BASE_URL400) + Constants.STRATOS_API_PATH + "cartridge" + File.separator
                        + cartridgeType + File.separator + "subscription" + File.separator + subscriptionAlias
                        + File.separator + "domains");
        String domainListString;
        if ((domainString != null) && (!domainString.isEmpty())) {
            domainListString = domainString.substring(domainString.indexOf('['), (domainString.lastIndexOf(']') + 1));
            return gson.fromJson(domainListString, new TypeToken<List<SubscriptionDomainBean>>() {
            }.getType());

        } else {
            log.info("Domain mappings for " + cartridgeType + " are not available");
            return null;
        }
    }

    /**
     * Method to connect to the REST endpoint with authorization
     *
     * @param serviceEndpoint the endpoint to connect with
     * @return JSON string
     */
    private static synchronized String readUrl(String serviceEndpoint) {
        RestClient restclient = new RestClient(System.getProperty(Constants.USERNAME400),
                System.getProperty(Constants.PASSWORD400));
        return restclient.doGet(serviceEndpoint);
    }
}