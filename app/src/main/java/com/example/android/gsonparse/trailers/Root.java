package com.example.android.gsonparse.trailers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Hugo Valente on 16/03/2018.
 */

public class Root {


    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<VideoResults> results = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public Root() {
    }

    /**
     *
     * @param id
     * @param results
     */
    public Root(Integer id, List<VideoResults> results) {
        super();
        this.id = id;
        this.results = results;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<VideoResults> getResults() {
        return results;
    }

    public void setResults(List<VideoResults> results) {
        this.results = results;
    }

}


