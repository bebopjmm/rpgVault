package org.lostkingdomsfrontier.rpgvault.datastore;

import org.apache.commons.configuration.XMLConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lostkingdomsfrontier.rpgvault.entities.environment.*;
import org.mongojack.WriteResult;

import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * @author: bebopjmm Date: 9/5/13 Time: 15:07
 */
public class SettingRepositoryDelegateTests {
    public static final String DEFAULT_CONFIG = "./conf/unitTest.config.xml";
    private static final Logger LOG = Logger.getLogger(SettingRepositoryDelegateTests.class.getName());
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

        repository.addSetting(createTestSetting());
        assertTrue(repository.getSettings().size() == 1);
    }

    @AfterClass
    public static void teardownAfterClass() throws Exception {
        repository.close();
    }

    @Test
    public void testAddRegion() {
        SettingRepositoryDelegate settingDelegate = repository.getDelegateForSetting("golarion");
        assertNull(settingDelegate.findRegion("sandpoint"));

        Region testRegion = SettingRepositoryDelegateTests.createTestRegion();
        settingDelegate.addRegion(testRegion);
        LOG.info("-- Added new region: " + testRegion.getName());
        assertTrue(settingDelegate.getRegions().size() == 1);
        assertFalse(settingDelegate.isSlugAvailable(testRegion));

        testRegion = settingDelegate.findRegion(testRegion.getSlug());
        assertNotNull(testRegion);

        testRegion = settingDelegate.findRegion("bogus");
        assertNull(testRegion);
    }

    @Test
    public void testAddComplex() {
        SettingRepositoryDelegate settingDelegate = repository.getDelegateForSetting("golarion");
        assertNotNull(settingDelegate.findRegion("sandpoint"));
        assertNull(settingDelegate.findComplex("catacombs-of-wrath"));

        Complex testComplex = SettingRepositoryDelegateTests.createTestComplex();
        settingDelegate.addComplex(testComplex);
        LOG.info("-- Added new complex: " + testComplex.getName());
        assertTrue(settingDelegate.getRegions().size() == 1);
        assertFalse(settingDelegate.isSlugAvailable(testComplex));

        testComplex = settingDelegate.findComplex(testComplex.getSlug());
        assertNotNull(testComplex);

        testComplex = settingDelegate.findComplex("bogus");
        assertNull(testComplex);
    }

    @Test
    public void testAddAreaToComplex() {
        SettingRepositoryDelegate settingDelegate = repository.getDelegateForSetting("golarion");
        Complex testComplex = settingDelegate.findComplex("catacombs-of-wrath");
        assertNotNull(testComplex);

        Area newArea = SettingRepositoryDelegateTests.createArea1();
        settingDelegate.addAreaToComplex(newArea, testComplex.getSlug());
        testComplex = settingDelegate.findComplex("catacombs-of-wrath");
        assertTrue(testComplex.getAreas().size() ==1);
        assertNotNull(testComplex.getArea(newArea.getSlug()));

        // Verify adding the same area again does not impact Complex
        settingDelegate.addAreaToComplex(newArea, testComplex.getSlug());
        assertTrue(testComplex.getAreas().size() ==1);

        // Verify adding an area with same content does not impact Complex
        settingDelegate.addAreaToComplex(SettingRepositoryDelegateTests.createArea1(), testComplex.getSlug());
        assertTrue(testComplex.getAreas().size() ==1);
    }

    @Test
    public void testReplaceComplex() {
        SettingRepositoryDelegate settingDelegate = repository.getDelegateForSetting("golarion");
        Complex testComplex = settingDelegate.findComplex("catacombs-of-wrath");
        assertNotNull(testComplex);
        assertTrue(testComplex.getAreas().size() == 1);

        Area newArea = SettingRepositoryDelegateTests.createArea2();
        testComplex.getAreas().add(newArea);
        settingDelegate.replaceComplex(testComplex);

        testComplex = settingDelegate.findComplex(testComplex.getSlug());
        assertTrue(testComplex.getAreas().size() == 2);
        assertNotNull(testComplex.getArea(newArea.getSlug()));
    }

    @Test
    public void testAddEntrance() {
        SettingRepositoryDelegate settingDelegate = repository.getDelegateForSetting("golarion");
        Complex testComplex = settingDelegate.findComplex("catacombs-of-wrath");
        assertNotNull(testComplex);
        assertTrue(testComplex.getEntrances().size() == 0);
        assertTrue(testComplex.getAreas().size() == 2);

        Entrance entrance = SettingRepositoryDelegateTests.createEntranceA1toA2();
        settingDelegate.addEntranceToComplex(entrance, testComplex.getSlug());
        testComplex = settingDelegate.findComplex("catacombs-of-wrath");
        assertTrue(testComplex.getEntrances().size() == 1);

        Area area = testComplex.getArea(entrance.getAreas()[0]);
        assertTrue(area.getEntrances().contains(entrance.getSlug()));

        area = testComplex.getArea(entrance.getAreas()[1]);
        assertTrue(area.getEntrances().contains(entrance.getSlug()));
    }

    static Region createTestRegion() {
        Region region = new Region();
        region.setName("Sandpoint");
        region.setSlug("sandpoint");
        region.setDescription("A small coastal town on the Lost Coast of Varisia");
        return region;
    }

    static Setting createTestSetting() {
        Setting setting = new Setting();
        setting.setName("Golarion");
        setting.setSlug("golarion");
        setting.setDescription("The default Pathfinder Campaign Setting");
        return setting;
    }

    static Complex createTestComplex() {
        Complex complex = new Complex();
        complex.setName("Catacombs of Wrath");
        complex.setSlug("catacombs-of-wrath");
        complex.setRegionSlug("sandpoint");
        return complex;
    }

    static Area createArea1() {
        Area area = new Area();
        area.setSlug("b1.1");
        area.setName("Guard Cave");
        area.setDescription("The worn natural tunnel curves around and then opens into a cave. Within a hairless " +
                                    "humanoid lurches on back-bent, dog-like legs, its hideous mouth flanked by tiny" +
                                    " arms with three-fingered hands.");
        area.setDetails("A sinspawn dwells in this cave, charged by Erylium to guard the approach to her realm." +
                                " The sinspawn does its job admirably, standing at its post for hours at a time" +
                                " until it is relieved by another.");
        return area;
    }

    static Area createArea2() {
        Area area = new Area();
        area.setSlug("b1.2");
        area.setName("Old Storeroom");
        area.setDescription("The original purpose of this chamber is unclear, but large mounds of rubble lie" +
                                    " strewn on its floor. The wall to the west has been torn down to reveal a" +
                                    " tunnel leading to the west.");
        area.setDetails("An investigation of the rubble reveals that most of it seems to have consisted of broken" +
                                " urns and other pottery containers that once held food stores, long since" +
                                " crumbled to dust.");
        return area;
    }

    static Entrance createEntranceA1toA2() {
        Entrance entrance = new Entrance();
        entrance.setSlug("b1.1-b1.2");
        entrance.setName("opening");
        entrance.setDescription("a simple opening");
        entrance.connectAreas(createArea1(), createArea2());
        return entrance;
    }
}
