package ma.ma;

/**
 * Created by Nitharani on 28/04/2018.
 */

//setters and getters
public class Convo {
    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Convo(boolean seen, long timestamp) {
        this.seen = seen;
        this.timestamp = timestamp;
    }

    public boolean seen;
    public long timestamp;
//constructor
    public Convo(){

    }


}
