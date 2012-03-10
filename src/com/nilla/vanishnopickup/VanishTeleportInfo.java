/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nilla.vanishnopickup;
import org.bukkit.Location;

/**
 *
 * @author telaeris
 */
public class VanishTeleportInfo {
    public String name;
    public Location location;
    
    public VanishTeleportInfo(String sName, Location l){
        name = sName;
        location = l;
    }
}
