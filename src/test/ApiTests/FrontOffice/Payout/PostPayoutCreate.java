package FrontOffice.Payout;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class PostPayoutCreate {

    public static String AUTH_HEADER_INSTANCE_API_KEY = "11E336EE91F5CEE67BB57EFD622314A2";
    public static String token = "";
    public static String quoteReference = "";
    public static String foToken = "";

    @Test(priority = 1, enabled = true)
    public void loginPost() throws ParseException {

        RestAssured.baseURI = "https://test-core.settlego.com/fo/login";
        RequestSpecification request = RestAssured.given();

        //Specifying request payload in JSON Format
        JSONObject requestParams = new JSONObject();
        requestParams.put("apiKey", "11E336EE91F5CEE67BB57EFD622314A2");
        requestParams.put("email", "linkedonpex1@com.com");
        requestParams.put("password", "Test12345");

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
    public void VerifyTwoSendSms() throws ParseException {

        RestAssured.baseURI = "https://test-core.settlego.com/fo";
        RequestSpecification request = RestAssured.given();

        //Specifying request payload in JSON Format

        // Add a header stating the Request body is a JSON
        request.header("X-AUTH-TOKEN", token);
        request.header("Content-Type", "application/json");
        request.header("AUTH_HEADER_INSTANCE_API_KEY", AUTH_HEADER_INSTANCE_API_KEY);


        Response response = request.request(Method.POST, "/user/two-factor/sms/send");

        //Printing Response
        System.out.println("Response Body Is:" + response.body().asString());

        //Valide Response Status Code
        int statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 200);

    }

    @Test(priority = 3, enabled = true)

    public void VerifyTwoFactorAuth() throws ParseException {
        RestAssured.baseURI = "https://test-core.settlego.com/fo";
        RequestSpecification request = RestAssured.given();

        // Add a header stating the Request body is a JSON
        request.header("X-AUTH-TOKEN", token);
        request.header("Content-Type", "application/json");
        request.header("AUTH_HEADER_INSTANCE_API_KEY", AUTH_HEADER_INSTANCE_API_KEY);

        Response response = request.request(Method.POST, "/user/two-factor/sms/validate/12345");

        //Printing Response
        System.out.println("Response Body Is:" + response.body().asString());

        //Valide Response Status Code
        int statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 200);
        foToken = response.getHeader("X-AUTH-TOKEN");

    }

    @Test(priority = 4, enabled = true)


    public void payoutCostQuote() throws ParseException {


        //Specifying request payload in JSON Format
        RestAssured.baseURI = "https://test-core.settlego.com/fo/payout/quote";
        RequestSpecification request = RestAssured.given();

        //Specifying request payload in JSON Format

        JSONObject requestParams = new JSONObject();
        requestParams.put("currency", "EUR");
        requestParams.put("amount", "350");
        requestParams.put("cost", "110");
        requestParams.put("totalDebitAmount", "reference");
        requestParams.put("quoteReference", quoteReference);
        requestParams.put("hasEnoughBalance", "true");
        requestParams.put("feeCollectionType", "ONS");
        requestParams.put("feeVisible", "true");
        requestParams.put("paymentType", "STD");

        // Add a header stating the Request body is a JSON
        request.header("Content-Type", "application/json");
        request.header("AUTH_HEADER_INSTANCE_API_KEY", AUTH_HEADER_INSTANCE_API_KEY);
        request.header("X-AUTH-TOKEN", foToken);

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

        HashMap<String, String> amount = response.jsonPath().get("payOutQuote");
        String amountValue = "";
        for (Map.Entry<String, String> entry : amount.entrySet()) {
            if (entry.getKey().equals("amount"))
                amountValue = String.valueOf(entry.getValue());
        }
        System.out.println(amountValue);
        Assert.assertEquals(amountValue, "350");

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response.body().asString());
        JSONObject object = (JSONObject) json.get("payOutQuote");
        quoteReference = (String) object.get("quoteReference");
        System.out.println(quoteReference);

    }


    @Test(priority = 5, enabled = true)
    public void payoutCustomer() {

        RestAssured.baseURI = "https://test-core.settlego.com/fo/payout/create";
        RequestSpecification request = RestAssured.given();

        //Specifying request payload in JSON Format
        JSONObject requestParams = new JSONObject();
        JSONObject payOutObject = new JSONObject();

        payOutObject.put("paymentDate", "2020-04-04");
        payOutObject.put("amount", "350");
        payOutObject.put("currency", "EUR");
        payOutObject.put("beneficiaryId", "10222");
        payOutObject.put("paymentType", "STD");
        payOutObject.put("reason", "samplePerform");
        payOutObject.put("reasonCode", "1");
        payOutObject.put("paymentReference", "reference");
        payOutObject.put("quoteReference", quoteReference);
        requestParams.put("payOut", payOutObject);

        // Add a header stating the Request body is a JSON
        request.header("Content-Type", "application/json");
        request.header("X-AUTH-TOKEN", foToken);

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

        HashMap<String, String> amount = response.jsonPath().get("payOut");
        String amountValue = "";
        for (Map.Entry<String, String> entry : amount.entrySet()) {
            if (entry.getKey().equals("beneficiaryId"))
                amountValue = String.valueOf(entry.getValue());
        }
        System.out.println(amountValue);
        Assert.assertEquals(amountValue, "10222");

    }
}
