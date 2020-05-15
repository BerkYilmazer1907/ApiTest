package BackOffice.CostDefinitions;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class PostCostDefinitionSave {
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
    public void ForeignExchangeQuoteGet() throws ParseException {

        RestAssured.baseURI = "https://test-core.settlego.com/bo/cost/TRS_IN";
        RequestSpecification request = RestAssured.given();


        //Specifying request payload in JSON Format

        JSONObject requestParams = new JSONObject();
        JSONObject costDefinition = new JSONObject();
        costDefinition.put("clientId", "31717");
        costDefinition.put("currency", "GBP");
        costDefinition.put("amount", "0.5");
        costDefinition.put("costDefinitionType", "FIX");
        requestParams.put("costDefinition",costDefinition);



        // Add a header stating the Request body is a JSON
        request.header("Content-Type", "application/json");
        request.header("AUTH_HEADER_INSTANCE_API_KEY", AUTH_HEADER_INSTANCE_API_KEY);
        request.header("X-AUTH-TOKEN", token);

        // Add the Json to the body of the request
        request.body(requestParams.toJSONString());


        //Post the request and check the response
        Response response = request.post();

        //Printing Response
        System.out.println("Response Body Is:" + response.body().asString());

        //Valide Response Status Code
        int statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 200);

        //Valide Response Success Payment

        HashMap<String, String> amount = response.jsonPath().get("costDefinition");
        String amountValue = "";
        for (Map.Entry<String, String> entry : amount.entrySet()) {
            if (entry.getKey().equals("clientId"))
                amountValue = String.valueOf(entry.getValue());
        }
        System.out.println(amountValue);
        Assert.assertEquals(amountValue, "31717");

    }

}

