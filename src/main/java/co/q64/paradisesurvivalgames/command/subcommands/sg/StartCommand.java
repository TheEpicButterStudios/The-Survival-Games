/**
 * Name: StartCommand.java Created: 10 December 2013
 *
 * @version 1.0.0
 */

package co.q64.paradisesurvivalgames.command.subcommands.sg;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import co.q64.paradisesurvivalgames.command.subcommands.SubCommand;
import co.q64.paradisesurvivalgames.exception.ArenaNotFoundException;
import co.q64.paradisesurvivalgames.locale.I18N;
import co.q64.paradisesurvivalgames.managers.SGApi;
import co.q64.paradisesurvivalgames.objects.SGArena;

public class StartCommand implements SubCommand {

	/**
	 * The start command. DO NOT CALL DIRECTLY. Only use in CommandHandler
	 *
	 * @param cmd  The command that was executed
	 * @param p    The player that executed the command
	 * @param args The arguments after the command
	 */
	@Override
	public void execute(String cmd, Player p, String[] args) {
		if (!p.isOp() && !p.hasPermission("sg.start"))
			return;
		if (cmd.equalsIgnoreCase("start") && args.length == 2 && p.hasPermission("sg.gamestate.start")) {
			int id = 0;
			try {
				id = Integer.parseInt(args[0]);
			} catch (NumberFormatException x) {
				p.sendMessage(SGApi.getArenaManager().getError() + I18N.getLocaleString("INVALID_ARENA") + args[0]);
			}
			SGArena a;
			try {
				a = SGApi.getArenaManager().getArena(id);
			} catch (ArenaNotFoundException e) {
				Bukkit.getLogger().severe(e.getMessage());
				return;
			}

			if (args[1].equals("starting") && p.hasPermission("sg.gamestate.starting")) {
				if (!a.getState().equals(SGArena.ArenaState.STARTING_COUNTDOWN) || a.getState().isConvertable(a, SGArena.ArenaState.STARTING_COUNTDOWN)) {
					p.sendMessage(SGApi.getArenaManager().getError() + I18N.getLocaleString("CANT_FORCE"));
					return;
				}
				a.setState(SGArena.ArenaState.STARTING_COUNTDOWN);
				SGApi.getTimeManager(a).countdownLobby(1);
				p.sendMessage(SGApi.getArenaManager().getPrefix() + I18N.getLocaleString("CHANGED_STATE"));
				return;
			}

			if (args[1].equals("game") && p.hasPermission("sg.gamestate.ingame")) {
				if (!a.getState().equals(SGArena.ArenaState.IN_GAME) || a.getState().isConvertable(a, SGArena.ArenaState.IN_GAME)) {
					p.sendMessage(SGApi.getArenaManager().getError() + I18N.getLocaleString("CANT_FORCE"));
					return;
				}
				a.setState(SGArena.ArenaState.IN_GAME);
				SGApi.getTimeManager(a).countdown();
				p.sendMessage(SGApi.getArenaManager().getPrefix() + I18N.getLocaleString("CHANGED_STATE"));
				return;
			}

			if (args[1].equals("dm") && p.hasPermission("sg.gamestate.dm")) {
				if (!a.getState().equals(SGArena.ArenaState.DEATHMATCH) || a.getState().isConvertable(a, SGArena.ArenaState.DEATHMATCH)) {
					p.sendMessage(SGApi.getArenaManager().getError() + I18N.getLocaleString("CANT_FORCE"));
					return;
				}
				a.setState(SGArena.ArenaState.DEATHMATCH);
				p.sendMessage(SGApi.getArenaManager().getPrefix() + I18N.getLocaleString("CHANGED_STATE"));
				return;
			}

			if (args[1].equals("dm") && p.hasPermission("sg.gamestate.dm")) {
				if (!a.getState().equals(SGArena.ArenaState.IN_GAME) || a.getState().isConvertable(a, SGArena.ArenaState.IN_GAME)) {
					p.sendMessage(SGApi.getArenaManager().getError() + I18N.getLocaleString("CANT_FORCE"));
					return;
				}
				a.setState(SGArena.ArenaState.DEATHMATCH);
				SGApi.getTimeManager(a).countdownDm();
				p.sendMessage(SGApi.getArenaManager().getPrefix() + I18N.getLocaleString("CHANGED_STATE"));
			}
		}
	}

}
