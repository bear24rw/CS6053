class SillyPlayerWrapper extends Player {
   SillyPlayerWrapper(String foo, Economy e)  { super(foo, e);}
   String getPlayerPassword(Player p) { return p.playerPassword; }
   String getWarReport(Player p) {
      return new String("fought " + p.warsFought + " declared " +
			p.warsDeclared  + " won " + p.warsWon + 
			" lost " + p.warsLost + " truce " + p.warsTruce);
   }
   int getWarsWon(Player p) { return p.warsWon; }
   int getWarsLost(Player p) { return p.warsLost; }
   int getWarsDeclared(Player p) { return p.warsDeclared; }
   long getWarsTruce(Player p) { return p.warsTruce;  }
   long getWarsFought(Player p) { return p.warsFought; }
}
