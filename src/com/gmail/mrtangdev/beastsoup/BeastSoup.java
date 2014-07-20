package com.gmail.mrtangdev.beastsoup;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BeastSoup extends JavaPlugin {

    public static BeastSoup plugin;

    //Default configuration
    private int soupAmount = 8;
    public int soupHealAmount = 7;
    private long soupCooldown = 30;
    private boolean customRecipes = true;
    private boolean soupDrawback = true;

    @Override
    public void onEnable() {
	plugin = this;
	Bukkit.getServer().getPluginManager().registerEvents(new Listeners(this), this); //Register the events

	//Configuration
	if (!this.getDataFolder().exists()) {
	    this.getDataFolder().mkdirs();
	}
	getConfig().options().copyDefaults(true);
	saveConfig();

	soupAmount = getConfig().getInt("soup-amount");
	soupHealAmount = getConfig().getInt("soup-heal");
	soupCooldown = getConfig().getLong("soup-cooldown");
	customRecipes = getConfig().getBoolean("soup-recipes");
	soupDrawback = getConfig().getBoolean("soup-drawback");

	//Load the recipes
	if (customRecipes) {
	    crecipe();
	    mrecipe();
	}
    }


    @Override
    public void onDisable() {
	plugin = null;
    }

    ArrayList<String> cooldown = new ArrayList<String>();

    //Different commands
    public boolean onCommand(CommandSender sender, Command cmd, String commandlabel, String[] args) {
	if (cmd.getName().equalsIgnoreCase("BeastSoup")) { 
	    if (args.length == 0) {
		sender.sendMessage(ChatColor.BLUE + "BeastSoup" + ChatColor.GRAY + " made by " + ChatColor.GOLD + "MrTang/Appelsinol");
		sender.sendMessage(ChatColor.DARK_PURPLE + "Use Mushroom soup to instantly heal up some hearts! Heal hunger when you are at full health.");
		sender.sendMessage(ChatColor.ITALIC + "Use /soup to get 8 more soup.");
		sender.sendMessage(ChatColor.GRAY + "For more info use ¡ìf/beastsoup info");
		return true;
	    } else if (args.length == 1) {
		//Shows the current configuration file
		if (args[0].equalsIgnoreCase("options")) {
		    sender.sendMessage(ChatColor.RED + "Current options/configuration:" + ChatColor.GRAY + " (editable in config.yml for operators)");
		    sender.sendMessage(ChatColor.DARK_GRAY + "Soup amount from command: " + "¡ì9" + soupAmount);
		    sender.sendMessage(ChatColor.DARK_GRAY + "Amount of health from soup: " + "¡ì9" + soupHealAmount);
		    sender.sendMessage(ChatColor.DARK_GRAY + "Soup cooldown on command: " + "¡ì9" + soupCooldown);
		    sender.sendMessage(ChatColor.DARK_GRAY + "Custom soup recipes status: " + "¡ì9" + customRecipes);
		    sender.sendMessage(ChatColor.DARK_GRAY + "Drawbacks + waitingtime when /soup: " + "¡ì9" + soupDrawback);
		    return true;
		    //Reloads the configuration file
		} else if (args[0].equalsIgnoreCase("reload")) {
		    if (sender.isOp()) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "BeastSoup " + ChatColor.GRAY + "has been reloaded.");
			reloadConfig();
			saveConfig();
			soupAmount = getConfig().getInt("soup-amount");
			soupHealAmount = getConfig().getInt("soup-heal");
			soupCooldown = getConfig().getLong("soup-cooldown");
			customRecipes = getConfig().getBoolean("soup-recipes");
			soupDrawback = getConfig().getBoolean("soup-drawback");
			return true;
		    } else {
			sender.sendMessage(ChatColor.RED + "You don't have permission to do that (Operators only).");
			return false;
		    }
		    //Shows some info
		} else if (args[0].equalsIgnoreCase("info")) {
		    sender.sendMessage(ChatColor.DARK_GREEN + "Recipes are shapeless.");
		    sender.sendMessage(ChatColor.GREEN + "Cocoa milk recipe: " + ChatColor.GRAY + "1 cocoa bean, a bowl");
		    sender.sendMessage(ChatColor.GREEN + "Cacti juice recipe: " + ChatColor.GRAY + "2 cacti, a bowl");
		    sender.sendMessage(ChatColor.DARK_GRAY + "/beastsoup options will give you the configured info of the plugin");
		    return true;
		} else {
		    sender.sendMessage(ChatColor.RED + "Argument not valid - " + args[0]);
		    return false;
		}
	    }
	    return true;
	}

	if (!(sender instanceof Player)) {
	    sender.sendMessage(ChatColor.DARK_RED + "Only players can use this command.");
	    return true;
	}
	//More Soup command
	if (cmd.getName().equalsIgnoreCase("soup")) {
	    final Player p = (Player) sender;
	    if (p.hasPermission("beastsoup.soup")) {
		if (cooldown.contains(p.getName())) {
		    p.sendMessage(ChatColor.RED + "You must wait before using the /soup command again.");
		    return true;
		}
		if (soupDrawback) {
		    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 2));
		    p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 2));
		    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 2));
		    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 100, 3));
		    //Cooldown time for /soup command
		    cooldown.add(p.getName());
		    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run() {
			    //Gives soup after 5 seconds
			    for (int i = 0; i < soupAmount; i++) {
				p.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP, 1));
			    }
			    p.sendMessage(ChatColor.DARK_PURPLE + "You have been given " + soupAmount + " soup!");
			}
		    }, 100L);
		} else {
		    for (int i = 0; i < soupAmount; i++) {
			p.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP, 1));
		    }
		    p.sendMessage(ChatColor.DARK_PURPLE + "You have been given " + soupAmount + " soup!");
		}
		//Removes player from cooldown list
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
		    @Override
		    public void run() {
			if (cooldown.contains(p.getName())) {
			    cooldown.remove(p.getName());
			    p.sendMessage(ChatColor.GRAY + "You can now use the /soup command again.");
			}
		    }
		}, 20L * soupCooldown);
		return true;
	    } else {
		p.sendMessage(ChatColor.RED + "You don't have permission to do that.");
		return false;
	    }
	}
	return true;
    }

    //Cacti Juice Recipe
    private void crecipe() {
	ItemStack cjuice = new ItemStack(Material.MUSHROOM_SOUP, 1);
	ItemMeta meta = cjuice.getItemMeta();
	meta.setDisplayName("Cacti Juice");
	cjuice.setItemMeta(meta);

	ShapelessRecipe crecipe = new ShapelessRecipe(cjuice);
	crecipe.addIngredient(2, Material.CACTUS);
	crecipe.addIngredient(1, Material.BOWL);
	Bukkit.getServer().addRecipe(crecipe);
    }

    //Cocoa Milk Recipe
    @SuppressWarnings("deprecation")
    private void mrecipe() {
	ItemStack cmilk = new ItemStack(Material.MUSHROOM_SOUP, 1);
	ItemMeta meta = cmilk.getItemMeta();
	meta.setDisplayName("Cocoa Milk");
	cmilk.setItemMeta(meta);

	ShapelessRecipe mrecipe = new ShapelessRecipe(cmilk);
	mrecipe.addIngredient(1, Material.INK_SACK, 3);
	mrecipe.addIngredient(1, Material.BOWL);
	Bukkit.getServer().addRecipe(mrecipe);
    }
    //Horrible code cramped inside of one class :p 
}