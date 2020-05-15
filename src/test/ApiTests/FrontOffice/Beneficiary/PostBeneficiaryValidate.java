package FrontOffice.Beneficiary;

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

import java.util.HashMap;
import java.util.Map;

public class PostBeneficiaryValidate {

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


    public void CheckBeneficiaryValidate() throws ParseException {


        //Specifying request payload in JSON Format
        RestAssured.baseURI = "https://test-core.settlego.com/fo/beneficiary/validate";
        RequestSpecification request = RestAssured.given();

        //Specifying request payload in JSON Format

        JsonObject benefParams = new JsonObject();
        JsonObject beneficiaryObject = new JsonObject();
        benefParams.add("beneficiary", beneficiaryObject);
        beneficiaryObject.addProperty("id", "201");
        JsonObject addressObject = new JsonObject();
        beneficiaryObject.add("address", addressObject);
        addressObject.addProperty("city", "gdf");
        addressObject.addProperty("line", "gfd");
        addressObject.addProperty("countryId", "175");
        addressObject.addProperty("postalCode", "1234");
        addressObject.addProperty("stateId", "66");
        beneficiaryObject.addProperty("legalEntityType", "CRP");
        beneficiaryObject.addProperty("countryId", "175");
        JsonObject bankAccountObject = new JsonObject();
        beneficiaryObject.add("bankAccount", bankAccountObject);
        bankAccountObject.addProperty("accountNumber", "null");
        bankAccountObject.addProperty("iban", "IE07AIBK93107122360028");
        bankAccountObject.addProperty("bic", "AIBKIE2DXXX");
        bankAccountObject.addProperty("currency", "EUR");
        bankAccountObject.addProperty("countryId", "106");
        bankAccountObject.addProperty("holderName", "Rock+Music+LTD");
        JsonArray paymentTypeList = new JsonArray();
        paymentTypeList.add("STD");
        bankAccountObject.add("paymentTypeList", paymentTypeList);
        JsonArray routingCodeList = new JsonArray();
        JsonObject keyAndValue = new JsonObject();
        keyAndValue.addProperty("key", "aba");
        keyAndValue.addProperty("value", "211370545");
        routingCodeList.add(keyAndValue);
        bankAccountObject.add("routingCodeList", routingCodeList);
        JsonObject bankInformation = new JsonObject();
        bankAccountObject.add("bankInformation", bankInformation);
        bankInformation.addProperty("bankName", "TEST BANK");
        bankInformation.addProperty("bankAccountType", "EXP");
        bankInformation.addProperty("bankAddress", "TEST ADDRESS");
        bankInformation.addProperty("standingOrderExist", "null");
        JsonObject additionalInformation = new JsonObject();
        beneficiaryObject.add("additionalInformation", additionalInformation);
        additionalInformation.addProperty("taxId", "null");
        JsonObject title = new JsonObject();
        beneficiaryObject.addProperty("title", "dfg");


        // Add a header stating the Request body is a JSON
        request.header("Content-Type", "application/json");
        request.header("AUTH_HEADER_INSTANCE_API_KEY", AUTH_HEADER_INSTANCE_API_KEY);
        request.header("X-AUTH-TOKEN", foToken);

        // Add the Json to the body of the request
        request.body(benefParams.toString());


        //Post the request and check the response
        Response response = request.post();

        //Printing Response
        System.out.println("Response Body Is:" + response.body().asString());

        //Valide Response Status Code
        int statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 200);

        //Valide Response Success Payment

        HashMap<String, String> amount = response.jsonPath().get("bankInformation");
        String amountValue = "";
        for (Map.Entry<String, String> entry : amount.entrySet()) {
            if (entry.getKey().equals("bankName"))
                amountValue = String.valueOf(entry.getValue());
        }
        System.out.println(amountValue);
        Assert.assertEquals(amountValue, "null");

    }
}

