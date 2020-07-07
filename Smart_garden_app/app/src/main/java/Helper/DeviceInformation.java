package Helper;

public class DeviceInformation {
    private String device_id;
    private String device_name;
    private String linked_device_id;
    private String linked_device_name;
    private String device_type;
    private String threshold;
    private String status;
    private String statusDate;

    public DeviceInformation(String device_id, String device_name, String linked_device_id,
                             String linked_device_name, String device_type, String threshold,
                             String status, String statusDate){
        this.device_id = device_id;
        this.device_name = device_name;
        this.linked_device_id = linked_device_id;
        this.linked_device_name = linked_device_name;
        this.device_type = device_type;
        this.threshold = threshold;
        this.status = status;
        this.statusDate = statusDate;
    }

    public String toString(){
        return "{\"device_id\": \"" + device_id +
                "\", \"device_name\": \"" + device_name +
                "\", \"linked_device_id\": \"" + linked_device_id +
                "\", \"linked_device_name\": \"" + linked_device_name +
                "\", \"device_type\": \"" + device_type + "\"}";
    }

    public String getDevice_id(){
        return this.device_id;
    }

    public String getDevice_name(){
        return this.device_name;
    }

    public String getLinked_device_id(){
        return this.linked_device_id;
    }

    public String getLinked_device_name(){
        return this.linked_device_name;
    }

    public String getDevice_type(){
        return this.device_type;
    }

    public String getThreshold(){
        return this.threshold;
    }

    public String getStatus(){
        return this.status;
    }

    public String getStatusDate(){
        return this.statusDate;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public void setStatusDate(String statusDate){
        this.statusDate = statusDate;
    }

}
