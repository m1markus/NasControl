package ch.m1m.nas;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SimpleDialog {

    public static void main(final String[] args) throws Exception
    {
        System.out.println("starting...");

        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }

        if (Taskbar.isTaskbarSupported())
        {
            final Taskbar taskbar = Taskbar.getTaskbar();

            showProvidedFeatures(taskbar);
            performSomeTaskbarChanges(taskbar);
        }
        else
        {
            System.out.println("Taskbar is not supported on your platform.");
        }
    }

    private static void showProvidedFeatures(final Taskbar taskbar)
    {
        System.out.println("Taskbar is supported and provides the " +
                "following features:");

        final Consumer<Taskbar.Feature> checkFeatureSupport = feature ->
                System.out.printf("Feature %s " +
                                "is supported: %s%n",feature,
                        taskbar.isSupported(feature));

        java.util.List<String> falseFeatureList = Arrays.stream(Taskbar.Feature.values())
                .filter(feature -> !taskbar.isSupported(feature))
                .map(feature -> feature.toString())
                .collect(Collectors.toList());

        falseFeatureList.forEach(item -> System.out.println("not supported " + item));

        java.util.List<String> trueFeatureList = Arrays.stream(Taskbar.Feature.values())
                .filter(feature -> taskbar.isSupported(feature))
                .map(feature -> feature.toString())
                .collect(Collectors.toList());

        trueFeatureList.forEach(item -> System.out.println("supported " + item));

        //Arrays.stream(Taskbar.Feature.values()).forEach(checkFeatureSupport);
    }

    private static void performSomeTaskbarChanges(final Taskbar taskbar)
            throws IOException, InterruptedException
    {
        setImage(taskbar);
        setBadge(taskbar, "1");

        requestUserAttention(taskbar, false); // springt einmal

        setBadge(taskbar, "2");
        setBadge(taskbar, "progress");

        requestUserAttention(taskbar, true); // springt fortwährend

        performProgressInteraction(taskbar);
    }

    private static void setImage(final Taskbar taskbar) throws IOException,
            InterruptedException
    {
        if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE))
        {
            final InputStream imageInputStream = SimpleDialog.class.getResourceAsStream("images/bulb.gif");
            final Image image = ImageIO.read(imageInputStream);
            taskbar.setIconImage(image);
            Thread.sleep(2500);
        }
    }

    private static void setBadge(final Taskbar taskbar,
                                 final String text) throws InterruptedException
    {
        if (taskbar.isSupported(Taskbar.Feature.ICON_BADGE_TEXT))
        {
            taskbar.setIconBadge(text);
            Thread.sleep(1000);
        }
    }

    private static void requestUserAttention(Taskbar taskbar,
                                             boolean critical)
            throws InterruptedException
    {
        if (taskbar.isSupported(Taskbar.Feature.USER_ATTENTION))
        {
            taskbar.requestUserAttention(true, critical);
            Thread.sleep(2500);
        }
    }

    private static void performProgressInteraction(final Taskbar taskbar)
            throws InterruptedException
    {
        if (taskbar.isSupported(Taskbar.Feature.PROGRESS_VALUE))
        {
            for (int i = 0; i < 100; i++)
            {
                taskbar.setProgressValue(i);
                Thread.sleep(100);
            }
        }
    }
}
