import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;


public class ApiManager {
    private final String getUserURL = "https://osu.ppy.sh/api/get_user";
    private final String getBPURL = "https://osu.ppy.sh/api/get_user_best";
    private final String getMapURL = "https://osu.ppy.sh/api/get_beatmaps";
    private final String getRecentURL = "https://osu.ppy.sh/api/get_user_recent";
    private final String getScoreURL = "https://osu.ppy.sh/api/get_scores";
    private final String getMatchURL = "https://osu.ppy.sh/api/get_match";

    private final String key = "25559acca3eea3e2c730cd65ee8a6b2da55b52c0";





    public Beatmap getBeatmap(Integer bid) {
        String result = accessAPI("beatmap", null, null, String.valueOf(bid), null, null, null);
        return new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setDateFormat("yyyy-MM-dd HH:mm:ss").create().fromJson(result, Beatmap.class);
    }

    public Beatmap getBeatmap(String hash) {
        String result = accessAPI("beatmapHash", null, null, null, String.valueOf(hash), null, null);
        return new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setDateFormat("yyyy-MM-dd HH:mm:ss").create().fromJson(result, Beatmap.class);
    }


    private String accessAPI(String apiType, String uid, String uidType, String bid, String hash, Integer rank, String mid) {
        String URL;
        String failLog;
        String output = null;
        HttpURLConnection httpConnection;
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("u", uid));
        switch (apiType) {
            case "user":
                URL = getUserURL + "?k=" + key + "&type=" + uidType + "&" + URLEncodedUtils.format(params, "utf-8");
                failLog = "玩家" + uid + "请求API：get_user失败五次";
                break;
            case "bp":
                URL = getBPURL + "?k=" + key + "&type=" + uidType + "&limit=100&" + URLEncodedUtils.format(params, "utf-8");
                failLog = "玩家" + uid + "请求API：get_user_best失败五次";
                break;
            case "beatmap":
                URL = getMapURL + "?k=" + key + "&b=" + bid;
                failLog = "谱面" + bid + "请求API：get_beatmaps失败五次";
                break;
            case "beatmapHash":
                URL = getMapURL + "?k=" + key + "&h=" + hash;
                failLog = "谱面" + bid + "请求API：get_beatmaps失败五次";
                break;
            case "recent":
                URL = getRecentURL + "?k=" + key + "&type=" + uidType + "&limit=1&" + URLEncodedUtils.format(params, "utf-8");
                failLog = "玩家" + uid + "请求API：get_recent失败五次";
                break;
            case "recents":
                URL = getRecentURL + "?k=" + key + "&type=" + uidType + "&limit=100&" + URLEncodedUtils.format(params, "utf-8");
                failLog = "玩家" + uid + "请求API：get_recent失败五次";
                break;
            case "first":
                URL = getScoreURL + "?k=" + key + "&limit=" + rank + "&b=" + bid;
                failLog = "谱面" + bid + "请求API：get_scores失败五次";
                break;
            case "score":
                URL = getScoreURL + "?k=" + key + "&type=" + uidType + "&u=" + uid + "&b=" + bid;
                failLog = "谱面" + bid + "请求API：get_scores失败五次";
                break;
            case "match":
                URL = getMatchURL + "?k=" + key + "&mp=" + mid;
                failLog = "谱面" + bid + "请求API：get_scores失败五次";
                break;
            default:
                System.out.println("apiType错误");
                return null;

        }

        int retry = 0;
        while (retry < 5) {
            try {
                httpConnection =
                        (HttpURLConnection) new URL(URL).openConnection();
                //设置请求头
                httpConnection.setRequestMethod("GET");
                httpConnection.setRequestProperty("Accept", "application/json");
                httpConnection.setConnectTimeout((int) Math.pow(2, retry + 1) * 1000);
                httpConnection.setReadTimeout((int) Math.pow(2, retry + 1) * 1000);
                if (httpConnection.getResponseCode() != 200) {
                    System.out.println("HTTP GET请求失败: " + httpConnection.getResponseCode() + "，正在重试第" + (retry + 1) + "次");
                    retry++;
                    continue;
                }
                //读取返回结果
                BufferedReader responseBuffer =
                        new BufferedReader(new InputStreamReader((httpConnection.getInputStream())));
                StringBuilder tmp2 = new StringBuilder();
                String tmp;
                while ((tmp = responseBuffer.readLine()) != null) {
                    tmp2.append(tmp);
                }
                //去掉两侧的中括号
                output = tmp2.toString().substring(1, tmp2.toString().length() - 1);
                //手动关闭流
                httpConnection.disconnect();
                responseBuffer.close();
                break;
            } catch (IOException e) {
                System.out.println("出现IO异常：" + e.getMessage() + "，正在重试第" + (retry + 1) + "次");
                retry++;
            }
        }
        if (retry == 5) {
            System.err.println(failLog);
            return null;
        }
        return output;
    }
}
