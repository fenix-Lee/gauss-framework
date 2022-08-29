package xyz.gaussframework.engine.framework;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Target;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Enables Gauss conversion and mapping capability. This is very important annotation for Gauss Engine
 * core functions especially for custom-defined convertor and self-defined {@link Mapper} for entities.
 *
 * @author Chang Su
 * @version 1.0
 * @see Mapper
 * @see GaussConvertor
 * @since 6/7/2022
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({GaussConversionRegistrar.class, GaussMapperImportSelector.class})
public @interface EnableGaussEngine {

    /**
     * Alias for the {@link #basePackages()} attribute. Allows for more concise annotation
     * declarations e.g.: {@code @ComponentScan("org.my.pkg")} instead of
     * {@code @ComponentScan(basePackages="org.my.pkg")}.
     * @return the array of 'basePackages'.
     */
    String[] value() default {};

    /**
     * Base packages to scan for annotated components.
     * <p>
     * {@link #value()} is an alias for (and mutually exclusive with) this attribute.
     * <p>
     * Use {@link #basePackageClasses()} for a type-safe alternative to String-based
     * package names.
     *
     * @return the array of 'basePackages'.
     */
    @SuppressWarnings("unused")
    String[] basePackages() default {};

    /**
     * Type-safe alternative to {@link #basePackages()} for specifying the packages to
     * scan for annotated components. The package of each class specified will be scanned.
     * <p>
     * Consider creating a special no-op marker class or interface in each package that
     * serves no purpose other than being referenced by this attribute.
     *
     * @return the array of 'basePackageClasses'.
     */
    @SuppressWarnings("unused")
    Class<?>[] basePackageClasses() default {};
}
