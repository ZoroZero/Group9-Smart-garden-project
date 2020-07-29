<?php
require_once "../includes/DbOperation.php";

$response = array();
if($_SERVER['REQUEST_METHOD'] == 'POST'){
    if(isset($_POST['type'])){
        // operate data
        $db = new DbOperator();
        $db->__contruct();
        $result = $db->getDeviceByType($_POST['type']);
        $response['reading'] = $result;

    }
    else{
        $response['error'] = true;
        $response['duy'] = false;
        $response['message'] = 'Required field are missing';
    }
}
else {

    $response['error'] = true;
    $response['message'] = 'Invalid Request';
}

echo json_encode($response);