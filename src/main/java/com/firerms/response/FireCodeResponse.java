package com.firerms.response;

import com.firerms.entity.checklists.FireCode;

public class FireCodeResponse {

    private FireCode fireCode;

    public FireCodeResponse(FireCode fireCode) {
        this.fireCode = fireCode;
    }

    public FireCode getFireCode() {
        return fireCode;
    }
}
