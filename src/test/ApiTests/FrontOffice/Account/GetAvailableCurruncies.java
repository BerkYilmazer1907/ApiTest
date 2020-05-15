package FrontOffice.Account;

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

public class GetAvailableCurruncies {

    public static String AUTH_HEADER_INSTANCE_API_KEY = "11E336EE91F5CEE67BB57EFD622314A2";
    public static String token = "";
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
        request.header("Content-Type","application/json");
        request.header("AUTH_HEADER_INSTANCE_API_KEY",AUTH_HEADER_INSTANCE_API_KEY);


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
        request.header("Content-Type","application/json");
        request.header("AUTH_HEADER_INSTANCE_API_KEY",AUTH_HEADER_INSTANCE_API_KEY);

        Response response = request.request(Method.POST, "/user/two-factor/sms/validate/12345");

        //Printing Response
        System.out.println("Response Body Is:" + response.body().asString());

        //Valide Response Status Code
        int statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 200);
        foToken=response.getHeader("X-AUTH-TOKEN");

    }

    @Test(priority = 4, enabled = true)
    public void getForeignExchangeList() throws ParseException, JSONException {

        RestAssured.baseURI = "https://test-core.settlego.com/fo";
        RequestSpecification request = RestAssured.given();

        request.header("X-AUTH-TOKEN", foToken);

        Response response = request.request(Method.GET, "available-currencies?category=PYO,PYI,FX");

        //Printing Response
        System.out.println("Response Body Is:" + response.body().asString());

        //Valide Response Status Code
        int statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 200);

        //Valide Response Success Payment
        String id = "";
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response.body().asString());
        org.json.simple.JSONArray array = (org.json.simple.JSONArray) json.get("currencyList");
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = (JSONObject) array.get(i);
            id = (String.valueOf(object.get("code")));

        }

        System.out.println(id);
        Assert.assertEquals(id, "ZAR");
    }
}
