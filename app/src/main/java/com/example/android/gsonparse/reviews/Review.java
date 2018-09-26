package com.example.android.gsonparse.reviews;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Hugo Valente on 15/03/2018.
 */

public class Review {


        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("page")
        @Expose
        private Integer page;
        @SerializedName("results")
        @Expose
        private List<ReviewsResult> results = null;
        @SerializedName("total_pages")
        @Expose
        private Integer totalPages;
        @SerializedName("total_results")
        @Expose
        private Integer totalResults;

        /**
         * No args constructor for use in serialization
         *
         */
        public Review() {
        }

        /**
         *
         * @param id
         * @param results
         * @param totalResults
         * @param page
         * @param totalPages
         */
        public Review(Integer id, Integer page, List<ReviewsResult> results, Integer totalPages, Integer totalResults) {
            super();
            this.id = id;
            this.page = page;
            this.results = results;
            this.totalPages = totalPages;
            this.totalResults = totalResults;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getPage() {
            return page;
        }

        public void setPage(Integer page) {
            this.page = page;
        }

        public List<ReviewsResult> getResults() {
            return results;
        }

        public void setResults(List<ReviewsResult> results) {
            this.results = results;
        }

        public Integer getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(Integer totalPages) {
            this.totalPages = totalPages;
        }

        public Integer getTotalResults() {
            return totalResults;
        }

        public void setTotalResults(Integer totalResults) {
            this.totalResults = totalResults;
        }

    }
