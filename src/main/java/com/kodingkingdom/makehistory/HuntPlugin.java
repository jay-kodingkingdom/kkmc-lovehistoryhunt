package com.kodingkingdom.makehistory;
import java.util.logging.Level;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


public class HuntPlugin extends JavaPlugin {
	public Hunt x=new Hunt(this);
	@Override
    public void onEnable(){x.Live();} 
    @Override
    public void onDisable(){x.Die();}
        
    public Hunt getHunt(){return x;}

	public void registerEvents(Listener listener){
		this.getServer().getPluginManager().registerEvents(listener, this);}
	public void deregisterEvents(Listener listener){
		HandlerList.unregisterAll(listener);}

	public int scheduleAsyncTask(Runnable task){
		return this.getServer().getScheduler().scheduleAsyncDelayedTask(this, task);}
	public int scheduleAsyncTask(Runnable task, long delay){
		return this.getServer().getScheduler().scheduleAsyncDelayedTask(this, task, delay);}
	public int scheduleTask(Runnable task, long delay){
		return this.getServer().getScheduler().scheduleSyncDelayedTask(this, task, delay);}
	public void cancelTask(int taskId){
		this.getServer().getScheduler().cancelTask(taskId);}
	
    static HuntPlugin singleton;
    public HuntPlugin(){singleton=this;}
    public static HuntPlugin getPlugin(){return singleton;}
    public static void debug(String msg){
    		singleton.getLogger().log(Level.INFO
    				, msg);}}