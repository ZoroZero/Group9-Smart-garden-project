<?php
require_once "../includes/DbOperation.php";

$response = array();
if($_SERVER['REQUEST_METHOD'] == 'POST'){
    if(isset($_POST['input_id']) and isset($_POST['new_input_name']) and isset($_POST['new_output_id'])
    and isset($_POST['new_output_name']) and isset($_POST['new_threshold'])){
        // operate data
        $db = new DbOperator();
        $db->__contruct();
        $result = $db->changeDeviceProperties($_POST['input_id'], $_POST['new_input_name'], $_POST['new_output_id'],
         $_POST['new_output_name'], $_POST['new_threshold']);
        if($result){
            $response['error'] = false;
            $response['message'] = 'Successfully change device properties';
        }
        else{
            $response['error'] = false;
            $response['message'] = 'Unable to change device properties';
        }
    }
    else{
        $response['error'] = true;
        $response['message'] = 'Required field are missing';
    }
}
else {
    $response['error'] = true;
    $response['message'] = 'Invalid Request';
}

echo json_encode($response);