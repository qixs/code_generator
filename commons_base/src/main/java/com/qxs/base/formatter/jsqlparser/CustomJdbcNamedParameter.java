package com.qxs.base.formatter.jsqlparser;

import net.sf.jsqlparser.expression.JdbcNamedParameter;

/**
 * @author qixingshen
 * **/
public class CustomJdbcNamedParameter extends JdbcNamedParameter{
    @Override
    public String toString() {
        return getName();
    }
}
