<?php

 
	if (isset($_POST['CustomerNo']) && isset($_POST['FLaborNo']) && isset($_POST['ProblemDescription']) && isset($_POST['CreateProblemDate']) && isset($_POST['ProblemStatus'])){
		$cNo = $_POST['CustomerNo'];
		$fNo = $_POST['FLaborNo'];
		$pDescription = $_POST['ProblemDescription'];
		$pDate = $_POST['CreateProblemDate'];
		$pStatus = $_POST['ProblemStatus'];
		// include db connect class
		require_once __DIR__ . '/db_connect.php'; 		
		// connecting to db
		$db = new DB_CONNECT();
		$result = mysql_query("INSERT INTO gbdormitory.APP_ProblemRecord (`CustomerNo`,`FLaborNo`,`ProblemDescription`,`CreateProblemDate`,`ProblemStatus`) values ('$cNo','$fNo','$pDescription','$pDate','$pStatus');") or die(mysql_error());
		
		if ($result) {
			$response["success"] = 1;	
			echo json_encode($response);
		} else {
			$response["success"] = 0;
			echo json_encode($response);
		}        
	}


?>