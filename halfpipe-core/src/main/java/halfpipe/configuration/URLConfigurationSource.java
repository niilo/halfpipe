package halfpipe.configuration;

import static com.netflix.config.sources.URLConfigurationSource.*;

import java.io.InputStream;
import java.net.URL;
import java.util.*;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.config.PollResult;
import com.netflix.config.PolledConfigurationSource;
import halfpipe.configuration.json.JSONConfiguration;
import halfpipe.configuration.yaml.YamlConfiguration;

/**
 * A polled configuration source based on a set of URLs. For each poll,
 * it always returns the complete union of properties defined in all files. If one property
 * is defined in more than one URL, the value in file later on the list will override
 * the value in the previous one. The content of the URL should conform to the properties file format.
 *
 * @author awang
 *
 */
public class URLConfigurationSource implements PolledConfigurationSource {

    private final URL[] configUrls;

    private static final Logger logger = LoggerFactory.getLogger(URLConfigurationSource.class);

    /**
     * Create an instance with a list URLs to be used.
     *
     * @param urls list of URLs to be used
     */
    public URLConfigurationSource(String... urls) {
        configUrls = createUrls(urls);
    }

    private static URL[] createUrls(String... urlStrings) {
        if (urlStrings == null || urlStrings.length == 0) {
            return null;
        }
        URL[] urls = new URL[urlStrings.length];
        try {
            for (int i = 0; i < urls.length; i++) {
                urls[i] = new URL(urlStrings[i]);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return urls;
    }

    /**
     * Create an instance with a list URLs to be used.
     *
     * @param urls list of URLs to be used
     */
    public URLConfigurationSource(URL... urls) {
        configUrls = urls;
    }

    /**
     * Create the instance for the default list of URLs, which is composed by the following order
     *
     * <ul>
     * <li>A configuration file (default name to be <code>config.properties</code>, see DEFAULT_CONFIG_FILE_NAME) on the classpath
     * <li>A list of URLs defined by system property CONFIG_URL with values separated by comma <code>","</code>.
     * </ul>
     */
    public URLConfigurationSource() {
        List<URL> urlList = new ArrayList<URL>();
        URL configFromClasspath = getConfigFileFromClasspath();
        if (configFromClasspath != null) {
            urlList.add(configFromClasspath);
        }
        String[] fileNames = getDefaultFileSources();
        if (fileNames.length != 0) {
            urlList.addAll(Arrays.asList(createUrls(fileNames)));
        }
        if (urlList.size() == 0) {
            configUrls = new URL[0];
            logger.warn("No URLs will be polled as dynamic configuration sources.");
            logger.info("To enable URLs as dynamic configuration sources, define System property "
                    + CONFIG_URL + " or make " + DEFAULT_CONFIG_FILE_FROM_CLASSPATH + " available on classpath.");
        } else {
            configUrls = urlList.toArray(new URL[urlList.size()]);
            logger.info("URLs to be used as dynamic configuration source: " + urlList);
        }
    }

    private URL getConfigFileFromClasspath() {
        URL url = null;
        // attempt to load from the context classpath
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader != null) {
            url = loader.getResource(DEFAULT_CONFIG_FILE_FROM_CLASSPATH);
        }
        if (url == null) {
            // attempt to load from the system classpath
            url = ClassLoader.getSystemResource(DEFAULT_CONFIG_FILE_FROM_CLASSPATH);
        }
        if (url == null) {
            // attempt to load from the system classpath
            url = URLConfigurationSource.class.getResource(DEFAULT_CONFIG_FILE_FROM_CLASSPATH);
        }
        return url;
    }

    public List<URL> getConfigUrls() {
        return Collections.unmodifiableList(Arrays.asList(configUrls));
    }

    private static final String[] getDefaultFileSources() {
        String name = System.getProperty(CONFIG_URL);
        String[] fileNames;
        if (name != null) {
            fileNames = name.split(",");
        } else {
            fileNames = new String[0];
        }
        return fileNames;
    }


    /**
     * Retrieve the content of the property files. For each poll, it always
     * returns the complete union of properties defined in all URLs. If one
     * property is defined in content of more than one URL, the value in file later on the
     * list will override the value in the previous one.
     *
     * @param initial this parameter is ignored by the implementation
     * @param checkPoint this parameter is ignored by the implementation
     * @throws java.io.IOException IOException occurred in file operation
     */
    @Override
    public PollResult poll(boolean initial, Object checkPoint)
            throws Exception {
        if (configUrls == null || configUrls.length == 0) {
            return PollResult.createFull(null);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        for (URL url: configUrls) {

            String urlString = url.toString();
            if (urlString.endsWith(".json")) {
                loadConfig(map, new JSONConfiguration(url));
            } else if (urlString.endsWith(".yaml") || urlString.endsWith(".yml")) {
                loadConfig(map, new YamlConfiguration(url));
            } else {
                Properties props = new Properties();
                InputStream fin = url.openStream();
                props.load(fin);
                fin.close();
                for (Map.Entry<Object, Object> entry: props.entrySet()) {
                    map.put((String) entry.getKey(), entry.getValue());
                }
            }
        }
        return PollResult.createFull(map);
    }

    private void loadConfig(Map<String, Object> map, FileConfiguration config) throws ConfigurationException {
        config.load();
        Iterator<String> keys = config.getKeys();
        while (keys.hasNext()) {
            String key = keys.next();
            map.put(key, config.getProperty(key));
        }
    }

    @Override
    public String toString() {
        return "FileConfigurationSource [fileUrls=" + Arrays.toString(configUrls)
                + "]";
    }
}