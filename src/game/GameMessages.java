package game;

public class GameMessages {

    public static final String START_GAME = "THE GAME IS ON! ";
    public static final String COMMAND_LIST = """

            List of available commands:
            ___________________________

            Command                                     Description
            -------                                     -----------
            /myhand                                     Shows your hand of cards
            /bet <CardName Criminal; CardName Place; CardName Weapon>
                                                       To throw your bet in your turn
            /finalbet <CardName Criminal; CardName Place; CardName Weapon>
                                                       Once one time in the game: if you guess the cards in the envelope you win, otherwise you lose and quit the game
            /showmissing                                Get the List of cards you did not see yet
            /whisper <username> <message>               Lets you whisper a message to a single connected client
            /showcard <cardName>                        Show your opponent a card present in your hand
            /help                                       Show all commands
            /name <new name>                            Lets you change your name
            /quit                                       Exits the server

            __________________________________________________________________________________________________
            """;
    public static final String INVALID_COMMAND = "No such command\n";
    public static final String WAITING_FOR_PLAYER_JOIN = "Waiting for another player to join...";
    public static final String ENTER_NAME = "NickName: ";
    public static final String CLIENT_ERROR = "Something went wrong with this client's connection. Error: ";
    public static final String OPPONENT_TURN = "\n\nIt's %s's turn. wait for his bet...\n\n";
    public static final String PLAYER_TURN = "\n\n It's your turn. Make a bet! \n\n";
    public static final String SHOW_HAND_OF_CARDS = "\nYOUR HAND OF CARDS YOU CAN SHOW TO YOUR OPPONENT \n\n";
    public static final String YOU_DONT_HAVE_THAT_CARD = "You don't have that card. Please answer correctly.";
    public static final String OPPONENT_HAS_A_CARD = " has a card to show you: \n\n ";
    public static final String YOU_BET = "You sent your bet:  ";
    public static final String OPPONENT_DOESNT_HAVE_CARD_TO_SHOW = "\n\nYour ooponent doesn't have any card to show you!";

}
