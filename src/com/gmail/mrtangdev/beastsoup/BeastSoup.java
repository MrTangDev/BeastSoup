package com.gmail.mrtangdev.beastsoup;

import java.util.ArrayList;
import java.util.List;

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

    //List of players that still are still cooling down
    public List<String> cooldown = new ArrayList<String>();
    
    //Default configuration
    public int soupHealAmount = 7;
    private int soupAmount = 8;
    private long soupCooldown = 30;
    private boolean customRecipes = true;
    private boolean soupDrawback = true;

    @Override
    public void onEnable() {
	plugin = this;

	Bukkit.getServer().getPluginManager().registerEvents(new Listeners(), this); //Register the events

	//Load the configuration
	if (!this.getDataFolder().exists()) {
	    this.getDataFolder().mkdirs();
	}
	getConfig().options().copyDefaults(true);
	saveConfig();

	soupHealAmount = getConfig().getInt("soup-heal");
	soupAmount = getConfig().getInt("soup-amount");
	soupCooldown = getConfig().getLong("soup-cooldown");
	customRecipes = getConfig().getBoolean("soup-recipes");
	soupDrawback = getConfig().getBoolean("soup-drawback");

	//Load the recipes
	if (customRecipes) {
	    cactiRecipe();
	    milkRecipe();
	}
    }

    @Override
    public void onDisable() {
	cooldown.clear();
	plugin = null;
    }

    //Different commands
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandlabel, String[] args) {
	if (cmd.getName().equalsIgnoreCase("BeastSoup")) { 
	    if (args.length == 0) {
		String[] helpMsg = new String[] {
			ChatColor.BLUE + "BeastSoup" + ChatColor.GRAY + " made by " + ChatColor.GOLD + "MrTang | appelsinol",
			ChatColor.DARK_PURPLE + "Use Mushroom soup to instantly heal up some hearts! Heal hunger when you are at full health.",
			ChatColor.GRAY.toString() + ChatColor.ITALIC + "Use /soup to get more soup.",
			ChatColor.GRAY + "For more info use " + ChatColor.WHITE + "/beastsoup info"
		};
		sender.sendMessage(helpMsg);
		return true;

	    } else if (args.length == 1) {
		//Shows the current configuration file
		if (args[0].equalsIgnoreCase("options")) {
		    String[] optionMsg = new String[] {
			    ChatColor.RED + "Current options/configuration:" + ChatColor.GRAY + " (editable in config.yml for operators)",
			    ChatColor.DARK_GRAY + "Amount of health from soup: " + ChatColor.BLUE + soupHealAmount,
			    ChatColor.DARK_GRAY + "Soup amount from command: " + ChatColor.BLUE + soupAmount,
			    ChatColor.DARK_GRAY + "Soup cooldown on command: " + ChatColor.BLUE + soupCooldown,
			    ChatColor.DARK_GRAY + "Custom soup recipes status: " + ChatColor.BLUE + customRecipes,
			    ChatColor.DARK_GRAY + "Drawbacks + waitingtime when /soup: " + ChatColor.BLUE + soupDrawback
		    };
		    sender.sendMessage(optionMsg);
		    return true;

		    //Reloads the configuration file
		} else if (args[0].equalsIgnoreCase("reload")) {
		    if (sender.isOp()) {
			reloadConfig();
			saveConfig();
			soupAmount = getConfig().getInt("soup-amount");
			soupHealAmount = getConfig().getInt("soup-heal");
			soupCooldown = getConfig().getLong("soup-cooldown");
			customRecipes = getConfig().getBoolean("soup-recipes");
			soupDrawback = getConfig().getBoolean("soup-drawback");
			sender.sendMessage(ChatColor.DARK_PURPLE + "BeastSoup " + ChatColor.GRAY + "has been reloaded.");
			return true;
		    } else {
			sender.sendMessage(ChatColor.RED + "You don't have permission to do that (Operators only).");
			return true;
		    }

		    //Shows some info
		} else if (args[0].equalsIgnoreCase("info")) {
		    String[] infoMsg = new String[] {
			    ChatColor.DARK_GREEN + "Recipes are shapeless.",
			    ChatColor.GREEN + "Cocoa milk recipe: " + ChatColor.GRAY + "1 cocoa bean, a bowl",
			    ChatColor.GREEN + "Cacti juice recipe: " + ChatColor.GRAY + "2 cacti, a bowl",
			    ChatColor.DARK_GRAY + "/beastsoup options will give you the configured info of the plugin"
		    };
		    sender.sendMessage(infoMsg);
		    return true;
		} else {
		    sender.sendMessage(ChatColor.RED + "Argument not valid - " + args[0]);
		    return false;
		}
	    }
	    return false;
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

		//Checking if you get drawbacks
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

		//Using scheduler so he can get a message when it's over :(
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
		    //Removes player from cooldown list
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
		return true;
	    }
	}
	return false;
    }

    //Cacti Juice Recipe
    private void cactiRecipe() {
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
    private void milkRecipe() {
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