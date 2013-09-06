package org.lostkingdomsfrontier.rpgvault.entities.environment;

import org.mongojack.ObjectId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author John McCormick
 *         Date: 8/10/13
 */
public class Complex {
    private String _id;

    String name;

    String slug;

    private String regionSlug;

    Set<Area> areas = new HashSet<>();

    List<Entrance> entrances = new ArrayList<>();

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

    public String getRegionSlug() {
        return regionSlug;
    }

    public void setRegionSlug(String regionSlug) {
        this.regionSlug = regionSlug;
    }

    public Set<Area> getAreas() {
        return areas;
    }

    public void setAreas(Set<Area> areas) {
        this.areas = areas;
    }

    public Area getArea(String slug) {
        for(Area area : this.areas) {
            if (area.getSlug().equalsIgnoreCase(slug)) return area;
        }
        return null;
    }

    public List<Entrance> getEntrances() {
        return entrances;
    }

    public void setEntrances(List<Entrance> entrances) {
        this.entrances = entrances;
    }
}
