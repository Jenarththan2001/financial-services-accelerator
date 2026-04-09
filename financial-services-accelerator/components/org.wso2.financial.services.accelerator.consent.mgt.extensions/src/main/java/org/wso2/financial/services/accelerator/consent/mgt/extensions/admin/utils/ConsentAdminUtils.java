/**
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com).
 * <p>
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *     http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.financial.services.accelerator.consent.mgt.extensions.admin.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.financial.services.accelerator.consent.mgt.dao.models.AuthorizationResource;
import org.wso2.financial.services.accelerator.consent.mgt.dao.models.ConsentMappingResource;
import org.wso2.financial.services.accelerator.consent.mgt.dao.models.DetailedConsentResource;
import org.wso2.financial.services.accelerator.consent.mgt.extensions.common.ConsentException;
import org.wso2.financial.services.accelerator.consent.mgt.extensions.common.ConsentExtensionConstants;
import org.wso2.financial.services.accelerator.consent.mgt.extensions.common.ResponseStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Consent admin utils.
 */
public class ConsentAdminUtils {

    /**
     * Validate and retrieve query param.
     * 1. Check whether the key exists as a query param.
     * 2. Validate whether the value is a string.
     * 3. Retrieve query param.
     *
     * @param queryParams  query params
     * @param key          key to be retrieved
     * @return   query param value
     */
    public static String validateAndGetQueryParam(Map queryParams, String key) {
        if (queryParams.containsKey(key) && (((ArrayList<?>) queryParams.get(key)).get(0) instanceof String)) {
            return (String) ((ArrayList<?>) queryParams.get(key)).get(0);
        }
        return null;
    }

    /**
     * Get array list from query param.
     * @param queryParam    query param values
     * @return  array list constructed from the query param value
     */
    public static ArrayList<String> getArrayListFromQueryParam(String queryParam) {
        return queryParam != null ? new ArrayList<>(Arrays.asList(queryParam.split(","))) : null;
    }

    /**
     * Get long values from query param.
     * @param queryParam    query param values
     * @return  long value constructed from the query param value
     */
    public static long getLongFromQueryParam(String queryParam) {
        return queryParam != null ? Long.parseLong(queryParam) : 0;
    }

    /**
     * Get int values from query param.
     * @param queryParam    query param values
     * @return  int value constructed from the query param value
     */
    public static int getIntFromQueryParam(String queryParam) {
        return queryParam != null ? Integer.parseInt(queryParam) : 0;
    }

    /**
     * Convert detailed consent resource to JSON.
     * @param detailedConsentResource   detailed consent resource
     * @return  JSON object constructed from the detailed consent resource
     */
    public static JSONObject detailedConsentToJSON(DetailedConsentResource detailedConsentResource) {
        JSONObject consentResource = new JSONObject();

        consentResource.put(ConsentExtensionConstants.CC_CONSENT_ID,
                detailedConsentResource.getConsentID());
        consentResource.put(ConsentExtensionConstants.CLIENT_ID, detailedConsentResource.getClientID());
        try {
            consentResource.put(ConsentExtensionConstants.RECEIPT,
                    new JSONObject(detailedConsentResource.getReceipt()));
        } catch (JSONException e) {
            throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR, "Exception occurred while parsing" +
                    " receipt");
        }
        consentResource.put(ConsentExtensionConstants.CONSENT_TYPE, detailedConsentResource.getConsentType());
        consentResource.put(ConsentExtensionConstants.CURRENT_STATUS,
                detailedConsentResource.getCurrentStatus());
        consentResource.put(ConsentExtensionConstants.CONSENT_FREQUENCY,
                detailedConsentResource.getConsentFrequency());
        consentResource.put(ConsentExtensionConstants.VALIDITY_PERIOD,
                detailedConsentResource.getValidityPeriod());
        consentResource.put(ConsentExtensionConstants.CREATED_TIMESTAMP,
                detailedConsentResource.getCreatedTime());
        consentResource.put(ConsentExtensionConstants.UPDATED_TIMESTAMP,
                detailedConsentResource.getUpdatedTime());
        consentResource.put(ConsentExtensionConstants.RECURRING_INDICATOR,
                detailedConsentResource.isRecurringIndicator());
        JSONObject attributes = new JSONObject();
        Map<String, String> attMap = detailedConsentResource.getConsentAttributes();
        for (Map.Entry<String, String> entry : attMap.entrySet()) {
            attributes.put(entry.getKey(), entry.getValue());
        }
        consentResource.put(ConsentExtensionConstants.CONSENT_ATTRIBUTES, attributes);
        JSONArray authorizationResources = new JSONArray();
        ArrayList<AuthorizationResource> authArray = detailedConsentResource.getAuthorizationResources();
        for (AuthorizationResource resource : authArray) {
            JSONObject resourceJSON = new JSONObject();
            resourceJSON.put(ConsentExtensionConstants.AUTH_ID, resource.getAuthorizationID());
            resourceJSON.put(ConsentExtensionConstants.CC_CONSENT_ID, resource.getConsentID());
            resourceJSON.put(ConsentExtensionConstants.USER_ID, resource.getUserID());
            resourceJSON.put(ConsentExtensionConstants.AUTH_STATUS, resource.getAuthorizationStatus());
            resourceJSON.put(ConsentExtensionConstants.AUTH_TYPE, resource.getAuthorizationType());
            resourceJSON.put(ConsentExtensionConstants.UPDATE_TIME, resource.getUpdatedTime());
            authorizationResources.put(resourceJSON);
        }
        consentResource.put(ConsentExtensionConstants.AUTH_RESOURCES, authorizationResources);
        JSONArray consentMappingResources = new JSONArray();
        ArrayList<ConsentMappingResource> mappingArray = detailedConsentResource.getConsentMappingResources();
        for (ConsentMappingResource resource : mappingArray) {
            JSONObject resourceJSON = new JSONObject();
            resourceJSON.put(ConsentExtensionConstants.MAPPING_ID, resource.getMappingID());
            resourceJSON.put(ConsentExtensionConstants.AUTH_ID, resource.getAuthorizationID());
            resourceJSON.put(ConsentExtensionConstants.ACCOUNT_ID_CC, resource.getAccountID());
            resourceJSON.put(ConsentExtensionConstants.PERMISSION, resource.getPermission());
            resourceJSON.put(ConsentExtensionConstants.MAPPING_STATUS, resource.getMappingStatus());
            consentMappingResources.put(resourceJSON);
        }
        consentResource.put(ConsentExtensionConstants.MAPPING_RESOURCES, consentMappingResources);
        return consentResource;
    }

    /**
     * Construct consent attribute response from consent IDs, attribute key and attribute value.
     * This method will be used to construct the response for consent attributes endpoint when there are multiple
     * consents with the same attribute key and same attribute value.
     *
     * @param consentIds        list of consent IDs which have the same attribute key and attribute value
     * @param attributeKey      attribute key
     * @param attributeValue    attribute value
     * @return JSONArray consent attribute response array
     */
    public static JSONArray constructConsentAttributeResponse(ArrayList<String> consentIds, String attributeKey,
                                                              String attributeValue) {
        JSONArray consentAttribute = new JSONArray();
        for (String consentId : consentIds) {
            JSONObject consentAttributeObj = new JSONObject();
            consentAttributeObj.put(ConsentExtensionConstants.CC_CONSENT_ID, consentId);
            consentAttributeObj.put(ConsentExtensionConstants.ATTRIBUTE_KEY, attributeKey);
            consentAttributeObj.put(ConsentExtensionConstants.ATTRIBUTE_VALUE, attributeValue);
            consentAttribute.put(consentAttributeObj);
        }
        return consentAttribute;
    }

    /**
     * Construct consent attribute response from consent details map retrieved fot attribute key. Consent details map
     * contains consent ID as key and attribute value as value. This method will be used to construct the response
     * for consent attributes endpoint when there are multiple consents with the same attribute key and different
     * attribute values.
     *
     * @param consentDetailsMap consent details map
     * @param attributeName attribute name for which the consent details are retrieved
     * @return JSONArray consent attribute response array
     */
    public static JSONArray constructConsentAttributeResponse(Map<String, String> consentDetailsMap,
                                                              String attributeName) {
        JSONArray consentAttribute = new JSONArray();
        for (Map.Entry<String, String> entry : consentDetailsMap.entrySet()) {
            JSONObject consentAttributeObj = new JSONObject();
            consentAttributeObj.put(ConsentExtensionConstants.CC_CONSENT_ID, entry.getKey());
            consentAttributeObj.put(ConsentExtensionConstants.ATTRIBUTE_KEY, attributeName);
            consentAttributeObj.put(ConsentExtensionConstants.ATTRIBUTE_VALUE, entry.getValue());
            consentAttribute.put(consentAttributeObj);
        }
        return consentAttribute;
    }
}
