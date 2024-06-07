package game;

public class GameMessages {

    public static final String START_GAME = "THE GAME IS ON! ";
    public static final String COMMAND_LIST = """
            List of available commands:
            ___________________________

            /bet <CardName Criminal; CardName Place; CardName Weapon>\t\t\t -> to throw your bet in your turn
            /finalbet <CardName Criminal; CardName Place; CardName Weapon>\t\t\t -> once one time in the game: if you guess the cards in the envelope you win, otherwise you loose and quit the game
            /list\t\t\t\t\t\t -> gets you the list of connected clients
            /whisper <username> <message>\t\t\t -> lets you whisper a message to a single connected client
            /help\t\t\t\t\t\t\t\t\t -> show all commands
            /name <new name>\t\t\t\t\t\t -> lets you change your name
            /quit\t\t\t -> exits the server""";
    public static final String INVALID_COMMAND = "No0 such command\n";
    public static final String WAITING_FOR_PLAYER_JOIN = "Waiting for another player to join...";
    public static final String ENTER_NAME = "NickName: ";

}
