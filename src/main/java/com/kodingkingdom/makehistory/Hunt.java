package com.kodingkingdom.makehistory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.kodingkingdom.hunt.Problem;
import com.kodingkingdom.hunt.ProblemData;
import com.kodingkingdom.hunt.Room;

public class Hunt implements Listener/*, CommandExecutor*/ {

	List <Room> rooms = new ArrayList <Room> ();
	public Room free_room () {
		for (Room room : rooms) {
			if (room .is_free ())
				return room;
		}
		throw new RuntimeException ("No rooms available!");
	}
	
	
	
	//add initializer for rooms;
	List <Problem> problems = new ArrayList <Problem> ();
	
	
	HuntPlugin plugin;	
	public Hunt(HuntPlugin Plugin){plugin=Plugin;}
	
	public void Live(){		
		plugin.registerEvents(this);
		HuntConfig.loadConfig();
		
		/*World w = Bukkit.getServer().getWorld("spawn");
				
		for (int i = -50; i <= 50; i ++) {
			rooms .add (new Room (new Location (w, -135 + 9 * i, 2, 139)));
		}*/
		
		//plugin.getCommand("hunt").setExecutor(this);
		//add rehydration code
	}	
	public void Die(){
		HuntConfig.saveConfig();}
	

	@EventHandler
    public void get_tested (PlayerInteractEvent e) {
        Block clicked = e.getClickedBlock();
        
	    if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
	        if (clicked .getType () == Material .STONE_BUTTON) {
//HuntPlugin.debug("recieved problem attempt");
	        	
	            int index = -1;
	            int number = -1;
	            for (int i = 0; i < this .rooms .size (); i ++) {
	            		Room r = this .rooms .get (i);
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
					Room room = this .rooms .get (index);
					room .tested (room .recognize_answer_index (number));
	            }
	        }
	    }
	}
	
	
	
	
	@EventHandler
    public void update_problems (InventoryCloseEvent e) {
		if (e .getInventory () .getLocation () != null && e .getInventory () .getLocation () .getBlock () != null) 
			update_problems (e .getInventory () .getLocation () .getBlock ());
	}
	void update_problems (Block block) {
        if (block .getType () == Material .TRAPPED_CHEST) {

        		Problem known = null;
        		ProblemData data = ProblemData .from (((Chest) block .getState ()) .getInventory ());
        		
        		
        		for (Problem p : this .problems) {
        			if (p .chest_location () .equals (block .getLocation ())) {
        				known = p;
        				break;
        			}
        		}
        		
        		
        		if (known != null && data != null) {
        			this .remove_problem(known);
        			this .add_problem (block .getLocation ());
        		}
        		else if (known != null && data == null) {
        			this .remove_problem(known);
        		}
        		else if (known == null && data != null) {
        			this .add_problem (block .getLocation ());
        		}
        }
	}
	
	@EventHandler
    public void protect_plates (BlockBreakEvent e) {
        if(e.getBlock().getType().equals(Material.GOLD_PLATE)){
        		for (Problem p : this .problems) {
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
            		for (Problem p : this .problems) {
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
        		for (Problem p : this .problems) {
        			if (p .chest_location () .equals(e .getBlock() .getLocation())) {
        				p .die ();
        				return;
        			}
        		}
        }
	}
	
	void add_problem (Location l) {
		Chest c = (Chest) l .getBlock() .getState ();
		Problem p = Problem .live(c);
		if (p != null) {
			this .problems .add (p);
		}
	}
	void remove_problem (Problem p) {
		if (p != null) {
			p .die ();
			this .problems .remove (p);
		}
	}
}
