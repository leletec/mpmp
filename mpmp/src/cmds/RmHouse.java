package cmds;

import java.util.Arrays;
import main.Client;
import main.Conn;
import main.ErrCode;
import model.Player;
import model.Plot;

/**
 * C->S
 * @author Leander
 */
public class RmHouse implements CmdFunc {

	@Override
	public void exec(String line, Conn conn) {
		String[] args = line.split(" ");

		Player p = Player.search(((Client) conn).getName());
		if (!p.isPlayer()) {
			conn.sendErr(ErrCode.NotAPlayer);
			return;
		}

		if (args.length < 2) {
			conn.sendErr(ErrCode.Usage, "rm-house <Grundstück>");
			return;
		}

		Plot plot = Plot.search(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
		if (plot == null) {
			conn.sendErr(ErrCode.NotAPlot);
			return;
		}

		if (plot.getOwner() != p) {
			conn.sendErr(ErrCode.AlreadyOwned, plot.getOwner().getName());
			return;
		}
		
		switch (plot.rmHouse()) {
		case -1:
			conn.sendErr(ErrCode.DontHave, "a single house");
			return;
		case -2:
			conn.sendErr(ErrCode.UnbalancedColor);
			return;
		case 1:
			conn.sendOK();
			conn.send("show-transaction " + plot.getHousePrice(plot.getHouses()+1)/2 + " Sell house for plot " + plot.getName());
			conn.send("plot-update " + plot.getName() + " " + plot.getHouses() + " " + plot.isHypothec() + plot.getOwner());
			break;
		default:
			conn.sendErr(ErrCode.Internal, "Unexpected error");
		}
		
		
	}

	@Override
	public void error(ErrCode err, String line, Conn conn) {
		//TODO
	}
	
}
