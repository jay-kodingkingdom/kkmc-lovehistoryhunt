package com.kodingkingdom.makehistory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.kodingkingdom.hunt.Problem;
import com.kodingkingdom.hunt.ProblemData;
import com.kodingkingdom.hunt.Room;

public class Hunt implements Listener/*, CommandExecutor*/ {

	static List <Room> rooms = new ArrayList <Room> ();
	public static Room free_room () {
		for (Room room : rooms) {
			if (room .is_free ())
				return room;
		}
		throw new RuntimeException ("No rooms available!");
	}
	
	
	
	//add initializer for rooms;
	public static List <Problem> problems = new ArrayList <Problem> ();
	
	
	HuntPlugin plugin;	
	public Hunt(HuntPlugin Plugin){plugin=Plugin;}
	
	public void Live(){		
		//Logger.getGlobal().setFilter(new HuntFilter());
		Bukkit .getLogger() .setFilter(new Filter () {

			@Override
			public boolean isLoggable(LogRecord record) {
				return false;/*
			    if (record .getMessage () .contains ("Playing effect fireworksSpark for 3 times")) {
			        return false;
			    }
			    else if (record .getMessage () .contains ("Playing effect portal for 3 times")) {
			        return false;
			    }
			    else if (record .getMessage () .contains ("sch contend")) {
			        return false;
			    }
			    else {
			    		return true;
		        }*/
			}
			
		});

		plugin.registerEvents(this);
		
		World w = Bukkit.getServer().getWorld("spawn");
		
		w .setGameRuleValue("logAdminCommands", "false");
		w .setGameRuleValue("sendCommandFeedback", "false");

		
		for (int i = -50; i <= 50; i ++) {
			rooms .add (new Room (new Location (w, -135 + 9 * i, 2, 139)));
		}
		
		//plugin.getCommand("hunt").setExecutor(this);
		//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule logAdminCommands false");
		//add rehydration code
	}	
	public void Die(){}
	

	/*@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if (sender instanceof ConsoleCommandSender || ((Player)sender).isOp()) {
			if (args.length==3
			&& args[0] == "hunt") /*{
				try {
					int index = Integer .parseInt (args [1]);
					int answer = Integer .parseInt (args [2]);
					Room room = Hunt .rooms .get (index);
					room .tested (room .recognize_answer_index (answer));
				}
				catch (Exception e) {
					sender.sendMessage("Cannot understand command!");	
				}
			}	
			else {//*//*/
				sender.sendMessage("Cannot understand command!");	
			//*}*//*/
			return true;
		}
		else {
			return false;
		}
	}*/

	
	
	

	@EventHandler
    public void get_tested (PlayerInteractEvent e) {
        Block clicked = e.getClickedBlock();
        
	    if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
	        if (clicked .getType () == Material .STONE_BUTTON) {
//HuntPlugin.debug("recieved problem attempt");
	        	
	            int index = -1;
	            int number = -1;
	            for (int i = 0; i < Hunt .rooms .size (); i ++) {
	            		Room r = Hunt .rooms .get (i);
	            		int r_num = r .recognize_answer_location(clicked .getLocation());
	            		if (r_num != -1) {
	            			index = i;
	            			number = r_num;
	            			break;
	            		}
	            }
//HuntPlugin.debug("try resolve room index:" + index);
//HuntPlugin.debug("try resolve room number:" + number);
	            
	            if (index != -1) {
	            		//Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "hunt " + index + " " + number);
					Room room = Hunt .rooms .get (index);
					room .tested (room .recognize_answer_index (number));
	            }
	        }
	    }
	}
	
	
	
	
	@EventHandler
    public void update_problems (InventoryCloseEvent e) {
		update_problems (e .getInventory () .getLocation () .getBlock ());
	}
	void update_problems (Block block) {
        if (block .getType () .equals (Material .TRAPPED_CHEST)) {

        		Problem known = null;
        		ProblemData data = ProblemData .from (((Chest) block .getState ()) .getInventory ());
        		
        		
        		for (Problem p : Hunt .problems) {
        			if (p .chest_location () .equals (block .getLocation ())) {
        				known = p;
        				break;
        			}
        		}
        		
        		
        		if (known != null && data != null) {
        			known .die ();
        			Problem .live ((Chest) block .getState ());
        		}
        		else if (known != null && data == null) {
        			known .die ();
        		}
        		else if (known == null && data != null) {
        			Problem .live ((Chest) block .getState ());
        		}
        }
	}
	
	@EventHandler
    public void protect_plates (BlockBreakEvent e) {
        if(e.getBlock().getType().equals(Material.GOLD_PLATE)){
        		for (Problem p : Hunt .problems) {
        			if (p .plate_location() .equals(e .getBlock() .getLocation())) {
        				e.setCancelled(true);
        				return;
        			}
        		}
        }
	}

	@EventHandler
    public void trigger_problem (PlayerInteractEvent e) {
        if(e.getAction().equals(Action.PHYSICAL)){
            if(e.getClickedBlock().getType().equals(Material.GOLD_PLATE)){
            		for (Problem p : Hunt .problems) {
            			if (p .plate_location() .equals(e .getClickedBlock() .getLocation())) {
            				p .test (e .getPlayer());
            				return;
            			}
            		}
            }
        }
	}
	
	@EventHandler
    public void problem_destroyed (BlockBreakEvent e) {
        if (e.getBlock().getType().equals(Material.TRAPPED_CHEST)){
        		for (Problem p : Hunt .problems) {
        			if (p .chest_location () .equals(e .getBlock() .getLocation())) {
        				p .die ();
        				return;
        			}
        		}
        }
	}
}
