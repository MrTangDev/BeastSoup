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

public class Listeners implements Listener {

    //ok
    public Listeners(BeastSoup beastSoup) {
    }

    //Removes /soup cooldown
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
	Player p = (Player) e.getEntity();
	if (BeastSoup.plugin.cooldown.contains(p.getName())) {
	    BeastSoup.plugin.cooldown.remove(p.getName());
	}
    }

    //Different interact events
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
	Player p = e.getPlayer();
	//Instant soup
	if (p.getItemInHand().getType() == Material.MUSHROOM_SOUP) {
	    if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
		if (!(p.getHealth() == p.getMaxHealth())) {
		    e.setCancelled(true);
		    int soupHealAmount = BeastSoup.plugin.soupHealAmount;
		    p.setHealth(p.getHealth() + soupHealAmount > p.getMaxHealth() ? p.getMaxHealth() : p.getHealth() + soupHealAmount);
		    p.playSound(p.getLocation(), Sound.BURP, 10, 1);
		    p.getItemInHand().setType(Material.BOWL);
		}
	    }
	}
	//Instant Food
	int food = p.getFoodLevel();
	if (food < 20 && p.getHealth() == p.getMaxHealth()) {
	    if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
		if (p.getItemInHand().getType() == Material.MUSHROOM_SOUP) {
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
	//Create a double chest full of soup
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
    }

    //Make soup signs fancy
    @EventHandler
    public void onSignChange(SignChangeEvent e) {
	if (e.getLine(0).equalsIgnoreCase("[Soup]")) {
	    e.setLine(0, ChatColor.DARK_PURPLE + "[" + ChatColor.GREEN + "Soup" + ChatColor.DARK_PURPLE + "]");
	}
    }
    //At least the listeners got a class for itself
}
