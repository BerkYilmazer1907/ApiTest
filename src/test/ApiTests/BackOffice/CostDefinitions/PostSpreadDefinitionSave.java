package BackOffice.CostDefinitions;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.testng.annotations.Test;

import java.util.*;

public class PostSpreadDefinitionSave {
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
    public void SpreadDefinitionSave() throws ParseException {

        RestAssured.baseURI = "https://test-core.settlego.com/bo/spread/FX";
        RequestSpecification request = RestAssured.given();

        JSONObject requestParams = new JSONObject();
        JSONObject spreadDefinition = new JSONObject();
        spreadDefinition.put("clientId", "31717");
        spreadDefinition.put("currency", "TRY");
        JsonArray definitionDetails = new JsonArray();
        List<JsonObject> definitionDetailList = new ArrayList<>();
        JsonObject definitionDetail = new JsonObject();
        definitionDetail.addProperty("costDefinitionType", "RATIO");
        definitionDetail.addProperty("ratio", "10");
        definitionDetail.addProperty("lowerAmountBound", "0");
        definitionDetail.addProperty("upperAmountBound", "1000");
        definitionDetail.addProperty("minimumAmount", "10");
        definitionDetail.addProperty("maximumAmount", "100");
        definitionDetailList.add(definitionDetail);
        JsonObject definitionDetail1 = new JsonObject();
        definitionDetail1.addProperty("costDefinitionType", "RATIO");
        definitionDetail1.addProperty("ratio", "5");
        definitionDetail1.addProperty("lowerAmountBound", "1000");
        definitionDetail1.addProperty("upperAmountBound", "2000");
        definitionDetail1.addProperty("minimumAmount", "50");
        definitionDetail1.addProperty("maximumAmount", "500");
        definitionDetailList.add(definitionDetail1);
        JsonObject definitionDetail2 = new JsonObject();
        definitionDetail2.addProperty("costDefinitionType", "RATIO");
        definitionDetail2.addProperty("ratio", "3");
        definitionDetail2.addProperty("lowerAmountBound", "2000");
        definitionDetail2.addProperty("upperAmountBound", "-1");
        definitionDetailList.add(definitionDetail2);
        definitionDetailList.stream().forEach(item -> definitionDetails.add(item));

        spreadDefinition.put("definitionDetails", definitionDetails);
        requestParams.put("spreadDefinition", spreadDefinition);


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

        HashMap<String, String> amount = response.jsonPath().get("spreadDefinition");
        String amountValue = "";
        for (Map.Entry<String, String> entry : amount.entrySet()) {
            if (entry.getKey().equals("clientId"))
                amountValue = String.valueOf(entry.getValue());
        }
        System.out.println(amountValue);
        Assert.assertEquals(amountValue, "31717");


    }
}
