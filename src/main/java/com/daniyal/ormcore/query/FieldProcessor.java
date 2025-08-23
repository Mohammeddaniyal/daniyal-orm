package com.daniyal.ormcore.query;
import com.daniyal.ormcore.pojo.*;
import com.daniyal.ormcore.exceptions.*;

@FunctionalInterface
interface FieldProcessor
{
	void process(FieldMeta fieldMeta,List<String> columns,List<Object> params,StringBuilder placeHolder) throws ORMException;
}