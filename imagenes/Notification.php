<?php
class Notification{
    
	function sendNotificationByTopic($title, $message, $tokens){
		$path_to_firebase_cm = 'https://fcm.googleapis.com/fcm/send'; 
		
		$fields = array(
            'registration_ids' => $tokens,
            'notification' => array('title' => $title, 'body' => $message),
			'time_to_live' => 604800
        );

        $headers = array('Authorization:key=',
            'Content-Type:application/json'
        );
		
		$ch = curl_init();
		
		curl_setopt($ch, CURLOPT_URL, $path_to_firebase_cm);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        curl_setopt($ch, CURLOPT_IPRESOLVE, CURL_IPRESOLVE_V4 );
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
		
        $result = curl_exec($ch);

        if(!$result) {
          $response["success"]=100;
          $response["error"]='Error: "' . curl_error($ch) . '" - Code: ' . curl_errno($ch);
        } else {
          $response["success"]=3;
          $response["statusCode"]= curl_getinfo($ch, CURLINFO_HTTP_CODE);
          $response["message"]='Notificacion enviada correctamente.';
          $response["response HTTP Body"]= " - " .$result ." -";
        }

		curl_close($ch);

        return ($response);
	}
}
?>
