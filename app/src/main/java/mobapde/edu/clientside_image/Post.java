package mobapde.edu.clientside_image;

/**
 * Created by courtneyngo on 4/1/16.
 */
public class Post {

    public static final String TITLE = "title";
    public static final String IMAGE_URL = "imageUrl";

    private String title;
    private String imageUrl;

    public Post(){}

    public Post(String title, String imageUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
