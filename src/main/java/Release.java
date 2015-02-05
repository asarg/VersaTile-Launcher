import com.google.gson.Gson;

import java.util.List;

/**
 * Created by ericmartinez on 2/4/15.
 */
class Release{
    String url;
    String assets_url;
    String upload_url;
    String html_url;
    int id;
    String tag_name;
    String created_at;
    String published_at;
    List<Assets> assets;
}