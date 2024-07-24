package server.commands;

public enum Command {

    NAME("/name", new NameHandler()),
    HELP("/help", new HelpHandler()),
    // RULES("/rules", new RulesHandler()),
    // SHOW_CARDS_NOT_SEEN("/show", new ShowNotSeenHandler()),
    BET("/bet", new BetHandler()),
    // FINAL_BET("/finalbet", new FinalBetHandler()),
    MY_HAND("/myhand", new MyHandHandler()),
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
