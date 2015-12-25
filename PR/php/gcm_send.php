<?php

	if (isset($_POST['CustomerNo']) && isset($_POST['FLaborNo']) && isset($_POST['message'])){
		$cNo = $_POST['CustomerNo'];
		$fNo = $_POST['FLaborNo'];
		$message = $_POST['message'];
		
		$apiKey = "AIzaSyCPxZWJhtJidDCj_qOsnpKZmO3ULpwtQkE";
		// include db connect class
		require_once __DIR__ . '/db_connect.php'; 		
		// connecting to db
		$db = new DB_CONNECT();
		$rs = mysql_query("SELECT GCMid FROM gbdormitory.APP_forGCM where CustomerNo ='$cNo' and FLaborNo = '$fNo';") or die(mysql_error());
		$aRegID = array();
		if($row = mysql_fetch_array($rs)){
			array_push($aRegID, $row["GCMid"]);
		}
		
	
		// Set POST variables
		$url = 'https://android.googleapis.com/gcm/send';
	
		// send content
		// ex :  title, message
		$fields = array('registration_ids'  => $aRegID,
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
								WHERE GCMid='".$aRegID[$i]."' ";
				$deleters = mysql_query($sqlTodel);
			}
		} 
		*/
		// Close connection
		curl_close($ch);
		// GCM end -------
		unset($aRegID);	
		
		} catch(Exception $e) {
		trigger_error(sprintf('Curl failed with error #%d: %s',
		$e->getCode(), $e->getMessage()),E_USER_ERROR);
		}
	}
?>