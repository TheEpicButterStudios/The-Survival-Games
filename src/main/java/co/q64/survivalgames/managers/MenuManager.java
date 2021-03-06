package co.q64.survivalgames.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import co.q64.survivalgames.exception.ArenaNotFoundException;
import co.q64.survivalgames.objects.MapHash;
import co.q64.survivalgames.objects.SGArena;
import co.q64.survivalgames.util.gui.IconMenu;
import co.q64.survivalgames.util.gui.IconMenu.OptionClickEvent;

public class MenuManager {
	static MenuManager menuManager;
	private IconMenu joinMenu;
	private IconMenu specMenu;
	private IconMenu voteMenu;

	public MenuManager() {
		///////////////
		///Join Menu///
		///////////////
		joinMenu = new IconMenu("Join an arena", 54, true, new IconMenu.OptionClickEventHandler() {

			@Override
			public void onOptionClick(OptionClickEvent event) {
				if (event.getItem().getType() == Material.EMERALD_BLOCK || event.getItem().getType() == Material.GOLD_BLOCK) {
					try {
						SGApi.getArenaManager().addPlayer(event.getPlayer(), Integer.parseInt(event.getName().replaceAll("[^0-9]", "")));
						event.setWillClose(true);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}
		}, SGApi.getPlugin());

		Bukkit.getScheduler().scheduleSyncRepeatingTask(SGApi.getPlugin(), new Runnable() {

			// 11-15 joinable
			// 26-53 non-joinable
			@Override
			public void run() {
				joinMenu.clear();
				List<SGArena> arenas = cloneThoseArenas();
				int index = 0;
				for (SGArena a : arenas) {
					if (index == 5)
						break;
					if (a.getState().equals(SGArena.ArenaState.WAITING_FOR_PLAYERS) || a.getState().equals(SGArena.ArenaState.PRE_COUNTDOWN)) {
						joinMenu.setOption(11 + index, new ItemStack(Material.EMERALD_BLOCK, a.getPlayers().size()), "SG - Arena " + a.getId(), new String[] { ChatColor.BLACK + "", ChatColor.YELLOW + "Players: " + ChatColor.WHITE + a.getPlayers().size(), ChatColor.YELLOW + "Status: " + ChatColor.GREEN + a.getState().toString(), ChatColor.AQUA + "", ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Click to " + "Join!" });
						index++;
					}

				}

				for (SGArena a : arenas) {
					if (index == 27)
						break;
					if (a.getState().equals(SGArena.ArenaState.IN_GAME) || a.getState().equals(SGArena.ArenaState.DEATHMATCH)) {
						joinMenu.setOption(26 + index, new ItemStack(Material.GOLD_BLOCK, a.getPlayers().size()), "SG - Arena " + a.getId(), new String[] { ChatColor.BLACK + "", ChatColor.YELLOW + "Players: " + ChatColor.WHITE + a.getPlayers().size(), ChatColor.YELLOW + "Status: " + ChatColor.GREEN + a.getState().toString(), ChatColor.AQUA + "", ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Click to " + "Spectate!" });
						index++;
					}

				}
			}
		}, 20L, 20L);

		///////////////
		///Vote Menu///
		///////////////

		voteMenu = new IconMenu("Vote for a map", 9, false, new IconMenu.OptionClickEventHandler() {

			@Override
			public void onOptionClick(OptionClickEvent event) {
				if (event.getItem().getType() == Material.EMPTY_MAP) {
					try {
						try {
							SGApi.getArenaManager().getArena(event.getPlayer()).vote(event.getPlayer(), Integer.parseInt(event.getItem().getItemMeta().getLore().get(0).charAt(4) + ""));
						} catch (ArenaNotFoundException ignored) {}
						event.setWillClose(true);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}
		}, SGApi.getPlugin());

		///////////////
		///Spec Menu///
		///////////////

		specMenu = new IconMenu("Select the player to spectate", 27, false, new IconMenu.OptionClickEventHandler() {

			@Override
			public void onOptionClick(final OptionClickEvent event) {
				if (event.getItem().getType() == Material.REDSTONE_BLOCK) {
					event.setWillClose(true);
					return;
				}
				event.getPlayer().teleport(Bukkit.getPlayer(event.getItem().getItemMeta().getDisplayName()));
				event.setWillClose(true);
			}
		}, SGApi.getPlugin());
	}

	public static MenuManager getMenuManager() {
		if (menuManager == null)
			menuManager = new MenuManager();
		return menuManager;
	}

	private List<SGArena> cloneThoseArenas() {
		List<SGArena> a = new ArrayList<SGArena>();
		for (SGArena arena : SGApi.getArenaManager().getArenas()) {
			a.add(arena);
		}
		Collections.sort(a, new Comparator<SGArena>() {
			@Override
			public int compare(SGArena o1, SGArena o2) {
				return Integer.compare(o1.getPlayers().size(), o2.getPlayers().size());
			}
		});
		Collections.reverse(a);
		return a;
	}

	public void displayJoinMenu(Player p) {
		joinMenu.open(p);
	}

	public void displaySpecMenu(Player player) {
		SGArena a;
		try {
			a = SGApi.getArenaManager().getArena(player);
		} catch (ArenaNotFoundException e1) {
			return;
		}
		List<ItemStack> items = new ArrayList<ItemStack>();
		for (UUID s : a.getPlayers()) {
			try {
				if (SGApi.getArenaManager().getArena(Bukkit.getPlayer(s)).getSpectators().contains(s)) {
					continue;
				}
			} catch (ArenaNotFoundException e) {
				continue;
			}
			ItemStack item = new ItemStack(Material.EMERALD, (int) Bukkit.getPlayer(s).getHealth());
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(Bukkit.getPlayer(s).getName());
			item.setItemMeta(meta);
			items.add(item);
		}
		Collections.sort(items, new Comparator<ItemStack>() {
			@Override
			public int compare(ItemStack o1, ItemStack o2) {
				return Integer.compare(o1.getAmount(), o2.getAmount());
			}
		});
		specMenu.clearOptions();
		for (int i = 0; i < items.size(); i++) {
			specMenu.setOption(i, items.get(i), items.get(i).getItemMeta().getDisplayName(), new String[] { ChatColor.translateAlternateColorCodes('&', "&e&lClick to teleport to this person!"), ChatColor.translateAlternateColorCodes('&', "&aNote: Health = Amount of emeralds") });
		}
		specMenu.setOption(26, new ItemStack(Material.REDSTONE_BLOCK), ChatColor.RED + "Cancel", ChatColor.translateAlternateColorCodes('&', "&e&lExits out of this menu"));
		specMenu.open(player);
	}

	public void displayVoteMenu(Player p) {
		voteMenu.clearOptions();
		try {
			SGArena arena = SGApi.getArenaManager().getArena(p);
			int i = 0;
			for (Map.Entry<MapHash, Integer> entry : arena.getVotes().entrySet()) {
				voteMenu.setOption(i, new ItemStack(Material.EMPTY_MAP), entry.getKey().getWorld().getDisplayName(), new String[] { "Map " + entry.getKey().getId(), "Current Votes: " + entry.getValue() });
				i++;
			}
		} catch (ArenaNotFoundException e) {
			return;
		}
		voteMenu.open(p);
	}
}
