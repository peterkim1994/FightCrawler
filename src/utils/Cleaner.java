/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.jsoup.nodes.Element;

/**
 *
 * @author peter
 */
public class Cleaner {
    
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM-dd-yyyy");
    static DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("MMM-dd-yyyy");
    
    public static String getNumericalString(String text){
        // text = text.replaceAll("([a-zA-Z|\\-|\\s|\"|'|:])+","");
         text = text.replaceAll("([^0-9|^\\.])+","");
         text = text.trim();
         return text;
    }
    
    public static int parseInt(Element element){
        String text = element.text().replaceAll("([^0-9])+","");
        return Integer.parseInt(text);
    }    
 
    public static int parseInt(String string){
        String text = string.replaceAll("([^0-9])+","");
        return Integer.parseInt(text);
    }
    
    public static String whiteSpaceToHyphen(String text){
        text = text.replaceAll("'+",""); 
        text = text.replaceAll("\\s+","-");     
        return text;
    }
    public static void main(String[] args) {
        System.out.println(removeApostrophe(" peter o'kim "));
    }
    public static String removeApostrophe(String text){
        text = text.trim();
        text = text.replaceAll("\\.+","");  
        text = text.replaceAll("'+","");     
        return text;
    }
    
    public static double percentageToDecimal(Element element){
       return  percentageToDecimal(element.text());
    }
    
    public static double percentageToDecimal(String text){
          text = text.replaceAll("([^0-9])+","").trim();
          double decimal = Double.parseDouble(text);
          decimal = decimal/100;
          return decimal;
    }   
 
    public static String getNumberAndHyphen(String text){
        text = text.replaceAll("([^0-9|-])+","").trim();
        return text;
    }
    
    public static String parseText(Element element){
        String text = element.text().trim().toLowerCase();
        return text;
    }
   
    public static String getNumberAndHypen(Element element){
        return getNumberAndHyphen(element.text());
    }

    public static double parseDouble(Element element){
        String value = element.text();
        return Double.parseDouble(value);
    }
    
    public static String replace(Element element, String regex, String replacement){
        String text = element.text();
        text = text.replaceAll(regex, replacement).trim();
        return text;
    }
    public static String splitThenExtract(Element element, String seperator, int indexToExtract){
        String [] splits = element.text().split(seperator);
        if(indexToExtract >= 0){
            return splits[indexToExtract];
        }else{//if indexToExtract is negative
            return splits[splits.length + indexToExtract];
        }
    }
    
     public static String splitThenExtract(String text, String seperator, int indexToExtract){
        String [] splits = text.split(seperator);
        if(indexToExtract >= 0){
            return splits[indexToExtract];
        }else{//if indexToExtract is negative
            return splits[splits.length + indexToExtract];
        }
    }
    
     public static  double extractPercentage(Element element){
          return extractPercentage(element.text());
     }
    
     public static double extractPercentage(String rawData){
        String [] splitStats = rawData.split("\\(");   
        double percentage = percentageToDecimal(splitStats[1]);
        return percentage;
    }
     
    //converts Jan 14 2019 --->  2019-01-14
    public static LocalDate reformatDate(String date){        
        date = date.trim();
        date = date.replaceAll("\\.", "");
        date = date.replaceAll("\\s", "-");
        date = date.replaceAll(",", "").trim();  
        try{
            LocalDate aDate = LocalDate.parse(date, formatter);
             return aDate;
        }catch(DateTimeParseException e){
            LocalDate aDate = LocalDate.parse(date, formatter2);
            return aDate;
        }  
    }
    

}
