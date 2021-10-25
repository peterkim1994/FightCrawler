/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fightcrawler;

import Models.Fight;
import Models.UFCEvent;
import utils.Cleaner;
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
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.devUtils;

/**
 *
 * @author peter
 */
public class EventCrawler {

    private DbSingleton db;
    private FighterProfileScrapper fighterScrapper;
    private FightCrawler fightScrapper;
    
    public EventCrawler() {
        try {
            db = new DbSingleton();
            fighterScrapper = new FighterProfileScrapper(db);
            fightScrapper = new FightCrawler(db, fighterScrapper);
        } catch (SQLException ex) {
            Logger.getLogger(EventCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        EventCrawler crawler = new EventCrawler();
       // crawler.scrapeEvent(3);
     //   crawler.scrapeEvent(4);
        for (int i=1; i<=1; i++){
            crawler.scrapeEvent(i);
          //  Scanner s = new Scanner(System.in);     
        //    System.out.println("Scrape next page?:");
        }
    }

    public void scrapeEvent(int page) {
        scrapeEvent(page, true);
    }
    //Crawls the main event listings page for all events contained on webpage

    public void scrapeEvent(int page, boolean previousEvent) {
        try {
            String url = "http://www.ufcstats.com/statistics/events/completed?page=" + page;
            Document eventsPage = Jsoup.connect(url).get(); // URL shortened!
            Elements names = eventsPage.getElementsByClass("b-link b-link_style_black");
            int x = 0;
            for (Element name : names) {     
                  x++;
                  if(x!=1 && page ==1)
                    scrapeEventPage(name.attr("href"), true);        
            }

        } catch (IOException ex) {
            Logger.getLogger(EventCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //crawls a single event
    public void scrapeEventPage(String url, boolean isPreviousEvent) throws IOException{//num attendence vs num fights to scrape  
       // url = "http://www.ufcstats.com/event-details/805ad1801eb26abb";
        Document eventPage = Jsoup.connect(url).get();
        Elements eventDetails = eventPage.getElementsByClass("b-list__box-list-item");
        
        UFCEvent event = new UFCEvent();
        getEventDetails(eventDetails, eventPage, event);

        int numChampionFights = eventPage.getElementsByAttributeValue("src", "http://1e49bc5171d173577ecd-1323f4090557a33db01577564f60846c.r80.cf1.rackcdn.com/belt.png").size();
        if (numChampionFights == 0) {//main event is always 5 rounds even though its not a title fight
            numChampionFights = 1;
        }

        Elements fights = eventPage.getElementsByClass("b-fight-details__table-row b-fight-details__table-row__hover js-fight-details-click");
        for (int i = 0; i < fights.size(); i++) {
            Element fight = fights.get(i);
            Fight eventFight = new Fight(event.getId());
            if (i <= numChampionFights) {
                eventFight.rounds = 5;
                numChampionFights--;
            }else{
                eventFight.rounds = 3;
            }
            String fightLink = fight.attr("data-link");
            Element fightRow = eventPage.getElementsByClass("b-fight-details__table-row b-fight-details__table-row__hover js-fight-details-click").get(i);
            Elements cols = fightRow.getElementsByClass("b-fight-details__table-text");
            eventFight.method = cols.get(cols.size() - 4).text().trim();
            eventFight.weightClass = cols.get(cols.size() - 5).ownText().trim();

            Elements fighters = fight.getElementsByClass("b-link b-link_style_black");            
            fightScrapper.scrapeFight(fightLink, eventFight);
        }

    }
     

    public void getEventDetails(Elements eventDetails, Document eventPage, UFCEvent event) {
        String eventDate = Cleaner.splitThenExtract(eventDetails.get(0), ":", 1);
        event.date = Cleaner.reformatDate(eventDate);
        event.country = Cleaner.splitThenExtract(eventDetails.get(1), ",", -1);
        event.EventName = eventPage.getElementsByClass("b-content__title-highlight").get(0).text();
        System.out.println(event.EventName);
        db.recordEvent(event);
    }

}
