package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.downloader.models.Report;
import org.example.utils.EtcdClient;
import org.example.utils.KafkaProducer;
import static org.junit.Assert.assertEquals;



public class validate_kafka_success_report {

    static ObjectMapper mapper = new ObjectMapper();

    int total_number_of_success_request ;
    String key = "104/okta/users";

    @Given("{int} successful requests")
    public void successfulRequests(int totalNumberOfRequests) {
        total_number_of_success_request = totalNumberOfRequests;
    }

    @When("Added into the kafka {string} topic")
    public void addedIntoTheKafkaTopic(String arg0) {
        //        Here add requests to kafka in the topic given by arg
        try{

            KafkaProducer.produce(key, arg0, 0, total_number_of_success_request);
        }
        catch(Exception e){
            System.out.println("Exception here " + e.getMessage());
        }
    }


    @When("wait for {int} minutes to download execute the request")
    public void waitForMinutesToDownloadExecuteTheRequest(int arg0) {
        try{
            Thread.sleep(arg0*60*1000);
        }
        catch(Exception e){
            System.out.println( "Exception occured" + e.getMessage());
        }

    }

    @Then("Report should show {int} total received request and {int} successful requests")
    public void reportShouldShowTotalReceivedRequestAndSuccessfulRequests(int totalRequestReceieved, int totalSuccessfulRequests) {
        try{
            String report = EtcdClient.getKey(key + "/downloader/report").getKvs(0).getValue().toStringUtf8();
            Report report1 = mapper.readValue(report, Report.class);
            System.out.println("Total request received : " + totalRequestReceieved + " and received responses are : "+ report1.getTotalRequest());
            System.out.println("Total request received : " + totalSuccessfulRequests + " and received responses are : "+ report1.getTotalSuccess());
            assertEquals(totalRequestReceieved,report1.getTotalRequest());
            assertEquals(totalSuccessfulRequests,report1.getTotalSuccess());
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

}
