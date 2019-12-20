package apiSelfPrac;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.sun.xml.fastinfoset.tools.XML_DOM_FI;

import API.TestUtil;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class RestClient {
	
	//RestClient.setBaseURI(baseURI);
	//RestAssured.baseURI = baseURI;
	
	public static Response doGet(String baseURI,String contentType,Header token,boolean log) {
		RequestSpecification request = RestClient.createRequest(contentType, token, log);
		return RestClient.executeAPI("GET", request,baseURI);
	}
	
   public static Response doPost(String baseURI,String contentType,Header token,Object obj,boolean bodyThroughFile,boolean log) {
		RequestSpecification request = RestClient.createRequest(contentType, token, log);
		if(bodyThroughFile) {
			request.body(obj.toString());
		}else {
			String jsonPayLoad = TestUtil.getSerializedJson(obj);
			request.body(jsonPayLoad);
		}
		return RestClient.executeAPI("POST", request, baseURI);
	}
   
    public static Response doPut(String baseURI,String contentType,Header token,Object obj,boolean bodyThroughFile,boolean log) {
		RequestSpecification request = RestClient.createRequest(contentType, token, log);
		String jsonPayLoad = TestUtil.getSerializedJson(obj);
		request.body(jsonPayLoad);
		return RestClient.executeAPI("PUT", request, null);
    }
    
    public static Response doDelete(String baseURI,String contentType,Header token,boolean log) {
 		RequestSpecification request = RestClient.createRequest(contentType, token, log);
 		return RestClient.executeAPI("DELETE", request, null);
    }
    
    public static void setBaseURI(String baseURI) {
    	RestAssured.baseURI = baseURI;
    }
    
    /*//To get response
    public static Response getResponse(String httpMethod,RequestSpecification request,String basepath) {
    	return executeAPI(httpMethod,request,basepath);
    }*/
    
    // To create request
    public static RequestSpecification createRequest(String contentType,Header token,boolean log) {
    	RequestSpecification request;
    	request = RestAssured.given();
    	// To add token
    	if(token!=null) {
    		request.header(token);//authoriz token
    	}
    	//To set content type
    	if(contentType.equalsIgnoreCase("Json")) {
    		request.contentType(ContentType.JSON);
    	}else if(contentType.equalsIgnoreCase("XML")) {
    		request.contentType(ContentType.XML);
    	}else if(contentType.equalsIgnoreCase("Text")) {
    		request.contentType(ContentType.TEXT);
    	}
    	//For Logs
    	if(log) {
    		request = RestAssured.given().log().all();
    	}
		return request;
    }
    //To call type of request
    public static Response executeAPI(String httpMethod,RequestSpecification request,String basepath) {
     	Response response = null;
    	switch(httpMethod) {
    	case "GET":
    		response = request.get(basepath);
    		break;
    	case "POST":
    		response = request.post(basepath);
    		break;
    	case "PUT":
    		response = request.put(basepath);
    		break;
    	case "DELETE":
    		response = request.delete(basepath);
    		break;
    	default:
    		System.out.println("Please PASS correct method");
    		break;
    	}
		return response;
    }
    
    public static Header setAuthKey(boolean passAuthKey,String authKeyParm,String authKeyVal) {
    	Header authKey = null;
    	if(passAuthKey) {
    		authKey= new Header(authKeyParm,authKeyVal);
    	}
		return authKey;
    }
    
    //Generic methods for responses
    
    public static JSONObject getResponseAsJsonObject(Response response) throws JSONException {
    	JSONObject jparentobj= new JSONObject(response.asString());
		return jparentobj;
    }
    
    public static String getValueFromJsonResponse(Response response,String value) {
		return response.jsonPath().get(value).toString();
    }
    
    public static JsonPath getJsonPath(Response response) {
		return response.jsonPath();
    }
    
    public static ArrayList getJsonbodysFromArray(Response response,String varaibleNameOFArray) throws JSONException {
    	return response.jsonPath().get(varaibleNameOFArray);
    	/*ArrayList list = response.jsonPath().get(arrayElementVaraible);
    	Map<String,Object> outputdataFromArray = (Map<String, Object>) list.get(4);
		for(Map.Entry<String,Object> entry : outputdataFromArray.entrySet()) {
			System.out.println(entry.getKey() +" -> "+entry.getValue());
		}*/
    }
    
    public static String getValueFromJsonArray(Response response,String propName) throws JSONException {
    	JSONArray responseJsonArrays = new JSONArray(response.asString());
    	String value = null;
    	for (int i = 0; i < responseJsonArrays.length(); i++) {
    		value = responseJsonArrays.getJSONObject(i).getString(propName);
    	}
    	return value;
    }
    
    public static InputStream getResponseAsInputStream_XML(Response response) {
		return response.asInputStream();
    }
    
    public static int getStatusCode(Response response) {
		return response.getStatusCode();
    }
    
    public static String getHeaderValue(Response response,String headername) {
		return response.getHeader(headername);	
    }
    
    public static int getHeaderCount(Response response) {
    	Headers headers = response.getHeaders();
		return headers.size();	
    }
    
    public static List<Header> getHeaderHeaders(Response response) {
    	Headers headers = response.getHeaders();
    	List<Header> headerList = headers.asList();
		return headerList;	
    }
    
    public static Object uploadJsonBody(String filePath) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
    	JsonParser jsinParser = new JsonParser();
    	Object obj = jsinParser.parse(new FileReader(filePath));
    	return obj;
    }
    
    public static Object uploadXmlBody(String filePath) throws JsonIOException, JsonSyntaxException, IOException {
    	FileInputStream fileInput = new FileInputStream(filePath);
    	Object obj = fileInput.read();
		return obj;	
    }
    
    /*public static Object deSerilizeTheResponse(Response response,String obj) {
    	response.as(response);
    }*/

}
