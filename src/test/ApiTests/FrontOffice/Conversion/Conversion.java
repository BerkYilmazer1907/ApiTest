package FrontOffice.Conversion;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.testng.annotations.Test;

public class Conversion {


    public static String AUTH_HEADER_INSTANCE_API_KEY = "11E336EE91F5CEE67BB57EFD622314A2";
    public static String token = "";
    public static String firstForeignExchangeDate = "";
    public static String reference = "";
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
    public void getForeignExchangeDatesGet() throws ParseException, JSONException {

        RestAssured.baseURI = "https://test-core.settlego.com/fo";
        RequestSpecification request = RestAssured.given();


        request.header("X-AUTH-TOKEN", foToken);

        Response response = request.request(Method.GET, "foreignExchange/get/invalid/dates?foreignExchangePair=GBPEUR");

        //Printing Response
        System.out.println("Response Body Is:" + response.body().asString());

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response.body().asString());
        firstForeignExchangeDate = (String) json.get("firstForeignExchangeDate");
        System.out.println(firstForeignExchangeDate);

        //Valide Response Status Code
        int statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 200);

    }

    @Test(priority = 5, enabled = true)
    public void postForeignExchangeQuoteCreate() throws ParseException, JSONException {

        RestAssured.baseURI = "https://test-core.settlego.com/fo";
        RequestSpecification request = RestAssured.given();

        JSONObject quoteObject = new JSONObject();


        quoteObject.put("sellCurrency", "GBP");
        quoteObject.put("buyCurrency", "EUR");
        quoteObject.put("fixedSide", "S");
        quoteObject.put("amount", "200");
        quoteObject.put("foreignExchangeDate", firstForeignExchangeDate);

        request.header("X-AUTH-TOKEN", foToken);
        request.header("Content-Type", "application/json");
        request.body(quoteObject.toJSONString());

        Response response = request.request(Method.POST, "foreignexchange/quote");

        //Printing Response
        System.out.println("Response Body Is:" + response.body().asString());
        JSONParser parserQuote = new JSONParser();
        JSONObject jsonQuote = (JSONObject) parserQuote.parse(response.body().asString());
        JSONObject jsonOutputObject = (JSONObject) jsonQuote.get("output");
        reference = (String) jsonOutputObject.get("reference");
        System.out.println(reference);

        //Valide Response Status Code
        int statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 200);

        String id = "";
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response.body().asString());

        JSONObject output = (JSONObject) json.get("output");
        Object currencyPair = output.get("currencyPair");
        Assert.assertEquals(currencyPair, "GBPEUR");
    }

    @Test(priority = 6, enabled = true)
    public void postForeignExchangeCreate() throws ParseException, JSONException {

        RestAssured.baseURI = "https://test-core.settlego.com/fo";
        RequestSpecification request = RestAssured.given();

        JSONObject createObject = new JSONObject();


        createObject.put("reference", reference);
        createObject.put("termAgreement", "true");


        request.header("X-AUTH-TOKEN", foToken);
        request.header("Content-Type", "application/json");
        request.body(createObject.toJSONString());

        Response response = request.request(Method.POST, "foreignexchange/create");

        //Printing Response
        System.out.println("Response Body Is:" + response.body().asString());

        //Valide Response Status Code
        int statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 200);

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response.body().asString());

        JSONObject output = (JSONObject) json.get("output");
        Object sellCurrency = output.get("sellCurrency");
        System.out.println(sellCurrency);
        Assert.assertEquals(sellCurrency, "GBP");
    }
}