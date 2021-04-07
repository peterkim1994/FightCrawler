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
public class Fighter {
    private int id;
    public String name;
    public String stance;
    public int reach;
    public int weight;
    public int height;
    public int DOB;
    public String country;    
    
    public Fighter(String name){
        this.name = name;
    }
    
    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return id;
    }
}
