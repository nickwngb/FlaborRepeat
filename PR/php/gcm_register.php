<?php

	if (isset($_POST['RegId']) && isset($_POST['CustomerNo']) && isset($_POST['FLaborNo'])){
		$cNo = $_POST['CustomerNo'];
		$fNo = $_POST['FLaborNo'];
		$RegId = $_POST['RegId'];	
		// include db connect class
		require_once __DIR__ . '/db_connect.php'; 		
		// connecting to db
		$db = new DB_CONNECT();
		$result = mysql_query("INSERT INTO gbdormitory.APP_forGCM (CustomerNo,FLaborNo,GCMid) 
		VALUES ('$cNo','$fNo','$RegId') 
		ON DUPLICATE KEY UPDATE GCMid = VALUES(GCMid)") or die(mysql_error());
		if ($result) {
			$response["success"] = 1;	
			echo json_encode($response);
		} else {
			$response["success"] = 0;
			echo json_encode($response);
		}
	}

?>