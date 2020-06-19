package com.excilys.cdb.persistence;

public enum OrderByColumn {

    COMPUTERID("computer.id"), COMPUTERNAME("computer.name"), COMPUTERINTRO("computer.introduced"),
    COMPUTERDISCONT("computer.discontinued"), COMPANYID("company.id"), COMPANYNAME("company.name");

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
