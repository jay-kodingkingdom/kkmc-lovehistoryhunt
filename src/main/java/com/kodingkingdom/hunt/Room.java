package com.kodingkingdom.hunt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.kodingkingdom.makehistory.HuntPlugin;

public class Room {
	Player player = null;
	List <String> plan = null;
	Problem commitment = null;
	Location reference;
	
	String [] scrambled_answers;
	
	public Room (Location reference) {
		this .reference = reference .clone();
	}
	public int recognize_answer_location (Location loc) {
		if (loc .equals(reference .clone () .add (-3, 1, 2))) {
			return 0;
		}
		else if (loc .equals(reference .clone () .add  (-1, 1, 2))) {
			return 1;
		}
		else if (loc .equals(reference .clone () .add  (1, 1, 2))) {
			return 2;
		}
		else if (loc .equals(reference .clone () .add  (3, 1, 2))) {
			return 3;
		}
		else {
			return -1;
		}
	}
	public String recognize_answer_index (int index) {
		return this .scrambled_answers [index];
	}
	public boolean is_free () { return this .commitment == null; }
	public void test (Problem problem, Player player) {
		if (commitment != null) {
			throw new RuntimeException ("already commited to other problem");
		}
		
		this .commitment = problem;
		List <String> plan = new ArrayList <String> ();
		plan .add (problem .problem_data .traps [0]);
		plan .add (problem .problem_data .traps [1]);
		plan .add (problem .problem_data .traps [2]);
		plan .add (problem .problem_data .answer);
		java .util .Collections .shuffle (plan);
		this .plan = plan;
		this .player = player;
		this .setup (problem .problem_data);
	}
	
	void write_sign (Block block, String text) {
		BlockState state = block.getState();
		if (!(state instanceof Sign)) {
		    throw new RuntimeException ("not a sign");
		}
		Sign sign = (Sign) state;
		try {
			sign.setLine(0, text);
			sign.update();
		}
		catch (Throwable t) {}
		
		//add cases for text too long
	}
	void scramble_answers () {
		List <String> scrambler = new ArrayList <String> ();
		scrambler .addAll (Arrays .asList (this .commitment .problem_data .traps));
		scrambler .add (this .commitment .problem_data .answer);
		Collections .shuffle (scrambler);
		this .scrambled_answers = new String [] {
			scrambler .get (0),
			scrambler .get (1),
			scrambler .get (2),
			scrambler .get (3)
		};
	}
	void setup (ProblemData p) {
		write_sign (reference .getBlock () .getRelative (0, 2, 0), p .question);
		
		this .scramble_answers ();
		
		write_sign (reference .getBlock () .getRelative (-3, 2, 2), this .scrambled_answers [0]);
		write_sign (reference .getBlock () .getRelative (-1, 2, 2), this .scrambled_answers [1]);
		write_sign (reference .getBlock () .getRelative (1, 2, 2), this .scrambled_answers [2]);
		write_sign (reference .getBlock () .getRelative (3, 2, 2), this .scrambled_answers [3]);

		/*reference .getBlock () .getState () .update (true);

		HuntPlugin.getPlugin() .scheduleTask (new Runnable () {public void run () {
			reference .clone() .add(0, 1, 0) .getBlock () .getState () .update (true);
		}}, 500);
		HuntPlugin.getPlugin() .scheduleTask (new Runnable () {public void run () {
			reference .clone() .add(0, 2, 0) .getBlock () .getState () .update (true);
		}}, 1000);
		HuntPlugin.getPlugin() .scheduleTask (new Runnable () {public void run () {
			reference .clone() .add(0, 2, 1) .getBlock () .getState () .update (true);
		}}, 1500);
		HuntPlugin.getPlugin() .scheduleTask (new Runnable () {public void run () {
			reference .clone() .add(0, 1, 1) .getBlock () .getState () .update (true);
		}}, 2000);*/
	}
	
	
	
	
	public void tested (String result) {
		this .commitment .tested (this .player, result);
		this .commitment = null;
		this .plan = null;
		this .player = null;
	}
}
