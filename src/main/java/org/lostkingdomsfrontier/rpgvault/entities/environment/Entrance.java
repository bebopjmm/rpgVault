package org.lostkingdomsfrontier.rpgvault.entities.environment;

import org.mongojack.ObjectId;

/**
 *
 * @author John McCormick
 *         Date: 8/10/13
 */
public class Entrance {
    private String _id;

    String locator;

    String name;

    String slug;

    String description;

    @ObjectId
    public String get_id() {
        return _id;
    }

    @ObjectId
    public void set_id(String _id) {
        this._id = _id;
    }

    public String getLocator() {
        return locator;
    }

    public void setLocator(String locator) {
        this.locator = locator;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
