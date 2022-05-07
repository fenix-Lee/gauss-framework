package com.hbfintech.gauss.factory;

import com.hbfintech.gauss.framework.Chain;
import com.hbfintech.gauss.framework.Chains;
import com.hbfintech.gauss.framework.DomainFactory;
import com.hbfintech.gauss.framework.Module;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class GaussFactory<T extends Module, R> extends DomainChain<T>
        implements DomainFactory<T, R> {

    public abstract Class<?> getChainClassType();

    @Override
    public List<R> manufacture(Function<? super T, ? extends R> mapper) {
        return null;
    }

    @Override
    public R fabricate(Function<? super T, ? extends R> mapper) {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() {
        Class<?> chainClass = getChainClassType();
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
                assert c.chain() != null;
                if (c.chain().equals(chainClass)) {
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
            assert chain.chain() != null;
            if (chain.chain().equals(chainClass)) {
                if (container.containsKey(chain.sequence()))
                    throw new RuntimeException();
                container.put(chain.sequence(), (T)m);
            }
        });
        modules = sortModule(container);
    }
}
