package org.lostkingdomsfrontier.rpgvault.entities.environment;

import com.fasterxml.jackson.annotation.JsonView;
import org.lostkingdomsfrontier.rpgvault.entities.IndexList;
import org.lostkingdomsfrontier.rpgvault.entities.JacksonViews;
import org.mongojack.MongoCollection;
import org.mongojack.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: bebopjmm
 * Date: 8/12/13 Time: 10:25
 */
public class Region {
    private String _id;

    private String settingID;

    String name;

    String slug;

    String description;

    @JsonView(JacksonViews.MongoView.class)
    List<String> complexIDs = new ArrayList<String>();

    @JsonView(JacksonViews.RestfulView.class)
    IndexList complexIndex = new IndexList();

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

    public String getSettingID() {
        return settingID;
    }

    public void setSettingID(String settingID) {
        this.settingID = settingID;
    }

    public List<String> getComplexIDs() {
        return complexIDs;
    }

    public void setComplexIDs(List<String> complexIDs) {
        this.complexIDs = complexIDs;
    }

    public IndexList getComplexIndex() {
        return complexIndex;
    }

    public void setComplexIndex(IndexList complexIndex) {
        this.complexIndex = complexIndex;
    }
}
