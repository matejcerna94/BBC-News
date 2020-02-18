package com.matejcerna.bbcnews.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.matejcerna.bbcnews.R;
import com.matejcerna.bbcnews.adapter.ArticleAdapter;
import com.matejcerna.bbcnews.model.Article;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ArticlesFragment extends Fragment implements ArticleAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private ArticleAdapter articleAdapter;
    private RequestQueue requestQueue;
    List<Article> articlesList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_articles, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Factory News");

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        articlesList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getActivity());
        parseJSON();
        return view;
    }

    private void parseJSON() {
        String url = "https://newsapi.org/v1/articles?source=bbc-news&sortBy=top&apiKey=6946d0c07a1c4555a4186bfcade76398";
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(String.valueOf(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray jsonArray = null;
                try {
                    jsonArray = jsonObject.getJSONArray("articles");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    Article article = null;
                    try {
                        article = gson.fromJson(jsonArray.get(i).toString(), Article.class);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    articlesList.add(article);
                }


                articleAdapter = new ArticleAdapter(getContext(), (ArrayList<Article>) articlesList);
                recyclerView.setAdapter(articleAdapter);
                articleAdapter.setOnItemClickListener(new ArticleAdapter.OnItemClickListener() {
                    @Override
                    public void onCreateView(Bundle savedInstanceState) {

                    }

                    @Override
                    public void onItemClick(int position) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("articlesList", (Serializable) articlesList);
                        bundle.putInt("position", position);

                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        ArticleDetailFragment secondFragment = new ArticleDetailFragment();
                        secondFragment.setArguments(bundle);

                        fragmentTransaction.replace(R.id.frame_container, secondFragment);
                        fragmentTransaction.addToBackStack(null).commit();
                    }
                });

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setMessage("Ups, došlo je do pogreške.").setCancelable(false)
                        .setPositiveButton("U redu", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                progressDialog.dismiss();
                            }


                        });
                AlertDialog alert = alertDialog.create();
                alert.setTitle("Greška");
                alert.show();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                Response<JSONObject> resp = super.parseNetworkResponse(response);
                if (!resp.isSuccess()) {
                    return resp;
                }
                long now = System.currentTimeMillis();
                Cache.Entry entry = resp.cacheEntry;
                if (entry == null) {
                    articlesList.clear();
                    entry = new Cache.Entry();
                    entry.data = response.data;
                    entry.responseHeaders = response.headers;
                }
                entry.ttl = now + 300 * 1000;  //keeps cache for 5 min

                return Response.success(resp.result, entry);
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onCreateView(Bundle savedInstanceState) {

    }

    @Override
    public void onItemClick(int position) {

    }
}
