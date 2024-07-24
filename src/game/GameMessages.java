package game;

public class GameMessages {

    public static final String START_GAME = "THE GAME IS ON! ";
    public static final String COMMAND_LIST = """
            List of available commands:
            ___________________________

            /myhand\t\t\t -> Shows your hand of cards
            /bet <CardName Criminal; CardName Place; CardName Weapon> -> to throw your bet in your turn
            /finalbet <CardName Criminal; CardName Place; CardName Weapon> -> once one time in the game: if you guess the cards in the envelope you win, otherwise you loose and quit the game
            /list\t\t\t\t\t\t -> gets you the list of connected clients
            /whisper <username> <message>\t\t -> lets you whisper a message to a single connected client
            /help\t\t\t -> show all commands
            /name <new name>\t\t -> lets you change your name
            /quit\t\t\t -> exits the server""";
    public static final String INVALID_COMMAND = "No0 such command\n";
    public static final String WAITING_FOR_PLAYER_JOIN = "Waiting for another player to join...";
    public static final String ENTER_NAME = "NickName: ";
    public static final String CLIENT_ERROR = "Something went wrong with this client's connection. Error: ";
    public static final String OPPONENT_TURN = "It's %s's turn. wait for his bet...\\n";
    public static final String PLAYER_TURN = "It's your turn. Make a bet!";

}
