package com.risksense.converters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.json_custom.JSONObject;
import org.json_custom.JSONString;
import org.json_custom.JSONStringer;
import org.json_custom.JSONTokener;
import org.json_custom.XML;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;

public class XMLJSONConverter implements XMLJSONConverterI {
	
   public void eat(){
      System.out.println("Interface eats");
   }

   public void travel(){
      System.out.println("Interface travels");
   } 

   public int noOfLegs(){
      return 0;
   }

   /*
   public static void main(String args[]) throws IOException{
	  XMLJSONConverter x = new XMLJSONConverter();
      //x.eat();
      //x.travel();
      
      //System.out.println(args[0]);
      //System.out.println(args[1]);
      
      File fileIn = new File(args[0]);
      File fileOut = new File(args[1]);
      
      //System.out.println(fileIn.getPath());
      //System.out.println(fileOut.canRead());
      
      x.convertJSONtoXML(fileIn,fileOut);

   }
   */

   	//public void convertJSONtoXML(String json, String xml) throws IOException {
	public void convertJSONtoXML(File json, File xml) throws IOException {
		JsonElement jsonElement = convertFileToJSONElement(json);
		routeJsonElement(jsonElement);
		
	}
	
	public static void routeJsonElement(JsonElement jsonElement) {
		if(jsonElement.isJsonNull())
		{
			buildNullXmlElement();
		}
		
		if(jsonElement.isJsonPrimitive())
		{
			buildPrimitiveXmlElement(jsonElement.getAsJsonPrimitive());
		}
		
		if(jsonElement.isJsonArray())
		{
			buildArrayXmlElement(jsonElement.getAsJsonArray());
		}
		
		if(jsonElement.isJsonObject())
		{
			buildObjectXmlElement(jsonElement.getAsJsonObject());
		}
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
	
	
   	public static void printJsonObjectForReal(JsonObject object) {
   		Set<Map.Entry<String, JsonElement>> entrySet = object.entrySet();
   		for(Map.Entry<String, JsonElement> entry : entrySet) {
   		    //Print key and value
   	        System.out.println("key: "+ entry.getKey() + "; value: " + entry.getValue());
   	        
   	        //for nested objects iteration if required
   	        if (entry.getValue() instanceof JsonObject)
   	        	printJsonObjectForReal((JsonObject)entry.getValue());
   		}
   	}
   	
   	public static void buildNullXmlElement() {
   		System.out.println("</null>");
   		//return "</null>";
   	}
   	
   	public static void buildNullXmlElement(String name) {
   		if(!name.trim().isEmpty())
   		{
   			System.out.println("<null name=\"" + name + "\"/>");
   		} else {   		
   			System.out.println("</null>");
   		}
   		//return "</null>";
   	}
   	
   	public static void buildObjectXmlElement(JsonObject object) {
   		buildObjectXmlElement(object, "");
   	}

   	public static void buildObjectXmlElement(JsonObject object, String name) {
   		System.out.print("<object");
   		if(!name.trim().isEmpty())
   		{
   			System.out.print(" name=\"" + name + "\"");
   		}
   		System.out.println(">");
   		
   		Set<Map.Entry<String, JsonElement>> entrySet = object.entrySet();
   		for(Map.Entry<String, JsonElement> entry : entrySet) {
   	        
   	        //for nested objects iteration if required
   	        if (entry.getValue() instanceof JsonObject)
   	        {
   	        	buildObjectXmlElement((JsonObject)entry.getValue(), entry.getKey());
   	        }
   	        else if (entry.getValue() instanceof JsonArray)
   	        {
   	        	buildArrayXmlElement((JsonArray)entry.getValue(), entry.getKey());
   	        }
   	        else if (entry.getValue() instanceof JsonNull)
   	        {
   	        	buildNullXmlElement(entry.getKey());
   	        }
   	        else {
   	   	        String x = buildXmlElement(entry.getKey(), entry.getValue());
   	   	        System.out.println(x);
   	        }
   		}
   		System.out.println("</object>");
   	}
   	
   	public static void buildArrayXmlElement(JsonArray jsonArray) {
   		buildArrayXmlElement(jsonArray, "");
   	}
   	
   	public static void buildArrayXmlElement(JsonArray jsonArray, String name) {
   		System.out.print("<array");
   		if(!name.trim().isEmpty())
   		{
   			System.out.print(" name=\"" + name + "\"");
   		}
   		System.out.println(">");
   		
   		for(int i = 0; i < jsonArray.size(); i++)
   		{
   			JsonElement jsonElement = jsonArray.get(i);
   			routeJsonElement(jsonElement);
   		}
   		System.out.println("</array>");

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
   		
   		System.out.println(sb.toString());
   		
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