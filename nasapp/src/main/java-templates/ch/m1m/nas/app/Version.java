package ch.m1m.nas.app;

public class Version {

    private static final String MAVEN_PROJECT_VERSION = "${project.version}";

    public static String getProjectVersion() {
        return MAVEN_PROJECT_VERSION;
    }
}
