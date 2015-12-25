<?php

	if (isset($_POST['message'])){

		$message = $_POST['message'];
		
		$apiKey = "AIzaSyCPxZWJhtJidDCj_qOsnpKZmO3ULpwtQkE";
		// include db connect class
		require_once __DIR__ . '/db_connect.php'; 		
		// connecting to db
		$db = new DB_CONNECT();
		$result = mysql_query("SELECT GCMid FROM gbdormitory.APP_forGCM;") or die(mysql_error());
		$RegIds = array();
		while($row = mysql_fetch_array($result)){
			array_push($RegIds, $row["GCMid"]);
		}
		$allcount = count($RegIds);
		$sendMax = 1000;
		$bigcount = ceil($allcount/$sendMax);
		
		for($x = 0;$x<$bigcount;$x++){
			$batchRegId = array();
			for($y =0 ; $y < $sendMax;$y++){
				$index = ($x * $bigcount) + $y ;
				if($index < $allcount){
					array_push($batchRegId,$RegIds[$index]);
				}else{
					break;
				}
			}
			
		// Set POST variables
		$url = 'https://android.googleapis.com/gcm/send';
	
		// send content
		// ex :  title, message
		$fields = array('registration_ids'  => $batchRegId,
		'data'=> array( 'message' => $message )              
		);
		echo json_encode($fields);
		$headers = array('Content-Type: application/json',
						'Authorization: key='.$apiKey);
		try {
		// Open connection
		$ch = curl_init();
		// Set the url, number of POST vars, POST data
		curl_setopt($ch, CURLOPT_URL, $url );
		curl_setopt($ch, CURLOPT_POST, true );
		curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, true );
		curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
		curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
 
		// 送出 post, 並接收回應, 存入 $result
		$result = curl_exec($ch);
		if (FALSE === $result)
			throw new Exception(curl_error($ch), curl_errno($ch));
		
		echo json_encode($result);
		// 由回傳結果, 取得已解除安裝的 regID
		// 自資料庫中刪除
		$aGCMresult = json_decode($result,true);
		$aUnregID = $aGCMresult['results'];
		$unregcnt = count($aUnregID);
		/*
		for($i=0;$i<$unregcnt;$i++){
			$aErr = $aUnregID[$i];
			if($aErr['error']=='NotRegistered'){
				$sqlTodel = "DELETE FROM gbdormitory.APP_forGCM
								WHERE GCMid = '".$RegIds[$i]."' ";
				$deleters = mysql_query($sqlTodel);
			}
		} 
		*/
		// Close connection
		curl_close($ch);
		// GCM end -------
		unset($batchRegId);	
		
		} catch(Exception $e) {
		trigger_error(sprintf('Curl failed with error #%d: %s',
		$e->getCode(), $e->getMessage()),E_USER_ERROR);
		}
		}
	}
?>