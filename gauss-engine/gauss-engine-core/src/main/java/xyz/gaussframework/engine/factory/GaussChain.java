package xyz.gaussframework.engine.factory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.Setter;
import xyz.gaussframework.engine.framework.GaussBeanFactory;
import xyz.gaussframework.engine.exception.GaussFactoryException;
import xyz.gaussframework.engine.framework.Chain;
import xyz.gaussframework.engine.framework.Chains;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A component for combining all necessary chains for specific factory
 *
 * @author Chang Su
 * @version 2.0
 * @since 8/5/2022
 * @param <T> chain class type
 */
public abstract class GaussChain<T> {

    @Setter
    private List<T> modules;

    private volatile boolean isInitialed;

    // strongly recommend returning a copy of operations instead of original one
    public final List<T> getModules() {
        return copyModules();
    }

    private List<T> copyModules() {
        if (ObjectUtils.isEmpty(modules)) {
            // avoid NullPointException
            return Lists.newArrayList();
        }
        return modules.stream()
                .map(GaussBeanFactory::copyObject)
                .collect(Collectors.toList());
    }

    private List<T> sortModule(Map<Integer, T> container) {
        return container.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    private synchronized void moduleInitialization() {
        if (!ObjectUtils.isEmpty(modules)) {
            isInitialed = true;
            return;
        }
        if (isInitialed) {
            return;
        }
        Class<?> chainTypeClass = getClass();
        while (!ObjectUtils.isEmpty(chainTypeClass)) {
            if (chainTypeClass.equals(GaussFactory.class)) {
                return;
            }
            Map<Integer, T> container = getModuleContainer(chainTypeClass);
            if (!ObjectUtils.isEmpty(container)) {
                modules = sortModule(container);
                break;
            }
            chainTypeClass = chainTypeClass.getSuperclass();
        }
    }

    public void init () {
        if (!isInitialed) {
            moduleInitialization();
            isInitialed = true;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, T> getModuleContainer (Class<?> chainClass) {
        // get all classes with specific annotation
        // notice: this method will get different results(based on different version of spring boot)
        Map<String, Object> modulesWithChainsMap = GaussBeanFactory.getBeansWithAnnotation(Chains.class);

        Map<Integer, T> container = Maps.newHashMapWithExpectedSize(modulesWithChainsMap.size());
        modulesWithChainsMap.values().forEach(m -> chainsAnnotationCap(chainClass,  container, (T)m));

        Map<String, Object> modulesWithChainMap = GaussBeanFactory.getBeansWithAnnotation(Chain.class);
        modulesWithChainMap.values().forEach(m -> {
            Chain chain = AnnotationUtils.findAnnotation(m.getClass(), Chain.class);
            if (ObjectUtils.isEmpty(chain)) {
                // the @Chain annotation must be in @Chains
                // need to check the work is done yet
                chainsAnnotationCap(chainClass,  container, (T)m, module -> {});
                return;
            }
            Assert.notNull(chain.factory(), chain.getClass().getName() + "'s factory cannot be null.....");
            if (chain.factory().equals(chainClass)) {
                if (container.containsKey(chain.sequence())) {
                    throw new IllegalArgumentException(chain.getClass().getName() +
                            "has already been contained please check the sequence.....");
                }
                container.put(chain.sequence(), (T)m);
            }
        });
        return container;
    }

    private void chainsAnnotationCap(Class<?> chainClass, Map<Integer, T> container, T module) {
        chainsAnnotationCap(chainClass, container, module, m -> {throw new GaussFactoryException(m.getClass().getName()
                + " has already been contained please check the sequence.....");});
    }

    private void chainsAnnotationCap(Class<?> chainClass, Map<Integer, T> container,
                                     T module, Consumer<T> ifContainedThen) {
        Class<?> moduleClass = module.getClass();
        Chains chains = AnnotationUtils.findAnnotation(moduleClass, Chains.class);
        assert chains != null;
        Arrays.stream(chains.value()).forEach(c -> {
            /*
             * cannot use Validated annotation for non-null check coz spring will enhance beans by CGlib
             * with that annotation so that orika cannot copy or map to other beans
             */
            Assert.notNull(c.factory(), c.getClass().getName() + "'s factory cannot be null.....");
            if (c.factory().equals(chainClass)) {
                if (container.containsKey(c.sequence())) {
                    ifContainedThen.accept(module);
                }
                container.put(c.sequence(), module);
            }
        });
    }

}
