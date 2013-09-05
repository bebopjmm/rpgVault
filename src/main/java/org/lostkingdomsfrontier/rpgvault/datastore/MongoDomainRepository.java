package org.lostkingdomsfrontier.rpgvault.datastore;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import org.lostkingdomsfrontier.rpgvault.entities.JacksonViews;
import org.lostkingdomsfrontier.rpgvault.entities.campaign.Campaign;
import org.lostkingdomsfrontier.rpgvault.entities.environment.Setting;
import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author John McCormick Date: 8/10/13
 */
public class MongoDomainRepository {
    private static final Logger LOG = Logger.getLogger(MongoDomainRepository.class.getName());
    MongoClient mongoClient;
    DB settingDB;
    DB campaignDB;
    JacksonDBCollection<Setting, String> settingColl;
    JacksonDBCollection<Campaign, String> campaignColl;

    HashMap<String,SettingRepositoryDelegate>  settingDelegateMap = new HashMap<String, SettingRepositoryDelegate>();

    public boolean connect(String address, int port) {
        if (this.mongoClient != null) {
            LOG.warning("Refusing connect() attempt against mongo repository that is already connected!");
            return false;
        }

        try {
            LOG.info("Establishing connection to mongodb repository at " + address + ":" + port);
            this.mongoClient = new MongoClient(address, port);
        } catch (UnknownHostException uhe) {
            LOG.log(Level.SEVERE, "Failure to connect to mongo at " + address + ":" + port, uhe);
            LOG.throwing(this.getClass().getName(), "connect", uhe);
            return false;
        }
        return true;
    }

    public void setSettingDB(String dbName, boolean dropExisting) {
        if (dropExisting) this.mongoClient.dropDatabase(dbName);
        this.settingDB = this.mongoClient.getDB(dbName);
        LOG.info("-- Now using settingDB [" + dbName + "]");
        this.settingColl =
                JacksonDBCollection.wrap(this.settingDB.getCollection("settings"), Setting.class, String.class,
                                         JacksonViews.MongoView.class);
        if(this.settingColl.getIndexInfo().size() < 2) {
            this.settingColl.createIndex(new BasicDBObject("slug", 1));
            LOG.info("-- created settingColl index for slug");
        }
    }

    public void setCampaignDB(String dbName, boolean dropExisting) {
        if (dropExisting) this.mongoClient.dropDatabase(dbName);
        this.campaignDB = this.mongoClient.getDB(dbName);
        LOG.info("-- Now using campaignDB [" + dbName + "]");
        this.campaignColl =
                JacksonDBCollection.wrap(this.campaignDB.getCollection("campaigns"), Campaign.class, String.class,
                                         JacksonViews.MongoView.class);
        if(this.campaignColl.getIndexInfo().size() < 2) {
            this.campaignColl.createIndex(new BasicDBObject("slug", 1));
            LOG.info("-- created campaignColl index for slug");
        }
    }

    public void close() {
        if (this.mongoClient != null) {
            this.mongoClient.close();
        }
        LOG.info("Closed connection to mongo repository");
    }

    public SettingRepositoryDelegate getDelegateForSetting(String slug) {
        // Ensure there is a setting for this slug
        DBCursor<Setting> cursor = this.settingColl.find().is("slug", slug);
        if (cursor.count() == 0) {
            LOG.warning("No Setting with provided slug: " + slug);
            RepositoryException repositoryException =
                    new RepositoryException("No Setting with provided slug: " + slug);
            LOG.throwing(this.getClass().getName(), "getDelegateForSetting", repositoryException);
            throw repositoryException;
        }
        if (!settingDelegateMap.containsKey(slug)) {
            DBCollection collection = this.settingColl.getDbCollection().getCollection(slug);
            SettingRepositoryDelegate delegate = new SettingRepositoryDelegate(collection, findSetting(slug), this.settingColl);
            settingDelegateMap.put(slug, delegate);
        }
        return settingDelegateMap.get(slug);
    }

    public JacksonDBCollection<Campaign, String> getCampaignColl() {
        return campaignColl;
    }

    public Campaign getCampaign(String slug) {
        DBCursor<Campaign> cursor = this.campaignColl.find().is("slug", slug);
        LOG.info("-- Attempt to getCampaign [" + slug + "] yielded results size = " + cursor.count());
        if (cursor.hasNext()) return cursor.next();
        else return null;
    }

    public List<Setting> getSettings() {
        return this.settingColl.find().toArray();
    }

    /**
     * This method returns true if the getSlug() value is not already being used by a Setting in the repository
     *
     * @param setting Setting to evaluate
     * @return false if setting.getSlug() is null or matches a slug value of a Setting in the repository, otherwise
     *         true
     */
    public boolean isSlugAvailable(Setting setting) {
        if (setting.getSlug() == null) return false;
        DBCursor<Setting> cursor = this.settingColl.find().is("slug", setting.getSlug());
        LOG.fine("Total settings found for slug[" + setting.getSlug() + "] = " + cursor.count());
        return cursor.count() == 0;
    }

    /**
     * This method is used to create a new Setting within the repository. The provided setting must have a unique value
     * for its slug attribute or a RepositoryException will be thrown.
     *
     * @param setting the new Setting to insert into the repository
     * @return the WriteResult of the insert
     */
    public WriteResult<Setting, String> addSetting(Setting setting) {
        // Verify there isn't an existing setting with same slug
        if (!isSlugAvailable(setting)) {
            RepositoryException repositoryException =
                    new RepositoryException(
                            "Attempt to add Setting already in repository, slug = " + setting.getSlug());
            LOG.throwing(this.getClass().getName(), "addSetting", repositoryException);
            throw repositoryException;
        }
        // Insert the new setting and create a sub-collection for its content
        WriteResult<Setting, String> result = this.settingColl.insert(setting);
        this.settingColl.getDbCollection().getCollection(setting.getSlug());
        return result;
    }

    public Setting findSetting(String slug) {
        DBCursor<Setting> cursor = this.settingColl.find().is("slug", slug);
        LOG.info("-- Attempt to findSetting [" + slug + "] yielded results size = " + cursor.count());
        if (cursor.hasNext()) return cursor.next();
        else return null;
    }
}
