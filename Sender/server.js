var http = require("http");
var fs = require("fs");
var AWS = require('aws-sdk');

var server = http.createServer(function(req,res){
	var url = req.url;
	if(url.match(/whoami/)){
		fs.readFile("whoami.txt",function(err,data){
			if(err)
				res.end("Error loading my details: " + err.message)
			else{
				res.end(data);
			}	
		});	
	}
	else if(url.match(/send/)){
		var url = req.url;
		var temp = url.split("/send/");
		if(temp.length == 2){
			 AWS.config.loadFromPath('./aws.credentials.json');
			 var sqs = new AWS.SQS();  
			 fs.readFile("sqsendpointdetails.txt",function(err,data){
				 if(err){
					 res.end("Error loading sqsendpointdetails file\n" + err.message);
				 }
				 else{
	 			 	var queueUrl = data.toString();
	 				sqs.sendMessage(
						{
	 					 	MessageBody : "Hello " + temp[1],
	 						QueueUrl : queueUrl
	 					},
						function(err,result){
	 				    	if (err){ 
								// An ERROR OCCURED
	 							res.end("ERROR: " + err.message);
	 				    	} 
	 				     	else{
	 						 // successful response
	 				     	 res.end("Message sent successfully \nMessageId : " + result.MessageId);   
	 				     	}     
					
	 					});
	 			 }
		   });
		}
		else
			res.end("Invalid URL " + url + "\nShould be of the pattern http://<server>/send/<name>")
	}
	else{
		var output = "Enter http://<server>/whoami for your details\n\n";
		output += "Enter http://<server>/send/<name> for sending message to SQS\n";
		res.end(output);
	}
});
server.listen(process.env.PORT || 3000);
console.log("Server running");