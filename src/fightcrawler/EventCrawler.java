/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fightcrawler;


import Models.UFCEvent;
import TextPreprocessingUtils.Cleaner;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author peter
 */
public class EventCrawler {
    
   private static int numChampionFights; 
   private DbSingleton db;
   private FighterProfileScrapper fighterScraper;
    
   public EventCrawler(){
       try {
           db = new DbSingleton();
           fighterScraper = new FighterProfileScrapper(db);
       } catch (SQLException ex) {
           Logger.getLogger(EventCrawler.class.getName()).log(Level.SEVERE, null, ex);
       }
   }
   
   public static void main(String[] args) {
        EventCrawler crawler = new EventCrawler();
        crawler.scrapeEvent(1);
   }   
   
   public void scrapeEvent(int page){
       scrapeEvent(page,true);
   }   
   //Crawls the main event listings page for all events contained on webpage
   public void scrapeEvent(int page, boolean previousEvent){
       try {
           String url ="http://www.ufcstats.com/statistics/events/completed?page="+page;
           Document eventsPage = Jsoup.connect(url).get(); // URL shortened!
           Elements names = eventsPage.getElementsByClass("b-link b-link_style_black"); 
           for(Element name: names)
               scrapeEventPage(name.attr("href"), true);         
        
       } catch(IOException ex){
           Logger.getLogger(EventCrawler.class.getName()).log(Level.SEVERE, null, ex);
       }
   }
   
   //crawls a single event
   public void scrapeEventPage(String url, boolean isPreviousEvent) throws IOException{//num attendence vs num fights to scrape  
        Document eventPage = Jsoup.connect(url).get();       
        Elements eventDetails = eventPage.getElementsByClass("b-list__box-list-item");          
        UFCEvent event = new UFCEvent();        
        int eventId = getEventDetails(eventDetails, eventPage, event);
        
        EventCrawler.numChampionFights = eventPage.getElementsByAttributeValue("src","http://1e49bc5171d173577ecd-1323f4090557a33db01577564f60846c.r80.cf1.rackcdn.com/belt.png").size();
        if(EventCrawler.numChampionFights == 0){//main event is always 5 rounds even though its not a title fight
            EventCrawler.numChampionFights = 1;
        }
        
//        Elements fightersOnEvent = eventPage.getElementsByClass("b-link b-link_style_black");
//        for(Element fighter : fightersOnEvent){
//            fighterScraper.scrapeFighterProfile(fighter.attr("href"));
//        }
        
        Elements fights = eventPage.getElementsByClass("b-fight-details__table-row b-fight-details__table-row__hover js-fight-details-click");
        
        for(Element fight : fights){
           String fightLink = fight.attr("data-link");
           Elements fighters = fight.getElementsByClass("b-link b-link_style_black");
           int fighter1Id = fighterScraper.scrapeFighterProfile(fighters.get(0).attr("href"));
           int fighter2Id = fighterScraper.scrapeFighterProfile(fighters.get(1).attr("href"));
           scrapeFight(fightLink, eventId, fighter1Id, fighter2Id);
        }
        
   }  
   
   public void scrapeFight(String fightUrl, int eventId, int fighter1Id,int fighter2Id){
       System.out.println(fightUrl);
   }
   
   public int getEventDetails(Elements eventDetails,Document eventPage, UFCEvent event){       
        String eventDate = Cleaner.splitThenExtract(eventDetails.get(0),":",1);
        event.date = Cleaner.reformatDate(eventDate);    
        event.country = Cleaner.splitThenExtract(eventDetails.get(1),",",-1);       
        event.EventName = eventPage.getElementsByClass("b-content__title-highlight").get(0).text();
        System.out.println(event.EventName);
        return db.recordEvent(event);        
   }

   

 
   //Updates the input lists with the method of outcome, and also the fighter names and urls for a event
   public static void getEventInfo(ArrayList<Element> fighters, ArrayList<Element> fightersOnEvent,
        Queue<String> fightOutcomes, Document eventPage, boolean isPastEvent ){       
        int counter = 0;   
        for(int i=0; i<fightersOnEvent.size(); i++){
            Element link = fightersOnEvent.get(i);
            if(!link.text().contains("View Matchup")){
                fighters.add(link);
            }
            if(i%2 == 0 && isPastEvent){//gets the method outcome for fight
                 counter++;
                 Element outcome = eventPage.select("tr.js-fight-details-click.b-fight-details__table-row__hover.b-fight-details__table-row:nth-of-type("+ counter +")"
                         + " > td.l-page_align_left.b-fight-details__table-col:nth-of-type(8) > p.b-fight-details__table-text:nth-of-type(1)").get(0); 
                fightOutcomes.add(outcome.text());
            }
       }
      //  System.out.println(fighters);
   }
   
    public static void getEventInfo(ArrayList<Element> fighters, ArrayList<Element> fightersOnEvent, Queue<String> fightOutcomes, Document eventPage){       
        int counter = 0;   
        for(int i=0; i<fightersOnEvent.size(); i++){
            Element link = fightersOnEvent.get(i);
            if(!link.text().contains("View Matchup")){
                fighters.add(link);
            }
            if(i%2 == 0){//gets the method outcome for fight
                 counter++;
                 Element outcome = eventPage.select("tr.js-fight-details-click.b-fight-details__table-row__hover.b-fight-details__table-row:nth-of-type("+ counter +")"
                         + " > td.l-page_align_left.b-fight-details__table-col:nth-of-type(8) > p.b-fight-details__table-text:nth-of-type(1)").get(0); 
                fightOutcomes.add(outcome.text());
            }
       }
   }
  
 
}
