import org.apache.hadoop.io.Text;

import java.nio.ByteBuffer;

public class TextIterator {

    public static void main(String[] args) {
        Text t = new Text("2\\ud638\\uc120");

        ByteBuffer buf = ByteBuffer.wrap(t.getBytes(), 0, t.getLength());
        System.out.println();
        System.out.println(t.getBytes());
        System.out.println();
        int cp;
        while (buf.hasRemaining() && (cp = Text.bytesToCodePoint(buf)) != -1){
            System.out.println(Integer.toString(cp));
            System.out.println(Integer.toHexString(cp));
            System.out.println(Integer.toString(cp));
        }
    }
}
