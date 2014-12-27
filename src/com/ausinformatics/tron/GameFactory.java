package com.ausinformatics.tron;

import java.util.List;
import java.util.Random;

import com.ausinformatics.phais.core.interfaces.GameBuilder;
import com.ausinformatics.phais.core.interfaces.GameInstance;
import com.ausinformatics.phais.core.interfaces.PersistentPlayer;
import com.ausinformatics.phais.core.server.DisconnectedException;

public class GameFactory implements GameBuilder {

	private int boardSize = 25;

	public GameInstance createGameInstance(List<PersistentPlayer> players) {
		int randKey = new Random().nextInt();
		
		for (PersistentPlayer p : players) {
			String toSend = "NEWGAME " + players.size() + " " + boardSize + " " + randKey;
			p.getConnection().sendInfo(toSend);
			
			try {
				while (true) {
					String inputString = p.getConnection().getStrInput();
					String[] tokens = inputString.split("\\s");
					if (tokens.length != 2) {
						continue;
					} else if (!tokens[0].equals("READY")) {
						continue;
					}
					
					try {
						if (Integer.parseInt(tokens[1]) == randKey) {
							break;
						}
					} catch (NumberFormatException nfe) {
						continue;
					}
				}
			} catch (DisconnectedException de) {
				p.getConnection().disconnect();
			}
		}
		return new GameRunner(players, boardSize);
	}

}
