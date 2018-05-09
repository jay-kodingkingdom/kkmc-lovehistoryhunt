package com.kodingkingdom.hunt;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class ProblemData {
	String question;
	String answer;
	String [] traps = new String [3];

	String book_content (ItemStack book_stack) {
//Bukkit .broadcastMessage("book content has meta?" + (book_stack .getItemMeta ()));
//Bukkit .broadcastMessage("book content meta is book??" + ((BookMeta) book_stack .getItemMeta ()));
		
		return String .join ("", ((BookMeta) book_stack .getItemMeta ()) .getPages ());
	}
	
	
	public static ProblemData from (Inventory inventory) {
		try {
			return new ProblemData (inventory);
		}
		catch (Throwable e) {
			return null;
		}
	}
	
	public ProblemData (Inventory inventory) {
		int items = 0;
		boolean _all_books = true;
		for (ItemStack i : inventory .getContents ()) {
			if (i != null) {
				items ++;
				if (! (i .getType() == Material .BOOK_AND_QUILL || i .getType() == Material .BOOK)) {
					_all_books = false;
				}
			}
		}
		
//Bukkit .broadcastMessage("Hopeful probelm data contents length:" + items);
//Bukkit .broadcastMessage("Hopeful probelm data all books:" + _all_books);
		if (! (_all_books && items == 5)) {
//Bukkit .broadcastMessage("Throwing");
			throw new RuntimeException ("not a valid inventory");
		}

		int questions = 0;
		int answers = 0;
		ItemStack question = null;
		ItemStack answer = null;
		List <ItemStack> traps = new ArrayList <ItemStack> ();
		for (ItemStack i : inventory .getContents ()) {
			if (i != null) {
				String name = i .getItemMeta () .getDisplayName ();
//Bukkit .broadcastMessage("Hopeful probelm data book name:" + name);
//if (name != null) { Bukkit .broadcastMessage("Hopeful probelm data book name length:" + name .length());
//Bukkit .broadcastMessage("Hopeful probelm data book name is question?" + ("題目" .equals (name) || "Question" .equals (name))); }
				if ("題目" .equals (name) || "Question" .equals (name)) {
					questions ++;
					question = i .clone ();
				}
				else if ("答案" .equals (name) || "Answer" .equals (name)) {
					answers ++;
					answer = i .clone ();
				}
				else {
					traps .add (i .clone ());
				}
			}
		}
//Bukkit .broadcastMessage("Hopeful probelm data answers:" + answers);
//Bukkit .broadcastMessage("Hopeful probelm data questions:" + questions);
//try {
//Bukkit .broadcastMessage("Hopeful probelm data one question, one answer:" + (answers == 1 && questions == 1));
		if (! (answers == 1 && questions == 1)) {
//Bukkit .broadcastMessage("Throwing");
			throw new RuntimeException ("not a valid inventory");
		}

//Bukkit .broadcastMessage("Hopeful probelm data question:" + book_content (question));
//Bukkit .broadcastMessage("Hopeful probelm data answer:" + book_content (answer));
//Bukkit .broadcastMessage("Hopeful probelm data trap:" + book_content (traps .get (0)));
//Bukkit .broadcastMessage("Hopeful probelm data trap:" + book_content (traps .get (1)));
//Bukkit .broadcastMessage("Hopeful probelm data trap:" + book_content (traps .get (2)));
		
		
		this .question = book_content (question);
		this .answer = book_content (answer);
		this .traps = new String [] {
			book_content (traps .get (0)),
			book_content (traps .get (1)), 
			book_content (traps .get (2))
		};
//}
//catch (Exception e) {
//	e .printStackTrace();
//	throw e;
//}
	}
}
