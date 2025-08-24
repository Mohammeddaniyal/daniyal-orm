package com.daniyal.ormcore.validation;
import com.daniyal.ormcore.exceptions.*;
import com.daniyal.ormcore.pojo.*;
import java.util.*;
import java.sql.*;
import java.math.*;

public class EntityValidator
{
	public static Object validateAndConvert(Object value,FieldMetaData fieldMetaData,ColumnMetaData columnMetaData) throws ORMException
	{
		if(value==null)
		{
			if(!columnMetaData.isNullable())
			{
				throw new ORMException("Field '" + fieldMetaData.getField().getName() + "' cannot be null.");
			}
			return null;
		}
		else
		{
			if(value instanceof String)
			{
				int maxSize=columnMetaData.getSize();
				String str=(String)value;
				if(str.length()>maxSize)
				{
					throw new ORMException("Value for field '" + fieldMetaData.getField().getName() + "' exceeds max length of " + maxSize);
				}
			}else if(value instanceof Character)
			{
					value=String.valueOf(value);
			}else if(value instanceof java.util.Date)
			{
				java.util.Date utilDate=(java.util.Date)value;
				String sqlType=columnMetaData.getDataType();
				if("DATE".equals(sqlType))
				{
					return new java.sql.Date(utilDate.getTime());
				}else if("TIMESTAMP".equals(sqlType) || "DATETIME".equals(sqlType))
				{
					return new java.sql.Timestamp(utilDate.getTime());
				}else{
					throw new ORMException("Field '" + fieldMetaData.getField().getName() +"' is Date but column type is '" + sqlType + "'");
				}
			}else if(value instanceof BigDecimal)
			{
				BigDecimal v=(java.math.BigDecimal)value;
				BigDecimal normalized=v.stripTrailingZeros();
				
				int valuePrecision=normalized.precision();
				int valueScale=normalized.scale();
				
				int precision=columnMetaData.getSize();
				int scale=columnMetaData.getScale();
				if (valuePrecision > precision) 
				{
				throw new ORMException("BigDecimal value for field '" + fieldMetaData.getField().getName() +
                "' exceeds the maximum precision allowed (" + precision + "). Actual: " + valuePrecision);
				}
				if (valueScale > scale) 
				{
				throw new ORMException("BigDecimal value for field '" + fieldMetaData.getField().getName() +
                "' exceeds the maximum scale allowed (" + scale + "). Actual: " + valueScale);
				}
			}
			return value;
		}
	}
}