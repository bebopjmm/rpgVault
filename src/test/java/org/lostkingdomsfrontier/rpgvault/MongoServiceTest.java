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

        if (settingDelegate.getComplexes().size() == 0) {
            Complex sampleComplex = new Complex();
            sampleComplex.setName("Catacombs of Wrath");
            sampleComplex.setSlug("catacombs-of-wrath");
            settingDelegate.addComplex(sampleComplex);

            Area area = new Area();
            area.setSlug("b1.1");
            area.setName("Guard Cave");
            area.setDescription("The worn natural tunnel curves around and then opens into a cave. Within a hairless " +
                                        "humanoid lurches on back-bent, dog-like legs, its hideous mouth flanked by tiny" +
                                        " arms with three-fingered hands.");
            area.setDetails("A sinspawn dwells in this cave, charged by Erylium to guard the approach to her realm." +
                                    " The sinspawn does its job admirably, standing at its post for hours at a time" +
                                    " until it is relieved by another.");
            settingDelegate.addAreaToComplex(area, sampleComplex.getSlug());
            sampleComplex = settingDelegate.findComplex(sampleComplex.getSlug());

            // Test the replaceComplex function
            area = new Area();
            area.setSlug("b1.2");
            area.setName("Old Storeroom");
            area.setDescription("The original purpose of this chamber is unclear, but large mounds of rubble lie" +
                                        " strewn on its floor. The wall to the west has been torn down to reveal a" +
                                        " tunnel leading to the west.");
            area.setDetails("An investigation of the rubble reveals that most of it seems to have consisted of broken" +
                                    " urns and other pottery containers that once held food stores, long since" +
                                    " crumbled to dust.");
            sampleComplex.getAreas().add(area);
            settingDelegate.replaceComplex(sampleComplex);
        }

        repository.setCampaignDB("rpg_campaigns", true);
        JacksonDBCollection<Campaign, String> campaignColl = repository.getCampaignColl();
        Campaign sampleCampaign = null;
        if (campaignColl.getCount() == 0) {
            sampleCampaign = new Campaign();
            sampleCampaign.setName("Rise of the Runelords");
            sampleCampaign.setSlug("rise-of-the-runelords");
            WriteResult<Campaign, String> result = campaignColl.insert(sampleCampaign);
            LOG.info("sampleCampaign id = " + result.getSavedId());
        }
        sampleCampaign = campaignColl.findOne();

        repository.close();
    }
}
