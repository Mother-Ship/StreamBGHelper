import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        String beatMapFile = "C:\\Program Files (x86)\\StreamCompanion\\Files\\np_playing_DL.txt";
        String thumbFile = "C:\\Program Files (x86)\\StreamCompanion\\Files\\thumb.jpg";
        File filename = new File(beatMapFile);
        ApiManager apiManager = new ApiManager();
        String line = "";
        while (true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(filename)));
            String line2 = br.readLine();
            if(!line2.equals(line)) {
                line = line2;
                System.out.println("检测到谱面变化，新的谱面为："+line2+"，开始获取谱面信息");
                Beatmap beatmap = apiManager.getBeatmap(Integer.valueOf(line.substring(20)));
                System.out.println("开始抓取缩略图");
                BufferedImage image = ImageIO.read(new URL("https://b.ppy.sh/thumb/" + beatmap.getBeatmapSetId() + "l.jpg"));
                System.out.println("开始写入缩略图");
                ImageIO.write(image, "jpg", new File(thumbFile));
            }
            TimeUnit.MILLISECONDS.sleep(100);
        }

    }
}
