package com.hbfintech.gauss.infrastructure;

import java.util.Optional;

public class BaseRepository<D extends BaseDao<E>, E extends BasePO> {

    protected D entityDao;

    protected void setDao(D dao) {
        this.entityDao = dao;
    }

    protected Optional<E> queryEntity(Long id) {
        return Optional.ofNullable(entityDao)
                .map(d -> d.select(id));
    }
}
