package thirtytwo.degrees.halfpipe.configuration;

import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicStringProperty;

import javax.ws.rs.DefaultValue;

/**
 * User: spencergibb
 * Date: 10/4/12
 * Time: 11:18 PM
 */
public class HttpConfiguration {
    DynamicIntProperty port;

    @DefaultValue("/*")
    DynamicStringProperty rootPath;

    GzipConfiguration gzip;
}
