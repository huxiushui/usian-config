package common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.util.Map;

/**
 *  xml工具类
 * @author zjl
 * @date 2020/7/28
 * @description:
 */
public class XmlUtil {

  /*  *
     * 实体类转换xml字符串
     * @param obj
     * @return
     * @throws JsonProcessingException
     * */
    public static String objToXml(Object obj,String head) throws JsonProcessingException {
        ObjectMapper xmlMapper = new XmlMapper();
        //反序列化时，若实体类没有对应的属性，是否抛出JsonMappingException异常，false忽略掉
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //序列化是否绕根元素，true，则以类名为根元素
        xmlMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        //忽略空属性
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //XML标签名:使用骆驼命名的属性名，
        xmlMapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
        //设置转换模式
        xmlMapper.enable(MapperFeature.USE_STD_BEAN_NAMING);
        String xml = head  + xmlMapper.writeValueAsString(obj);
        return xml;
    }

    /**
     * 实体类转换xml字符串
     * @param obj
     * @return
     * @throws JsonProcessingException
     */
    public static String objToXmlNotNull(Object obj,String head) throws JsonProcessingException {
        ObjectMapper xmlMapper = new XmlMapper();
        //反序列化时，若实体类没有对应的属性，是否抛出JsonMappingException异常，false忽略掉
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //序列化是否绕根元素，true，则以类名为根元素
        xmlMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        //忽略空属性
//        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //XML标签名:使用骆驼命名的属性名，
//        xmlMapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
        //设置转换模式
        xmlMapper.enable(MapperFeature.USE_STD_BEAN_NAMING);
        String xml = head  + xmlMapper.writeValueAsString(obj);
        return xml;
    }

    /**
     * xml转Map
     * @param xml
     * @param cmis10000202ReponseDTOClass
     * @return
     * @throws IOException
     */
    public static Map xmlToMap(String xml, Class<Object> cmis10000202ReponseDTOClass) throws IOException {
        ObjectMapper xmlMapper = new XmlMapper();
        //反序列化时，若实体类没有对应的属性，是否抛出JsonMappingException异常，false忽略掉
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //序列化是否绕根元素，true，则以类名为根元素
        xmlMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        //忽略空属性
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //XML标签名:使用骆驼命名的属性名，
        xmlMapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
        //设置转换模式
        xmlMapper.enable(MapperFeature.USE_STD_BEAN_NAMING);
        return xmlMapper.readValue(xml,Map.class);
    }

    /**
     * xml转对象
     * @param xml
     * @return
     * @throws IOException
     */
    public static <T> T xmlToObj(String xml,Class<T> valueType) throws IOException {
        ObjectMapper xmlMapper = new XmlMapper();
        //反序列化时，若实体类没有对应的属性，是否抛出JsonMappingException异常，false忽略掉
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //序列化是否绕根元素，true，则以类名为根元素
        xmlMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        //忽略空属性
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //设置转换模式
        xmlMapper.enable(MapperFeature.USE_STD_BEAN_NAMING);
        return xmlMapper.readValue(xml,  valueType);
    }




}
