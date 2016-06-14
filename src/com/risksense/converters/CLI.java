package com.risksense.converters;

import java.io.File;
import java.io.IOException;

public class CLI {
   public static void main(String args[]) throws IOException, IllegalArgumentException{
	  XMLJSONConverterI x2j = ConverterFactory.createXMLJSONConverter();
	  
	  if(args.length != 2)
	  {
		  System.out.println("Please enter two arguments");
	  } else {
      	  
	      File fileIn = new File(args[0]);
	      File fileOut = new File(args[1]);
	      
	      x2j.convertJSONtoXML(fileIn,fileOut);
	  }

   }
}
