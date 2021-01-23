package stepdefs;

import java.util.List;
import org.junit.Assert;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class FindCityStepDefs
{

    private String baseURI = "http://api.citybik.es/v2/networks";
    RequestSpecification request;
    private Response response;
    Scenario scenario;


    @Before
    public void before(Scenario scenario)
    {
        this.scenario = scenario;
    }

    @Given("^City bikes API is up and User has required privilege to access$")
    public void cityBikesAPIIsUpAndUserHasRequiredPrivilegeToAccess()
    {
        RestAssured.baseURI = baseURI;
        request = RestAssured.given();
        request.header("Content-Type", "appliation/json");
    }

    @When("^A user requested details for city of \"([^\"]*)\"$")
    public void aUserRequestedDetailsForCityOf(String cityName)
    {
        response = getResponseByNetworkId(getCityNetworkId(cityName));
        if (response.getStatusCode() == 200)
        {
            String actualCity = response.then().extract().path("network.location.city");
            scenario.write("City : " + actualCity);
            Assert.assertEquals(cityName.toLowerCase(), actualCity.toLowerCase());
        }
        else
        {
            scenario.write("City is not covered in the network");
        }

    }

    private String getCityNetworkId(String cityName)
    {
        String result;
        switch (cityName.toLowerCase())
        {
            case "frankfurt":
                result = "/visa-frankfurt";
                break;
            case "moscow":
                result = "/velobike-moscow";
                break;
            default:
                result = "null";
        }
        return result;
    }

    @Then("^Receive a positive response$")
    public void receiveAPositiveResponse()
    {
        scenario.write("HTTP Status Code : " + response.getStatusCode());
        Assert.assertEquals(200, response.getStatusCode());
    }

    @Then("^Receive a negative response$")
    public void receiveANegativeResponse()
    {
        scenario.write("HTTP Status Code : " + response.getStatusCode());
        Assert.assertEquals(404, response.getStatusCode());
    }

    @And("^response contains country equals \"([^\"]*)\"$")
    public void responseContainsCountryEquals(String expectedCountry)
    {

        String actualCountry = response.then().extract().path("network.location.country");
        scenario.write("Country : " + expectedCountry);
        scenario.write("Country code : " + actualCountry);
        Assert.assertEquals(getCountryCode(expectedCountry), actualCountry.toUpperCase());
    }

    private String getCountryCode(String expectedCountry)
    {
        String result;
        switch (expectedCountry)
        {
            case "Germany":
                result = "DE";
                break;
            case "Russia":
                result = "RU";
                break;
            default:
                result = "null";
        }
        return result;
    }

    @And("^response contains latitude and longitude$")
    public void responseContainsLatitudeAndLongitude()
    {
        float latitude = response.then().extract().path("network.location.latitude");
        scenario.write("Latitude is :" + String.valueOf(latitude));
        float longitude = response.then().extract().path("network.location.longitude");
        scenario.write("Longitude is :" + String.valueOf(longitude));
        Assert.assertNotNull(latitude);
        Assert.assertNotNull(longitude);
    }

    public Response getResponseByNetworkId(String networkId)
    {
        response = request.given().get(baseURI + networkId);
        return response;
    }

    @When("^A user requested details for all cities$")
    public void aUserRequestedDetailsForAllCities()
    {
        response = request.given().get(baseURI);
    }

    public List<String> getAllCities()
    {
        String responseJsonAsString = response.asString();
        JsonPath jsonPath = new JsonPath(responseJsonAsString);
        List<String> results = jsonPath.getList("networks.location.city");
        return results;
    }

    public boolean cityExists(String city)
    {
        if (getAllCities().stream().anyMatch(v -> v.equals(city)))
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    @And("^verify \"([^\"]*)\" exists in the cities$")
    public void verifyExistsInTheCities(String city)
    {
        Assert.assertTrue(cityExists(city));
    }


}
