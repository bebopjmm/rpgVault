package org.lostkingdomsfrontier.rpgvault.datastore;

import com.mongodb.DBCollection;
import org.lostkingdomsfrontier.rpgvault.entities.JacksonViews;
import org.lostkingdomsfrontier.rpgvault.entities.environment.Complex;
import org.lostkingdomsfrontier.rpgvault.entities.environment.Region;
import org.lostkingdomsfrontier.rpgvault.entities.environment.Setting;
import org.mongojack.DBCursor;
import org.mongojack.DBUpdate;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;

import java.util.List;
import java.util.logging.Logger;

/**
 * @author: bebopjmm Date: 8/13/13 Time: 16:15
 */
public class SettingRepositoryDelegate {
    private static final Logger LOG = Logger.getLogger(SettingRepositoryDelegate.class.getName());
    final DBCollection settingCollection;
    JacksonDBCollection<Setting, String> rootCollection;
    Setting setting;
    JacksonDBCollection<Region, String> regionCollection;
    JacksonDBCollection<Complex, String> complexCollection;

    public SettingRepositoryDelegate(DBCollection settingCollection, Setting setting,
                                     JacksonDBCollection<Setting, String> rootCollection) {
        this.settingCollection = settingCollection;
        this.rootCollection = rootCollection;
        this.setting = setting;
        this.regionCollection = JacksonDBCollection.wrap(this.settingCollection.getCollection("regions"),
                                                         Region.class, String.class, JacksonViews.MongoView.class);
        this.complexCollection = JacksonDBCollection.wrap(this.settingCollection.getCollection("complexes"),
                                                          Complex.class, String.class, JacksonViews.MongoView.class);
    }

    /**
     * This method returns true if the getSlug() value is not already being used by a Region in the repository for the
     * Setting this delegate manages
     *
     * @param region Region to evaluate
     * @return false if region.getSlug() is null or region.getSlug() matches a slug value of a Region already associated
     *         with the Setting, otherwise true
     */
    public boolean slugIsUnique(Region region) {
        if (region.getSlug() == null) return false;
        DBCursor<Region> cursor = this.regionCollection.find().is("slug", region.getSlug());
        return cursor.count() == 0;
    }

    public List<Region> getRegions() {
        return this.regionCollection.find().toArray();
    }

    public List<Region> getRegions(List<String> slugFilter) {
        return this.regionCollection.find().in("slug", slugFilter).toArray();
    }

    public Region findRegion(String regionSlug) {
        DBCursor<Region> cursor = this.regionCollection.find().is("slug", regionSlug);
        LOG.info("-- Attempt to getSetting [" + regionSlug + "] yielded results size = " + cursor.count());
        if (cursor.hasNext())
            return cursor.next();
        else
            return null;
    }

    /**
     * This method is used to create a new Region within the repository. The provided region must have a unique value
     * for its slug attribute or a RepositoryException will be thrown.
     *
     * @param region
     * @return
     */
    public WriteResult<Region, String> addRegion(Region region) {
        // Verify there isn't an existing region with same slug within the setting
        if (!slugIsUnique(region)) {
            RepositoryException repositoryException =
                    new RepositoryException(
                            "Attempt to add Regions already in repository, slug = " + region.getSlug());
            LOG.throwing(this.getClass().getName(), "addRegion", repositoryException);
            throw repositoryException;
        }

        // Insert the Region and add its id to setting's regionIDs
        WriteResult<Region, String> result = regionCollection.insert(region);

        this.rootCollection.updateById(setting.get_id(), DBUpdate.push("regionIDs", result.getSavedId()));
        return result;
    }


    public List<Complex> getComplexes() {
        return this.complexCollection.find().toArray();
    }

    public List<Complex> getComplexes(List<String > slugFilter) {
        return this.complexCollection.find().in("slug", slugFilter).toArray();
    }

    public List<Complex> getComplexesInRegion(Region region) {
        return this.complexCollection.find().in("_id", region.getComplexIDs()).toArray();
    }

    /**
     * This method returns true if the getSlug() value is not already being used by a Complex in the repository for the
     * Setting this delegate manages
     *
     * @param complex Complex to evaluate
     * @return false if complex.getSlug() is null or complex.getSlug() matches a slug value of a Complex already associated
     *         with the Setting, otherwise true
     */
    public boolean slugIsUnique(Complex complex) {
        if (complex.getSlug() == null) return false;
        DBCursor<Complex> cursor = this.complexCollection.find().is("slug", complex.getSlug());
        return cursor.count() == 0;
    }

    public WriteResult<Complex, String> addComplex(Complex complex) {
        // Verify there isn't an existing region with same slug within the setting
        if (!slugIsUnique(complex)) {
            RepositoryException repositoryException =
                    new RepositoryException(
                            "Attempt to add Complex already in repository, slug = " + complex.getSlug());
            LOG.throwing(this.getClass().getName(), "addComplex", repositoryException);
            throw repositoryException;
        }

        // Insert the Complex and add its id to setting's regionIDs
        WriteResult<Complex, String> result = this.complexCollection.insert(complex);

//        this.rootCollection.updateById(setting.get_id(), DBUpdate.push("regionIDs", result.getSavedId()));
        return result;
    }
}
