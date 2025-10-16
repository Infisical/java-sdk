package com.infisical.sdk.auth;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.IdTokenCredentials;
import com.google.auth.oauth2.IdTokenProvider;
import com.google.auth.oauth2.IdTokenProvider.Option;
import com.infisical.sdk.util.InfisicalException;

public class GCPAuthProvider {

    public static HashMap<String,String> getGCPAuthInput(String identityId) throws InfisicalException{

        if ( identityId == null || identityId.isEmpty() )

            throw new InfisicalException( "Infisical Identity ID is required");

        try{

            // This will fetch credentials from environment variable named GOOGLE_APPLICATION_CREDENTIALS or
            // or if it's running in a GCP instance it will get them from the instance itself (GCP service account attached) 
            GoogleCredentials googleCredentials = GoogleCredentials.getApplicationDefault();

            IdTokenCredentials idTokenCredentials =
                IdTokenCredentials.newBuilder()
                    .setIdTokenProvider((IdTokenProvider) googleCredentials)
                    .setTargetAudience(identityId)
                    .build();
    
            // Get the ID token.
            String idToken = idTokenCredentials.refreshAccessToken().getTokenValue();
    
            // Body cannot be a string so used a HashMap, you can use builder, POJO etc
            HashMap<String, String> body =  new HashMap<>();
              body.put("identityId", identityId);
              body.put("jwt", idToken);

            return body;

        } catch (Exception e){
            if (e.getCause() instanceof UnknownHostException) {
                throw new InfisicalException("Unknown Host, may be check network connection ?"); 
            }
            throw new InfisicalException("Failed to fetch Google credentials: " + e.getMessage());
        }

    }
}
