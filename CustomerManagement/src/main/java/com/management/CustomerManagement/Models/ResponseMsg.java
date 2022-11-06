package com.management.CustomerManagement.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseMsg {
    private String status;
    private List<String> errors;
    private Map<String, Object> responseMsg;

    public ResponseMsg() {
    }

    public ResponseMsg(String status) {
        this.status = status;
        this.responseMsg = new HashMap<>();
        this.errors = new ArrayList<>();
    }

    public ResponseMsg(String status, List<String> errors) {
        this.status = status;
        this.errors = errors;
        responseMsg = new HashMap<>();
        responseMsg.put("Status", this.status);
        responseMsg.put("errors", errors);
    }

    public Map<String, Object> getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(Map<String, Object> responseMsg) {
        this.responseMsg = responseMsg;
    }

    public void setMsg(String label, Object msg){
        responseMsg.put(label, msg);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public void addError(String error){
        errors.add(error);
    }

    @Override
    public String toString() {
        return "{" +
                "status='" + status + '\'' +
                ", errors=" + errors +
                ", responseMsg=" + responseMsg +
                '}';
    }
}
