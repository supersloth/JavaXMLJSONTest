package com.risksense.converters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class XMLJSONConverter implements XMLJSONConverterI {
	
	public void convertJSONtoXML(File json, File xml) throws IOException {
		JsonElement jsonElement = convertFileToJSONElement(json); //read file in as jsonElement. 
		
		StringBuilder sb = new StringBuilder(); //create a string to write all the info to
   		sb.append(routeJsonElement(jsonElement)); //create root element all others attach too, return it as string, append
   		
   		writeXMLFile(xml, sb.toString()); //write to file!! done.
	}
	
	public void writeXMLFile(File xmlfile, String xmlstring) throws IOException {
		//System.out.println(xmlstring);
		try{
			if (!xmlfile.exists()) {
				xmlfile.createNewFile(); //create a file if it doesn't exist
	        }
			FileWriter fw = new FileWriter(xmlfile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(xmlstring); //write it
			bw.close(); //close it
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	public static String routeJsonElement(JsonElement jsonElement) {
		
		//figure out what type of jsonElement something is and then route appropriate based on that
		
		if(jsonElement.isJsonNull()) //if it's a null
		{
			return buildNullXmlElement();
		}
		
		if(jsonElement.isJsonPrimitive()) //if it's a primitive, i.e. just a string or a number, but not an object or array
		{
			return buildPrimitiveXmlElement(jsonElement.getAsJsonPrimitive()); //build primitive element based on requirements scope
		}
		
		if(jsonElement.isJsonArray()) // if it's an [a,r,r,a,y]
		{
			return buildArrayXmlElement(jsonElement.getAsJsonArray()); //build array element based on requirements scope
		}
		
		if(jsonElement.isJsonObject()) //if it's { an : object }
		{
			return buildObjectXmlElement(jsonElement.getAsJsonObject()); //build object element based on requirements scope
		}
		
		return ""; //it's nothing! return nothing! (it should never be nothing)
	}
	
	public static JsonObject convertFileToJSONObject (File fileName){

		//this function was what i started with to read to a JSONObject
		//but then i realizxed JSONObject was a subset of what was possible
		//and that wouldn't work for arrays, primitives, etc if that was the input
		//so this was a good starting point but isn't actually used anymore
		
        JsonObject jsonObject = new JsonObject();
        
        try {
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(new FileReader(fileName.getPath()));
            jsonObject = jsonElement.getAsJsonObject();
        } catch (IOException ioe){
        	System.out.println(ioe);
        }
        
        return jsonObject;
    }
	
	public static JsonElement convertFileToJSONElement (File fileName){
		
		//this is what's in use instead of the above function
		//this read it in as the jsonELEMENT and then we do routing based on
		//if that element is a null, primitive, array, or object
		//JsonElement is a type available to us thru the google gson library i imported using maven

		JsonElement jsonElement = null;
        try {
            JsonParser parser = new JsonParser();
            jsonElement = parser.parse(new FileReader(fileName.getPath()));
            //jsonObject = jsonElement.getAsJsonObject();
        } catch (IOException ioe){
        	System.out.println(ioe);
        }
        
		return jsonElement;
    }
   	
   	public static String buildNullXmlElement() {
   		//null xml element with no name
   		return "</null>";
   	}
   	
   	public static String buildNullXmlElement(String name) {
   	//null xml element with name
   		if(!name.trim().isEmpty())
   		{
   			return "<null name=\"" + name + "\"/>";
   		} else {   		
   			return "</null>";
   		}
   	}
   	
   	public static String buildObjectXmlElement(JsonObject object) {
   		//overload functions in case there's no name for elements
   		return buildObjectXmlElement(object, "");
   	}

   	public static String buildObjectXmlElement(JsonObject object, String name) {
   		StringBuilder sb = new StringBuilder();
   		
   		sb.append("<object"); //create object tag
   		if(!name.trim().isEmpty()) //add name if it exists
   		{
   			sb.append(" name=\"" + name + "\"");
   		}
   		sb.append(">"); //close object tag
   		
   		Set<Map.Entry<String, JsonElement>> entrySet = object.entrySet();
   		for(Map.Entry<String, JsonElement> entry : entrySet) { //loop thru elements within jsonobject
   	        
   	        //for nested objects iteration if required
   	        if (entry.getValue() instanceof JsonObject)
   	        {
   	        	//subelement is another object, so call this function again with recursion, append
   	        	sb.append(buildObjectXmlElement((JsonObject)entry.getValue(), entry.getKey())); //also takes keyname which is important for the requirements scope
   	        }
   	        else if (entry.getValue() instanceof JsonArray)
   	        {
   	        	//subelement is an array, so call that function, append
   	        	sb.append(buildArrayXmlElement((JsonArray)entry.getValue(), entry.getKey())); //also takes keyname which is important for the requirements scope
   	        }
   	        else if (entry.getValue() instanceof JsonNull)
   	        {
   	        	//subelement is a null, so call that function, append
   	        	sb.append(buildNullXmlElement(entry.getKey())); //also takes keyname which is important for the requirements scope
   	        }
   	        else {
   	        	//subelement is an primitive, so call that function, append
   	        	sb.append(buildPrimitiveXmlElement((JsonPrimitive)entry.getValue(), entry.getKey())); //also takes keyname which is important for the requirements scope
   	        }
   		}
   		sb.append("</object>"); //close the object up
   		
   		return sb.toString(); //return the string
   	}
   	
   	public static String buildArrayXmlElement(JsonArray jsonArray) {
   		//overload functions in case there's no name for elements
   		return buildArrayXmlElement(jsonArray, "");
   	}
   	
   	public static String buildArrayXmlElement(JsonArray jsonArray, String name) {
   		StringBuilder sb = new StringBuilder();
   		
   		sb.append("<array"); //create array tag
   		if(!name.trim().isEmpty()) //add name if it exists
   		{
   			sb.append(" name=\"" + name + "\"");
   		}
   		sb.append(">"); //close array tag
   		
   		for(int i = 0; i < jsonArray.size(); i++) //loop thru array elements
   		{
   			JsonElement jsonElement = jsonArray.get(i);
   			sb.append(routeJsonElement(jsonElement)); //use routing function, easier with arrays then with objects
   		}
   		sb.append("</array>"); //close the array tag up
   		
   		return sb.toString(); //return the string
   	}
   	
   	public static String buildPrimitiveXmlElement(JsonPrimitive JsonPrimitive) {
   		//overload functions in case there's no name for elements
   		return buildPrimitiveXmlElement(JsonPrimitive, "");
   	}
   	
   	public static String buildPrimitiveXmlElement(JsonPrimitive jsonPrimitive, String name) {
   		
   		//primitives are 'just values'
   		//the xml needs to be tagged with type and name if it exists based on requirements scope
   		//s does a little bit of routing
   		
   		String type = "";
   		
   		if(jsonPrimitive.isString()) { //check for type
   			type = "string";
		} else if(jsonPrimitive.isBoolean()) {
			type = "boolean"; 
		}else if(jsonPrimitive.isNumber()) {
			type = "number";
		}
   		
   		StringBuilder sb = new StringBuilder();
   		
   		sb.append("<" + type);
   		if(!name.trim().isEmpty())
   		{
   			sb.append(" name=\"" + name + "\"");
   		}
   		sb.append(">");
   		sb.append(jsonPrimitive.getAsString().replace("\"", ""));
   		sb.append("</" + type + ">");
   		
   		return sb.toString(); //return string
   	}

}