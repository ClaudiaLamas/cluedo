import game.Game;
import server.Server;

public interface CommandHandler {
    void handleCommands(Game game, Game.PlayerConnectionHandler playerConnectionHandler);
}