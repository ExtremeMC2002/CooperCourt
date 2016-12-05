package com.extrememc2002.coopercourt.core;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.matejdro.bukkit.jail.JailAPI;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Core extends JavaPlugin implements CommandExecutor, Listener {
	
	
// Permissions: 
// court.user (/sue, /court judges) 
	
// court.admin (/court reload, /court config, /court setjudge)

// court.judge (/court jail, /court fine, /court list) 
	
	JailAPI jail;
	
	ArrayList<String> courtRequests = new ArrayList<String>();
	ArrayList<String> judgesList = new ArrayList<String>();
	
	public void onEnable() {
		getConfig().options().copyDefaults(true);
		saveConfig();
		this.jailTime = getConfig().getInt("DefaultJailTime");
		this.fineAmount = getConfig().getInt("DefaultFine");
		
		
	}
	
	public void onDisable() {
		
	}
	
	int jailTime = 10;
	int fineAmount = 2000;
	
	public static boolean isInt(String s) {
	    try {
	        Integer.parseInt(s);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player p = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("court")) {
			if (args[0].equalsIgnoreCase("help")) {

				p.sendMessage("§c§lCourt plugin developed by: ExtremeMC2002, and Wolf_Shephard.");
				p.sendMessage("");
				p.sendMessage("§6Commands:");
				p.sendMessage("");
				p.sendMessage("§6/court sue <player> <amount>");
				p.sendMessage("§6/court judges");
			
				PermissionUser user1 = PermissionsEx.getUser(p);
				
				if(user1.inGroup("Judge")) {
					
					p.sendMessage("§6/court jail <player>");
					p.sendMessage("§6/court fine <player> <amount>");
					p.sendMessage("§6/court list - WIP");
					}
						
				if(p.isOp()) { 
						
					p.sendMessage("§6/court reload");
					p.sendMessage("§6/court setjudge <player> - WIP");
					
				}
			}
						
			if (args[0].equalsIgnoreCase("jail")) {
				
				if (args.length > 1) {
						
					String playerName = args[1];
						
					String reason = "Found Guilty";
							
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "jail " + playerName + " " + this.jailTime + " r:" + reason);
					p.sendMessage("§c[§eCooperCraft§c] Successfully jailed §e" + playerName + " §cbecause they were found guilty!");
					Bukkit.broadcastMessage("§c[§eCooperCraft§c] §e" + playerName + " §c was just found guilty by Judge §e" + sender.getName() + " §cand sent to federal prison for their crime.");
				}
				else {
					p.sendMessage("§c[§eCooperCraft§c] Please specify a player to jail!");
					}
				}
			
			if (args[0].equalsIgnoreCase("reload")) {
					
				p.sendMessage("§c[§eCooperCraft§c] Successfully reloaded the config");
			
			}
			
			if (args[0].equalsIgnoreCase("config")) {
				
				p.sendMessage("§6Config Values:");
				p.sendMessage("");
				p.sendMessage("§cPrison Time:§e " + this.jailTime);
				p.sendMessage("§cCourt Default Fine Amount:§e " + this.fineAmount);
			}
				
			if (args[0].equalsIgnoreCase("fine")) {
					
				if (args.length > 1) {
						
					if (args.length == 3) {
							
						String takePlayer1 = args[1];
					        
						if (isInt(args[2])) {
							int customFineAmount = Integer.parseInt(args[2]);
					            
							Player finedPlayer1 = Bukkit.getPlayerExact(args[1]);
							
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "eco take " + finedPlayer1.getName() + " " + customFineAmount);
						    	
						    finedPlayer1.sendMessage("§c[§eCooperCraft§c] The Cooper Nation Court of law has fined you §e$" + customFineAmount + " §c.");
						    p.sendMessage("§c[§eCooperCraft§c] You have fined §e" + finedPlayer1.getName() + " §cfor §e$" + customFineAmount + "§c.");
					            
						}
						else {
							p.sendMessage("§c[§eCooperCraft§c] Error, that is not a valid amount of money.");
						}
					}
					else {
						p.sendMessage("§c[§eCooperCraft§c] Error, please do the command (/court fine <player> <amount>");
					}
				}
			}
			if (args[0].equalsIgnoreCase("sue")) {
				
				if (args.length > 1) {
					
					if (args.length == 3) {
						
						if(!courtRequests.contains(p.getName())) {
						
							String suedPlayer = args[1];
						
							int sueAmount = Integer.parseInt(args[2]);
						
							p.sendMessage("§c[§eCooperCraft§c] You've successfully sent in a request for a court case, please wait for a judge to review it and get in contact with you!");
							
							Player suedPlayer2 = Bukkit.getServer().getPlayer(suedPlayer);
							
							suedPlayer2.sendMessage("§c[§eCooperCraft§c] Oh no! §e" + p.getName() + "§c has filed a request for a court case against you, be prepared to go to court, failure to attend the court session will result in punishment.");
							
							courtRequests.add(p.getName());
							
							for (Player judges : Bukkit.getServer().getOnlinePlayers()) {
								
								PermissionUser user2 = PermissionsEx.getUser(judges);
								
		                        if (user2.inGroup("Judge") || user2.inGroup("AttorneyGeneral")) {
		                        	
		                        	judges.sendMessage("§c[§eCooperCraft§c] The player §e" + p.getName() + "§c has requested to sue §e" + suedPlayer + "§c for §e$" + sueAmount + "§c.");
		                        	judges.sendMessage("§c[§eCooperCraft§c] All current players requesting a court case: §e" + courtRequests);
								
								}
							}
						}
					}
				}
			}
			if (args[0].equalsIgnoreCase("list")) {
				
				if (!courtRequests.isEmpty()) {
					
					p.sendMessage("§c[§eCooperCraft§c] All current players requesting a court case: §e" + courtRequests);
				}
				else {
					
					p.sendMessage("§c[§eCooperCraft§c] No one is currently in need of a court session");
					
				}
			}
			if (args[0].equalsIgnoreCase("finish")) {
				
				if (args.length == 2) {
					
					String asker = args[1];
					
					if (courtRequests.contains(asker)) {
					
						PermissionUser finisher = PermissionsEx.getUser(p);
					
						if (finisher.inGroup("Judge") || finisher.inGroup("AttorneyGeneral")) {
							
							courtRequests.remove(asker);
							
							Player asker2 = Bukkit.getServer().getPlayer(asker);
								
							asker2.sendMessage("§c[§eCooperCraft§c] Your court case has been declared closed by §e" + finisher.getName() + "§c.");
							
							p.sendMessage("§c[§eCooperCraft§c] You have closed §e" + asker2.getName() + "'s§c court case");
							
						}
					}
				}
			}
			if (args[0].equalsIgnoreCase("setjudge")) {
				
				if (args.length == 2) {
					
					String newJudge = args[1];
					
					Player newJudge2 = Bukkit.getServer().getPlayer(newJudge);
					
					if (!judgesList.contains(newJudge2.getName())) {
						
						judgesList.add(newJudge2.getName());
						
						p.sendMessage("§c[§eCooperCraft§c] You have appointed a new judge!");
						
						newJudge2.sendMessage("§c[§eCooperCraft§c] You have been apointed as a Cooper Nation Judge!");
						
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + newJudge2.getName() + " group set Citizen");
						
						PermissionUser judgeGroup = PermissionsEx.getUser(newJudge2);
						
						judgeGroup.addGroup("Judge");
						
					}
					
					
				}
				
			}
			if (args[0].equalsIgnoreCase("removejudge")) {
				
				if (args.length == 2) {
						
					String removedJudge = args[1];
					
					Player removedJudge2 = Bukkit.getServer().getPlayer(removedJudge);
					
					if (judgesList.contains(removedJudge2.getName())) {
					
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + removedJudge2.getName() + " group set Citizen");
					
						removedJudge2.sendMessage("§c[§eCooperCraft§c] You have been stripped of your title as a Cooper Nation Judge");
						
						p.sendMessage("§c[§eCooperCraft§c] You have stripped §e" + removedJudge2.getName() + "§c of there Judge title.");
						
						judgesList.remove(removedJudge2.getName());
					}
				}
			}
			return true;
		}
		return false;
	}
}
