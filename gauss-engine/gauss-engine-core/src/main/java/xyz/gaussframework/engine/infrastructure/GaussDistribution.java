package xyz.gaussframework.engine.infrastructure;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This is
 *
 * @author Chang Su
 * @version 1.0
 * @since 10/5/2022
 * @param <D> subclass for BaseDao
 * @param <E> subclass for Binomial
 */
public abstract class GaussDistribution<D extends BaseDao<E>, E extends Binomial> {

    protected D entityDao;

    protected void setDao(D dao) {
        this.entityDao = dao;
    }

    protected Optional<E> queryEntity(Long id) {
        return Optional.ofNullable(entityDao)
                .map(d -> d.select(id));
    }

    protected Optional<List<E>> queryWithCondition(Map<String, Object> condition) {
        return queryWithFilter(condition, (e) -> e.getValid() == 0);
    }

    protected Optional<List<E>> queryWithFilter(Map<String, Object> condition, Predicate<E> filter) {
        return Optional.ofNullable(entityDao)
                .map(d -> d.selectEntities(condition).stream()
                        .filter(filter)
                        .collect(Collectors.toList()));
    }
}
