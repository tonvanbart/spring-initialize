package org.vanbart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Properties;

/**
 * Example Spring context initializer which uses Properties to switch profiles and initialize annotated beans.
 */
@Component
public class ExampleApplicationContextInitializer implements ApplicationContextInitializer {

    @Autowired
    Properties configProps;

    /**
     * {@inheritDoc}
     */
    public void initialize(ConfigurableApplicationContext ctx) {
        if (configProps == null) {
            System.out.println("No configproperties found, exiting");
            return;
        }

        if (configProps.containsKey("profile")) {
            String profile = configProps.getProperty("profile");
            System.out.println("Switching active profile to '"+profile+"'");
            ctx.getEnvironment().setActiveProfiles(profile);
            ctx.refresh();
        }

        System.out.println("Initializing annotated beans");
        Map<String, Object> configurableBeans = ctx.getBeansWithAnnotation(Configurable.class);
        for (String name : configurableBeans.keySet()) {
            System.out.println("Configuring bean ["+name+"]");
            Object bean = configurableBeans.get(name);
            Field[] fields = bean.getClass().getDeclaredFields();
            for (Field field: fields) {
                if (field.isAnnotationPresent(ConfigureValue.class)) {
                    ConfigureValue configureValue = field.getAnnotation(ConfigureValue.class);
                    String defaultValue = configureValue.defaultValue();
                    String key = configureValue.propertyName();
                    String configuredValue = configProps.getProperty(key);
                    if (configuredValue != null) {
                        System.out.println("Setting field '"+field.getName()+"' on bean '"+name+"' to value '"+configuredValue+"'");
                        setFieldOnBean(bean, field, configuredValue);
                    } else {
                        System.out.println("Setting field '"+field.getName()+"' on bean '"+name+"' to default:'"+defaultValue+"'");
                        setFieldOnBean(bean, field, defaultValue);
                    }
                }
            }
        }
    }

    /**
     * Set a field on a bean to a given value.
     * @param bean
     * @param field
     * @param value
     */
    private void setFieldOnBean(Object bean, Field field, Object value)  {
        try {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            field.set(bean, value);
            field.setAccessible(accessible);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void setConfigProps(Properties configProps) {
        this.configProps = configProps;
    }
}
