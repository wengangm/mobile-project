package com.example.comp90018.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp90018.dataBean.FriendItem;
import com.example.comp90018.R;
import com.example.comp90018.ui.SideIndexBar;
import com.example.comp90018.utils.DataManager;
import com.example.comp90018.utils.OnRecycleItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class FriendListAdapter extends RecyclerView.Adapter {
    // The data of the list
    private List<FriendItem> friendItems=new ArrayList<FriendItem>();

    // The request number
    private int requestNum;

    // The listener for the click event
    private OnRecycleItemClickListener onItemClickListener;
    public static final int VIEW_HOLEDER_TYPE_SPACE=0;
    public static final int VIEW_HOLEDER_TYPE_NORMAL=1;
    public static final int VIEW_HOLEDER_TYPE_INDEX=2;
    public static final int VIEW_HOLEDER_TYPE_REQUEST=3;


    //The view for each item
    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageAvatar;
        TextView nameText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageAvatar=(ImageView)itemView.findViewById(R.id.item_friend_image);
            nameText=(TextView)itemView.findViewById(R.id.item_friend_name);
        }
    }

    private class SpaceViewHolder extends RecyclerView.ViewHolder{
        Space space;
        public SpaceViewHolder(View view){
            super(view);
            space=view.findViewById(R.id.item_space);
        }
    }

    private class IndexViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public IndexViewHolder(@NonNull View itemView) {
            super(itemView);
            textView=(TextView)itemView.findViewById((R.id.item_index_text));
        }
    }

    private class RequestViewHolder extends RecyclerView.ViewHolder{
        TextView requestNumText;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            requestNumText=(TextView)itemView.findViewById(R.id.item_friend_request_num_text);
        }
    }

    public FriendListAdapter(List<FriendItem> friendItems){
        this.friendItems=new ArrayList<FriendItem>();
        for(FriendItem item:friendItems){
            this.friendItems.add(item);
        }
        for(String index:SideIndexBar.indexs){
            this.friendItems.add(new FriendItem("",null,index,VIEW_HOLEDER_TYPE_INDEX));
        }
        this.friendItems.add(new FriendItem("",null,null,VIEW_HOLEDER_TYPE_REQUEST));
        itemSort();
    }

    private void itemSort(){
        Collections.sort(friendItems, new Comparator<FriendItem>() {
            String letterPattern="[a-zA-Z]";
            @Override
            public int compare(FriendItem friendItem, FriendItem t1) {

                if(friendItem.getItemType()==VIEW_HOLEDER_TYPE_REQUEST){
                    return -1;
                }
                if(t1.getItemType()==VIEW_HOLEDER_TYPE_REQUEST){
                    return 1;
                }
                String str1=friendItem.getName().toUpperCase();
                String str2=t1.getName().toUpperCase();
                if(str1.charAt(0)==str2.charAt(0) && friendItem.getItemType()!=t1.getItemType()){
                    if(friendItem.getItemType()==VIEW_HOLEDER_TYPE_INDEX){
                        return -1;
                    }else{
                        return 1;
                    }
                }else if(Pattern.matches(letterPattern,friendItem.getName().substring(0,1)) && Pattern.matches(letterPattern,t1.getName().substring(0,1)) ||
                        !Pattern.matches(letterPattern,friendItem.getName().substring(0,1)) && !Pattern.matches(letterPattern,t1.getName().substring(0,1))){

                    return str1.compareTo(str2);
                }else {
                    if(Pattern.matches(letterPattern,friendItem.getName().substring(0,1))){
                        return -1;
                    }else{
                        return 1;
                    }
                }
            }
        });
        for(int i=0;i<friendItems.size();i++){
            if(friendItems.get(i).getItemType()==VIEW_HOLEDER_TYPE_INDEX){
                if(i==friendItems.size()-1 || friendItems.get(i+1).getItemType()==VIEW_HOLEDER_TYPE_INDEX){
                    friendItems.remove(i);
                    i--;
                }
            }
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==VIEW_HOLEDER_TYPE_NORMAL) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friends, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }else if(viewType==VIEW_HOLEDER_TYPE_INDEX){
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_index, parent, false);
            IndexViewHolder holder=new IndexViewHolder(view);
            return holder;
        }else if(viewType==VIEW_HOLEDER_TYPE_REQUEST){
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_request, parent, false);
            RequestViewHolder holder=new RequestViewHolder(view);
            return holder;
        }else{
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        //Bind the data
        int viewType=holder.getItemViewType();
        if(viewType==VIEW_HOLEDER_TYPE_INDEX){
            IndexViewHolder myHolder=(IndexViewHolder)holder;
            myHolder.textView.setText(friendItems.get(position).getName());
        }else if(viewType==VIEW_HOLEDER_TYPE_NORMAL){
            FriendItem item = friendItems.get(position);
            ViewHolder myHolder = (ViewHolder) holder;
            Picasso.get().load(item.getImage()).into(myHolder.imageAvatar);
            myHolder.nameText.setText(item.getName());

            //Set click listener to each item
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        int pos = holder.getLayoutPosition();
                        onItemClickListener.onItemClick(holder.itemView, pos);
                    }
                }
            });

        }else if(viewType==VIEW_HOLEDER_TYPE_REQUEST){
            RequestViewHolder myHolder=(RequestViewHolder) holder;
            if(requestNum==0){
                myHolder.requestNumText.setVisibility(View.INVISIBLE);
            }else if(requestNum>99){
                myHolder.requestNumText.setVisibility(View.VISIBLE);
                myHolder.requestNumText.setText("99+");
            }else{
                myHolder.requestNumText.setVisibility(View.VISIBLE);
                myHolder.requestNumText.setText(String.valueOf(requestNum));
            }

            //Set click listener
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        int pos = holder.getLayoutPosition();
                        onItemClickListener.onItemClick(holder.itemView, pos);
                    }
                }
            });

        }
    }

    @Override
    public int getItemViewType(int position) {
        return friendItems.get(position).getItemType();
    }

    @Override
    public int getItemCount() {
        return friendItems.size();
    }

    //The method used to set a listener
    public void setOnItemClickListener(OnRecycleItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }


    public List<FriendItem> getFriendListItem(){
        return this.friendItems;
    }

    public void setRequestNum(int num){
        requestNum=num;
    }
}
