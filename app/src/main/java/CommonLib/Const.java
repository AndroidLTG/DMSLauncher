package CommonLib;

/**
 * Created by My PC on 01/12/2015.
 */
public abstract class Const {
    public static final int QueueTimerView = 100; // thời gian sleep giữa các lần xử lý message ở View
    public static final int QueueTimerControl = 100; // thời gian sleep giữa các lần xử lý message ở Controller
    public static final int DefaultAlarmIntervalInSeconds = 5 * 60; // chu kì cập nhật location
    public static final int DefaultHighPrecisionIntervalInSeconds = 30;
    public static final float BoostedSpeedMPS = 1.0f;
    public static final float DroppedSpeedMPS = 30.0f;
    public static final int MaxRecordsLastSend = 300; // số bản ghi cuối cùng tối đa gửi lên server mỗi lần đọc csdl
    public static final String HttpEndpoint = "http://indico.vn:8103/HttpService.aspx"; // địa chỉ web service
    //public static final String HttpEndpoint = "http://www.myca.vn:88/HttpService.aspx";
    public enum ConfigKeys { //
        DeviceID,
        LoginToken,
        Username,
        Fullname,
        Kickout,
        InitTrackingRowID,
        ServerTime,
        AlarmIntervalNormal,
        AlarmIntervalBoosted,
        End
    }
}
