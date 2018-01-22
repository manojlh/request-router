/*******************************************************************************
 * Copyright  (c) 2017-2018, WSO2.Telco Inc. (http://www.wso2telco.com) All Rights Reserved.
 *
 * WSO2.Telco Inc. licences this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.wso2telco.gateway.requestRouter.util;

import com.wso2telco.gateway.requestRouter.exception.BusinessException;
import com.wso2telco.gateway.requestRouter.exception.ErrorType;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

public class UrlHelper {

    private UrlHelper() {}

    private static UrlHelper instance;

    public static synchronized UrlHelper getInstance() {
        if (instance == null) {
            instance = new UrlHelper();
        }
        return instance;
    }

    /**
     *
     * @param url
     * @param param
     * @return
     * @throws BusinessException
     * @throws UnsupportedEncodingException
     */
    public String splitQuery(URL url, String param) throws BusinessException, UnsupportedEncodingException {
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if( URLDecoder.decode(pair.substring(0, idx), "UTF-8").equalsIgnoreCase(param)){
                return URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
            }
        }
        throw new BusinessException(ErrorType.INVALID_URL_PARAMETER);
    }

}
