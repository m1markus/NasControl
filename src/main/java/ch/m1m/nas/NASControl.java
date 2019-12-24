package ch.m1m.nas;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class NASControl {

    private static final String OPT_HELP = "help";
    private static final String OPT_VERSION = "version";

    private static final Logger LOGGER = LoggerFactory.getLogger(NASControl.class);

    public static void main(String... args) {
        Config config = ConfigUtils.loadConfiguration();

        // https://commons.apache.org/proper/commons-cli/usage.html
        //
        Options options = new Options();
        options.addOption("h", OPT_HELP, false, "print usage");
        options.addOption("v", OPT_VERSION, false, "print version information");

        CommandLineParser parser = new DefaultParser();
        CommandLine clArgs;

        try {
            clArgs = parser.parse(options, args);

            if (clArgs.hasOption(OPT_HELP)) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(config.getProgramName() + " [options]", options);
                System.exit(0);
            }

            if (clArgs.hasOption(OPT_VERSION)) {
                LOGGER.info("{} version {}", config.getProgramName(), config.getVersion());
                System.exit(0);
            }

        } catch (ParseException e) {
            LOGGER.error("parsing command line: {}", e.getMessage());
            System.exit(1);
        }

        List<String> liArgs = Arrays.asList(args);
        LOGGER.info("start {} {} with args: {}", config.getProgramName(), config.getVersion(), liArgs);

        DriverFreeNAS nasDriver = new DriverFreeNAS(config);

        TrayIconUI trayIconUI = new TrayIconUI(config, nasDriver);
        trayIconUI.executeStatusLoop();
    }
}
