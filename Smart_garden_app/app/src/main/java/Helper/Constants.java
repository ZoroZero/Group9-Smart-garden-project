package Helper;

public class Constants {
    private static final String ROOT_URL = "//Android/v1";

    private static final String MQTT_URL = "//Android/MQTT";

    public static final String DATABASE_IP = "192.168.1.119";

    public static final String DATABASE_PORT = ":3306";
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
    public static String RECORD_URL = MQTT_URL + "/subscribeMQTT1.php";

    // Update output status record
    public static String UPDATE_OUTPUT_STATUS_URL = ROOT_URL + "/updateOutputStatus.php";

    // Get device last measurement
    public static final String GET_MEASUREMENT = ROOT_URL + "/getDeviceMeasurement.php";

    // Get output status
    public static final String GET_STATUS = ROOT_URL + "/getOutputInfo.php";

    // Change device threshold
    public static final String CHANGE_THRESHOLD = ROOT_URL + "/changeDeviceThreshold.php";

    // Delete plant
    public static final String REMOVE_PLANT = ROOT_URL + "/removePlant.php";

    // Get device by type
    public static final String GET_DEVICE_BY_TYPE = ROOT_URL + "/getInputDevicesWithType.php";

    // Get value by month
    public static final String GET_VALUE_BY_MONTH = ROOT_URL + "/getValueThisMonth.php";

    // Get value by month
    public static final String GET_VALUE_BY_YEAR = ROOT_URL + "/getValueThisYear.php";

    // Get value by month
    public static final String GET_VALUE_BY_DAY = ROOT_URL + "/getValueToday.php";

    // Send data to AI
    public static final String SEND_DATA_TO_AI = "/api/post_some_data";

    // Device id identifier
    public static String[] OUTPUT_ID = {"LightD", "Speaker"};

    public static String[] LIGHT_SENSOR_ID = {"Light"};

    public static String[] TEMPHUMI_SENSOR_ID = {"TempHumi"};

    public static String[] DEVICE_TYPE = {"sensor", "output"};

    public static final String LIGHT_SENSOR_TYPE = "Light sensor";

    public static final String TEMPHUMI_SENSOR_TYPE = "Temperature humidity sensor";

    public static final String OUTPUT_TYPE = "Output";


    // Default value for register device
    public static int DEFAULT_LIGHT = 20;

    public static int DEFAULT_TEMP = 30;

    public static int DEFAULT_HUMID = 80;

    public static int MAX_LIGHT = 500;

    public static int MAX_TEMP = 50;

    public static int MAX_HUMID = 100;

    // Notification
    public static String CHANNEL_ID = "smart_garden";

    public static String CHANNEL_Name = "Smart garden";

    public static String CHANNEL_DESC = "Smart garden notification";
}