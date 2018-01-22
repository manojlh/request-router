package com.wso2telco.gateway.requestRouter;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import com.wso2telco.gateway.requestRouter.model.HeaderModel;
import com.wso2telco.gateway.requestRouter.model.HeaderModes;
import com.wso2telco.gateway.requestRouter.model.ReplaceBodyModel;
import com.wso2telco.gateway.requestRouter.util.UrlHelper;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by WSO2telco
 */

@Controller
@RequestMapping("/route")
public class RequestController {

    public static Properties prop = null;

    public static String callback = null;
    public static ArrayList<String> jsonPath = null;
    public static List<String> headerToSkip = null;
    private static Logger log = null;
    public static TrustManager[] trustAllCerts;
    private static int timeout = 5000;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public String handleCommonRequest(@RequestBody String body, @RequestParam("org") String url, HttpServletRequest request, HttpServletResponse response) {
        return handleRequest(body, url, request, response, "COMMON");
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String handleCommonRequest2(@RequestBody String body, @RequestParam("org") String url, HttpServletRequest request, HttpServletResponse response) {
        return handleRequest(body, url, request, response, "COMMON");
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.POST)
    @ResponseBody
    public String handleRequest(@RequestBody String body, @RequestParam("org") String url, HttpServletRequest request, HttpServletResponse response, @PathVariable String key) {
        log.info("Request : " + url + "|" + key + "|");

        //replacing with unicode letters
        try {
            body = body.replace('\u003d' + "", "=");
            body = body.replace("\\u003d" + "", "=");
        } catch (Exception e) {

        }


        for (String cPath : jsonPath) {
            try {
                String c = JsonPath.read(body, cPath);
                if (!c.equalsIgnoreCase(cPath))
                    body = body.replace(c, StringEscapeUtils.escapeJavaScript(resetInpath(c + "", callback)));
            } catch (InvalidPathException e) {

            } catch (Exception e) {
                log.info("Convert Exception :  " + cPath + "|" + e.getMessage());
            }
        }

        ArrayList<ReplaceBodyModel> replaceBodyModels = DatabaseLibrary.getReplaceBodyList(key);
        for (ReplaceBodyModel current : replaceBodyModels) {
            try {

                String currentPath = JsonPath.read(body, current.getJsonPath());
                String originalPath = currentPath;
                if (current.getReplace() == null)
                    current.setReplace("");

                if (current.getFind() != null && current.getFind().equals("*"))
                    currentPath = current.getReplace();
                else if (!current.getFind().equals("") && current.getFind() != null)
                    currentPath = currentPath.replace(current.getFind(), current.getReplace());

                if (current.getNeedURLDecodeRest()) {
                    currentPath = java.net.URLDecoder.decode(currentPath, "UTF-8");
                }

                body = body.replace(originalPath, StringEscapeUtils.escapeJavaScript(currentPath));
                //body = body.replace(originalPath, StringEscapeUtils.escapeJavaScript(resetInpath(currentPath, callback)));

            } catch (InvalidPathException e) {

            } catch (Exception e) {
                log.info("Convert Exception :  " + current.getJsonPath() + "|" + e.getMessage());
            }
        }

        Map<String, String> headers = new HashMap<String, String>();
        String requestType = "";
        try {
            Enumeration headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = (String) headerNames.nextElement();
                headers.put("" + headerName, "" + request.getHeader(headerName));
                log.debug("Header Name : " + headerName + " | Header Value : " + request.getHeader(headerName));
            }
            requestType = request.getHeader("Content-type");

        } catch (Exception e) {

        }

        //some API call contain GET parameters in JSON body
        try {
            //remove POST from body
            if (requestType.contains("json")) {
                Pattern MY_PATTERN = Pattern.compile("\\}&");
                Matcher m = MY_PATTERN.matcher(body);
                while (m.find()) {
//                    String s = m.group(0);
                    if (body.substring(0, m.start() + 1).length() > 2) {
                        body = body.substring(0, m.start() + 1);
                        break;
                    }
                }

            }
        } catch (Exception e) {

        }


        try {
            ArrayList<HeaderModel> addingHeaders = DatabaseLibrary.getHeaders(getDomainName(url));
            headers = processHeaders(addingHeaders, headers, url);

        } catch (Exception e) {
            log.info("Header Ex : " + e.getMessage(), e);

        }
        try {
            PostResponse result = sendPost(url, body, headers);
            setHeaders(response, result.getHeaders());
            return result.getResponse();
        } catch (Exception e) {
            log.info("ReRoute Ex : " + e.getMessage(), e);

            e.printStackTrace();
            return "{}";
        }
    }

    public RequestController() {
        log = Logger.getLogger(RequestController.class);

        /*
        setting SSL certificate validation skip
         */
        try {
            trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {

        }

        /*
        reading Properties
         */
        prop = new Properties();
        InputStream input = null;

        try {

            input = Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties");

            prop.load(input);

            callback = prop.getProperty("conf.callBack");
            jsonPath = new ArrayList<String>();
            jsonPath.addAll(Arrays.asList(prop.getProperty("conf.path").toString().split("\\s*,\\s*")));

            try {
                timeout = Integer.parseInt(prop.getProperty("conf.path").toString());
            } catch (Exception ei) {

            }
            try {
                String str = (prop.getProperty("conf.headersToSkip").toString());
                headerToSkip = new ArrayList<String>();
                log.info("Headers to skip :" + str);
                String[] s = str.split(",");

                for (String c : s) {
                    if (c != null && c.length() > 1) {
                        log.info("Adding Skip Header : " + c);
                        headerToSkip.add(c);
                    }
                }

            } catch (Exception ei) {
                log.error("Loading header error : " + ei.getMessage());
            }


            if (callback.contains("$_IP")) {
                try {
                    callback = callback.replace("$_IP", InetAddress.getLocalHost().getHostAddress());
                } catch (Exception s) {
                    log.error(" IP ERR : " + s.getMessage());
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String resetInpath(String original, String callbackURL) {


        try {
            original = original.replace(callbackURL, "");

            URL url = new URL(original);
            original = UrlHelper.getInstance().splitQuery(url, "org");

            original = URLDecoder.decode(original);

        } catch (Exception e) {
            log.info("Error : " + e.getMessage() + "|" + original);

        }
        return original;
    }


    private PostResponse sendPost(String url, String body, Map<String, String> headers) throws Exception {
        log.info("Sending 'POST' request to URL : " + url);
        log.info("Post parameters : " + body);

        URL obj = new URL(url);

        HttpURLConnection con = null;

        if (url.startsWith("https")) {
            con = (HttpsURLConnection) obj.openConnection();
        } else {
            con = (HttpURLConnection) obj.openConnection();
        }
        con.setConnectTimeout(timeout);
        con.setReadTimeout(timeout);
        con.setRequestMethod("POST");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");


        try {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                con.setRequestProperty(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {

        }
//        if (contentType != null)
//            con.setRequestProperty("Content-type", contentType);

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(body);
        wr.flush();
        wr.close();


        log.info("Response Code : " + con.getResponseCode());

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        PostResponse r = new PostResponse();


        try {
            Map<String, List<String>> map = con.getHeaderFields();
            r.setHeaders(map);
        } catch (Exception e) {

        }
        //print result
        log.info("Response : " + response.toString());

        r.setResponse(response.toString());

        return r;
//        return response.toString();

    }

    public void setHeaders(HttpServletResponse response, Map<String, List<String>> headers) {
        try {
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                if (entry != null && entry.getValue() != null && entry.getKey() != null && entry.getValue().size() > 0) {
                    if (!isSkip(entry.getKey())) {
                        response.setHeader(entry.getKey(), entry.getValue().get(0));
                    }

                }
            }
        } catch (Exception e) {
            log.info("Header error : " + e.getMessage());
        }

    }


    //checking hgeaders to skip
    public boolean isSkip(String header) {
        boolean match = false;
        try {
            for (String c : headerToSkip) {
                if (c.equalsIgnoreCase(header)) {
                    match = true;
                    log.info("Found Skip header : " + header);

                    break;
                }
            }
        } catch (Exception e) {
            log.error("Skip header error : " + e.getMessage());
        }


        return match;
    }

    public static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    public Map<String, String> processHeaders(ArrayList<HeaderModel> list, Map<String, String> headers, String url) {

        try {
            for (HeaderModel header : list) {
                if (!url.toLowerCase().startsWith(header.getUrlPrefix().toLowerCase())) {
                    continue;
                }
                String exist = headers.get(header.getHeader());

                if (header.getMode() == HeaderModes.ADD)
                    headers.put(header.getHeader(), header.getHeaderValue());
                if (header.getMode() == HeaderModes.REPLACE) {
                    if (exist != null)
                        headers.remove(header.getHeader());
                    headers.put(header.getHeader(), header.getHeaderValue());
                }
                if (header.getMode() == HeaderModes.APPEND) {
                    if (exist != null)
                        exist = exist + ";";
                    else
                        exist = "";
                    headers.put(header.getHeader(), exist + header.getHeaderValue());
                }
            }
        } catch (Exception e) {

        }
        return headers;
    }
}