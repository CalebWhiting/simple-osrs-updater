package edu.revtek.concurrent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * @author Caleb Bradford
 *
 * A wrapper for instances, instanced by ThreadGroup's
 */
public class Instance {

	private static final Map<ThreadGroup, Map<Class<? extends Instance>, Instance>> INSTANCES = new HashMap<>();

	private ThreadGroup threadGroup;

	public Instance(ThreadGroup threadGroup) {
		this.threadGroup = threadGroup;
	}

	public void setThreadGroup(ThreadGroup threadGroup) {
		this.threadGroup = threadGroup;
	}

	public ThreadGroup getThreadGroup() {
		return threadGroup;
	}

	public boolean isActive() {
		Map<Class<? extends Instance>, Instance> sessionMap = INSTANCES.get(getThreadGroup());
		return sessionMap.containsValue(this);
	}

	public static Instance invoke(Instance instance) {
		if (INSTANCES.get(instance.getThreadGroup()) == null) {
			INSTANCES.put(instance.getThreadGroup(), new HashMap<Class<? extends Instance>, Instance>());
		}
		INSTANCES.get(instance.getThreadGroup()).put(instance.getClass(), instance);
		return instance;
	}

	public static Instance revoke(Instance instance) {
		INSTANCES.remove(instance.getThreadGroup());
		return instance;
	}

	public static Instance[] getInstances() {
		Collection<Map<Class<? extends Instance>, Instance>> instances = INSTANCES.values();
		Collection<Instance> collection = new Vector<>();
		for (Map<Class<? extends Instance>, Instance> i : instances) {
			collection.addAll(i.values());
		}
		return collection.toArray(new Instance[collection.size()]);
	}

	public static <T extends Instance> T get(ThreadGroup threadGroup, Class<T> type) {
		Instance instance = INSTANCES.get(threadGroup).get(type);
		if (instance == null)
			throw new RuntimeException("No Instance For ThreadGroup '" + threadGroup.hashCode() + "' / " + type);
		return (T) instance;
	}

}
