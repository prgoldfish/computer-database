package com.excilys.cdb.dto;

public class DashboardDTO {
    private String page = "1";
    private String length = "10";
    private String search;
    private String headerMessage;
    private String order;
    private String ascendent;
    private String selection;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getHeaderMessage() {
        return headerMessage;
    }

    public void setHeaderMessage(String headerMessage) {
        this.headerMessage = headerMessage;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getAscendent() {
        return ascendent;
    }

    public void setAscendent(String ascendent) {
        this.ascendent = ascendent;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

}
