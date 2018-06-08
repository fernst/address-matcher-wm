package com.fvelasquez.wm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.fvelasquez.wm.domain.Response;
import com.fvelasquez.wm.service.AddressGeolocationService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AddressGeolocation {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) {

        try {

            List<String> addresses = getAddressList();
            List<Response> responses = getResponseList(addresses);

            System.out.println(gson.toJson(responses));

        } catch (IOException ioe) {
            System.err.println("Unable to open file: " + ioe.getMessage());
        }

    }

    /**
     * Read the Address list from the file stored in the classpath and return as a list of strings
     *
     * @return list of strings containing all addresses read from file.
     */
    private static List<String> getAddressList() throws IOException {

        List<String> addressList = new LinkedList<>();

        InputStream inputStream = AddressGeolocation.class.getClassLoader()
                                                          .getResourceAsStream("addresses.txt");

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));

        String address;

        while((address = reader.readLine()) != null) {
            addressList.add(address);
        }

        return addressList;
    }

    /**
     * Create a new instance of the Address Matching service and match each address to it's geolocation.
     *
     * For performance reasons, the list is processed in a paralell stream.
     *
     * @return list of responses obtained from Google's Geocode API.
     */
    private static List<Response> getResponseList(List<String> addressList) {
        AddressGeolocationService service = new AddressGeolocationService();

        return addressList.parallelStream()
                          .map(service::getLocation)
                          .collect(Collectors.toList());
    }

}
