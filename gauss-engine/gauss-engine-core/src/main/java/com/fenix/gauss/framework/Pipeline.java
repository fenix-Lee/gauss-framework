package com.fenix.gauss.framework;

/**
 * The interface Pipeline is used for method-chain class, which is by multiple operations to change or modify inner
 * fields or even methods
 *
 * @author Chang Su
 * @since /13/3/2022
 * @see java.util.stream.Stream
 */
public interface Pipeline {

    /**
     * must be committed in the end for all operations in pipeline
     */
    void commit();
}
