import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.vanbart.ExampleApplicationContextInitializer;
import org.vanbart.MessageClient;

import java.util.Properties;

import static org.junit.Assert.*;

/**
 *
 */
public class ContextTest {

    private ConfigurableApplicationContext ctx;

    @Before
    public void loadContext() throws Exception {
        ctx = new ClassPathXmlApplicationContext("context.xml");
//        for (String name : ctx.getBeanDefinitionNames()) {
//            System.out.println("Found a bean named ["+name+"]");
//        }
    }

    @Test
    public void serviceIsFound() throws Exception {
        assertTrue(ctx.containsBean("messageClient"));
    }

    @Test
    public void printServiceMessage() throws Exception {
        MessageClient messageClient = (MessageClient) ctx.getBean("messageClient");
        assertEquals("This is profile default", messageClient.messageValue());
        messageClient.printMessage();
    }

    @Test
    public void initializer_initsContext() throws Exception {
        ExampleApplicationContextInitializer initializer = new ExampleApplicationContextInitializer();
        Properties properties = new Properties();
        properties.setProperty("profile","test1");
        initializer.setConfigProps(properties);

        initializer.initialize(ctx);

        MessageClient messageClient = (MessageClient) ctx.getBean("messageClient");
        assertEquals("This is profile test1", messageClient.messageValue());
        assertEquals("default value", messageClient.showConfiguredValue());
        messageClient.printMessage();
    }

    @Test
    public void initializer_initsContextAndSetsField() throws Exception {
        ExampleApplicationContextInitializer initializer = new ExampleApplicationContextInitializer();
        Properties properties = new Properties();
        properties.setProperty("profile","test1");
        properties.setProperty("someValue","value from initializer");
        initializer.setConfigProps(properties);

        initializer.initialize(ctx);

        MessageClient messageClient = (MessageClient) ctx.getBean("messageClient");
        assertEquals("This is profile test1", messageClient.messageValue());
        assertEquals("value from initializer", messageClient.showConfiguredValue());
        messageClient.printMessage();
    }


}
