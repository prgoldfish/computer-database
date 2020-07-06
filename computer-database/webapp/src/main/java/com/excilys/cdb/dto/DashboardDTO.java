package com.excilys.cdb.dto;

import java.util.Objects;

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

    @Override
    public int hashCode() {
        return Objects.hash(ascendent, headerMessage, length, order, page, search, selection);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DashboardDTO)) {
            return false;
        }
        DashboardDTO other = (DashboardDTO) obj;
        return Objects.equals(ascendent, other.ascendent) && Objects.equals(headerMessage, other.headerMessage)
                && Objects.equals(length, other.length) && Objects.equals(order, other.order)
                && Objects.equals(page, other.page) && Objects.equals(search, other.search)
                && Objects.equals(selection, other.selection);
    }

    @Override
    public String toString() {
        return "DashboardDTO [page=" + page + ", length=" + length + ", search=" + search + ", headerMessage="
                + headerMessage + ", order=" + order + ", ascendent=" + ascendent + ", selection=" + selection + "]";
    }

}
