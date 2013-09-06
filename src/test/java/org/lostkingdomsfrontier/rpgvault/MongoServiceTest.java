package org.lostkingdomsfrontier.rpgvault;

import org.lostkingdomsfrontier.rpgvault.datastore.MongoDomainRepository;
import org.lostkingdomsfrontier.rpgvault.datastore.SettingRepositoryDelegate;
import org.lostkingdomsfrontier.rpgvault.entities.campaign.Campaign;
import org.lostkingdomsfrontier.rpgvault.entities.environment.Area;
import org.lostkingdomsfrontier.rpgvault.entities.environment.Complex;
import org.lostkingdomsfrontier.rpgvault.entities.environment.Region;
import org.lostkingdomsfrontier.rpgvault.entities.environment.Setting;
import org.lostkingdomsfrontier.rpgvault.restful.ConfigDao;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;

import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA. User: bebopjmm Date: 8/10/13 Time: 07:08 To change this template use File | Settings |
 * File Templates.
 */
public final class MongoServiceTest {
    private static final Logger LOG = Logger.getLogger(MongoServiceTest.class.getName());

    public static void main(String[] args) throws Exception {
        LOG.info("Starting up MongoServiceApp...");
        ConfigDao.CONFIG.changeConfig("./conf/restfulAppTest.config.xml");
        String host = ConfigDao.CONFIG.getConfig().getString("repository.host", "localhost");
        int port = ConfigDao.CONFIG.getConfig().getInt("repository.port", 27017);
        MongoDomainRepository repository = new MongoDomainRepository();

        if (!repository.connect(host, port)) {
            LOG.severe("Failed to connect to repository: [" + host + ":" + port + "]");
            return;
        }
        repository.setSettingDB("rpg_settings", true);
        Setting sampleSetting = null;
        if (repository.getSettings().size() == 0) {
            sampleSetting = new Setting();
            sampleSetting.setName("Golarion");
            sampleSetting.setSlug("golarion");
            sampleSetting.setDescription("The default Pathfinder Campaign Setting");
            repository.addSetting(sampleSetting);
            LOG.info("-- Added new setting: " + sampleSetting.getName());
        }
        SettingRepositoryDelegate settingDelegate = repository.getDelegateForSetting("golarion");

        if (settingDelegate.getRegions().size() == 0) {
            Region sampleRegion = new Region();
            sampleRegion.setName("Sandpoint");
            sampleRegion.setSlug("sandpoint");
            sampleRegion.setDescription("A small coastal town on the Lost Coast of Varisia");
            settingDelegate.addRegion(sampleRegion);
            LOG.info("-- Added new region: " + sampleRegion.getName() + " to setting: " + sampleSetting.getName());
        }

        repository.close();
    }
}
