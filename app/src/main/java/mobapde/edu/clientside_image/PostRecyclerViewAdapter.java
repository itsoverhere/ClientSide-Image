package mobapde.edu.clientside_image;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by courtneyngo on 4/1/16.
 */
public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.PostViewHolder>{

    ArrayList<Post> postArrayList;
    LoadImageListener loadImageListener;

    public PostRecyclerViewAdapter(ArrayList<Post> postArrayList){
        this.postArrayList = postArrayList;
    }

    public void setPostArrayList(ArrayList<Post> postArrayList){
        this.postArrayList = postArrayList;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        Post post = postArrayList.get(position);
        holder.tvText.setText(post.getTitle());
        holder.setPost(post);
        loadImageListener.loadImage(holder);
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{

        ImageView ivImage;
        TextView tvText;
        private Post p;

        public PostViewHolder(View itemView) {
            super(itemView);

            ivImage = (ImageView) itemView.findViewById(R.id.iv_image);
            tvText = (TextView) itemView.findViewById(R.id.tv_text);
        }

        public void setPost(Post p){
            this.p = p;
        }

        public Post getPost(){
            return this.p;
        }
    }

    public interface LoadImageListener{
        public void loadImage(PostViewHolder postViewHolder);
    }

    public void setLoadImageListener(LoadImageListener loadImageListener){
        this.loadImageListener = loadImageListener;
    }

}
