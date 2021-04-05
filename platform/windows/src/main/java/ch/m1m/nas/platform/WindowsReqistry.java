package ch.m1m.nas.platform;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * @author Oleg Ryaboy, based on work by Miguel Enriquez
 */
public class WindowsReqistry {
    /**
     * @param location path in the registry
     * @param key      registry key
     * @return registry value or null if not found
     */
    public static String readRegistry(String location, String key) {
        try {
            // Run reg query, then read output with StreamReader (internal class)
            Process process = Runtime.getRuntime().exec("reg query \"" + location + "\" /v \"" + key + "\"");

            StreamReader reader = new StreamReader(process.getInputStream());
            reader.start();
            process.waitFor();
            int procExitValue = process.exitValue();
            if (procExitValue != 0) {
                return null;
            }
            reader.join();
            String output = reader.getResult();

            if (output == null) return null;

            // Output has the following format:
            // \n<Version information>\n\n<key>\t<registry type>\t<value>
            output = output.replace("\r", " ");
            output = output.replace("\n", " ");
            output = output.replace("\t", " ");
            output = output.trim();

            int actualPos = location.length() + key.length();
            int posKeyType = output.indexOf("REG_", actualPos);
            actualPos = posKeyType + 4;

            // skip all nonblank and count pos
            while (output.charAt(actualPos) != ' ')
                actualPos++;

            // skip all blank and count pos
            while (output.charAt(actualPos) == ' ')
                actualPos++;

            // now we have the value
            String result = output.substring(actualPos);
            return result;

        } catch (Exception e) {
        }
        return null;
    }

    static class StreamReader extends Thread {
        private InputStream is;
        private StringWriter sw = new StringWriter();

        public StreamReader(InputStream is) {
            this.is = is;
        }

        public void run() {
            try {
                int c;
                while ((c = is.read()) != -1)
                    sw.write(c);
            } catch (IOException e) {
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public String getResult() {
            return sw.toString();
        }
    }

    public static boolean deleteValue(String key, String valueName) {
        try {
            // Run reg query, then read output with StreamReader (internal class)
            Process process = Runtime.getRuntime().exec("reg delete \"" + key + "\" /v \"" + valueName + "\" /f");

            StreamReader reader = new StreamReader(process.getInputStream());
            reader.start();
            process.waitFor();
            reader.join();
            String output = reader.getResult();

            // Output has the following format:
            // \n<Version information>\n\n<key>\t<registry type>\t<value>
            return output.contains("The operation completed successfully");
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean addValue(String key, String valName, String val) {
        try {
            // Run reg query, then read output with StreamReader (internal class)
            Process process = Runtime.getRuntime().exec(
                    "reg add \"" + key + "\" /v \"" + valName + "\" /d \"\\\"" + val + "\\\"\" /f");

            StreamReader reader = new StreamReader(process.getInputStream());
            reader.start();
            process.waitFor();
            reader.join();
            String output = reader.getResult();

            // Output has the following format:
            // \n<Version information>\n\n<key>\t<registry type>\t<value>
            return output.contains("The operation completed successfully");
        } catch (Exception e) {
        }
        return false;
    }
}
