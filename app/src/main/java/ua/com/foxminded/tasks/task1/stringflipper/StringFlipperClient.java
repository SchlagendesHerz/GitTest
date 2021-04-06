package ua.com.foxminded.tasks.task1.stringflipper;

public class StringFlipperClient {
    public static void main(String[] args) {
//        System.out.println(Arrays.toString("Foxminded   cool 24/7".split("\\p{Space}+")));
        StringFlipper rot = new StringFlipper();
        rot.setToIgnore("1");
//        rot.clearIgnore();
        rot.setToIgnore("Foxminded cool 24/7");
        System.out.println(rot.getIgnore());
//        System.out.println(rot.rotate("cool ^  12345"));
        System.out.println(rot.rotate("Foxminded   cool 24/7"));
    }
}