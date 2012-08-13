package org.vanbart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Example bean which holds some configurable value.
 */
@Component
@Configurable
public class MessageClient {

    @Autowired
    private String message;

    @ConfigureValue(defaultValue = "default value", propertyName = "someKey")
    private String myText;

    public void printMessage() {
        System.out.println("message = " + message);
    }

    public String messageValue() {
        return message;
    }

    public String showConfiguredValue() {
        return myText;
    }

}
