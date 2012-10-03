package thirtytwo.degrees.halfpipe.scalaexample.view

import org.springframework.context.annotation.{Import, Bean, ComponentScan, Configuration}
import thirtytwo.degrees.halfpipe.config.{MetricsConfig, AbstractViewConfig}
import org.springframework.web.servlet.config.annotation.{ViewControllerRegistry, EnableWebMvc}
import org.springframework.web.servlet.view.freemarker.{FreeMarkerViewResolver, FreeMarkerConfigurer}
import thirtytwo.degrees.halfpipe.mgmt.view.MgmtControllers

/**
 * User: spencergibb
 * Date: 9/22/12
 * Time: 4:11 PM
 */
@EnableWebMvc
@Configuration
@ComponentScan(basePackageClasses = Array(classOf[ViewConfig], classOf[MgmtControllers]))
@Import(Array(classOf[MetricsConfig]))
class ViewConfig extends AbstractViewConfig {

  /*@Bean
  def jspViewResolver = {
    val view = new UrlBasedViewResolver()
    view.setViewClass(classOf[JstlView])
    view.setPrefix("/WEB-INF/jsp/")
    view.setSuffix(".jsp")
    view
  }*/

  //TODO: https://github.com/sps/mustache-spring-view

  @Bean
  def freemarkerConfig = {
    val cfg = new FreeMarkerConfigurer
    cfg.setTemplateLoaderPath("/WEB-INF/freemarker")
    cfg
  }

  @Bean
  def freemarkerViewResolver = {
    val view = new FreeMarkerViewResolver
    view.setCache(true)
    view.setPrefix("")
    view.setSuffix(".ftl")
    view
  }

  override def addViewControllers(registry: ViewControllerRegistry) {
    registry.addViewController("/").setViewName("index")
  }
}
