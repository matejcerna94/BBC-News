package com.matejcerna.bbcnews.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.matejcerna.bbcnews.R;
import com.matejcerna.bbcnews.model.Article;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ArticleDetailFragment extends Fragment {
    Context context;
    ArrayList<Article> articlesList;
    //ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    int selectedPosition = 0;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    Unbinder unbinder;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article_detail, container, false);
        ButterKnife.bind(this, view);

        //viewPager = view.findViewById(R.id.view_pager);

        articlesList = (ArrayList<Article>) getArguments().getSerializable("articlesList");
        selectedPosition = getArguments().getInt("position");

        viewPagerAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPosition);


        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float v, int i1) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(articlesList.get(position).getTitle());
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public class ViewPagerAdapter extends PagerAdapter {

        LayoutInflater layoutInflater;

        @BindView(R.id.image_preview)
        ImageView imageViewDetail;
        @BindView(R.id.title_preview)
        TextView titleDetail;
        @BindView(R.id.description_preview)
        TextView descriptionDetail;
        @BindView(R.id.author_preview)
        TextView authorDetail;
        @BindView(R.id.published_at_preview)
        TextView publishedAtDetail;
        @BindView(R.id.url_preview)
        TextView urlDetail;

        public ViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.article_fullscreen_preview, container, false);
            ButterKnife.bind(this, view);
           /* ImageView imageViewDetail = view.findViewById(R.id.image_preview);
            TextView titleDetail = view.findViewById(R.id.title_preview);
            TextView descriptionDetail = view.findViewById(R.id.description_preview);
            TextView authorDetail = view.findViewById(R.id.author_preview);
            TextView urlDetail = view.findViewById(R.id.url_preview);
            TextView publishedAtDetail = view.findViewById(R.id.published_at_preview);*/

            Article article = articlesList.get(position);
            titleDetail.setText(article.getTitle());
            descriptionDetail.setText(article.getDescription());
            authorDetail.setText(article.getAuthor());
            urlDetail.setText(article.getUrl());
            publishedAtDetail.setText(article.getPublishedAt());

            Picasso.with(context).load(article.getUrlToImage()).into(imageViewDetail);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return articlesList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == ((View) o);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
