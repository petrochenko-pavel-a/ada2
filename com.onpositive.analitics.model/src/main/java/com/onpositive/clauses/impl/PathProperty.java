package com.onpositive.clauses.impl;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.onpositive.analitics.model.ActionProperty;
import com.onpositive.analitics.model.IClass;
import com.onpositive.analitics.model.IProperty;
import com.onpositive.analitics.model.IType;

public class PathProperty implements IProperty{

	protected List<IProperty>path;

	public List<IProperty> getPath() {
		return path;
	}

	public PathProperty(List<IProperty> path) {
		this.path = path;
	}

	@Override
	public String name() {
		return path.toString();
	}

	@Override
	public IClass domain() {
		return path.get(0).domain();
	}

	@Override
	public IType range() {
		return path.get(path.size()-1).range();
	}

	@Override
	public String id() {
		return path.stream().map(x->x.id()).collect(Collectors.toList()).toString();
	}
	
	@Override
	public int complexity() {
		int sum=0;
		if (path.stream().allMatch(x-> x instanceof ContainingProperty)){
			return 1;
		}
		for (IProperty p:path){
			sum+=p.complexity();
		}
		int s=sum;
		for (int i=0;i<path.size();i++){
			s=s*sum;
		}
		return s*2;
	}
	
	@Override
	public String toString() {
		return "PathProp:("+path.toString()+")";
	}
	
	@Override
	public boolean multiValue() {
		return path.stream().anyMatch(x->x.multiValue());
	}

	@Override
	public Object getValue(Object obj) {
		Object curr=obj;
		for (IProperty cp:path) {
			if (curr==null) {
				return null;
			}
			if (curr instanceof Collection) {
				LinkedHashSet<Object>rs=new LinkedHashSet<>();
				Collection<Object>cv=(Collection<Object>) curr;
				cv.forEach(e->{
					Object value = cp.getValue(e);
					if (value instanceof Collection) {
						rs.addAll((Collection<? extends Object>) value);
					}
					else {
						if (value!=null) {
							rs.add(value);
						}
					}
				});
				if (rs.size()==1) {
					curr=rs.iterator().next();
				}
				else if (rs.isEmpty()) {
					return null;
				}
				else {
					curr=rs;
				}
				
			}
			else {
				curr=cp.getValue(curr);
			}
		}
		return curr;
	}
}
