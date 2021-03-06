package net;

import java.util.ArrayList;

import srv.Client;
import model.Diceroll;
import model.Field;
import model.Player;
import model.SrvModel;
import model.SrvPlayer;

/**
 * end-turn C->S
 */
public class EndTurn implements CmdFunc {

	@Override
	public void exec(String line, Conn conn) {
		// XXX check if plot was bought, if not -> auction
		SrvModel sm = SrvModel.self;
		Client c = (Client) conn;
		SrvPlayer sp;
		ArrayList<SrvPlayer> players;

		// XXX duplication in StartGame packet! -oki

		if(!sm.m.running()) {
			conn.sendErr(ErrCode.GameNotRunning);
			return;
		}

		sp = sm.getSrvPlayer(sm.m.getCurrentPlayer());
		if(sp == null)
			return; /* no current player! */

		// Only current player can end their turn.
		if(!c.getName().equals(sp.p.getName()))
			return;

		Diceroll dr = new Diceroll();
		if(dr.getPaschs() >= 3) {
			sp.prison(true);
			conn.send("prison enter");
			Client.broadcast("turn-update " + 0 + " " + dr.getPaschs() + " " + sp.p.getName()); // XXX move to Update
		} else {
			sp.move(dr.getSum());
			Client.broadcast("turn-update " + dr.getSum() + " " + dr.getPaschs() + " " + sp.p.getName()); // XXX move to Update
		}

		players = sm.getSrvPlayers();
		sm.m.setCurrentPlayer(players.get((players.indexOf(sp)+1) % players.size()).p);
		conn.sendOK();
	}

	@Override
	public void error(ErrCode err, String line, Conn conn) {
		//TODO
	}
	
}
