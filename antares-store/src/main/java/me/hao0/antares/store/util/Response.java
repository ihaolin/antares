package me.hao0.antares.store.util;

import com.google.common.base.Objects;
import java.io.Serializable;

/**
 * Service Response Wrapper
 */
public final class Response<T> implements Serializable {

    private static final long serialVersionUID = 3727205004706510648L;

    public static final Integer OK = 200;

    /**
     * 500
     */
    public static final Integer ERR = 500;

    /**
     * Business error
     */
    public static final Integer BUSINESS_ERR = 1000;

    /**
     * status
     */
    private Integer status;

    /**
     * error message
     */
    private Object err;

    /**
     * data
     */
    private T data;

    public static <T> Response<T> ok(){
        Response r = new Response();
        r.status = OK;
        return r;
    }

    public static <T> Response<T> ok(T data){
        Response r = new Response();
        r.status = OK;
        r.data = data;
        return r;
    }

    public static <T> Response<T> notOk(Object err){
        Response r = new Response();
        r.status = ERR;
        r.err = err;
        return r;
    }

    public static <T> Response<T> notOk(Integer status, Object err){
        Response r = new Response();
        r.status = status;
        r.err = err;
        return r;
    }

    public Boolean isSuccess(){
        return Objects.equal(status, OK);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Object getErr() {
        return err;
    }

    public void setErr(Object err) {
        this.err = err;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        status = OK;
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "status=" + status +
                ", err='" + err + '\'' +
                ", data=" + data +
                '}';
    }
}