package ua.utilix.model;

public interface Sigfox {

    default SigfoxData parse(String id, String input, int sequence) {
        System.out.println("Лічильник " + id + ". Невідомий тип. Данні: " + input);
        return null;
    }

    enum TypeMessage {
        SYSTEM {
            @Override
            public String toString() {
                return "SYSTEM";
            }
        },
        BTT {
            @Override
            public String toString() {
                return "BTT";
            }
        },
        TMP {
            @Override
            public String toString() {
                return "TMP";
            }
        },
        CC {
            @Override
            public String toString() {
                return "CC";
            }
        },
        ERR {
            @Override
            public String toString() {
                return "ERR";
            }
        },
        VD {
            @Override
            public String toString() {
                return "VD";
            }
        }
    }

    enum TypeError {
        NOERROR {
            @Override
            public String toString() {
                return "";
            }
        },
        MAG {
            @Override
            public String toString() {
                return "Виявлено магнит! ";
            }
        },
        BATT {
            @Override
            public String toString() {
                return "Проблема з батареєю! ";
            }
        },
        PHOTO {
            @Override
            public String toString() {
                return "Виявлено пошкодження! ";
            }
        },
        WDOG {
            @Override
            public String toString() {
                return "Сенсор потрібно замінити! ";
            }
        }
    }
}
