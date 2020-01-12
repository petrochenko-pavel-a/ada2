package com.onpositive.analitics.model;

import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;

public class Builtins {

	private static final class ALL implements IType,IClass {
		@Override
		public String name() {
			return "them";
		}

		@Override
		public boolean isOrdered() {
			return false;
		}

		@Override
		public boolean isSummable() {
			return false;
		}

		@Override
		public boolean isSubtypeOf(IType domain) {
			if (domain instanceof IClass){
				return true;
			}
			return false;
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<IProperty> properties() {
			return Collections.emptyList();
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<IProperty> allProperties() {
			return Collections.emptyList();
		}

		@Override
		public boolean isPartOf(IClass b) {
			return false;
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<IClass> contained() {
			return Collections.emptyList();
		}

		@Override
		public IProperty keyProperty() {
			return null;
		}

		@Override
		public BiPredicate<Object, Object> containing(IClass c) {
			return null;
		}
	}

	public static final BuiltinType NUMBER=new BuiltinType("number",true);


	public static final BuiltinType STRING=new BuiltinType("number",false);

	
	public static final BuiltinType INTEGER=new BuiltinType("integer",true,NUMBER);
	public static final BuiltinType FLOAT=new BuiltinType("float",true,NUMBER);
	
	public static final BuiltinType BOOLEAN=new BuiltinType("boolean",false);

	public static final BuiltinType DATETIME=new BuiltinType("datetime",true);

	public static final BuiltinType DATE=new BuiltinType("date",true,DATETIME);

	public static final BuiltinType TIME=new BuiltinType("time",true,DATETIME);

	public static final BuiltinType DURATION=new BuiltinType("duration",true);
	
	
	public static IType ALLMATCH=new ALL();
	
	static{
		NUMBER.setSummable(true);
	}

}
