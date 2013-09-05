package org.lostkingdomsfrontier.rpgvault.datastore;

import static org.junit.Assert.*;

import org.apache.commons.configuration.XMLConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lostkingdomsfrontier.rpgvault.entities.environment.Setting;
import org.mongojack.WriteResult;

import java.util.logging.Logger;

/**
 * @author bebopjmm Date: 9/5/13 Time: 10:01
 */
public class MongoDomainRepositoryTests {
    public static final String DEFAULT_CONFIG = "./conf/unitTest.config.xml";
    private static final Logger LOG = Logger.getLogger(MongoDomainRepositoryTests.class.getName());
    static MongoDomainRepository repository = new MongoDomainRepository();

    @BeforeClass
    public static void setupBeforeClass() throws Exception {
        XMLConfiguration config = new XMLConfiguration(DEFAULT_CONFIG);
        String host = config.getString("repository.host", "localhost");
        int port = config.getInt("repository.port", 27017);
        if (!repository.connect(host, port)) {
            throw new RepositoryException("Failed to connect to repository: [\" + host + \":\" + port + \"]");
        }
        repository.setSettingDB(config.getString("repository.settingsDB", "rpg_settingsTest"), true);
        assertTrue(repository.getSettings().isEmpty());
    }

    @AfterClass
    public static void teardownAfterClass() throws Exception {
        repository.close();
    }

    @Test(expected = RepositoryException.class)
    public void testSettingDelegateFailure() {
        LOG.info("\n\n+++ testSettingDelegateFailure +++");
        SettingRepositoryDelegate delegate = repository.getDelegateForSetting("bogus");
    }

    @Test
    public void testAddSetting() {
        LOG.info("\n\n+++ testAddSetting +++");
        Setting sampleSetting = new Setting();
        sampleSetting.setName("Golarion");
        sampleSetting.setSlug("golarion");
        sampleSetting.setDescription("The default Pathfinder Campaign Setting");
        assertTrue(repository.isSlugAvailable(sampleSetting));

        WriteResult<Setting, String> result = repository.addSetting(sampleSetting);
        LOG.info("-- Added new setting: " + sampleSetting.getName() + "[" + result.getSavedId() + "]");
        assertTrue(!result.getSavedId().isEmpty());
        assertTrue(repository.getSettings().size() == 1);
        assertFalse(repository.isSlugAvailable(sampleSetting));

        sampleSetting = repository.findSetting(sampleSetting.getSlug());
        assertNotNull(sampleSetting);

        assertNotNull(repository.getDelegateForSetting(sampleSetting.getSlug()));

        sampleSetting = repository.findSetting("bogus");
        assertNull(sampleSetting);

    }

}
