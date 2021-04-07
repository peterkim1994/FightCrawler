/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author peter
 */
public class devUtils {
    
    public static void printElms(Elements els){
        for(Element e: els){
            System.out.println(e.text());
        }
    }
    
    public static void printElmOwnText(Elements els){
          for(Element e: els){
            System.out.println(e.ownText());
        }
    }
    
    
}
