package ch.m1m.nas;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConfigUtilsTest {

    @Before
    public void setUp() throws Exception {
        // Workaround to fake a 'user.home' to the resource dir of this test
        String pathToTestClasses = ConfigUtilsTest.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String testUserHome = pathToTestClasses + ConfigUtilsTest.class.getPackage().getName().replace('.', '/');
        System.setProperty("user.home", testUserHome);
    }

    @Test
    public void loadConfigurationReturnsAConfig() {
        // GIVEN
        // WHEN
        Config config = ConfigUtils.loadConfiguration();

        // THEN
        assertNotNull(config);
        assertEquals("192.168.1.255", config.getBroadcastAddress());
        assertEquals("0C:9D:92:2F:39:3D", config.getMacAddress());
        assertEquals("http://freenas.local", config.getNasAdminUI());
        assertEquals("root", config.getNasUserId());
        assertEquals("changeme", config.getNasUserPassword());
        assertEquals("6.6.6", config.getVersion());
        assertEquals("NASControl", config.getProgramName());
    }
}
