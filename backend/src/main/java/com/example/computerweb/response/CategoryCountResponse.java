package com.example.computerweb.response;

import com.example.computerweb.model.CategoryCount;
import com.example.computerweb.model.NetworkLocation;

import java.util.List;

public class CategoryCountResponse {
    private NetworkLocation networkLocation;
    private List<CategoryCount> categoryCount;

    public CategoryCountResponse() {}

    public CategoryCountResponse(NetworkLocation networkLocation, List<CategoryCount> categoryCount) {
        this.networkLocation = networkLocation;
        this.categoryCount = categoryCount;
    }

    public NetworkLocation getNetworkLocation() {
        return networkLocation;
    }

    public void setNetworkLocation(NetworkLocation networkLocation) {
        this.networkLocation = networkLocation;
    }

    public List<CategoryCount> getCategoryCount() {
        return categoryCount;
    }

    public void setCategoryCount(List<CategoryCount> categoryCount) {
        this.categoryCount = categoryCount;
    }
} 