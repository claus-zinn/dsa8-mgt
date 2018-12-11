package de.unituebingen.sfs.navgraph;

import java.io.IOException;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class Cities {

    // given
    private final Map<String, String> vertexIdToCity;
    private final Map<Integer, String> vertexToCity;
    private final Map<Integer, Float> vertexToLongitude;
    private final Map<Integer, Float> vertexToLatitude;
    private final Map<String, Integer> cityToPrefVertex;
    private int numberCities;

    public static Cities readCities(BufferedReader reader) throws IOException {

        Map<Integer, Float> vertexToLatitude = new HashMap<Integer, Float>();
        Map<Integer, Float> vertexToLongitude = new HashMap<Integer, Float>();
        Map<Integer, String> vertexToCity = new HashMap<Integer, String>();
        Map<String, String> vertexIdToCity = new HashMap<String, String>();
        Map<String, Integer> cityToPrefVertex = new HashMap<String, Integer>();
        int numberCities = 0;

        // your code comes here

        return new Cities(vertexToLatitude,
                vertexToLongitude,
                vertexIdToCity,
                vertexToCity,
                cityToPrefVertex,
                numberCities);
    }

    // given
    private Cities(Map<Integer, Float> vertexToLatitude,
                   Map<Integer, Float> vertexToLongitude,
                   Map<String, String> vertexIdToCity,
                   Map<Integer, String> vertexToCity,
                   Map<String, Integer> cityToPrefVertex,
                   int numberCities) {

        this.vertexToLatitude = vertexToLatitude;
        this.vertexToLongitude = vertexToLongitude;
        this.vertexToCity = vertexToCity;
        this.vertexIdToCity = vertexIdToCity;
        this.cityToPrefVertex = cityToPrefVertex;
        this.numberCities = numberCities;
    }

    public int numberCities() {
        // your code comes here
        return 0;
    }

    public String vertexToCity(int vertex) {
        // your code comes here
        return "";
    }

    public int cityToPrefVertex(String city) {
        // your code comes here
        return 0;
    }

    public String vertexIdToCity(String vertex) {
        // your code comes here
        return "";
    }

    public Float vertexToLatitude(int vertex) {
        // your code comes here
        return 0.0f;
    }

    public Float vertexToLongitude(int vertex) {
        // your code comes here
        return 0.0f;
    }
}
