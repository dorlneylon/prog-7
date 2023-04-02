package itmo.lab7.basic.utils.files;

import itmo.lab7.commands.Command;
import itmo.lab7.commands.CommandFactory;
import itmo.lab7.commands.CommandType;
import itmo.lab7.commands.CommandUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

import static itmo.lab7.commands.CollectionValidator.getCollectionSize;

/**
 * ScriptExecutor is used to execute script files
 */
public class ScriptExecutor {
    /**
     * Command list
     */
    private final ArrayList<Command> commandQueue = new ArrayList<>();
    /**
     * Main script file
     */
    private final File scriptFile;
    /**
     * List of executing commands
     */
    private final ArrayDeque<File> filesMemory = new ArrayDeque<>();

    public ScriptExecutor(File scriptFile) {
        this.scriptFile = scriptFile;
    }

    /**
     * Returns command list
     *
     * @return command list
     */
    public ArrayList<Command> getCommandList() {
        return commandQueue;
    }

    /**
     * Reads given file line by line and adds commands to the command list
     *
     * @param scriptFile File to execute
     * @return this
     */
    private ScriptExecutor readScript(File scriptFile) {
        int insertsCount = 0;
        List<String> lines;
        try {
            lines = Files.readAllLines(scriptFile.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        filesMemory.add(scriptFile);
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            CommandType commandType = CommandUtils.getCommandType(line.split(" ")[0]);
            String[] args = Arrays.copyOfRange(line.split(" "), 1, line.split(" ").length);
            if (commandType == CommandType.EXECUTE_SCRIPT) {
                if (filesMemory.contains(new File(args[0]))) {
                    System.err.println("Recursive file execution. Skipping line: " + line);
                    continue;
                }
                if (FileUtils.isFileExist(args[0])) {
                    this.readScript(new File(args[0]));
                    continue;
                }
            }

            if (Set.of(CommandType.SHOW, CommandType.PRINT_ASCENDING, CommandType.PRINT_DESCENDING).contains(commandType)) {
                int size = insertsCount;

                try {
                    size += getCollectionSize();
                } catch (Exception e) {
                    continue;
                }
                for (int i = 0; i * 20 < size; i++) {
                    Command cmd = CommandFactory.createCommand(commandType, new String[]{String.valueOf(i * 20)});
                    if (cmd != null) commandQueue.add(cmd);
                }

                continue;
            }

            if (Set.of(CommandType.INSERT, CommandType.UPDATE, CommandType.REPLACE_IF_LOWER).contains(commandType)) {
                if (args.length < 1 || index + 13 >= lines.size()) {
                    System.err.println("Not enough arguments/data for command " + commandType + ". Skipping line: " + line);
                    continue;
                }
                String[] movieArgs = lines.subList(index + 1, index + 14).toArray(new String[0]);
                args = new String[movieArgs.length + 1];
                args[0] = line.split(" ")[1];
                System.arraycopy(movieArgs, 0, args, 1, movieArgs.length);
                index += 13;
                if (commandType.equals(CommandType.INSERT)) insertsCount++;
            }
            
            Command cmd = CommandFactory.createCommand(commandType, args);
            if (cmd != null) commandQueue.add(cmd);
        }
        filesMemory.pop();
        return this;
    }


    public ScriptExecutor readScript() {
        return this.readScript(scriptFile);
    }
}
