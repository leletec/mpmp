/**
 * Controller part of the MVC model (MVC is used in the client only).
 */
package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;
import model.GameState;

import model.Model;
import model.Player;
import model.PlotGroup;
import net.ShowTransaction;
import net.Cmd;
import net.Conn;
import net.ChatUpdate;
import net.PlayerlistUpdate;
import net.PosUpdate;
import net.Prison;
import net.StartUpdate;
import net.Subscribe;
import net.TurnUpdate;

import view.Frame;

/**
 * Controller class of the MVC model; used only by the client.
 */
public class Controller {

	Frame frame;
	Timer t;
	String name;

	public Controller(String addr, int port, String mode, String color, String name) throws UnknownHostException, IOException {
		Model m = new Model();
		frame = new Frame(m);
		Conn conn = new Conn(new Socket(addr, port));
		this.name = name;

		Player.reset();
		PlotGroup.init();

		((ChatUpdate) Cmd.ChatUpdate.getFn()).addDisplayer(frame.chatDisp);
		((PlayerlistUpdate) Cmd.PlayerlistUpdate.getFn()).addDisplayer(frame.playerDisp);
		((ShowTransaction) Cmd.ShowTransaction.getFn()).addDisplayer(frame.chatDisp);
		((TurnUpdate) Cmd.TurnUpdate.getFn()).addDisplayer(frame.chatDisp);
		((PosUpdate) Cmd.PosUpdate.getFn()).addDisplayer(frame.pieceDisp);
		((Subscribe) Cmd.Subscribe.getFn()).addSubscribeErrer(frame);
		((Prison) Cmd.Prison.getFn()).addDisplayer(frame.chatDisp);
		((StartUpdate) Cmd.StartUpdate.getFn()).addDisplayer(frame.startDisp);

		new Thread(() -> {
			try {
				conn.handle();
			} catch (IOException ioe) {
				// XXX "do what"?
			}
		}).start();

		conn.send("subscribe " + mode + " " + color + " " + name);

		//http://stackoverflow.com/questions/5844794/java-timertick-event-for-game-loop
		t = new Timer();
		t.schedule(new TimerTick(), 1000);

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

		frame.addSurrenderListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				conn.send("ragequit");
			}
		});

		frame.addEndTurnListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (GameState.running()) {
					conn.send("end-turn");
				} else {
					conn.send("start-game");
					frame.updateMyPlayerText(Player.search(name));
				}
			}
		});

		frame.addUpdatePlayerListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.updateMyPlayerText(Player.search(name));
			}
		});

		frame.addTradeListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(frame.showDialog("Welcher Spieler"));
				System.out.println(frame.showDialog("Welches Grundstück"));
			}
		});

		frame.addBuyHouseListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//conn.send("add-house " + Player.getPos());
			}
		});

		frame.addBuyPlotListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//conn.send("buy-plot " + Player.getPos());
			}
		});

		frame.addPayPrisonListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				conn.send("unjail money");
			}
		});

		frame.addUsePrisonLeaveListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				conn.send("unjail card");
			}
		});

		frame.addClearChatListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.chatDisp.reset();
			}
		});
	}

	public class TimerTick extends TimerTask {

		@Override
		public void run() {
			frame.updateMyPlayerText(Player.search(name));
		}
	}

}
