/**
 * 
 */
package loadbalancer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * @author tarique
 *
 */
public class HttpConnection {
	private static ObjectMapper mapper = new ObjectMapper();

	public static void main(String[] args) throws Exception {

		HttpConnection http = new HttpConnection();
		http.sendGet("getState");
		State state= new State();
		state.setCpuUsePercent(40.0);
		http.sendPost("submitState", mapper.writeValueAsString(state));

	}
	
	
	
	

	// HTTP GET request
	public static String sendGet(String path) throws Exception {

		String url = Config.remoteHost+"/"+path;
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		System.out.println(response);

		return response.toString();

	}
	
	// HTTP POST request
	public static String sendPost(String path, Object postBody) throws Exception {

		String body=mapper.writeValueAsString(postBody);	
		String url = Config.remoteHost+"/"+path;
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		// Send post request
		con.setDoOutput(true);
		con.setRequestProperty("Content-Type", "text/plain"); 
		con.setRequestProperty("charset", "utf-8");
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(body);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		System.out.println(response);
		
		return response.toString();

	}
}
