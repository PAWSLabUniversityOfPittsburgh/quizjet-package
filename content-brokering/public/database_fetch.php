<?php
header('Access-Control-Allow-Origin: *');

$username = "";
$password = "";
$hostname = "localhost";

//connection to the database
$dbhandle = mysql_connect($hostname, $username, $password)
 or die("Unable to connect to MySQL");
//echo "Connected to MySQL<br>";

//select a database to work with
$selected = mysql_select_db("aggregate",$dbhandle)
  or die("Could not select examples");

//execute the SQL query and return records
$result = mysql_query("SELECT aggregate.ent_topic.display_name AS topic_name,aggregate.rel_topic_content.display_name AS rdf_id,webex21.ent_dissection.Name AS ex_name,webex21.ent_line.LineIndex AS line_number,webex21.ent_line.Code as code,webex21.ent_line.Comment AS comment
                           FROM aggregate.ent_topic,aggregate.rel_topic_content,aggregate.rel_resource_provider,webex21.ent_dissection,webex21.ent_line
                           WHERE aggregate.ent_topic.topic_id = aggregate.rel_topic_content.topic_id
                           AND aggregate.rel_topic_content.resource_id = aggregate.rel_resource_provider.resource_id
                           AND aggregate.rel_resource_provider.provider_id = 'webex'
                           AND aggregate.rel_topic_content.display_name = webex21.ent_dissection.rdfID
                           AND webex21.ent_dissection.DissectionID = webex21.ent_line.DissectionID");

//fetch tha data from the database
while ($row = mysql_fetch_array($result,MYSQL_ASSOC)) {
   $to_encode[] = $row;
}
$main = json_encode($to_encode);
echo($main);

//close the connection
mysql_close($dbhandle);


?>