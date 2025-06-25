package com.example.computerweb.service.impl;

import com.example.computerweb.model.NetworkLocation;
import com.example.computerweb.response.NetworkLocationResponse;
import com.example.computerweb.service.NetworkLocationService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class NetworkLocationServiceImpl implements NetworkLocationService {
    @Override
    public NetworkLocationResponse getNetworkLocations() {
        List<NetworkLocation> locations = Arrays.asList(
                new NetworkLocation("192.168.95.101", "shareSpace", "lel0958_share", ""),
                new NetworkLocation("192.168.95.101", "学习资料", "lel0958_share", "")
        );
        return new NetworkLocationResponse(locations);
    }
}
