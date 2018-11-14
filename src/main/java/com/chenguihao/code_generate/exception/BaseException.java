package com.chenguihao.code_generate.exception;


import com.chenguihao.code_generate.system.SysEnum;

public class BaseException extends RuntimeException {
    private Integer code;

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    public BaseException(String message) {
        super(message);
        this.code = null;
    }
    public BaseException(SysEnum sysEnum) {
        super(sysEnum.getMsg());
        this.code = sysEnum.getCode();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
