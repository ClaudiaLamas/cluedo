package server.commands;

public enum Command {

    HELP("/help", new HelpHandler()),
    // RULES("/rules", new RulesHandler()),
    BET("/bet", new BetHandler()),
    FINAL_BET("/finalbet", new FinalBetHandler()),
    MY_HAND("/myhand", new MyHandHandler()),
    SHOW_SEEN_CARDS("/seencards", new ShowSeenCards()),
    SHOW_CARDS_NOT_SEEN("/showmissing", new ShowNotSeenHandler()),
    SHOW_A_CARD("/showcard", new ShowCardHandler()),
    // WHISPER("/whisper", new WhisperHandler()),
    NOT_FOUND("Command not found!", new CommandNotFoundHandler());

    private String description;
    private CommandHandler handler;

    Command(String description, CommandHandler handler) {
        this.description = description;
        this.handler = handler;
    }

    public static Command getCommandDescription(String description) {
        for (Command command : values()) {
            if (description.equals(command.description)) {
                return command;
            }
        }
        return NOT_FOUND;
    }

    public CommandHandler getHandler() {
        return handler;
    }

    public String getDescription() {
        return description;
    }

}
