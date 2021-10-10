package org.foxesworld.launchserver.command.service;

import io.jsonwebtoken.Jwts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.command.Command;
import org.foxesworld.utils.command.SubCommand;

public class TokenCommand extends Command {
    private transient final Logger logger = LogManager.getLogger();
    public TokenCommand(LaunchServer server) {
        super(server);
        this.childCommands.put("info", new SubCommand("[token]", "print token info") {
            @Override
            public void invoke(String... args) throws Exception {
                verifyArgs(args, 1);
                var parser = Jwts.parserBuilder().setSigningKey(server.keyAgreementManager.ecdsaPublicKey).build();
                var claims = parser.parseClaimsJws(args[0]);
                logger.info("Token: {}", claims.getBody());
            }
        });
        this.childCommands.put("server", new SubCommand("[serverName] (authId)", "generate new server token") {
            @Override
            public void invoke(String... args) throws Exception {
                String token = server.authManager.newCheckServerToken(args[0], args.length > 1 ? args[1] : server.config.getAuthProviderPair().name);
                logger.info("Token: {}", token);
            }
        });
    }

    @Override
    public String getArgsDescription() {
        return "[new/info/token name] [args]";
    }

    @Override
    public String getUsageDescription() {
        return "jwt management";
    }

    @Override
    public void invoke(String... args) throws Exception {
        invokeSubcommands(args);
    }
}
