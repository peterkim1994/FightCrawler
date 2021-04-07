/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Models;

/**
 *
 * @author peter
 */
public class Fight {
     public int eventId;
     public int fighter1Id;
     public int fighter2Id;
     public String weightClass;
     public int rounds;
     public int durationSeconds;
     public int winner;
     public String method;
     public Bonus bonus;
     public String gender;
     
     public Fight(int eventId){
         this.eventId = eventId;
         winner = 1;
     }
     
     
}
