package com.fvelasquez.wm.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Optional;

import com.fvelasquez.wm.domain.GeocodeAPIResponse;
import com.fvelasquez.wm.domain.GeocodeAPIResponseStatus;
import com.fvelasquez.wm.domain.Response;
import com.fvelasquez.wm.domain.Result;
import com.fvelasquez.wm.domain.Status;
import com.google.gson.Gson;

/**
 * Service used to match addresses to geolocations used Google's geocode API
 */
public class AddressGeolocationService {

    protected final String SERVICE_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=%s";
    protected final Gson gson = new Gson();

    /**
     * Get the Location from an address.
     *
     * @param address the address
     * @return Response object containing the address, the status of the request, and the geolocation (if found)
     */
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

    /**
     * Search address response.
     *
     * @param address the address
     * @return the response
     */
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

    /**
     * Used to build a valid response object.
     *
     * If for some reason, the list of Results from the API response does not contain a Geolocation, a Failed response
     * will be returned.
     *
     * @param address            the address
     * @param geocodeAPIResponse the geocode api response
     * @return Response containing the address, geolocation and the status indicating the address was found.
     */
    protected Response processValidResponse(String address, GeocodeAPIResponse geocodeAPIResponse) {


        Optional<Result> optResult = geocodeAPIResponse.getResults()
                                                       .stream()
                                                       .filter(result -> result.getGeometry() != null &&
                                                                         result.getGeometry().getLocation() != null)
                                                       .findFirst();

        if (!optResult.isPresent()) {
            return processInvalidResponse(address);
        }

        Response response = new Response();

        response.setAddress(address);
        response.setStatus(Status.FOUND);
        optResult.ifPresent(result -> response.setLocation(result.getGeometry().getLocation()));

        return response;
    }

    /**
     * Used to build a failed/invalid response object.
     *
     * @param address            the address
     * @return Response containing the address and the status indicating the address was not found.
     */
    protected Response processInvalidResponse(String address) {
        Response response = new Response();

        response.setAddress(address);
        response.setStatus(Status.NOT_FOUND);

        return response;
    }


}
