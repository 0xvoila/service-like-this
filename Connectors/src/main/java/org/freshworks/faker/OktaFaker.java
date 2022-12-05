package org.freshworks.faker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.freshworks.connectors.okta.Application;
import org.freshworks.connectors.okta.ServicePrincipal;
import org.freshworks.connectors.okta.Usage;
import org.freshworks.connectors.okta.User;
import org.freshworks.core.DiscoveryObject;

public class OktaFaker {

    public static String generateApplication() throws JsonProcessingException {
        com.github.javafaker.Faker faker = new com.github.javafaker.Faker();
        Application app = new Application();
        String appName = faker.name().fullName();
        app.setAppName(appName);

        app.setAppId(faker.number().randomDigit());

        DiscoveryObject discoveryObject = new DiscoveryObject(Application.class.getPackage().getName(), app);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(discoveryObject);
    }

    public static String generateServicePrincipal() throws JsonProcessingException {
        com.github.javafaker.Faker faker = new com.github.javafaker.Faker();

        Application app = new Application();
        String appName = faker.name().fullName();
        app.setAppName(appName);
        app.setAppId(faker.number().randomDigit());

        ServicePrincipal servicePrincipal = new ServicePrincipal(app);
        String principalName = faker.name().fullName();
        servicePrincipal.setServicePrincipalName(principalName);
        servicePrincipal.setId(faker.number().randomDigit());

        DiscoveryObject discoveryObject = new DiscoveryObject(ServicePrincipal.class.getPackage().getName(), servicePrincipal);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(discoveryObject);

    }

    public static String generateUser() throws JsonProcessingException {
        com.github.javafaker.Faker faker = new com.github.javafaker.Faker();

        Application app = new Application();
        String appName = faker.name().fullName();
        app.setAppName(appName);
        app.setAppId(faker.number().randomDigit());

        ServicePrincipal servicePrincipal = new ServicePrincipal(app);
        String principalName = faker.name().fullName();
        servicePrincipal.setServicePrincipalName(principalName);
        servicePrincipal.setId(faker.number().randomDigit());

        User user = new User(servicePrincipal);
        String userName = faker.name().fullName();
        user.setUserName(userName);
        int x = faker.number().randomDigit();
//        user.setId(x);
        user.setId(123);

        DiscoveryObject discoveryObject = new DiscoveryObject(User.class.getPackage().getName(), user);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(discoveryObject);
    }

    public static String generateUsage() throws JsonProcessingException {
        com.github.javafaker.Faker faker = new com.github.javafaker.Faker();

        Application app = new Application();
        String appName = faker.name().fullName();
        app.setAppName(appName);
        app.setAppId(faker.number().randomDigit());

        ServicePrincipal servicePrincipal = new ServicePrincipal(app);
        String principalName = faker.name().fullName();
        servicePrincipal.setServicePrincipalName(principalName);
        servicePrincipal.setId(faker.number().randomDigit());


        Usage usage = new Usage(servicePrincipal);
        usage.setUserUsage("axnd");
//        usage.setId(faker.number().randomDigit());
        usage.setId(123);
        DiscoveryObject discoveryObject = new DiscoveryObject(User.class.getPackage().getName(), usage);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(discoveryObject);
    }
}
