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
}
