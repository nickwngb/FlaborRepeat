<?php


$response = array();
// post field
if (isset($_POST['phone'])){
	
	$phone = $_POST['phone'];
		
	// include db connect class
	require_once __DIR__ . '/db_connect.php'; 
		
	// connecting to db
	$db = new DB_CONNECT();
		
	$result = mysql_query("SELECT CustomerNo,FLaborNo,CellPhone FROM gbdormitory.ResidentManage where CellPhone = '$phone';") or die(mysql_error());
		
	if (mysql_num_rows($result) > 0) {
			
		$response["finfo"] = array();
 
		while ($row = mysql_fetch_array($result)) {
				
			$record = array();
			$record["CustomerNo"] = $row["CustomerNo"];
			$record["FLaborNo"] = $row["FLaborNo"];
			$record["CellPhone"] = $row["CellPhone"];
	
			array_push($response["finfo"], $record);
		}		
		$response["success"] = 1;					
		echo json_encode($response);		
	}else{		
		$response["success"] = 0; 
		echo json_encode($response);
	}	        
}	

?>