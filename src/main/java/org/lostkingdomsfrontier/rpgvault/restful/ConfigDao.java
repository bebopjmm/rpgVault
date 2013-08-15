package org.lostkingdomsfrontier.rpgvault.restful;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author John McCormick
 *         Date: 8/10/13
 */
public enum ConfigDao {
    CONFIG;
    private static final String DEFAULT_CONFIG = "./conf/rpgVaultConfiguration.xml";
    private final Logger LOG = Logger.getLogger(ConfigDao.class.getName());
    private XMLConfiguration config;

    private ConfigDao() {
        LOG.info("Initializing with DEFAULT_CONFIG: " + DEFAULT_CONFIG);
        try {
            config = new XMLConfiguration(DEFAULT_CONFIG);
        } catch (ConfigurationException e) {
            LOG.log(Level.SEVERE, "FAILED to load configuration file: " + DEFAULT_CONFIG, e);
            return;
        }
    }

    public XMLConfiguration getConfig() {
        return config;
    }

    public void changeConfig(String configFilename) {
        LOG.info("Reloading configuration using file: " + configFilename);
        try {
            config = new XMLConfiguration(configFilename);
        } catch (ConfigurationException e) {
            LOG.log(Level.SEVERE, "FAILED to load configuration file: " + configFilename, e);
            return;
        }
    }
}
