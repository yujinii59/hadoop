import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionOutputStream;
import org.apache.hadoop.util.ReflectionUtils;

public class StreamCompressor {
    public static void main(String[] args) throws Exception {
        String codecClassname = args[0];  // org.apache.hadoop.io.compress.GzipCodec
        Class<?> codecClass = Class.forName(codecClassname);
        Configuration conf = new Configuration();
        // 코덱의 새로운 인스턴스 생성
        CompressionCodec codec = (CompressionCodec)
                ReflectionUtils.newInstance(codecClass, conf);

        CompressionOutputStream out = codec.createOutputStream(System.out);
        // 메서드 호출 시 CompressionOutputStream으로 압축
        IOUtils.copyBytes(System.in, out, 4096, false);
        out.finish(); // 스트림 쓰기 중단. 스트림 종료 X

    }
}
