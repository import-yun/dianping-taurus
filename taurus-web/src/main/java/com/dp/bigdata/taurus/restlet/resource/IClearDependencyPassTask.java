package com.dp.bigdata.taurus.restlet.resource;

import org.restlet.resource.Get;

/**
 * Created by kirinli on 14/10/28.
 */
public interface IClearDependencyPassTask {
    @Get
    public int retrieve();
}
