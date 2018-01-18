
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        String beatMapFile = "C:\\Program Files (x86)\\StreamCompanion\\Files\\np_playing_DL.txt";
        String thumbFile = "C:\\Program Files (x86)\\StreamCompanion\\Files\\thumb.jpg";
        File filename = new File(beatMapFile);
        ApiManager apiManager = new ApiManager();
        String line = "";
        Pattern pattern = Pattern.compile("http://osu.ppy.sh/b/(.*)");
        while (true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(filename)));
            String line2 = br.readLine();
            //StreamCompanion没开启的时候line2是null
            if(line2!=null&&!line.equals(line2)) {
                Matcher m = pattern.matcher(line2);
                if(m.find()) {
                    line = line2;
                    System.out.println(Instant.now() + " 检测到谱面变化，新的谱面为：" + line2 + "，开始获取谱面信息");
                    Beatmap beatmap = apiManager.getBeatmap(Integer.valueOf(m.group(1)));
                    if (beatmap != null) {
                        System.out.println(Instant.now() + " 开始抓取"+ beatmap.getArtist() + " - " + beatmap.getTitle() + " [" + beatmap.getVersion() + "]的缩略图");
                        BufferedImage image = ImageIO.read(new URL("https://b.ppy.sh/thumb/" + beatmap.getBeatmapSetId() + "l.jpg"));
                        if (image != null) {
                            System.out.println(Instant.now() + " 开始写入缩略图");
                            ImageIO.write(image, "jpg", new File(thumbFile));
                            System.out.println(Instant.now() + " 缩略图已更新.");
                        } else {
                            System.out.println(Instant.now() + " 获取" + line + "的背景失败，开始重试");
                            line = "";
                        }
                    } else {
                        System.out.println(Instant.now() + " 获取" + line + "的谱面信息失败。");
                        line = "";
                    }
                }
            }
            TimeUnit.MILLISECONDS.sleep(100);
        }

    }
}
