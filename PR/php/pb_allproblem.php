<?php


$response = array();
// post field
if (isset($_POST['CustomerNo'])&&isset($_POST['FLaborNo'])){
	
	$cNo = $_POST['CustomerNo'];
	$fNo = $_POST['FLaborNo'];
		
	// include db connect class
	require_once __DIR__ . '/db_connect.php'; 
		
	// connecting to db
	$db = new DB_CONNECT();
	$result = mysql_query("SELECT * FROM gbdormitory.APP_ProblemRecord where CustomerNo = '$cNo' and FLaborNo = '$fNo';") or die(mysql_error());
		
	if (mysql_num_rows($result) > 0) {
			
		$response["fproblems"] = array();
 
		while ($row = mysql_fetch_array($result)) {

			$record = array();
			$record["PRSNo"] = $row["PRSNo"];
			$record["CustomerNo"] = $row["CustomerNo"];
			$record["FLaborNo"] = $row["FLaborNo"];
			$record["ProblemDescription"] = $row["ProblemDescription"];
			$record["CreateProblemDate"] = $row["CreateProblemDate"];
			$record["ResponseResult"] = $row["ResponseResult"];
			$record["ResponseDate"] = $row["ResponseDate"];
			$record["ResponseID"] = $row["ResponseID"];
			$record["ProblemStatus"] = $row["ProblemStatus"];
			$record["SatisfactionDegree"] = $row["SatisfactionDegree"];			
			array_push($response["fproblems"], $record);
		}		
		$response["success"] = 1;					
		echo json_encode($response);		
	}else{		
		$response["success"] = 0; 
		echo json_encode($response);
	}	        
}	
?>