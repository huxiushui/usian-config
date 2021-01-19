package common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.syxfjr.cserver.common.dto.YSDSC2RespHeaderDataDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Slf4j
public class HttpClientUtil {
    private static final HttpClient httpClient;

    private static String[] imageFields = new String[]{"image", "image_ref1", "image_ref2", "video"};

    //初始化pool及设置http超时时间
    static {
        RequestConfig config = RequestConfig.custom()
                .setSocketTimeout(1000 * 30)//数据传输处理时间
                .setConnectTimeout(1000 * 10)//建立连接的timeout时间
                .setConnectionRequestTimeout(2000)//从连接池获取链接超时时间
                .build();
        httpClient = HttpClients.custom().setDefaultRequestConfig(config).setMaxConnTotal(100).setMaxConnPerRoute(50).build();
    }

    public static HttpClientUtil getInstance() {
        return HttpClientUtilHolder.instance;
    }

    private HttpClientUtil() {
    }

    private static class HttpClientUtilHolder {
        private final static HttpClientUtil instance = new HttpClientUtil();
    }

    public String get(String url) throws IOException {
        String result;
        long start = System.currentTimeMillis();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.error("HTTP GET failed,url = {}, statusLine = {}", url, httpResponse.getStatusLine());
                return null;
            }
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity, "utf-8");
        } catch (IOException e) {
            log.error("wrong HttpClient get method ! url = {} ,exception message:{} ", url, e.getMessage());
            throw e;
        } finally {
            httpGet.reset();
        }
        log.info("execute http GET url = {}, spend time:{} milliseconds", url, System.currentTimeMillis() - start);
        return result;
    }

    public String post(String uri, List<NameValuePair> nameValuePairs) throws IOException {
        String result;
        long start = System.currentTimeMillis();
        HttpPost httpPost = new HttpPost(uri);
        StringBuffer params = new StringBuffer("");
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, Consts.UTF_8));
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (!CollectionUtils.isEmpty(nameValuePairs)) {
                for (NameValuePair nameValuePair : nameValuePairs) {
                    params.append(nameValuePair.getName()).append("=").append(nameValuePair.getValue()).append("&");
                }
            }
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.error("HTTP POST failed,uri = {}, params = {} ,statusLine = {}", uri, params.toString(), httpResponse.getStatusLine());
                return null;
            }
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity, Consts.UTF_8);
        } catch (IOException e) {
            log.error("wrong HttpClient post method ! uri = {}, params = {} ,exception message:{}", uri, params.toString(), e.getMessage());
            throw e;
        } finally {
            httpPost.reset();
        }
        log.info("execute http POST uri = {}, params= {} ,spend time:{} milliseconds", uri, params.toString(), System.currentTimeMillis() - start);
        return result;
    }



    public String postForJSon(String uri, String paramsJson) throws Exception {
        HttpPost httpPost = null;
        String result = null;
        try {
            httpPost = new HttpPost(uri);
            StringEntity stringEntity = new StringEntity(paramsJson, ContentType.APPLICATION_JSON);
            httpPost.setEntity(stringEntity);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.error("HTTP POST failed,uri: {}, params : {} ,statusLine : {}", uri, paramsJson, httpResponse.getStatusLine());
                return result;
            }
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity, Consts.UTF_8);
            log.info("http post uri: {} result:{}", uri, result);
            return result;
        } catch (Exception e) {
            log.error("HTTP POST failed,uri:{},paramsJson:{},exception message:{}", uri, paramsJson, e.getMessage());
            throw e;
        } finally {
            if (httpPost != null) {
                httpPost.reset();
            }
        }
    }

    public String postForJSon(String uri, String paramsJson, Map<String,String> headerMap) throws Exception {
        HttpPost httpPost = null;
        String result = null;
        try {
            httpPost = new HttpPost(uri);
            //添加header信息
            for (String key : headerMap.keySet()){
                httpPost.addHeader(key,headerMap.get(key));
            }
            StringEntity stringEntity = new StringEntity(paramsJson, ContentType.APPLICATION_JSON);
            httpPost.setEntity(stringEntity);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.error("HTTP POST failed,uri: {}, params : {} ,statusLine : {}", uri, paramsJson, httpResponse.getStatusLine());
                return result;
            }
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity, Consts.UTF_8);
            log.info("http post uri: {} result:{}", uri, result);
            return result;
        } catch (Exception e) {
            log.error("HTTP POST failed,uri:{},paramsJson:{},exception message:{}", uri, paramsJson, e.getMessage());
            throw e;
        } finally {
            if (httpPost != null) {
                httpPost.reset();
            }
        }
    }

    public YSDSC2RespHeaderDataDto postForJSonYSD(String uri, String paramsJson, Map<String,String> headerMap) throws Exception {
        HttpPost httpPost = null;
        String result = null;
        try {
            httpPost = new HttpPost(uri);
            //添加header信息
            for (String key : headerMap.keySet()){
                httpPost.addHeader(key,headerMap.get(key));
            }
            StringEntity stringEntity = new StringEntity(paramsJson, ContentType.APPLICATION_JSON);
            httpPost.setEntity(stringEntity);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.error("HTTP POST failed,uri: {}, params : {} ,statusLine : {}", uri, paramsJson, httpResponse.getStatusLine());
                return null;
            }
            YSDSC2RespHeaderDataDto dataDto = new YSDSC2RespHeaderDataDto();
            Header headerAuthorization = httpResponse.getFirstHeader(ConstantSY.AUTHORIZATION);
            if (headerAuthorization == null) {
                return null;
            }
            String authorization = headerAuthorization.getValue();
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity, Consts.UTF_8);
            dataDto.setAuthorization(authorization);
            dataDto.setResult(result);
            log.info("http post uri: {} result:{},authorization:{},securityKey:{}", uri, result,authorization);
            return dataDto;
        } catch (Exception e) {
            log.error("HTTP POST failed,uri:{},paramsJson:{},exception message:{}", uri, paramsJson, e.getMessage());
            throw e;
        } finally {
            if (httpPost != null) {
                httpPost.reset();
            }
        }
    }

    /**
     * 发送文件请求
     * @param uri
     * @param request
     * @return
     * @throws Exception
     */
    public String postForFile(String uri, Map<String, Object> request) throws Exception {

        HttpPost httpPost = null;
        String result = null;
        MultipartEntity multipartEntity = new MultipartEntity();
        request.forEach((key,value) -> {
            if (value instanceof StringBody) {
                StringBody body = (StringBody)value;
                log.info("postForFile调用，key为：{},字符串请求参数为：{}",key,body.toString());
                multipartEntity.addPart(key,(StringBody)value);
            }
            if (value instanceof FileBody) {
                FileBody body = (FileBody)value;
                log.info("postForFile开始调用，key为：{},文件求参数为：{}",key,body.getFile().getPath());
                multipartEntity.addPart(key,(FileBody)value);
            }
        });
        try {
            httpPost = new HttpPost(uri);
            httpPost.setEntity(multipartEntity);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                HttpEntity entity = httpResponse.getEntity();
                result = EntityUtils.toString(entity, Consts.UTF_8);
                log.info("失败 http post uri: {} result:{}", uri, result);
                return result;
            }
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity, Consts.UTF_8);
            log.info("http post uri: {} result:{}", uri, result);
            return result;
        } catch (Exception e) {
            throw e;
        } finally {
            if (httpPost != null) {
                httpPost.reset();
            }
        }
    }

    /**
     * post发送xml请求
     *
     * @param uri
     * @param xml
     * @return
     * @throws Exception
     */
    public String postForXml(String uri, String xml) throws Exception {
        HttpPost httpPost = null;
        String result = null;
        try {
            httpPost = new HttpPost(uri);
            StringEntity stringEntity = new StringEntity(xml, ContentType.create("text/xml", Consts.UTF_8));
            httpPost.setEntity(stringEntity);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.error("HTTP POST failed,uri: {}, params : {} ,statusLine : {}", uri, xml, httpResponse.getStatusLine());
                return result;
            }
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity, Consts.UTF_8);
            log.info("http post uri: {} result:{}", uri, result);
            return result;
        } catch (Exception e) {
            log.error("HTTP POST failed,uri:{},paramsJson:{},exception message:{}", uri, xml, e.getMessage());
            throw e;
        } finally {
            if (httpPost != null) {
                httpPost.reset();
            }
        }
    }

    public JSONObject postForJSONObject(String url, List<NameValuePair> nameValuePairs) throws IOException {
        JSONObject result = null;
        try {
            result = JSONObject.parseObject(post(url, nameValuePairs));
        } catch (Exception e) {
            result = new JSONObject();
            log.error("postForJSONObject fromObject error:{}", e);
        }
        return result;
    }


    public List<NameValuePair> postParameter(JSONObject aramJsonObject) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        @SuppressWarnings("unchecked")
        Iterator<String> keys = aramJsonObject.keySet().iterator();
        String key;
        while (keys.hasNext()) {
            key = keys.next();
            list.add(new BasicNameValuePair(key, aramJsonObject.get(key).toString()));
        }
        return list;
    }

    // 参数排序
    public String sort(JSONObject paramJsonObject) throws Exception {
        Iterator<String> keys = paramJsonObject.keySet().iterator();
        List<String> keysList = new ArrayList<String>(paramJsonObject.values().size());
        while (keys.hasNext()) {
            keysList.add(keys.next().toString());
        }
        Collections.sort(keysList);

        StringBuffer result = new StringBuffer("");
        for (String key : keysList) {
            result.append(key).append(paramJsonObject.getString(key));
        }
        return result.toString();
    }

    /**
     * 发送face请求使用方法
     * @param urlStr
     * @param requestMap
     * @param fileMap
     * @return
     */
    public static String faceRequest(String urlStr, Map<String, Object> requestMap, Map<String, String> fileMap) {
        Map textMap = new HashMap();
        textMap.putAll(requestMap);
        textMap.remove("class");
        String contentType = "application/octet-stream";
        log.info("HttpClientTransportJson_AuthService_FaceID prepareing!");
        String res = "";
        HttpURLConnection conn = null;
        String BOUNDARY = "-----------------12345654321-----------";
        DataInputStream in = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(50000);
            conn.setReadTimeout(50000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + BOUNDARY);
            conn.setRequestProperty("Charset", "UTF-8");
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            if (textMap != null) {
                byte[] data = (byte[]) textMap.get("meglive_data");
                if (data == null) {

                } else {
                    StringBuffer strBuf = new StringBuffer();
                    strBuf.append("\r\n").append("--").append(BOUNDARY)
                            .append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\""
                            + "meglive_data" + "\"; filename=\"" + " "
                            + "\"\r\n");
                    strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
                    out.write(strBuf.toString().getBytes("UTF-8"));
                    out.write(data);
                    textMap.remove("meglive_data");//移除
                    log.info("HttpClientTransportJson_AuthService_FaceID write meglive_data end!");
                }


            }
            if (textMap != null) {
                //处理图片
                for (int i = 0; i < imageFields.length; i++) {
                    String imageName = imageFields[i];
                    String image = (String)textMap.get(imageName);
                    byte[] data = null;
                    if (StringUtils.isNotBlank(image)) {
                        data =  image.getBytes("utf-8");
                    }
                    if (data == null) {

                    } else {
                        StringBuffer strBuf = new StringBuffer();
                        strBuf.append("\r\n").append("--").append(BOUNDARY)
                                .append("\r\n");
                        strBuf.append("Content-Disposition: form-data; name=\""
                                + imageName + "\"; filename=\"" + " "
                                + "\"\r\n");
                        strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
                        out.write(strBuf.toString().getBytes("UTF-8"));
                        out.write(data);
                        textMap.remove(imageName);//移除
                        log.info("HttpClientTransportJson_AuthService_FaceID write [" + imageFields[i] + "] end!");
                    }
                }
            }

            if (textMap != null) {
                StringBuffer strBuf = new StringBuffer();
                Iterator iter = textMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String inputName = (String) entry.getKey();
                    String inputValue = (String) entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    strBuf.append("\r\n").append("--").append(BOUNDARY)
                            .append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\""
                            + inputName + "\"\r\n\r\n");
                    strBuf.append(inputValue);
                }
                out.write(strBuf.toString().getBytes());
                log.info("HttpClientTransportJson_AuthService_FaceID write textMap other param end!");
            }
            if (fileMap != null) {
                Iterator iter = fileMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String inputName = (String) entry.getKey();
                    String inputValue = (String) entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    File file = new File(inputValue);
                    String filename = file.getName();
                    StringBuffer strBuf = new StringBuffer();
                    strBuf.append("\r\n").append("--").append(BOUNDARY)
                            .append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\""
                            + inputName + "\"; filename=\"" + filename
                            + "\"\r\n");
                    strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
                    out.write(strBuf.toString().getBytes());
                    in = new DataInputStream(
                            new FileInputStream(file));
                    int bytes = 0;
                    byte[] bufferOut = new byte[1024];
                    while ((bytes = in.read(bufferOut)) != -1) {
                        out.write(bufferOut, 0, bytes);
                    }

                }
                log.info("HttpClientTransportJson_AuthService_FaceID write fileMap param end!");
            }
            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                // 读取返回数据
                StringBuffer strBuf = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));

                String line = null;
                while ((line = reader.readLine()) != null) {
                    strBuf.append(line).append("\n");
                }
                res = strBuf.toString();
                reader.close();
                reader = null;
                log.info("HttpClientTransportJson_AuthService_FaceID ResponseCode:[" + responseCode + "], ResponseInfo:" + res);
            } else {
                StringBuffer error = new StringBuffer();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                        conn.getErrorStream()));
                String line1 = null;
                while ((line1 = bufferedReader.readLine()) != null) {
                    error.append(line1).append("\n");
                }
                res = error.toString();
                bufferedReader.close();
                bufferedReader = null;
                log.info("HttpClientTransportJson_AuthService_FaceID ResponseCode:[" + responseCode + "], ResponseInfo:" + res);
            }

        } catch (Exception e) {
            log.error("HttpClientTransportJson_AuthService_FaceID 发送POST请求出错。" + e);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }

    /**
     * 影像系统上传文件请求
     * @param file 文件
     * @param url url接口地址
     * @param applSeq
     * @param fileType 文件类型
     * @param idCard
     * @param channelCode 渠道号
     * @return
     */
    public static String imageUpload(File file, String url, String applSeq, String fileType, String idCard, String channelCode,String goodsKind) throws IOException {
        HttpPost httpPost = null;
        String result = null;
        MultipartEntity multipartEntity = new MultipartEntity();
        multipartEntity.addPart("File",new FileBody(file));
        try {
            String uri = createImageUpload(url, applSeq, fileType, idCard, channelCode,goodsKind);
            httpPost = new HttpPost(uri);
            httpPost.setEntity(multipartEntity);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                HttpEntity entity = httpResponse.getEntity();
                result = EntityUtils.toString(entity, Consts.UTF_8);
                log.info("失败 http post uri: {} result:{}", uri, result);
                return result;
            }
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity, Consts.UTF_8);
            log.info("http post uri: {} result:{}", uri, result);
            return result;
        } catch (Exception e) {
            throw e;
        } finally {
            if (httpPost != null) {
                httpPost.reset();
            }
        }
    }

    /**
     * 拼装imageUpload方法发送参数
     * @param url
     * @param applSeq
     * @param fileType
     * @param idCard
     * @param channelCode
     * @return
     */
    private static String createImageUpload(String url, String applSeq, String fileType, String idCard, String channelCode,String goodsKind){
        StringBuffer str = new StringBuffer();
        str.append(url).append("?").append("applSeq=").append(applSeq).append("&")
                .append("fileType=").append(fileType).append("&").append("idCard=").
                append(idCard).append("&").append("appOrigin=").append(channelCode).append("&").append("username=自营app");
        if (StringUtils.isNotBlank(goodsKind)) {
            str.append("&goodsKind=").append(goodsKind);
        }
        return str.toString();
    }

    /**
     * 调用猎户系统
     * @param channelId
     * @param url
     * @param params
     * @return
     */
    public static JSONObject quyHuntChann(String channelId, String url, JSONObject params) {
        long s = System.currentTimeMillis();
        try {
            String result = HttpClientUtil.getInstance().postForJSon(url, params.toString());
            log.info("quyHuntChann 调用猎户， channelId:{}, result:{}", channelId, JSON.toJSONString(result));
            return JSONObject.parseObject(result);
        } catch (Exception e) {
            log.error("quyHuntChann 调用猎户， channelId:{}, 异常:{}", channelId, e);
        } finally {
            log.info("quyHuntChann 调用猎户， channelId:{}, 耗时:{}", channelId, System.currentTimeMillis() - s);
        }
        return null;
    }

}
