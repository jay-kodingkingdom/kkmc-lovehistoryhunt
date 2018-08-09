package com.kodingkingdom.makehistory;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;

import com.kodingkingdom.hunt.Problem;

public enum HuntConfig {

	OPEN("makehistoryhunt.open",ConfigType.SETTING),
	LOCATIONS("makehistoryhunt.locations",ConfigType.SETTING)
	;
	
	public final String config;
	public final ConfigType configType;
	
	private HuntConfig(String Config,ConfigType ConfigType){
		config=Config;configType=ConfigType;}
		
	public static void loadConfig(){
		HuntPlugin plugin = HuntPlugin.getPlugin();
		FileConfiguration config = plugin.getConfig();
		
		try{
			for(String x : config.getStringList(LOCATIONS.config)){
				String[] args=x.split("~");
				if (args.length!=4)throw new IllegalStateException();
				HuntPlugin .getPlugin() .getHunt() .update_problems (new Location(plugin.getServer().createWorld(new WorldCreator(args[0])),
						Integer.parseInt(args[1]),
						Integer.parseInt(args[2]),
						Integer.parseInt(args[3])) .getBlock ());}
			plugin.getLogger().info("Config successfully loaded");}
		
		catch(Exception e){
			plugin.getLogger().severe("Could not load config!");
			plugin.getLogger().severe("ERROR MESSAGE: "+e.getMessage());
			e.printStackTrace();
			config.set("craftercoordinator.ERROR", true);}}
			
	
	public static void saveConfig(){
		HuntPlugin plugin = HuntPlugin.getPlugin();
		FileConfiguration config = plugin.getConfig();

		if (config.isSet("craftercoordinator.ERROR")){
			plugin.getLogger().info("Config state invalid, will not save");
			return;}
		
		try{
			for(String key : config.getKeys(false)){
				 config.set(key,null);}

			ArrayList<String> locations = new ArrayList<String>();
			for (Problem p : HuntPlugin .getPlugin() .getHunt() .problems){
				Location l = p.chest_location();
				locations.add(l.getWorld().getName()+"~"+l.getBlockX()+"~"+l.getBlockY()+"~"+l.getBlockZ());}
			config.set(LOCATIONS.config,locations);

			plugin.saveConfig();
			plugin.getLogger().info("Config successfully saved");}
		catch(Exception e){
			plugin.getLogger().severe("Could not save config!");
			plugin.getLogger().severe("ERROR MESSAGE: "+e.getMessage());
			e.printStackTrace();}}

	public enum ConfigType{
		SETTING;}}
