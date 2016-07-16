package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.ScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import net.Subscribe;
import model.Model;
import model.Player;

public class Frame extends JFrame implements Subscribe.SubscribeErrer {

	public final ChatDisp chatDisp;
	public final PlayerDisp playerDisp;
	public final PieceDisp pieceDisp;
	public final StartDisp startDisp;
	private Model m;
	private JPanel pLeft;
	private JPanel pChat;
	private JPanel pBottom;
	private JPanel pCurrentPlayer;
	private JPanel pChatSeperat;
	private JButton bUpdatePlayer;
	private JButton bTrade;
	private JButton bBuyHouse;
	private JButton bBuyPlot;
	private JButton bSurrender;
	private JButton bPayPrison;
	private JButton bUsePrisonLeave;
	private JButton bClearChat;
	private JTextField tChatField;
	private JTextPane tChatBox;
	private JTextPane tPlayerList;
	private JLabel lmP;
	private JLabel lmPMoney;
	private JLabel lmPPlots;
	private JButton bEndTurn;
	private ScrollPane sP;
	private Gameboard gameboard;

	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Frame(null);
			}
		});
	}

	public Frame(Model m) {
		this.m = m;
		chatDisp = new ChatDisp();
		playerDisp = new PlayerDisp();
		pieceDisp = new PieceDisp();
		startDisp = new StartDisp();
		setMinimumSize(new Dimension(800, 800));
		setPreferredSize(new Dimension(1920, 1080));
		createFrame();
		setBackground(new Color(247, 247, 124));
	}

	/**
	 * Create the frame.
	 */
	public void createFrame() {
		setTitle("MPMP");
		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameboard = new Gameboard(this);

		//Set background
		try {
			setContentPane(new JLabel(new ImageIcon(ImageIO.read(new File("graphics/background.png")))));
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Set all layouts
		setLayout(new BorderLayout());
		pLeft = new JPanel();
		pChat = new JPanel();
		pBottom = new JPanel();
		pCurrentPlayer = new JPanel();
		pChatSeperat = new JPanel();

		pLeft.setLayout(new BoxLayout(pLeft, BoxLayout.Y_AXIS));
		pChat.setLayout(new BoxLayout(pChat, BoxLayout.Y_AXIS));
		pBottom.setLayout(new BoxLayout(pBottom, BoxLayout.X_AXIS));
		pCurrentPlayer.setLayout(new BoxLayout(pCurrentPlayer, BoxLayout.Y_AXIS));
		pChatSeperat.setLayout(new BoxLayout(pChatSeperat, BoxLayout.X_AXIS));

		bTrade = new JButton("Tauschen");
		bBuyHouse = new JButton("Haus kaufen");
		bBuyPlot = new JButton("Straße kaufen");
		bSurrender = new JButton("Aufgeben");
		bEndTurn = new JButton("Spiel starten");
		bClearChat = new JButton("Chat leeren");
		bPayPrison = new JButton("Aus dem Gefängnis freikaufen");
		bPayPrison.setVisible(false);
		bUsePrisonLeave = new JButton("Benutze Gefängnis-Frei-Karte");
		bUsePrisonLeave.setVisible(false);
		bUpdatePlayer = new JButton("Update Spieler");
		bUpdatePlayer.setVisible(false);

		lmP = new JLabel("Kein Spieler");
		lmPMoney = new JLabel("RM 0");
		lmPPlots = new JLabel("Keine Grundstücke");

		tPlayerList = new JTextPane();
		tPlayerList.setEditable(false);
		tPlayerList.setSize(new Dimension(200, 500));

		tChatBox = new JTextPane();
		tChatBox.setEditable(false);
		tChatBox.setSize(300, 500);
		tChatBox.setMaximumSize(new Dimension(300, 500));

		sP = new ScrollPane();
		sP.setMaximumSize(new Dimension(300, 500));
		sP.setSize(300, 500);
		sP.add(tChatBox);

		tChatField = new JTextField();
		tChatField.requestFocus(true);
		tChatField.setSelectionColor(Color.pink);

		pLeft.add(new JLabel("Alle Spieler:"));
		pLeft.add(tPlayerList);
		pLeft.add(bPayPrison);
		pLeft.add(bUsePrisonLeave);

		pChatSeperat.add(bClearChat);
		pChatSeperat.add(bEndTurn);

		pChat.add(sP);
		pChat.add(tChatField);
		pChat.add(pChatSeperat);
		pChat.add(bUpdatePlayer);

		pCurrentPlayer.add(new JLabel("Spieler"));
		pCurrentPlayer.add(lmP);
		pCurrentPlayer.add(lmPMoney);
		pCurrentPlayer.add(lmPPlots);

		pLeft.add(bTrade);
		pLeft.add(bBuyHouse);
		pLeft.add(bBuyPlot);
		pLeft.add(bSurrender);
		pLeft.add(bPayPrison);
		pChat.add(bUsePrisonLeave);
		pBottom.add(pCurrentPlayer);

		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				gameboard.repaint();
			}
		});

		add(gameboard, BorderLayout.CENTER);
		add(pBottom, BorderLayout.SOUTH);
		add(pLeft, BorderLayout.WEST);
		add(pChat, BorderLayout.EAST);

		setVisible(true);
		pack();
	}

	public void addChatListener(KeyAdapter k) {
		tChatField.addKeyListener(k);
	}

	public String getChat() {
		String chat = tChatField.getText();
		tChatField.setText("");
		return chat;
	}

	public void addEndTurnListener(ActionListener al) {
		bEndTurn.addActionListener(al);
	}

	public void addTradeListener(ActionListener al) {
		bTrade.addActionListener(al);
	}

	public void addBuyHouseListener(ActionListener al) {
		bBuyHouse.addActionListener(al);
	}

	public void addBuyPlotListener(ActionListener al) {
		bBuyPlot.addActionListener(al);
	}

	public void addSurrenderListener(ActionListener al) {
		bSurrender.addActionListener(al);
	}

	public void addUpdatePlayerListener(ActionListener al) {
		bUpdatePlayer.addActionListener(al);
	}

	public void addPayPrisonListener(ActionListener al) {
		bPayPrison.addActionListener(al);
	}

	public void addUsePrisonLeaveListener(ActionListener al) {
		bUsePrisonLeave.addActionListener(al);
	}

	public void addClearChatListener(ActionListener al) {
		bClearChat.addActionListener(al);
	}

	public void startGame() {
		bEndTurn.setText("Runde beenden");
		bUpdatePlayer.setVisible(true);
	}

	@Override
	public void subscribeErr() {
		JOptionPane.showMessageDialog(this, "Name taken or running game!");
		System.exit(0);
	}
	
	public String showDialog(String msg){
		return gameboard.showPrompt(msg);
	}

	public void updateMyPlayerText(Player p) {
		if (p != null) {
			if (p.inPrison) {
				lmP.setText(p.getName() + "(Im Gefängnis)");
			} else {
				lmP.setText(p.getName());
			}

			lmP.setForeground(p.getColor());
			bUsePrisonLeave.setVisible(p.isInJail());
			bPayPrison.setVisible(p.isInJail());
			lmPMoney.setText("RM " + p.getMoney());
			lmPPlots.setText("Gekaufte Grundstücke: " + p.getPlots());
		}
	}

	/**
	 * Append text to a text pane in that color and boldness.
	 */
	public void append(JTextPane tp, String s, Color col, boolean bold) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet as;
		StyledDocument doc;
		int start, end;

		as = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, col);
		as = sc.addAttribute(as, StyleConstants.Alignment, StyleConstants.ALIGN_LEFT);
		as = sc.addAttribute(as, StyleConstants.Bold, bold);

		doc = tp.getStyledDocument();
		start = doc.getLength();
		end = start + s.length();
		try {
			doc.insertString(start, s + "\n", null);
		} catch (BadLocationException ble) {
			;// XXX how to handle?
		}

		doc.setCharacterAttributes(start, end, as, true);
	}

	public class ChatDisp implements Displayer {

		/**
		 * Take a string and an optional color and display it in the chat box.
		 */
		@Override
		public synchronized void show(Object... args) {
			String s = (String) args[0];
			final Color col;

			if (args.length == 2) {
				col = (Color) args[1];
			} else {
				col = Color.BLACK;
			}

			// Call in the Event Dispatching Thread.
			SwingUtilities.invokeLater(() -> {
				append(tChatBox, s, col, false);
			});
		}

		@Override
		public void reset() {
			tChatBox.setText("");
		}
	}

	public class PlayerDisp implements Displayer {

		/**
		 * Take a Player and display it in the player list.
		 */
		@Override
		public synchronized void show(Object... args) {
			final Player p = (Player) args[0];

			SwingUtilities.invokeLater(() -> {
				append(tPlayerList, "" + p, p.getColor(), true);
			});
		}

		@Override
		public void reset() {
			SwingUtilities.invokeLater(() -> {
				tPlayerList.setText("");
			});
		}

	}

	public class PieceDisp implements Displayer {

		@Override
		public void show(Object... args) {
		}

		@Override
		public void reset() {
			SwingUtilities.invokeLater(() -> {
				gameboard.repaint();
			});
		}
	}

	public class StartDisp implements Displayer {

		@Override
		public void show(Object... args) {
		}

		/**
		 * 'Changes' the StartGame button to a EndTurn button
		 */
		@Override
		public void reset() {
			startGame();
		}
	}
}
