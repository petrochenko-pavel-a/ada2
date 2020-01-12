package com.ada.logic;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.ada.model.AndComparison;
import com.ada.model.Comparative;
import com.ada.model.Comparative.Kind;
import com.ada.model.Comparison;
import com.ada.model.GenericTime;
import com.ada.model.IScalarWithDimension;
import com.ada.model.Measure;
import com.ada.model.NumberInDomain;
import com.ada.model.OrComparison;
import com.ada.model.Preposition;
import com.ada.model.PropertyComparison;
import com.ada.model.PropertyValue;
import com.ada.model.SimpleRelativeComparison;
import com.ada.model.TopValues;
import com.ada.model.conditions.IHasDomain;
import com.onpositive.analitics.model.ActionProperty;
import com.onpositive.analitics.model.Builtins;
import com.onpositive.analitics.model.IClass;
import com.onpositive.analitics.model.ICompoundProperty;
import com.onpositive.analitics.model.IProperty;
import com.onpositive.analitics.model.IType;
import com.onpositive.analitics.model.java.TimeProp;
import com.onpositive.clauses.IClause;
import com.onpositive.clauses.IComparison;
import com.onpositive.clauses.ISelector;
import com.onpositive.clauses.Multiplicity;
import com.onpositive.clauses.impl.Aggregators;
import com.onpositive.clauses.impl.AllInstancesOf;
import com.onpositive.clauses.impl.AndSelector;
import com.onpositive.clauses.impl.Clause;
import com.onpositive.clauses.impl.ClauseSelector;
import com.onpositive.clauses.impl.ContainmentClause;
import com.onpositive.clauses.impl.InverseProperty;
import com.onpositive.clauses.impl.MapByProperty;
import com.onpositive.clauses.impl.NumberProperty;
import com.onpositive.clauses.impl.OrSelector;
import com.onpositive.clauses.impl.PathProperty;
import com.onpositive.clauses.impl.PropertyFilter;
import com.onpositive.clauses.impl.PropertyFilter2;
import com.onpositive.clauses.impl.QuestionInstances;
import com.onpositive.clauses.impl.SingleSelector;

public class BasicLogic {

	@Clause("TAKE_FIRST")
	public static ISelector takeFirst(ISelector s) {
		Kind min = Comparative.Kind.MIN;
		int limit = 1;
		return takeN(s, min, limit);
	}

	@Clause("TAKE_LAST")
	public static ISelector takeLast(ISelector s) {
		Kind min = Comparative.Kind.MAX;
		int limit = 1;
		return takeN(s, min, limit);
	}

	@Clause("TAKE_FIRST_N")
	public static ISelector takeFirstN(ISelector s, Number limit) {
		Kind min = Comparative.Kind.MIN;
		return takeN(s, min, limit.intValue());
	}

	@Clause("TAKE_LAST_N")
	public static ISelector takeLastN(ISelector s, Number limit) {
		Kind min = Comparative.Kind.MAX;
		return takeN(s, min, limit.intValue());
	}

	private static ISelector takeN(ISelector s, Kind min, int limit) {
		IClass cl = (IClass) s.domain();
		TimeProp annotation = cl.annotation(TimeProp.class);
		if (annotation != null) {
			String value = annotation.value();
			IProperty ps = cl.property(value).get();
			TopValues topValues = new TopValues(ps, min);
			topValues.setLimit(limit);
			ISelector produce = topValues.produce(s);
			return produce;
		}
		return null;
	}

	@Clause("COMP_S")
	public static Object proc(ISelector s, IComparison ca) {
		IType domain = ca.domain();
		if (ca.domain().equals(s.domain())) {
			return partOf(s, ca);
		}
		return null;

	}

	@Clause("COMPARISON_NUMB2")
	public static IClause nextDate(Number n, IProperty p) {
		if (p.range().equals(Builtins.INTEGER) || p.range().equals(Builtins.NUMBER)) {
			return PropertyFilter.propertyFilter(p,
					new Comparison(new NumberInDomain(p.range(), n), new Comparative(Kind.EQUAL, "eq")));
		}
		return null;
	}

	@Clause("TOPN")
	public static ISelector topN(Number n, ISelector s) {
		if (s instanceof ClauseSelector) {
			ClauseSelector sm = (ClauseSelector) s;
			if (sm.clause() instanceof TopValues) {
				TopValues mm = (TopValues) sm.clause();
				mm.setLimit(n.intValue());
			}
			return s;
		}
		return null;
	}

	@Clause("OWNED2")
	public static IHasDomain ownedEntities(SingleSelector s, ISelector sel) {
		if (s.clazz().isPresent() && sel.clazz().isPresent()) {
			IProperty findPath = PropertyAdapter.findPath(s.clazz().get(), sel.clazz().get());
			if (findPath != null) {
				MapByProperty map = MapByProperty.map(findPath);
				if (sel instanceof ClauseSelector) {
					ClauseSelector cla = (ClauseSelector) sel;
					ClauseSelector root = cla.getRoot();
					ISelector parent = root.parent();
					if (parent instanceof AllInstancesOf && sel.domain().equals(parent.domain())) {
						return cla.cloneWithNewRoot(map.produce(s));
					}
				} else if (sel instanceof AllInstancesOf) {
					return map.produce(s);
				}
			}
		}
		return null;
	}

	@Clause("COLLAPSE_PROPVAL")
	public static ISelector collapsePropVal(PropertyValue p, ISelector s) {
		if (p.getProperty().domain().equals(s.domain())) {
			PropertyFilter propertyFilter = PropertyFilter.propertyFilter(p.getProperty(),
					new Comparison(p, new Comparative(Kind.IN, "in")));
			return propertyFilter.produce(s);
		}
		return null;
	}

	@Clause("COLLAPSE_ENTITY")
	public static ISelector collapse(ISelector s0, AllInstancesOf clazz) {
		if (s0.domain().isSubtypeOf(clazz.domain())) {
			if (clazz instanceof QuestionInstances) {
				return null;
			}
			return s0;
		}
		return null;
	}
	@Clause("CS_Q")
	public static Object collapse(ISelector s0,ISelector s1) {
		if (s0 instanceof QuestionInstances) {
			IType domain = s0.domain();
			
			IType domain2 = s1.domain();
			if (domain2 instanceof IClass&&!domain.equals(domain2)) {
				IClass ms=(IClass) domain2;
				for (IProperty aa:ms.allProperties()) {
					if (aa.range().equals(domain)) {
						MapByProperty mapByProperty = new MapByProperty(aa);
						mapByProperty.freezeMap();
						ISelector produce = mapByProperty.produce(s1);
						return produce;
					}
				}
			}
		}
		return null;
	}
	@Clause("CS_Q2")
	public static Object collapse(ISelector s0,ISelector s1,IComparison mm) {
		if (s0 instanceof QuestionInstances) {
			IType domain = s0.domain();
			
			IType domain2 = s1.domain();
			
		}
		return null;
	}
	

	@Clause("MEASURE")
	public static Measure dim(Number n, ISelector sel) {
		if (sel instanceof AllInstancesOf) {
			return new Measure(n, sel);
		}
		return null;
	}

	// PROPERTY_COMPARATIVE
	@Clause("PROPERTY_COMPARATIVE")
	public static IComparison cmp(Comparative cmp, IProperty p) {
		if (cmp.getOperation() == Kind.MIN || cmp.getOperation() == Kind.MAX) {
			return new PropertyComparison(cmp, p);
		}
		return null;
	}

	@Clause("COMPARISON_NUMB")
	public static IClause cmpNump(Comparative cmp, Number n, IProperty p) {
		if (p.range().equals(Builtins.NUMBER) || p.range().equals(Builtins.INTEGER)) {
			return PropertyFilter.propertyFilter(p, new Comparison(new NumberInDomain(p.range(), n), cmp));
		}
		return null;
	}

	@Clause("OR")
	public static ISelector or(List<ISelector> prop) {
		return OrSelector.or(prop);
	}

	@Clause("AND")
	public static ISelector and(List<ISelector> prop) {
		LinkedHashSet<IType> tp = new LinkedHashSet<>();
		for (ISelector s : prop) {
			if (!(s instanceof SingleSelector)) {
				return null;
			}
			tp.add(s.domain());
		}
		if (tp.size() > 1) {
			return null;
		}
		return AndSelector.and(prop);
	}

	@Clause("TAKE_PROPERTY")
	public static Object takeProperty(IProperty prop, ISelector s) {
		if (!s.mappable()) {
			return null;
		}
		if (s instanceof AllInstancesOf) {
			if (s.domain().equals(prop.range())) {
				return prop;
			}
		}
		if (!prop.canMap()) {
			if (!(s instanceof SingleSelector)) {
				return null;
			}
		}
		if (prop instanceof PropertyFilter) {
			return null;
		}
		if (s instanceof ClauseSelector) {
			ClauseSelector sm = (ClauseSelector) s;
			if (sm.clause() instanceof MapByProperty) {
				MapByProperty mp = (MapByProperty) sm.clause();
				if (mp.property().equals(prop)) {
					return null;
				}
			}
		}

		if (s.domain().isSubtypeOf(prop.domain())) {
			return MapByProperty.map(prop).produce(s);
		}
		if (prop.range() instanceof IClass) {
			IProperty inverseProperty = InverseProperty.createInverseProperty(prop);
			if (inverseProperty != null) {
				if (s.domain().isSubtypeOf(inverseProperty.domain())) {
					return MapByProperty.map(inverseProperty).produce(s);
				}
			}
		}
		IProperty adaptProperty = PropertyAdapter.adaptProperty(prop, s.domain());
		if (adaptProperty != null) {
			return new MapByProperty(adaptProperty).produce(s);
		}
		
		return null;
	}

	@Clause("NUMP")
	public static IProperty numberOf(IProperty prop) {
		if (prop.range().equals(Builtins.INTEGER)) {
			return prop;
		}
		if (prop instanceof PropertyFilter) {
			PropertyFilter m = (PropertyFilter) prop;

			PropertyFilter2 flt2 = PropertyFilter2.propertyFilter(m.property(), m.getPredicate());
			return new NumberProperty(flt2);
		}
		if (prop instanceof PathProperty) {
			PathProperty pa = (PathProperty) prop;
			if (pa.getPath().get(0) instanceof PropertyFilter) {
				PropertyFilter m = (PropertyFilter) pa.getPath().get(0);

				PropertyFilter2 flt2 = PropertyFilter2.propertyFilter(m.property(), m.getPredicate());
				if (flt2.range().equals(m.domain())) {
					ArrayList<IProperty> path = new ArrayList<>();
					path.add(flt2);
					path.addAll(pa.getPath().subList(1, pa.getPath().size()));
					PathProperty pm = new PathProperty(path);
					return new NumberProperty(pm);
				}
			}

		}
		return new NumberProperty(prop);
	}

	@Clause("NUMP2")
	public static ISelector numberOf(ISelector prop) {
		return Aggregators.COUNT.produce(prop);
	}

	@Clause("HAVING")
	public static ISelector partOf(ISelector base, IHasDomain parts) {

		if (base.clazz().isPresent() && parts.clazz().isPresent()) {
			IClass iClass = parts.clazz().get();
			BiPredicate<Object, Object> containing = base.clazz().get().containing(iClass);

			IProperty ps = PropertyAdapter.findContainmentPath(base.clazz().get(), iClass);
			if (ps != null) {
				if (parts instanceof PropertyComparison) {
					// we are here;
					PropertyComparison mm = (PropertyComparison) parts;
					IProperty prop = mm.getProp();
					ArrayList<IProperty> mms = new ArrayList<>();
					mms.add(ps);
					mms.add(prop);
					return ClauseSelector.produce(base, base.domain(), Multiplicity.SINGLE,
							new TopValues(new PathProperty(mms), mm.getCmp().getOperation()));
				}
				return new ContainmentClause(parts, false, ps).produce(base);
			}
		}

		if (base.domain() instanceof IClass && !(base instanceof SingleSelector)) {
			if (base.domain().equals(parts.domain()) && parts instanceof PropertyComparison) {
				PropertyComparison propertyComparison = (PropertyComparison) parts;
				return new TopValues(propertyComparison.getProp(), propertyComparison.getCmp().getOperation())
						.produce(base);
			}
			if (parts instanceof Comparison) {
				Comparison propertyComparison = (Comparison) parts;
				IHasDomain target = propertyComparison.getTarget();
				if (target.originalDomain().equals(base.domain())) {
					if (target instanceof ClauseSelector) {
						ClauseSelector mmm = (ClauseSelector) target;
						IClause clause = mmm.getClause();
						if (clause instanceof MapByProperty) {
							MapByProperty mp = (MapByProperty) clause;
							return ClauseSelector.produce(base, base.domain(), Multiplicity.MULTIPLE,
									PropertyFilter.propertyFilter(mp.property(), propertyComparison));
						}
					}
				}

			}
			if (parts instanceof IComparison) {
				IComparison cm = (IComparison) parts;
				if (!cm.isGoodForContainment()) {
					return null;
				}
			}
			if (has(base.properties(), x -> {
				IType domain = parts.domain();
				return x.hasCompatibleRange(domain);
			}) && parts.clazz().isPresent()) {
				IClass iClass = parts.clazz().get();
				IProperty findPath = PropertyAdapter.findPath(iClass, base.clazz().get());
				IProperty createInverseProperty = InverseProperty
						.createInverseProperty(findPath);
				if (createInverseProperty != null) {
					return new ContainmentClause(parts, false, createInverseProperty).produce(base);
				}
			}
			if (has(parts.properties(), x -> x.hasCompatibleRange(base.domain()))) {
				IClass iClass = parts.clazz().get();
				IProperty findPath = PropertyAdapter.findPath(base.clazz().get(), iClass);
				if (findPath != null) {
					return new ContainmentClause(parts, false, findPath).produce(base);
				}
			}
		}
		return null;
	}

	@Clause("CONTAINED_SC")
	public static ISelector contained(ISelector base, IScalarWithDimension owner) {
		List<IProperty> properties = base.properties().stream().filter(x -> x.range().isSubtypeOf(owner.domain()))
				.collect(Collectors.toList());
		if (!properties.isEmpty()) {
			if (properties.size() == 1) {
				PropertyFilter vv = PropertyFilter.propertyFilter(properties.get(0),
						new Comparison(owner, new Comparative(Kind.IN, "in")));
				return vv.produce(base);
			}
			List<IProperty> usedProperties = base.usedProperties();
//			for (IProperty p : usedProperties) {
//				if (p instanceof IProperty) {
//					Property mm = (Property) p;
//					List<IProperty> related = mm.getRelated();
//					properties.retainAll(related);
//					if (properties.size() == 1) {
//						PropertyFilter vv = PropertyFilter.propertyFilter(properties.get(0),
//								new Comparison(owner, new Comparative(Kind.IN, "in")));
//						return vv.produce(base);
//					}
//				}
//			}
		}
		return null;
	}

	@Clause("LAST_RESORT")
	public static IHasDomain lastResoirt(ISelector base, IComparison c) {
		if (base instanceof ClauseSelector) {
			ClauseSelector sm = (ClauseSelector) base;
			IClause clause = sm.clause();
			if (clause instanceof PropertyFilter) {
				PropertyFilter p1 = (PropertyFilter) clause;
				IType range = p1.property().range();
				if (range instanceof IClass) {
					TimeProp annotation = ((IClass) range).annotation(TimeProp.class);
					if (annotation != null) {
						IProperty prop = ((IClass) range).property(annotation.value()).get();

						IComparison predicate = p1.getPredicate();
						ArrayList<IComparison> cm = new ArrayList<>();
						cm.add(predicate);
						cm.add(c);
						AndComparison co = AndComparison.and(cm, predicate.domain());
						p1.setPredicate(co);
						return sm;
					}
				}
			}

		}
		return null;
	}

	@Clause("DATE_S1")
	public static IHasDomain inTime(IHasDomain base, Preposition p, GenericTime time) {
		IType domain = base.domain();
		if (!(domain instanceof IClass)) {
			return null;
		}
		IClass cm = (IClass) domain;
		TimeProp annotation = (cm).annotation(TimeProp.class);
		if (annotation != null) {
			if (base instanceof ClauseSelector) {

				IProperty prop = ((IClass) cm).property(annotation.value()).get();

				Comparative comparative = null;
				if (p.getText().equals("in")) {
					comparative = new Comparative(Comparative.Kind.IN, "in");
				}
				if (p.getText().equals("before")) {
					comparative = new Comparative(Comparative.Kind.LESS, "before");
				}
				if (p.getText().equals("after")) {
					comparative = new Comparative(Comparative.Kind.MORE, "after");
				}
				if (comparative != null) {
					PropertyFilter m = PropertyFilter.propertyFilter(prop, new Comparison(time, comparative));

					ClauseSelector original = (ClauseSelector) base;
					ClauseSelector produce = ClauseSelector.produce(((ClauseSelector) base).parent(), base.domain(),
							Multiplicity.MULTIPLE, m);
					if (produce != null) {
						return ClauseSelector.produce(produce, base.domain(), Multiplicity.MULTIPLE, original.clause());
					}
				}
			}
		}
		return null;
	}

	@Clause("DATE_S")
	public static IHasDomain inTime(IHasDomain base, GenericTime time) {
		IType domain = base.domain();
		if (base instanceof IComparison) {
			IComparison p1 = (IComparison) base;
			IType range = p1.domain();
			if (range instanceof IClass) {
				TimeProp annotation = ((IClass) range).annotation(TimeProp.class);
				if (annotation != null) {
					IProperty prop = ((IClass) range).property(annotation.value()).get();

					ArrayList<IComparison> cm = new ArrayList<>();
					cm.add(p1);
					PropertyComparison m = new PropertyComparison(
							new Comparison(time, new Comparative(Comparative.Kind.IN, "in")), prop);
					cm.add(m);
					AndComparison co = AndComparison.and(cm, p1.domain());
					return co;
				}
			}
		}
		if (domain instanceof IClass && base instanceof ISelector) {
			ISelector s = (ISelector) base;
			TimeProp annotation = domain.annotation(TimeProp.class);
			if (annotation != null) {
				Optional<IProperty> property = ((IClass) domain).property(annotation.value());
				return PropertyFilter
						.propertyFilter(property.get(),
								new Comparison(time, new Comparative(Comparative.Kind.IN, "in")))
						.produce((ISelector) base);
			}
		}
		if (base instanceof ClauseSelector) {
			ClauseSelector sm = (ClauseSelector) base;
			IClause clause = sm.clause();
			if (clause instanceof PropertyFilter) {
				PropertyFilter p1 = (PropertyFilter) clause;
				IType range = p1.property().range();
				if (range instanceof IClass) {
					TimeProp annotation = ((IClass) range).annotation(TimeProp.class);
					if (annotation != null) {
						IProperty prop = ((IClass) range).property(annotation.value()).get();

						IComparison predicate = p1.getPredicate();
						ArrayList<IComparison> cm = new ArrayList<>();
						cm.add(predicate);
						PropertyComparison m = new PropertyComparison(
								new Comparison(time, new Comparative(Comparative.Kind.IN, "in")), prop);
						cm.add(m);
						AndComparison co = AndComparison.and(cm, predicate.domain());
						p1.setPredicate(co);
						return sm;
					}
				}
			}

		}
		return null;
	}

	@Clause("INVERSE_COMPARISON")
	public static IComparison INVERSE_COMPARISON(IComparison c) {
		return c.negate();
	}

	@Clause("DATE_B")
	public static ISelector beforeTime(ISelector base, GenericTime time) {
		IType domain = base.domain();
		if (domain instanceof IClass) {
			TimeProp annotation = domain.annotation(TimeProp.class);
			if (annotation != null) {
				Optional<IProperty> property = ((IClass) domain).property(annotation.value());
				return PropertyFilter.propertyFilter(property.get(),
						new Comparison(time, new Comparative(Comparative.Kind.LESS, "before"))).produce(base);
			}
		}
		if (base instanceof ClauseSelector) {
			ClauseSelector sm = (ClauseSelector) base;
			IClause clause = sm.clause();
			if (clause instanceof PropertyFilter) {
				PropertyFilter p1 = (PropertyFilter) clause;
				IType range = p1.property().range();
				if (range instanceof IClass) {
					TimeProp annotation = ((IClass) range).annotation(TimeProp.class);
					if (annotation != null) {
						IProperty prop = ((IClass) range).property(annotation.value()).get();

						IComparison predicate = p1.getPredicate();
						ArrayList<IComparison> cm = new ArrayList<>();
						cm.add(predicate);
						PropertyComparison m = new PropertyComparison(
								new Comparison(time, new Comparative(Comparative.Kind.LESS, "before")), prop);
						cm.add(m);
						AndComparison co = AndComparison.and(cm, predicate.domain());
						p1.setPredicate(co);
						return sm;
					}
				}
			}

		}
		return null;
	}

	@Clause("DATE_P")
	public static Object prop_and_date(IProperty p, GenericTime time) {
		return dateOp(p, time, "in", Comparative.Kind.IN);
	}

	@Clause("DATE_PB")
	public static Object prop_and_date1(IProperty p, GenericTime time) {
		return dateOp(p, time, "before", Comparative.Kind.LESS);
	}

	@Clause("DATE_PA")
	public static Object prop_and_date2(IProperty p, GenericTime time) {
		return dateOp(p, time, "after", Comparative.Kind.MORE);
	}

	private static Object dateOp(IProperty p, GenericTime time, String nm, Comparative.Kind knd) {
		IType range = p.range();
		if (range instanceof IClass) {
			TimeProp annotation = ((IClass) range).annotation(TimeProp.class);
			if (annotation != null) {
				IProperty prop = ((IClass) range).property(annotation.value()).get();

				PropertyComparison m = new PropertyComparison(new Comparison(time, new Comparative(knd, nm)), prop);
				return PropertyFilter.propertyFilter(p, m);
			}
		}
		return null;
	}

	@Clause("DATE_F")
	public static ISelector afrerTime(ISelector base, GenericTime time) {
		IType domain = base.domain();
		if (domain instanceof IClass) {
			TimeProp annotation = domain.annotation(TimeProp.class);
			if (annotation != null) {
				Optional<IProperty> property = ((IClass) domain).property(annotation.value());
				return PropertyFilter.propertyFilter(property.get(),
						new Comparison(time, new Comparative(Comparative.Kind.MORE, "after"))).produce(base);
			}
		}
		if (base instanceof ClauseSelector) {
			ClauseSelector sm = (ClauseSelector) base;
			IClause clause = sm.clause();
			if (clause instanceof PropertyFilter) {
				PropertyFilter p1 = (PropertyFilter) clause;
				IType range = p1.property().range();
				if (range instanceof IClass) {
					TimeProp annotation = ((IClass) range).annotation(TimeProp.class);
					if (annotation != null) {
						IProperty prop = ((IClass) range).property(annotation.value()).get();

						IComparison predicate = p1.getPredicate();
						ArrayList<IComparison> cm = new ArrayList<>();
						cm.add(predicate);
						PropertyComparison m = new PropertyComparison(
								new Comparison(time, new Comparative(Comparative.Kind.MORE, "after")), prop);
						cm.add(m);
						AndComparison co = AndComparison.and(cm, predicate.domain());
						p1.setPredicate(co);
						return sm;
					}
				}
			}

		}
		return null;
	}

	@Clause("CONTAINED")
	public static ISelector contained(ISelector base, IHasDomain owner) {

		if (base instanceof ClauseSelector) {
			ClauseSelector sm = (ClauseSelector) base;
			if (sm.clause() instanceof TopValues) {
				IType domain = sm.domain();
				if (domain instanceof IClass) {
					IClass cc = (IClass) domain;
					for (IProperty z : cc.properties()) {
						if (z.range() == owner.domain()) {
							PropertyFilter propertyFilter = PropertyFilter.propertyFilter(z,
									new Comparison(owner, new Comparative(Comparative.Kind.IN, "in")));
							ClauseSelector original = (ClauseSelector) base;
							ClauseSelector produce = ClauseSelector.produce(((ClauseSelector) base).parent(),
									base.domain(), Multiplicity.MULTIPLE, propertyFilter);
							if (produce != null) {
								return ClauseSelector.produce(produce, base.domain(), Multiplicity.MULTIPLE,
										original.clause());
							}
						}
					}
				}
			}
		}
		if (base.clazz().isPresent() && owner.clazz().isPresent()) {
			IProperty ps = PropertyAdapter.findContainmentPath(owner.clazz().get(), base.clazz().get());
			if (ps != null) {
				IProperty createInverseProperty = InverseProperty.createInverseProperty(ps);
				if (createInverseProperty!=null) {
					return new ContainmentClause(owner, true, createInverseProperty).produce(base);
				}
			}
		}
		if (base.domain() instanceof IClass && !(base instanceof SingleSelector)) {
			IType d = owner.domain();
			if (d instanceof IClass) {
				IProperty findPath = PropertyAdapter.findPath((IClass) base.domain(), (IClass) d);
				if (findPath != null || (d == Builtins.ALLMATCH)) {

					return new ContainmentClause(owner, true, findPath).produce(base);

				}
			}
		}

		return null;
	}

	static IProperty findPath(IClass owner, IClass target) {
		return null;
	}

	@Clause("NOT")
	public static IHasDomain not(IHasDomain d) {
		if (d instanceof Comparison) {
			return ((Comparison) d).negate();
		} else if (d instanceof Measure) {
			Comparative comparative = new Comparative(Kind.EQUAL, "!=");
			return new Comparison(d, comparative);
		} else if (d instanceof PropertyFilter) {
			PropertyFilter m = (PropertyFilter) d;
			return m.negate();
		} else if (d instanceof ISelector) {
			Comparative comparative = new Comparative(Kind.NOT_IN, "in");
			return new Comparison(d, comparative);
		}
		return null;
	}

	@Clause("COMBINE")
	public static ISelector combine(ISelector s, IClause cl) {

		return cl.produce(s);
	}

	@Clause("COMPARISON")
	public static Comparison compare(Comparative c, Measure m) {
		return new Comparison(m, c);
	}

	@Clause("COMBINE_COMPARISON")
	public static IHasDomain combineAnd(List<IComparison> cls) {
		if (cls.stream().filter(x -> x.isNotSolved()).findAny().isPresent()) {
			return null;
		}
		if (cls.stream().map(x -> x.domain()).distinct().count() == 1) {
			return AndComparison.and(cls, cls.stream().map(x -> x.domain()).distinct().findFirst().get());
		}
		return null;
	}

	@Clause("OR_COMPARISON")
	public static IHasDomain combineOr(List<IComparison> cls) {
		if (cls.stream().filter(x -> x.isNotSolved()).findAny().isPresent()) {
			return null;
		}
		if (cls.stream().map(x -> x.domain()).distinct().count() == 1) {
			return new OrComparison(cls, cls.stream().map(x -> x.domain()).distinct().findFirst().get());
		}
		return null;
	}

	@Clause("COMPARISON2")
	public static SimpleRelativeComparison compare_relative(Comparative c, ISelector s0, IHasDomain s1) {
		SimpleRelativeComparison simpleRelativeComparison = new SimpleRelativeComparison(c, s0, s1);
		if (s1.domain().isSubtypeOf(s0.domain())) {
			return simpleRelativeComparison;
		} else {
			simpleRelativeComparison.setNotSolved(true);
		}
		return simpleRelativeComparison;
	}

	//
	@Clause("NUMBER_FIX")
	public static ISelector number_fix(Number n, ISelector s1) {
		if (s1 instanceof ClauseSelector) {
			ClauseSelector sm = (ClauseSelector) s1;
			if (sm.clause() instanceof TopValues) {
				TopValues tp = (TopValues) sm.clause();
				tp.setLimit(n.intValue());
				return sm;
			}
		}
		return null;
	}

	@Clause("COMPARISON3")
	public static Comparison compare_relative(Comparative c, IProperty s0, ISelector s1) {
		// if (s1.domain().isSubtypeOf(s0.domain())){
		// return new SimpleRelativeComparison(c, s0, s1);
		// }
		if (s1.multiplicity() == Multiplicity.SINGLE) {
			if (s1.properties().contains(s0)) {
				return new Comparison(new MapByProperty(s0).produce(s1), c);
			}

		}
		return null;
	}

	@Clause("FILTER")
	public static Object propertyFilter(IProperty prop, IHasDomain predicate) {
		IComparison c = null;
		if (predicate instanceof AllInstancesOf) {
			if (predicate.domain().equals(prop.range())) {
				return null;
			}
		}
		if (predicate instanceof ISelector) {
			Comparative comparative = new Comparative(Kind.IN, "in");
			c = new Comparison(predicate, comparative);
		} else if (predicate instanceof GenericTime) {
			Comparative comparative = new Comparative(Kind.IN, "in");
			c = new Comparison(predicate, comparative);
		} else if (predicate instanceof Measure) {
			Comparative comparative = new Comparative(Kind.EQUAL, "==");
			c = new Comparison(predicate, comparative);
		} else if (predicate instanceof IComparison) {
			IComparison mm = (IComparison) predicate;
			if (mm instanceof Comparison) {

			}
			c = (IComparison) predicate;
		}
		if (predicate instanceof PropertyComparison) {
			PropertyComparison m = (PropertyComparison) predicate;
			IProperty prop2 = m.getProp();
			if (prop2 instanceof ICompoundProperty) {
				IProperty original = ((ICompoundProperty) prop2).original();
				if (original.eq(prop)) {
					return predicate;
				}
			}
			System.out.println(prop2);
		}

		if (c == null) {
			return null;
		}
		if (c.isNotSolved()) {
			c = c.solve(prop);
			if (c == null) {
				return null;
			}
		}

		IType domain = c.domain();
		IType range = prop.range();

		if (!domain.isSubtypeOf(range) && !range.isSubtypeOf(domain)) {
			if (prop.domain().isSubtypeOf(domain)) {
				prop = InverseProperty.createInverseProperty(prop);
			} else {
				// prop = InverseProperty.createInverseProperty(prop);

				// if (prop.)
				// check for containment
				if (range instanceof IClass) {
					IClass clz = (IClass) range;
					List<IProperty> collect = clz.allProperties().stream().filter(x -> x.range().isSubtypeOf(domain))
							.collect(Collectors.toList());
					if (!collect.isEmpty() && collect.size() == 1) {
						IProperty ps = collect.iterator().next();
						ArrayList<IProperty> path = new ArrayList<>();
						if (prop instanceof ActionProperty) {
							return PropertyFilter.propertyFilter(prop, new PropertyComparison(c, ps));

						}
						path.add(prop);
						path.add(PropertyFilter.propertyFilter(ps, c));
						PathProperty pm = new PathProperty(path);

						return pm;
					}
				}
				if (prop instanceof PropertyFilter) {
					PropertyFilter2 flt2 = PropertyFilter2.propertyFilter(((PropertyFilter) prop).property(),
							((PropertyFilter) prop).getPredicate());
					Object propertyFilter = propertyFilter(flt2, predicate);
					if (propertyFilter instanceof PathProperty) {
						return propertyFilter;
					}
				}
				return null;
			}
		}

		return PropertyFilter.propertyFilter(prop, c);
	}

	private static <T> boolean has(List<T> b, Predicate<T> p) {
		return b.stream().filter(p).findAny().isPresent();
	}
}
