package org.lostkingdomsfrontier.rpgvault.restful.resources;

import com.fasterxml.jackson.annotation.JsonView;
import org.lostkingdomsfrontier.rpgvault.entities.IndexList;
import org.lostkingdomsfrontier.rpgvault.entities.JacksonViews;
import org.lostkingdomsfrontier.rpgvault.entities.ResourceIndex;
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
 * @author: bebopjmm Date: 8/12/13 Time: 09:08
 */
@Path("/settings")
public class SettingIndexResource {
    private static final Logger LOG = Logger.getLogger(SettingIndexResource.class.getName());
    @Context
    UriInfo uriInfo;
    @Context
    private ResourceContext resourceContext;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JacksonViews.RestfulView.class)
    public IndexList getSettingsIndex() {
        LOG.info("-- Looking up settings...");
        IndexList results = new IndexList();
        List<Setting> settings = RepositoryDao.VAULT_REPOSITORY.getRepository().getSettings();
        LOG.info("-- Assembling SettingIndex Response, total resource indices = " + settings.size());
        for (Setting setting : settings) {
            LOG.info("-- results should contain setting: " + setting.getName());
            ResourceIndex index = new ResourceIndex();
            index.setId(setting.getSlug());
            UriBuilder ub = uriInfo.getAbsolutePathBuilder();
            URI psURI = ub.path(setting.getSlug()).build();
            index.setUri(psURI.toASCIIString());
            index.setName(setting.getName());
            results.getIndices().add(index);
        }

        return results;
    }

    @Path("/{id}")
    public SettingResource getSetting(@PathParam("id") String slug) {
        SettingResource resource = resourceContext.getResource(SettingResource.class);
        // Load the specific setting
        Setting setting = RepositoryDao.VAULT_REPOSITORY.getRepository().getSetting(slug);
        if (setting != null) {
            resource.setSetting(setting);
            return resource;
        } else // We didn't find a setting for this id, throw HTTP Not Found
            throw new WebApplicationException(404);
    }
}
