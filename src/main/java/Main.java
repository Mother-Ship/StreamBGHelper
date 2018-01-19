import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.time.Instant;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        java.net.URL url = Main.class.getProtectionDomain().getCodeSource().getLocation();
        String jarFilePath = null;
        try {
            jarFilePath = java.net.URLDecoder.decode(url.getPath(), "utf-8");
        } catch (Exception e) {
            System.err.println(Instant.now()+" 解析JAR运行路径失败。程序已挂起，请手动关闭并提交Issue");
            TimeUnit.DAYS.sleep(100);
        }
        if (jarFilePath.endsWith(".jar"))
            jarFilePath = jarFilePath.substring(0, jarFilePath.lastIndexOf("/") + 1);
        java.io.File file = new java.io.File(jarFilePath);
        jarFilePath = file.getAbsolutePath();
        String defaultBeatMapFilePath = "C:\\Program Files (x86)\\StreamCompanion\\Files\\np_playing_DL.txt";
        String defaultThumbFilePath = "C:\\Program Files (x86)\\StreamCompanion\\Files\\thumb.jpg";
        //读取配置
        File configFile = new File(jarFilePath + "\\config.ini");
        if (configFile.exists()) {
            //如果配置存在，读取路径覆盖掉默认文件路径
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(configFile)));
            String path = br.readLine();
            defaultBeatMapFilePath = path + "\\np_playing_DL.txt";
            defaultThumbFilePath = path + "\\thumb.jpg";
            System.out.println(Instant.now() + " 读取配置文件成功，生成的图片路径为："+path + "\\thumb.jpg");
        }else{
            //如果不存在，把默认路径写进去
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(jarFilePath + "\\config.ini"), "utf-8"))) {
                writer.write("C:\\Program Files (x86)\\StreamCompanion\\Files");
            }
        }
        File beatMapFile = new File(defaultBeatMapFilePath);
        if (!beatMapFile.exists()) {
            System.err.println(Instant.now() + " 在默认位置和配置文件："+jarFilePath+"\\config.ini指定的位置都没有找到np_playing_DL.txt。请将Stream Companion安装路径下的np_playing_DL.txt拖入本窗口并回车…");
            Scanner scanner = new Scanner(System.in);
            while (true) {
                defaultBeatMapFilePath = scanner.nextLine();
                beatMapFile = new File(defaultBeatMapFilePath.substring(1, defaultBeatMapFilePath.length() - 1));
                if (beatMapFile.exists()&&beatMapFile.getName().equals("np_playing_DL.txt")) {
                    try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(jarFilePath + "\\config.ini"), "utf-8"))) {
                        //parentFile是File目录
                        writer.write(beatMapFile.getParent());
                    }
                    defaultThumbFilePath = beatMapFile.getParent() + "\\thumb.jpg";
                    System.out.println(Instant.now() + " 配置成功，读取文本/生成图片的路径已指定为"+beatMapFile.getParent());
                    break;
                } else {
                    System.err.println(Instant.now() + " 请你们正常一点…\n请拖入正确的文件：");
                }
            }
        }
        ApiManager apiManager = new ApiManager();
        String line = "";
        Pattern pattern = Pattern.compile("http://osu.ppy.sh/b/(.*)");
        while (true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(beatMapFile)));
            String line2 = br.readLine();
            //StreamCompanion没开启的时候line2是null
            if (line2 != null && !line.equals(line2)) {
                Matcher m = pattern.matcher(line2);
                if (m.find()) {
                    line = line2;
                    System.out.println(Instant.now() + " 检测到谱面变化，新的谱面为：" + line2 + "，开始获取谱面信息");
                    Beatmap beatmap = apiManager.getBeatmap(Integer.valueOf(m.group(1)));
                    if (beatmap != null) {
                        System.out.println(Instant.now() + " 开始抓取" + beatmap.getArtist() + " - " + beatmap.getTitle() + " [" + beatmap.getVersion() + "]的缩略图");
                        BufferedImage image = ImageIO.read(new URL("https://b.ppy.sh/thumb/" + beatmap.getBeatmapSetId() + "l.jpg"));
                        if (image != null) {
                            System.out.println(Instant.now() + " 开始写入缩略图");
                            ImageIO.write(image, "jpg", new File(defaultThumbFilePath));
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
