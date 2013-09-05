package org.lostkingdomsfrontier.rpgvault.datastore;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import org.lostkingdomsfrontier.rpgvault.entities.JacksonViews;
import org.lostkingdomsfrontier.rpgvault.entities.environment.Area;
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
        this.regionCollection.createIndex(new BasicDBObject("slug", 1));
        this.complexCollection = JacksonDBCollection.wrap(this.settingCollection.getCollection("complexes"),
                                                          Complex.class, String.class, JacksonViews.MongoView.class);
        this.complexCollection.createIndex(new BasicDBObject("slug", 1));
    }

    /**
     * This method returns true if the getSlug() value is not already being used by a Region in the repository for the
     * Setting this delegate manages
     *
     * @param region Region to evaluate
     * @return false if region.getSlug() is null or region.getSlug() matches a slug value of a Region already associated
     *         with the Setting, otherwise true
     */
    public boolean isSlugAvailable(Region region) {
        if (region.getSlug() == null) return false;
        DBCursor<Region> cursor = this.regionCollection.find().is("slug", region.getSlug());
        return cursor.count() == 0;
    }

    /**
     * This method returns a List of all the regions currently associated with this delegate's Setting.
     *
     * @return List of Region objects
     */
    public List<Region> getRegions() {
        return this.regionCollection.find().toArray();
    }

    /**
     * This method return the List of all Region objects currently associated with this delegate's Setting that match
     * the provided slugFilter values.
     *
     * @param slugFilter List of slug values to filter the returned results
     * @return List of Region objects
     */
    public List<Region> getRegions(List<String> slugFilter) {
        return this.regionCollection.find().in("slug", slugFilter).toArray();
    }

    /**
     * This method returns the Region object within the delegate's Setting with the provided slug.
     * @param regionSlug slug value to search against
     * @return the Region object matching the slug value or null if no match within this Setting.
     */
    public Region findRegion(String regionSlug) {
        DBCursor<Region> cursor = this.regionCollection.find().is("slug", regionSlug);
        LOG.info("-- Attempt to findRegion [" + regionSlug + "] yielded results size = " + cursor.count());
        if (cursor.hasNext()) return cursor.next();
        else return null;
    }

    /**
     * This method is used to create a new Region within the repository. The provided region must have a unique value
     * for its slug attribute or a RepositoryException will be thrown.
     *
     * @param region Region object to add to this delegate's Setting
     * @return  WriteResult of the insertion operation
     */
    public WriteResult<Region, String> addRegion(Region region) {
        // Verify there isn't an existing region with same slug within the setting
        if (!isSlugAvailable(region)) {
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

    /**
     * This method returns a List of all the Complex instances currently associated with this delegate's Setting.
     * @return List of Complex objects
     */
    public List<Complex> getComplexes() {
        return this.complexCollection.find().toArray();
    }

    /**
     * This method return the List of all Complex objects currently associated with this delegate's Setting that match
     * the provided slugFilter values.
     * @param slugFilter List of slug values to filter the returned results
     * @return List of Complex objects matching the filter values
     */
    public List<Complex> getComplexes(List<String> slugFilter) {
        return this.complexCollection.find().in("slug", slugFilter).toArray();
    }

    public List<Complex> getComplexesInRegion(Region region) {
        return this.complexCollection.find().in("_id", region.getComplexIDs()).toArray();
    }

    public Complex findComplex(String complexSlug) {
        DBCursor<Complex> cursor = this.complexCollection.find().is("slug", complexSlug);
        LOG.info("-- Attempt to findComplex [" + complexSlug + "] yielded results size = " + cursor.count());
        if (cursor.hasNext()) return cursor.next();
        else return null;
    }

    /**
     * This method returns true if the getSlug() value is not already being used by a Complex in the repository for the
     * Setting this delegate manages
     *
     * @param complex Complex to evaluate
     * @return false if complex.getSlug() is null or complex.getSlug() matches a slug value of a Complex already
     *         associated with the Setting, otherwise true
     */
    public boolean isSlugAvailable(Complex complex) {
        if (complex.getSlug() == null) return false;
        DBCursor<Complex> cursor = this.complexCollection.find().is("slug", complex.getSlug());
        return cursor.count() == 0;
    }

    public WriteResult<Complex, String> addComplex(Complex complex) {
        // Verify there isn't an existing complex with same slug within the setting
        if (!isSlugAvailable(complex)) {
            RepositoryException repositoryException =
                    new RepositoryException(
                            "Attempt to add Complex already in repository, slug = " + complex.getSlug());
            LOG.throwing(this.getClass().getName(), "addComplex", repositoryException);
            throw repositoryException;
        }

        // Insert the Complex and add its id to setting's regionIDs
        WriteResult<Complex, String> result = this.complexCollection.insert(complex);

        // TODO include the Complex slug value in its Region
//        this.rootCollection.updateById(setting.get_id(), DBUpdate.push("regionIDs", result.getSavedId()));
        return result;
    }

    public WriteResult<Complex, String> replaceComplex(Complex complex) {
        Complex origComplex = findComplex(complex.getSlug());
        if (origComplex != null) {
            complex.set_id(origComplex.get_id());
            return this.complexCollection.updateById(origComplex.get_id(), complex);
        } else {
            return addComplex(complex);
        }
    }

    public WriteResult<Complex, String> addAreaToComplex(Area area, String complexSlug) {
        Complex origComplex = findComplex(complexSlug);
        if (origComplex == null) {
            RepositoryException repositoryException =
                    new RepositoryException(
                            "Attempt to add Area to complex NOT in repository, complexSlug = " + complexSlug);
            LOG.throwing(this.getClass().getName(), "addAreaToComplex", repositoryException);
            throw repositoryException;
        }

        // TODO Make sure this area's slug is unique to the complex
        return this.complexCollection.updateById(origComplex.get_id(), DBUpdate.addToSet("areas", area));
    }

}
