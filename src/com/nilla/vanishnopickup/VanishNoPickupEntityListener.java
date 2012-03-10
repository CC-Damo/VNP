package com.nilla.vanishnopickup;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

/**
 * Handle events for Entities
 * 
 * @author EvilNilla
 * Code borrowed from github.com/fullwall/Friendlies
 */
public class VanishNoPickupEntityListener implements Listener 
{
	private final VanishNoPickup plugin;

	public VanishNoPickupEntityListener(VanishNoPickup instance)
	{
		plugin = instance;
	}

	/*
	 * This code graciously ripped off of the Friendlies plugin by fullwall:
	 * github.com/fullwall/Friendlies
	 * 
	 * We disregard all his permissions checks and 
	 *    just cancel if allowed and invisible.
	 */
	@EventHandler
	public void onEntityTarget(EntityTargetEvent e) {
		
		//Don't bother with non players
		if (!(e.getTarget() instanceof Player))
			return;
		
		Player player = (Player)e.getTarget();
		
		//Make sure this player is invisible
		if (!plugin.isPlayerInvisible(player.getName()))
			return;

		//Check the permissions
		if (player.hasPermission("vanish.noaggromobs") || player.isOp())
			return;
		
		//Make sure it's a hostile mob
		//LivingEntity le = (LivingEntity) e.getEntity();
		
		//Get the name
		/*String name = checkMonsters(le);
		
		//If it's not in our list, exit
		if (name.isEmpty())
			return;
		*/
		
		//We've passed all checks, cancel the event
		e.setCancelled(true);
		return;
		//}
		//return;
	}
/*
	public String checkMonsters(LivingEntity le) {
		String name = "";
		if (le instanceof Chicken) {
			name = "chicken";
		} else if (le instanceof Cow) {
			name = "cow";
		} else if (le instanceof Creeper) {
			name = "creeper";
		} else if (le instanceof Ghast) {
			name = "ghast";
		} else if (le instanceof Giant) {
			name = "giant";
		} else if (le instanceof Pig) {
			name = "pig";
		} else if (le instanceof PigZombie) {
			name = "pigzombie";
		} else if (le instanceof Monster) {
			name = "monster";
		} else if (le instanceof Sheep) {
			name = "sheep";
		} else if (le instanceof Skeleton) {
			name = "skeleton";
		} else if (le instanceof Slime) {
			name = "slime";
		} else if (le instanceof Spider) {
			name = "spider";
		} else if (le instanceof Squid) {
			name = "squid";
		} else if (le instanceof Wolf) {
			name = "wolf";
		} else if (le instanceof Zombie) {
			name = "zombie";
		}
		return name;
	}*/
}
