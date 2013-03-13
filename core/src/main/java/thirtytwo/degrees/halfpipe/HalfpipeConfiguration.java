package thirtytwo.degrees.halfpipe;

import static thirtytwo.degrees.halfpipe.Halfpipe.*;
import static com.netflix.config.sources.URLConfigurationSource.*;

import com.google.common.collect.Maps;
import com.netflix.config.ConcurrentCompositeConfiguration;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicPropertyFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import thirtytwo.degrees.halfpipe.configuration.Configuration;
import thirtytwo.degrees.halfpipe.configuration.DynamicURLConfiguration;
import thirtytwo.degrees.halfpipe.jersey.HalfpipeResourceConfig;
import thirtytwo.degrees.halfpipe.logging.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * User: spencergibb
 * Date: 9/26/12
 * Time: 10:13 PM
 */
public class HalfpipeConfiguration {
    private static final Log LOG = Log.forThisClass();
    public static AnnotationConfigWebApplicationContext rootContext;

    public static Map<String, String> jerseyProperties(Configuration config) {
        HashMap<String, String> props = Maps.newHashMap();
        props.put(ServletContainer.RESOURCE_CONFIG_CLASS, HalfpipeResourceConfig.class.getName());
        props.put(PackagesResourceConfig.PROPERTY_PACKAGES, config.resourcePackages.get());
        props.put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE.toString());
        return props;
    }

    public static void createConfig(String configFile) throws Exception {
        if (DynamicPropertyFactory.isInitializedWithDefaultConfig() || ConfigurationManager.isConfigurationInstalled()) {
            LOG.warn("Trying to initialze config again");
            return; //TODO: why does this happen on exploded?
        }

        System.setProperty("archaius.configurationSource.defaultFileName", HALFPIPE_PROPERTIES_FILENAME);

        ConcurrentCompositeConfiguration configuration = new ConcurrentCompositeConfiguration();

        if (!StringUtils.isBlank(configFile)) {
            File file = new File(configFile);
            String url = file.toURI().toURL().toString();
            LOG.debug("config file url {}", url);
            String urls = System.getProperty(CONFIG_URL, null);
            if (urls == null) {
                System.setProperty(CONFIG_URL, url);
            } else {
                System.setProperty(CONFIG_URL, urls+","+url);
            }
        }

        configuration.addConfiguration(new SystemConfiguration(), DynamicPropertyFactory.SYS_CONFIG_NAME);
        try {
            DynamicURLConfiguration defaultURLConfig = new DynamicURLConfiguration();
            configuration.addConfiguration(defaultURLConfig, DynamicPropertyFactory.URL_CONFIG_NAME);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        DynamicPropertyFactory.initWithConfigurationSource(configuration);
    }

    public static AnnotationConfigWebApplicationContext registerRootContext(Class<?> appConfigClass) {
        rootContext = createWebContext(appConfigClass);
        return rootContext;
    }

    public static AnnotationConfigWebApplicationContext createWebContext(Class<?> appConfigClass) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(appConfigClass);
        return context;
    }
}
