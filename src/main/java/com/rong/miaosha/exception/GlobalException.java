package com.rong.miaosha.exception;

import com.rong.miaosha.result.CodeMessage;

public class GlobalException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    private CodeMessage cm;

    public GlobalException(CodeMessage cm) {
        super(cm.toString());
        this.cm = cm;
    }

    public CodeMessage getCm() {
        return cm;
    }

}