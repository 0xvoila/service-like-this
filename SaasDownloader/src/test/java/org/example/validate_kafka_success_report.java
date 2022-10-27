package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.downloader.models.Report;
import org.example.utils.EtcdClient;
import org.example.utils.KafkaProducer;
import static org.junit.Assert.assertEquals;



public class validate_kafka_success_report {

    static ObjectMapper mapper = new ObjectMapper();
    static CloseableHttpClient httpClient = HttpClients.createDefault();

    int total_number_of_success_request ;
    int total_number_of_failure_request;
    String key = "";

    @Given("{int} requests are 200Ok request")
    public void successfulRequests(int totalNumberOfSuccessRequests) {
        total_number_of_success_request = totalNumberOfSuccessRequests;
    }

    @Given("{int} requests are 400 request")
    public void failedRequests(int totalNumberOfFailedRequests) {
        total_number_of_failure_request = totalNumberOfFailedRequests;
    }
    @Given("Having rate limit of {int} per minute")
    public void havingRateLimitOfLimitPerMinute( int rateLimit) {
        String url = "http://localhost:8080/register/abc/" + rateLimit;

        try {
            CloseableHttpResponse response = httpClient.execute(new HttpGet(url));
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Given("Key {string} for which we want to send requests")
    public void keyForWhichWeWantToSendRequests(String arg0) {
        System.out.println("Key is " + arg0);
        this.key = arg0;
    }

    @When("Added into the kafka {string} topic")
    public void addedIntoTheKafkaTopic(String arg0) {
        //        Here add requests to kafka in the topic given by arg
        try{

            KafkaProducer.produce(key, arg0, total_number_of_failure_request, total_number_of_success_request);
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

    @Then("Report should show {int} plus {int} total received request and {int} successful requests")
    public void reportShouldShowTotalReceivedRequestAndSuccessfulRequests(int totalSuccessfulRequestsSent, int totalFailedRequestSent, int totalSuccessfulRequests) {
        try{
            String report = EtcdClient.getKey(key + "/downloader/report").getKvs(0).getValue().toStringUtf8();
            Report report1 = mapper.readValue(report, Report.class);
            assertEquals(totalSuccessfulRequestsSent +  totalFailedRequestSent,report1.getTotalRequest());
            assertEquals(totalSuccessfulRequests,report1.getTotalSuccess());
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    @Then("Report should show {int} plus {int} total received request and {int} have failed")
    public void reportShouldShowNo_of_requestsTotalReceivedRequestAndNo_of_failure_requestsHaveFailed(int totalSuccessfulRequestsSent, int totalFailedRequestSent, int totalFailedRequest) {
        try{
            String report = EtcdClient.getKey(key + "/downloader/report").getKvs(0).getValue().toStringUtf8();
            Report report1 = mapper.readValue(report, Report.class);
            assertEquals(totalSuccessfulRequestsSent +  totalFailedRequestSent,report1.getTotalRequest());
            assertEquals(totalFailedRequest,report1.getTotalFailure());
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
