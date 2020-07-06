package com.excilys.cdb.persistence;

public enum OrderByColumn {

    COMPUTERID("id"), COMPUTERNAME("name"), COMPUTERINTRO("introduced"), COMPUTERDISCONT("discontinued"),
    COMPANYID("id"), COMPANYNAME("name");

    private String columnName;

    private OrderByColumn(String cName) {
        columnName = cName;
    }

    public String getColumnName() {
        return columnName;
    }

    public static OrderByColumn getEnum(String columnName) {
        if (columnName == null) {
            return OrderByColumn.COMPUTERID;
        }
        switch (columnName) {
        case "ComputerName":
            return OrderByColumn.COMPUTERNAME;
        case "IntroducedDate":
            return OrderByColumn.COMPUTERINTRO;
        case "DiscontinuedDate":
            return OrderByColumn.COMPUTERDISCONT;
        case "CompanyName":
            return OrderByColumn.COMPANYNAME;
        case "CompanyId":
            return OrderByColumn.COMPANYID;
        default:
            return OrderByColumn.COMPUTERID;
        }
    }
}
