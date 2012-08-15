package resources;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class PropertiesLoader {

    /**
     * Loads properties file
     *
     * @param FileName the name of the requested properties file
     * @return props the loaded properties file
     */
    public static Properties load(String FileName) throws Exception {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(FileName));
        } catch (Exception e) {
            System.out.println("There was a problem loading the properties file.");
            e.printStackTrace();
        }

        return props;
    }

    /**
     * Loads a Properties File
     *
     * @param propsFile the file created with path to your properties file
     * @return Properties the loaded properties file
     */
    public static Properties load(File propsFile) {
        Properties props = new Properties();

        try {
            FileInputStream fis = new FileInputStream(PropertiesLoader.load("application.properties").getProperty("applicationPath") + System.getProperty("file.separator") + propsFile);
            props.load(fis);
            fis.close();
        } catch (Exception e) {
            System.out.println("There was a problem loading the properties file.");
            e.printStackTrace();
        }
        return props;
    }

    /**
     * This method loads a specific property from a given property file.
     *
     * @param fileName     the name of the requested properties file
     * @param propertyName the name of the property requested
     * @return the value for the given property from the given properties file
     * @throws Exception java.io.IOException
     */
    public static String loadProperty(String fileName, String propertyName) throws Exception {
        Properties properties = new Properties();
        FileInputStream fis = new FileInputStream(fileName);
        properties.load(fis);
        return properties.getProperty(propertyName);
    }
}