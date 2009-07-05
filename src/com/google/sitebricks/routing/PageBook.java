package com.google.sitebricks.routing;

import com.google.inject.AbstractModule;
import com.google.inject.ImplementedBy;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.sitebricks.Renderable;

import java.util.Map;

/**
 * @author Dhanji R. Prasanna (dhanji@gmail.com)
 */
@ImplementedBy(DefaultPageBook.class)
public interface PageBook {

  /**
   * Register a page class at the given contextual URI.
   *
   * @return A {@link Page} representing the given class
   *  without a compiled template applied.
   */
  Page at(String uri, Class<?> myPageClass);

  /**
   *
   * @param uri A contextual URI where a page (maybe) registered.
   * @return A {@link Page} object thatis capable of rend
   */
  Page get(String uri);

  Page forName(String name);

  /**
   * Registers a page class as an embeddable.
   * @param as The annotation name to register this widget as.
   *  Example: {@code "Hello"} will make this page class
   * available for embedding as <pre>{@literal @}Hello</pre>.
   */
  Page embedAs(Class<?> page, String as);

  /**
   * Same as {@link #get} except guaranteed not to trigger a
   * cascading compile of page bricks.
   */
  Page nonCompilingGet(String uri);

  public static interface Page {
    Renderable widget();

    Object instantiate();

    Object doMethod(String httpMethod, Object page, String pathInfo, Map<String, String[]> params);

    Class<?> pageClass();

    void apply(Renderable widget);
  }

  public static final class Routing extends AbstractModule {
    private Routing() {
    }

    @Override
    protected final void configure() {
      if (Stage.DEVELOPMENT.equals(binder().currentStage())) {
        bind(PageBook.class)
            .annotatedWith(Production.class)
            .to(DefaultPageBook.class);

        bind(RoutingDispatcher.class)
            .annotatedWith(Production.class)
            .to(WidgetRoutingDispatcher.class);
      }
    }

    public static Module module() {
      return new Routing();
    }
    
    //Ensures only one instance of the Routine module is installed.
    @Override
    public boolean equals(Object obj) {
      return obj instanceof Routing;
    }

    @Override
    public int hashCode() {
      return Routing.class.hashCode();
    }
  }
}