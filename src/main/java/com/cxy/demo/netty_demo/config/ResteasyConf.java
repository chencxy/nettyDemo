/**
 * 
 */
package com.cxy.demo.netty_demo.config;

import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Configuration
public class ResteasyConf {
    @Value("${demo.port:9080}")
    private int port;
    @Value("${demo.address:0.0.0.0}")
    private String address;
    @Autowired
    private ConfigurableApplicationContext context;
    private Logger logger = LoggerFactory.getLogger(ResteasyConf.class);

    @Bean(destroyMethod = "stop")
    public NettyJaxrsServer nettyContainer() {
        ResteasyDeployment deployment = new ResteasyDeployment();
        deployment.setSecurityEnabled(false);
        List<Object> providers = new ArrayList<Object>();
        for (Object obj : context.getBeansWithAnnotation(Provider.class).values()) {
            providers.add(obj);
            Class<?> clazz = obj.getClass();
            if (!clazz.isInterface() && clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class)) {
                clazz = clazz.getSuperclass();
            }
            logger.info("rest provider '{}' registered.", clazz.getName());
        }
        deployment.setProviders(providers);
        List<Object> services = new ArrayList<Object>();
        for (Object obj : context.getBeansWithAnnotation(Path.class).values()) {
            services.add(obj);
            Class<?> pathClazz = obj.getClass(), implClazz = obj.getClass();
            if (!pathClazz.isInterface() && pathClazz.getSuperclass() != null
                && !pathClazz.getSuperclass().equals(Object.class)) {
                pathClazz = pathClazz.getSuperclass();
                implClazz = pathClazz;
                if (pathClazz.getAnnotation(Path.class) == null) {
                    for (Class<?> cls : pathClazz.getInterfaces()) {
                        if (cls.getAnnotation(Path.class) != null) {
                            pathClazz = cls;
                            break;
                        }
                    }
                }
            }
            Path p = pathClazz.getAnnotation(Path.class);
            for (Method m : pathClazz.getDeclaredMethods()) {
                Path mp = m.getAnnotation(Path.class);
                if (mp == null)
                    continue;
                logger.info("rest service '{}' is provided by '{}->{}'.", p.value() + mp.value(), implClazz.getName(),
                    m.getName());
            }
        }
        deployment.setResources(services);
        NettyJaxrsServer netty = new NettyJaxrsServer();
        netty.setExecutorThreadCount(Runtime.getRuntime().availableProcessors());
        Map<String, Object> channelOptions = new HashMap<String, Object>();
        channelOptions.put("child.tcpNoDelay", true);
        netty.setChannelOptions(channelOptions);
        netty.setDeployment(deployment);
        netty.setPort(port);
        netty.setHostname(address);
        netty.setSecurityDomain(null);
        netty.start();
        logger.info("netty jaxrs container started on port '{}'", port);
        return netty;
    }
}
