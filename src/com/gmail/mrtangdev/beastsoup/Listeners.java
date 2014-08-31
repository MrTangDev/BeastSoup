package com.gmail.mrtangdev.beastsoup;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

//woah everything is not cramped in the main class
public class Listeners implements Listener {

    private final int soupHealAmount = BeastSoup.plugin.soupHealAmount;

    //Removes /soup cooldown
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
	Player p = e.getEntity();
	if (BeastSoup.plugin.cooldown.contains(p.getName())) {
	    BeastSoup.plugin.cooldown.remove(p.getName());
	}
    }

    //Different interact events
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
	Player p = e.getPlayer();

	//Open a double chest full of soup
	if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
	    if (e.getClickedBlock().getState() instanceof Sign) {
		Sign sign = (Sign) e.getClickedBlock().getState();
		if (sign.getLine(0).equalsIgnoreCase(ChatColor.DARK_PURPLE + "[" + ChatColor.GREEN + "Soup" + ChatColor.DARK_PURPLE + "]")) {

		    Inventory inv = Bukkit.createInventory(null, 54, ChatColor.LIGHT_PURPLE + "Soup");
		    ItemStack soup = new ItemStack(Material.MUSHROOM_SOUP);
		    for (int i = 0; i < 54; i++) {
			inv.addItem(new ItemStack[] { soup });
		    }
		    p.openInventory(inv);
		}
	    }
	}
	
	if (p.getItemInHand() == null) return; //dumb check
	
	//Instant soup
	if (p.getItemInHand().getType() == Material.MUSHROOM_SOUP) {
	    if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
		if (!(p.getHealth() == p.getMaxHealth())) {
		    e.setCancelled(true);

		    p.playSound(p.getLocation(), Sound.BURP, 10, 1);
		    p.setHealth(p.getHealth() + soupHealAmount > p.getMaxHealth() ? p.getMaxHealth() : p.getHealth() + soupHealAmount);
		    p.getItemInHand().setType(Material.BOWL);
		}
	    }


	    //Instant Food
	    int food = p.getFoodLevel();
	    if (food < 20 && p.getHealth() == p.getMaxHealth()) {
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
		    e.setCancelled(true);
		    int nfood = food + 7;

		    if (nfood > 20) {
			p.setFoodLevel(20);
			p.playSound(p.getLocation(), Sound.BURP, 10, 1);
			p.getItemInHand().setType(Material.BOWL);
		    }
		    if (food <= 20) {
			p.setFoodLevel(nfood);
			p.playSound(p.getLocation(), Sound.BURP, 10, 1);
			p.getItemInHand().setType(Material.BOWL);
		    }
		}
	    }
	}

    }

    //Make soup signs fancy, requires permissions
    @EventHandler
    public void onSignChange(SignChangeEvent e) {
	if ((e.getLine(0).equalsIgnoreCase("[Soup]")) && 
		(e.getPlayer().hasPermission("beastsoup.sign") || e.getPlayer().isOp())) {
	    e.setLine(0, ChatColor.DARK_PURPLE + "[" + ChatColor.GREEN + "Soup" + ChatColor.DARK_PURPLE + "]");
	}
    }
}
