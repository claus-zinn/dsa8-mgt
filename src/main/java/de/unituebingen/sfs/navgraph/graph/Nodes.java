package de.unituebingen.sfs.navgraph.graph;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class Nodes {

    private final Map<String, Integer>  idToVertex;
    private final Map<Integer, String>  vertexToId;
    private final Map<Integer, String>  vertexToCity;
    private final Map<String,  String>  vertexStringToCity;        // for cleaning
    private final Map<Integer, Float>   vertexToLongitude;
    private final Map<Integer, Float>   vertexToLatitude;
    private final Map<String,  Integer> cityToPrefVertex;
    
    private int totalV;
    
    public static Nodes readNodes(BufferedReader reader) throws IOException {

        Map<String, Integer> idToVertex = new HashMap<String, Integer>();
        Map<Integer, String> vertexToId = new HashMap<Integer, String>();

        Map<Integer, Float>   vertexToLatitude   = new HashMap<Integer, Float>();
        Map<Integer, Float>   vertexToLongitude  = new HashMap<Integer, Float>();
	Map<String,  String>  vertexStringToCity = new HashMap<String,  String>();
        Map<Integer, String>  vertexToCity       = new HashMap<Integer, String>();	
        Map<String,  Integer> cityToPrefVertex   = new HashMap<String,  Integer>();		

        String line;
	int v = 0;

	// read first line and ignore
	reader.readLine();
	
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(";");
            String vertexId = parts[0];
	    String city     = parts[3];
	    
	    // in all cases, associate the vertex string to city
	    vertexStringToCity.put(vertexId, city);
	    
	    // the first city encountered in the list of nodes is the preferred city, the others will be "mapped" to it
	    if (!cityToPrefVertex.containsKey(city)) {
		cityToPrefVertex.put(city, v);

		// vertex string (and vice versa)
		idToVertex.put(vertexId, v);
		vertexToId.put(v, vertexId); 
		vertexToCity.put(v, city);
		
		// vertex latitude and longitude
		float vertexLatitude = Float.parseFloat( parts[4] );
		float vertexLongitude = Float.parseFloat( parts[5] );	    
		vertexToLatitude.put(v, vertexLatitude);
		vertexToLongitude.put(v, vertexLongitude);
		
		System.out.printf("Processing: %d:%s:%s--%s:%s\n", v, vertexId, vertexLatitude, vertexLongitude, city);
		v++;
	    } else {
		System.out.printf("Processing duplicate: %s:%s:%s\n", vertexId, city, cityToPrefVertex.get(city));
		// no decrement
	    }
        }

        return new Nodes(idToVertex, vertexToId,
			 vertexToLatitude, vertexToLongitude,
			 vertexToCity, vertexStringToCity,
			 cityToPrefVertex, v);	
    }

    private Nodes(Map<String, Integer> idToVertex,
		  Map<Integer, String> vertexToId,
		  Map<Integer, Float> vertexToLatitude,
		  Map<Integer, Float> vertexToLongitude,
		  Map<Integer, String> vertexToCity,
		  Map<String, String> vertexStringToCity,
		  Map<String,  Integer> cityToPrefVertex,	      		  
		  int v) {
        this.idToVertex = idToVertex;
        this.vertexToId = vertexToId;
	this.vertexToLatitude = vertexToLatitude;
	this.vertexToLongitude = vertexToLongitude;
	this.vertexToCity = vertexToCity;
	this.vertexStringToCity = vertexStringToCity;
	this.cityToPrefVertex = cityToPrefVertex;
	this.totalV = v;
    }

    private static EdgeWeightedDigraph processEdges( EdgeWeightedDigraph G, Nodes nodes, BufferedReader edgeReader ) throws Exception {

	    int edgeCounter = 0;
	    String line;
	    
	    // ignore first line
	    edgeReader.readLine();
	
	    // now build a EdgeWeightedDirectedGraph
	    while ((line = edgeReader.readLine()) != null) {
		String[] parts = line.split(";");
		String source = parts[0];
		String target = parts[1];

		String sourceCity = nodes.vertexStringToCity(source);
		String targetCity = nodes.vertexStringToCity(target);

		int preferredVertexSourceCity = nodes.cityToPrefVertex(sourceCity);
		int preferredVertexTargetCity = nodes.cityToPrefVertex(targetCity);
		
		double distance = getDistance( nodes.vertexToLatitude(preferredVertexSourceCity),
					       nodes.vertexToLongitude(preferredVertexSourceCity),
					       nodes.vertexToLatitude(preferredVertexTargetCity),
					       nodes.vertexToLongitude(preferredVertexTargetCity) );
		
		DirectedEdge e = new DirectedEdge(preferredVertexSourceCity, preferredVertexTargetCity, distance);
		G.addEdge(e);
		
		edgeCounter++;
	    }
	    System.out.printf( "\nRead %d edges\n", edgeCounter );

	    return G;
    }
    
    public int getTotal() {
	return this.totalV;
    }
    
    public String vertexToCity(int vertex) {
        return vertexToCity.get(vertex);	
    }

    public int cityToPrefVertex(String vertex) {
        return cityToPrefVertex.get(vertex);	
    }
    
    public String vertexStringToCity(String vertex) {
        return vertexStringToCity.get(vertex);	
    }
    
    public Float vertexToLatitude(int vertex) {
        return vertexToLatitude.get(vertex);	
    }
    
    public Float vertexToLongitude(int vertex) {
        return vertexToLongitude.get(vertex);	
    }

    // computing the distance between two given geographical positions
    private static double getDistance( float lat1, float lon1, float lat2, float lon2 ) {

	int R = 6371; // Radius of the earth in km
	double dLat = deg2rad(lat2-lat1);  
	double dLon = deg2rad(lon2-lon1);

	double a =  Math.sin(dLat/2) * Math.sin(dLat/2) +
	    Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * 
	    Math.sin(dLon/2) * Math.sin(dLon/2);
	
	double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
	double d = R * c; // Distance in km
	return d;
    }

    private static double deg2rad(float deg) {
	return deg * (Math.PI / 180);
    }

    public static void main( String[] args ) throws Exception
    {

	String cityStart = args[0];
	String cityEnd   = args[1];
	
        System.out.println( "Hello NodesReader" );

	Nodes nodes;	
        BufferedReader nodeReader = new BufferedReader(new FileReader("/Users/zinn/Teaching/DSA-CL-III/navigationExercice/navgraph/src/main/java/de/unituebingen/sfs/navgraph/data_nodes.csv"));
	
        try {
            nodes = Nodes.readNodes(nodeReader);
        } finally {
           nodeReader.close();
        }

        System.out.printf( "\nRead %d unique nodes\n", nodes.getTotal() );
        EdgeWeightedDigraph G = new EdgeWeightedDigraph( nodes.getTotal() );
	
        BufferedReader edgeReader = new BufferedReader(new FileReader("/Users/zinn/Teaching/DSA-CL-III/navigationExercice/navgraph/src/main/java/de/unituebingen/sfs/navgraph/data_edges.csv"));

        try {	
	    G = processEdges( G, nodes, edgeReader );
	} finally {
	    edgeReader.close();
	}

        // compute shortest paths
	int s = 0; // Berlin
        DijkstraSP sp = new DijkstraSP(G, s);

        // print shortest path
        for (int t = 0; t < G.V(); t++) {
            if (sp.hasPathTo(t)) {
                StdOut.printf("From %s to %s (%.2f) go from ", nodes.vertexToCity(s), nodes.vertexToCity(t), sp.distTo(t));
                for (DirectedEdge e : sp.pathTo(t)) {
		    StdOut.printf("%s -> %s (%.2f)  ", nodes.vertexToCity(e.from()), nodes.vertexToCity(e.to()), e.weight());  
                }
                StdOut.println();
            }
            else {
                StdOut.printf("%d to %d         no path\n", s, t);
            }
        }

	int cityStartVertex = nodes.cityToPrefVertex(cityStart);
	int cityEndVertex = nodes.cityToPrefVertex(cityEnd);	
	
	System.out.printf("Trying to find shortest path between %s (%d) and %s (%d)\n",
			  cityStart, cityStartVertex,
			  cityEnd,   cityEndVertex);
	
	
        sp = new DijkstraSP(G, cityStartVertex);
        for (int t = 0; t < G.V(); t++) {
            if ((nodes.vertexToCity(t).equals(cityEnd)) && (sp.hasPathTo(t))) {
                StdOut.printf("From %s to %s (%.2f) go from ", nodes.vertexToCity(cityStartVertex), nodes.vertexToCity(t), sp.distTo(t));
                for (DirectedEdge e : sp.pathTo(t)) {
		    StdOut.printf("%s -> %s (%.2f)  ", nodes.vertexToCity(e.from()), nodes.vertexToCity(e.to()), e.weight());  
                }
                StdOut.println();
            }
            else {
                StdOut.printf("%d to %d         no path\n", cityStartVertex, t);
            }
        }
	
    }
}
