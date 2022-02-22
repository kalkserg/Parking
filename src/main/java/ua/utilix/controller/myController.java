package ua.utilix.controller;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.utilix.model.*;
import ua.utilix.service.DeviceService;
import ua.utilix.service.LocationService;
import ua.utilix.service.SendMessageService;
import ua.utilix.service.UserService;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Controller
public class myController {

    String messageIn = "";
    String messageOut = "";
    private UserService userService;
    private DeviceService deviceService;
    private LocationService locationService;
    private SendMessageService sendMessageService;

    @Autowired
    public void setSendMessageService(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setDeviceService(LocationService locationService) {
        this.locationService = locationService;
    }

    @Autowired
    public void setLocationService(DeviceService deviceService) {
        this.deviceService = deviceService;
    }


    @PostMapping(produces = "application/json")
    public ResponseEntity<String> postBody(@RequestBody(required = false) String str, Model model) {
        System.out.println("post  " + str);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        JSONObject obj = new JSONObject(str);

        String sigfoxId = obj.getString("device");
        String unixTime = obj.getString("time");
        String data = obj.getString("data");
        int seqNumber = obj.getInt("seqNumber");

        SigfoxData sigfoxData = null;
        SigfoxParser sigfoxParser = new SigfoxParser();

        messageIn = messageIn + str;

        Device[] devices = null;

        try {
            devices = deviceService.findBySigfoxId(sigfoxId);
        } catch (Exception e) {
            System.out.println("There is not user");
        }

        Location location = null;

        try {
            location = locationService.findBySigfoxId(sigfoxId);
        } catch (Exception e) {
            System.out.println("There is not location");
        }

//        String prtcl = locationService.findBySigfoxId(device.getSigfoxId()).getProtocol();
        String prtcl = location.getProtocol();
        try {
            if (prtcl.equals("ParkingSensor")) {
                sigfoxData = sigfoxParser.getData(sigfoxId, data, prtcl, seqNumber);
                location.setPark(sigfoxData.isPark());
                location.setBattery(sigfoxData.getBattery());
                location.setTemperature(sigfoxData.getTemperature());
                location.setCarCount(sigfoxData.getCarCount());
                location.setErrorMagnet(sigfoxData.getErrorMagnet());
                location.setErrorBatteryAlarm(sigfoxData.getErrorBatteryAlarm());
                location.setErrorDamage(sigfoxData.getErrorDamage());
                location.setErrorUnknown(sigfoxData.getErrorUnknown());
                locationService.updateLocation(location);
            } else {
                //sigfoxData = sigfoxParser.getData(sigfoxId, data, prtcl, seqNumber);
            }
        } catch (Exception ex) {

        }


        try {
            for (Device device : devices) {
                String sigfoxName = device.getSigfoxName();
                Long chatId = device.getChatId();
//                final String formattedDtm = Instant.ofEpochSecond(Long.parseLong(unixTime))
//                        .atZone(ZoneId.of("GMT+3"))
//                        .format(formatter);


                if (true) {
                    Date now = new Date();
                    Long tokennow = new Long(now.getTime() / 1000);
                    Long tokenend = tokennow;

                    String user = "6107a6e641758161bc41ddd0";
                    String pwd = "abea5dd6ead1c6e14b6ada4a02bd9e2e";
                    Double latitude = 0.;
                    Double longitude = 0.;

                    CredentialsProvider provider = new BasicCredentialsProvider();
                    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(user, pwd);
                    provider.setCredentials(AuthScope.ANY, credentials);
                    HttpClient client = HttpClientBuilder.create()
                            .setDefaultCredentialsProvider(provider)
                            .build();
                    HttpResponse response = client.execute(new HttpGet("https://api.sigfox.com/v2/devices/" + sigfoxId));
                    //HttpResponse response = client.execute(new HttpGet("https://api.sigfox.com/v2/devices/32FFCE"));
                    int statusCode = response.getStatusLine().getStatusCode();
                    System.out.println(statusCode);
                    System.out.println(statusCode);
                    if (statusCode == 200) {
                        // CONVERT RESPONSE TO STRING
                        String result = EntityUtils.toString(response.getEntity());
                        System.out.println(result);
                        JSONObject objResponse = new JSONObject(result);
                        try {
                            latitude = objResponse.getJSONObject("location").getDouble("lat");
                            longitude = objResponse.getJSONObject("location").getDouble("lng");
                            tokenend = objResponse.getJSONObject("token").getLong("end");
                        } catch (Exception ex) {
                            //do nothing
                        }
                    }
                    Long leftToken = tokenend - tokennow;
                    String warningToken = "";
                    if (leftToken > 0) {
                        Date date = new Date(leftToken);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                        sdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
                        String formattedDate = sdf.format(date);
                        warningToken = leftToken < 2592000 ? "\n<b>Увага! Реєстрація закінчиться " + formattedDate + "</b>" : "";
//                        warningToken = "\n\n<b>Увага! Реєстрація закінчиться " + formattedDate + "</b>";
                    }
                    System.out.println(latitude + "   " + longitude);

                    String coordinateHref = latitude != 0 && longitude != 0 ? "\n\n<a href=\"https://www.google.com/maps/place/" + latitude + "," + longitude + "\"><b>Показати положення на мапі </b></a>" : "";

                    //Err
                    if (sigfoxData.getErrorMagnet().equals(Sigfox.TypeError.MAG) ||
                            sigfoxData.getErrorDamage().equals(Sigfox.TypeError.PHOTO) ||
                            sigfoxData.getErrorBatteryAlarm().equals(Sigfox.TypeError.BATT) ||
                            sigfoxData.getErrorUnknown().equals(Sigfox.TypeError.WDOG)
                    ) {
                        String message = "\u26A0" + "<b>" + sigfoxName + "</b>: " + sigfoxData.toString() + coordinateHref + warningToken;
                        sendMessageService.sending(message, device.getChatId());
                    } else {
                        String message = "<b>" + sigfoxName + "</b>: " + sigfoxData.toString();
                        sendMessageService.sending(message, device.getChatId());
                    }

                }
            }
        } catch (Exception e) {
            System.out.println("Error get data!");
        }

        return new ResponseEntity<String>(HttpStatus.OK);
    }


//    @GetMapping()
//    public String getBody(@RequestBody(required = false) String str, Model model) {
//        System.out.println("GET " + messageIn);
//        System.out.println("GET " + messageOut);
//        model.addAttribute("str", messageIn + messageOut);
//        return "byby";
//    }

    //    @ResponseBody
    @GetMapping(path = "/api/users")
    @ResponseBody
    public List<User> listUsers() {
//        System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
//        System.out.println(userService.findAllUsers());
        return userService.findAllUsers();
    }

    @GetMapping(path = "/api/byby")
    public String listUsers2(
            @RequestParam(name = "name", required = false, defaultValue = "нету юзеров") String name, Model model
    ) {
        System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC");
        name = userService.findAllUsers().toString();
        model.addAttribute("name", name);// = userService.findAllUsers().toString();
        return "byby";
    }


    @GetMapping
    public List<User> listUsers1() {
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        return userService.findAllUsers();
    }
}

//    @GetMapping(path = "/api/users")
//    public ResponseEntity<?> listUsers() {
////        log.info("UsersController:  list users");
//        List<User> resource = userService.findAllUsers();
//        return ResponseEntity.ok(resource);
//    }

//    @PostMapping(path = UserLinks.ADD_.saveUser(user);
////        return ResponseEntity.ok(resource);
////    }
//
////
////    private byte[] intToByte(int num) {
////        byte[] bytes = new byte[4];
////        String str = String.valueOf(num);
////        for (int i = 0; i < 4; i++) {
////            bytes[i] = (byte) str.charAt(i);
////            //bytes[i] = (byte)(num >>> (i * 8));
////            System.out.println(num + " " + bytes[i]);
////        }
////        return bytes;
////    }
//}USER)
//    public ResponseEntity<?> saveUser(@RequestBody Users user) {
//        log.info("UsersController:  list users");
//        Users resource = usersService
