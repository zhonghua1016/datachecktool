package com.example.datacheck.dto;

import com.example.datacheck.constant.CommonConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 响应信息主体
 *
 */
@Builder
@ToString
@Accessors(chain = true)
@AllArgsConstructor
public class R<T> implements Serializable {
	private static final long serialVersionUID = -2130804516060897152L;

	private int code = CommonConstants.SUCCESS;

	private String msg = "success";

	private T data;

	public R() {
		super();
	}

	public R(T data) {
		super();
		this.data = data;
	}

	public R(T data, String msg) {
		super();
		this.data = data;
		this.msg = msg;
	}

	public R(Throwable e) {
		super();
		this.msg = e.getMessage();
		this.code = CommonConstants.FAIL;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public void setResultCode(IResultCode code) {
		if (code != null) {
			this.code = code.getCode();
			this.msg = code.getMessage();
		}
	}
	public static <T> R<T> resultCode(IResultCode code, T data) {
		R<T> r = new R<>();
		r.setResultCode(code);
		r.setData(data);
		return r;
	}

	public static <T> R<T> ok(T data) {
		IResultCode code = ResultCode.CODE_OK;
		if (data instanceof Boolean && Boolean.FALSE.equals(data)) {
			code = ResultCode.CODE_FAIL;
		}
		return resultCode(code, data);
	}

	public static R ok() {
		R r = new R<>();
		r.setResultCode(ResultCode.CODE_OK);
		return r;
	}

	public static R error(int code, String msg) {
		R r = new R<>();
		r.setCode(code);
		r.setMsg(msg);
		return r;
	}

	public static R error(String msg) {
		R r = new R<>();
		r.setCode(ResultCode.CODE_FAIL.getCode());
		r.setMsg(msg);
		return r;
	}


	public static <T> R<T> resultCode(IResultCode code) {
		R<T> r = new R<>();
		r.setResultCode(code);
		return r;
	}



	@JsonIgnore
	public boolean isOk() {
		return CommonConstants.SUCCESS.equals(this.code);
	}
}
