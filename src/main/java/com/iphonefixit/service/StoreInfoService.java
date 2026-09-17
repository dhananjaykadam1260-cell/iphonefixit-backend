package com.iphonefixit.service;

import org.springframework.stereotype.Service;

import com.iphonefixit.entity.StoreInfo;
import com.iphonefixit.repository.StoreInfoRepository;

@Service
public class StoreInfoService {

    private final StoreInfoRepository repository;

    public StoreInfoService(
            StoreInfoRepository repository) {
        this.repository = repository;
    }

    public StoreInfo getStoreInfo() {

        return repository
                .findFirstByOrderByIdAsc()
                .orElseGet(() -> {

                    StoreInfo info =
                            new StoreInfo();

                    info.setStoreName(
                            "iPhoneFixit"
                    );

                    return repository.save(info);
                });
    }

    public StoreInfo updateStoreInfo(
            StoreInfo request) {

        StoreInfo info =
                getStoreInfo();

        info.setStoreName(
                request.getStoreName()
        );

        info.setAddress(
                request.getAddress()
        );

        info.setContactNumber(
                request.getContactNumber()
        );

        info.setWhatsappNumber(
                request.getWhatsappNumber()
        );

        info.setEmail(
                request.getEmail()
        );

        info.setOpeningTime(
                request.getOpeningTime()
        );

        info.setClosingTime(
                request.getClosingTime()
        );

        info.setGoogleMapsLink(
                request.getGoogleMapsLink()
        );

        return repository.save(info);
    }
}