package com.dp.bigdata.taurus.restlet.resource;

import org.restlet.resource.Get;

/**
 * Created by kirinli on 15/1/30.
 */
public interface IExceptionHosts {
    @Get
    public String retrieve();
}