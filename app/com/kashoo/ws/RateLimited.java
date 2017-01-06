package com.kashoo.ws;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to be applied to {@link play.api.libs.ws.WSClient} injected dependencies to have rate limiting enabled on
 * requests made with it.  Example:
 *
 * <pre>
 *   class ExampleController @Inject() (@RateLimited ws: WSClient) extends Controller { ...
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@BindingAnnotation
public @interface RateLimited {
}
