package ch.m1m.nas.app;

import ch.m1m.nas.lib.Config;
import ch.m1m.nas.lib.ConfigUtils;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// https://phrase.com/blog/posts/a-beginners-guide-to-java-internationalization/
//
// https://github.com/dustinkredmond/FXTrayIcon/blob/main/src/test/java/com/dustinredmond/fxtrayicon/RunnableTest.java

public class NASControl extends Application {
    private static final String PROGRAM_NAME = "NASControl";
    private static final String OPT_HELP = "help";
    private static final String OPT_VERSION = "version";

    private static final Logger LOG = LoggerFactory.getLogger(NASControl.class);
    private static Stage stage;

    public static void main(String... args) {
        CommandLine clArgs = setupAndParseArgs(args);
        LOG.info("start {} {} with args: {}", PROGRAM_NAME, Version.getProjectVersion(), clArgs.getArgList());
        launch();
    }

    public static Stage getUiStage() {

        return stage;
    }

    @Override
    public void start(Stage inStage) {

        stage = inStage;
        Config config = ConfigUtils.loadConfiguration();

        // begin UI
        //
        var xVersion = "x.y.z";
        var label = new Label("Hello, JavaFX " + xVersion);
        var scene = new Scene(new StackPane(label), 640, 480);
        stage.setScene(scene);
        //stage.show();

        //
        // end UI

        TrayIconUI trayIconUI = new TrayIconUI(config);

        Thread backgroundThread = new Thread(trayIconUI::executeStatusLoop);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    public static CommandLine setupAndParseArgs(String... args) {
        // https://commons.apache.org/proper/commons-cli/usage.html
        //
        Options options = new Options();
        options.addOption("h", OPT_HELP, false, "print usage");
        options.addOption("v", OPT_VERSION, false, "print version information");

        CommandLineParser parser = new DefaultParser();
        CommandLine clArgs = null;

        try {
            clArgs = parser.parse(options, args);

            if (clArgs.hasOption(OPT_HELP)) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(PROGRAM_NAME + " [options]", options);
                System.exit(0);
            }

            if (clArgs.hasOption(OPT_VERSION)) {
                LOG.info(PROGRAM_NAME + " version " + Version.getProjectVersion());
                System.exit(0);
            }

        } catch (ParseException e) {
            LOG.error("parsing command line: {}", e.getMessage());
            System.exit(1);
        }

        return clArgs;
    }
}
