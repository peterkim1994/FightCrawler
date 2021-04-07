/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Models;

import java.time.LocalDate;

/**
 *
 * @author peter
 */
public class UFCEvent {    
    private int id;
    public String country;
    public LocalDate date;
    public String EventName;
    
    public void setId(int id){
        this.id = id;
    }
    
    public int getId(){
        return id;
    }
}
