package com.risksense.converters;

import java.io.File;
import java.io.IOException;

public class CLI {
   public static void main(String args[]) throws IOException, IllegalArgumentException{
	  XMLJSONConverterI x2j = new XMLJSONConverter();
	  
	  if(args.length != 2)
	  {
		  System.out.println("Please enter two arguments");
	  }
      
	  System.out.println(args[0]);
	  System.out.println(args[1]);
	  
      File fileIn = new File(args[0]);
      File fileOut = new File(args[1]);
      
      System.out.println(fileIn.getAbsolutePath());
      System.out.println(fileOut.getAbsolutePath());
      
      x2j.convertJSONtoXML(fileIn,fileOut);

   }
}