package eu.clarussecure.secpolmgmt;

public class CommandParser {
    // Singleton implementation.
    private static CommandParser instance = null;

    private CommandParser() {
    }

    public static CommandParser getInstance() {
        if (CommandParser.instance == null)
            CommandParser.instance = new CommandParser();

        return CommandParser.instance;
    }

    public Command parse(String[] command) throws CommandParserException {
        Command com = null;

        // This is where the delegation occurs.
        // Depending on the command provided, the parser will create the correct instance of the Command object
        // Extend this switch with new cases to support new commands
        try {
            switch (command[0].toLowerCase()) {
            case "create":
                com = new Create(command);
                break;
            case "set_dataspace_endpoint":
                com = new SetDataspaceEndpoint(command);
                break;
            case "set_attribute_type":
                com = new SetAttributeType(command);
                break;
            case "set_data_usage":
                com = new SetDataUsage(command);
                break;
            case "set_protection_module":
                com = new SetProtectionModule(command);
                break;
            case "set_protection_param":
                com = new SetProtectionParam(command);
                break;
            case "register":
                com = new Register(command);
                break;
            case "delete":
                com = new Delete(command);
                break;
            case "list":
                com = new List(command);
                break;
            case "convert_policy":
                com = new ConvertPolicy(command);
                break;
            case "-h":
                // Show help
                throw new ArrayIndexOutOfBoundsException();
                // AKKA request
            case "set_protocol_param":
                com = new SetProtocolParam(command);
                break;
            default:
                throw new CommandParserException("Unrecognized command '" + command[0] + "'");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // Show help
            com = new Help(command);
        }
        return com;
    }
}
