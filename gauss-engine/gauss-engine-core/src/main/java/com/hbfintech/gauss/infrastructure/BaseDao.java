package com.hbfintech.gauss.infrastructure;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Chang Su
 * @version 1.0
 * @since 10/05/2022
 * @param <P> base persistent object
 */
public interface BaseDao<P> {

    default P select(Long id) {return null;}

    default List<P> selectEntities(Map<String, Object> condition) {return null;}

    default int insert(P PlainObject) {return 0;}

    default int update(Map<String, Object> condition) {return 0;}

    default int batchInsert(List<P> batch) {
        return 0;
    }
}
