package com.qxs.base.formatter.jsqlparser;

import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.alter.AlterExpression;
import net.sf.jsqlparser.statement.alter.AlterExpression.ColumnDataType;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.util.List;

/**
 * @author qixingshen
 * **/
public class SetStatementFormatter {
	
	public String format(Alter alter) {
		StringBuilder b = new StringBuilder();
        b.append("ALTER TABLE ").append(alter.getTable().getFullyQualifiedName()).append(" ");

        List<AlterExpression> altList = alter.getAlterExpressions();
        
        if(altList.size() > 1 || altList.get(0).getColDataTypeList().size() > 1) {
        	b.append("\r\n");
        }
        
        for(int i = 0 , length = altList.size() ; i < length ;i ++) {
        	AlterExpression alt = altList.get(i);
            b.append(formatAlterExpression(alt));

            if (i != length - 1) {
                b.append(",\r\n");
            }
        }

        return b.toString();
	}
	
	private String formatAlterExpression(AlterExpression alt) {
		StringBuilder b = new StringBuilder();

        b.append(alt.getOperation()).append(" ");

        if (alt.getColumnName() != null) {
            b.append("COLUMN ").append(alt.getColumnName());
        } else if (alt.getColDataTypeList() != null) {
            if (alt.getColDataTypeList().size() > 1) {
                b.append("(\r\n");
            } else {
                b.append("COLUMN ");
            }
            b.append(formatColDataTypeList(alt.getColDataTypeList(), true, false));
            if (alt.getColDataTypeList().size() > 1) {
                b.append("\r\n)");
            }
        } else if (alt.getConstraintName() != null) {
            b.append("CONSTRAINT ").append(alt.getConstraintName());
        } else if (alt.getPkColumns() != null) {
            b.append("PRIMARY KEY (").append(PlainSelect.getStringList(alt.getPkColumns())).append(')');
        } else if (alt.getUkColumns() != null) {
            b.append("UNIQUE KEY ").append(alt.getUkName()).append(" (").append(PlainSelect.
                    getStringList(alt.getUkColumns())).append(")");
        } else if (alt.getFkColumns() != null) {
            b.append("FOREIGN KEY (").append(PlainSelect.getStringList(alt.getFkColumns())).
                    append(") REFERENCES ").append(alt.getFkSourceTable()).append(" (").append(
                    PlainSelect.getStringList(alt.getFkSourceColumns())).append(")");
            if (alt.isOnDeleteCascade()) {
                b.append(" ON DELETE CASCADE");
            } else if (alt.isOnDeleteRestrict()) {
                b.append(" ON DELETE RESTRICT");
            } else if (alt.isOnDeleteSetNull()) {
                b.append(" ON DELETE SET NULL");
            }
        } else if (alt.getIndex() != null) {
            b.append(alt.getIndex());
        }
        if (alt.getConstraints() != null && !alt.getConstraints().isEmpty()) {
            b.append(' ').append(PlainSelect.getStringList(alt.getConstraints(), false, false));
        }

        return b.toString();
	}
	
	private String formatColDataTypeList(List<ColumnDataType> list, boolean useComma, boolean useBrackets) {
		StringBuilder ans = new StringBuilder();
//      String ans = "";
      String comma = ",\r\n";
      if (!useComma) {
          comma = "";
      }
      if (list != null) {
          if (useBrackets) {
              ans.append("(");
//              ans += "(";
          }

          for (int i = 0; i < list.size(); i++) {
              ans.append(i == 0 ? " " : "").append(list.get(i)).append((i < list.size() - 1) ? comma + " " : "");
//              ans += "" + list.get(i) + ((i < list.size() - 1) ? comma + " " : "");
          }

          if (useBrackets) {
              ans.append(")");
//              ans += ")";
          }
      }

      return ans.toString();
	}
}
