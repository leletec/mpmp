package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import model.Player;

/**
 * Main class of the server.
 *
 * @author Leander, oki
 */
public class SrvMain {
	public static void srvmain(String[] args) {
		ServerSocket listener;
		Client.reset();
		Player.reset();

		try {
			listener = new ServerSocket(1918);

			// XXX total exception madness - WHY, JAVA, WHY?
			for (;;) {
				final Socket sock = listener.accept();
				new Thread(() -> {
					Client c = null;
					System.out.println("Client connected through " + sock);
					try {
						c = new Client(sock);
						c.handle();
					} catch (IOException ioe) {
						/* don't do anything; fall through to finally */
					} finally {
						System.out.println("Client " + sock + " disconnected");
						if(c != null)
							c.remove();
					}
				}).start();
			}
		}
		catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		}
	}
}
