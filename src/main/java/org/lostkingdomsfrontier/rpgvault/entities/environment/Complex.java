package org.lostkingdomsfrontier.rpgvault.entities.environment;

import org.mongojack.DBRef;
import org.mongojack.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author John McCormick
 *         Date: 8/10/13
 */
public class Complex {
    private String _id;

    String name;

    String slug;

    private String regionID;

    List<Area> areas = new ArrayList<>();

    @ObjectId
    public String get_id() {
        return _id;
    }

    @ObjectId
    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getRegionID() {
        return regionID;
    }

    public void setRegionID(String regionID) {
        this.regionID = regionID;
    }

    public List<Area> getAreas() {
        return areas;
    }

    public void setAreas(List<Area> areas) {
        this.areas = areas;
    }
}
