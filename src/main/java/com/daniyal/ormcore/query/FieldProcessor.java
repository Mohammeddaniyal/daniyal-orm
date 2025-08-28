package com.daniyal.ormcore.query;
import com.daniyal.ormcore.metadata.*;
import com.daniyal.ormcore.exceptions.*;
import java.util.*;

@FunctionalInterface
interface FieldProcessor
{
	void process(FieldMetaData fieldMetaData,Object validatedValue,List<String> columns,List<Object> params,StringBuilder placeholders) throws ORMException;
}