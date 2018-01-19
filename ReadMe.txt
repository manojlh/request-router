======================================================================================================================================
Application Name 	: 	HTTPRequestRouter
Description 		: 	This application will use to send the callback from Gateway to Hub.
Method 				: 	POST
Request URL 		: 	http://localhost:8080/HTTPRequestRouter/route/<KEY>/?org=<ENCODED URL>
======================================================================================================================================

Instructions for configure the HTTPRequestRouter application

1.	Create a database on mysql
		Eg : requestrouter

2.	Excute the database.sql in dbscripts folder on that created database

3.	Add records to the headers table

		domain : 	domain or ip 
				Eg :	abc.lk or 10.2.3.4

		urlPrefix :	start part of the url 
				Eg :	http://abc.lk/api/

		header :	header key
				Eg :	Authorization

		headerValue :	value of the header
				Eg :	Bearer dfrd3343dff

		mode : this value indicate how to add the header
				Eg :	REPLACE - if the request contains the same header, requestrouter will replace the that header from new header (RECOMMENDED)
						ADD - Add the new header 
						APPEND - if the request contains the same header, requestrouter append the new header value to the header

4. 	Add records to the replacebody table (Using values in this table we can replace any json value in body. if there is no any value to replace you don't need to add records to this table)

		urlKey :	<KEY> value in the request url
				Eg :	MIFE-HUB-USSD
		jsonPath :	json path of the request body
				Eg : 	$.inboundUSSDMessageRequest.responseRequest.notifyURL
		find : 		value to find in request body 
				Eg : * - replace everything				     
		replace :	replace value 
		needURLDecodeRest : this will use the url decode the url in request body
				Eg : 0 -  nothing will url decode
					 1 -  url in the request body will url decode

5.	Change the properties in conf.properties file in the following path according the mysql server 
		File path - HTTPRequestRouter\src\main\resources

====================================================================================================================================
Instructions for integrate HTTPRequestRouter with Gateway

1. Configure the requestRouterUrl property in AxiataMediator-1.0.0.jar as following

	requestRouterUrl=http://<HOST>:<PORT>/HTTPRequestRouter/route/<KEY>/?org=
		Eg : requestRouterUrl=http://localhost:8080/HTTPRequestRouter/route/MIFE-HUB-USSD/?org=

=====================================================================================================================================