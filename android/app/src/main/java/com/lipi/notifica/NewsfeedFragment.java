package com.lipi.notifica;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aditya on 12/12/15.
 */
public class NewsfeedFragment extends Fragment {
    private RecyclerView.Adapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private List<Post> mPosts;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mPosts = new ArrayList<>();
        View rootView = inflater.inflate(R.layout.fragment_newsfeed, container, false);
        RecyclerView recyclerView;
        RecyclerView.LayoutManager layoutManager;
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view_posts);
        recyclerView.addItemDecoration(new PostDivider(rootView.getContext()));
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(rootView.getContext());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new PostAdapter(getActivity(), mPosts);
        recyclerView.setAdapter(mAdapter);

        getPosts();

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout_posts);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPosts();
            }
        });
        return rootView;
    }

    // TODO: implement fetching of posts from server
    // fetch posts from the server
    private void getPosts(){
        mPosts.clear();
        Post testPost1 = new Post();
        Post testPost2 = new Post();
        Post testPost3 = new Post();
        testPost1.title = "Hello World!";
        testPost1.content = "OK DOOD!";
        testPost2.title = "Yay! 2nd post";
        testPost2.content = "some shit";
        testPost3.title = "Saturday be tomorrow";
        testPost3.content = "Dont go to college tomorrow, cause its saturday";
        mPosts.add(testPost1);
        mPosts.add(testPost2);
        mPosts.add(testPost3);
        refreshView();
    }

    public void refreshView(){
        if(mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }
        if(mSwipeRefreshLayout != null){
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public class PostDivider extends RecyclerView.ItemDecoration{
        private Drawable mDivider;
        public PostDivider(Context context){
            mDivider = context.getResources().getDrawable(R.drawable.divider_post);
        }
        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            if( parent.getChildCount() < 2 ){
                return;
            }
            int left = parent.getPaddingLeft()+20;
            int right = parent.getWidth() - parent.getPaddingRight()-20;
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount-1; i++) {
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}