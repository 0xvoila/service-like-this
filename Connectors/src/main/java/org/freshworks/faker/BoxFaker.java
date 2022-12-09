package org.freshworks.faker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.freshworks.connectors.box.Application;
import org.freshworks.connectors.box.Usage;
import org.freshworks.connectors.box.User;
import org.freshworks.core.model.DiscoveryObject;

public class BoxFaker {

    public static String generateApplication() throws JsonProcessingException {
        com.github.javafaker.Faker faker = new com.github.javafaker.Faker();
        Application app = new Application();
        String appName = faker.name().fullName();
        app.setApplicationName(appName);
        app.setId(faker.name().name());

        DiscoveryObject discoveryObject = new DiscoveryObject(Application.class.getPackage().getName(), app);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(discoveryObject);
    }

    public static String generateUsage() throws JsonProcessingException {
        com.github.javafaker.Faker faker = new com.github.javafaker.Faker();

        Application app = new Application();
        String appName = faker.name().fullName();
        app.setApplicationName(appName);
        app.setId(faker.name().name());


        Usage usage = new Usage(app);
        usage.setUsage(faker.name().fullName());
        usage.setLogin("abc");
        usage.setId(faker.name().fullName());
        DiscoveryObject discoveryObject = new DiscoveryObject(Usage.class.getPackage().getName(), usage);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(discoveryObject);
    }


    public static String generateUser() throws JsonProcessingException {
        com.github.javafaker.Faker faker = new com.github.javafaker.Faker();

        Application app = new Application();
        String appName = faker.name().fullName();
        app.setApplicationName(appName);
        app.setId(faker.name().name());


        User user = new User(app);
        user.setId("abc");
        user.setName(faker.name().fullName());
        user.setAddress(faker.address().fullAddress());
        user.setAvatar_url(faker.animal().name());
        user.setCreated_at(faker.date().toString());
        user.setLanguage(faker.programmingLanguage().name());
        user.setJob_title(faker.job().title());
        user.setPhone(faker.phoneNumber().cellPhone());
        DiscoveryObject discoveryObject = new DiscoveryObject(User.class.getPackage().getName(), user);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(discoveryObject);
    }

}
