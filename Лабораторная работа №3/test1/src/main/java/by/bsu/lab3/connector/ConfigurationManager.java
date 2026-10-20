package by.bsu.lab3.connector;

import java.util.ResourceBundle;

public class ConfigurationManager {
    private static final String BUNDLE_NAME = "resources.database";
    private static final int DEFAULT_POOL_SIZE = 5;

    private static ConfigurationManager instance;

    private final String url;
    private final String driver;
    private final String user;
    private final String pass;
    private final int poolSize;

    private ConfigurationManager() {
        ResourceBundle resource = ResourceBundle.getBundle(BUNDLE_NAME);
        url = resource.getString("url");
        driver = resource.getString("driver");
        user = resource.getString("user");
        pass = resource.getString("password");
        String size = resource.getString("pool.size").trim();
        poolSize = size.isEmpty() ? DEFAULT_POOL_SIZE : Integer.parseInt(size);
    }

    public static synchronized ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    public String getDriver() {
        return driver;
    }

    public String getPass() {
        return pass;
    }

    public String getURL() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public int getPoolSize() {
        return poolSize;
    }
}
