package org.lostkingdomsfrontier.rpgvault.entities.campaign;

import org.mongojack.ObjectId;

/**
 * @author: John McCormick
 * Date: 8/11/13 Time: 08:07
 *
 * @since
 */
public class Adventure {
    private String _id;

    String name;

    String slug;  // slug also needs to be unique

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
}
