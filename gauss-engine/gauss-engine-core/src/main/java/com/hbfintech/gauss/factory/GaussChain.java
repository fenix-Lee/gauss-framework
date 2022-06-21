package com.hbfintech.gauss.factory;

import com.hbfintech.gauss.basis.BeanFactory;
import com.hbfintech.gauss.framework.Chain;
import com.hbfintech.gauss.framework.Chains;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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

    @Resource
    private ApplicationContext context;

    private List<T> modules;

    /*
     * strongly recommend returning a copy of operations instead of
     * original one
     *
     */
    public List<T> getModules() {
        return copyModules();
    }

    private List<T> copyModules() {
        return modules.stream()
                .map(BeanFactory::copyObject)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unused")
    protected void setModules(List<T> modules) {
        this.modules = modules;
    }

    protected List<T> sortModule(Map<Integer, T> container) {
        return container.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    private void afterPropertiesSet() {
        Class<?> chainClass = getClass();
        // get all classes with specific annotation
        // notice: this method will get different results(based on different version of spring boot)
        Map<String, Object> modulesWithChainsMap = context.getBeansWithAnnotation(Chains.class);

        Map<Integer, T> container = new HashMap<>();
        modulesWithChainsMap.values().forEach(m -> chainsAnnotationCap(chainClass,  container, (T)m));

        Map<String, Object> modulesWithChainMap = context.getBeansWithAnnotation(Chain.class);
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
                if (container.containsKey(chain.sequence()))
                    throw new RuntimeException();
                container.put(chain.sequence(), (T)m);
            }
        });
        modules = sortModule(container);
    }

    private void chainsAnnotationCap(Class<?> chainClass, Map<Integer, T> container, T module) {
        chainsAnnotationCap(chainClass, container, module, m -> {throw new RuntimeException(m.getClass().getName()
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
