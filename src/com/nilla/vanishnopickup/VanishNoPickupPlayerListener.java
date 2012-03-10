package com.nilla.vanishnopickup;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;


/**
 * Handle events for all Player related events
 * 
 * @author Nodren
 * @updated EvilNilla
 */
public class VanishNoPickupPlayerListener implements Listener
{
	private final VanishNoPickup plugin;

	public VanishNoPickupPlayerListener(VanishNoPickup instance)
	{
		plugin = instance;
	}
        @EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
            vanishForJoinOrRespawn(event.getPlayer(), true);
        }
        @EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
            vanishForJoinOrRespawn(event.getPlayer(), false);
        }
                
        private void vanishForJoinOrRespawn(Player player, boolean notify){
		if(plugin.isPlayerNP(player.getName())){
                    if(notify){
			player.sendMessage(ChatColor.RED + "You have item pickups disabled!");
                    }
		}

		if(plugin.isPlayerInvisible(player.getName())){
                        if(notify){
                            player.sendMessage(ChatColor.RED + "You are currently invisible!");    
                        }
			
			plugin.vanish(player);
		}
                
                //Make it so random players can't relog to see vanished ppl
                plugin.scheduler.scheduleSyncDelayedTask(plugin, new PlayerInvisibleTimerTask(player));
	}

        @EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		if (event.isCancelled())
			return;
                
                Location locFrom = event.getFrom();
                Location locTo = event.getTo();

		Player player = event.getPlayer();
                //No more processing if the player isn't invisible
                if (!plugin.isPlayerInvisible(player.getName())){
                  plugin.scheduler.scheduleSyncDelayedTask(plugin, new TPInvisibleTimerTask(player, locTo, false), 10);
                  return;
                }
                
                
                
                long distance_squared = plugin.getDistanceSquared(locFrom, locTo);
                boolean bDifferentWorld = false;
                
                if(locFrom.getWorld().getName() !=  locTo.getWorld().getName()){
                    bDifferentWorld = true;
                }
                
                VanishTeleportInfo tpi = null;
                int iLocation = -1;
                for(int i = 0; i < plugin.teleporting.size() - 1; i++){
                    if(plugin.teleporting.get(i).name == player.getName()){
                        tpi = plugin.teleporting.get(i);
                        iLocation = i;
                        break;
                    }
                }
                
                boolean bTeleportUp = false;
                boolean bPlayerClose = false;
                
                List<Player> lPlayers = locTo.getWorld().getPlayers();
                for (Player worldPlayer : lPlayers)
		{
                    if(plugin.getDistanceSquared(locTo, worldPlayer.getLocation()) < 25)
                    {
                        bPlayerClose = true;
                        break;
                    }
                }
                //Only teleport up if someone is really close to where we're landing and we're TPing pretty far(> 80)
                //Set distance > 125 means they probably don't have us loaded
                //6400 = 80^2
                bTeleportUp = (bDifferentWorld || ((distance_squared >= 6400) && bPlayerClose));
                
                //if we don't find their teleport info, we should add it
                if ((tpi == null) && bTeleportUp){           
                    //plugin.log.info("Cancelling Teleport for player and moving to top:" + player.getName());
                    //Add them to our teleporting list 
                    plugin.teleporting.add(new VanishTeleportInfo(player.getName(), locTo));
                    
                    //Don't cancel the event, just move them to the top
                   // player.sendMessage(ChatColor.RED + "TP'd to 127y because you're invisible.");
                    Location new_location = new Location(locTo.getWorld(), locTo.getX(), 127, locTo.getZ());
                    event.setTo(new_location);
                    //event.setCancelled(true);
                    
                    //Make it so the player doesn't take damage for one second. 
                    //This is in case they spawn within a block
                    player.setNoDamageTicks(40); 
                    //Fire a scheduled task to TP the player to the original location
                    //Bumped this up to 1.5 seconds
                    plugin.scheduler.scheduleSyncDelayedTask(plugin, new TPInvisibleTimerTask(player, locTo, true), 30);
                    return;
                }                        
                else {
                    // plugin.log.info("Should be at final destination:" + player.getName());
                    //Remove the player from the TPing list and update their invisibile info
                    if(iLocation >= 0){
                        plugin.teleporting.remove(iLocation);
                    }
                    plugin.updateInvisibleForPlayerDelayed(player);
                
                }
                    
                
                //We could cancel the event and TP the player to above the world where they're TPing
                //Then WAIT 2 seconds and TP them to exactly where they wanted.
                //2 seconds is completely arbitrary, but we want them to be invisible to the 
                //Player that they're TPing to.
                //We might need some metadata to be stored(is being moved) because we're going to end 
                //up inside of this event again if we TP the player a 2nd time.

		// Make it so this player can't see anyone invisible around them
		// Make it so no one around this player will see them if they're invisible
	
	}
	
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event){
		
		Player player = event.getPlayer();
	
		if(plugin.isPlayerNP(player.getName())) {
			event.setCancelled(true);
		}
		
	}
        
       private class TPInvisibleTimerTask implements Runnable
        {
            protected Player m_player;
            protected Location m_loc;
            protected boolean m_invis;

            public TPInvisibleTimerTask(Player player, Location location, boolean bInvisible)
            {
                m_player = player;
                m_loc = location;
                m_invis = bInvisible;
            }

            public void run()
            {
                if(m_invis){
                World world = m_player.getWorld();
                if(world.getPlayers().contains(m_player)){
                    //reset the players fall distance
                    m_player.setFallDistance(0);
                    m_player.teleport(m_loc);
                    plugin.updateInvisibleForPlayer(m_player);
                }
                }
                else{
                    plugin.updateInvisible(m_player);
                }
            }

        } 
       private class PlayerInvisibleTimerTask implements Runnable
        {
            protected Player m_player;

            public PlayerInvisibleTimerTask(Player player)
            {
                m_player = player;
            }

            public void run()
            {
                plugin.updateInvisible(m_player);                
            }

        } 
	
}
