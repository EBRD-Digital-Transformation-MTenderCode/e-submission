package com.procurement.submission.service;

import org.springframework.stereotype.Service;

@Service
public interface RulesService {

    int getInterval(String country, String method);

    int getRulesMinBids(String country, String method);
}
