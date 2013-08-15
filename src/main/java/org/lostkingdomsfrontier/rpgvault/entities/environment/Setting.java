package org.lostkingdomsfrontier.rpgvault.entities.environment;

import com.fasterxml.jackson.annotation.JsonView;
import org.lostkingdomsfrontier.rpgvault.entities.IndexList;
import org.lostkingdomsfrontier.rpgvault.entities.JacksonViews;
import org.lostkingdomsfrontier.rpgvault.entities.campaign.Adventure;
import org.mongojack.DBRef;
import org.mongojack.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: bebopjmm
 * Date: 8/12/13 Time: 09:17
 */
public class Setting {
    private String _id;

    String name;

    String slug;

    String description;

    @JsonView(JacksonViews.MongoView.class)
    List<String> regionIDs = new ArrayList<>();

    @JsonView(JacksonViews.RestfulView.class)
    IndexList regionIndex = new IndexList();

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getRegionIDs() {
        return regionIDs;
    }

    public void setRegionIDs(List<String> regionIDs) {
        this.regionIDs = regionIDs;
    }

    public IndexList getRegionIndex() {
        return regionIndex;
    }

    public void setRegionIndex(IndexList regionIndex) {
        this.regionIndex = regionIndex;
    }
}
