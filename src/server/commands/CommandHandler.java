import game.Game;
import server.Server;

@FunctionalInterface
public interface CommandHandler {
    void handleCommands(Game game, Game.PlayerConnectionHandler playerConnectionHandler);
}