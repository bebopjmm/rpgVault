package org.lostkingdomsfrontier.rpgvault.restful.resources;

import com.fasterxml.jackson.annotation.JsonView;
import org.lostkingdomsfrontier.rpgvault.datastore.SettingRepositoryDelegate;
import org.lostkingdomsfrontier.rpgvault.entities.IndexList;
import org.lostkingdomsfrontier.rpgvault.entities.JacksonViews;
import org.lostkingdomsfrontier.rpgvault.entities.ResourceIndex;
import org.lostkingdomsfrontier.rpgvault.entities.environment.Complex;
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
 * @author bebopjmm Date: 8/13/13 Time: 15:33
 */
public class RegionResource {
    private static final Logger LOG = Logger.getLogger(CampaignResource.class.getName());
    @Context
    UriInfo uriInfo;
    @Context
    private ResourceContext resourceContext;
    private Region region;
    private SettingRepositoryDelegate delegate;

    public void setRegion(Region region) {
        if (region == null) {
            LOG.warning("Setting NULL region!");
        }
        this.region = region;
    }

    public void setDelegate(SettingRepositoryDelegate delegate) {
        if (delegate == null) {
            LOG.warning("Setting NULL delegate!");
        }
        this.delegate = delegate;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JacksonViews.RestfulView.class)
    public Region getRegion() {
        LOG.info("-- getRegion:" + region.getSlug());
        this.region.setComplexIndex(mapComplexes());
        return this.region;
    }

    @Path("/{id}")
    public ComplexResource getRegionComplex(@PathParam("id") String slug) {
        ComplexResource resource = resourceContext.getResource(ComplexResource.class);
        // Lookup the specific complex associated with this region
        Complex complex = this.delegate.findComplex(slug);
        if (complex != null) {
            resource.setComplex(complex);
            resource.setDelegate(this.delegate);
            return resource;
        } else {
            throw new WebApplicationException(404);
        }
    }

    IndexList mapComplexes() {
        IndexList results = new IndexList();
        List<Complex> complexes = this.delegate.getComplexesInRegion(this.region);
        LOG.info("-- total complexes for this region = " + complexes.size());
        for (Complex complex : complexes) {
            LOG.info("-- results should contain complex: " + complex.getName());
            ResourceIndex index = new ResourceIndex();
            index.setId(complex.getSlug());
            UriBuilder ub = uriInfo.getAbsolutePathBuilder();
            URI psURI = ub.path(complex.getSlug()).build();
            index.setUri(psURI.toASCIIString());
            index.setName(complex.getName());
            results.getIndices().add(index);
        }
        return results;
    }
}
