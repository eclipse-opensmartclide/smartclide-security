//package com.theia.service;
//
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.theia.controller.SmartCLIDEController;
//import org.json.JSONObject;
//import org.json.simple.parser.ParseException;
//import org.junit.Assert;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.*;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.web.client.RestTemplate;
//import org.xml.sax.SAXException;
//
//import javax.xml.parsers.ParserConfigurationException;
//import java.io.File;
//import java.io.IOException;
//import java.lang.reflect.Type;
//import java.util.*;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class SonarqubeServiceTest {
//
//    private static String token = "3fa6958c8209021fa8e2d7f0f2cb899256494601";
//    @Mock
//    private RestTemplate restTemplate;
//    @InjectMocks
//    SonarqubeService sonarqubeService;
//
//    @Autowired
//    SmartCLIDEController controller;
//    @Autowired
//    TheiaService theiaService;
//
//
//    @Test
//    public void vulnerabilities() throws ParseException {
//
//        String json = "{\"CK\":{\"lcom\":[0,0.10910936800871021,3.1849529780564267],\"cbo\":[0.017050298380221656,0.03692993475020107,0.5714285714285714],\"wmc\":[0.13793103448275863,0.04986595433654195,0.2765273311897106]},\"PMD\":{\"ExceptionHandling\":[0,0.22938518010164353,12.987012987012987],\"Assignment\":[0,0.11160028050045479,7.6923076923076929],\"Logging\":[0,0.05692917472098835,6.8493150684931509],\"NullPointer\":[0,0.32358608981534067,25.966183574879229],\"ResourceHandling\":[0,2.201831659093579,166.66666666666667],\"MisusedFunctionality\":[0,0.13732179935769163,4.784688995215311]},\"Characteristics\":{\"Confidentiality\":[0.005,0.005,0.005,0.1,0.1,0.1,0.01,0.01,0.01,0.1,0.1,0.005,0.2,0.15,0.1],\"Integrity\":[0.01,0.005,0.005,0.1,0.15,0.01,0.01,0.01,0.01,0.15,0.15,0.01,0.16,0.21,0.01],\"Availability\":[0.005,0.005,0.01,0.1,0.01,0.01,0.2,0.3,0.01,0.01,0.01,0.3,0.01,0.01,0.01]},\"metricKeys\":{\"vulnerabilities\":[0,0.09848484848484848,4]},\"Sonarqube\":{\"command-injection\":[0,0.013234192551328933,1.5479876160990714],\"ssrf\":[0,0.024419175132769336,2.2172949002217297],\"weak-cryptography\":[0,0.0015070136414874827,0.1989258006763477],\"auth\":[0,0.024207864640426639,3.0959752321981428],\"insecure-conf\":[0,0.7356100591012389,32.05128205128205]}}";
//        String returnJson = "{\"ssrf\":16.0,\"insecure-conf\":16.0,\"auth\":16.0,\"weak-cryptography\":16.0,\"command-injection\":16.0}";
//        Type listType = new TypeToken<LinkedHashMap<String, LinkedHashMap<String, List<Double>>>>() {
//        }.getType();
//
//        LinkedHashMap<String, LinkedHashMap<String, List<Double>>> sonarProperties = new Gson().fromJson(json, listType);
//        HashMap<String, Double> returnHash = new HashMap<>();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBasicAuth("admin", "admin");
//
//        HttpEntity request = new HttpEntity(headers);
//        ResponseEntity<String> responseEntity = new ResponseEntity<String>("{\"paging\":{\"pageIndex\":1,\"pageSize\":500,\"total\":16},\"hotspots\":[{\"key\":\"AYURFdERz3zLmGxxe4lG\",\"component\":\"struts:core/src/main/java/org/apache/struts2/interceptor/I18nInterceptor.java\",\"project\":\"struts\",\"securityCategory\":\"insecure-conf\",\"vulnerabilityProbability\":\"LOW\",\"status\":\"TO_REVIEW\",\"line\":398,\"message\":\"Make sure creating this cookie without the \\\"secure\\\" flag is safe here.\",\"author\":\"lukaszlenart@apache.org\",\"creationDate\":\"2017-01-15T16:59:41+0000\",\"updateDate\":\"2022-12-14T14:36:25+0000\",\"textRange\":{\"startLine\":398,\"endLine\":398,\"startOffset\":32,\"endOffset\":38},\"flows\":[]},{\"key\":\"AYURFduXz3zLmGxxe408\",\"component\":\"struts:core/src/main/java/org/apache/struts2/result/plain/HttpCookies.java\",\"project\":\"struts\",\"securityCategory\":\"insecure-conf\",\"vulnerabilityProbability\":\"LOW\",\"status\":\"TO_REVIEW\",\"line\":31,\"message\":\"Make sure creating this cookie without the \\\"secure\\\" flag is safe here.\",\"author\":\"lukaszlenart@apache.org\",\"creationDate\":\"2020-06-29T08:57:00+0000\",\"updateDate\":\"2022-12-14T14:36:25+0000\",\"textRange\":{\"startLine\":31,\"endLine\":31,\"startOffset\":24,\"endOffset\":30},\"flows\":[]},{\"key\":\"AYURFeY6z3zLmGxxe4_a\",\"component\":\"struts:apps/showcase/src/main/java/org/apache/struts2/showcase/chat/ChatLoginAction.java\",\"project\":\"struts\",\"securityCategory\":\"insecure-conf\",\"vulnerabilityProbability\":\"LOW\",\"status\":\"TO_REVIEW\",\"line\":55,\"message\":\"Make sure this debug feature is deactivated before delivering the code in production.\",\"author\":\"tmjee@apache.org\",\"creationDate\":\"2006-06-17T08:33:02+0000\",\"updateDate\":\"2022-12-14T14:36:25+0000\",\"textRange\":{\"startLine\":55,\"endLine\":55,\"startOffset\":5,\"endOffset\":20},\"flows\":[]},{\"key\":\"AYURFccBz3zLmGxxe4ZV\",\"component\":\"struts:core/src/main/java/com/opensymphony/xwork2/mock/MockObjectTypeDeterminer.java\",\"project\":\"struts\",\"securityCategory\":\"insecure-conf\",\"vulnerabilityProbability\":\"LOW\",\"status\":\"TO_REVIEW\",\"line\":75,\"message\":\"Make sure this debug feature is deactivated before delivering the code in production.\",\"author\":\"lukaszlenart@apache.org\",\"creationDate\":\"2010-02-18T08:22:46+0000\",\"updateDate\":\"2022-12-14T14:36:25+0000\",\"textRange\":{\"startLine\":75,\"endLine\":75,\"startOffset\":14,\"endOffset\":29},\"flows\":[]},{\"key\":\"AYURFcm_z3zLmGxxe4dj\",\"component\":\"struts:core/src/main/java/com/opensymphony/xwork2/util/ClassPathFinder.java\",\"project\":\"struts\",\"securityCategory\":\"insecure-conf\",\"vulnerabilityProbability\":\"LOW\",\"status\":\"TO_REVIEW\",\"line\":109,\"message\":\"Make sure this debug feature is deactivated before delivering the code in production.\",\"author\":\"lukaszlenart@apache.org\",\"creationDate\":\"2016-12-31T11:04:35+0000\",\"updateDate\":\"2022-12-14T14:36:25+0000\",\"textRange\":{\"startLine\":109,\"endLine\":109,\"startOffset\":22,\"endOffset\":37},\"flows\":[]},{\"key\":\"AYURFcnOz3zLmGxxe4d1\",\"component\":\"struts:core/src/main/java/com/opensymphony/xwork2/util/PropertiesReader.java\",\"project\":\"struts\",\"securityCategory\":\"insecure-conf\",\"vulnerabilityProbability\":\"LOW\",\"status\":\"TO_REVIEW\",\"line\":450,\"message\":\"Make sure this debug feature is deactivated before delivering the code in production.\",\"author\":\"lukaszlenart@apache.org\",\"creationDate\":\"2010-02-18T08:22:46+0000\",\"updateDate\":\"2022-12-14T14:36:25+0000\",\"textRange\":{\"startLine\":450,\"endLine\":450,\"startOffset\":16,\"endOffset\":31},\"flows\":[]},{\"key\":\"AYURFc_rz3zLmGxxe4j3\",\"component\":\"struts:core/src/main/java/org/apache/struts2/interceptor/debugging/DebuggingInterceptor.java\",\"project\":\"struts\",\"securityCategory\":\"insecure-conf\",\"vulnerabilityProbability\":\"LOW\",\"status\":\"TO_REVIEW\",\"line\":208,\"message\":\"Make sure this debug feature is deactivated before delivering the code in production.\",\"author\":\"mrdon@apache.org\",\"creationDate\":\"2006-07-01T23:36:50+0000\",\"updateDate\":\"2022-12-14T14:36:25+0000\",\"textRange\":{\"startLine\":208,\"endLine\":208,\"startOffset\":23,\"endOffset\":38},\"flows\":[]},{\"key\":\"AYURFc_rz3zLmGxxe4j5\",\"component\":\"struts:core/src/main/java/org/apache/struts2/interceptor/debugging/DebuggingInterceptor.java\",\"project\":\"struts\",\"securityCategory\":\"insecure-conf\",\"vulnerabilityProbability\":\"LOW\",\"status\":\"TO_REVIEW\",\"line\":293,\"message\":\"Make sure this debug feature is deactivated before delivering the code in production.\",\"author\":\"mrdon@apache.org\",\"creationDate\":\"2006-07-01T23:36:50+0000\",\"updateDate\":\"2022-12-14T14:36:25+0000\",\"textRange\":{\"startLine\":293,\"endLine\":293,\"startOffset\":15,\"endOffset\":30},\"flows\":[]},{\"key\":\"AYURFe_Ez3zLmGxxe55P\",\"component\":\"struts:plugins/embeddedjsp/src/main/java/org/apache/struts2/el/lang/FunctionMapperImpl.java\",\"project\":\"struts\",\"securityCategory\":\"insecure-conf\",\"vulnerabilityProbability\":\"LOW\",\"status\":\"TO_REVIEW\",\"line\":158,\"message\":\"Make sure this debug feature is deactivated before delivering the code in production.\",\"author\":\"musachy@apache.org\",\"creationDate\":\"2009-09-28T01:55:26+0000\",\"updateDate\":\"2022-12-14T14:36:25+0000\",\"textRange\":{\"startLine\":158,\"endLine\":158,\"startOffset\":22,\"endOffset\":37},\"flows\":[]},{\"key\":\"AYURFez2z3zLmGxxe5oZ\",\"component\":\"struts:plugins/embeddedjsp/src/main/java/org/apache/struts2/jasper/JspC.java\",\"project\":\"struts\",\"securityCategory\":\"insecure-conf\",\"vulnerabilityProbability\":\"LOW\",\"status\":\"TO_REVIEW\",\"line\":1065,\"message\":\"Make sure this debug feature is deactivated before delivering the code in production.\",\"author\":\"musachy@apache.org\",\"creationDate\":\"2009-09-28T01:55:26+0000\",\"updateDate\":\"2022-12-14T14:36:25+0000\",\"textRange\":{\"startLine\":1065,\"endLine\":1065,\"startOffset\":26,\"endOffset\":41},\"flows\":[]},{\"key\":\"AYURFeokz3zLmGxxe5Yq\",\"component\":\"struts:plugins/embeddedjsp/src/main/java/org/apache/struts2/jasper/compiler/Dumper.java\",\"project\":\"struts\",\"securityCategory\":\"insecure-conf\",\"vulnerabilityProbability\":\"LOW\",\"status\":\"TO_REVIEW\",\"line\":188,\"message\":\"Make sure this debug feature is deactivated before delivering the code in production.\",\"author\":\"musachy@apache.org\",\"creationDate\":\"2009-09-28T01:55:26+0000\",\"updateDate\":\"2022-12-14T14:36:25+0000\",\"textRange\":{\"startLine\":188,\"endLine\":188,\"startOffset\":7,\"endOffset\":22},\"flows\":[]},{\"key\":\"AYURFeokz3zLmGxxe5Yr\",\"component\":\"struts:plugins/embeddedjsp/src/main/java/org/apache/struts2/jasper/compiler/Dumper.java\",\"project\":\"struts\",\"securityCategory\":\"insecure-conf\",\"vulnerabilityProbability\":\"LOW\",\"status\":\"TO_REVIEW\",\"line\":196,\"message\":\"Make sure this debug feature is deactivated before delivering the code in production.\",\"author\":\"musachy@apache.org\",\"creationDate\":\"2009-09-28T01:55:26+0000\",\"updateDate\":\"2022-12-14T14:36:25+0000\",\"textRange\":{\"startLine\":196,\"endLine\":196,\"startOffset\":7,\"endOffset\":22},\"flows\":[]},{\"key\":\"AYURFenXz3zLmGxxe5T2\",\"component\":\"struts:plugins/embeddedjsp/src/main/java/org/apache/struts2/jasper/compiler/Localizer.java\",\"project\":\"struts\",\"securityCategory\":\"insecure-conf\",\"vulnerabilityProbability\":\"LOW\",\"status\":\"TO_REVIEW\",\"line\":40,\"message\":\"Make sure this debug feature is deactivated before delivering the code in production.\",\"author\":\"musachy@apache.org\",\"creationDate\":\"2009-09-28T01:55:26+0000\",\"updateDate\":\"2022-12-14T14:36:25+0000\",\"textRange\":{\"startLine\":40,\"endLine\":40,\"startOffset\":14,\"endOffset\":29},\"flows\":[]},{\"key\":\"AYURFexWz3zLmGxxe5h7\",\"component\":\"struts:plugins/embeddedjsp/src/main/java/org/apache/struts2/jasper/runtime/TagHandlerPool.java\",\"project\":\"struts\",\"securityCategory\":\"insecure-conf\",\"vulnerabilityProbability\":\"LOW\",\"status\":\"TO_REVIEW\",\"line\":57,\"message\":\"Make sure this debug feature is deactivated before delivering the code in production.\",\"author\":\"musachy@apache.org\",\"creationDate\":\"2009-09-28T01:55:26+0000\",\"updateDate\":\"2022-12-14T14:36:25+0000\",\"textRange\":{\"startLine\":57,\"endLine\":57,\"startOffset\":18,\"endOffset\":33},\"flows\":[]},{\"key\":\"AYURFewgz3zLmGxxe5gL\",\"component\":\"struts:plugins/embeddedjsp/src/main/java/org/apache/struts2/jasper/servlet/JasperLoader.java\",\"project\":\"struts\",\"securityCategory\":\"insecure-conf\",\"vulnerabilityProbability\":\"LOW\",\"status\":\"TO_REVIEW\",\"line\":120,\"message\":\"Make sure this debug feature is deactivated before delivering the code in production.\",\"author\":\"musachy@apache.org\",\"creationDate\":\"2009-09-28T01:55:26+0000\",\"updateDate\":\"2022-12-14T14:36:25+0000\",\"textRange\":{\"startLine\":120,\"endLine\":120,\"startOffset\":23,\"endOffset\":38},\"flows\":[]},{\"key\":\"AYURFfujz3zLmGxxe6LZ\",\"component\":\"struts:plugins/jasperreports/src/main/java/org/apache/struts2/views/jasperreports/CompileReport.java\",\"project\":\"struts\",\"securityCategory\":\"insecure-conf\",\"vulnerabilityProbability\":\"LOW\",\"status\":\"TO_REVIEW\",\"line\":42,\"message\":\"Make sure this debug feature is deactivated before delivering the code in production.\",\"author\":\"tmjee@apache.org\",\"creationDate\":\"2006-04-03T15:28:51+0000\",\"updateDate\":\"2022-12-14T14:36:25+0000\",\"textRange\":{\"startLine\":42,\"endLine\":42,\"startOffset\":14,\"endOffset\":29},\"flows\":[]}],\"components\":[{\"key\":\"struts:core/src/main/java/com/opensymphony/xwork2/util/ClassPathFinder.java\",\"qualifier\":\"FIL\",\"name\":\"ClassPathFinder.java\",\"longName\":\"core/src/main/java/com/opensymphony/xwork2/util/ClassPathFinder.java\",\"path\":\"core/src/main/java/com/opensymphony/xwork2/util/ClassPathFinder.java\"},{\"key\":\"struts:core/src/main/java/org/apache/struts2/interceptor/I18nInterceptor.java\",\"qualifier\":\"FIL\",\"name\":\"I18nInterceptor.java\",\"longName\":\"core/src/main/java/org/apache/struts2/interceptor/I18nInterceptor.java\",\"path\":\"core/src/main/java/org/apache/struts2/interceptor/I18nInterceptor.java\"},{\"key\":\"struts:core/src/main/java/org/apache/struts2/interceptor/debugging/DebuggingInterceptor.java\",\"qualifier\":\"FIL\",\"name\":\"DebuggingInterceptor.java\",\"longName\":\"core/src/main/java/org/apache/struts2/interceptor/debugging/DebuggingInterceptor.java\",\"path\":\"core/src/main/java/org/apache/struts2/interceptor/debugging/DebuggingInterceptor.java\"},{\"key\":\"struts:plugins/jasperreports/src/main/java/org/apache/struts2/views/jasperreports/CompileReport.java\",\"qualifier\":\"FIL\",\"name\":\"CompileReport.java\",\"longName\":\"plugins/jasperreports/src/main/java/org/apache/struts2/views/jasperreports/CompileReport.java\",\"path\":\"plugins/jasperreports/src/main/java/org/apache/struts2/views/jasperreports/CompileReport.java\"},{\"key\":\"struts:core/src/main/java/com/opensymphony/xwork2/mock/MockObjectTypeDeterminer.java\",\"qualifier\":\"FIL\",\"name\":\"MockObjectTypeDeterminer.java\",\"longName\":\"core/src/main/java/com/opensymphony/xwork2/mock/MockObjectTypeDeterminer.java\",\"path\":\"core/src/main/java/com/opensymphony/xwork2/mock/MockObjectTypeDeterminer.java\"},{\"key\":\"struts:apps/showcase/src/main/java/org/apache/struts2/showcase/chat/ChatLoginAction.java\",\"qualifier\":\"FIL\",\"name\":\"ChatLoginAction.java\",\"longName\":\"apps/showcase/src/main/java/org/apache/struts2/showcase/chat/ChatLoginAction.java\",\"path\":\"apps/showcase/src/main/java/org/apache/struts2/showcase/chat/ChatLoginAction.java\"},{\"key\":\"struts:plugins/embeddedjsp/src/main/java/org/apache/struts2/jasper/servlet/JasperLoader.java\",\"qualifier\":\"FIL\",\"name\":\"JasperLoader.java\",\"longName\":\"plugins/embeddedjsp/src/main/java/org/apache/struts2/jasper/servlet/JasperLoader.java\",\"path\":\"plugins/embeddedjsp/src/main/java/org/apache/struts2/jasper/servlet/JasperLoader.java\"},{\"key\":\"struts:plugins/embeddedjsp/src/main/java/org/apache/struts2/jasper/JspC.java\",\"qualifier\":\"FIL\",\"name\":\"JspC.java\",\"longName\":\"plugins/embeddedjsp/src/main/java/org/apache/struts2/jasper/JspC.java\",\"path\":\"plugins/embeddedjsp/src/main/java/org/apache/struts2/jasper/JspC.java\"},{\"key\":\"struts:core/src/main/java/com/opensymphony/xwork2/util/PropertiesReader.java\",\"qualifier\":\"FIL\",\"name\":\"PropertiesReader.java\",\"longName\":\"core/src/main/java/com/opensymphony/xwork2/util/PropertiesReader.java\",\"path\":\"core/src/main/java/com/opensymphony/xwork2/util/PropertiesReader.java\"},{\"key\":\"struts:core/src/main/java/org/apache/struts2/result/plain/HttpCookies.java\",\"qualifier\":\"FIL\",\"name\":\"HttpCookies.java\",\"longName\":\"core/src/main/java/org/apache/struts2/result/plain/HttpCookies.java\",\"path\":\"core/src/main/java/org/apache/struts2/result/plain/HttpCookies.java\"},{\"key\":\"struts\",\"qualifier\":\"TRK\",\"name\":\"Struts 2\",\"longName\":\"Struts 2\"},{\"key\":\"struts:plugins/embeddedjsp/src/main/java/org/apache/struts2/jasper/runtime/TagHandlerPool.java\",\"qualifier\":\"FIL\",\"name\":\"TagHandlerPool.java\",\"longName\":\"plugins/embeddedjsp/src/main/java/org/apache/struts2/jasper/runtime/TagHandlerPool.java\",\"path\":\"plugins/embeddedjsp/src/main/java/org/apache/struts2/jasper/runtime/TagHandlerPool.java\"},{\"key\":\"struts:plugins/embeddedjsp/src/main/java/org/apache/struts2/jasper/compiler/Localizer.java\",\"qualifier\":\"FIL\",\"name\":\"Localizer.java\",\"longName\":\"plugins/embeddedjsp/src/main/java/org/apache/struts2/jasper/compiler/Localizer.java\",\"path\":\"plugins/embeddedjsp/src/main/java/org/apache/struts2/jasper/compiler/Localizer.java\"},{\"key\":\"struts:plugins/embeddedjsp/src/main/java/org/apache/struts2/jasper/compiler/Dumper.java\",\"qualifier\":\"FIL\",\"name\":\"Dumper.java\",\"longName\":\"plugins/embeddedjsp/src/main/java/org/apache/struts2/jasper/compiler/Dumper.java\",\"path\":\"plugins/embeddedjsp/src/main/java/org/apache/struts2/jasper/compiler/Dumper.java\"},{\"key\":\"struts:plugins/embeddedjsp/src/main/java/org/apache/struts2/el/lang/FunctionMapperImpl.java\",\"qualifier\":\"FIL\",\"name\":\"FunctionMapperImpl.java\",\"longName\":\"plugins/embeddedjsp/src/main/java/org/apache/struts2/el/lang/FunctionMapperImpl.java\",\"path\":\"plugins/embeddedjsp/src/main/java/org/apache/struts2/el/lang/FunctionMapperImpl.java\"}]}\n", HttpStatus.ACCEPTED);
//
//        when(restTemplate.exchange(
//                ArgumentMatchers.anyString(),
//                ArgumentMatchers.any(HttpMethod.class),
//                ArgumentMatchers.any(),
//                ArgumentMatchers.<Class<String>>any()))
//                .thenReturn(responseEntity);
//        returnHash = this.sonarqubeService.sonarqubeCustomVulnerabilities("user", "password", sonarProperties.get("Sonarqube").keySet(), "sda", 1000.00);
//        String testJson = new Gson().toJson(returnHash);
//
//        Assert.assertEquals(testJson, returnJson);
//    }
//
//
//    @Test
//    public void metrics() throws ParseException {
//
//        String json = "{\"CK\":{\"lcom\":[0,0.10910936800871021,3.1849529780564267],\"cbo\":[0.017050298380221656,0.03692993475020107,0.5714285714285714],\"wmc\":[0.13793103448275863,0.04986595433654195,0.2765273311897106]},\"PMD\":{\"ExceptionHandling\":[0,0.22938518010164353,12.987012987012987],\"Assignment\":[0,0.11160028050045479,7.6923076923076929],\"Logging\":[0,0.05692917472098835,6.8493150684931509],\"NullPointer\":[0,0.32358608981534067,25.966183574879229],\"ResourceHandling\":[0,2.201831659093579,166.66666666666667],\"MisusedFunctionality\":[0,0.13732179935769163,4.784688995215311]},\"Characteristics\":{\"Confidentiality\":[0.005,0.005,0.005,0.1,0.1,0.1,0.01,0.01,0.01,0.1,0.1,0.005,0.2,0.15,0.1],\"Integrity\":[0.01,0.005,0.005,0.1,0.15,0.01,0.01,0.01,0.01,0.15,0.15,0.01,0.16,0.21,0.01],\"Availability\":[0.005,0.005,0.01,0.1,0.01,0.01,0.2,0.3,0.01,0.01,0.01,0.3,0.01,0.01,0.01]},\"metricKeys\":{\"vulnerabilities\":[0,0.09848484848484848,4]},\"Sonarqube\":{\"command-injection\":[0,0.013234192551328933,1.5479876160990714],\"ssrf\":[0,0.024419175132769336,2.2172949002217297],\"weak-cryptography\":[0,0.0015070136414874827,0.1989258006763477],\"auth\":[0,0.024207864640426639,3.0959752321981428],\"insecure-conf\":[0,0.7356100591012389,32.05128205128205]}}";
//        String returnJson = "{\"ncloc\":134343.0,\"vulnerabilities\":4.0}";
//        Type listType = new TypeToken<LinkedHashMap<String, LinkedHashMap<String, List<Double>>>>() {
//        }.getType();
//
//        LinkedHashMap<String, LinkedHashMap<String, List<Double>>> sonarProperties = new Gson().fromJson(json, listType);
//
//        Set<String> sonarMetrics = Set.copyOf(sonarProperties.get("metricKeys").keySet());
//
//        HashMap<String, Double> returnHash = new HashMap<>();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBasicAuth("admin", "admin");
//
//        HttpEntity request = new HttpEntity(headers);
//        ResponseEntity<String> responseEntity = new ResponseEntity<String>("{\"component\":{\"key\":\"struts\",\"name\":\"Struts 2\",\"description\":\"Apache Struts 2\",\"qualifier\":\"TRK\",\"measures\":[{\"metric\":\"ncloc\",\"value\":\"134343\"},{\"metric\":\"vulnerabilities\",\"value\":\"4\",\"bestValue\":false}]}}\n", HttpStatus.ACCEPTED);
//        //ResponseEntity<String> responseEntity = new ResponseEntity<String>("{\"paging\":{\"pageIndex\":1,\"pageSize\":500,\"total\":1},\"hotspots\":[{\"key\":\"AYURFebFz3zLmGxxe5Ba\",\"component\":\"struts:apps/showcase/src/main/java/org/apache/struts2/showcase/hangman/PropertiesVocabSource.java\",\"project\":\"struts\",\"securityCategory\":\"weak-cryptography\",\"vulnerabilityProbability\":\"MEDIUM\",\"status\":\"TO_REVIEW\",\"line\":55,\"message\":\"Make sure that using this pseudorandom number generator is safe here.\",\"author\":\"tmjee@apache.org\",\"creationDate\":\"2006-07-09T17:06:43+0000\",\"updateDate\":\"2022-12-14T14:36:25+0000\",\"textRange\":{\"startLine\":55,\"endLine\":55,\"startOffset\":37,\"endOffset\":43},\"flows\":[]}],\"components\":[{\"key\":\"struts\",\"qualifier\":\"TRK\",\"name\":\"Struts 2\",\"longName\":\"Struts 2\"},{\"key\":\"struts:apps/showcase/src/main/java/org/apache/struts2/showcase/hangman/PropertiesVocabSource.java\",\"qualifier\":\"FIL\",\"name\":\"PropertiesVocabSource.java\",\"longName\":\"apps/showcase/src/main/java/org/apache/struts2/showcase/hangman/PropertiesVocabSource.java\",\"path\":\"apps/showcase/src/main/java/org/apache/struts2/showcase/hangman/PropertiesVocabSource.java\"}]}\n", HttpStatus.ACCEPTED);
//
//        when(restTemplate.exchange(
//                ArgumentMatchers.anyString(),
//                ArgumentMatchers.any(HttpMethod.class),
//                ArgumentMatchers.any(),
//                ArgumentMatchers.<Class<String>>any()))
//                .thenReturn(responseEntity);
//        returnHash = this.sonarqubeService.sonarqubeCustomMetrics("user", "password", sonarMetrics, "");
//        String testJson = new Gson().toJson(returnHash);
//
//        Assert.assertEquals(testJson, returnJson);
//
//
//    }
//
//
//    @Test
//    public void testcpp() throws ParserConfigurationException, IOException, ParseException, SAXException {
//
//        //public HashMap<String, Double> sonarqubeCustomCPP(String sonar_user,String sonar_password, Double linesOfCode, String id, List<String> CppRules) throws ParserConfigurationException, IOException, SAXException, ParseException {
//            String sonarCPPreturn = "{\"Assignment\":0.0,\"Exception_Handling\":0.0,\"NPE\":0.0,\"IO\":0.0,\"Misused_Functionality\":36.36363636363637,\"Dead_Code\":0.0,\"Overflow\":0.0}";
//            String cpprules = "[\"Assignment\",\"Exception_Handling\",\"IO\",\"Misused_Functionality\",\"NPE\",\"Overflow\",\"Dead_Code\"]";
//            Type listType = new TypeToken< List<String>>() {
//            }.getType();
//
//            List<String> cppRules = new Gson().fromJson(cpprules, listType);
//
//        ResponseEntity<String> responseEntity = new ResponseEntity<String>("{\"total\":4,\"p\":1,\"ps\":100,\"paging\":{\"pageIndex\":1,\"pageSize\":100,\"total\":4},\"effortTotal\":20,\"issues\":[{\"key\":\"AYURIOQ7z3zLmGxxfKw5\",\"rule\":\"cppcheck:funcArgNamesDifferent\",\"severity\":\"MINOR\",\"component\":\"travis-ci-cpp-example:lib.c\",\"project\":\"travis-ci-cpp-example\",\"line\":59,\"hash\":\"cd5056d2cfaa4e4527d9a8a66bccc74c\",\"textRange\":{\"startLine\":59,\"endLine\":59,\"startOffset\":0,\"endOffset\":29},\"flows\":[],\"status\":\"OPEN\",\"message\":\"[inconclusive] Function \\u0027op_xor\\u0027 argument 1 names different: declaration \\u0027a\\u0027 definition \\u0027x\\u0027.\",\"effort\":\"5min\",\"debt\":\"5min\",\"author\":\"deftio@deftio.com\",\"tags\":[\"cppcheck\",\"cwe\"],\"creationDate\":\"2017-03-26T05:39:29+0000\",\"updateDate\":\"2022-12-14T14:53:42+0000\",\"type\":\"CODE_SMELL\",\"scope\":\"MAIN\",\"quickFixAvailable\":false},{\"key\":\"AYURIOQ7z3zLmGxxfKw6\",\"rule\":\"cppcheck:funcArgNamesDifferent\",\"severity\":\"MINOR\",\"component\":\"travis-ci-cpp-example:lib.c\",\"project\":\"travis-ci-cpp-example\",\"line\":59,\"hash\":\"cd5056d2cfaa4e4527d9a8a66bccc74c\",\"textRange\":{\"startLine\":59,\"endLine\":59,\"startOffset\":0,\"endOffset\":29},\"flows\":[],\"status\":\"OPEN\",\"message\":\"[inconclusive] Function \\u0027op_xor\\u0027 argument 2 names different: declaration \\u0027b\\u0027 definition \\u0027y\\u0027.\",\"effort\":\"5min\",\"debt\":\"5min\",\"author\":\"deftio@deftio.com\",\"tags\":[\"cppcheck\",\"cwe\"],\"creationDate\":\"2017-03-26T05:39:29+0000\",\"updateDate\":\"2022-12-14T14:53:42+0000\",\"type\":\"CODE_SMELL\",\"scope\":\"MAIN\",\"quickFixAvailable\":false},{\"key\":\"AYURIOQ7z3zLmGxxfKw7\",\"rule\":\"cppcheck:funcArgNamesDifferent\",\"severity\":\"MINOR\",\"component\":\"travis-ci-cpp-example:lib.c\",\"project\":\"travis-ci-cpp-example\",\"line\":64,\"hash\":\"ef9c1ea410565ad95aed331051da304f\",\"textRange\":{\"startLine\":64,\"endLine\":64,\"startOffset\":0,\"endOffset\":30},\"flows\":[],\"status\":\"OPEN\",\"message\":\"[inconclusive] Function \\u0027op_xnor\\u0027 argument 1 names different: declaration \\u0027a\\u0027 definition \\u0027x\\u0027.\",\"effort\":\"5min\",\"debt\":\"5min\",\"author\":\"deftio@deftio.com\",\"tags\":[\"cppcheck\",\"cwe\"],\"creationDate\":\"2017-03-26T05:39:29+0000\",\"updateDate\":\"2022-12-14T14:53:42+0000\",\"type\":\"CODE_SMELL\",\"scope\":\"MAIN\",\"quickFixAvailable\":false},{\"key\":\"AYURIOQ7z3zLmGxxfKw8\",\"rule\":\"cppcheck:funcArgNamesDifferent\",\"severity\":\"MINOR\",\"component\":\"travis-ci-cpp-example:lib.c\",\"project\":\"travis-ci-cpp-example\",\"line\":64,\"hash\":\"ef9c1ea410565ad95aed331051da304f\",\"textRange\":{\"startLine\":64,\"endLine\":64,\"startOffset\":0,\"endOffset\":30},\"flows\":[],\"status\":\"OPEN\",\"message\":\"[inconclusive] Function \\u0027op_xnor\\u0027 argument 2 names different: declaration \\u0027b\\u0027 definition \\u0027y\\u0027.\",\"effort\":\"5min\",\"debt\":\"5min\",\"author\":\"deftio@deftio.com\",\"tags\":[\"cppcheck\",\"cwe\"],\"creationDate\":\"2017-03-26T05:39:29+0000\",\"updateDate\":\"2022-12-14T14:53:42+0000\",\"type\":\"CODE_SMELL\",\"scope\":\"MAIN\",\"quickFixAvailable\":false}],\"components\":[{\"key\":\"travis-ci-cpp-example:lib.c\",\"enabled\":true,\"qualifier\":\"FIL\",\"name\":\"lib.c\",\"longName\":\"lib.c\",\"path\":\"lib.c\"},{\"key\":\"travis-ci-cpp-example\",\"enabled\":true,\"qualifier\":\"TRK\",\"name\":\"travis-ci-cpp-example\",\"longName\":\"travis-ci-cpp-example\"}],\"facets\":[]}", HttpStatus.ACCEPTED);
//        //ResponseEntity<String> responseEntity = new ResponseEntity<String>("{\"paging\":{\"pageIndex\":1,\"pageSize\":500,\"total\":1},\"hotspots\":[{\"key\":\"AYURFebFz3zLmGxxe5Ba\",\"component\":\"struts:apps/showcase/src/main/java/org/apache/struts2/showcase/hangman/PropertiesVocabSource.java\",\"project\":\"struts\",\"securityCategory\":\"weak-cryptography\",\"vulnerabilityProbability\":\"MEDIUM\",\"status\":\"TO_REVIEW\",\"line\":55,\"message\":\"Make sure that using this pseudorandom number generator is safe here.\",\"author\":\"tmjee@apache.org\",\"creationDate\":\"2006-07-09T17:06:43+0000\",\"updateDate\":\"2022-12-14T14:36:25+0000\",\"textRange\":{\"startLine\":55,\"endLine\":55,\"startOffset\":37,\"endOffset\":43},\"flows\":[]}],\"components\":[{\"key\":\"struts\",\"qualifier\":\"TRK\",\"name\":\"Struts 2\",\"longName\":\"Struts 2\"},{\"key\":\"struts:apps/showcase/src/main/java/org/apache/struts2/showcase/hangman/PropertiesVocabSource.java\",\"qualifier\":\"FIL\",\"name\":\"PropertiesVocabSource.java\",\"longName\":\"apps/showcase/src/main/java/org/apache/struts2/showcase/hangman/PropertiesVocabSource.java\",\"path\":\"apps/showcase/src/main/java/org/apache/struts2/showcase/hangman/PropertiesVocabSource.java\"}]}\n", HttpStatus.ACCEPTED);
//
//        when(restTemplate.exchange(
//                ArgumentMatchers.anyString(),
//                ArgumentMatchers.any(HttpMethod.class),
//                ArgumentMatchers.any(),
//                ArgumentMatchers.<Class<String>>any()))
//                .thenReturn(responseEntity);
//
//        HashMap<String, Double> sonarCPP= this.sonarqubeService.sonarqubeCustomCPP("admin","admin",110.0,"" ,cppRules);
//
//        Gson gson = new Gson();
//
//        String jsonString = gson.toJson(sonarCPP);
//        Assert.assertEquals(jsonString, sonarCPPreturn);
//     }
//    }
//    //@Test
////    void sonarJavaAnalysis() {
//////        String url = "https://github.com/spring-projects/spring-mvc-showcase";
//////        UUID id = UUID.randomUUID();
//////        String sha = "test";
//////        File dir = null;
//////        try {
//////            dir = new File(this.theiaService.retrieveGithubCode(url, id));
//////        } catch (IOException e) {
//////            e.printStackTrace();
//////        }
//////        assertTrue(dir.exists(), "Directory created!");
/////
//// /
//////        try {
//////            this.sonarqubeService.sonarMavenAnalysis(sha,id, token);
//////        } catch (InterruptedException e) {
//////            e.printStackTrace();
//////        } catch (IOException e) {
//////            e.printStackTrace();
//////        }
//////
//////        RestTemplate restTemplate = new RestTemplate();
//////        HttpHeaders headers = new HttpHeaders();
//////        headers.setBasicAuth(token, "");
//////        HttpEntity request = new HttpEntity(headers);
//////
//////        ResponseEntity<String> response = restTemplate.exchange("http://" + host + ":9000/api/components/app?component=" + id,
//////                HttpMethod.GET,
//////                request,
//////                String.class
//////        );
//////        String json = response.getBody();
//////        JSONObject object = new JSONObject(json);
//////        String key = (String) object.get("key");
//////        assertTrue(key.equals(id.toString()), "Analysis successful.");
////        ResponseEntity<String> responseEntity = new ResponseEntity<String>("sampleBodyString", HttpStatus.ACCEPTED);
////
////        when(restTemplate.exchange(
////                ArgumentMatchers.anyString(),
////                ArgumentMatchers.any(HttpMethod.class),
////                ArgumentMatchers.any(),
////                ArgumentMatchers.<Class<String>>any()))
////                .thenReturn(responseEntity);
////
////        String json = "{\"CK\":{\"lcom\":[0,0.10910936800871021,3.1849529780564267],\"cbo\":[0.017050298380221656,0.03692993475020107,0.5714285714285714],\"wmc\":[0.13793103448275863,0.04986595433654195,0.2765273311897106]},\"PMD\":{\"ExceptionHandling\":[0,0.22938518010164353,12.987012987012987],\"Assignment\":[0,0.11160028050045479,7.6923076923076929],\"Logging\":[0,0.05692917472098835,6.8493150684931509],\"NullPointer\":[0,0.32358608981534067,25.966183574879229],\"ResourceHandling\":[0,2.201831659093579,166.66666666666667],\"MisusedFunctionality\":[0,0.13732179935769163,4.784688995215311]},\"Characteristics\":{\"Confidentiality\":[0.005,0.005,0.005,0.1,0.1,0.1,0.01,0.01,0.01,0.1,0.1,0.005,0.2,0.15,0.1],\"Integrity\":[0.01,0.005,0.005,0.1,0.15,0.01,0.01,0.01,0.01,0.15,0.15,0.01,0.16,0.21,0.01],\"Availability\":[0.005,0.005,0.01,0.1,0.01,0.01,0.2,0.3,0.01,0.01,0.01,0.3,0.01,0.01,0.01]},\"metricKeys\":{\"vulnerabilities\":[0,0.09848484848484848,4]},\"Sonarqube\":{\"command-injection\":[0,0.013234192551328933,1.5479876160990714],\"ssrf\":[0,0.024419175132769336,2.2172949002217297],\"weak-cryptography\":[0,0.0015070136414874827,0.1989258006763477],\"auth\":[0,0.024207864640426639,3.0959752321981428],\"insecure-conf\":[0,0.7356100591012389,32.05128205128205]}}";
////
////        controller.githubRetrieve("java",)
//
////    }
////
////    @Test
////    void sonarPythonAnalysis() {
////        String url = "https://github.com/pallets/flask";
////        UUID id = UUID.randomUUID();
////        File dir = null;
////        try {
////            dir = this.theiaService.retrieveGithubCode(url, id);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////        assertTrue(dir.exists(), "Directory created!");
////
////        try {
////            this.sonarqubeService.sonarPythonAnalysis(id, token);
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////
////        RestTemplate restTemplate = new RestTemplate();
////        HttpHeaders headers = new HttpHeaders();
////        headers.setBasicAuth(token, "");
////        HttpEntity request = new HttpEntity(headers);
////
////        ResponseEntity<String> response = restTemplate.exchange("http://" + host + ":9000/api/components/app?component=" + id,
////                HttpMethod.GET,
////                request,
////                String.class
////        );
////        String json = response.getBody();
////        JSONObject object = new JSONObject(json);
////        String key = (String) object.get("key");
////        assertTrue(key.equals(id.toString()), "Analysis successful.");
////    }
////
////    @Test
////    void sonarqubeCustomVulnerabilities() {
////        String url = "https://github.com/pallets/flask";
////        UUID id = UUID.randomUUID();
////        File dir = null;
////        try {
////            dir = this.theiaService.retrieveGithubCode(url, id);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////        assertTrue(dir.exists(), "Directory created!");
////
////        try {
////            this.sonarqubeService.sonarPythonAnalysis(id, token);
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////
////        RestTemplate restTemplate = new RestTemplate();
////        HttpHeaders headers = new HttpHeaders();
////        headers.setBasicAuth(token, "");
////        HttpEntity request = new HttpEntity(headers);
////        Set<String> vulnerabilities = new HashSet<>(){{
////           add("dos");
////           add("auth");
////        }};
////
////        HashMap<String, Double> sonarVuls = this.sonarqubeService.sonarqubeCustomVulnerabilities(token, vulnerabilities, id.toString());
////        assertTrue(sonarVuls.keySet().contains("auth"));
////        assertTrue(sonarVuls.keySet().contains("dos"));
////        assertTrue(sonarVuls.keySet().size() == 2);
////
////        assertFalse(sonarVuls.get("dos").isNaN());
////        assertFalse(sonarVuls.get("auth").isNaN());
////
////        vulnerabilities.add("weak-cryptography");
////        sonarVuls = this.sonarqubeService.sonarqubeCustomVulnerabilities(token, vulnerabilities, id.toString());
////        assertTrue(sonarVuls.keySet().contains("weak-cryptography"));
////        assertTrue(sonarVuls.keySet().size() == 3);
////
////        assertFalse(sonarVuls.get("weak-cryptography").isNaN());
////    }
////
////    @Test
////    void sonarqubeCustomMetrics() {
////        String url = "https://github.com/pallets/flask";
////        UUID id = UUID.randomUUID();
////        File dir = null;
////        try {
////            dir = this.theiaService.retrieveGithubCode(url, id);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////        assertTrue(dir.exists(), "Directory created!");
////
////        try {
////            this.sonarqubeService.sonarPythonAnalysis(id, token);
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////
////        RestTemplate restTemplate = new RestTemplate();
////        HttpHeaders headers = new HttpHeaders();
////        headers.setBasicAuth(token, "");
////        HttpEntity request = new HttpEntity(headers);
////
////        Set<String> metrics = new HashSet<>(){{
////            add("complexity");
////            add("vulnerabilities");
////        }};
////        HashMap<String, Double> sonarMetrics = this.sonarqubeService.sonarqubeCustomMetrics(token, metrics, id.toString());
////        assertTrue(sonarMetrics.keySet().contains("complexity"));
////        assertTrue(sonarMetrics.keySet().contains("vulnerabilities"));
////        assertTrue(sonarMetrics.keySet().size() == 3);
////
////        assertFalse(sonarMetrics.get("complexity").isNaN());
////        assertFalse(sonarMetrics.get("vulnerabilities").isNaN());
////
////        metrics.add("violations");
////        sonarMetrics = this.sonarqubeService.sonarqubeCustomMetrics(token, metrics, id.toString());
////        assertTrue(sonarMetrics.keySet().contains("violations"));
////        assertTrue(sonarMetrics.keySet().size() == 4);
////
////        assertFalse(sonarMetrics.get("violations").isNaN());
////    }
//
////    @Test
// //   public void testCPPcheck() throws IOException {
//       // String url = "https://github.com/spring-projects/spring-mvc-showcase.git";
////        UUID id = UUID.randomUUID();
////        //File dir = null;
////        Pattern pattern = Pattern.compile("(\\/)(?!.*\\1)(.*)(.git)");
////        Matcher matcher = pattern.matcher(url);
////        String name = "";
////        if (matcher.find()) {
////            name = matcher.group(2);
////        }
////
////
////        File dir = new File("/home/upload/" + name);
////
////
////        try {
////            this.theiaService.retrieveGithubCode(url, id);
////        } catch (IOException e) {
////            throw new RuntimeException(e);
////        }
////
////        this.sonarqubeService.runCPPcheck(name);
////        File report =  new File("/home/upload/" + name+"/build/report.xml");
////        assertTrue(report.exists());
//
//
//
//  //  }
////}