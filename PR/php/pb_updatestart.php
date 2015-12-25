<?php

	if (isset($_POST['PRSNo']) && isset($_POST['Start'])){
		$PRSNo = $_POST['PRSNo'];
		$Start = $_POST['Start'];
		// include db connect class
		require_once __DIR__ . '/db_connect.php'; 		
		// connecting to db
		$db = new DB_CONNECT();
		$result = mysql_query("UPDATE gbdormitory.APP_ProblemRecord 
		SET SatisfactionDegree = '$Start' WHERE PRSNo = $PRSNo;") or die(mysql_error());		
		if ($result) {
			$response["success"] = 1;	
			echo json_encode($response);
		} else {
			$response["success"] = 0;
			echo json_encode($response);
		}        
	}
?>