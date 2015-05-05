package com.eciii;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.PropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

public class ReceiverServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		AWSCredentials credentials = null;
        try {
            credentials = new ClasspathPropertiesFileCredentialsProvider().getCredentials();
        } catch (Exception e) {
        	response.getWriter().println("Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your AwsCredentials.properties file is at the correct " +
                    "location (src folder), and is in valid format: " + e.getMessage());
        	return;
        }

        try {
            AmazonSQS sqs = new AmazonSQSClient(credentials);
            String myQueueUrl = this.getInitParameter("SQSDetailsEndPoint");
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
            receiveMessageRequest.setMaxNumberOfMessages(10);
            List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
            String output = "";
            for (Message message : messages) {
                output += message.getMessageId() + “;”;
                output += message.getBody() + “;”;
            }
            response.getWriter().println(output);
        }
        catch(Exception ex){
        	response.getWriter().println(ex.getMessage());
        }	
     }

}
