package com.fvelasquez.wm.service;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.fvelasquez.wm.domain.GeocodeAPIResponse;
import com.fvelasquez.wm.domain.Geometry;
import com.fvelasquez.wm.domain.Location;
import com.fvelasquez.wm.domain.Response;
import com.fvelasquez.wm.domain.Result;
import com.fvelasquez.wm.domain.Status;

@RunWith(JUnit4.class)
public class AddressGeolocationServiceTest {

    final AddressGeolocationService service = new AddressGeolocationService();

    @Test
    public void testProcessValidReponse() {
        String address = "123 Any St";
        BigDecimal lat = new BigDecimal("12.3456789");
        BigDecimal lng = new BigDecimal("-98.7654321");

        Location location = new Location();
        location.setLat(lat);
        location.setLng(lng);

        Geometry geometry = new Geometry();
        geometry.setLocation(location);

        Result result = new Result();
        result.setGeometry(geometry);

        GeocodeAPIResponse geocodeResponse = new GeocodeAPIResponse();
        geocodeResponse.setResults(Collections.singletonList(result));

        Response response = service.processValidResponse(address, geocodeResponse);

        Assert.assertEquals(address, response.getAddress());
        Assert.assertEquals(Status.FOUND, response.getStatus());
        Assert.assertEquals(lat, response.getLocation().getLat());
        Assert.assertEquals(lng, response.getLocation().getLng());
    }

    @Test
    public void testProcessValidReponseWithInvalidGeocodeResponse() {
        String address = "123 Any St";

        Result result = new Result();

        GeocodeAPIResponse geocodeResponse = new GeocodeAPIResponse();
        geocodeResponse.setResults(Collections.singletonList(result));

        Response response = service.processValidResponse(address, geocodeResponse);

        Assert.assertEquals(address, response.getAddress());
        Assert.assertEquals(Status.NOT_FOUND, response.getStatus());
        Assert.assertNull(response.getLocation());
    }

    @Test
    public void testProcessInvalidResponse() {
        String address = "123 Any St";

        Response response = service.processInvalidResponse(address);

        Assert.assertEquals(address, response.getAddress());
        Assert.assertEquals(Status.NOT_FOUND, response.getStatus());
        Assert.assertNull(response.getLocation());
    }

}
