package encryption;

import org.apache.commons.lang3.RandomStringUtils;
import org.jasypt.util.password.ConfigurablePasswordEncryptor;
import org.jasypt.util.text.BasicTextEncryptor;
import resources.PropertiesLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Properties;

public class EncryptionTool {

    public EncryptionTool() {
    }

    public static void main(String[] args) {
        if ("encrypt".equals(args[0])) {
            String plainText = args[2].trim();
            String keyName = args[1].trim();
            System.out.println("The encrypted version of '" + plainText + "' is: " + encrypt(plainText, keyName));
        } else if ("decrypt".equals(args[0])) {
            String cipherText = args[1];
            System.out.println("The decrypted version of '" + args[1] + "' is: " + decrypt(cipherText));
        } else if ("addKey".equals(args[0])) {
            String name = args[1].trim();
            int length = Integer.valueOf(args[2].trim());
            System.out.println("Random encryption key: " + addNewKey(name, length));
        }
    }

    public static String addNewKey(String name, int length) {
        try {
            File f = loadKeyring();
            if (f == null) {
                createKeyring();
                f = loadKeyring();
            }

            Properties properties = PropertiesLoader.load(f);
            properties.setProperty(name, generateRandomString(length));
            properties.store(new FileOutputStream(f.getName()), null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return name + "=" + generateRandomString(length);
    }

    public static String encrypt(String clearTextPassword, String keyName) {
        BasicTextEncryptor encryptor = new BasicTextEncryptor();

        try {
            encryptor.setPassword(PropertiesLoader.load(loadKeyring()).getProperty(keyName));
        } catch (IllegalArgumentException e) {
            System.out.println("The given key name could not be loaded from the keyring file.");
            e.printStackTrace();
        }

        String encryptedString = "Cornfluence(=" + keyName + "=";

        try {
            encryptedString += encryptor.encrypt(clearTextPassword) + "=)";
        } catch (Exception e) {
            System.out.println("There was a problem encrypting the clear text");
            e.printStackTrace();
        }

        return encryptedString;
    }

    public static String decrypt(String encryptedText) {
        if (!validEncryptedString(encryptedText)) {
            throw new IllegalArgumentException("This encrypted text does not match the pattern generated by the Cornfluence encryption tool.");
        }

        BasicTextEncryptor encryptor = new BasicTextEncryptor();
        ConfigurablePasswordEncryptor encryp = new ConfigurablePasswordEncryptor();
        encryp.setAlgorithm("");

        try {
            encryptor.setPassword(PropertiesLoader.load(loadKeyring()).getProperty(encryptedText.split("=")[1]));
        } catch (IllegalArgumentException e) {
            System.out.println("The key given in the encrypted text could not be loaded.");
            e.printStackTrace();
        }

        return encryptor.decrypt(encryptedText.split("=")[2]);
    }

    public static String generateRandomString(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    private static boolean validEncryptedString(String encryptedString) {
        return encryptedString.startsWith("Cornfluence(") && encryptedString.endsWith("=)");
    }

    private static void createKeyring() {
        try {
            File file = new File(".keyring");
            if (!file.exists()) {
                if (file.createNewFile()) {
                    System.out.println("new keyring file created");
                } else {
                    System.out.println("new keyring could not be created");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static File loadKeyring() {
        ArrayList<String> possibleFiles = new ArrayList<String>();
        possibleFiles.add(".keyring");
        possibleFiles.add("keyring.txt");

        for (String possibleFile : possibleFiles) {
            File file = new File(possibleFile);
            if (file.isFile()) {
                try {
                    return file;
                } catch (Exception e) {
                    System.out.println("The keyring file could not be found.");
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
