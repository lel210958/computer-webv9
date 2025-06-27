package com.example.computerweb.response;

import com.example.computerweb.model.CategoryCount;

import java.util.List;

public class CategoryCountResponse {
    private Integer networkLocationId;
    private List<CategoryCount> categoryCount;

    public CategoryCountResponse() {}

    public CategoryCountResponse(Integer networkLocationId, List<CategoryCount> categoryCount) {
        this.networkLocationId = networkLocationId;
        this.categoryCount = categoryCount;
    }

    public Integer getNetworkLocationId() {
        return networkLocationId;
    }

    public void setNetworkLocationId(Integer networkLocationId) {
        this.networkLocationId = networkLocationId;
    }

    public List<CategoryCount> getCategoryCount() {
        return categoryCount;
    }

    public void setCategoryCount(List<CategoryCount> categoryCount) {
        this.categoryCount = categoryCount;
    }
} 