package application

import com.google.inject.AbstractModule

/**
  * Created by dkichler on 2016-12-04.
  */
class ExampleModule extends AbstractModule {
  def configure() = {
    bind(classOf[MockServer]).asEagerSingleton()
  }
}