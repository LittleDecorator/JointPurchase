package com.acme.service;

import com.acme.model.dto.RouteDto;

import java.io.IOException;
import java.util.List;

/**
 * Created by nikolay on 22.10.17.
 */
public interface RouteService {

    List<RouteDto> getSimpleRoutes() throws IOException;

}
