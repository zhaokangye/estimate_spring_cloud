package com.yezhaokang.estimatebase.core.error;

import com.yezhaokang.estimatebase.core.error.CommonError;

public class BussinessException extends RuntimeException implements CommonError {
    private CommonError commonError;
    public BussinessException(CommonError commonError){
        super();
        this.commonError=commonError;
    }
    public BussinessException(CommonError commonError, String errMsg){
        super();
        this.commonError=commonError;
        this.commonError.setErrMsg(errMsg);
    }
    @Override
    public int getErrCode() {
        return this.commonError.getErrCode();
    }

    @Override
    public String getErrMsg() {
        return this.commonError.getErrMsg();
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        return this.commonError.setErrMsg(errMsg);
    }
}
