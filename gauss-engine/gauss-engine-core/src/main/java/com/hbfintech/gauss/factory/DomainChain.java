package com.hbfintech.gauss.factory;

import com.hbfintech.gauss.framework.Chain;
import com.hbfintech.gauss.framework.Chains;
import com.hbfintech.gauss.util.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

public abstract class DomainChain<T> implements InitializingBean {

    @Resource
    protected ApplicationContext context;

    protected List<T> modules;

    /*
     * strongly recommend returning a copy of operations instead of
     * original one
     *
     */
    protected final List<T> getModules() {
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

    protected void setModules(List<T> modules) {
        this.modules = modules;
    }

    protected List<T> sortModule(Map<Integer, T> container) {
        return container.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
}
