package org.freshworks.faker;

public class BoxFaker {

//    public static String generateApplication() throws JsonProcessingException {
//        com.github.javafaker.Faker faker = new com.github.javafaker.Faker();
//        Application app = new Application();
//        String appName = faker.name().fullName();
//        app.setApplicationName(appName);
//        app.setId(faker.name().name());
//
//        DiscoveryObject discoveryObject = new DiscoveryObject(Application.class.getPackage().getName(), app);
//        ObjectMapper objectMapper = new ObjectMapper();
//        return objectMapper.writeValueAsString(discoveryObject);
//    }
//
//    public static String generateUsage() throws JsonProcessingException {
//        com.github.javafaker.Faker faker = new com.github.javafaker.Faker();
//
//        Application app = new Application();
//        String appName = faker.name().fullName();
//        app.setApplicationName(appName);
//        app.setId(faker.name().name());
//
//
//        Usage usage = new Usage();
//        usage.setUsage(faker.name().fullName());
//        usage.setLogin(Integer.toString(faker.number().numberBetween(0,10000)));
//        usage.setId(faker.name().fullName());
//        DiscoveryObject discoveryObject = new DiscoveryObject(Usage.class.getPackage().getName(), usage);
//        ObjectMapper objectMapper = new ObjectMapper();
//        return objectMapper.writeValueAsString(discoveryObject);
//    }
//
//
//    public static String generateUser() throws JsonProcessingException {
//        com.github.javafaker.Faker faker = new com.github.javafaker.Faker();
//
//        Application app = new Application();
//        String appName = faker.name().fullName();
//        app.setApplicationName(appName);
//        app.setId(faker.name().name());
//
//
//        User user = new User();
//        user.setId(Integer.toString(faker.number().numberBetween(0,10000)));
//        user.setName(faker.name().fullName());
//        user.setAddress(faker.address().fullAddress());
//        user.setAvatar_url(faker.animal().name());
//        user.setCreated_at(faker.date().toString());
//        user.setLanguage(faker.programmingLanguage().name());
//        user.setJob_title(faker.job().title());
//        user.setPhone(faker.phoneNumber().cellPhone());
//        DiscoveryObject discoveryObject = new DiscoveryObject(User.class.getPackage().getName(), user);
//        ObjectMapper objectMapper = new ObjectMapper();
//        return objectMapper.writeValueAsString(discoveryObject);
//    }

}
