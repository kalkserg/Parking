package ua.utilix.Handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ua.utilix.bot.*;
import ua.utilix.model.Device;
import ua.utilix.model.Location;
import ua.utilix.model.User;
import ua.utilix.service.DeviceService;
import ua.utilix.service.LocationService;
import ua.utilix.service.UserService;

import java.util.Arrays;
import java.util.List;

@Component
public class MessageHandler implements Handler<Message> {

    @Autowired
    UserService userService;
    @Autowired
    DeviceService deviceService;
    @Autowired
    LocationService locationService;

    @Override
    public void choose(ChatBot chatBot, Message message) {
//        if (!update.hasMessage() || !update.getMessage().hasText())
//            return;

        final String text = message.getText();
        final long chatId = message.getChatId();

        User user = null;
        BotState state = null;
        BotContext context = null;

        Device device = null;
        Location location = null;
        LocationState locstate = null;
        LocationContext loccontext = null;

        user = userService.findByChatId(chatId);

//        System.out.println("----- " + message.getText());
//        System.out.println("-----user.getStateId() " + user.getStateId());


//        if (user != null && (user.getStateId() == BotState.Done.ordinal() || user.getStateId() == BotState.EndUserReg.ordinal())) {
        if (user != null && user.getStateId() == BotState.Done.ordinal()) {
            //if (userCommands(device, text)) return;
            if (checkCommand(chatBot, user, text)) return;

//LOCATION
            if(user.getAdmin()) {
                List locations = locationService.findAllLocations();
                try {
                    location = (Location) locations.get(locations.size() - 1);
                } catch (Exception e) {
                }

//                System.out.println("---- locaton addr " + location.getAddr());
                if (location == null) {
                    locstate = LocationState.getInitialState();
//                System.out.println("chatId " + chatId);
//                System.out.println("devstate.ordinal() " + devstate);
                    location = new Location(locstate.ordinal());
                    locationService.addLocation(location);

                    loccontext = LocationContext.of(this, chatBot, user, location, text);
                    locstate.enter(loccontext, chatId,this);
                    //LOGGER.info("New device registered: " + chatId);

                } else if (text.equals(ADD_REQUEST)) {
                    locstate = LocationState.StartRegLocation;

                    location = new Location(locstate.ordinal());
                    locationService.addLocation(location);

                    loccontext = LocationContext.of(this, chatBot, user, location, text);
                    locstate.enter(loccontext, chatId,this);
                    LOGGER.info("Add id registered: " + chatId);
                } else if (text.contains(REMOVE_REQUEST)) {
                    //devstate = DeviceState.BeginRemoving;

                    try {
                        long Id;
                        long Ids[];
                        String[] subStr;
                        text.trim();
                        subStr = text.split(delimeter); // Разделения строки str с помощью метода split()
                        System.out.println(subStr.length);
                        for (int i = 1; i < subStr.length; i++) {
                            Id = Long.parseLong(subStr[i]);
                            System.out.println(Id);
                            location = locationService.findById(Id);
                            sendMessage(chatBot, chatId, "Deleted " + Id, null, null);
                            locationService.delLocation(location);
                            location = null;
                        }
                    } catch (Exception ex) {
                        sendMessage(chatBot, chatId, "Try command:\ndel NumberId [NextNumberId ...]", null, null);
                    }
                }

                //System.out.println("devstate aa " + devstate.ordinal());
                if (location != null) {
                    System.out.println("location != null");
                    loccontext = LocationContext.of(this, chatBot, user, location, text);
                    locstate = LocationState.byId(location.getStateId());

                    locstate.handleInput(loccontext, chatId,this);

                System.out.println("locstate bb " + locstate.ordinal());

                    //save chain doing
                    do {
                        locstate = locstate.nextState();
                        locstate.enter(loccontext, chatId,this);
//                    System.out.println("devstate cc " + devstate.ordinal());
                    } while (!locstate.isInputNeeded());

                    location.setStateId(locstate.ordinal());

//                if (devstate != DeviceState.BeginRemoving && devstate != DeviceState.Removing)
//                    deviceService.updateDevice(device);
                    System.out.println("states  " + locstate.ordinal() + " " +location.getStateId());

                    locationService.updateLocation(location);
                }
//END LOCATION

            } else {
//DEVICE
                Device[] devices = deviceService.findByChatId(chatId);
                try {
                    device = devices[devices.length - 1];
                } catch (Exception e) {
                }

                System.out.println("----" + devices.length);
                if (device == null) {
                    System.out.println("NEMA PRYSTROIV");
                } else if (text.equals(ADD_REQUEST)) {
                    System.out.println("ADD PR");
                } else if (text.contains(REMOVE_REQUEST)) {
                    System.out.println("REMOVE PR");
                }

                //System.out.println("devstate aa " + devstate.ordinal());
                if (device != null) {
                    System.out.println("PR UPDATE");

//                    deviceService.updateDevice(device);
                }
//END DEVICE
            }


        } else if (user == null) {
            state = BotState.getInitialState();

            user = new User(chatId, state.ordinal());
            userService.addUser(user);

            context = BotContext.of(chatBot, user, text);
            state.enter(context);
//            isDel = false;
            //LOGGER.info("New user registered: " + chatId);
        }

        context = BotContext.of(chatBot, user, text);
        state = BotState.byId(user.getStateId());
        state.handleInput(context);

        //add for begin reg device
        if(BotContext.of(chatBot, user, text).getUser().getStateId() == BotState.Done.ordinal() && !user.getAdmin()){
            user.setStateId(BotState.Done.ordinal());
            userService.updateUser(user);

            if (deviceService.findByChatId(chatId).length == 0) {
                sendMessage(chatBot, user.getChatId(), "У вас немає жодного пристрою.", getMainMenu(), getInlineStartMenu());
            }
        }

        //save chain doing
        do {
            state = state.nextState();
            state.enter(context);
            //System.out.println("user next state  " + context.getUser().getStateId() + " " + context.getUser().getChatId());
        } while (!state.isInputNeeded());

        user.setStateId(state.ordinal());
        userService.updateUser(user);

/*

        Device device = null;


        //String command = null;
        String sigfoxName = null;
        String sigfoxId = null;
        Long Id = null;
        int currentState = 0;



        if (user == null) {
            state = BotState.getInitialState();

            user = new User(chatId, state.ordinal());
            userService.addUser(user);

            context = BotContext.of(this, user, text);
            state.enter(context);
//            isDel = false;
            LOGGER.info("New user registered: " + chatId);
        }
        state.handleInput(context);

        //save chain doing
        do {
            state = state.nextState();
            state.enter(context);
        } while (!state.isInputNeeded());

        user.setStateId(state.ordinal());
        userService.updateUser(user);

    }

 */



/*
        if(botState.containsKey(chatId)){
            currentState = botState.get(chatId);
        }

        //command = parseCommand(text);

        user = userService.findByChatId(chatId);
//        sendMessage(chatId,"chatId " + users[0].getId());

        if(user!=null) { if(commands(user, text)) return;}
        else {sendMessage(chatId,"Нужно зарегистрировать хотя бы одно устройство");}

        // button add
        if (command.equals(ADD_REQUEST)) {
            sigfoxName = parseSigfoxName(text);
            sigfoxId = parseSigfoxId(text);

            try{
                int surgardNum = Device.calculateNumSurgard(sigfoxId);
                Device testUser = userService.findByNumSurgard(surgardNum);
                if(testUser!=null) {
                    sendMessage(chatId,"Не возможно поставить на охрану, устройство с таким номером уже в системе!");
                    sigfoxId = null;
                    sigfoxName = null;
                }
            }catch (Exception ex) {
                sigfoxId = null;
                sigfoxName = null;
            }
            if(sigfoxId == null || sigfoxName == null){
                sendMessage(chatId,"add sigfoxName sigfoxId(8 HEX)");
            }else {
                user = userService.findByUserIdAndSigfoxId(chatId, sigfoxId);

                if (user == null) {
                    user = new Device(chatId, sigfoxName, sigfoxId);
                    userService.addUser(user);
                    sendMessage(chatId, "id зарегистирован: " + sigfoxId);
                    LOGGER.info("Add id registered: " + sigfoxId + " surgardId " + user.getNumSurgard());
                } else {
                    sendMessage(chatId, "Такое устройство уже зарегистировано");
                    LOGGER.info("Id already has registered: " + sigfoxId + " for " + chatId);
                }
            }
        // button del
        } else if (command.equals(REMOVE_REQUEST)) {
            try {
                Id = Long.parseLong(parseSigfoxName(text));
                user = userService.findById(Id);
                userService.delUser(user);
                sendMessage(chatId,"Deleted");
                LOGGER.info("Del id: " + Id);
            } catch (Exception ex) {
                sendMessage(chatId,"Нет устройства с таким id");
            }

        }else{
            sendMessage(chatId,(command + "?\r\nadd/del/list/broadcast "));
        }

    }
*/
    }

    private boolean checkCommand(ChatBot chatBot, User user, String text) {

        System.out.println("admin ?");
        System.out.println(text);

        if (user == null) return false;

        if (text.equals(START)) {
            sendMessage(chatBot, user.getChatId(), "Привіт, " + user.getUserName() + "!", getMainMenu(), null);
        }else
        if (text.equals("admin lilu5")) {
            user.setAdmin(true);
            sendMessage(chatBot, user.getChatId(),"Привіт, адміністратор!", getMainMenu(), null);
        }else
        if (text.equals("admin logout")) {
            user.setAdmin(false);
            sendMessage(chatBot, user.getChatId(),"Бувай, адміністратор!", getMainMenu(), null);
        }

        if (!user.getAdmin()) {
            System.out.println("admin no");
            if (text.trim().equals(DEVICES)) {
                LOGGER.info("Admin command received: " + DEVICES);
                listDevices(chatBot, user);
                return true;
            }
            return false;
        }

        System.out.println("admin yes");

        if (text.startsWith(BROADCAST)) {
            LOGGER.info("Admin command received: " + BROADCAST);
            text = text.substring(BROADCAST.length());
            broadcast(chatBot, text, userService);
            return true;

        } else if (text.trim().equals(USERS)) {
            LOGGER.info("Admin command received: " + USERS);
            adminListUsers(chatBot, user);
            return true;

        } else if (text.trim().equals(DEVICES)) {
            LOGGER.info("Admin command received: " + DEVICES);
            adminListLocations(chatBot, user, locationService);
//            adminListDevices(chatBot, user, deviceService);
            return true;
        }

        return false;
    }

    private void listDevices(ChatBot chatBot, User user) {
        StringBuilder sb = new StringBuilder("Список парковок:\r\n");
        List<Device> devices = Arrays.asList(deviceService.findByChatId(user.getChatId()));
        try {
            devices.forEach(device -> {
                    sb.append(locationService.findBySigfoxId(device.getSigfoxId()).isPark()?"\uD83D\uDE98":"\uD83C\uDD93")
                            .append(" - <b>")
                            .append(device.getSigfoxName())
                            .append("</b> (")
                            .append(" sigfoxId: ")
                            .append(String.format("%8.8s", device.getSigfoxId()).replace(' ', '0'))
//                            .append(" chatId: ")
//                            .append(device.getChatId())
                            .append(") ")
                            .append("\r\n");
            });
        }catch (Exception ex){
            sb.append("Error");
        }
        sendMessage(chatBot, user.getChatId(), sb.toString(), getMainMenu(), devices.size()>0?getInlineMenu():getInlineStartMenu());
    }

    private void adminListUsers(ChatBot chatBot, User admin) {
        StringBuilder sb = new StringBuilder("chatId " + admin.getChatId() + "\r\n" + "Всі користувачі:\r\n");
        List<User> users = userService.findAllUsers();

        users.forEach(user ->
                sb.append(user.getId())
                        .append(" - <b>")
                        .append(user.getUserName())
                        .append("</b> Phone: ")
                        .append(user.getPhoneNumber())
                        .append(" chatId: ")
                        .append(user.getChatId())
                        .append("\r\n")
        );

        sendMessage(chatBot, admin.getChatId(), sb.toString(), getMainMenu(), getInlineAdminMenu());
    }

    private void adminListLocations(ChatBot chatBot, User admin, LocationService locationService) {
        StringBuilder sb = new StringBuilder("chatId " + admin.getChatId() + "\r\n" + "Всі прилади:\r\n");
        List<Location> locations = locationService.findAllLocations();
        locations.forEach(location -> {
            sb.append(location.getId())
                    .append(" - <b>")
                    .append(location.getId())
                    .append("</b> (sigfoxId: ")
                    .append(String.format("%8.8s", location.getSigfoxId()).replace(' ', '0'))
                    .append(" address: ")
                    .append(location.getAddr())
                    .append(" protocol: ")
                    .append(location.getProtocol())
                    .append(") ")
                    .append("\r\n");
        });

        sendMessage(chatBot, admin.getChatId(), sb.toString(), getMainMenu(), getInlineAdminMenu());
    }

    private void adminListDevices(ChatBot chatBot, User admin, DeviceService deviceService) {
        StringBuilder sb = new StringBuilder("chatId " + admin.getChatId() + "\r\n" + "Всі лічильникі:\r\n");
        List<Device> devices = deviceService.findAllDevices();
        devices.forEach(device -> {

                sb.append(device.getId())
                        .append(" - <b>")
                        .append(device.getSigfoxName())
                        .append("</b> (sigfoxId: ")
                        .append(String.format("%8.8s", device.getSigfoxId()).replace(' ', '0'))
                        .append(" chatId: ")
                        .append(device.getChatId())
                        .append(" user: ")
                        .append(userService.findByChatId(device.getChatId()).getUserName())
                        .append(") ")
                        .append("\r\n");
        });

        sendMessage(chatBot, admin.getChatId(), sb.toString(), getMainMenu(), getInlineAdminMenu());
    }

    private void broadcast(ChatBot chatBot, String text, UserService userService) {
        List<User> users = userService.findAllUsers();
        users.forEach(user -> sendMessage(chatBot, user.getChatId(), text, getMainMenu(), getInlineAdminMenu()));
    }



/*
    private boolean checkIfAdminCommand(User user, String text) {
        try {
            System.out.println("admin " + user.toString());
        }catch (Exception e){}
        System.out.println("text "+text);
//        if (user == null || !user.getAdmin())
        if (user == null)
            return false;

        if (text.startsWith(BROADCAST)) {
            LOGGER.info("Admin command received: " + BROADCAST);

            text = text.substring(BROADCAST.length());
            broadcast(text);

            return true;
        } else if (text.equals(LIST_USERS)) {
            LOGGER.info("Admin command received: " + LIST_USERS);

            listUsers(user);
            return true;
        }

        return false;
    }

    private void sendMessage(Long chatId, String text) {
        var message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text)
                .build();
        message.setParseMode("HTML");
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void listDevices(Device device) {
        StringBuilder sb = new StringBuilder("chatId "+ device.getChatId() + "\r\n" + "All devices list:\r\n");
        List<Device> devices = deviceService.findAllDevices();

        devices.forEach(user ->
                sb.append(user.getId())
                        .append(" - <b>")
                        .append(user.getSigfoxName())
                        .append("</b> SigfoxId: ")
                        .append(user.getSigfoxId())
                        .append(" chatId: ")
                        .append(user.getChatId())
                        .append(" surgardId: ")
                        .append(user.getNumSurgard())
                        .append("\r\n")
        );

        sendMessage(device.getChatId(), sb.toString());
    }

    private void listUsers(User admin) {
        StringBuilder sb = new StringBuilder("chatId "+ admin.getChatId() + "\r\n" + "All users list:\r\n");
        List<User> users = userService.findAllUsers();

        users.forEach(user ->
            sb.append(user.getId())
                    .append(" - <b>")
                    .append(user.getUserName())
                    .append("</b> Phone: ")
                    .append(user.getPhoneNumber())
                    .append(" chatId: ")
                    .append(user.getChatId())
                    .append("\r\n")
        );

        sendMessage(admin.getChatId(), sb.toString());
    }

//    private void listDevice(User admin, chatId) {
//        StringBuilder sb = new StringBuilder("chatId "+ admin.getChatId() + "\r\n" + "All users list:\r\n");
//        List<User> users = userService.findAllUsers();
//
//        users.forEach(user ->
//                sb.append(user.getId())
//                        .append(" - <b>")
//                        .append(user.getSigfoxName())
//                        .append("</b> SigfoxId: ")
//                        .append(user.getSigfoxId())
//                        .append(" chatId: ")
//                        .append(user.getChatId())
//                        .append("\r\n")
//        );
//
//        sendMessage(admin.getChatId(), sb.toString());
//    }

    private void broadcast(String text) {
        List<User> users = userService.findAllUsers();
        users.forEach(user -> sendMessage(user.getChatId(), text));
    }

    private Device findUserById(Device[] users, long id) {
        for (int i = 0; i < users.length; i++) {
            //System.out.println(users[i].getId() + " " + users[i].getChatId() + " " + users[i].getSigfoxName() + " " + users[i].getSigfoxId() +" " + users[i].getStateId());
            if (users[i].getId() == id) {
                System.out.println( " find " + users[i].getId());
                return users[i];
            }
            System.out.println( " f " + users[i].getId());
        }
        return null;
    }

    private String parseCommand(String text){
        String[] subStr;
        subStr = text.split(delimeter); // Разделения строки str с помощью метода split()
        return subStr[0];
    }

    private String parseSigfoxName(String text){
        String[] subStr;
        subStr = text.split(delimeter); // Разделения строки str с помощью метода split()
        try {
            return subStr[1];
        }catch (Exception ex) {
            return null;
        }
    }

    private String parseSigfoxId(String text){
        String[] subStr;
        subStr = text.split(delimeter); // Разделения строки str с помощью метода split()
        try{
            String sigfoxId = subStr[2];
            if(sigfoxId.length()>8) return null;
            else sigfoxId = sigfoxId.replaceFirst ("^0*", "");//if(sigfoxId.charAt(0) == '0') sigfoxId = sigfoxId.substring(1,8);
            return sigfoxId;
        }catch (Exception ex) {
            return null;
        }

 */

}

