32º Halfpipe
====================

HTTP+JSON Services using industry best Java libraries.
Spring enabled, with embedded jetty or netty.  Fork of [dropwizard](http://dropwizard.codahale.com/).

Features
-----
- Maven
- [Embedded Jetty](http://www.eclipse.org/jetty/documentation/current/embedding-jetty.html)
- [Executable](https://github.com/brianm/really-executable-jars-maven-plugin)
- Spring enabled (no or little XML)
    - mvc [no xml] (http://rockhoppertech.com/blog/spring-mvc-configuration-without-xml/)
    - [java security, not xml](http://blog.springsource.org/2011/08/01/spring-security-configuration-with-scala/), see [scala example](https://github.com/32degrees/halfpipe/tree/master/halfpipe-scala-example)
    - data
    - integration
- [Finagle](http://twitter.github.com/finagle/) integrational ala [finagle resteasy](https://github.com/opower/finagle-resteasy)
    - TODO: Finagle client integration
- Multilingual java/Scala
    - [scala-spring](https://github.com/ewolff/scala-spring), see [scala example](https://github.com/32degrees/halfpipe/tree/master/halfpipe-scala-example)
- Guava integration
- Validation
- Commands
- Jersey
- Metrics
    - Health Checks
    - web metrics
    - TODO: jetty metrics
    - spring metrics, TODO: including forking metrics-spring which is no longer maintained
    - TODO: [jersey 2](http://jersey.java.net/documentation/snapshot/index.html)
- Spring command line [spring shell](http://www.springsource.org/spring-shell/)
    - TODO: Scala repl [via scala maven plugin](http://davidb.github.com/scala-maven-plugin/example_console.html)
    - TODO: Yeoman integration?
- Dynamic Config [Archaius](https://github.com/Netflix/archaius)
    - config classes
    - dynamic reload of config files
    - yaml or json config files
    - callbacks when config property changes
    - TODO: validate config
    - TODO: flush out configuration for: http, logginc, etc...
- Polyglot
    - Java
    - TODO: Groovy
    - Scala 2.10
        - TODO: [Spring Scala](http://blog.springsource.org/2012/12/10/introducing-spring-scala)
- TODO: Netflix OSS Platform [flux capacitor example app](https://github.com/cfregly/fluxcapacitor)
    - TODO: Service registry and loadbalancer [Eureka](https://github.com/Netflix/eureka)
- TODO: upload to [sonatype](https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide)
- TODO: Netty instead of finagle
- TODO: Model example after RSS recipe and flux capacitor
- TODO: Hysterix command simplification
- TODO: Yammer metrics to hysterix?
- TODO: Cloud formation templates?
- TODO: Admin like karyon
- TODO: Gradle?

Examples
-----
To run the examples:

Java cmd line: `example/target/halfpipe-example server example_config.yml`

Scala cmd line: `halfpipe.scalaexample.ExampleScalaApp server config.yml`

- 'server' runs the jetty server
- empty args runs the interactive shell with custom commands loaded
- in the examples try with 'hello' as the argument