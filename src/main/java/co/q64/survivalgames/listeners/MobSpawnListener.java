package co.q64.survivalgames.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import co.q64.survivalgames.managers.SGApi;

public class MobSpawnListener implements Listener {
	@EventHandler(priority = EventPriority.HIGH)
	public void onSpawn(CreatureSpawnEvent event) {
		for (int i = 0; i < SGApi.getMultiWorldManager().getWorlds().size(); i++) {
				event.setCancelled(true);
		}
	}
}
