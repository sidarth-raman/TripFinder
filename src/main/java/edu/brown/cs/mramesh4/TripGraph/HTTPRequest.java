package edu.brown.cs.mramesh4.TripGraph;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a class for an HTTPRequest
 * @param <T>
 */
public class HTTPRequest<T> {
  private String url;
  private List<List<String>> headers;
  private HttpRequest request;
  private HttpClient client;

  /**
   * Blank HTTP Request client, this is if the user doesn't know what URL to use yet.
   */
  public HTTPRequest(){
    this.url = "";
    this.headers = new ArrayList<List<String>>();
    this.client = HttpClient.newHttpClient();
  }


  /**
   * This is an http request that takes in an url and a list of parameters and builds
   * an HTTP request
   * @param url a url to use
   * @param headers headers for a request
   */
  public HTTPRequest(String url, List<List<String>> headers) throws IllegalArgumentException{
    for(List<String> header: headers){
      if(header.size() > 2){
        throw new IllegalArgumentException("Error: Headers should have only two values");
      }
    }
    this.url = url;
    this.headers = headers;
    this.client = HttpClient.newHttpClient();
  }

  /**
   * This is a method to build the HTTPRequest
   */
  private void buildRequest(){
    HttpRequest.Builder requestBuild = HttpRequest.newBuilder()
      .GET()
      .uri(URI.create(url));
    for(List<String> header: headers){
      requestBuild.header(header.get(0), header.get(1));
    }
    this.request = requestBuild.build();
    System.out.println("request");
  }

  /**
   * This sets the url and headers for the httprequest class.
   * @param url a url to use
   * @param headers headers to use
   */
  public void setUrlAndHeaders(String url, List<List<String>> headers){
    this.url = url;
    this.headers = headers;
  }

  /**
   * This gets a response from the API. If there is no response it returns null
   * @return response from the api;
   * @throws Exception if the user hasn't loaded a URL or headers
   */
  public HttpResponse<String> getResponse() throws Exception{
    if(this.url.equals("") && this.headers.isEmpty()){
      throw new Exception("Error: No url supplied");
    }
    buildRequest();
    try {
      HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
      System.out.println("got a response");
      return resp;
    } catch(IOException | InterruptedException e){
      System.out.println("Error" + e);
      return null;
    }
  }










}
