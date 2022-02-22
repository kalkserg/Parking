package ua.utilix.Handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ua.utilix.bot.ChatBot;
import ua.utilix.bot.LocationContext;
import ua.utilix.bot.LocationState;
import ua.utilix.model.Device;
import ua.utilix.model.Location;
import ua.utilix.model.User;
import ua.utilix.service.DeviceService;
import ua.utilix.service.LocationService;
import ua.utilix.service.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class CallbackQueryHandler implements Handler<CallbackQuery> {
    @Autowired
    UserService userService;
    @Autowired
    DeviceService deviceService;
    @Autowired
    LocationService locationService;

    @Override
    public void choose(ChatBot chatBot, CallbackQuery callbackQuery) {
//        System.out.println("------------------------------------------------------------ ");
//        System.out.println("callbackQuery getid " + callbackQuery.getId());
//        System.out.println("callbackQuery getData " + callbackQuery.getData());
//        System.out.println("callbackQuery getChatInstance " + callbackQuery.getChatInstance());
//        System.out.println("callbackQuery getInlineMessageId " + callbackQuery.getInlineMessageId());
//        System.out.println("callbackQuery getMessage " + callbackQuery.getMessage().getMessageId());
//        System.out.println("------------------------------------------------------------ ");
        Device device = null;
        Location location = null;
        LocationState locstate = null;
        LocationContext loccontext = null;

        final long chatId = callbackQuery.getMessage().getChatId();
        User user = userService.findByChatId(chatId);

        //ADMIN
        if (user.getAdmin()) {

            if (callbackQuery.getData().equals(CALLBACK_ADD_REQUEST)) {
                locstate = LocationState.StartRegLocation;
                location = new Location(locstate.ordinal());
                locationService.addLocation(location);

                loccontext = LocationContext.of(this, chatBot, user, location, "text");
                locstate.enter(loccontext, chatId, this);

                locstate = locstate.nextState();
                locstate.enter(loccontext, chatId, this);
                location.setStateId(locstate.ordinal());
                locationService.updateLocation(location);

            } else if (callbackQuery.getData().equals(CALLBACK_DEL_REQUEST)) {
                Integer messageId = callbackQuery.getMessage().getMessageId();
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
                editMessageText.setMessageId(messageId);
                editMessageText.setText(" ");
                System.out.println(callbackQuery.getMessage().getText());
                sendEditMessage(chatBot, chatId, editMessageText, null, null);
                ArrayList<String> list;
                list = getListLocations(locationService);
                sendMessage(chatBot, chatId, "Натиснуть для видалення", getMainMenu(), getInlineListMenu(list, CALLBACK_DEL_REQUEST));
            } else if (callbackQuery.getData().contains(CALLBACK_DEL_REQUEST)) {
                String[] subStr = callbackQuery.getData().split(delimeter);
                long Id;
                Id = Long.parseLong(subStr[1]);
                location = locationService.findById(Id);
                if (location != null) {
                    Device[] devices = deviceService.findBySigfoxId(location.getSigfoxId());
                    for (Device dev : devices) {
                        if (dev.getChatId() != chatId) {
                            sendMessage(chatBot, dev.getChatId(), "Видалено адміністратором" + " sigfoxId: " + location.getSigfoxId(), null, null);
                        }
                        deviceService.delDevice(dev);
                    }
                    sendMessage(chatBot, chatId, "Видалено " + " sigfoxId: " + location.getSigfoxId(), null, null);
                    locationService.delLocation(location);
                    location = null;
                    device = null;
                } else {
                    sendMessage(chatBot, chatId, "Видалення неможливе!", null, null);
                }

            } else if (callbackQuery.getData().equals(CALLBACK_LOGOUT_REQUEST)) {
                user.setAdmin(false);
                sendMessage(chatBot, user.getChatId(), "Бувай, адміністратор!", getMainMenu(), null);
                userService.updateUser(user);
            }

            //NOADMIN
        } else {
            System.out.println(" callbackQuery.getData()  " + callbackQuery.getData());
            if (callbackQuery.getData().equals(CALLBACK_FOLLOW_REQUEST)) {
                Integer messageId = callbackQuery.getMessage().getMessageId();
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
                editMessageText.setMessageId(messageId);
                editMessageText.setText(" ");
                sendEditMessage(chatBot, chatId, editMessageText, null, null);
                ArrayList<String> listLocationNoFollow;
                ArrayList<String> listDevicesSigfoxId;
                listDevicesSigfoxId = getListDevicesSigfoxId(chatId, deviceService);
                listLocationNoFollow = getListLocationsNoFollow(locationService, listDevicesSigfoxId);
                if (listLocationNoFollow.size() > 0)
                    sendMessage(chatBot, chatId, "Виберіть для слідкування", getMainMenu(), getInlineListMenu(listLocationNoFollow, CALLBACK_FOLLOW_REQUEST));
                else sendMessage(chatBot, chatId, "Вже вибрані всі", getMainMenu(), null);
            } else if (callbackQuery.getData().contains(CALLBACK_FOLLOW_REQUEST)) {
                String[] subStr = callbackQuery.getData().split(delimeter);
                long Id = Long.parseLong(subStr[1]);
                device = new Device();
                location = locationService.findById(Id);
                device.setSigfoxId(location.getSigfoxId());
                device.setChatId(chatId);
                device.setSigfoxName(location.getAddr());
                sendMessage(chatBot, chatId, "Виконано " + device.getSigfoxName() + " sigfoxId: " + device.getSigfoxId(), null, null);
                deviceService.updateDevice(device);

            } else if (callbackQuery.getData().equals(CALLBACK_NOFOL_REQUEST)) {
                Integer messageId = callbackQuery.getMessage().getMessageId();
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
                editMessageText.setMessageId(messageId);
                editMessageText.setText(" ");
                System.out.println(callbackQuery.getMessage().getText());
                sendEditMessage(chatBot, chatId, editMessageText, null, null);
                ArrayList<String> listLocationFollow;
                listLocationFollow = getListDevices(chatId, deviceService);
                sendMessage(chatBot, chatId, "Припинити слідкування за", getMainMenu(), getInlineListMenu(listLocationFollow, CALLBACK_NOFOL_REQUEST));
            } else if (callbackQuery.getData().contains(CALLBACK_NOFOL_REQUEST)) {
                String[] subStr = callbackQuery.getData().split(delimeter);
                long Id = Long.parseLong(subStr[1]);
                System.out.println(" id " + Id);
                device = deviceService.findById(Id);
                if (device != null) {
                    sendMessage(chatBot, chatId, "Припинено " + device.getSigfoxName() + " sigfoxId: " + device.getSigfoxId(), null, null);
                    deviceService.delDevice(device);
                    device = null;
                } else {
                    sendMessage(chatBot, chatId, "Видалення неможливе!", null, null);
                }

            } else if (callbackQuery.getData().equals(CALLBACK_MENU_REQUEST)) {
                sendMessage(chatBot, chatId, "Меню:", getMainMenu(), getInlineSubMenu());
            }
        }
    }

//    private void printed(String[] str) {
//        for(int i=0;i<str.length; i++) {
//            System.out.println(i + " " + str[i]);
//        }
//    }

    private ArrayList<String> getListDevices(long chatId, DeviceService deviceService) {

        List<Device> devices = Arrays.asList(deviceService.findByChatId(chatId));
        ArrayList<String> list = new ArrayList<>();

        devices.forEach(device ->
                list.add(device.getId().toString() + " - " + device.getSigfoxName() + " (sigfoxId: " + device.getSigfoxId() + ")")
        );
        return list;
    }

    private ArrayList<String> getListDevicesSigfoxId(long chatId, DeviceService deviceService) {

        List<Device> devices = Arrays.asList(deviceService.findByChatId(chatId));
        ArrayList<String> list = new ArrayList<>();

        devices.forEach(device ->
                list.add(device.getSigfoxId())
        );
        return list;
    }

    private ArrayList<String> getListLocationsNoFollow(LocationService locationService, List<String> ids) {
        List<Location> locations = locationService.findAllLocations();
        ArrayList<String> list = new ArrayList<>();
        for (Location location : locations) {
            String locId = String.format("%8.8s", location.getSigfoxId()).replace(' ', '0');
            int count = 0;
            for (String id : ids) {
                String devId = String.format("%8.8s", id).replace(' ', '0');
                if (locId.equals(devId)) {
                    count++;
                }
            }
            if (count == 0) {
                list.add(location.getId().toString() + " - " +
                        " sigfoxId: " + locId +
                        " address: " + location.getAddr());
            }
        }
        return list;
    }

    private ArrayList<String> getListAdminDevices(ChatBot chatBot, DeviceService deviceService) {

        List<Device> devices = deviceService.findAllDevices();
        ArrayList<String> list = new ArrayList<>();

        devices.forEach(device ->
                list.add(device.getId().toString() + " - " + device.getSigfoxName() +
                        " (sigfoxId: " + String.format("%8.8s", device.getSigfoxId()).replace(' ', '0') +
                        " chatId: " + device.getChatId() +
                        " user: " + userService.findByChatId(device.getChatId()).getUserName() + ")")
        );
        return list;
    }

    private ArrayList<String> getListLocations(LocationService locationService) {

        List<Location> locations = locationService.findAllLocations();
        ArrayList<String> list = new ArrayList<>();

        locations.forEach(location ->
                list.add(location.getId().toString() + " - " +
                        " sigfoxId: " + String.format("%8.8s", location.getSigfoxId()).replace(' ', '0') +
                        " address: " + location.getAddr())
        );
        return list;
    }

    private ArrayList<String> getListAdminNoValidDevices(ChatBot chatBot, DeviceService deviceService) {

        List<Device> devices = deviceService.findAllDevices();
        ArrayList<String> list = new ArrayList<>();

        devices.forEach(device ->
                list.add(device.getId().toString() + " - " + device.getSigfoxName() +
                        " sigfoxId: " + String.format("%8.8s", device.getSigfoxId()).replace(' ', '0') +
                        " chatId: " + device.getChatId() +
                        " user: " + userService.findByChatId(device.getChatId()).getUserName() + ")")
        );
        return list;
    }

}
