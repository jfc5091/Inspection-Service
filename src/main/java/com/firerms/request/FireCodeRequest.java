package com.firerms.request;

import com.firerms.entity.checklists.FireCode;

public class FireCodeRequest {

    private FireCode fireCode;

    public FireCodeRequest() {
    }

    public FireCodeRequest(FireCode fireCode) {
        this.fireCode = fireCode;
    }

    public FireCode getFireCode() {
        return fireCode;
    }
}
