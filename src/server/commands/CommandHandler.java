import game.Game;
import server.Server;

public interface CommandHandler {
    void execute(Game game, Game.PlayerConnectionHandler playerConnectionHandler);
}