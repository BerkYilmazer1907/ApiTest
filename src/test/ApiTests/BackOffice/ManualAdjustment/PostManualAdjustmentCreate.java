package BackOffice.ManualAdjustment;

import com.google.gson.JsonArray;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.testng.annotations.Test;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class PostManualAdjustmentCreate {

    public static String AUTH_HEADER_INSTANCE_API_KEY = "11E336EE91F5CEE67BB57EFD622314A2";
    public static String token = "";

    @Test(priority = 1, enabled = true)
    public void loginPost() throws ParseException {

        RestAssured.baseURI = "https://test-core.settlego.com/bo/login";
        RequestSpecification request = RestAssured.given();

        //Specifying request payload in JSON Format
        JSONObject requestParams = new JSONObject();
        requestParams.put("apiKey", "11E336EE91F5CEE67BB57EFD622314A2");
        requestParams.put("email", "canay@eftsoftware.com");
        requestParams.put("password", "Test1234");

        // Add a header stating the Request body is a JSON
        request.header("Content-Type", "application/json");
        request.header("AUTH-HEADER-INSTANCE-API-KEY", AUTH_HEADER_INSTANCE_API_KEY);

        request.body(requestParams.toJSONString());

        //Post the request and check the response
        Response response = request.post();

        //Valide Response Status Code
        int statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 200);

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response.body().asString());
        token = (String) json.get("token");
        System.out.println(token);

    }

    @Test(priority = 2, enabled = true)


    public void ManualAdjustmentCreate() throws ParseException {


        //Specifying request payload in JSON Format
        RestAssured.baseURI = "https://test-core.settlego.com/bo/manual-adjustment/create";
        RequestSpecification request = RestAssured.given();

        Random rand = new Random();

        // Generate random integers in range 0 to 999
        int spireference = rand.nextInt(1000);
        //Specifying request payload in JSON Format

        JSONObject requestParams = new JSONObject();
        JsonObject manualAdjustmentObject = new JsonObject();
        requestParams.put("manualAdjustment", manualAdjustmentObject);
        manualAdjustmentObject.addProperty("clientId",929);
        manualAdjustmentObject.addProperty("currency","EUR");
        manualAdjustmentObject.addProperty("adjustmentType","C");
        manualAdjustmentObject.addProperty("amount",400);
        manualAdjustmentObject.addProperty("serviceProvider","FMX");
        manualAdjustmentObject.addProperty("spiReference",spireference);
        manualAdjustmentObject.addProperty("clientReference","client-ref");
        manualAdjustmentObject.addProperty("clientReason","client-reason");
        manualAdjustmentObject.addProperty("internalReference","reference-1100");
        manualAdjustmentObject.addProperty("internalReason","balance_correction");



        // Add a header stating the Request body is a JSON
        request.header("Content-Type", "application/json");
        request.header("AUTH_HEADER_INSTANCE_API_KEY", AUTH_HEADER_INSTANCE_API_KEY);
        request.header("X-AUTH-TOKEN", token);

        // Add the Json to the body of the request
        request.body(requestParams.toString());


        //Post the request and check the response
        Response response = request.post();

        //Printing Response
        System.out.println("Response Body Is:" + response.body().asString());

        //Valide Response Status Code
        int statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 200);

    }
}

