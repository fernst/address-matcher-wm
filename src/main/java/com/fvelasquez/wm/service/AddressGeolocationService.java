package com.fvelasquez.wm.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import com.fvelasquez.wm.domain.GeocodeAPIResponse;
import com.fvelasquez.wm.domain.GeocodeAPIResponseStatus;
import com.fvelasquez.wm.domain.Response;
import com.fvelasquez.wm.domain.Status;
import com.google.gson.Gson;

public class AddressGeolocationService {

    protected final String SERVICE_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=%s";
    protected final Gson gson = new Gson();

    public Response getLocation(String address) {

        Response response;

        for (int i = 1 ; i <= 5 ; i++) {
            response = searchAddress(address);
            if (response != null) return response;

            try {
                Thread.sleep(100 * i);
            } catch (InterruptedException e) {
                System.err.println("Sleeping thread was interrupted.");
            }
        }

        return processInvalidResponse(address);
    }

    protected Response searchAddress(String address) {
        try {
            String requestURL = String.format(SERVICE_URL, URLEncoder.encode(address, "UTF-8"));
            URL url = new URL(requestURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();

            if (responseCode == 200) {

                InputStreamReader inputStreamReader = new InputStreamReader(con.getInputStream());
                GeocodeAPIResponse geocodeAPIResponse = gson.fromJson(inputStreamReader, GeocodeAPIResponse.class);
                GeocodeAPIResponseStatus status = GeocodeAPIResponseStatus.fromString(geocodeAPIResponse.getStatus());

                if (GeocodeAPIResponseStatus.OK.equals(status)) {
                    return processValidResponse(address, geocodeAPIResponse);
                }

                //If a request contains no results, or the request is invalid, return a Not Found response.
                if (GeocodeAPIResponseStatus.ZERO_RESULTS.equals(status) ||
                    GeocodeAPIResponseStatus.INVALID_REQUEST.equals(status)) {
                    return processInvalidResponse(address);
                }

            }
        } catch (MalformedURLException mue) {
            System.err.println("Exception caused by malformed URL: " + mue.getMessage());
        } catch (ProtocolException pe) {
            System.err.println("Exception caused by Java Protocol Exception: " + pe.getMessage());
        } catch (IOException ioe) {
            System.err.println("IO Exception when processing request or parsing JSON response: " + ioe.getMessage());
        }

        return null;
    }

    protected Response processValidResponse(String address, GeocodeAPIResponse geocodeAPIResponse) {
        Response response = new Response();

        response.setAddress(address);
        response.setStatus(Status.NOT_FOUND);

        geocodeAPIResponse.getResults()
                          .stream()
                          .filter(result -> result.getGeometry() != null && result.getGeometry().getLocation() != null)
                          .findFirst()
                          .ifPresent(result -> {
                              response.setLocation(result.getGeometry().getLocation());
                              response.setStatus(Status.FOUND);
                          });

        return response;
    }

    protected Response processInvalidResponse(String address) {
        Response response = new Response();

        response.setAddress(address);
        response.setStatus(Status.NOT_FOUND);

        return response;
    }


}
