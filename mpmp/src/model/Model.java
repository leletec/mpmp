package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A thing.
 *
 * Specifically, this is the client/small model containing data that can be updated.
 * The game logic resides only in the server parts.
 */
public class Model {
	public enum GameState {
		Pregame, Running;
	}

	private GameState gs;

	private Map<String, Player> players;    /* players, no spectators */
	private Player currentPlayer;

	private static Map<Integer, Plot> plots;
	private static ArrayList<PlotGroup> pgroups;

	public Model() {
		players = new HashMap<>();
		currentPlayer = null;
		plots = new HashMap<>();
		pgroups = new ArrayList<>();
	}

	public void addPlayer(Player p) {
		if(gs == GameState.Running)
			return;

		players.put(p.getName(), p);
	}

	public void rmPlayer(Player p) {
		players.remove(p.getName());
	}

	public void resetPlayers() {
		players = new HashMap<>();
	}

	public Player getPlayer(String name) {
		return players.get(name);
	}

	public Iterable<Player> getPlayers() {
		return players.values();
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	/**
	 * Sets the current player. Null is allowed.
	 */
	public void setCurrentPlayer(Player p) {
		currentPlayer = p;
	}

	public Plot getPlot(int pos) {
		pos %= Field.Nfields;
		return plots.get(new Integer(pos));
	}

	public boolean running() {
		return gs == GameState.Running;
	}

	public void startGame() {
		gs = GameState.Running;
	}

	/* STATIC */

	public static void init() throws IOException {
		initPlots();
		Card.init();
	}

	/* This code is generated by "cd scripts; awk -f refmt-plots.awk <../plots.md | awk -f mkplots.awk". */
	private static void initPlots() {
		/* generated at Sun 31 Jul 14:33:27 CEST 2016 */

		PlotGroup PGstations = new PlotGroup(0 | PlotGroup.NoBuild);

		PlotGroup PG30041c = new PlotGroup(0);
		HousePlot HPKüstenbahndamm = new HousePlot(PG30041c, "Küstenbahndamm", 1200, new int[]{40, 200, 600, 1800, 3200}, 1000);
		PG30041c.add(HPKüstenbahndamm);
		addPlot(1, HPKüstenbahndamm);
		HousePlot HPZimmererstraße = new HousePlot(PG30041c, "Zimmererstraße", 1200, new int[]{80, 400, 1200, 3600, 6400}, 1000);
		PG30041c.add(HPZimmererstraße);
		addPlot(3, HPZimmererstraße);

		PlotGroup PG91c3d8 = new PlotGroup(0);
		HousePlot HPWasserschifffahrtsamt = new HousePlot(PG91c3d8, "Wasserschifffahrtsamt", 2000, new int[]{120, 600, 1800, 5400, 8000}, 1000);
		PG91c3d8.add(HPWasserschifffahrtsamt);
		addPlot(6, HPWasserschifffahrtsamt);
		HousePlot HPAugustBebelStraße = new HousePlot(PG91c3d8, "August-Bebel-Straße", 2000, new int[]{120, 600, 1800, 5400, 8000}, 1000);
		PG91c3d8.add(HPAugustBebelStraße);
		addPlot(8, HPAugustBebelStraße);
		HousePlot HPAmEisenbahndock = new HousePlot(PG91c3d8, "Am Eisenbahndock", 2400, new int[]{160, 800, 2000, 6000, 9000}, 1000);
		PG91c3d8.add(HPAmEisenbahndock);
		addPlot(9, HPAmEisenbahndock);

		PlotGroup PG860459 = new PlotGroup(0);
		HousePlot HPFaldernstraße = new HousePlot(PG860459, "Faldernstraße", 2800, new int[]{200, 1000, 2000, 6000, 9000}, 2000);
		PG860459.add(HPFaldernstraße);
		addPlot(11, HPFaldernstraße);
		HousePlot HPSiegesAllee = new HousePlot(PG860459, "Sieges-Allee", 2800, new int[]{200, 1000, 3000, 9000, 12500}, 2000);
		PG860459.add(HPSiegesAllee);
		addPlot(13, HPSiegesAllee);
		HousePlot HPNeueStraße = new HousePlot(PG860459, "Neue Straße", 3200, new int[]{240, 1200, 3600, 10000, 14000}, 2000);
		PG860459.add(HPNeueStraße);
		addPlot(14, HPNeueStraße);

		PlotGroup PGde5126 = new PlotGroup(0);
		HousePlot HPGorchFockStraße = new HousePlot(PGde5126, "Gorch-Fock-Straße", 3600, new int[]{280, 1400, 4000, 11000, 15000}, 2000);
		PGde5126.add(HPGorchFockStraße);
		addPlot(16, HPGorchFockStraße);
		HousePlot HPAmBurggraben = new HousePlot(PGde5126, "Am Burggraben", 3600, new int[]{280, 1400, 4000, 11000, 15000}, 2000);
		PGde5126.add(HPAmBurggraben);
		addPlot(18, HPAmBurggraben);
		HousePlot HPBollwerkstraße = new HousePlot(PGde5126, "Bollwerkstraße", 4000, new int[]{320, 1600, 4400, 12000, 16000}, 2000);
		PGde5126.add(HPBollwerkstraße);
		addPlot(19, HPBollwerkstraße);

		PlotGroup PGd01f26 = new PlotGroup(0);
		HousePlot HPPhilosophenweg = new HousePlot(PGd01f26, "Philosophenweg", 4400, new int[]{360, 1800, 5000, 14000, 17500}, 3000);
		PGd01f26.add(HPPhilosophenweg);
		addPlot(21, HPPhilosophenweg);
		HousePlot HPOttovonBismarckStraße = new HousePlot(PGd01f26, "Otto-von-Bismarck-Straße", 4400, new int[]{360, 1800, 5000, 14000, 17500}, 3000);
		PGd01f26.add(HPOttovonBismarckStraße);
		addPlot(23, HPOttovonBismarckStraße);
		HousePlot HPFriedrichEbertStraße = new HousePlot(PGd01f26, "Friedrich-Ebert-Straße", 4800, new int[]{400, 2000, 6000, 15000, 18500}, 3000);
		PGd01f26.add(HPFriedrichEbertStraße);
		addPlot(24, HPFriedrichEbertStraße);

		PlotGroup PGfbe821 = new PlotGroup(0);
		HousePlot HPSchützenstraße = new HousePlot(PGfbe821, "Schützenstraße", 5200, new int[]{440, 2200, 6600, 16000, 19500}, 3000);
		PGfbe821.add(HPSchützenstraße);
		addPlot(26, HPSchützenstraße);
		HousePlot HPHafenstraße = new HousePlot(PGfbe821, "Hafenstraße", 5200, new int[]{440, 2200, 6600, 16000, 19500}, 3000);
		PGfbe821.add(HPHafenstraße);
		addPlot(28, HPHafenstraße);
		HousePlot HPHindenburgStraße = new HousePlot(PGfbe821, "Hindenburg-Straße", 5600, new int[]{480, 2400, 7200, 17000, 20500}, 3000);
		PGfbe821.add(HPHindenburgStraße);
		addPlot(29, HPHindenburgStraße);

		PlotGroup PG168140 = new PlotGroup(0);
		HousePlot HPJahnstraße = new HousePlot(PG168140, "Jahnstraße", 6000, new int[]{520, 2600, 7800, 18000, 22000}, 4000);
		PG168140.add(HPJahnstraße);
		addPlot(31, HPJahnstraße);
		HousePlot HPKarlMarxStraße = new HousePlot(PG168140, "Karl-Marx-Straße", 6000, new int[]{520, 2600, 7800, 18000, 22000}, 4000);
		PG168140.add(HPKarlMarxStraße);
		addPlot(32, HPKarlMarxStraße);
		HousePlot HPAmBollwerk = new HousePlot(PG168140, "Am Bollwerk", 6400, new int[]{560, 3000, 9000, 20000, 24000}, 4000);
		PG168140.add(HPAmBollwerk);
		addPlot(34, HPAmBollwerk);

		PlotGroup PG183a66 = new PlotGroup(0);
		HousePlot HPPariserplatz = new HousePlot(PG183a66, "Pariserplatz", 7000, new int[]{700, 3500, 10000, 22000, 26000}, 4000);
		PG183a66.add(HPPariserplatz);
		addPlot(37, HPPariserplatz);
		HousePlot HPTriumphstraße = new HousePlot(PG183a66, "Triumphstraße", 8000, new int[]{1000, 4000, 12000, 28000, 34000}, 4000);
		PG183a66.add(HPTriumphstraße);
		addPlot(39, HPTriumphstraße);
		TrainStation TSCentralbahnhof = new TrainStation(PGstations, "Centralbahnhof", 4000);
		PGstations.add(TSCentralbahnhof);
		addPlot(5, TSCentralbahnhof);
		TrainStation TSAnhalterBahnhof = new TrainStation(PGstations, "Anhalter Bahnhof", 4000);
		PGstations.add(TSAnhalterBahnhof);
		addPlot(15, TSAnhalterBahnhof);
		TrainStation TSBahnhofWeimar = new TrainStation(PGstations, "Bahnhof Weimar", 4000);
		PGstations.add(TSBahnhofWeimar);
		addPlot(25, TSBahnhofWeimar);
		TrainStation TSFlügelbahnhof = new TrainStation(PGstations, "Flügelbahnhof", 4000);
		PGstations.add(TSFlügelbahnhof);
		addPlot(35, TSFlügelbahnhof);

	}

	private static void addPlot(int pos, Plot plot) {
		plots.put(new Integer(pos), plot);
	}
}
