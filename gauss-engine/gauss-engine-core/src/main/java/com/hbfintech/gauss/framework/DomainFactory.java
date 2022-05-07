package com.hbfintech.gauss.framework;

import java.util.List;

public interface DomainFactory<T extends Module> {

    default List<T> manufacture() {return null;}

    default T fabricate() { return null;}
}
