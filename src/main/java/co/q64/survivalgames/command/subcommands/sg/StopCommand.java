/**
 * Name: StopCommand.java
 * Created: 2 January 2014
 *
 * @verson 1.0
 */

package co.q64.survivalgames.command.subcommands.sg;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import co.q64.survivalgames.command.subcommands.SubCommand;
import co.q64.survivalgames.exception.ArenaNotFoundException;
import co.q64.survivalgames.locale.I18N;
import co.q64.survivalgames.managers.SGApi;
import co.q64.survivalgames.objects.SGArena;

public class StopCommand implements SubCommand {
	@Override
	public void execute(String cmd, Player p, String[] args) {
		if (!p.hasPermission("sg.stop") || !p.isOp())
			return;
		if (cmd.equalsIgnoreCase("stop")) {
			int i;
			try {
				i = Integer.parseInt(args[0]);
			} catch (NumberFormatException x) {
				p.sendMessage(SGApi.getArenaManager().getError() + I18N.getLocaleString("NOT_NUMBER"));
				return;
			}
			SGArena a;
			try {
				a = SGApi.getArenaManager().getArena(i);
			} catch (ArenaNotFoundException e) {
				Bukkit.getLogger().severe(e.getMessage());
				return;
			}
			if (a == null) {
				p.sendMessage(SGApi.getArenaManager().getError() + I18N.getLocaleString("INVALID_ARENA") + a);
				return;
			}
			a.end();
			p.sendMessage(SGApi.getArenaManager().getPrefix() + I18N.getLocaleString("ARENA_END"));
		} else {
			p.sendMessage(SGApi.getArenaManager().getError() + I18N.getLocaleString("INVALID_ARGUMENTS"));
		}
	}
}
