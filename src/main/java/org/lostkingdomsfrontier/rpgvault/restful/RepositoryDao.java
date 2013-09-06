package org.lostkingdomsfrontier.rpgvault.restful;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.lostkingdomsfrontier.rpgvault.datastore.MongoDomainRepository;
import org.lostkingdomsfrontier.rpgvault.entities.IndexList;
import org.lostkingdomsfrontier.rpgvault.entities.ResourceIndex;
import org.lostkingdomsfrontier.rpgvault.entities.campaign.Adventure;
import org.lostkingdomsfrontier.rpgvault.entities.campaign.Campaign;
import org.lostkingdomsfrontier.rpgvault.entities.environment.Region;
import org.lostkingdomsfrontier.rpgvault.entities.environment.Setting;
import org.mongojack.DBRef;
import org.mongojack.JacksonDBCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author John McCormick
 *         Date: 8/10/13
 */
public enum RepositoryDao {
    VAULT_REPOSITORY;
    private final Logger LOG = Logger.getLogger(RepositoryDao.class.getName());
    boolean isConnected;
    private ObjectMapper mapper;
    private MongoDomainRepository repository = new MongoDomainRepository();

    private RepositoryDao() {
        this.mapper = new ObjectMapper();
        reconnect();
    }

    public boolean isConnected() {
        return isConnected;
    }

    public synchronized void reconnect() {
        String host = ConfigDao.CONFIG.getConfig().getString("repository.host", "localhost");
        int port = ConfigDao.CONFIG.getConfig().getInt("repository.port", 27017);

        if (!repository.connect(host, port)) {
            LOG.severe("Failed to connect to repository: [" + host + ":" + port + "]");
            isConnected = false;
        } else {
            LOG.info("-- Connected to repository at [" + host + ":" + port + "]");
            isConnected = true;
            repository.setSettingDB(ConfigDao.CONFIG.getConfig().getString("repository.settingsDB", "rpg_environments"), false);
            repository.setCampaignDB(ConfigDao.CONFIG.getConfig().getString("repository.campaignsDB", "rpg_campaigns"), false);
        }
    }

    public MongoDomainRepository getRepository() {
        return repository;
    }

    public List<Campaign> retrieveCampaigns() {
        if (!isConnected) {
            LOG.warning("** Not currently connected to a repository");
            return new ArrayList<Campaign>();
        }
        JacksonDBCollection<Campaign, String> campaignCollection = repository.getCampaignColl();
        LOG.info("Total campaigns = " + campaignCollection.getCount());
        return campaignCollection.find().toArray();
    }


    public List<Adventure> retrieveAdventuresForCampaign (Campaign campaign) {
        if (!isConnected) {
            LOG.warning("** Not currently connected to a repository");
            return new ArrayList<Adventure>();
        }
        return repository.getCampaignColl().fetch(campaign.getAdventures());
    }

}
