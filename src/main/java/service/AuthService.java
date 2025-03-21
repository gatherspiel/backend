package service;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.client5.http.*;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.util.ArrayList;


public class AuthService {

    final String API_KEY="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImthcnF5c2t1dWRudmZ4b2h3a29rIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDE5ODQ5NjgsImV4cCI6MjA1NzU2MDk2OH0.TR-Pn6dknOTtqS9y-gxK_S1-nw6TX-sL3gRH2kXJY_I";
    final String URL = "https://karqyskuudnvfxohwkok.supabase.co/auth/v1/token?grant_type=password";
    public boolean validate(InputRequest request) throws Exception {
        final HttpPost httpPost = new HttpPost(URL);
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("apikey",API_KEY);
        final HttpClient httpClient = HttpClients.createDefault();

        AuthRequest authRequest = new AuthRequest();
        authRequest.setPassword(request.getPassword());
        authRequest.setEmail(request.getEmail());


        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(authRequest);

        HttpEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        httpPost.setEntity(stringEntity);

        System.out.println(json);
        try {

            HttpResponse rawResponse = httpClient.execute(httpPost);

            final int statusCode = rawResponse.getCode();
            System.out.println(rawResponse);
            System.out.println(statusCode);

        } catch(Exception e) {
            System.out.println("Authorization failed");
            throw(e);
        }
        System.out.println("Authorized");
        return true;
    }
}
