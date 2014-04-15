package co.q64.paradisesurvivalgames.configs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;

import co.q64.paradisesurvivalgames.managers.SGApi;
import co.q64.paradisesurvivalgames.objects.MapHash;
import co.q64.paradisesurvivalgames.objects.SGArena;

public class ArenaConfigTemplate extends ConfigTemplate<SGArena> {
	private SGArena arena;

	private SGArena cachedArena;

	public ArenaConfigTemplate(File file) {
		super(file);
	}

	public ArenaConfigTemplate(SGArena arena) {
		super("arenas/" + arena.getId() + ".yml");
		this.arena = arena;
	}

	@Override
	public String[] pattern() {
		return new String[] { "Current-state", "Id", "Lobby", "Current-map", "Voted", "Votes", "Max-players", "Min-players", "Players", "Spectators" };
	}

	@Override
	public Object toFile(int keyPair) {
		Bukkit.getLogger().info("Attemping to save prop: " + arena.toString() + " with a keypair of " + keyPair);
		switch (keyPair) {
		case 0:
			Bukkit.getLogger().info(arena.getState().getTrueName());
			return arena.getState().getTrueName();
		case 1:
			Bukkit.getLogger().info(arena.getId() + "");
			return arena.getId();
		case 2:
			Bukkit.getLogger().info(SGApi.getArenaManager().serializeLoc(arena.getLobby()));
			return SGApi.getArenaManager().serializeLoc(arena.getLobby());
		case 3:
			if (arena.getCurrentMap() == null) {
				return "NOT_ACTIVE";
			}
			return arena.getCurrentMap().getName();
		case 4:
			return arena.getVoted();
		case 5:
			return serializeMaps();
		case 6:
			return arena.getMaxPlayers();
		case 7:
			return arena.getMinPlayers();
		case 8:
			return arena.getPlayers();
		case 9:
			return arena.getSpectators();
		}
		return null;
	}

	@Override
	public SGArena fromFile(int index, Object o) {
		Bukkit.getLogger().info("Attempting to load arnea prop: " + o + " with a keypair of " + index);
		switch (index) {
		case 0:
			this.cachedArena = new SGArena(); //This should only be called once.  Here?
			this.cachedArena.setState(SGArena.ArenaState.valueOf(String.valueOf(o)));
			break;
		case 1:
			this.cachedArena.createArena(Integer.parseInt(String.valueOf(o)));
			break;
		case 2:
			this.cachedArena.setLobby(SGApi.getArenaManager().deserializeLoc(String.valueOf(o)));
			break;
		case 3:
			if (String.valueOf(o).equals("NOT_ACTIVE"))
				break;
			this.cachedArena.setCurrentMap(SGApi.getMultiWorldManager().worldForName(String.valueOf(o)));
			break;
		case 4:
			this.cachedArena.setVoted((List<String>) o);
			break;
		case 5:
			this.cachedArena.setVotes(deserializeMaps((List<String>) o));
			break;
		case 6:
			this.cachedArena.setMaxPlayers(Integer.parseInt(String.valueOf(o)));
			break;
		case 7:
			this.cachedArena.setMinPlayers(Integer.parseInt(String.valueOf(o)));
			break;
		case 8:
			for (String s : (List<String>) o) {
				this.cachedArena.getPlayers().add(s);
			}
			break;
		case 9:
			for (String s : (List<String>) o) {
				this.cachedArena.getSpectators().add(s);
			}
			break;
		}
		return cachedArena;
	}

	public List<String> serializeMaps() {
		List<String> list = new ArrayList<>();
		for (Map.Entry<MapHash, Integer> entry : arena.getVotes().entrySet()) {
			list.add(entry.getKey().getId() + ":" + entry.getKey().getWorld().getWorld().getName() + ":" + entry.getValue());
		}
		return list;
	}

	public Map<MapHash, Integer> deserializeMaps(List<String> list) {
		Map<MapHash, Integer> mapHashIntegerMap = new HashMap<>();
		for (String s : list) {
			String[] strings = s.split(":");
			mapHashIntegerMap.put(new MapHash(SGApi.getMultiWorldManager().worldForName(strings[1]), Integer.parseInt(strings[0])), Integer.parseInt(strings[2]));
		}
		return mapHashIntegerMap;
	}
}
