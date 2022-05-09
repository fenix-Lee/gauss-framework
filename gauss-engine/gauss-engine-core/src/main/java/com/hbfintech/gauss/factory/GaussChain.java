package com.hbfintech.gauss.factory;

import com.hbfintech.gauss.basis.BeanFactory;
import com.hbfintech.gauss.framework.Chain;
import com.hbfintech.gauss.framework.Chains;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.*;
import java.util.stream.Collectors;

public abstract class GaussChain<T> implements InitializingBean, ApplicationContextAware {

    protected ApplicationContext context;

    protected List<T> modules;

    /*
     * strongly recommend returning a copy of operations instead of
     * original one
     *
     */
    public List<T> getModules() {
        return copyModules();
    }

    protected final List<T> accessModules() {
        return modules;
    }

    private List<T> copyModules() {
        return modules.stream()
                .map(BeanFactory::originalCopy)
                .collect(Collectors.toList());
    }

    public void setModules(List<T> modules) {
        this.modules = modules;
    }

    protected List<T> sortModule(Map<Integer, T> container) {
        return container.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() {
        Class<?> chainClass = getClass();
        // get all classes with specific annotation
        Map<String, Object> modulesWithChainsMap = context.getBeansWithAnnotation(Chains.class);

        Map<Integer, T> container = new HashMap<>();
        modulesWithChainsMap.values().forEach(m -> {
            Class<?> moduleClass = m.getClass();
            Chains chains = AnnotationUtils.findAnnotation(moduleClass, Chains.class);
            assert chains != null;
            Arrays.stream(chains.value()).forEach(c -> {
                /*
                 * cannot use Validated annotation for non-null check coz spring will enhance beans by CGlib
                 * with that annotation so that orika cannot copy or map to other beans
                 */
                assert c.factory() != null;
                if (c.factory().equals(chainClass)) {
                    if (container.containsKey(c.sequence()))
                        throw new RuntimeException("");
                    container.put(c.sequence(), (T)m);
                }
            });
        });

        Map<String, Object> modulesWithChainMap = context.getBeansWithAnnotation(Chain.class);
        modulesWithChainMap.values().forEach(m -> {
            Chain chain = AnnotationUtils.findAnnotation(m.getClass(), Chain.class);
            assert chain != null;
            assert chain.factory() != null;
            if (chain.factory().equals(chainClass)) {
                if (container.containsKey(chain.sequence()))
                    throw new RuntimeException();
                container.put(chain.sequence(), (T)m);
            }
        });
        modules = sortModule(container);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
