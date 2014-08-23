package in.konstant.Sensors;

public enum Type {
    TYPE_FLOAT,
    TYPE_UINT8,
    TYPE_UINT16,
    TYPE_UINT32,
    TYPE_INT8,
    TYPE_INT16,
    TYPE_INT32;
//    TYPE_BOOL;

    @Override
    public String toString() {
        switch (this) {
            default:
                throw new IllegalArgumentException();
            case TYPE_FLOAT:
                return "Float";
            case TYPE_UINT8:
            case TYPE_INT8:
                return "8-Bit";
            case TYPE_UINT16:
            case TYPE_INT16:
                return "16-Bit";
            case TYPE_UINT32:
            case TYPE_INT32:
                return "32-Bit";
//          case TYPE_BOOL:
//              return "Boolean";
        }
    }

}