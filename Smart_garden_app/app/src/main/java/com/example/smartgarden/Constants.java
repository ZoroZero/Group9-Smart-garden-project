package com.example.smartgarden;

public class Constants {
    private static final String ROOT_URL = "//Android/v1";

    private static final String MQTT_URL = "//Android/MQTT";
    // Register url
    public static final String REGISTER_URL = ROOT_URL + "/registerUser.php";

    // Login url
    public static String LOGIN_URL = ROOT_URL + "/loginUser.php";

    // Get device list
    public static String FETCH_URL = ROOT_URL + "/fetchDeviceInfo.php";

    // Insert new device
    public static String REGISTER_DEVICE_URL = ROOT_URL + "/registerDevice.php";

    // Add new plant
    public static String ADD_PLANT_URL = ROOT_URL + "/addPlant.php";

    // Get plant list
    public static String FETCH_PLANT_URL = ROOT_URL + "/fetchPlantInfo.php";

    // Automatically record measurement
    public static String RECORD_URL = MQTT_URL + "/subscribeMQTT.php";

    // Update output status record
    public static String UPDATE_OUTPUT_STATUS_URL = ROOT_URL + "/updateOutputStatus.php";

    // Get device last measurement
    public static final String GET_MEASUREMENT = ROOT_URL + "/getDeviceMeasurement.php";

    // Get output status
    public static final String GET_STATUS = ROOT_URL + "/getOutputInfo.php";

    // Device id identifier
    public static String[] OUTPUT_ID = {"LightD", "Speaker"};

    public static String LIGHT_SENSOR_ID = "Light";

    public static String TEMPHUMI_SENSOR_ID = "TempHumi";


    // Default value for register device
    public static int DEFAULT_LIGHT = 200;

    public static int DEFAULT_TEMP = 30;

    public static int DEFAULT_HUMID = 80;


    // Notification
    public static String CHANNEL_ID = "smart_garden";

    public static String CHANNEL_Name = "Smart garden";

    public static String CHANNEL_DESC = "Smart garden notification";
}
