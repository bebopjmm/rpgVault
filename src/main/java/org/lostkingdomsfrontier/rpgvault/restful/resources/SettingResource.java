package org.lostkingdomsfrontier.rpgvault.restful.resources;

import com.fasterxml.jackson.annotation.JsonView;
import org.lostkingdomsfrontier.rpgvault.datastore.SettingRepositoryDelegate;
import org.lostkingdomsfrontier.rpgvault.entities.IndexList;
import org.lostkingdomsfrontier.rpgvault.entities.JacksonViews;
import org.lostkingdomsfrontier.rpgvault.entities.ResourceIndex;
import org.lostkingdomsfrontier.rpgvault.entities.environment.Region;
import org.lostkingdomsfrontier.rpgvault.entities.environment.Setting;
import org.lostkingdomsfrontier.rpgvault.restful.RepositoryDao;

import javax.ws.rs.*;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author: bebopjmm Date: 8/13/13 Time: 11:16
 */
public class SettingResource {
    private static final Logger LOG = Logger.getLogger(CampaignResource.class.getName());
    @Context
    UriInfo uriInfo;
    @Context
    private ResourceContext resourceContext;
    private Setting setting;
    private SettingRepositoryDelegate delegate;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JacksonViews.RestfulView.class)
    public Setting getSetting() {
        this.setting.setRegionIndex(mapRegions());
        return this.setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
        this.delegate = RepositoryDao.VAULT_REPOSITORY.getRepository().getDelegateForSetting(setting.getSlug());
    }

    @Path("/{id}")
    public RegionResource getSettingRegion(@PathParam("id") String slug) {
        RegionResource resource = resourceContext.getResource(RegionResource.class);
        // Load the specific region associated with the setting
        Region region = this.delegate.findRegion(slug);
        if (region != null) {
            resource.setRegion(region);
            resource.setDelegate(this.delegate);
            LOG.info("-- regionResource instantiated for: " + slug);
            return resource;
        } else {
            throw new WebApplicationException(404);
        }
    }

    IndexList mapRegions() {
        IndexList results = new IndexList();
        List<Region> regions = this.delegate.getRegions();
        LOG.info("-- total regions for this setting = " + regions.size());
        for (Region region : regions) {
            LOG.info("-- results should contain region: " + region.getName());
            ResourceIndex index = new ResourceIndex();
            index.setId(region.getSlug());
            UriBuilder ub = uriInfo.getAbsolutePathBuilder();
            URI psURI = ub.path(region.getSlug()).build();
            index.setUri(psURI.toASCIIString());
            index.setName(region.getName());
            results.getIndices().add(index);
        }
        return results;
    }
}
