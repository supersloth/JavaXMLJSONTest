package com.risksense.converters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class XMLJSONConverter implements XMLJSONConverterI {
	
   	//public void convertJSONtoXML(String json, String xml) throws IOException {
	public void convertJSONtoXML(File json, File xml) throws IOException {
		JsonElement jsonElement = convertFileToJSONElement(json);
		
		StringBuilder sb = new StringBuilder();
   		sb.append(routeJsonElement(jsonElement));
   		
   		writeXMLFile(xml, sb.toString());
	}
	
	public void writeXMLFile(File xmlfile, String xmlstring) throws IOException {
		System.out.println(xmlstring);
	}
	
	public static String routeJsonElement(JsonElement jsonElement) {
		
		if(jsonElement.isJsonNull())
		{
			return buildNullXmlElement();
		}
		
		if(jsonElement.isJsonPrimitive())
		{
			return buildPrimitiveXmlElement(jsonElement.getAsJsonPrimitive());
		}
		
		if(jsonElement.isJsonArray())
		{
			return buildArrayXmlElement(jsonElement.getAsJsonArray());
		}
		
		if(jsonElement.isJsonObject())
		{
			return buildObjectXmlElement(jsonElement.getAsJsonObject());
		}
		
		return "";
	}
	
	public static JsonObject convertFileToJSONObject (File fileName){

        // Read from File to String
        JsonObject jsonObject = new JsonObject();
        
        try {
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(new FileReader(fileName.getPath()));
            jsonObject = jsonElement.getAsJsonObject();
        } catch (FileNotFoundException e) {
           
        } catch (IOException ioe){
        
        }
        
        return jsonObject;
    }
	
	public static JsonElement convertFileToJSONElement (File fileName){

		JsonElement jsonElement = null;
        try {
            JsonParser parser = new JsonParser();
            jsonElement = parser.parse(new FileReader(fileName.getPath()));
            //jsonObject = jsonElement.getAsJsonObject();
        } catch (FileNotFoundException e) {
           
        } catch (IOException ioe){
        
        }
        
		return jsonElement;
    }
	
	String readFile(File fileName) throws IOException {
	    BufferedReader br = new BufferedReader(new FileReader(fileName));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        return sb.toString();
	    } finally {
	        br.close();
	    }
	}
   	
   	public static String buildNullXmlElement() {
   		return "</null>";
   	}
   	
   	public static String buildNullXmlElement(String name) {
   		if(!name.trim().isEmpty())
   		{
   			return "<null name=\"" + name + "\"/>";
   		} else {   		
   			return "</null>";
   		}
   	}
   	
   	public static String buildObjectXmlElement(JsonObject object) {
   		return buildObjectXmlElement(object, "");
   	}

   	public static String buildObjectXmlElement(JsonObject object, String name) {
   		StringBuilder sb = new StringBuilder();
   		
   		sb.append("<object");
   		if(!name.trim().isEmpty())
   		{
   			sb.append(" name=\"" + name + "\"");
   		}
   		sb.append(">");
   		
   		Set<Map.Entry<String, JsonElement>> entrySet = object.entrySet();
   		for(Map.Entry<String, JsonElement> entry : entrySet) {
   	        
   	        //for nested objects iteration if required
   	        if (entry.getValue() instanceof JsonObject)
   	        {
   	        	sb.append(buildObjectXmlElement((JsonObject)entry.getValue(), entry.getKey()));
   	        }
   	        else if (entry.getValue() instanceof JsonArray)
   	        {
   	        	sb.append(buildArrayXmlElement((JsonArray)entry.getValue(), entry.getKey()));
   	        }
   	        else if (entry.getValue() instanceof JsonNull)
   	        {
   	        	sb.append(buildNullXmlElement(entry.getKey()));
   	        }
   	        else {
   	   	        String x = buildXmlElement(entry.getKey(), entry.getValue());
   	   	        sb.append(x);
   	        }
   		}
   		sb.append("</object>");
   		
   		return sb.toString();
   	}
   	
   	public static String buildArrayXmlElement(JsonArray jsonArray) {
   		return buildArrayXmlElement(jsonArray, "");
   	}
   	
   	public static String buildArrayXmlElement(JsonArray jsonArray, String name) {
   		StringBuilder sb = new StringBuilder();
   		
   		sb.append("<array");
   		if(!name.trim().isEmpty())
   		{
   			sb.append(" name=\"" + name + "\"");
   		}
   		sb.append(">");
   		
   		for(int i = 0; i < jsonArray.size(); i++)
   		{
   			JsonElement jsonElement = jsonArray.get(i);
   			sb.append(routeJsonElement(jsonElement));
   		}
   		sb.append("</array>");
   		
   		return sb.toString();
   	}
   	
   	public static String buildPrimitiveXmlElement(JsonPrimitive jsonPrimitive) {
   		
		String name = "";
		String value = "";
		
		if(jsonPrimitive.isString()) {
			name = "string";
		} else if(jsonPrimitive.isBoolean()) {
			name = "boolean"; 
		}else if(jsonPrimitive.isNumber()) {
			name = "number";
		}
		value = jsonPrimitive.getAsString();

   		StringBuilder sb = new StringBuilder();
   		
   		sb.append("<" + name + ">");
   		sb.append(value);
   		sb.append("</" + name + ">");
   		
   		return sb.toString();
   	}
   	
   	public static String buildXmlElement(String name, Object value) {
   		String type = getType(value.toString());
   		StringBuilder sb = new StringBuilder();
   		
   		sb.append("<" + type);
   		if(!name.trim().isEmpty())
   		{
   			sb.append(" name=\"" + name + "\"");
   		}
   		sb.append(">");
   		sb.append(value.toString().replace("\"", ""));
   		sb.append("</" + type + ">");
   		
   		return sb.toString();
   	}
   	
   	public static String getType(String s) {  
   	    String t = "string";
   	    
   	    if(isNumeric(s))
   	    	return "number";
   	    
   	    if(isBoolean(s))
   	    	return "boolean";
   	    
   	    return t;
   	}  
   	
   	public static boolean isNumeric(String s) {  
   	    return s.matches("[-+]?\\d*\\.?\\d+");  
   	}  
   	
   	public static boolean isBoolean(String s) {  
   	    return s.toLowerCase().equals("true")||s.toLowerCase().equals("false");
   	}  

}