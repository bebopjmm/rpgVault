package org.lostkingdomsfrontier.rpgvault.util;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.lostkingdomsfrontier.rpgvault.datastore.MongoDomainRepository;
import org.lostkingdomsfrontier.rpgvault.datastore.RepositoryException;
import org.lostkingdomsfrontier.rpgvault.datastore.SettingRepositoryDelegate;
import org.lostkingdomsfrontier.rpgvault.entities.environment.Area;
import org.lostkingdomsfrontier.rpgvault.entities.environment.Complex;
import org.lostkingdomsfrontier.rpgvault.entities.environment.Entrance;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author John McCormick Date: 9/6/13 Time: 14:04
 */
public class ComplexLoader {
    private final static Logger LOG = Logger.getLogger(ComplexLoader.class.getName());

    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.err.println("Usage: ComplexLoader filename repositoryHost repositoryPort settingsDB");
        }
        String filename = args[0];
        System.out.println("filename = " + filename);
        String host = args[1];
        System.out.println("repositoryHost = " + host);
        int port = Integer.parseInt(args[2]);
        System.out.println("repositoryPort = " + port);
        String db = args[3];
        System.out.println("settingsDB = " + db);
        MongoDomainRepository repository = new MongoDomainRepository();

        if (!repository.connect(host, port)) {
            throw new RepositoryException("Failed to connect to repository: [" + host + ":" + port + "]");
        }
        repository.setSettingDB(db, false);
        ComplexLoader.loadComplex(new XMLConfiguration(filename), repository);
        repository.close();
    }

    public static void loadComplex(HierarchicalConfiguration complexConfig, MongoDomainRepository repository) {
        String setting = complexConfig.getString("settingSlug");
        LOG.info("-- Loading complex into setting: " + setting);
        SettingRepositoryDelegate settingDelegate = repository.getDelegateForSetting(setting);

        Complex complex = new Complex();
        complex.setName(complexConfig.getString("name"));
        String complexSlug = complexConfig.getString("slug");
        complex.setSlug(complexConfig.getString("slug"));
        complex.setRegionSlug(complexConfig.getString("regionSlug"));
        settingDelegate.addComplex(complex);

        List areas = complexConfig.configurationsAt("area");
        LOG.info("-- total areas to process = " + areas.size());
        for (Iterator it = areas.iterator(); it.hasNext(); ) {
            addArea((HierarchicalConfiguration) it.next(), settingDelegate, complexSlug);
        }

        List entrances = complexConfig.configurationsAt("entrance");
        LOG.info("-- total entrances to process = " + entrances.size());
        for (Iterator it = entrances.iterator(); it.hasNext(); ) {
            addEntrance((HierarchicalConfiguration)it.next(), settingDelegate, complexSlug);
        }
    }

    static void addArea(HierarchicalConfiguration configuration, SettingRepositoryDelegate delegate,
                        String complexSlug) {
        LOG.info("--processing area: " + configuration.getString("slug"));
        Area area = new Area();
        area.setSlug(configuration.getString("slug"));
        area.setName(configuration.getString("name"));
        area.setDescription(configuration.getString("description"));
        area.setDetails(configuration.getString("details"));
        delegate.addAreaToComplex(area, complexSlug);
    }

    static void addEntrance(HierarchicalConfiguration configuration, SettingRepositoryDelegate delegate,
                        String complexSlug) {
        LOG.info("--processing entrance: " + configuration.getString("slug"));
        Entrance entrance = new Entrance();
        entrance.setSlug(configuration.getString("slug"));
        entrance.setName(configuration.getString("name"));
        entrance.setDescription(configuration.getString("description"));
        entrance.connectAreas(configuration.getString("area(0)"), configuration.getString("area(1)"));
        delegate.addEntranceToComplex(entrance, complexSlug);
    }
}
