package com.kodingkingdom.hunt;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.material.Directional;

import com.kodingkingdom.makehistory.HuntPlugin;
import com.kodingkingdom.commandline.kinds.SilentCommandLine;

//TODO: make problem data immutable wrt problem
public class Problem implements Runnable{
	ProblemData problem_data;
	Location chest_location;

	HashMap<UUID, String> attempts = new HashMap<UUID, String> ();
	
	
	
	
	public static boolean valid_chest (Block b) {
		return b .getType () .equals (Material .TRAPPED_CHEST);
	}
	
	

	private Problem (Chest chest) {
		if (! Problem .valid_chest (chest .getBlock ())) {
			throw new RuntimeException ("not a chest");
		}
		
		this .chest_location = chest .getLocation ();
		this .problem_data = new ProblemData (chest .getInventory ());
	}

	

	public static Problem live (Chest chest) {
		try {
			Problem problem = new Problem (chest);
			problem .plate_location() .getBlock() .setType (Material.GOLD_PLATE);
			problem .run ();
			return problem;
		}
		catch (Throwable t) {
			t .printStackTrace();
			return null;
		}
	}
	public void die () {
		this .plate_location() .getBlock() .setType (Material.AIR);
		this .attempts = null;
	}
	
	@Override
	public void run () {
		int plate_x = this .plate_location() .getBlockX();
		int plate_y = this .plate_location() .getBlockY();
		int plate_z = this .plate_location() .getBlockZ();
		
		if (Bukkit.getOnlinePlayers().size() > 0) {
			Player pawn = Bukkit.getOnlinePlayers().iterator().next();
			SilentCommandLine.eval(pawn, Arrays.asList (
					"particle portal " + plate_x + " " + plate_y + " " + plate_z + " 0.3 0.3 0.6 0.15 3",
					"particle fireworksSpark " + plate_x + " " + plate_y + " " + plate_z + " 0.3 0.3 0.6 0.15 3"));
		}

		if (this .attempts != null)
			HuntPlugin.getPlugin().scheduleTask(this, 2);	
	}
	
	public void set_problem_data (ProblemData p) {
		this .problem_data = p;
	}
	public Location chest_location () {
		return chest_location .clone () .add (0, +1, 0);
	}
	public Location plate_location () {
		BlockFace front = ((Directional) chest_location .getWorld () .getBlockAt (chest_location) .getState () .getData ()) .getFacing ();
		return chest_location .getBlock () .getRelative (front) .getLocation ();
	}
	
	
	public void test (Player p) {
		Room venue = HuntPlugin.getPlugin().getHunt() .free_room ();
		venue .test (this, p);
		p .teleport (venue .reference);
	}
	void tested (Player p, String result) {
		if (this .attempts != null) {
			this .attempts .put (p .getUniqueId (), result);
		}
		if (result == this .problem_data .answer) {
			//org.bukkit.command.BlockCommandSender
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + p .getName() + " title \"" + ChatColor .GOLD + "正確！\"");
		}
		else {
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + p .getName () + " title \"" + ChatColor .RED + "不正確。。。\"");
		}
		p .teleport (this .chest_location .clone () .add (0, +1, 0));
	}
}
