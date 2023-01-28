import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionOutputStream;
import org.apache.hadoop.util.ReflectionUtils;

import java.io.InputStream;

public class StreamCompressorLocalFile {
    public static void main(String[] args) throws Exception {
        String codecClassname = args[0]; // org.apache.hadoop.io.compress.GzipCodec
        String uri = args[1];
        Class<?> codecClass = Class.forName(codecClassname);
        Configuration conf = new Configuration();

        // 코덱의 새로운 인스턴스 생성
        CompressionCodec codec = (CompressionCodec) ReflectionUtils.newInstance(codecClass, conf);

        CompressionOutputStream out = codec.createOutputStream(System.out);

        LocalFileSystem fs = LocalFileSystem.getLocal(conf);
        InputStream in = null;
        try {
            in = fs.open(new Path(uri));
            // 메서드 호출 시 CompressionOutputStream으로 압축
            IOUtils.copyBytes(in, out, 4096, false);
        } finally {
            IOUtils.closeStream(in);
            // 스트림 쓰기 중단. 스트림 종료 X
            out.finish();
        }
    }
}
