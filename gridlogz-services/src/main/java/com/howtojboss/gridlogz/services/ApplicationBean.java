package com.howtojboss.gridlogz.services;

import javax.enterprise.context.ApplicationScoped;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 * @author Shane K Johnson
 */
@ApplicationPath("/application")
@ApplicationScoped
public class ApplicationBean extends Application {
    
}
