/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fightcrawler;

import Models.Fight;
import Models.Fighter;
import Models.UFCEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import utils.devUtils;

/**
 *
 * @author peter
 */
public class FightCrawler {

    /**
     * @param args the command line arguments
     */
    private DbSingleton db;
    private FighterProfileScrapper fightScrapper;

    public FightCrawler(DbSingleton db, FighterProfileScrapper fightScrapper) {
        this.db = db;
        this.fightScrapper = fightScrapper;
    }

    public void scrapeFight(String fightUrl, UFCEvent event) {
        try {
            Document page = Jsoup.connect(fightUrl).get();
            Elements fighterDetails = page.getElementsByClass("b-fight-details__person");  
            
            Fighter fighter1 = scrapeFighter(fighterDetails, true);                       
            Fighter fighter2 = scrapeFighter(fighterDetails, false);         
            
            Fight fight = new Fight(event.getId());
            String fighter1Outcome = fighterDetails.get(0).getElementsByClass("b-fight-details__person-status").text();
            assignFightOutcome(fighter1Outcome, fight);

//            /b-fight-details__label
            String infoHeader = page.getElementsByClass("b-fight-details__fight-title").get(0).text().trim();
            String gender = (infoHeader.contains("Women")) ? "female" : "male";

            Elements fightInfo = page.getElementsByClass("b-fight-details__text-item");
            String method = fightInfo.get(0).nextElementSibling().outerHtml();
//            String round =   fightInfo.get(1).nextElementSibling().outerHtml();
       //     String time = fightInfo.get(2).nextElementSibling().outerHtml();
            System.out.println(method + " " ); //+ round +  " " + time);
            devUtils.printElmOwnText(fightInfo);

        } catch (IOException ex) {
            Logger.getLogger(EventCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Fighter scrapeFighter(Elements fighterHeaders, boolean redGloves) throws IOException {
        int index = (redGloves) ? 0 : 1;
        String fighterName = fighterHeaders.get(index).getElementsByClass("b-fight-details__person-name").text().trim();
        String fighterProfileLink = fighterHeaders.get(index).getElementsByClass("b-link b-fight-details__person-link").get(0).attr("href");      
        System.out.println(fighterProfileLink);
        Fighter fighter = new Fighter(fighterName);
        fightScrapper.scrapeFighterProfile(fighterProfileLink);        
        System.out.println("\n" + fighterName + " " + fighterProfileLink + "\n");
        return fighter;
    }

    public void assignFightOutcome(String fighter1Outcome, Fight fight) {
        if (fighter1Outcome.contains("W")) {
            fight.winner = 1;
        } else if (fighter1Outcome.contains("L")) {
            fight.winner = 2;
        } else {
            fight.winner = 0;
        }
    }

    public void checkForBonus(Elements fighterDetails, Fight fight) {
        String rawHtml = fighterDetails.get(0).getElementsByClass("b-fight-details__fight-title").outerHtml();
        if (rawHtml.contains("rackcdn.com/perf.png")) {

        } else if (rawHtml.contains("rackcdn.com/fight.png")) {

        } else {

        }
    }

}
