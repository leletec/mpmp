package srv;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;

import model.Player;
import model.Player.Mode;
import model.SrvModel;
import model.SrvPlayer;

/**
 * Class Client implements the connection to a Client and has static methods for
 * sending packets to all clients. No gameplay state is saved here except a reference
 * to a Player.
 */
public class Client extends net.Conn {
	private static HashSet<Client> clients;
	private Player player;

	public Client(Socket sock) throws IOException {
		super(sock);
		synchronized(clients) {
			clients.add(this);
		}
		send("+JAWOHL Willkommen, Genosse! Subscriben Sie!");
	}

	/**
	 * Method subscribe subscribes a client as discussed in the protocol.
	 * @return false on failure (name already used or nil), true otherwise
	 */
	public boolean subscribe(String color, Mode mode, String name) {
		/* remove old Player from subscribed list */
		if(player != null)
			player.remove();

		if(name == null)
			return false;

		for(Client c : clients)
			if(c != this && c.isSubscribed() && name.equals(c.player.getName()))
				return false;


		player = new Player(Player.parseColor(color), mode, name);
		if(mode == Player.Mode.Player)
			SrvModel.self.addSrvPlayer(new SrvPlayer(player));

		return true;
	}

	/**
	 * Give up, auction everything, become a spectator.
	 */
	public void ragequit() {
		SrvModel sm = SrvModel.self;
		SrvPlayer sp;

		if(player == null)
			return;

		/* Is active player? Then give up and remove from all player lists. */
		sp = sm.getSrvPlayer(player);
		if(sp != null) {
			sp.ragequit();
			sm.rmSrvPlayer(sp);
			sm.m.rmPlayer(player);
		}

		/* Remove from player+spectator list and re-add as spectator. */
		player.remove();
		player = new Player(player.getColor(), Player.Mode.Spectator, player.getName());
		listClients();
	}

	public String getName() {
		if(player != null)
			return player.getName();
		return null;
	}

	public boolean isSubscribed() {
		return player != null;
	}

	/**
	 * Remove this client from the client list.
	 */
	public void remove() {
		clients.remove(this);
		if(player != null)
			player.remove();
		listClients();
	}

	/**
	 * Send a client list to all clients.
	 */
	public static synchronized void listClients() {
		// XXX use broadcast
		// XXX stupid name
		for (Client receiver : clients) {
			synchronized(receiver) {
				receiver.send("playerlist-update " + Player.numPlayers());
				for (Client c : clients)
					if(c.isSubscribed())
						receiver.sendCont("" + c.player);
			}
		}
	}
	
	/**
	 * Send a string to all clients.
	 */
	public static void broadcast(String s) {
		for (Client c : clients)
			c.send(s);
	}

	/**
	 * Reset the client table.
	 */
	public static void reset() {
		clients = new HashSet<>();
	}
	
	public static Client search(String name) {
		for (Client c : clients)
			if (name.equals(c.getName()))
				return c;
		return null;
	}
}
