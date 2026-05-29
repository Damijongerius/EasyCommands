    // 1. Dependency Injection (@Sender) & Modifiers
    // /guild delete
    @SubCommand(commandPath = {}, name = "delete", description = "Delete your guild")
    @Confirm(timeout = 10) // Forces player to run the command again within 10s
    @Require("is_guild_leader") // Checks if they are the leader
    @Async // Runs the logic off the main server thread
    public void deleteGuild(@Sender GuildPlayer player) {
        
        player.getGuild().delete();
        player.sendMessage("Your guild was disbanded.");
        
        // Throw business logic exceptions to be caught globally!
        throw new SuccessException("Guild deleted successfully.");
    }