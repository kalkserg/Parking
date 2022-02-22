package ua.utilix.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.utilix.Handlers.Handler;

import java.util.Locale;

public enum LocationState {

    StartEmptyRegLocation {
        @Override
        public void enter(LocationContext locationContext, long chatId, Handler handler) {
            String name = locationContext.getUser().getUserName();
            sendMessage(handler, locationContext, chatId, "Привіт" + (name==null?"":(", " + name)) + "!\n У вас немає жодного пристрою." );
        }

        @Override
        public LocationState nextState() {
            return EnterSigfoxAddr;
        }
    },

    StartRegLocation {
        @Override
        public void enter(LocationContext locationContext, long chatId, Handler handler) {
            String name = locationContext.getUser().getUserName();
            sendMessage(handler, locationContext,  chatId,"Привіт" + (name==null?"":(", " + name)) + "!" );
        }

        @Override
        public LocationState nextState() {
            return EnterSigfoxID;
        }
    },

    EnterSigfoxID {
        private LocationState next;
        @Override
        public void enter(LocationContext deviceContext, long chatId, Handler handler) {
            sendMessage(handler, deviceContext,  chatId,"Введіть ідентифікатор Sigfox:");
        }

        @Override
        public void handleInput(LocationContext locationContext, long chatId, Handler handler) {
            String str = locationContext.getInput().toUpperCase(Locale.ROOT);
            if(str.matches("[0-9A-F]{1,8}")) {
                str = str.replaceFirst ("^0*", "");
                locationContext.getLocation().setSigfoxId(str);
                next = EnterSigfoxAddr;
            }else{
                sendMessage(handler, locationContext,  chatId, "Невірний ідентифікатор Sigfox!");
                next = EnterSigfoxID;
            }
            //deviceContext.getDevice().setNotified(true);
        }

        @Override
        public LocationState nextState() {
            return next;
        }
    },

    EnterSigfoxAddr {
        @Override
        public void enter(LocationContext locationContext, long chatId, Handler handler) {
            sendMessage(handler,locationContext,  chatId,"Введіть адресу паркінгу:");
        }

        @Override
        public void handleInput(LocationContext locationContext, long chatId, Handler handler) {
            locationContext.getLocation().setAddr(locationContext.getInput());
            locationContext.getLocation().setProtocol("ParkingSensor");
        }

        @Override
        public LocationState nextState() {
            return Registred;
        }
    },

    Registred {
        @Override
        public void enter(LocationContext context, long chatId, Handler handler) {
            sendMessage(handler, context,  chatId,"Паркінг зареєстровано.");
        }

        @Override
        public LocationState nextState() {
            return Done;
        }
    },

    Done {

        @Override
        public LocationState nextState() {
            return Done;
        }
    },

    BeginRemoving {
        @Override
        public LocationState nextState() {
            return Done;
        }
    },

    Removing {
//        private BotState next;
        @Override
        public void enter(LocationContext locationContext, long chatId, Handler handler) { sendMessage(handler,locationContext,  chatId,"Removed");}

        @Override
        public void handleInput(LocationContext locationContext, long chatId, Handler handler) {
            locationContext.getLocation().setId(Long.parseLong(locationContext.getInput()));
            //System.out.println("start removing context " + Long.parseLong(deviceContext.getInput()) );
        }

//        @Override
//        public void handleInput(BotContext context) {
//            if (context.getInput().toLowerCase(Locale.ROOT).contains("y")) {
//                next = Done;
//                sendMessage(context, "Deleted!");
//            } else {
//                next = BeginRemoving;
//                sendMessage(context, "Begin removing");
//            }
//        }

        @Override
        public LocationState nextState() {
            return Done;
        }
    },

    Removed {
        private LocationState next;
        @Override
        public void enter(LocationContext locationContext, long chatId, Handler handler) { sendMessage(handler,locationContext,  chatId,"Yes:No"); }

        @Override
        public void handleInput(LocationContext locationContext, long chatId, Handler handler) {
            locationContext.getLocation().setId(Long.parseLong(locationContext.getInput()));
            //System.out.println("start removing context " +Long.parseLong(deviceContext.getInput()) );
        }

//        @Override
//        public void handleInput(BotContext context) {
//            if (context.getInput().toLowerCase(Locale.ROOT).contains("y")) {
//                next = Done;
//                sendMessage(context, "Deleted!");
//            } else {
//                next = BeginRemoving;
//                sendMessage(context, "Begin removing");
//            }
//        }

        @Override
        public LocationState nextState() {
            return next;
        }
    };

    //private static BotState[] states;

    private static LocationState[] states;
//    private static Map<Long, DeviceState> states;
    private final boolean inputNeeded;

    LocationState() {
        this.inputNeeded = true;
    }

    LocationState(boolean inputNeeded) {
        this.inputNeeded = inputNeeded;
    }

    public static LocationState getInitialState() {
        return byId(0);
    }

    public static LocationState byId(int id) {
        if (states == null) {
            states = LocationState.values();
        }
        //System.out.println("byId state "  + " id " + id);
        return states[id];

//        if (states == null) {
//            states = new HashMap<>();
//        }
//        //System.out.println("byId state " + states[id] + " id " + id);
//        return states.get(id);
    }

    protected void sendMessage(Handler handler, LocationContext locationContext, long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text)
                .replyMarkup(handler.getMainMenu())
                .build();
        try {
            locationContext.getBot().execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public boolean isInputNeeded() {
        System.out.println("device inputneeded "+inputNeeded);
        return inputNeeded;
    }

    public void handleInput(LocationContext context, long chatId, Handler handler) {
        // do nothing by default
        //System.out.println("need next");
    }

    public void enter(LocationContext context, long chatId, Handler handler) {};

    public abstract LocationState nextState();


}
