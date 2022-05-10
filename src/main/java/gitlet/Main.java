package gitlet;


/** Driver class for Gitlet, the tiny super rad version-control system.
 *  @author ELARABY ELAIDY */
public class Main {

    /** This is where the magic happens!
     * @param args - user input */
    public static void main(String... args) {
        try {
            Main.oOoOoO(args);
            return;
        } catch (GitletException e) {
            System.err.printf("%s%n", e.getMessage());
        }
        System.exit(0);

    }

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND>. */
    public static void oOoOoO(String... args) {
        Repository gitletRepository = new Repository();
        if (args.length < 1) {
            throw new GitletException("Please enter a command.");
        }
        if (!args[0].equals("init") && !gitletRepository.getGitRepository().exists()) {
            throw new
                    GitletException("Not in an initialized Gitlet directory.");
        }

        switch (args[0]) {
        case "init": gitletRepository.init();
        break;

        case "add": gitletRepository.add(args[1]);
        break;

        case "commit": gitletRepository.commit(args[1], null);
        break;

        case "log": gitletRepository.log();
        break;

        case "global-log": gitletRepository.globalLog();
        break;

        case "find": gitletRepository.find(args[1]);
        break;

        case "status": gitletRepository.status();
        break;

        case "checkout": if (args.length != 3
                && args.length != 4 && args.length != 2) {
                throw new GitletException("Incorrect operands.");
            }
            gitletRepository.checkout(args);
        break;

        case "branch": gitletRepository.branch(args[1]);
        break;

        case "rm-branch": gitletRepository.rmBranch(args[1]);
        break;

        case "rm" : gitletRepository.rm(args[1]);
        break;

        case "reset" : gitletRepository.reset(args[1]);
        break;

        case "merge" : gitletRepository.merge(args[1]);
        break;

        default: throw new GitletException("No command with that name exists.");

        }
    }
}