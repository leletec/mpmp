/**
 * Controller part of the MVC model (MVC is used in the client only).
 */
package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.SwingUtilities;

import model.Model;
import model.Player;
import model.Plot;
import model.PlotGroup;
import net.ShowTransaction;
import net.Cmd;
import net.Conn;
import net.ChatUpdate;
import net.Eventcard;
import net.MoneyUpdate;
import net.PlayerlistUpdate;
import net.PlotUpdate;
import net.PosUpdate;
import net.Prison;
import net.StartUpdate;
import net.Subscribe;
import net.TurnUpdate;

import net.MoneyUpdate.MoneyUpdater;
import net.PlayerlistUpdate.PlayerlistUpdater;
import net.PlotUpdate.PlotUpdater;
import net.PosUpdate.PosUpdater;
import net.Prison.PrisonUpdater;
import net.StartUpdate.StartUpdater;
import net.TurnUpdate.TurnUpdater;

import view.Frame;

/**
 * Controller class of the MVC model; used only by the client.
 */
public class Controller
implements MoneyUpdater, PosUpdater, TurnUpdater, PrisonUpdater, StartUpdater, PlotUpdater, PlayerlistUpdater {

	Frame frame;
	Timer t;
	String myName; /* player name of this client */
	Model m;

	public Controller(String addr, int port, String mode, String color, String name) throws UnknownHostException, IOException {
		Conn conn = new Conn(new Socket(addr, port));
		this.myName = name;

		Player.reset();
		Model.init();
		m = new Model();
		frame = new Frame(m);

		try {
			SwingUtilities.invokeAndWait(() -> {
					frame.createFrame();
				});
		} catch (InterruptedException | java.lang.reflect.InvocationTargetException e) {
			e.printStackTrace();
			System.exit(1);
		}

		/* Please insert alphabetically. -oki */
		((ChatUpdate) Cmd.ChatUpdate.getFn()).addDisplayer(frame.chatDisp);
		((Eventcard) Cmd.Eventcard.getFn()).addDisplayer(frame.chatDisp);
		((MoneyUpdate) Cmd.MoneyUpdate.getFn()).addMoneyUpdater(this);
		((PlayerlistUpdate) Cmd.PlayerlistUpdate.getFn()).addPlayerlistUpdater(this);
		((PlotUpdate) Cmd.PlotUpdate.getFn()).addPlotUpdater(this);
		((PosUpdate) Cmd.PosUpdate.getFn()).addPosUpdater(this);
		((Prison) Cmd.Prison.getFn()).addDisplayer(frame.chatDisp);
		((Prison) Cmd.Prison.getFn()).addPrisonUpdater(this);	
		((ShowTransaction) Cmd.ShowTransaction.getFn()).addDisplayer(frame.chatDisp);
		((StartUpdate) Cmd.StartUpdate.getFn()).addStartUpdater(this);
		((Subscribe) Cmd.Subscribe.getFn()).addSubscribeErrer(frame);
		((TurnUpdate) Cmd.TurnUpdate.getFn()).addTurnUpdater(this);

		frame.addChatListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
					String line = frame.getChat();
					if (line.length() == 0)
						return;

					conn.send("chat " + line);
				}
			}
		});

		frame.addSurrenderListener((ActionEvent e) -> {
				conn.send("ragequit");
				frame.showQuitButton();
				frame.addSurrenderListener((ActionEvent ev) -> {System.exit(0);});
			});

		frame.addEndTurnListener((ActionEvent e) -> {
				if (m.running())
					conn.send("end-turn");
				else
					conn.send("start-game");
			});

		frame.addTradeListener((ActionEvent e) -> {
				String buyer;
				int pos;
				int amount;

				try {
					pos = Integer.parseInt(frame.showDialog("Grundstücksnummer pls"));
					amount = Integer.parseInt(frame.showDialog("Wieviel soll's kosten?"));
				} catch(NumberFormatException nfe) {
					frame.chatDisp.show("Dat is keine Zahl gewesen!", Color.ORANGE);
					return;
				}

				buyer = frame.showDialog("Wer ist der Käufer?");
				if(m.getPlayer(buyer) == null) {
					frame.chatDisp.show("Diesen Spieler gibt's nicht mal!", Color.ORANGE);
					return;
				}

				conn.send("sell-plot " + pos + " " + amount + " " + buyer);
			});

		frame.addBuyHouseListener((ActionEvent e) -> {
				Player p = m.getPlayer(myName);
				// XXX this is a UX nightmare
				int plotpos = Integer.parseInt(frame.showDialog("Grundstücksnummer pls"));
				conn.send("add-house " + plotpos);
				// XXX show answer by handling error in AddHouse
			});

		frame.addBuyPlotListener((ActionEvent e) -> {
				Player p = m.getPlayer(myName);
				conn.send("buy-plot " + p.getPos() + " " + p.getName());
				// XXX show answer by handling error in BuyPlot
			});

		frame.addPayPrisonListener((ActionEvent e) -> {
				conn.send("unjail money");
				// XXX show answer by handling error in Unjail
			});

		frame.addUsePrisonLeaveListener((ActionEvent e) -> {
				conn.send("unjail card");
				// XXX show answer by handling error in Unjail
			});

		frame.addClearChatListener((ActionEvent e) -> {
				frame.chatDisp.reset();
			});

		new Thread(() -> {
			try {
				conn.handle();
			} catch (IOException ioe) {
				// XXX "do what"?
			}
		}).start();

		conn.send("subscribe " + mode + " " + color + " " + name);
	}

	/* S->C UPDATES */

	public void moneyUpdate(int amount, String name) {
		Player p = m.getPlayer(name);
		if(p == null)
			return;

		p.setMoney(amount);
		frame.myPlayerDisp.show(m.getPlayer(myName));
	}

	public void plotUpdate(int pos, int houses, boolean hyp, String ownername) {
		Plot plot; 
		Player oldowner, owner;

		plot = m.getPlot(pos);
		if(plot == null)
			return;

		oldowner = plot.getOwner();
		if(oldowner != null) {
			oldowner.rmPlot(plot);
			plot.setOwner(null);
		}

		owner = m.getPlayer(ownername);
		if(owner == null)
			return;

		plot.setHouses(houses);
		plot.hypothec(hyp);     // XXX inconsistent
		owner.addPlot(plot);
		plot.setOwner(owner);

		frame.chatDisp.show(plot.getName() + " " + plot, Color.RED);
		frame.boardDisp.reset();
		frame.myPlayerDisp.show(m.getPlayer(myName));
	}

	public void posUpdate(int pos, String name) {
		Player p = m.getPlayer(name);
		if(p == null)
			return;

		p.setPos(pos);
		frame.boardDisp.reset();
		frame.myPlayerDisp.show(m.getPlayer(myName));
	}

	public void turnUpdate(int roll, int paschs, String cpname) {
		m.setCurrentPlayer(m.getPlayer(cpname));
		frame.myPlayerDisp.show(m.getPlayer(myName));
		frame.chatDisp.show("Gesamtwürfelsumme: " + roll + "; Anzahl Paschs: " + paschs);
	}

	public void startUpdate() {
		m.startGame();
		frame.startDisp.reset();
		frame.myPlayerDisp.show(m.getPlayer(myName));
	}

	public void prisonUpdate(boolean enter, String name) {
		Player p = m.getPlayer(name);
		if(p == null)
			return; // XXX return error to allow for no-such-player error

		p.setPrison(enter);
		frame.myPlayerDisp.show(m.getPlayer(myName));
	}

	public void playerlistAdd(String col, String mode, String name) {
		Player.Mode md;
		Player p;

		switch(mode.toLowerCase()) {
			default:
			case "spectator":
				md = Player.Mode.Spectator;
				break;
			case "player":
				md = Player.Mode.Player;
				break;
		}

		p = new Player(Player.parseColor(col), md, name);
		m.addPlayer(p);
		frame.playerDisp.show(p);

		/* If it's yourself, display in the bottom. We won't display anything
		 * if there's no guarantee that it is you, or we'd crash.
		 */
		if(name == myName)
			frame.myPlayerDisp.show(m.getPlayer(myName));
	}

	public void playerlistReset() {
		frame.playerDisp.reset();
		m.resetPlayers();
	}
}
