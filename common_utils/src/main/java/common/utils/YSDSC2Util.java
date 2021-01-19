package common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.syxfjr.cserver.common.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

/**
 * @description: SC2安全级别工具类
 * @author: zjl
 * @date: 2020-11-10 11:26
 **/
@Slf4j
public class YSDSC2Util {


    /**
     * 解密响应对象 ⚠️注意判断空
     * @param msgHead
     * @param msgBody 加密数据
     * @param privateKey 私钥
     * @param securityKey 加密的symK
     * @param authorization 签名
     * @return
     * @throws Exception
     */
    public static YSDSC2RespDto decryptSC2RespDto(MsgHead msgHead, String msgBody, String privateKey, String securityKey, String authorization) throws Exception{
        YSDSC2RespDto dto = new YSDSC2RespDto();
        dto.setMsgHead(msgHead);
//        String symK = decryptSymK(securityKey,privateKey);
        String symK = securityKey;
        //解密msgBody
        String msgBodyJson = decryptMsgBody(symK,msgBody);
        //校验签名
        if (!checkSign(getSignReponse(JSONObject.parseObject(msgBodyJson),msgHead),authorization)) {
            //不通过，返回null
            return null;
        }
        //通过后，返回
        dto.setMsgBody(msgBodyJson);
        return dto;
    }


    /**
     * 加密发起请求数据 ⚠️注意判断空
     * @param msgHead
     * @param msgBodyJson 数据json
     * @param symK 密钥
     * @param publicKey 公钥
     * @return
     * @throws Exception
     */
    public static YSDSC2ReqHeaderDataDto encryptionSC2ReqHeaderDataDto(MsgHead msgHead, String msgBodyJson, String symK, String publicKey) throws Exception{
        YSDSC2ReqHeaderDataDto dto = new YSDSC2ReqHeaderDataDto();
        dto.setSecuritykey(encryptionSymK(symK,publicKey));
        String sign = getSign(JSONObject.parseObject(msgBodyJson),msgHead);
        if (StringUtils.isBlank(sign)) {
            return null;
        }
        dto.setAuthorization(sign);
        dto.setDto(encryptionSC2ReqDto(msgHead,msgBodyJson,symK));
        return dto;
    }


    /**
     * 加密SC2ReqDto请求对象
     * @param msgHead
     * @param msgBodyJson 数据json字符串
     * @param symK 密钥
     * @return
     * @throws Exception
     */
    public static YSDSC2ReqDto encryptionSC2ReqDto(MsgHead msgHead, String msgBodyJson, String symK) throws Exception {
        YSDSC2ReqDto dto = new YSDSC2ReqDto();
        dto.setMsgHead(JSONObject.toJSONString(msgHead));
        dto.setMsgBody(encryptionMsgBody(symK,msgBodyJson));
        return dto;
    }


    /**
     * 通过公钥加密symK密钥
     * @param securityKey 要加密的symK
     * @param publicKey 公钥
     */
    public static String encryptionSymK(String securityKey,String publicKey) throws Exception {
        byte[] aesSecretKeyArr = RSAUtils.encryptByPublicKey(securityKey.getBytes(), publicKey);
        String symK = Base64.encodeBase64String(aesSecretKeyArr);
        log.info("加密后的的到symk为:{}",symK);
        return symK;
    }

    /**
     * 通过私钥解密symK密钥
     * @param securityKey 公钥加密后的SymK
     * @param privateKey 私钥
     */
    public static String decryptSymK(String securityKey,String privateKey) throws Exception {
        byte[] aesSecretKeyArr = RSAUtils.decryptByPrivateKey(Base64.decodeBase64(securityKey), privateKey);
        String symK = new String(aesSecretKeyArr);
        log.info("解密后的到symk为:{}",symK);
        return symK;
    }

    /**
     * 加密msgBody
     * @param symK 密钥
     * @param msgBody 数据json字符串
     */
    public static String encryptionMsgBody(String symK,String msgBody) throws Exception{
        String encryptionBody = SM4Utils.encryptByEcb2Base64(msgBody,symK);
        log.info("加密后的数据为:{}",encryptionBody);
        return encryptionBody;
    }

    /**
     * 解密msgBody
     * @param symK 密钥
     * @param msgBody 数据json字符串
     */
    public static String decryptMsgBody(String symK,String msgBody) throws Exception{
        String decryptBody = SM4Utils.decryptEcb(symK,  new String(Base64.decodeBase64(msgBody)));
        log.info("解密密后的数据为:{}",decryptBody);
        return decryptBody;
    }

    /**
     * 获取加密签名
     * @param msgBody json字符串
     * @param msgHead json字符串
     */
    public static String getSign(JSONObject msgBody, MsgHead msgHead){
        StringBuilder sb = new StringBuilder();
        String msgHeadJson=null;

        if (null != msgHead) {
            msgHeadJson = JSON.toJSONString(msgHead);
            if (StringUtils.isNoneBlank(msgHeadJson)) {
                sb.append(msgHeadJson);//order 1
            }
        }
        if (null != msgBody) {
            sb.append(msgBody.get("commonRequest").toString());//order 2
            sb.append(msgBody.get("requestBody").toString());// order 3
        }

        if(sb.length() > 0) {
            String signNative = Base64.encodeBase64String(Md5Util.getMd532(sb.toString()).getBytes());
            log.info("返回签名为：{}",signNative);
           return signNative;
        }else {
            log.info("request sb length=0 getSign 签名失败");
            return null;
        }
    }

    /**
     * 获取响应加密签名
     * @param msgBody json字符串
     * @param msgHead json字符串
     */
    public static String getSignReponse(JSONObject msgBody, MsgHead msgHead){
        StringBuilder sb = new StringBuilder();
        String msgHeadJson=null;

        if (null != msgHead) {
            msgHeadJson = JSON.toJSONString(msgHead);
            if (StringUtils.isNoneBlank(msgHeadJson)) {
                sb.append(msgHeadJson);//order 1
            }
        } else {
            sb.append("{}");
        }
        if (null != msgBody) {
            sb.append(msgBody.get("commonResponse").toString());//order 2
            sb.append(msgBody.get("responseBody").toString());// order 3
        }
        log.info("本次签名数据为:{}",sb.toString());
        if(sb.length() > 0) {
            String signNative = Base64.encodeBase64String(Md5Util.getMd532(sb.toString()).getBytes());
            log.info("返回签名为：{}",signNative);
            return signNative;
        }else {
            log.info("request sb length=0 getSign 签名失败");
            return null;
        }
    }

    /**
     * 校验签名
     * @param sing 自己的签名
     * @param authorization 要对比的签名
     * @return
     */
    public static boolean checkSign(String sing, String authorization){
        if (!sing.equals(authorization)) {
            log.info("getSign 签名失败, sing:{}, authorization:{}",sing,authorization);
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws Exception{
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCKK8wcdp44LzKX8wqvph7STIDlFCJe/7YCMKTxcII4sB/eQIJ4leKWepc7Qvf0kMDQiMDt9GRnGnOOm8BYdzVdlNKbHA4OxrtP0EBbsK1om+3Evuw0gNIik4qx+pwiD7B7KUdnCBPlQOStVBJSRZvle3DmRBkRwHfPtdLxxHW3GwIDAQAB";
        String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIorzBx2njgvMpfzCq+mHtJMgOUUIl7/tgIwpPFwgjiwH95AgniV4pZ6lztC9/SQwNCIwO30ZGcac46bwFh3NV2U0pscDg7Gu0/QQFuwrWib7cS+7DSA0iKTirH6nCIPsHspR2cIE+VA5K1UElJFm+V7cOZEGRHAd8+10vHEdbcbAgMBAAECgYA/FzKTSKKEWyDJ+0NE4yCtvBsa/u+W20Jbi6S1Tj9X7SwQCvrIXwBg38dQWwrnlHVGgzs9LFvwLEbZtkrE2mH2FlyGbz5WqqxHYhpNj85tEzVHnS/vckqxL/exL3/iHU8rqBLvWWHWZIo7VCSsKeohhpb83ElkIXZmzjaYG6pP0QJBAMFtcKcAG7+PZ8b285hFSsetlF1mXmOLxyCzanSO5K6CWPtwXR11TokrfVKBPFC7DiEJw8F7y3Hepgy97hUxNskCQQC23lYusaCnjk1jq4g51rIjCWOTszjpLxfEPXkHNdXQhHfRemnE9/QJsquB5WYBsNQP3HetvI5A45qGtK98oxzDAkEAp38MkSCv5qbY99s7P72pLtYp5uNRkyQkUDXtVneyoUcwpLM9ftfpJTEqubo9r677YjKheDeqCbYC1n/V3jYauQJAfZ6mXVUwI2ohx4IiBnlxzZe5i9DmG2l6Thhcd5OH2cHl/US2O9SJE9Bk+Mp5FFhAlNTCcdrBbdnsYPTmSBxUuQJAGlhHmhiCS/UaJzLNwJNIAqqVoUCHC99VScYmbbUMD1WfYeKylJsMqmb+gWaTMmxCj+JdT8opfNYwYubuUIKY4g==";
        MsgHead msgHead = new MsgHead();
        msgHead.setNonce("1");
        msgHead.setPartnerId("2");
        msgHead.setTimestamp(4L);

        String msgBodyJson = "{\n" +
                "  \"msgBody\":\"MUM3NjhBRjg1NkZBQjlDRTU0REUzNzA4ODQ2RTUzQzY1MDM1MUExRTc4QzJBOTdCQkMwM0NEMzBGMEQwODNDNjI0RTdENUI4OTBFRUE3MTgwMDQ1NzdBRjQwMzNCQzA2ODNCMEM5RDBENTVBODU1MEIyNzA0RTk4ODkxNzc0MDQwRjhGRjRBOTM3NTMxMEIwNUY5MjU1RjI0NTgwM0E5RDdDM0RBOEJERUJEMjg4Qzk0NjZDQTNENTVCRjI5NzFF\"\n" +
                "}";
        YSDSC2ReqHeaderDataDto dto = encryptionSC2ReqHeaderDataDto(msgHead,msgBodyJson,"d938c23d1416a7aaf2480aa39f434961",publicKey);
        System.out.println(dto);
        YSDSC2RespDto respDto = decryptSC2RespDto(msgHead,dto.getDto().getMsgBody(),privateKey,dto.getSecuritykey(),dto.getAuthorization());
        System.out.println(respDto);

    }
}
